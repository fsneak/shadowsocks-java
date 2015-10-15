package com.fsneak.shadowsocks.event;

import com.fsneak.shadowsocks.ShadowsocksLocal;
import com.fsneak.shadowsocks.crypto.Encryptor;
import com.fsneak.shadowsocks.log.Logger;
import com.fsneak.shadowsocks.session.ChannelData;
import com.fsneak.shadowsocks.session.ChannelType;
import com.fsneak.shadowsocks.session.Session;
import com.fsneak.shadowsocks.socks5.Socks5HandleResult;
import com.fsneak.shadowsocks.socks5.Socks5HandlerFactory;
import com.fsneak.shadowsocks.socks5.Socks5StageHandler;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

/**
 * @author fsneak
 */
public class ReadEventHandler implements EventHandler<ReadEvent> {
    private static final ReadEventHandler INSTANCE = new ReadEventHandler();

    private ReadEventHandler() {
    }

    public static ReadEventHandler getInstance() {
        return INSTANCE;
    }

    @Override
	public void handle(ReadEvent event) {
		SelectionKey key = event.getKey();
		SocketChannel channel = (SocketChannel) key.channel();
		Session session = (Session) key.attachment();

        if (session.isClosed()) {
            return;
        }

        try {
            ChannelData data = session.getData(channel);
            ByteBuffer readBuffer = data.getReadBuffer();
            int size = channel.read(readBuffer);
            if (size < 0) {
                session.close();
                return;
            }

            readBuffer.flip();
            handleRead(session, data.getType(), data.getReadBuffer());
        } catch (Throwable t) {
            Logger.debug(t);
            session.close();
        }
	}

    private void handleRead(Session session, ChannelType sourceType, ByteBuffer readableBuffer) throws IOException {
        if (session.getStage() == Session.Stage.TRANSFER) {
            handleReadTransfer(session, sourceType, readableBuffer);
            Logger.debug("transfer " + sourceType + " data");
        } else {
            handleSocks5Read(session, readableBuffer);
        }
    }

    private void handleReadTransfer(Session session, ChannelType sourceType, ByteBuffer readableBuffer) {
        byte[] bytes = new byte[readableBuffer.remaining()];
        readableBuffer.get(bytes).compact();
        addDataToSend(session, sourceType.getOpposite(), bytes, true);
    }

    private void handleSocks5Read(Session session, ByteBuffer readableBuffer) throws IOException {
        // socks5 protocol
        Socks5StageHandler handler = Socks5HandlerFactory.getHandler(session.getStage());
        Socks5HandleResult result = handler.handle(readableBuffer);
	    sendSocks5Response(session, result);

        if (result.getType() == Socks5HandleResult.Type.COMPLETED) {
            handleSocks5ReadCompleted(session, readableBuffer);
        } else if (result.getType() == Socks5HandleResult.Type.UNCOMPLETED) {
            handleSocks5ReadUncompleted(session, readableBuffer);
        } else if (result.getType() == Socks5HandleResult.Type.ERROR) {
            handleSocks5ReadError(session);
        } else {
            throw new IllegalStateException("wrong code: " + result.getType());
        }
    }

    private void handleSocks5ReadCompleted(Session session, ByteBuffer readableBuffer) throws IOException {
        readableBuffer.compact();
        Logger.debug("socks5 " + session.getStage() + " completed");
        session.setSocks5NextStage();
    }

	private void sendSocks5Response(Session session, Socks5HandleResult result) throws IOException {
		if (result.getResponseToLocal() != null) {
		    addDataToSend(session, ChannelType.LOCAL, result.getResponseToLocal(), false);
		}

		if (result.getResponseToRemote() != null) {
		    if (!session.isRemoteConnected()) {
		        session.connectToRemote();
		    }
		    addDataToSend(session, ChannelType.REMOTE, result.getResponseToRemote(), true);
		}
	}

	private void handleSocks5ReadUncompleted(Session session, ByteBuffer readableBuffer) {
        readableBuffer.position(readableBuffer.limit());
        readableBuffer.limit(readableBuffer.capacity());
        Logger.debug("socks5 " + session.getStage() + " completed");
    }

    private void handleSocks5ReadError(Session session) {
        Logger.error("socks5 " + session.getStage() + " error, close session");
        session.close();
    }

    private void addDataToSend(Session session, ChannelType target, byte[] originalData, boolean needEncryption) {
        byte[] dataToSend = originalData;
        if (needEncryption) {
            dataToSend = handleDataEncryption(session.getEncryptor(),target, originalData);
        }
        ChannelData channelData = session.getData(target);
        if (channelData == null) {
            throw new NullPointerException();
        }
        channelData.addDataToSend(dataToSend);
        ShadowsocksLocal.getInstance().addEvent(new WriteEvent(session, target));
    }

    private byte[] handleDataEncryption(Encryptor encryptor, ChannelType target, byte[] data) {
        byte[] handledData;
        if (target == ChannelType.REMOTE) {
            handledData = encryptor.encrypt(data);
        } else if (target == ChannelType.LOCAL) {
            handledData = encryptor.decrypt(data);
        } else {
            throw new IllegalStateException("wrong code: " + target);
        }

        return handledData;
    }
}

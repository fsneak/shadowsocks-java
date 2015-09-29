package com.fsneak.shadowsocks.event;

import com.fsneak.shadowsocks.ShadowsocksLocal;
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
            Logger.error(t);
            session.close();
        }

	}

    private void handleRead(Session session, ChannelType sourceType, ByteBuffer readableBuffer) throws IOException {
        if (session.getStage() == Session.Stage.TRANSFER) {
            handleReadTransfer(session, sourceType, readableBuffer);
        } else {
            handleSocks5Read(session, readableBuffer);
        }
    }

    private void handleReadTransfer(Session session, ChannelType sourceType, ByteBuffer readableBuffer) {
        byte[] bytes = readAndCompact(readableBuffer);
        session.addDataToSend(sourceType.getOpposite(), bytes, true);
        addWriteEvent(session, sourceType.getOpposite());
    }

    private void handleSocks5Read(Session session, ByteBuffer readableBuffer) throws IOException {
        // socks5 protocol
        Socks5StageHandler handler = Socks5HandlerFactory.getHandler(session.getStage());
        Socks5HandleResult result = handler.handle(readableBuffer);
        if (result.getType() == Socks5HandleResult.Type.COMPLETED) {
            handleSocks5ReadCompleted(session, readableBuffer, result);
        } else if (result.getType() == Socks5HandleResult.Type.UNCOMPLETED) {
            handleSocks5ReadUncompleted(readableBuffer);
        } else if (result.getType() == Socks5HandleResult.Type.ERROR) {
            handleSocks5ReadError(session);
        } else {
            throw new IllegalStateException("wrong code: " + result.getType());
        }
    }

    private void handleSocks5ReadCompleted(Session session, ByteBuffer readableBuffer, Socks5HandleResult result) throws IOException {
        if (result.getResponseToLocal() != null) {
            session.addDataToSend(ChannelType.LOCAL, result.getResponseToLocal(), false);
            addWriteEvent(session, ChannelType.LOCAL);
        }

        if (result.getResponseToRemote() != null) {
            if (!session.isRemoteConnected()) {
                session.connectToRemote();
            }
            session.addDataToSend(ChannelType.REMOTE, result.getResponseToRemote(), true);
            addWriteEvent(session, ChannelType.REMOTE);
        }

        readableBuffer.compact();
        session.setSocks5NextStage();
    }

    private void handleSocks5ReadUncompleted(ByteBuffer readableBuffer) {
        readableBuffer.position(readableBuffer.limit());
        readableBuffer.limit(readableBuffer.capacity());
    }

    private void handleSocks5ReadError(Session session) {
        Logger.error("socks5 error, close session");
        session.close();
    }

    private byte[] readAndCompact(ByteBuffer buffer) {
        byte[] bytes = new byte[buffer.remaining()];
        buffer.get(bytes).compact();
        return bytes;
    }

    private void addWriteEvent(Session session, ChannelType type) {
        EventQueue eventQueue = ShadowsocksLocal.getInstance().getEventQueue();
        eventQueue.addEvent(new WriteEvent(session, type));
    }
}

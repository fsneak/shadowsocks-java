package com.fsneak.shadowsocks.event;

import com.fsneak.shadowsocks.ShadowsocksLocal;
import com.fsneak.shadowsocks.crypto.EncryptionHandler;
import com.fsneak.shadowsocks.log.Logger;
import com.fsneak.shadowsocks.session.ChannelData;
import com.fsneak.shadowsocks.session.Session;

import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

/**
 * @author fsneak
 */
public class ReadEventHandler implements EventHandler<ReadEvent> {

	@Override
	public void handle(ShadowsocksLocal shadowsocksLocal, ReadEvent event) {
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


        } catch (Throwable t) {
            Logger.error(t);
        }

	}

    private void handleLocalRead(ShadowsocksLocal shadowsocksLocal, Session session, ChannelData data) {
        if (session.getStage() == Session.Stage.TRANSFER) {
            EncryptionHandler encryptionHandler = shadowsocksLocal.getEncryptionHandler();
            byte[] bytes = readAndCompact(data.getReadBuffer());
            byte[] encryptedBytes = encryptionHandler.encrypt(bytes);
            session.transferData(data.getType(), encryptedBytes);
        }
    }

    private void handleRemoteRead(ShadowsocksLocal shadowsocksLocal, Session session, ChannelData data) {
        // all remote data should by decrypted and transfer to local channel
        EncryptionHandler encryptionHandler = shadowsocksLocal.getEncryptionHandler();
        byte[] bytes = readAndCompact(data.getReadBuffer());
        byte[] decryptedBytes = encryptionHandler.decrypt(bytes);
        session.transferData(data.getType(), decryptedBytes);
    }

    private void transferData(ShadowsocksLocal shadowsocksLocal, Session session, ChannelData source) {

    }

	private byte[] readAndCompact(ByteBuffer buffer) {
        byte[] bytes = new byte[buffer.remaining()];
        buffer.get(bytes).compact();
        return bytes;
    }
}

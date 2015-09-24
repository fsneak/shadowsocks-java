package com.fsneak.shadowsocks.event;

import com.fsneak.shadowsocks.Session;
import com.fsneak.shadowsocks.ShadowsocksLocal;
import com.fsneak.shadowsocks.log.Logger;
import com.fsneak.shadowsocks.socks5.Socks5HandleResult;
import com.fsneak.shadowsocks.socks5.Socks5HandlerFactory;
import com.fsneak.shadowsocks.socks5.Socks5StageHandler;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

/**
 * @author xiezhiheng
 */
public class ReadEventHandler implements EventHandler<ReadEvent> {

	@Override
	public void handle(ShadowsocksLocal shadowsocksLocal, ReadEvent event) {
		SelectionKey key = event.getKey();
		SocketChannel channel = (SocketChannel) key.channel();
		Session session = (Session) key.attachment();
		try {
			int size = channel.read(session.getReadBuffer());
			if (size < 0) {
				// TODO close session
			}


		} catch (IOException e) {
			Logger.error(e);
		}

	}

	private void handleReadBuffer(Session session) {
		Session.Stage stage = session.getStage();
		if (stage == Session.Stage.TRANSFER) {
			ByteBuffer readBuffer = session.getReadBuffer();
			readBuffer.flip();
			ByteBuffer copyBuffer = ByteBuffer.allocate(readBuffer.remaining());
			copyBuffer.put(readBuffer);
		} else {
			Socks5StageHandler handler = Socks5HandlerFactory.getHandler(session.getStage());
			Socks5HandleResult result = handler.handle(session.getReadBuffer());

		}
	}

	private void transferData() {

	}
}

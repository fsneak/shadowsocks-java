package com.xiezhiheng.shadowsocks.event;

import com.xiezhiheng.shadowsocks.Session;
import com.xiezhiheng.shadowsocks.ShadowsocksLocal;
import com.xiezhiheng.shadowsocks.log.Logger;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

/**
 * @author xiezhiheng
 */
public class AcceptEventHandler implements EventHandler<AcceptEvent> {

	@Override
	public void handle(ShadowsocksLocal shadowsocksLocal, AcceptEvent event) {
		ServerSocketChannel acceptor = shadowsocksLocal.getLocalAcceptor();
		Selector selector = shadowsocksLocal.getSelector();
		try {
			SocketChannel localChannel = acceptor.accept();
			if (localChannel != null) {
				localChannel.configureBlocking(false);
				SelectionKey selectionKey = localChannel.register(selector, SelectionKey.OP_READ);
				Session session = new Session(localChannel);
				selectionKey.attach(session);
			}
		} catch (IOException e) {
			Logger.error(e);
		}
	}
}

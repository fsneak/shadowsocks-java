package com.fsneak.shadowsocks.event;

import com.fsneak.shadowsocks.ShadowsocksLocal;
import com.fsneak.shadowsocks.session.Session;
import com.fsneak.shadowsocks.log.Logger;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

/**
 * @author fsneak
 */
public class AcceptEventHandler implements EventHandler<AcceptEvent> {
    private static final AcceptEventHandler INSTANCE = new AcceptEventHandler();

    private AcceptEventHandler() {
    }

    public static AcceptEventHandler getInstance() {
        return INSTANCE;
    }

    @Override
	public void handle(AcceptEvent event) {
		ServerSocketChannel acceptor = ShadowsocksLocal.getInstance().getLocalAcceptor();
		Selector selector = ShadowsocksLocal.getInstance().getSelector();
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

package com.fsneak.shadowsocks;

import com.fsneak.shadowsocks.crypto.EncryptionHandler;
import com.fsneak.shadowsocks.crypto.EncryptionHandlerFactory;
import com.fsneak.shadowsocks.crypto.EncryptionType;
import com.fsneak.shadowsocks.event.Event;
import com.fsneak.shadowsocks.event.EventHandler;
import com.fsneak.shadowsocks.event.EventHandlerFactory;
import com.fsneak.shadowsocks.event.EventQueue;
import com.fsneak.shadowsocks.log.Logger;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;

/**
 * @author xiezhiheng
 */
public class ShadowsocksLocal {
	private static final ShadowsocksLocal INSTANCE = new ShadowsocksLocal(Config.getInstance());

	private final SocketAddress serverAddress;
	private final int localPort;
    private final EncryptionHandler encryptionHandler;

	private ServerSocketChannel localAcceptor;
	private Selector selector;
	private EventQueue eventQueue;

	public ShadowsocksLocal(Config config) {
		serverAddress = config.getServerAddress();
		localPort = config.getLocalPort();
        encryptionHandler = EncryptionHandlerFactory.createHandler(config.getEncryptionType(), config.getPassword());
		eventQueue = new EventQueue();
	}

	public static ShadowsocksLocal getInstance() {
		return INSTANCE;
	}

	public void start() {

	}

    public EncryptionHandler getEncryptionHandler() {
        return encryptionHandler;
    }

    public SocketAddress getServerAddress() {
        return serverAddress;
    }

    public ServerSocketChannel getLocalAcceptor() {
		return localAcceptor;
	}

	public Selector getSelector() {
		return selector;
	}

	public EventQueue getEventQueue() {
		return eventQueue;
	}

    @SuppressWarnings("unchecked")
    private void startListening() throws IOException {
		selector = Selector.open();
		localAcceptor = ServerSocketChannel.open();
		localAcceptor.bind(new InetSocketAddress("localhost", localPort));
		localAcceptor.configureBlocking(false);
		localAcceptor.register(selector, SelectionKey.OP_ACCEPT);

		while (true) {
			Event event = eventQueue.poll();
            if (event == null) {
                throw new IllegalStateException("event queue must not be empty");
            }

            try {
                EventHandler handler = EventHandlerFactory.getHandler(event.getType());
                handler.handle(event);
            } catch (Throwable t) {
                Logger.error(String.format("%s event handling error", event.getType()), t);
            }
        }
	}
}

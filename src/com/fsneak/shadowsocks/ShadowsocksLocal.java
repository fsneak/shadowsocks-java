package com.fsneak.shadowsocks;

import com.fsneak.shadowsocks.crypto.EncryptionHandler;
import com.fsneak.shadowsocks.crypto.EncryptionHandlerFactory;
import com.fsneak.shadowsocks.event.*;
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

    private final EventQueue eventQueue;
    private ServerSocketChannel localAcceptor;
    private Selector selector;

	public ShadowsocksLocal(Config config) {
		serverAddress = config.getServerAddress();
		localPort = config.getLocalPort();
        encryptionHandler = EncryptionHandlerFactory.createHandler(config.getEncryptionType(), config.getPassword());
		eventQueue = new EventQueue();
	}

	public static ShadowsocksLocal getInstance() {
		return INSTANCE;
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

    public void addEvent(Event event) {
        eventQueue.addEvent(event);
    }

    public boolean isEventQueueEmpty() {
        return eventQueue.isEmpty();
    }

    @SuppressWarnings("unchecked")
    public void startEventLoop() throws IOException {
		selector = Selector.open();
		localAcceptor = ServerSocketChannel.open();
		localAcceptor.bind(new InetSocketAddress(localPort));
        Logger.info("server socket bind " + localPort);
		localAcceptor.configureBlocking(false);
		localAcceptor.register(selector, SelectionKey.OP_ACCEPT);

        addEvent(new SelectEvent());

        Logger.info("start event loop");

		while (true) {
			Event event = eventQueue.poll();

            if (event == null) {
                throw new IllegalStateException("event queue must not be empty");
            }

            Logger.debug("handle " + event.getType().toString());

            try {
                EventHandler handler = EventHandlerFactory.getHandler(event.getType());
                handler.handle(event);
            } catch (Throwable t) {
                Logger.error(String.format("%s event handling error", event.getType()), t);
            }
        }
	}

    public static void main(String[] args) throws IOException {
        ShadowsocksLocal.getInstance().startEventLoop();
    }
}

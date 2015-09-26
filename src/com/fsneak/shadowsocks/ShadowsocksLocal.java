package com.fsneak.shadowsocks;

import com.fsneak.shadowsocks.crypto.EncryptionHandler;
import com.fsneak.shadowsocks.crypto.EncryptionHandlerFactory;
import com.fsneak.shadowsocks.crypto.EncryptionType;
import com.fsneak.shadowsocks.event.Event;
import com.fsneak.shadowsocks.event.EventQueue;

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
	private final SocketAddress serverAddress;
	private final int localPort;
    private final EncryptionHandler encryptionHandler;

	private ServerSocketChannel localAcceptor;
	private Selector selector;
	private EventQueue eventQueue;

	public ShadowsocksLocal(SocketAddress serverAddress, int localPort, String password,
	                        EncryptionType encryptionType) {
		this.serverAddress = serverAddress;
		this.localPort = localPort;
        encryptionHandler = EncryptionHandlerFactory.createHandler(encryptionType, password);

		eventQueue = new EventQueue();
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

	private void startListening() throws IOException {
		selector = Selector.open();
		localAcceptor = ServerSocketChannel.open();
		localAcceptor.bind(new InetSocketAddress("localhost", localPort));
		localAcceptor.configureBlocking(false);
		localAcceptor.register(selector, SelectionKey.OP_ACCEPT);

		while (true) {
			Event event = eventQueue.poll();

		}
	}
}

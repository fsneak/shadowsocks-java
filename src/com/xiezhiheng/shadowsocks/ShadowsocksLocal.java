package com.xiezhiheng.shadowsocks;

import com.xiezhiheng.shadowsocks.encrypt.EncryptionType;
import com.xiezhiheng.shadowsocks.event.Event;
import com.xiezhiheng.shadowsocks.event.EventQueue;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author xiezhiheng
 */
public class ShadowsocksLocal {
	private final SocketAddress serverAddress;
	private final int localPort;
	private final String password;
	private final EncryptionType encryptionType;

	private ExecutorService listeningService;
	private ServerSocketChannel localAcceptor;
	private Selector selector;
	private EventQueue eventQueue;

	public ShadowsocksLocal(SocketAddress serverAddress, int localPort, String password,
	                        EncryptionType encryptionType) {
		this.serverAddress = serverAddress;
		this.localPort = localPort;
		this.password = password;
		this.encryptionType = encryptionType;

		eventQueue = new EventQueue();
		listeningService = Executors.newSingleThreadExecutor();
	}

	public void start() {

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

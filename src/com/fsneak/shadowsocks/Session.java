package com.fsneak.shadowsocks;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.LinkedList;

/**
 *
 * @author xiezhiheng
 */
public class Session {
	private static final int BUFFER_SIZE = 32 * 1024;

	public enum Stage {
		SOCKS5_HELLO,
		SOCKS5_ADDR,
		TRANSFER;
	}

	private Stage stage;
	private SocketChannel localChannel;
	private SocketChannel remoteChannel;
	private ByteBuffer readBuffer;
	private LinkedList<ByteBuffer> sendToLocalQueue;
	private LinkedList<ByteBuffer> sendToRemoteQueue;

	public Session(SocketChannel localChannel) {
		this.localChannel = localChannel;
		stage = Stage.SOCKS5_HELLO;
		readBuffer = ByteBuffer.allocate(BUFFER_SIZE);
	}

	public Stage getStage() {
		return stage;
	}

	public boolean isRemoteConnected() {
		return remoteChannel != null && remoteChannel.isConnected();
	}

	public ByteBuffer getReadBuffer() {
		return readBuffer;
	}

	public void sendToLocal(ByteBuffer data) {
		sendToLocalQueue.add(data);
	}

	public void sendToRemote(ByteBuffer data) {
		sendToRemoteQueue.add(data);
	}

	public void connectToRemote(SocketAddress address) throws IOException {
		remoteChannel = SocketChannel.open(address);
		remoteChannel.configureBlocking(false);
	}
}

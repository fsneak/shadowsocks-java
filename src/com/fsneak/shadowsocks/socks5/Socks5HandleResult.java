package com.fsneak.shadowsocks.socks5;

import java.nio.ByteBuffer;

/**
 * @author xiezhiheng
 */
public class Socks5HandleResult {
	public enum Type {
		UNCOMPLETED,
		COMPLETED,
		ERROR
	}

	private final Type type;
	private final ByteBuffer responseToLocal;
	private final ByteBuffer responseToRemote;

	public Socks5HandleResult(Type type, ByteBuffer responseToLocal, ByteBuffer responseToRemote) {
		this.type = type;
		this.responseToLocal = responseToLocal;
		this.responseToRemote = responseToRemote;
	}

	public Socks5HandleResult(Type type) {
		this(type, null, null);
	}

	public ByteBuffer getResponseToLocal() {
		return responseToLocal;
	}

	public ByteBuffer getResponseToRemote() {
		return responseToRemote;
	}

	public Type getType() {
		return type;
	}
}

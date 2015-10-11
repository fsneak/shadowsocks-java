package com.fsneak.shadowsocks.socks5;

import java.nio.ByteBuffer;

/**
 * @author fsneak
 */
public class Socks5HandleResult {
	public enum Type {
		UNCOMPLETED,
		COMPLETED,
		ERROR
	}

	private final Type type;
	private final byte[] responseToLocal;
	private final byte[] responseToRemote;

	public Socks5HandleResult(Type type, byte[] responseToLocal, byte[] responseToRemote) {
		this.type = type;
		this.responseToLocal = responseToLocal;
		this.responseToRemote = responseToRemote;
	}

	public Socks5HandleResult(Type type) {
		this(type, null, null);
	}

	public byte[] getResponseToLocal() {
		return responseToLocal;
	}

	public byte[] getResponseToRemote() {
		return responseToRemote;
	}

	public Type getType() {
		return type;
	}
}

package com.xiezhiheng.shadowsocks.socks5;

import java.nio.ByteBuffer;

/**
 * @author xiezhiheng
 */
public final class Socks5Utils {
	private static final byte SOCKS5_VERSION = 0x05;
	private Socks5Utils() {
	}

	public static boolean verifyVersion(byte version) {
		return SOCKS5_VERSION == version;
	}

	public static int getByteLen(ByteBuffer buffer) {
		return 0xff & buffer.get();
	}
}

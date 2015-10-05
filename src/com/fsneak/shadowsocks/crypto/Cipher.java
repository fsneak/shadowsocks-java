package com.fsneak.shadowsocks.crypto;

/**
 * @author xiezhiheng
 */
public interface Cipher {
	byte[] update(byte[] input);
}

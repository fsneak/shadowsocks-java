package com.fsneak.shadowsocks.crypto;

/**
 * @author fsneak
 */
public interface Cipher {
	byte[] update(byte[] input);
}

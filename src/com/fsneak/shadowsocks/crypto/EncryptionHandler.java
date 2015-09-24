package com.fsneak.shadowsocks.crypto;

/**
 * @author xiezhiheng
 */
public interface EncryptionHandler {
	byte[] encrypt(byte[] source);

	byte[] decrypt(byte[] source);
}

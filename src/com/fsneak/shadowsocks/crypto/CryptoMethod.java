package com.fsneak.shadowsocks.crypto;

/**
 * @author xiezhiheng
 */
public enum CryptoMethod {
	RC4_MD5(16, 16)
    ;

    private final int keyLen;
    private final int ivLen;

    CryptoMethod(int keyLen, int ivLen) {
        this.keyLen = keyLen;
        this.ivLen = ivLen;
    }

    public int getKeyLen() {
        return keyLen;
    }

    public int getIvLen() {
        return ivLen;
    }
}

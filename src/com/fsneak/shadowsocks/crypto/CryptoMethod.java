package com.fsneak.shadowsocks.crypto;

/**
 * @author fsneak
 */
public enum CryptoMethod {
	RC4_MD5("rc4-md5", 16, 16)
    ;

    private final String name;
    private final int keyLen;
    private final int ivLen;

    CryptoMethod(String name, int keyLen, int ivLen) {
        this.name = name;
        this.keyLen = keyLen;
        this.ivLen = ivLen;
    }

    public static CryptoMethod getMethod(String name) {
        for (CryptoMethod cryptoMethod : values()) {
            if (cryptoMethod.name.equalsIgnoreCase(name)) {
                return cryptoMethod;
            }
        }

        return null;
    }

    public int getKeyLen() {
        return keyLen;
    }

    public int getIvLen() {
        return ivLen;
    }
}

package com.fsneak.shadowsocks.crypto;

public class CipherFactory {
    public static Cipher create(CryptoMethod method, byte[] key, byte[] iv, Encryptor.Operation operation) {
        switch (method) {
            case RC4_MD5:
                return new Rc4Md5Cipher(key, iv, operation);
        }
        throw new UnsupportedOperationException("unsupported crypto method " + method);
    }
}

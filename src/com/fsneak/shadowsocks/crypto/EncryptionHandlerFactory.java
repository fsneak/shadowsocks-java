package com.fsneak.shadowsocks.crypto;

public class EncryptionHandlerFactory {
    public static EncryptionHandler createHandler(EncryptionType type, String pwd) {
        switch (type) {
            case RC4_MD5:
                return new Rc4Md5Handler(pwd);
        }
        throw new UnsupportedOperationException("unsupported encryption type " + type);
    }
}

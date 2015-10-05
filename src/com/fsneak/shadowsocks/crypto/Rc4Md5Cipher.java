package com.fsneak.shadowsocks.crypto;

import javax.crypto.spec.SecretKeySpec;
import java.util.Arrays;

/**
 * @author fsneak
 */
public class Rc4Md5Cipher implements Cipher {
    private final javax.crypto.Cipher rc4Cipher;

    public Rc4Md5Cipher(byte[] key, byte[] iv, Encryptor.Operation operation) {
        byte[] rc4Key = CryptoCommon.md5Digest(Arrays.asList(key, iv));
        try {
            rc4Cipher = javax.crypto.Cipher.getInstance("RC4");
            SecretKeySpec keySpec = new SecretKeySpec(rc4Key, "RC4");
            if (operation == Encryptor.Operation.ENCRYPT) {
                rc4Cipher.init(javax.crypto.Cipher.ENCRYPT_MODE, keySpec);
            } else if (operation == Encryptor.Operation.DECRYPT) {
                rc4Cipher.init(javax.crypto.Cipher.DECRYPT_MODE, keySpec);
            } else {
                throw new IllegalArgumentException("illegal operation: " + operation);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public byte[] update(byte[] input) {
        return rc4Cipher.update(input);
    }
}

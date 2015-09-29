package com.fsneak.shadowsocks.crypto;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.MessageDigest;

/**
 * @author fsneak
 */
public class Rc4Md5Handler implements EncryptionHandler {
    private final Cipher rc4Cipher;
    private final SecretKeySpec keySpec;

    public Rc4Md5Handler(String pwd) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.update(pwd.getBytes());
            byte[] md5Key = messageDigest.digest();
            rc4Cipher = Cipher.getInstance("RC4");
            keySpec = new SecretKeySpec(md5Key, "RC4");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public byte[] encrypt(byte[] source) {
        try {
            rc4Cipher.init(Cipher.ENCRYPT_MODE, keySpec);
            return rc4Cipher.doFinal(source);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public byte[] decrypt(byte[] source) {
        try {
            rc4Cipher.init(Cipher.DECRYPT_MODE, keySpec);
            return rc4Cipher.doFinal(source);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

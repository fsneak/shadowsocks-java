package com.fsneak.shadowsocks.crypto;

import com.sun.corba.se.impl.orbutil.RepositoryIdCache_1_3;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author fsneak
 */
public class Encryptor {
    public enum Operation {
        ENCRYPT,
        DECRYPT
    }

    private static final ConcurrentHashMap<String, List<byte[]>> CACHED_KEYS = new ConcurrentHashMap<>();

    private byte[] iv = null;
    private boolean ivSent = false;
    private byte[] cipherIv = new byte[0];
    private final Cipher cipher;
    private Cipher decipher = null;
    private final String key;
    private final CryptoMethod method;

    public Encryptor(String key, CryptoMethod method) {
        this.key = key;
        this.method = method;
        cipher = getCipher(Operation.ENCRYPT, randomBytes(method.getIvLen()));
    }

    public byte[] encrypt(byte[] input) {
        if (input.length == 0) {
            return input;
        }

        if (ivSent) {
            return cipher.update(input);
        } else {
            ivSent = true;
            return CryptoCommon.concat(cipherIv, cipher.update(input));
        }
    }

    public byte[] decrypt(byte[] input) {
        if (input.length == 0) {
            return input;
        }

        if (decipher == null) {
            int decipherLvLen = method.getIvLen();
            byte[] decipherLv = Arrays.copyOf(input, decipherLvLen);
            decipher = getCipher(Operation.DECRYPT, decipherLv);

            input = Arrays.copyOfRange(input, decipherLvLen, input.length);
            if (input.length == 0) {
                return input;
            }
        }
        return decipher.update(input);
    }

    private Cipher getCipher(Operation operation, byte[] iv) {
        byte[] pwd = CryptoCommon.getBytes(key);
        List<byte[]> keyAndIv = CryptoCommon.evpBytesToKey(pwd, method.getKeyLen(), method.getIvLen());
        byte[] key = keyAndIv.get(0);

        if (iv == null) {
            // in fact, iv will never be null in this class ...
            iv = keyAndIv.get(1);
        }

        if (operation == Operation.ENCRYPT) {
            cipherIv = Arrays.copyOf(iv, method.getIvLen());
        }

        iv = Arrays.copyOf(iv, method.getIvLen());
        return CipherFactory.create(method, key, iv, operation);
    }

    private static byte[] randomBytes(int len) {
        byte[] bytes = new byte[len];
        ThreadLocalRandom.current().nextBytes(bytes);
        return bytes;
    }

    public static void main(String[] args) {
        byte[] plain = randomBytes(10240);
        Encryptor encryptor = new Encryptor("key", CryptoMethod.RC4_MD5);
        Encryptor decryptor = new Encryptor("key", CryptoMethod.RC4_MD5);
        byte[] cipher = encryptor.encrypt(plain);
        byte[] plain2 = decryptor.decrypt(cipher);
        System.out.println(Arrays.equals(plain, plain2));
    }
}

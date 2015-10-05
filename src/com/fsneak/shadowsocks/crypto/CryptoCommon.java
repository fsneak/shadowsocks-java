package com.fsneak.shadowsocks.crypto;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * @author fsneak
 */
public final class CryptoCommon {
    private CryptoCommon() {
    }

    public static byte[] getBytes(String str) {
        try {
            return str.getBytes("utf-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public static byte[] md5Digest(byte[] input) {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            return md5.digest(input);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static byte[] md5Digest(List<byte[]> inputs) {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            for (byte[] input : inputs) {
                md5.update(input);
            }
            return md5.digest();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static byte[] concat(byte[] a, byte[] b) {
        byte[] result = Arrays.copyOf(a, a.length + b.length);
        System.arraycopy(b, 0, result, a.length, b.length);
        return result;
    }

    public static byte[] concat(List<byte[]> bytesList) {
        if (bytesList.size() == 0) {
            return new byte[0];
        }

        int len = 0;
        for (byte[] bytes : bytesList) {
            len += bytes.length;
        }

        byte[] result = new byte[len];
        int destPos = 0;
        for (byte[] bytes : bytesList) {
            System.arraycopy(bytes, 0, result, destPos, bytes.length);
            destPos += bytes.length;
        }

        return result;
    }

    public static List<byte[]> evpBytesToKey(byte[] password, int keyLen, int ivLen) {
        List<byte[]> m = new LinkedList<>();
        int i = 0;
        int count = 0;
        while (count < keyLen + ivLen) {
            byte[] data = password;
            if (i > 0) {
                data = CryptoCommon.concat(m.get(i - 1), password);
            }

            byte[] dataDigest = CryptoCommon.md5Digest(data);
            m.add(dataDigest);
            count += dataDigest.length;
            i++;
        }

        byte[] ms = CryptoCommon.concat(m);
        byte[] key = Arrays.copyOfRange(ms, 0, keyLen);
        byte[] iv = Arrays.copyOfRange(ms, keyLen, keyLen + ivLen);
        return Arrays.asList(key, iv);
    }

    public static void main(String[] args) {
        byte[] a = new byte[]{1, 3, 35, 35, 12};
        byte[] b = new byte[]{11, 33, 16, 1, 4, 7, 8, 52};
        byte[] c = new byte[]{111, -24, -21};
        List<byte[]> list = Arrays.asList(a, b, c);
        System.out.println(Arrays.toString(concat(list)));
    }
}

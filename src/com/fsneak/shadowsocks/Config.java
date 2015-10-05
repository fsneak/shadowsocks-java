package com.fsneak.shadowsocks;

import com.fsneak.shadowsocks.crypto.CryptoMethod;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

/**
 * @author fsneak
 */
public class Config {
    private static final Config INSTANCE = new Config();

    public static Config getInstance() {
        return INSTANCE;
    }

    public SocketAddress getServerAddress() {
        return new InetSocketAddress("0.0.0.0", 8388);
    }

    public int getLocalPort() {
        return 10888;
    }

    public String getPassword() {
        return "key";
    }

    public CryptoMethod getCryptoMethod() {
        return CryptoMethod.RC4_MD5;
    }

}

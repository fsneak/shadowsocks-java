package com.fsneak.shadowsocks;

import com.fsneak.shadowsocks.crypto.EncryptionType;

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
        return null;
    }

    public int getLocalPort() {
        return -1;
    }

    public String getPassword() {
        return null;
    }

    public EncryptionType getEncryptionType() {
        return null;
    }

}

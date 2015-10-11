package com.fsneak.shadowsocks;

import com.fsneak.shadowsocks.crypto.CryptoMethod;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Properties;

/**
 * @author fsneak
 */
public class Config {
    private static final String CONFIG_FILE_PATH = "/config.properties";

    private final InetSocketAddress serverAddress;
    private final int localPort;
    private final String password;
    private final CryptoMethod  cryptoMethod;

    public Config() throws IOException {
        Properties properties = loadProperties();
        String serverHostname = properties.getProperty("server_hostname");
        String serverPort = properties.getProperty("server_port");
        serverAddress = new InetSocketAddress(serverHostname, Integer.parseInt(serverPort));
        localPort = Integer.parseInt(properties.getProperty("local_port"));
        password = properties.getProperty("password");
        String methodName = properties.getProperty("method");
        cryptoMethod = CryptoMethod.getMethod(methodName);
        if (cryptoMethod == null) {
            throw new IllegalArgumentException("unsupported crypto method " + methodName);
        }
    }

    public SocketAddress getServerAddress() {
        return serverAddress;
    }

    public int getLocalPort() {
        return localPort;
    }

    public String getPassword() {
        return password;
    }

    public CryptoMethod getCryptoMethod() {
        return cryptoMethod;
    }

    private Properties loadProperties() throws IOException {
        InputStream stream = getClass().getResourceAsStream(CONFIG_FILE_PATH);
        Properties properties = new Properties();
        properties.load(stream);
        return properties;
    }

}

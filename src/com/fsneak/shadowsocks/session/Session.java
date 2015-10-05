package com.fsneak.shadowsocks.session;

import com.fsneak.shadowsocks.ShadowsocksLocal;
import com.fsneak.shadowsocks.crypto.Encryptor;
import com.fsneak.shadowsocks.log.Logger;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Map;

/**
 * @author fsneak
 */
public class Session {
	public enum Stage {
		SOCKS5_HELLO,
        SOCKS5_CONNECT,
		TRANSFER,
        CLOSE,
        ;
	}

    private final Map<SocketChannel, ChannelData> channelMap = new HashMap<>(4);
    private final Encryptor encryptor;
    private Stage stage;

    public Session(SocketChannel localChannel) {
        ChannelData data = new ChannelData(this, ChannelType.LOCAL, localChannel);
        channelMap.put(localChannel, data);
        encryptor = new Encryptor(ShadowsocksLocal.getInstance().getKey(),
                ShadowsocksLocal.getInstance().getCryptoMethod());
        stage = Stage.SOCKS5_HELLO;
    }

    public Encryptor getEncryptor() {
        return encryptor;
    }

    public Stage getStage() {
		return stage;
	}

    public void setSocks5NextStage() {
        if (stage == Stage.SOCKS5_HELLO) {
            stage = Stage.SOCKS5_CONNECT;
        } else if (stage == Stage.SOCKS5_CONNECT) {
            stage = Stage.TRANSFER;
        }
    }

    public ChannelData getData(SocketChannel channel) {
        return channelMap.get(channel);
    }

    public ChannelData getData(ChannelType type) {
        for (ChannelData channelData : channelMap.values()) {
            if (channelData.getType() == type) {
                return channelData;
            }
        }

        return null;
    }

    public boolean isRemoteConnected() {
        return getData(ChannelType.REMOTE) != null;
    }

	public void connectToRemote() throws IOException {
        if (isClosed()) {
            return;
        }

        for (ChannelData data : channelMap.values()) {
            if (data.getType() == ChannelType.REMOTE) {
                return;
            }
        }

        SocketAddress address = ShadowsocksLocal.getInstance().getServerAddress();
        SocketChannel remoteChannel = SocketChannel.open(address);
        remoteChannel.configureBlocking(false);
        Selector selector = ShadowsocksLocal.getInstance().getSelector();
        SelectionKey selectionKey = remoteChannel.register(selector, SelectionKey.OP_READ);
        selectionKey.attach(this);
        channelMap.put(remoteChannel, new ChannelData(this, ChannelType.REMOTE, remoteChannel));
    }

    public void close() {
        try {
            for (SocketChannel socketChannel : channelMap.keySet()) {
                socketChannel.close();
            }
            channelMap.clear();
        } catch (IOException e) {
            Logger.error(e);
        } finally {
            stage = Stage.CLOSE;
        }
    }

    public boolean isClosed() {
        return stage == Stage.CLOSE;
    }
}

package com.fsneak.shadowsocks.session;

import com.fsneak.shadowsocks.ShadowsocksLocal;
import com.fsneak.shadowsocks.log.Logger;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.channels.SocketChannel;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author fsneak
 */
public class Session {
	public enum Stage {
		SOCKS5_HELLO,
		SOCKS5_ADDRESS,
		TRANSFER,
        CLOSE,
        ;
	}

    private final Map<SocketChannel, ChannelData> channelMap = new TreeMap<>();
    private Stage stage;

    public Session(SocketChannel localChannel) {
        ChannelData data = new ChannelData(this, ChannelType.LOCAL, localChannel);
        channelMap.put(localChannel, data);
        stage = Stage.SOCKS5_HELLO;
    }

	public Stage getStage() {
		return stage;
	}

    public void setSocks5NextStage() {
        if (stage == Stage.SOCKS5_HELLO) {
            stage = Stage.SOCKS5_ADDRESS;
        } else if (stage == Stage.SOCKS5_ADDRESS) {
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

package com.fsneak.shadowsocks.session;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.EnumMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author xiezhiheng
 */
public class Session {
	public enum Stage {
		SOCKS5_HELLO,
		SOCKS5_ADDRESS,
		TRANSFER,
        CLOSE
	}

	private Stage stage;
    private final Map<SocketChannel, ChannelData> channelMap = new TreeMap<>();

    public Session(SocketChannel localChannel) {
        stage = Stage.SOCKS5_HELLO;
        ChannelData data = new ChannelData(this, ChannelType.LOCAL);
        channelMap.put(localChannel, data);
    }

	public Stage getStage() {
		return stage;
	}

    public ChannelData getData(SocketChannel channel) {
        return channelMap.get(channel);
    }

    public void transferData(ChannelType sourceType, byte[] data) {
        for (ChannelData channelData : channelMap.values()) {
            if (channelData.getType() != sourceType) {
                channelData.addDataToSend(data);
                return;
            }
        }
    }

	public void connectToRemote(SocketAddress address) throws IOException {
        if (isClosed()) {
            return;
        }

        for (ChannelData data : channelMap.values()) {
            if (data.getType() == ChannelType.REMOTE) {
                return;
            }
        }

        SocketChannel remoteChannel = SocketChannel.open(address);
        remoteChannel.configureBlocking(false);
        channelMap.put(remoteChannel, new ChannelData(this, ChannelType.REMOTE));
    }

    public void close() throws IOException {
        try {
            for (SocketChannel socketChannel : channelMap.keySet()) {
                socketChannel.close();
            }
        } finally {
            stage = Stage.CLOSE;
        }
    }

    public boolean isClosed() {
        return stage == Stage.CLOSE;
    }
}

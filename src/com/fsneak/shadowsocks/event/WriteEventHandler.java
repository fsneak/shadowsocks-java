package com.fsneak.shadowsocks.event;

import com.fsneak.shadowsocks.ShadowsocksLocal;
import com.fsneak.shadowsocks.log.Logger;
import com.fsneak.shadowsocks.session.ChannelData;
import com.fsneak.shadowsocks.session.ChannelType;
import com.fsneak.shadowsocks.session.Session;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * @author fsneak
 */
public class WriteEventHandler implements EventHandler<WriteEvent> {
    private static final WriteEventHandler INSTANCE = new WriteEventHandler();

    private WriteEventHandler() {
    }

    public static WriteEventHandler getInstance() {
        return INSTANCE;
    }

    @Override
    public void handle(WriteEvent event) {
        Session session = event.getSession();
        if (session.isClosed()) {
            return;
        }

        ChannelType channelType = event.getChannelType();
        ChannelData channelData = session.getData(channelType);
        SocketChannel channel = channelData.getChannel();
        ByteBuffer buffer;
        try {
            while ((buffer = channelData.peekDataToSend()) != null) {
                channel.write(buffer);
                if (buffer.hasRemaining()) {
                    // socket output buffer is full, write data in next event
                    ShadowsocksLocal.getInstance().addEvent(new WriteEvent(session, channelType));
                    break;
                } else {
                    channelData.pollDataToSend(); // buffer totally written, remove it
                }
            }
        } catch (IOException e) {
            Logger.error(e);
            session.close();
        }
    }
}

package com.fsneak.shadowsocks.session;

import java.nio.ByteBuffer;
import java.util.LinkedList;

/**
 * @author fsneak
 */
public class ChannelData {
    private static final int BUFFER_SIZE = 32 * 1024;

    private final ByteBuffer readBuffer = ByteBuffer.allocate(BUFFER_SIZE);
    private final LinkedList<ByteBuffer> dataQueue = new LinkedList<ByteBuffer>();
    private final Session session;
    private final ChannelType type;

    public ChannelData(Session session, ChannelType type) {
        this.session = session;
        this.type = type;
    }

    public ByteBuffer getReadBuffer() {
        return readBuffer;
    }

    public ChannelType getType() {
        return type;
    }

    public void addDataToSend(byte[] data) {
        dataQueue.add(ByteBuffer.wrap(data));
    }

    public ByteBuffer peekDataToSend() {
        return dataQueue.peek();
    }

    public ByteBuffer pollDataToSend() {
        return dataQueue.poll();
    }

}

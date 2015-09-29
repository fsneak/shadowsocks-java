package com.fsneak.shadowsocks.event;

import com.fsneak.shadowsocks.session.ChannelType;
import com.fsneak.shadowsocks.session.Session;

/**
 * @author fsneak
 */
public class WriteEvent extends Event {
    private final Session session;
    private final ChannelType channelType;

    public WriteEvent(Session session, ChannelType channelType) {
        this.session = session;
        this.channelType = channelType;
    }

    public ChannelType getChannelType() {
        return channelType;
    }

    public Session getSession() {
        return session;
    }
}

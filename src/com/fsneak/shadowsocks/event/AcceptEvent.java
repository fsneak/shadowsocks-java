package com.fsneak.shadowsocks.event;

/**
 * @author fsneak
 */
public class AcceptEvent extends Event {
    @Override
    public Type getType() {
        return Type.ACCEPT;
    }
}

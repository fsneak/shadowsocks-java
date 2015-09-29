package com.fsneak.shadowsocks.event;

/**
 * @author fsneak
 */
public class SelectEvent extends Event {
    @Override
    public Type getType() {
        return Type.SELECT;
    }
}

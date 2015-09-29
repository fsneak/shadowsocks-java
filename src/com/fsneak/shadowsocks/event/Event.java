package com.fsneak.shadowsocks.event;

/**
 * @author fsneak
 */
public abstract class Event {
    public enum Type {
        ACCEPT,
        SELECT,
        READ,
        WRITE,
        ;
    }

    public abstract Type getType();
}

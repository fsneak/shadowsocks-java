package com.fsneak.shadowsocks.event;

/**
 * @author fsneak
 */
public class EventHandlerFactory {
    public static EventHandler getHandler(Event.Type type) {
        switch (type) {
            case ACCEPT:
                return AcceptEventHandler.getInstance();
            case SELECT:
                return SelectEventHandler.getInstance();
            case READ:
                return ReadEventHandler.getInstance();
            case WRITE:
                return WriteEventHandler.getInstance();
        }

        throw new UnsupportedOperationException("type " + type);
    }
}

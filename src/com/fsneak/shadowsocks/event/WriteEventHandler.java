package com.fsneak.shadowsocks.event;

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
        // TODO
    }
}

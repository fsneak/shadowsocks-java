package com.fsneak.shadowsocks.event;

import java.nio.channels.SelectionKey;

/**
 * @author fsneak
 */
public class ReadEvent extends Event {
	private final SelectionKey key;

	public ReadEvent(SelectionKey key) {
		this.key = key;
	}

    @Override
    public Type getType() {
        return Type.READ;
    }

    public SelectionKey getKey() {
		return key;
	}
}

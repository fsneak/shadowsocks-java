package com.fsneak.shadowsocks.event;

import java.nio.channels.SelectionKey;

/**
 * @author xiezhiheng
 */
public class ReadEvent extends Event {
	private final SelectionKey key;

	public ReadEvent(SelectionKey key) {
		this.key = key;
	}

	public SelectionKey getKey() {
		return key;
	}
}

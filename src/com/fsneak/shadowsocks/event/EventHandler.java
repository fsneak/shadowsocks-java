package com.fsneak.shadowsocks.event;

/**
 * @author fsneak
 */
public interface EventHandler<T extends Event> {
	void handle(T event);
}

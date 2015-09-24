package com.fsneak.shadowsocks.event;

import com.fsneak.shadowsocks.ShadowsocksLocal;

/**
 * @author xiezhiheng
 */
public interface EventHandler<T extends Event> {
	void handle(ShadowsocksLocal shadowsocksLocal, T event);
}

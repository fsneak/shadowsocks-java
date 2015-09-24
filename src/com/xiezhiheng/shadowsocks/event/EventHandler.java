package com.xiezhiheng.shadowsocks.event;

import com.xiezhiheng.shadowsocks.ShadowsocksLocal;

/**
 * @author xiezhiheng
 */
public interface EventHandler<T extends Event> {
	void handle(ShadowsocksLocal shadowsocksLocal, T event);
}

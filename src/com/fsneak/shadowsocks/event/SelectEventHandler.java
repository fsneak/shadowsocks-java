package com.fsneak.shadowsocks.event;

import com.fsneak.shadowsocks.log.Logger;
import com.fsneak.shadowsocks.ShadowsocksLocal;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Iterator;
import java.util.Set;

/**
 * @author xiezhiheng
 */
public class SelectEventHandler implements EventHandler<SelectEvent> {
	@Override
	public void handle(ShadowsocksLocal shadowsocksLocal, SelectEvent event) {
		Selector selector = shadowsocksLocal.getSelector();
		EventQueue eventQueue = shadowsocksLocal.getEventQueue();
		try {
			int ready;
			if (eventQueue.isEmpty()) {
				// nothing should be handled, block
				ready = selector.select();
			} else {
				ready = selector.selectNow();
			}

			if (ready > 0) {
				Set<SelectionKey> selectionKeys = selector.selectedKeys();
				Iterator<SelectionKey> iterator = selectionKeys.iterator();
				while (iterator.hasNext()) {
					SelectionKey key = iterator.next();
					handleKey(shadowsocksLocal, key);
					iterator.remove();
				}
			}
		} catch (IOException e) {
			Logger.error(e);
		} finally {
			eventQueue.addEvent(new SelectEvent());
		}
	}

	private void handleKey(ShadowsocksLocal shadowsocksLocal, SelectionKey key) {
		if (!key.isValid()) {
			return;
		}

		EventQueue eventQueue = shadowsocksLocal.getEventQueue();
		if (key.isAcceptable()) {
			eventQueue.addEvent(new AcceptEvent());
		} else if (key.isReadable()) {
			eventQueue.addEvent(new ReadEvent(key));
		}
	}
}

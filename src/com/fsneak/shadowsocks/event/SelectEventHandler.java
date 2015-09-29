package com.fsneak.shadowsocks.event;

import com.fsneak.shadowsocks.log.Logger;
import com.fsneak.shadowsocks.ShadowsocksLocal;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Iterator;
import java.util.Set;

/**
 * @author fsneak
 */
public class SelectEventHandler implements EventHandler<SelectEvent> {
    private static final SelectEventHandler INSTANCE = new SelectEventHandler();

    private SelectEventHandler() {
    }

    public static SelectEventHandler getInstance() {
        return INSTANCE;
    }

	@Override
	public void handle(SelectEvent event) {
		Selector selector = ShadowsocksLocal.getInstance().getSelector();
		try {
			int ready;
			if (ShadowsocksLocal.getInstance().isEventQueueEmpty()) {
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
					handleKey(key);
					iterator.remove();
				}
			}
		} catch (IOException e) {
			Logger.error(e);
		} finally {
            ShadowsocksLocal.getInstance().addEvent(new SelectEvent());
		}
	}

	private void handleKey(SelectionKey key) {
		if (!key.isValid()) {
			return;
		}

		if (key.isAcceptable()) {
            ShadowsocksLocal.getInstance().addEvent(new AcceptEvent());
		} else if (key.isReadable()) {
            ShadowsocksLocal.getInstance().addEvent(new ReadEvent(key));
		}
	}
}

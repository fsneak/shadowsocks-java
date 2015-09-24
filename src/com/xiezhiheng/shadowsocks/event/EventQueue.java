package com.xiezhiheng.shadowsocks.event;

import java.util.LinkedList;

/**
 * @author xiezhiheng
 */
public class EventQueue {
	private final LinkedList<Event> list = new LinkedList<Event>();

	public void addEvent(Event event) {
		list.add(event);
	}

	public Event poll() {
		return list.poll();
	}

	public boolean isEmpty() {
		return list.isEmpty();
	}
}

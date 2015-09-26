package com.fsneak.shadowsocks.socks5;

import com.fsneak.shadowsocks.session.Session;

/**
 * @author xiezhiheng
 */
public class Socks5HandlerFactory {
	public static Socks5StageHandler getHandler(Session.Stage stage) {
		switch (stage) {
			case SOCKS5_HELLO:
				return Socks5HelloHandler.getInstance();
			case SOCKS5_ADDRESS:
				return Socks5AddressHandler.getInstance();
			default:
				throw new IllegalArgumentException("not socks5 stage: " + stage);
		}
	}
}

package com.xiezhiheng.shadowsocks.log;

/**
 * @author xiezhiheng
 */
public class Logger {
	public static void error(String msg, Throwable t) {
		System.out.println(msg);
		t.printStackTrace(System.out);
	}

	public static void error(String msg) {
		System.out.println(msg);
	}

	public static void error(Throwable t) {
		t.printStackTrace(System.out);
	}

	public static void info(String msg) {
		System.out.println(msg);
	}
}

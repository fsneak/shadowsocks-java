package com.fsneak.shadowsocks.log;

/**
 * @author fsneak
 */
public class Logger {
    private static final int DEBUG = 0;
    private static final int INFO = 1;
    private static final int ERROR = 2;

    private static int LEVEL = INFO;

	public static void error(String msg, Throwable t) {
        if (ERROR < LEVEL) {
            return;
        }
        System.out.println(msg);
        t.printStackTrace(System.out);
    }

	public static void error(String msg) {
        if (ERROR < LEVEL) {
            return;
        }

		System.out.println(msg);
	}

	public static void error(Throwable t) {
        if (ERROR < LEVEL) {
            return;
        }

		t.printStackTrace(System.out);
	}

	public static void info(String msg) {
        if (INFO < LEVEL) {
            return;
        }

		System.out.println(msg);
	}

    public static void debug(Throwable t) {
        if (DEBUG < LEVEL) {
            return;
        }

        t.printStackTrace(System.out);
    }

    public static void debug(String msg) {
        if (DEBUG < LEVEL) {
            return;
        }

        System.out.println(msg);
    }
}

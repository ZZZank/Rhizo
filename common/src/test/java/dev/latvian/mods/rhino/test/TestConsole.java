package dev.latvian.mods.rhino.test;

public class TestConsole {
	public static void info(Object o) {
		System.out.println("[INFO]" + o);
	}

	public static void warn(Object o) {
		System.out.println("[WARN]" + o);
	}

	public static void error(Object o) {
		System.out.println("[ERR ]" + o);
	}

	public static void log(Object o) {
		System.out.println(o);
	}
}

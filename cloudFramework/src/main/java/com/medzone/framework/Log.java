package com.medzone.framework;

public final class Log {

	private static String tag = "MEDZONE_FRAMEWORK";

	private Log() {
	}

	public static void v(String msg) {
		android.util.Log.v(tag, msg);
	}

	public static void e(String msg) {
		android.util.Log.e(tag, msg);
	}

	public static void i(String msg) {
		android.util.Log.i(tag, msg);
	}

	public static void w(String msg) {
		android.util.Log.w(tag, msg);
	}

	public static void d(String msg) {
		android.util.Log.d(tag, msg);
	}

	public static void v(String msg, Throwable e) {
		android.util.Log.v(tag, msg, e);
	}

	public static void e(String msg, Throwable e) {
		android.util.Log.v(tag, msg, e);
	}

	public static void i(String msg, Throwable e) {
		android.util.Log.v(tag, msg, e);
	}

	public static void w(String msg, Throwable e) {
		android.util.Log.v(tag, msg, e);
	}

	public static void d(String msg, Throwable e) {
		android.util.Log.v(tag, msg, e);
	}

	public static void v(String mtag, String msg) {
		android.util.Log.v(mtag, msg);
	}

	public static void e(String mtag, String msg) {
		android.util.Log.e(mtag, msg);
	}

	public static void i(String mtag, String msg) {
		android.util.Log.i(mtag, msg);
	}

	public static void w(String mtag, String msg) {
		android.util.Log.w(mtag, msg);
	}

	public static void d(String mtag, String msg) {
		android.util.Log.d(mtag, msg);
	}

	public static void v(String mtag, String msg, Throwable e) {
		android.util.Log.v(mtag, msg, e);
	}

	public static void e(String mtag, String msg, Throwable e) {
		android.util.Log.e(mtag, msg, e);
	}

	public static void i(String mtag, String msg, Throwable e) {
		android.util.Log.e(mtag, msg, e);
	}

	public static void w(String mtag, String msg, Throwable e) {
		android.util.Log.e(mtag, msg, e);
	}

	public static void d(String mtag, String msg, Throwable e) {
		android.util.Log.e(mtag, msg, e);
	}

}

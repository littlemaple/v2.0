package com.medzone.cloud.data.helper;

public class FlagHelper {

	public static final int FLAG_POSITION_VOICE = 0;
	public static final int FLAG_POSITION_AUTO_SHARE = 2;

	private static final int maxLength = 8;

	private static int[] generalValueSet = new int[maxLength];

	public static void put(int position, boolean isSet) {
		if (isVaild(position)) {
			generalValueSet[position] = isSet ? 1 : 0;
			return;
		} else {
			throw new IndexOutOfBoundsException(
					"min position is 0 and max is 8;");
		}
	}

	public static int get(int position) {
		if (isVaild(position)) {
			return generalValueSet[position];
		} else {
			throw new IndexOutOfBoundsException(
					"min position is 0 and max is 8;");
		}
	}

	public static void reset() {
		for (int i = 0; i < generalValueSet.length; i++) {
			generalValueSet[i] = 0;
		}
	}

	public static int calculateFlag() {
		int flag = 0;
		for (int i = 0; i < generalValueSet.length; i++) {
			flag += generalValueSet[i] << i;
		}
		return flag;
	}

	private static boolean isVaild(int position) {
		if (position < maxLength && position >= 0) {
			return true;
		}
		return false;
	}

	/**
	 * 工具类用于解析Flag,获取对应位置的值
	 * 
	 * @param flag
	 *            复合字段
	 * @param position
	 *            要取具体指定项的设置值
	 * @return
	 */
	public static boolean getSetValueInFlag(int flag, int position) {
		if (isVaild(position)) {
			return (flag & (1 << position)) > 0 ? true : false;
		}
		throw new IndexOutOfBoundsException("min position is 0 and max is 8;");
	}
}

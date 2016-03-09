package com.medzone.cloud.clock.manager;

import java.util.List;

import android.content.Context;

public class AlarmManager {

	// private static int accountId;

	private static final int CURRENTALARM = 0;

	public static void initManager(Context context) {
		PropertiesManager.getInstance().initLocalFile(context.getFilesDir());
	}

	/**
	 * @param accountId
	 */
	public static void addCurrentAlarm(Alarm alarm) {
		alarm.setClockID(CURRENTALARM);
		PropertiesManager.getInstance().saveCurrentAlarm(alarm);
	}

	public static Alarm getCurrentAlarm(int accountId) {
		Object res = PropertiesManager.getInstance().readAlarm(accountId,
				CURRENTALARM);
		return (Alarm) res;

	}

	public static void clearCurrentAlarm(int accountId) {
		PropertiesManager.getInstance().deleteAlarm(accountId, CURRENTALARM);
	}

	public static void addAlarm(Alarm alarm) {
		PropertiesManager.getInstance().saveAlarm(alarm);
	}

	public static void deleteAlarm(Alarm alarm) {
		PropertiesManager.getInstance().deleteAlarm(alarm);
	}

	public static void updateAlarm(Alarm alarm) {
		PropertiesManager.getInstance().updateAlarm(alarm);
	}

	public static List<Alarm> ReadAllAlarm(int accountId) {
		List<Alarm> list = PropertiesManager.getInstance().readAccountAlarm(
				accountId);
		return list;
	}

	public static int getAllAlarmSize(int accountId) {
		List<Alarm> list = PropertiesManager.getInstance().readAccountAlarm(
				accountId);
		return list == null ? 0 : list.size();
	}

}

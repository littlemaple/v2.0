package com.medzone.cloud.util;

import android.content.Context;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;

import com.medzone.cloud.CloudApplication;

public class WakeLockUtil {

	private static WakeLock wakeLock = null;

	public static void acquireWakeLock() {
		if (null == wakeLock) {
			PowerManager pm = (PowerManager) CloudApplication.getInstance()
					.getApplicationContext()
					.getSystemService(Context.POWER_SERVICE);
			wakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK
					| PowerManager.ON_AFTER_RELEASE, "mCloudWakeLock");
			if (null != wakeLock) {
				wakeLock.acquire();
			}
		}
	}

	public static void releaseWakeLock() {
		if (null != wakeLock) {
			wakeLock.release();
			wakeLock = null;
		}
	}
}

package com.medzone.cloud.clock;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;

import com.medzone.framework.Log;

public class AlarmReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context ctx, Intent mIntent) {

		// 点亮屏幕
		PowerManager pm = (PowerManager) ctx
				.getSystemService(Context.POWER_SERVICE);
		PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK
				| PowerManager.ACQUIRE_CAUSES_WAKEUP
				| PowerManager.ON_AFTER_RELEASE, "AppAlarmReceiver");
		wl.acquire(30000);

		Intent i = new Intent(ctx, AlarmService.class);
		Log.e(">>>>>>>alarm receive and start service");
		i.setAction(AlarmService.ACTION_LAUNCH_ALARM);
		ctx.startService(i);

	}

}

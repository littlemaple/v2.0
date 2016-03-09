package com.medzone.cloud.clock;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.KeyguardManager;
import android.app.KeyguardManager.KeyguardLock;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.SparseIntArray;
import android.view.Gravity;
import android.view.WindowManager.LayoutParams;
import android.widget.Toast;

import com.medzone.cloud.CloudApplicationPreference;
import com.medzone.cloud.clock.manager.Alarm;
import com.medzone.cloud.data.helper.ClockHelper;
import com.medzone.cloud.data.helper.CustomComparator;
import com.medzone.cloud.ui.dialog.global.GlobalDialogUtil;
import com.medzone.cloud.ui.dialog.global.GlobalDialogUtil.onGlobalClickListener;
import com.medzone.framework.Config;
import com.medzone.framework.Log;
import com.medzone.framework.data.bean.imp.Account;
import com.medzone.mcloud.R;

public class AlarmService extends Service {
	// service action
	public static final String ACTION_SET_ALARM = "set_alarm";
	public static final String ACTION_LAUNCH_ALARM = "launch_alarm";
	public static final String ACTION_FORCE_LAUNCH_ALARM = "force_launch";
	public static final String ACTION_STOP_ALARM = "stop_alarm";
	public static final String ACTION_STOP_ALARM_AND_KILL = "stop_alarm_and_kill";
	public static final String ACTION_SET_SILENT_ALARM = "set_silent_alarm";
	public static final String ACTION_SET_SHOW_NOTIF = "set_show_notif";
	public static final String ACTION_REFRESH = "action.refresh";
	public static final String EXTRA_SHOW_NOTIF = "show_notification";
	public static final String EXTRA_DONT_DISABLE = "dont_disable";
	public static final String ACTION_DELETE_ALARM = "delete_alarm";
	public static final String PREF_FILE_NAME = "AutoAppLauncherPrefs";
	private static final int NOTIFY_TYPE_ONE = 1;
	private static final int NOTIFY_TYPE_TWO = 2;

	public static final boolean DEFAULT_SHOW_NOTIF = false;
	// private PackageManager mPackageManager;
	private NotificationManager mNotificationManager;
	private PowerManager.WakeLock mFullWakeLock;
	private PowerManager.WakeLock mPartialWakeLock;
	private KeyguardLock mKeyguardLock;
	private PendingIntent sender;
	private AlarmManager alarmManager;
	private Vibrator vibrator;
	private final Handler mHandler = new Handler();
	private boolean mIsPlayingBackup;

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		mPartialWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
				"AppAlarmTag");
		mPartialWakeLock.acquire();
		// mPackageManager = getPackageManager();
		mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		mIsPlayingBackup = true;
		alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
		sender = PendingIntent.getBroadcast(this, 0, new Intent(this,
				AlarmReceiver.class), 0);
	}

	@Override
	public void onDestroy() {
		setNextAlarm();
		if (mFullWakeLock != null)
			mFullWakeLock.release();
		if (mNotificationManager != null)
			mNotificationManager.cancelAll();
		if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			if (vibrator != null && vibrator.hasVibrator()) {
				vibrator.cancel();
			}
		}
		Log.e(">>>>关闭提醒服务");

		super.onDestroy();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		if (ClockHelper.isLoginState()) {
			com.medzone.cloud.clock.manager.AlarmManager.initManager(this);
			Log.e(">>>>闹钟提醒处于登陆状态");
			doAction(intent);
			try {
				if (mPartialWakeLock.isHeld()) {
					mPartialWakeLock.release();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return 1;
		} else {
			Log.e(">>>>闹钟提醒处于注销状态");
			return 2;
		}
	}

	private void doAction(Intent intent) {
		if (intent != null) {
			String action = intent.getAction();
			if (action.equals(ACTION_SET_ALARM)) {
				actionSetAlarm();
			} else if (action.equals(ACTION_SET_SILENT_ALARM)) {
				actionSetAlarm();
			} else if (action.equals(ACTION_LAUNCH_ALARM)) {
				actionLaunchAlarm();
			} else if (action.equals(ACTION_FORCE_LAUNCH_ALARM)) {
				actionForceLaunchAlarm();
			} else if (action.equals(ACTION_STOP_ALARM)) {
				actionStopAlarm();
			} else if (action.equals(ACTION_STOP_ALARM_AND_KILL)) {
				actionStopAlarm();
			} else if (action.equals(ACTION_SET_SHOW_NOTIF)) {
				actionShowNotify(intent);
			} else if (action.equals(ACTION_DELETE_ALARM)) {
				actionDeleteAlarm();
			}
		}
	}

	/**
	 * 删除闹钟后清除已经在系统中设定的闹钟
	 */
	public void actionDeleteAlarm() {
		alarmManager.cancel(sender);
		setNextAlarm();
	}

	/**
	 * 编辑闹钟后显示的时间
	 * 
	 * @param intent
	 */
	public void actionShowNotify(Intent intent) {
		if (intent.hasExtra(ClockHelper.CURRNET_TIME)) {
			String currentTime = intent
					.getStringExtra(ClockHelper.CURRNET_TIME);
			int repeatition = intent.getIntExtra(ClockHelper.REPEATITION, 0);
			if (repeatition == 0) {
				String content = String.format(
						getResources().getString(R.string.remind_next_time),
						currentTime);
				// showAlarmSetNotification(content, currentTime);
				Toast.makeText(this, content, Toast.LENGTH_SHORT).show();
			} else {
				Long time = ClockHelper.getNextTimemillsecond(ClockHelper
						.getAlarmDate(currentTime).getTime(), repeatition);
				Long interval = time - System.currentTimeMillis();

				String content = String.format(
						getResources().getString(
								R.string.remind_next_time_start),
						ClockHelper.getIntervalText(interval));
				// showAlarmSetNotification(content, currentTime);
				Toast.makeText(this, content, Toast.LENGTH_SHORT).show();
			}
		}
		alarmHandler.sendEmptyMessageDelayed(1, 2000);
		setNextAlarm();
	}

	/**
	 * 闹钟到点后的动作
	 */
	// FIXME clockhelper 的生命周期与service？
	private void actionLaunchAlarm() {
		Log.e("launch alarm");
		if (Config.isDeveloperMode) {
			Alarm alarm = ClockHelper.getCurrentAlarm();
			Log.e(">>>>>>" + alarm == null ? null : alarm.getClockTime());
		}
		if (ClockHelper.getCurrentAlarm() != null) {
			if (ClockHelper.getCurrentAlarm().getRepetition().intValue() == 0) {
				// 启动闹钟时>>>>不重复----修改成关闭状态
				ClockHelper.getCurrentAlarm().setSwitchState(false);
				// ClockCache.getInstance().addOrUpdate(ClockHelper.currentAlarm);
				com.medzone.cloud.clock.manager.AlarmManager
						.updateAlarm(ClockHelper.getCurrentAlarm());
			}
			showSnoozeDialog(ClockHelper.getCurrentAlarm());
		}
		launchAlarm();
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			turnOnForeground(getNotification(R.string.as_nm_launched,
					R.string.as_nt_launched));
			Log.e(">>>>>>>>3.0以上版本");
		} else {
			Log.e(">>>>>>>>3.0以下版本null");
			turnOnForeground(null);
		}
		Log.e("alarm execute complete");
	}

	public void actionSetAlarm() {
		setNextAlarm();
	}

	private void actionForceLaunchAlarm() {
		// 登陆后强制启动闹钟提醒
		setNextAlarm();
	}

	private void actionStopAlarm() {

		Intent intent = new Intent(ACTION_REFRESH);
		sendBroadcast(intent);
		mIsPlayingBackup = false;
		try {
			mRingtone.stop();
			mRingtone = null;
		} catch (Exception e) {
		}
		stopSelf();
	}

	public void showSnoozeDialog(Alarm clock) {

		String time = TextUtils.isEmpty(clock.getClockTime()) ? "" : clock
				.getClockTime();
		String label = TextUtils.isEmpty(clock.getLabel()) ? "" : clock
				.getLabel();
		GlobalDialogUtil.showGlobalDialog(this,
				getText(R.string.reminder_measure).toString(), time + "\n"
						+ label, new onGlobalClickListener() {
					@Override
					public void onClick() {
						actionStopAlarm();
					}
				});
	}

	private void launchAlarm() {

		launchApp();
		mHandler.postDelayed(mSetTask, 200);
	}

	public void stopAlarm() {
		Intent i = new Intent(getBaseContext(), AlarmService.class);
		i.setAction(AlarmService.ACTION_STOP_ALARM);
		startService(i);
	}

	private void launchApp() {

		AudioManager am = (AudioManager) getBaseContext().getSystemService(
				AUDIO_SERVICE);
		am.setMode(AudioManager.MODE_RINGTONE);
		am.setSpeakerphoneOn(true);
		am.setStreamVolume(AudioManager.STREAM_VOICE_CALL,
				am.getStreamMaxVolume(AudioManager.STREAM_RING),
				AudioManager.FLAG_VIBRATE);
		playBackMusicAndVirebor();
	}

	public void playBackMusicAndVirebor() {
		Thread t = new Thread(mPlayRintoneTask);
		t.setDaemon(true);
		t.start();
	}

	// 生成通知项
	@SuppressLint("NewApi")
	private Notification getNotification(int message, int ticker) {

		Intent delIntent = new Intent(this, AlarmService.class);
		delIntent.setAction(ACTION_STOP_ALARM);

		Intent intent = new Intent(getBaseContext(), TransitActivity.class);
		intent.setAction(Intent.ACTION_MAIN);
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
				intent, 0);
		Notification.Builder noti = new Notification.Builder(this);
		noti.setSmallIcon(R.drawable.ic_launcher);
		noti.setContentText(getString(ticker));
		noti.setWhen(System.currentTimeMillis());
		noti.setContentIntent(contentIntent);
		noti.setContentTitle(getString(R.string.as_notif_constant));
		noti.setDeleteIntent(PendingIntent.getService(this, 0, delIntent, 0));
		noti.setLights(0xffffff00, 300, 1000);
		Notification notif = noti.build();
		notif.flags |= Notification.FLAG_AUTO_CANCEL;
		return notif;
	}

	@SuppressWarnings("unused")
	private void showAlarmSetNotification(String message, String timeString) {

		Intent intent = new Intent(getBaseContext(), AlarmListActivity.class);
		intent.setAction(Intent.ACTION_MAIN);
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
				intent, 0);
		Bitmap btm = BitmapFactory.decodeResource(getResources(),
				R.drawable.ic_launcher);
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
				this)
				.setSmallIcon(R.drawable.ic_launcher)
				.setContentTitle(
						getResources().getString(R.string.reminder_measure))
				.setContentText(message);
		mBuilder.setTicker(getResources().getString(R.string.as_nt_launched));
		mBuilder.setLargeIcon(btm);
		mBuilder.setAutoCancel(true);
		mBuilder.setContentIntent(contentIntent);
		mNotificationManager.notify(NOTIFY_TYPE_TWO, mBuilder.build());

	}

	private void turnOnForeground(Notification notif) {

		PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		// 亮屏
		mFullWakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK
				| PowerManager.ACQUIRE_CAUSES_WAKEUP
				| PowerManager.ON_AFTER_RELEASE, "AppAlarmTag");
		mFullWakeLock.acquire();
		// 解锁
		KeyguardManager kgm = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
		mKeyguardLock = kgm.newKeyguardLock("unlock");
		mKeyguardLock.disableKeyguard();
		if (notif != null) {
			try {
				Method m = Service.class.getMethod("startForeground",
						new Class[] { int.class, Notification.class });
				m.invoke(this, NOTIFY_TYPE_ONE, notif);
			} catch (Exception e) {
				stopForeground(true);
				mNotificationManager.notify(NOTIFY_TYPE_ONE, notif);
			}
		}
	}

	public static String getIntentUri(Intent i) {
		String rtr = "";
		try {
			Method m = Intent.class.getMethod("toUri",
					new Class[] { int.class });
			rtr = (String) m.invoke(i,
					Intent.class.getField("URI_INTENT_SCHEME").getInt(null));
		} catch (Exception e) {
			rtr = i.toURI();
		}
		return rtr;
	}

	/**
	 * Is the battery currently discharging?
	 * 
	 * @return True if our battery is discharging. False otherwise.
	 */
	public static boolean isBatteryDischarging(Context c) {
		try {
			Intent batteryIntent = c.registerReceiver(null, new IntentFilter(
					Intent.ACTION_BATTERY_CHANGED));
			if (batteryIntent == null) {
				return true;
			}
			int batteryStatus = batteryIntent.getIntExtra(
					BatteryManager.EXTRA_PLUGGED, 0);
			if (batteryStatus == 0) {
				return true;
			}
			return batteryStatus == 0;
		} catch (Exception e) {
			return true;
		}
	}

	private boolean isPhoneNotIdle() {
		TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		return tm.getCallState() != TelephonyManager.CALL_STATE_IDLE;
	}

	@SuppressWarnings("unused")
	private boolean isBatteryDischarging() {
		return isBatteryDischarging(this);
	}

	private Runnable mSetTask = new Runnable() {
		@Override
		public void run() {
			try {
				setNextAlarm();
				long timeout = 1000;
				mHandler.postDelayed(mStopTask, timeout * 30);
			} catch (Exception e) {
				Intent i = new Intent(getBaseContext(), AlarmService.class);
				i.setAction(ACTION_SET_SILENT_ALARM);
				startService(i);
			}
		}
	};

	private Ringtone mRingtone;

	private Ringtone getAlarmRingtone() {
		Ringtone r = null;
		try {
			Uri uri = RingtoneManager.getActualDefaultRingtoneUri(
					getBaseContext(), RingtoneManager.TYPE_ALARM);
			r = RingtoneManager.getRingtone(getBaseContext(), uri);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return r;
	}

	private Runnable mPlayRintoneTask = new Runnable() {

		@Override
		public void run() {
			try {
				if (mRingtone == null) {
					mRingtone = getAlarmRingtone();
				}
				// 开启振动
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
					vibrator.vibrate(new long[] { 800, 1000, 800 }, 0);
				while (mIsPlayingBackup) {
					if (!mRingtone.isPlaying()) {
						mRingtone.play();
					}
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
					}
				}
			} catch (Exception e) {

			}
			mRingtone = null;
		}
	};

	private Runnable mStopTask = new Runnable() {

		@Override
		public void run() {
			mIsPlayingBackup = false;
			try {
				mRingtone.stop();
				vibrator.cancel();
				mRingtone = null;
				Log.e("alarm stop ring");
				stopAlarm();
			} catch (Exception e) {
			}
		}

	};

	@SuppressWarnings("unused")
	private final Runnable mSetSpeakerphoneTask = new Runnable() {
		@Override
		public void run() {
			int i = 0;
			do {
				i += 1;
				try {
					Thread.sleep(6000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			} while (!isPhoneNotIdle() || (i < 5));
			AudioManager am = (AudioManager) getBaseContext().getSystemService(
					AUDIO_SERVICE);
			am.setMode(AudioManager.MODE_IN_CALL);
			if (!am.isSpeakerphoneOn()) {
				am.setSpeakerphoneOn(true);
				am.setStreamVolume(AudioManager.STREAM_VOICE_CALL,
						am.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL),
						AudioManager.FLAG_SHOW_UI);
			}
		}
	};

	private void setNextAlarm() {
		// List<Alarm> list = ClockCache.getInstance().read();
		Account account = CloudApplicationPreference.getLoginAccount();
		if (account == null) {
			Log.e(">>account" + account);
			return;
		}
		List<Alarm> list = com.medzone.cloud.clock.manager.AlarmManager
				.ReadAllAlarm(account.getAccountID());
		CustomComparator<Alarm> cmp = new CustomComparator<Alarm>();
		Collections.sort(list, cmp);
		if (list == null) {
			// 如果没有时刻则将闹钟管理器清空
			ClockHelper.clearCurrentAlarm();
			alarmManager.cancel(sender);
			return;
		}
		for (Alarm c : list) {
			if (c != null && c.isSwitchState()) {
				if (c.getRepetition() == 0) {
					if (setAlarmOfNoRepeat(c)) {
						break;
					} else {
						ClockHelper.clearCurrentAlarm();
					}
				} else {
					if (setAlarmOfRepeat(c)) {
						break;
					} else {
						ClockHelper.clearCurrentAlarm();
					}
				}
			} else {
				// 将currentAlarm清空
				ClockHelper.clearCurrentAlarm();
				// 如果没有时刻则将闹钟管理器清空
				alarmManager.cancel(sender);
			}
		}
	}

	/**
	 * 处理逻辑 只提醒一次的情况
	 * 
	 * isNext null: 需要设置成下一次时间 true ：
	 * 
	 * @param c
	 */
	public boolean setAlarmOfNoRepeat(Alarm c) {
		if (ClockHelper.getAlarmDate(c.getClockTime()).getTime() > System
				.currentTimeMillis()) {
			Log.e(">>>>set no repeat time" + c.getClockTime());
			setAlarm(c);
			return true;
		} else {
			// 日期已过期,判断是否下次设置
			if (c.isNext()) {
			} else {
				c.setSwitchState(false);
				// ClockCache.getInstance().addOrUpdate(c);
				com.medzone.cloud.clock.manager.AlarmManager.updateAlarm(c);
			}
			return false;
		}
	}

	/**
	 * 处理逻辑 循环提醒的情况
	 * 
	 * @param c
	 */
	public boolean setAlarmOfRepeat(Alarm c) {
		// 判断重复是否包含今天
		SparseIntArray sArray = ClockHelper.getWeekRepeatition(c
				.getRepetition());
		if (sArray.get(ClockHelper.getTodaySparseKey()) == 1) {
			if (ClockHelper.getAlarmDate(c.getClockTime()).getTime() > System
					.currentTimeMillis()) {
				setAlarm(c);
				return true;
			} else {
				// 日期已过期
				return false;
			}
		} else {
			// 不在当天提醒
			return false;
		}
	}

	/**
	 * 
	 * 设置闹钟后，该时间已经提醒一次 = 设置isNext false 每次add or edit 闹钟的时候将isNext true
	 * 
	 * 具体设置闹钟的方法
	 * 
	 * @param time
	 */
	public void setAlarm(Alarm alarm) {
		alarmManager.cancel(sender);
		alarmManager.set(AlarmManager.RTC,
				ClockHelper.getAlarmDate(alarm.getClockTime()).getTime(),
				sender);
		alarm.setIsNext(false);
		// ClockCache.getInstance().addOrUpdate(clock);
		com.medzone.cloud.clock.manager.AlarmManager.updateAlarm(alarm);
		ClockHelper.setCurrentAlarm(alarm);
		Log.e("next launch alarm time" + alarm.getClockTime());
	}

	/**
	 * 
	 * @author maple 关闭通知栏信息
	 * 
	 */

	private Handler alarmHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			mNotificationManager.cancelAll();
		};
	};
}

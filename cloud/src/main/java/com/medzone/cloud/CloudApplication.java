package com.medzone.cloud;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.graphics.Point;
import android.os.Build;
import android.os.Environment;
import android.os.StrictMode;
import android.view.Display;
import android.view.WindowManager;

import com.medzone.cloud.database.DatabaseHelper;
import com.medzone.cloud.defender.DefenderServiceManager;
import com.medzone.cloud.network.NetworkClient;
import com.medzone.cloud.ui.SplashScreenActivity;
import com.medzone.common.media.inf.IOnServiceConnectComplete;
import com.medzone.framework.Config;
import com.medzone.framework.Log;

public class CloudApplication extends Application implements
		Thread.UncaughtExceptionHandler {

	private static CloudApplication instance;
	public static DefenderServiceManager defenderServiceManager;
	public static int width;
	public static int height;
	public static int actionBarHeight;

	// public static int statusBarHeight;

	public static CloudApplication getInstance() {
		return instance;
	}

	@Override
	public void onCreate() {
		initStrictMode();
		super.onCreate();
		Thread.setDefaultUncaughtExceptionHandler(this);
		CloudApplication.instance = this;
		initApplication();
		Log.i("CloudApplication#onCreate");
	}

	@Override
	public void onLowMemory() {
		super.onLowMemory();
		Log.i("CloudApplication#onLowMemory");
	}

	@SuppressLint("NewApi")
	@Override
	public void onTrimMemory(int level) {
		super.onTrimMemory(level);
		Log.i("CloudApplication#onTrimMemory:" + level);
	}

	@Override
	public void onTerminate() {
		super.onTerminate();
		Log.i("CloudApplication#onTerminate");
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		Log.i("CloudApplication#onConfigurationChanged");
	}

	// Don't place here time-consuming operation.
	private void initApplication() {
		// Initializes the global variables.
		// GlobalVars.init(getApplicationContext());
		// Initializes the global variable initialization configuration file.
		CloudApplicationPreference.init(getApplicationContext());

		initDefenderService(false);

		measureWindow();
	}

	protected void initStrictMode() {
		if (Config.isDeveloperMode
				&& Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
			StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
					.detectAll().penaltyLog().build());
			StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
					.detectAll().penaltyLog().build());
		}
	}

	public String getCurProcessName(Context context) {
		int pid = android.os.Process.myPid();
		ActivityManager mActivityManager = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		for (ActivityManager.RunningAppProcessInfo appProcess : mActivityManager
				.getRunningAppProcesses()) {
			if (appProcess.pid == pid) {
				return appProcess.processName;
			}
		}
		return null;
	}

	public void initDefenderService(boolean isAutoStart) {
		if (defenderServiceManager == null) {
			defenderServiceManager = new DefenderServiceManager(
					getApplicationContext());
		}
		// 此时无法得知该服务的状态是首次连接，还是已经连接成功
		if (isAutoStart) {
			defenderServiceManager.setOnServiceConnectComplete(ioscc);
		}
		if (defenderServiceManager.connectService()) {
			if (isAutoStart) {
				defenderServiceManager.startJPush();
				Log.v("key:service已经连接状态，直接启动接收JPUSH");
			}
		}
	}

	private IOnServiceConnectComplete ioscc = new IOnServiceConnectComplete() {

		@Override
		public void OnServiceConnectComplete() {
			Log.v("key:等待service建立连接后，启动接收JPUSH");
			defenderServiceManager.startJPush();
		}
	};

	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	private void measureWindow() {
		WindowManager wm = (WindowManager) getApplicationContext()
				.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		if (isNewAPI(Build.VERSION_CODES.HONEYCOMB_MR2)) {
			Point size = new Point();
			display.getSize(size);
			width = size.x;
			height = size.y;
		} else {
			width = display.getWidth();
			height = display.getHeight();
		}
	}

	public static int getStatusBarHeight(Context context) {
		Class<?> c = null;
		Object obj = null;
		Field field = null;
		int x = 0, statusBarHeight = 0;
		try {
			c = Class.forName("com.android.internal.R$dimen");
			obj = c.newInstance();
			field = c.getField("status_bar_height");
			x = Integer.parseInt(field.get(obj).toString());
			statusBarHeight = context.getResources().getDimensionPixelSize(x);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return statusBarHeight;
	}

	public void exit() {
		unInitApplication();
	}

	private void unInitApplication() {
		DatabaseHelper.unInit();
		NetworkClient.uninit();
		GlobalVars.uninit();
		CloudImageLoader.getInstance().unInitImageLoader();
		System.gc();
	}

	public static boolean isNewAPI(int apiLevel) {
		if (android.os.Build.VERSION.SDK_INT >= apiLevel) {
			return true;
		}
		return false;
	}

	@Override
	public void uncaughtException(Thread thread, Throwable ex) {

		handleException(ex);

		Intent intent = new Intent(getApplicationContext(),
				SplashScreenActivity.class);
		PendingIntent restartIntent = PendingIntent.getActivity(
				getApplicationContext(), 0, intent,
				Intent.FLAG_ACTIVITY_NEW_TASK);
		AlarmManager mgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100,
				restartIntent);
		// android.os.Process.killProcess(android.os.Process.myPid());
		System.exit(0);
	}

	// 用来存储设备信息和异常信息
	private Map<String, String> infos = new HashMap<String, String>();
	// 用于格式化日期,作为日志文件名的一部分
	private DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");

	/**
	 * 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成.
	 * 
	 * @param ex
	 * @return true:如果处理了该异常信息;否则返回false.
	 */
	private boolean handleException(Throwable ex) {
		if (ex == null) {
			return false;
		}
		// 收集设备参数信息
		collectDeviceInfo(getApplicationContext());
		// 保存日志文件
		saveCrashInfo2File(ex);
		return true;
	}

	/**
	 * 收集设备参数信息
	 * 
	 * @param ctx
	 */
	public void collectDeviceInfo(Context ctx) {
		try {
			PackageManager pm = ctx.getPackageManager();
			PackageInfo pi = pm.getPackageInfo(ctx.getPackageName(),
					PackageManager.GET_ACTIVITIES);
			if (pi != null) {
				String versionName = pi.versionName == null ? "null"
						: pi.versionName;
				String versionCode = pi.versionCode + "";
				infos.put("versionName", versionName);
				infos.put("versionCode", versionCode);
			}
		} catch (NameNotFoundException e) {
			Log.e("an error occured when collect package info", e);
		}
		Field[] fields = Build.class.getDeclaredFields();
		for (Field field : fields) {
			try {
				field.setAccessible(true);
				infos.put(field.getName(), field.get(null).toString());
				Log.d(field.getName() + " : " + field.get(null));
			} catch (Exception e) {
				Log.e("an error occured when collect crash info", e);
			}
		}
	}

	/**
	 * 保存错误信息到文件中
	 * 
	 * @param ex
	 * @return 返回文件名称,便于将文件传送到服务器
	 */
	private String saveCrashInfo2File(Throwable ex) {

		StringBuffer sb = new StringBuffer();
		for (Map.Entry<String, String> entry : infos.entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue();
			sb.append(key + "=" + value + "\n");
		}

		Writer writer = new StringWriter();
		PrintWriter printWriter = new PrintWriter(writer);
		ex.printStackTrace(printWriter);
		Throwable cause = ex.getCause();
		while (cause != null) {
			cause.printStackTrace(printWriter);
			cause = cause.getCause();
		}
		printWriter.close();
		String result = writer.toString();
		sb.append(result);
		try {
			long timestamp = System.currentTimeMillis();
			String time = formatter.format(new Date());
			String fileName = "crash-" + time + "-" + timestamp + ".log";
			if (Environment.getExternalStorageState().equals(
					Environment.MEDIA_MOUNTED)) {

				File fileDir = Environment.getExternalStorageDirectory();
				String path = fileDir.getPath() + "/" + getPackageName()
						+ "/log/";

				File dir = new File(path);
				if (!dir.exists()) {
					dir.mkdirs();
				}
				FileOutputStream fos = new FileOutputStream(path + fileName);
				fos.write(sb.toString().getBytes());
				fos.close();
			}
			return fileName;
		} catch (Exception e) {
			Log.e("an error occured while writing file...", e);
		}

		return null;
	}
}

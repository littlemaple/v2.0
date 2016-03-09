/**
 * 
 */
package com.medzone.cloud.defender;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.IBinder;
import android.os.RemoteException;
import cn.jpush.android.api.JPushInterface;

import com.medzone.cloud.defender.DefenderServiceConnect.Stub;

/**
 * @author ChenJunQi.
 * 
 */
public class DefenderService extends Service {

	private Defender mDefender;
	private ConnectivityReceiver mConnectivityReceiver;
	private BootUninstallReceiver mBootUninstallReceiver;

	@Override
	public void onCreate() {
		super.onCreate();
		System.out.println("DefenderService#onCreate()");
		System.out.println("DefenderService#getConnectionState()"
				+ JPushInterface.getConnectionState(getApplicationContext()));
		mDefender = new Defender(getApplicationContext());
		registerConnectivityReceiver();
		registerInstalledReceiver();
	}

	@Override
	public IBinder onBind(Intent intent) {
		System.out.println("DefenderService#onBind()");
		System.out.println("DefenderService#getConnectionState()"
				+ JPushInterface.getConnectionState(getApplicationContext()));
		return iBinder;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		System.out.println("DefenderService#onStartCommand()");
		System.out.println("DefenderService#getConnectionState()"
				+ JPushInterface.getConnectionState(getApplicationContext()));
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public boolean onUnbind(Intent intent) {
		System.out.println("DefenderService#onUnbind()");
		System.out.println("DefenderService#getConnectionState()"
				+ JPushInterface.getConnectionState(getApplicationContext()));
		mDefender.stopJPush();
		return super.onUnbind(intent);
	}

	@SuppressLint("NewApi")
	@Override
	public void onTaskRemoved(Intent rootIntent) {
		super.onTaskRemoved(rootIntent);
		System.out.println("DefenderService#onTaskRemoved()");
		System.out.println("DefenderService#getConnectionState()"
				+ JPushInterface.getConnectionState(getApplicationContext()));
	}

	@Override
	public void onRebind(Intent intent) {
		super.onRebind(intent);
		System.out.println("DefenderService#onRebind()");
	}

	@Override
	public void onLowMemory() {
		super.onLowMemory();
		System.out.println("DefenderService#onLowMemory()");
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		System.out.println("DefenderService#onDestroy()");
		mDefender.stopJPush();
		unregisterConnectivityReceiver();
		unregisterInstalledReceiver();
	}

	private DefenderServiceConnect.Stub iBinder = new Stub() {

		@Override
		public void initJPush() throws RemoteException {
			mDefender.initJPush();
		}

		@Override
		public void startJPush() throws RemoteException {
			mDefender.startJPush();
		}

		@Override
		public void stopJPush() throws RemoteException {
			mDefender.stopJPush();
		}

		@Override
		public String getRegisterID() throws RemoteException {
			return mDefender.getRegisterID();
		}

	};

	private void registerConnectivityReceiver() {
		if (mConnectivityReceiver == null) {
			mConnectivityReceiver = new ConnectivityReceiver();
			IntentFilter filter = new IntentFilter();
			filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
			registerReceiver(mConnectivityReceiver, filter);
		}
	}

	private void registerInstalledReceiver() {
		if (mBootUninstallReceiver == null) {
			mBootUninstallReceiver = new BootUninstallReceiver();
			IntentFilter filter = new IntentFilter();
			filter.addAction(Intent.ACTION_PACKAGE_REMOVED);
			registerReceiver(mBootUninstallReceiver, filter);
		}
	}

	private void unregisterConnectivityReceiver() {
		if (mConnectivityReceiver != null) {
			unregisterReceiver(mConnectivityReceiver);
		}
	}

	private void unregisterInstalledReceiver() {
		if (mBootUninstallReceiver != null) {
			unregisterReceiver(mBootUninstallReceiver);
		}
	}

	class BootUninstallReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(Intent.ACTION_PACKAGE_REMOVED)) {
				String packageName = intent.getDataString();
				if (packageName.equals(getPackageName())) {
					if (mDefender != null) {
						System.out.println("DefenderService#stopJPush()");
						mDefender.stopJPush();
					}
				}
			}
		}
	}

}

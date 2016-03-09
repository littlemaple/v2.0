/**
 * 
 */
package com.medzone.cloud.defender;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;

import com.medzone.common.media.inf.IOnServiceConnectComplete;
import com.medzone.framework.Log;

/**
 * @author ChenJunQi.
 * 
 * 
 */
public class DefenderServiceManager {

	private final static String SERVICE_NAME = "com.medzone.cloud.defender";

	private Context mContext;// Applciaton context
	private ServiceConnection mServiceConnection;
	private DefenderServiceConnect mServiceConnect;
	private IOnServiceConnectComplete onServiceConnectComplete;
	private Boolean mConnectComplete;

	public void setOnServiceConnectComplete(
			IOnServiceConnectComplete IServiceConnect) {
		this.onServiceConnectComplete = IServiceConnect;
	}

	/**
	 * 
	 */
	public DefenderServiceManager(Context context) {
		this.mContext = context;
		init(false);
	}

	private void init(final boolean isAutoStartJpush) {
		mServiceConnection = new ServiceConnection() {

			@Override
			public void onServiceDisconnected(ComponentName name) {
				Log.d(">>>推送等#DefenderService进程服务已经断开！");
			}

			@Override
			public void onServiceConnected(ComponentName name, IBinder service) {
				mServiceConnect = DefenderServiceConnect.Stub
						.asInterface(service);
				initJPush();

				if (isAutoStartJpush) {
					startJPush();
				}
				if (onServiceConnectComplete != null) {
					onServiceConnectComplete.OnServiceConnectComplete();
				}
				Log.d(">>>推送等#DefenderService进程服务已经连接！");
			}
		};
		mConnectComplete = false;
		mServiceConnect = null;
	}

	public boolean connectService() {
		if (mConnectComplete == true) {
			return true;
		}
		if (mContext != null) {
			Intent intent = new Intent(SERVICE_NAME);
			mContext.bindService(intent, mServiceConnection,
					Context.BIND_AUTO_CREATE);
			mContext.startService(intent);
			mConnectComplete = true;
			return true;
		}
		return false;
	}

	public boolean disconnectService() {
		if (mConnectComplete == false) {
			return true;
		}
		if (mContext != null) {
			mContext.unbindService(mServiceConnection);
			Intent service = new Intent(SERVICE_NAME);
			mContext.stopService(service);
			mServiceConnect = null;
			mConnectComplete = false;
			return true;
		}

		return false;
	}

	public void initJPush() {
		try {
			if (mServiceConnect != null) {
				mServiceConnect.initJPush();
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	public void startJPush() {
		if (mServiceConnect != null) {
			try {
				mServiceConnect.startJPush();
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	}

	public void stopJPush() {
		if (mServiceConnect != null) {
			try {
				mServiceConnect.stopJPush();
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	}

	public String getRegisterID() {
		if (mServiceConnect != null) {
			try {
				return mServiceConnect.getRegisterID();
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

}

package com.medzone.common.media.service;

import java.util.ArrayList;
import java.util.List;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.medzone.common.media.aidl.ServiceConnect;
import com.medzone.common.media.bean.Media;
import com.medzone.common.media.inf.IOnServiceConnectComplete;
import com.medzone.common.media.player.PlayMode;
import com.medzone.common.media.player.PlayState;

public class ServiceManager {

	private final static String SERVICE_NAME = "com.medzone.common.media.service";
	private Boolean mConnectComplete;
	private ServiceConnection mServiceConnection;
	private ServiceConnect mMusicConnect;
	private Context mContext;
	private IOnServiceConnectComplete onServiceConnectComplete;

	public ServiceManager(Context context) {
		mContext = context;
		init();
	}

	private void init() {
		mServiceConnection = new ServiceConnection() {
			@Override
			public void onServiceDisconnected(ComponentName name) {
				return;
			}

			@Override
			public void onServiceConnected(ComponentName name, IBinder service) {
				// 获取服务端的Proxy代码，�?过proxy可以访问其中的远程Binder对象
				mMusicConnect = ServiceConnect.Stub.asInterface(service);
				if (mMusicConnect != null) {
					onServiceConnectComplete.OnServiceConnectComplete();
				}
			}
		};
		mConnectComplete = false;
		mMusicConnect = null;
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
			mMusicConnect = null;
			mConnectComplete = false;
			return true;
		}

		return false;
	}

	public void exit() {
		if (mConnectComplete) {
			try {
				mMusicConnect.exit();
			} catch (Exception e) {
				e.printStackTrace();
			}
			mContext.unbindService(mServiceConnection);
			Intent intent = new Intent(SERVICE_NAME);
			mContext.stopService(intent);
			mMusicConnect = null;
			mConnectComplete = false;

		}
	}

	public void reset() {
		if (mConnectComplete) {
			try {
				mMusicConnect.exit();
			} catch (Exception e) {
			}
		}
	}

	public void setOnServiceConnectComplete(
			IOnServiceConnectComplete IServiceConnect) {
		this.onServiceConnectComplete = IServiceConnect;
	}

	public void setList(List<Media> list) {
		if (mMusicConnect != null) {
			try {
				mMusicConnect.setList(list);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	public List<Media> getList() {
		List<Media> list = new ArrayList<Media>();
		try {
			list = mMusicConnect.getList();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return list;
	}

	public boolean start() {
		if (mMusicConnect != null) {
			try {
				return mMusicConnect.start();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return false;
	}

	public boolean play(int position) {
		if (mMusicConnect != null) {
			try {
				return mMusicConnect.play(position);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return false;

	}

	public boolean pause() {
		if (mMusicConnect != null) {
			try {
				return mMusicConnect.pause();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return false;
	}

	public boolean stop() {
		if (mMusicConnect != null) {
			try {
				return mMusicConnect.stop();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return false;
	}

	public boolean playNext() {
		if (mMusicConnect != null) {
			try {
				return mMusicConnect.playNext();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return false;
	}

	public boolean playPre() {
		if (mMusicConnect != null) {
			try {
				return mMusicConnect.playPre();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return false;
	}

	public boolean seekTo(int rate) {
		if (mMusicConnect != null) {
			try {
				return mMusicConnect.seekTo(rate);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return false;
	}

	public int getCurPosition() {
		if (mMusicConnect != null) {
			try {
				return mMusicConnect.getcurPosition();
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

		return 0;
	}

	public int getDuration() {
		if (mMusicConnect != null) {
			try {
				return mMusicConnect.getDuration();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return 0;
	}

	public int getCurPlayIndex() {
		if (mMusicConnect != null) {
			try {
				return mMusicConnect.getCurPlayIndex();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return 0;
	}

	public int getPlayState() {
		if (mMusicConnect != null) {
			try {
				return mMusicConnect.getPlayState();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return PlayState.NOFILE;
	}

	public void setPlayMode(int mode) {

		if (mMusicConnect != null) {
			try {
				mMusicConnect.setPlayMode(mode);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public int getPlayMode() {
		if (mMusicConnect != null) {
			try {
				return mMusicConnect.getPlayMode();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return PlayMode.LIST_LOOP_PLAY;
	}

}

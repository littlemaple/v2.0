package com.medzone.cloud.acl.thread.audio;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Handler;
import android.os.Message;

public class LocalAudioManager extends BroadcastReceiver {

	private Context mContext;
	private Handler mHandler;
	private boolean isHeadsetPlugged;
	private AudioManager audioManager = null;

	public LocalAudioManager(Context context, Handler handler) {
		Context contx = context;
		this.mContext = contx;
		Handler h = handler;
		this.mHandler = h;
		AudioManager aManager = (AudioManager) (mContext
				.getSystemService(Service.AUDIO_SERVICE));
		this.audioManager = aManager;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		if (intent.hasExtra("state")) {
			// 耳机没有插入
			if (intent.getIntExtra("state", 0) == 0) {
				setHeadsetPlugState(false);
				mSendMessage(0, 0, 0, "请将耳温计插入手机音频口");
			} else if (intent.getIntExtra("state", 0) == 1) {
//				setHeadsetPlugState(true);
//				mSendMessage(1, 0, 0, "耳机已插入");
//				setAudioVolume();
			}
		}
	}

	public synchronized boolean getHeadsetPlugState() {
		boolean b = this.isHeadsetPlugged;
		return b;
	}

	public synchronized void restoreAudioVolume() {
		if (audioManager != null) {
			audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0,
					AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
		}
	}

	private synchronized void setAudioVolume() {
		if (audioManager != null) {
			int max = audioManager
					.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
			int current = audioManager
					.getStreamVolume(AudioManager.STREAM_MUSIC);
			// mSendMessage(Constants.MSG_TOAST, 0, 0, "最大音量:" + max + "\n当前音量:"
			// + current);
			// showToast("最大音量:" + max + "\n当前音量:" + current);
			int target = max - 1;
			if (current < target) {
				audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, target,
						AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
			}
		}
	}

	private synchronized void setHeadsetPlugState(boolean b) {
		boolean bool = b;
		this.isHeadsetPlugged = bool;
	}

	private void mSendMessage(int what, int arg1, int arg2, String str) {
		if (mHandler != null) {
			Message msg = mHandler.obtainMessage();
			msg.what = what;
			msg.arg1 = arg1;
			msg.arg2 = arg2;
			msg.obj = str;
			mHandler.sendMessage(msg);
		}
	}
}

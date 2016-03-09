package com.medzone.cloud.acl.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

import com.medzone.cloud.acl.thread.audio.AcceptDualThread;
import com.medzone.cloud.acl.thread.audio.LocalAudioManager;
import com.medzone.cloud.acl.thread.audio.PostCMDThread;
import com.medzone.framework.data.bean.imp.Transmit;
import com.medzone.framework.util.AudioUtils;

public class AudioClientService extends Service {
	private LocalAudioManager localAudioManager;
	private PostCMDThread postCMDThread;// 发送命令线程
	private AcceptDualThread audioDualThread;// 发送命令线程
	private boolean isInsert = false;
	private int mCommunicateType;
	private boolean isConnOk = false;
	private static final int AUDIO_DETECTION = 11;
	private static final int AUDIO_DETECTION_TIMEOUT = 22;

	@SuppressWarnings("deprecation")
	@Override
	public void onStart(Intent intent, int startId) {
		// controlReceiver的IntentFilter
		IntentFilter controlFilter = new IntentFilter();
		controlFilter.addAction(AudioUtils.ACTION_AUDIO_DETECTION);
		controlFilter.addAction(AudioUtils.AUDIO_ACTION_STOP_SERVICE);
		controlFilter.addAction(AudioUtils.ACTION_AUDIO_CONNECT_DETECTION);
		registerReceiver(controlReceiver, controlFilter);
		// 注册BroadcastReceiver
		localAudioManager = new LocalAudioManager(this, myHandler);
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(AudioUtils.HEADSET_PLUG);
		registerReceiver(localAudioManager, intentFilter);
		super.onStart(intent, startId);
	}

	// 控制信息广播的接收器
	private BroadcastReceiver controlReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (AudioUtils.AUDIO_ACTION_STOP_SERVICE.equals(action)) {
				if (postCMDThread != null) {
					postCMDThread.cancel();
					postCMDThread.release();
					postCMDThread = null;
				}

				if (audioDualThread != null) {
					audioDualThread.release();
					audioDualThread = null;
				}
				stopSelf();
			} else if (AudioUtils.ACTION_AUDIO_CONNECT_DETECTION.equals(action)) {
				if (isInsert) {
					if (isConnOk) {
						mCommunicateType = AudioUtils.COMMUNICATE_TYPE_START;
						start(mCommunicateType);
					} else {
						Transmit bean = new Transmit();
						intent = new Intent(
								AudioUtils.ACTION_AUDIO_CONNECT_FAILURE);
						bean.setMsg("请检查耳温仪是否正确连接或开启！");
						intent.putExtra(AudioUtils.DATA, bean);
						sendBroadcast(intent);
					}
				} else {
					Transmit bean = new Transmit();
					intent = new Intent(AudioUtils.HEADEST_NOT_INSERT);
					bean.setMsg("请插入耳机");
					intent.putExtra(AudioUtils.DATA, bean);
					sendBroadcast(intent);
				}
			} else if (AudioUtils.ACTION_AUDIO_DETECTION.equals(action)) {
				Transmit bean = new Transmit();
				bean = (Transmit) intent.getSerializableExtra(AudioUtils.DATA);
				if (bean != null && bean.getMsg().equals("restart")) {
					audioDetection();
				} else {
					mCommunicateType = AudioUtils.COMMUNICATE_TYPE_QUERY;
					start(mCommunicateType);
				}
			}
		}
	};

	@Override
	public void onDestroy() {
		// 释放一些资源
		if (localAudioManager != null)
			unregisterReceiver(localAudioManager);

		if (controlReceiver != null)
			unregisterReceiver(controlReceiver);
		if (postCMDThread != null) {
			postCMDThread.cancel();
			postCMDThread.release();
			postCMDThread = null;
		}

		if (audioDualThread != null) {
			audioDualThread.release();
			audioDualThread = null;
		}
		super.onDestroy();
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
	}

	private Handler myHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			int what = msg.what;
			String str = (String) msg.obj;
			int arg1 = msg.arg1;
			Transmit bean = new Transmit();
			switch (what) {
			case 0:
				isInsert = false;
				Intent intent = new Intent(AudioUtils.HEADEST_NOT_INSERT);
				bean.setMsg(str);
				intent.putExtra(AudioUtils.DATA, bean);
				sendBroadcast(intent);
				break;
			case 1:
				isInsert = true;
				intent = new Intent(AudioUtils.HEADEST_IS_INSERT);
				bean.setMsg(str);
				intent.putExtra(AudioUtils.DATA, bean);
				sendBroadcast(intent);
				audioDetection();
				break;
			case AudioUtils.MSG_TOAST: {
				break;
			}
			case AudioUtils.MSG_DEBUG: {
				break;
			}
			case AudioUtils.MSG_MESSAGE: {
				switch (arg1) {
				case 0:
					intent = new Intent(AudioUtils.ACTION_COMMON_ERROR);
					bean.setMsg(str);
					intent.putExtra(AudioUtils.DATA, bean);
					sendBroadcast(intent);
					break;
				case 1:
					intent = new Intent(AudioUtils.ACTION_GET_DATA);
					bean.setMsg(str);
					intent.putExtra(AudioUtils.DATA, bean);
					sendBroadcast(intent);
					break;
				case 2:
					isConnOk = true;
					intent = new Intent(AudioUtils.ACTION_AUDIO_CONNECT_SUCCESS);
					bean.setWhat(1);
					bean.setMsg(str);
					intent.putExtra(AudioUtils.DATA, bean);
					sendBroadcast(intent);
					break;
				default:
					break;
				}
				break;
			}
			case AudioUtils.MSG_SEND_CMD_OVER: {
				audioDualThread.start();
				break;
			}
			case AudioUtils.MSG_RECEIVE_DATA: {
				intent = new Intent(AudioUtils.ACTION_COMMON_ERROR);
				bean.setMsg(str);
				intent.putExtra(AudioUtils.DATA, bean);
				sendBroadcast(intent);
				break;
			}

			case AudioUtils.MSG_DECODE_ERROR: {
				bean.setMsg("解码异常！");
				intent = new Intent(AudioUtils.ACTION_COMMON_ERROR);
				intent.putExtra(AudioUtils.DATA, bean);
				sendBroadcast(intent);
				break;
			}
			case AUDIO_DETECTION:
				if (isInsert) {
					intent = new Intent(AudioUtils.ACTION_AUDIO_DETECTION);
					sendBroadcast(intent);
				} else {
					bean = new Transmit();
					intent = new Intent(AudioUtils.HEADEST_NOT_INSERT);
					bean.setMsg("请插入耳机");
					intent.putExtra(AudioUtils.DATA, bean);
					sendBroadcast(intent);
				}
				break;
			case AUDIO_DETECTION_TIMEOUT:
				if (!isConnOk) {
					bean.setMsg("检测超时,耳温仪未正常连接或开启");
					intent = new Intent(AudioUtils.ACTION_AUDIO_CONNECT_FAILURE);
					intent.putExtra(AudioUtils.DATA, bean);
					sendBroadcast(intent);
				}
				break;
			default:
				break;
			}
		}

	};

	private void audioDetection() {
		Message message = myHandler.obtainMessage();
		message.what = AUDIO_DETECTION;
		myHandler.sendMessage(message);

		message = myHandler.obtainMessage();
		message.what = AUDIO_DETECTION_TIMEOUT;
		myHandler.sendMessageDelayed(message, 9000);
	}

	private synchronized void start(int communicatetpye) {
		if (true) {
			if (audioDualThread != null) {
				audioDualThread.release();
				audioDualThread = null;
				try {
					Thread.sleep(200);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			audioDualThread = new AcceptDualThread(myHandler);
			audioDualThread.prepare();
			postCMDThread = new PostCMDThread(myHandler);
			postCMDThread.setCommunicateType(communicatetpye);
			postCMDThread.start();

		}
	}
}

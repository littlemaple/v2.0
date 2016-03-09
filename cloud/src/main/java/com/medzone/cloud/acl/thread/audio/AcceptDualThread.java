package com.medzone.cloud.acl.thread.audio;

import java.lang.reflect.Field;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.medzone.cloud.acl.audio.decode.AudioDecodeResult;
import com.medzone.cloud.acl.audio.decode.AudioDecoder;
import com.medzone.cloud.acl.audio.decode.AudioResultAnalyzer;
import com.medzone.framework.util.AudioUtils;

public class AcceptDualThread implements CommunicateInterface, AcceptInterface {

	private final String TAG = "AcceptDualThread";
	private final boolean D = true;
	private Handler mHandler;
	private AudioRecordInstance audioRecordInstance;
	private HandlerThread acceptDataHandlerThread;
	private HandlerThread handleDataHandlerThread;
	private Handler handleDataHandler;
	private HandleDataRunnable handleDataRunnable = new HandleDataRunnable();
	private AudioDecoder mAudioDecoder = new AudioDecoder();
	private AudioResultAnalyzer mAudioResultAnalyzer = null;
	private boolean isRecording = false;
	private boolean isRunning = false;
	private BlockingQueue<QueueMember> mBlockingQueue = new ArrayBlockingQueue<QueueMember>(
			100);

	public AcceptDualThread(Handler handler) {
		mHandler = handler;
		audioRecordInstance = new AudioRecordInstance(mHandler, mBlockingQueue);
		mAudioResultAnalyzer = new AudioResultAnalyzer(mHandler);
	}

	// --------------------

	private void initHandleDataThread() {
		isRecording = false;
		handleDataHandlerThread = new HandlerThread("handleDataHandlerThread");
		handleDataHandlerThread.start();
		handleDataHandler = new Handler(handleDataHandlerThread.getLooper());
		handleDataHandler.post(handleDataRunnable);
	}

	private void decode(short[] buffer, int length, int bufsize, int rcount) {
		// long time1 = System.currentTimeMillis();
		List<AudioDecodeResult> tmplistAudioDecodeResult = mAudioDecoder
				.doDecode(buffer, length, bufsize, rcount);
		if (tmplistAudioDecodeResult != null) {
			int size = tmplistAudioDecodeResult.size();
			for (int i = 0; i < size; i++) {
				AudioDecodeResult tmpADR = tmplistAudioDecodeResult.get(i);
				if (tmpADR != null) {
					String showliststring = mAudioResultAnalyzer
							.resultListToString(tmpADR);
					sendMessageToUI(AudioUtils.MSG_DEBUG, 0, 0, showliststring);
					mAudioResultAnalyzer.analyze(tmpADR);
					if (mAudioResultAnalyzer.getAssertResult()) {
						mAudioDecoder.stop();
					}
				}
			}
		}
		// long time2 = System.currentTimeMillis();
		// Log.i(TAG, "decode time ==> " + (long) (time2 - time1));
	}

	private final class HandleDataRunnable implements Runnable {
		public void run() {
			Log.i(TAG, "HandleDataRunnable:start");
			isRecording = true;
			while (isRecording) {
				try {
					QueueMember member = mBlockingQueue.take();
					// long time = System.currentTimeMillis();
					// String str = "length:" + member.mlength + " ;size:"
					// + member.MemberBufSize + " ;rcount:"
					// + member.rcount;
					// Log.i(TAG, "HandleDataRunnable==>" + str + " ;time:" +
					// time);
					// --------------------------
					decode(member.member, member.mlength, member.MemberBufSize,
							member.rcount);
					// --------------------------
				} catch (InterruptedException e) {
					e.printStackTrace();
					Log.i(TAG, "HandleDataRunnable ==> interrupted");
				}

			}
		}
	};

	private void sendMessageToUI(int what, int arg1, int arg2, String str) {
		if (mHandler != null) {
			Message msg = mHandler.obtainMessage();
			msg.what = what;
			msg.arg1 = arg1;
			msg.arg2 = arg2;
			msg.obj = str;
			mHandler.sendMessage(msg);
		} else {
			if (D) {
				Log.i(TAG, "sendMessageToUI ==> mHandler is null");
			}

		}
	}

	private void initAcceptDataThread() {
		acceptDataHandlerThread = new HandlerThread("handlerThread");
		acceptDataHandlerThread.start();
		try {
			// -----------------------------
			Field field = audioRecordInstance.getClass().getDeclaredField(
					"audioRecorder");
			Field field2 = field.getType().getDeclaredField(
					"mInitializationLooper");
			field.setAccessible(true);
			field2.setAccessible(true);
			// -----------------
			System.out.println("--handlerThread-lp--"
					+ acceptDataHandlerThread.getLooper());
			System.out.println("--main-lp--" + Looper.getMainLooper());
			// ------------------
			field2.set(field.get(audioRecordInstance),
					acceptDataHandlerThread.getLooper());
			// ------------------------------
			audioRecordInstance.prepare();
			// default: audioRecordInstance.start();
			// ------------------------------
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void configure(boolean threshold, boolean pulse, boolean standard) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setCommunicateType(int type) {
		// TODO Auto-generated method stub

	}

	@Override
	public void start() {
		// TODO Auto-generated method stub
		audioRecordInstance.start();
		isRunning = true;
	}

	@Override
	public void prepare() {
		// TODO Auto-generated method stub
		if (audioRecordInstance.audioRecordDestroyed()) {
			audioRecordInstance = new AudioRecordInstance(mHandler,
					mBlockingQueue);
		}
		initAcceptDataThread();
		// ----------------------
		initHandleDataThread();

	}

	@Override
	public void stop() {
		// TODO Auto-generated method stub
		if (audioRecordInstance != null) {
			audioRecordInstance.stop();
		}
		if (acceptDataHandlerThread != null) {
			isRecording = false;
			acceptDataHandlerThread.interrupt();
			acceptDataHandlerThread.getLooper().quit();
			acceptDataHandlerThread = null;
		}
		if (handleDataHandlerThread != null) {
			handleDataHandlerThread.interrupt();
			handleDataHandlerThread.getLooper().quit();
			handleDataHandlerThread = null;
		}
		isRunning = false;
	}

	@Override
	public void release() {
		// TODO Auto-generated method stub
		stop();
		if (audioRecordInstance != null) {
			audioRecordInstance.release();
		}
	}

	@Override
	public boolean isrunning() {
		// TODO Auto-generated method stub
		return isRunning;
	}

}
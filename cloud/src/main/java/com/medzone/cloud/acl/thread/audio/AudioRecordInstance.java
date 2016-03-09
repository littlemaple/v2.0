package com.medzone.cloud.acl.thread.audio;

import java.util.concurrent.BlockingQueue;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Handler;
import android.util.Log;

public class AudioRecordInstance {
	private final String TAG = "AudioRecordInstance";
	private final boolean D = true;
	private int frequence = 44100;
	private int bSamples = 16;
	private int nChannels = 1;
	private int recordMinBufSize = 4096;
	private int recordCount = 0;
	private int framePeriod = 4096;
	private int maxReocrdCount = 300;
	private int minListenerInteval = 0;
	private long lasttime = 0;
	private boolean isRecording = false;
	private boolean audiorecordDestroyed = true;
	private AudioRecord audioRecorder;
	private BlockingQueue<QueueMember> mBlockingQueue;
	private MyQueueMember myQueueMember;
	private short[] audioData;
	private State state;
	//private Handler mHandler;

	public enum State {
		INITIALIZING, READY, RECORDING, PAUSED, ERROR, STOPPED
	};

	protected class MyQueueMember extends QueueMember {
		public MyQueueMember(int memberlength) {
			super.setMemberLength(memberlength);
		}

		public boolean setMember(short[] m, int l, int r) {
			super.member = m;
			super.mlength = l;
			super.rcount = r;
			return true;
		}
	}

	public State getState() {
		return state;
	}

	private synchronized void setState(State s) {
		state = s;
		if (D)
			Log.e(TAG, "state==>" + state);
	}

	public AudioRecordInstance(Handler handler, BlockingQueue<QueueMember> queue) {
		//mHandler = handler;
		mBlockingQueue = queue;
		setState(State.STOPPED);
		initAudioRecord();
		myQueueMember = new MyQueueMember(recordMinBufSize);
	}

	private void initAudioRecord() {
		AudioRecord record = null;
		int audioFormat = AudioFormat.CHANNEL_IN_MONO;
		int audioEncoding = AudioFormat.ENCODING_PCM_16BIT;
		int bufferSize;
		int mAudioBufferSize;
		try {
			recordMinBufSize = AudioRecord.getMinBufferSize(frequence,
					audioFormat, audioEncoding);
			Log.e(TAG, "--------------recordMinBufSize==>" + recordMinBufSize);
			// ---------------非常重要----------------
			int tmpSize = recordMinBufSize;
			if (tmpSize < 2048) {
				int tmpmultiple = 7680 / recordMinBufSize;
				int multiple = ((tmpmultiple % 2) == 0) ? tmpmultiple
						: (tmpmultiple + 1);
				recordMinBufSize = multiple * tmpSize;
			} else if ((tmpSize >= 2048) && (tmpSize < 7680)) {
				recordMinBufSize = 7680;
			}
			Log.e(TAG, "--------new------recordMinBufSize==>"
					+ recordMinBufSize);
			// ----------------------------------
			minListenerInteval = 400 * recordMinBufSize / frequence;// default:1000*ans;
			// ----------------------------------
			mAudioBufferSize = 4 * recordMinBufSize;
			record = new AudioRecord(MediaRecorder.AudioSource.MIC, frequence,
					audioFormat, audioEncoding, mAudioBufferSize);
			audioRecorder = record;
			audiorecordDestroyed = false;
			// -----------------------------------
			maxReocrdCount = 441000 / recordMinBufSize;// 最多录10s数据
			bufferSize = recordMinBufSize;
			audioData = new short[bufferSize];
			framePeriod = bufferSize / (2 * bSamples * nChannels / 8);
			if (audioRecorder.getState() != AudioRecord.STATE_INITIALIZED) {
				throw new Exception("AudioRecord initialization failed");
			}
			setState(State.INITIALIZING);
		} catch (IllegalArgumentException e) {
			// mSendMessage(Constants.MSG_TOAST, 0, 0, "初始化record失败");
			setState(State.ERROR);
			Log.e(TAG, "111 State.ERROR");
			e.printStackTrace();
		} catch (Exception e) {
			setState(State.ERROR);
			e.printStackTrace();
			Log.e(TAG, "AudioRecord initialization failed");
		}
	}

	private AudioRecord.OnRecordPositionUpdateListener updateListener = new AudioRecord.OnRecordPositionUpdateListener() {
		@Override
		public void onPeriodicNotification(AudioRecord recorder) {
			// TODO Auto-generated method stub
			if (isRecording && (recordCount <= maxReocrdCount)) {
				try {
					int audioDataSize = audioRecorder.read(audioData, 0,
							audioData.length);
					// ------------------------
					long time = System.currentTimeMillis();
					long deltatime = time - lasttime;
					if (deltatime > minListenerInteval) {
						lasttime = time;
						recordCount++;
						// ----------------------------------------
						short[] decodeBuf = new short[audioDataSize];
						System.arraycopy(audioData, 0, decodeBuf, 0,
								audioDataSize);
						myQueueMember.setMember(decodeBuf, audioDataSize,
								recordCount);
						mBlockingQueue.put(myQueueMember);
						// ----------------------------------------
					}
					Log.e(TAG, "audioDataSize: " + audioDataSize
							+ " ;recordCount: " + recordCount + " ;read time: "
							+ time + " ;Δt: " + deltatime);

				} catch (InterruptedException e) {
					Log.e(TAG, "BlockingQueue full and bolck");
					e.printStackTrace();
				}

			}

		}

		@Override
		public void onMarkerReached(AudioRecord recorder) {
			// TODO Auto-generated method stub
			if (D)
				Log.e(TAG, "onMarkerReached called");
		}
	};

	// private void mSendMessage(int what, int arg1, int arg2, String str) {
	// if (mHandler != null) {
	// Message msg = mHandler.obtainMessage();
	// msg.what = what;
	// msg.arg1 = arg1;
	// msg.arg2 = arg2;
	// msg.obj = str;
	// mHandler.sendMessage(msg);
	// }
	// }

	public void prepare() {
		if (state == State.INITIALIZING) {
			if (audioRecorder.getState() == AudioRecord.STATE_INITIALIZED) {
				// -------------------------
				int setPNP = audioRecorder
						.setPositionNotificationPeriod(framePeriod);
				Log.e(TAG, "setPNP return:" + setPNP);// 0:success, -3:error
				// --------------------------
				audioRecorder.setRecordPositionUpdateListener(updateListener);
				setState(State.READY);
				audioRecorder.startRecording();
			}
		} else if (state == State.PAUSED) {
			audioRecorder.setRecordPositionUpdateListener(updateListener);
			setState(State.READY);
			audioRecorder.startRecording();
		}
		// if (state == State.INITIALIZING) {
		// if (audioRecorder.getState() == AudioRecord.STATE_INITIALIZED) {
		// audioRecorder.setPositionNotificationPeriod(framePeriod);
		// audioRecorder.setRecordPositionUpdateListener(updateListener);
		// setState(State.READY);
		// audioRecorder.startRecording();
		// } else {
		// System.out.println("Record not INITIALIZED");
		// }
		// } else {
		// Log.e(TAG, "prepare()==>cannot prepare; state==>" + state);
		// audioRecorder.setRecordPositionUpdateListener(updateListener);
		// setState(State.READY);
		// }
	}

	public void start() {
		if (state == State.READY) {
			if (audioRecorder.getRecordingState() == AudioRecord.RECORDSTATE_RECORDING) {
				recordCount = 0;
				isRecording = true;
				audioRecorder.read(audioData, 0, audioData.length);
				setState(State.RECORDING);
			} else {
				Log.e(TAG, " audiorecord recordingstate is not recording");
				audioRecorder.startRecording();
				recordCount = 0;
				isRecording = true;
				audioRecorder.setPositionNotificationPeriod(framePeriod);
				audioRecorder.setRecordPositionUpdateListener(updateListener);
				audioRecorder.read(audioData, 0, audioData.length);
				setState(State.RECORDING);
			}
		} else {
			// ERR
			// setState(State.ERROR);
			Log.e(TAG, "start() State.ERROR");
		}
	}

	public void reset() {
		if (state != State.ERROR) {
			release();
			initAudioRecord();
		}
	}

	public void pause() {
		if (state == State.RECORDING) {
			isRecording = false;
			audioRecorder.setRecordPositionUpdateListener(null);// ??????
			audioRecorder.stop();
		}
	}

	public void stop() {
		if (state == State.RECORDING) {
			isRecording = false;
			audioRecorder.setRecordPositionUpdateListener(null);// ??????
			audioRecorder.stop();
			setState(State.STOPPED);
		}
	}

	// --------------调用顺序：先stop，再release----------------
	public void release() {
		if (state == State.RECORDING) {
			stop();
		}
		if (audioRecorder != null) {
			audioRecorder.release();
			audioRecorder = null;
			audiorecordDestroyed = true;
		}
	}

	public boolean audioRecordDestroyed() {
		return audiorecordDestroyed;
	}

}

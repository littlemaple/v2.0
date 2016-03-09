/**
 * 
 */
package com.medzone.cloud.acl.thread.audio;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.medzone.framework.util.AudioUtils;

public class PostCMDThread extends Thread implements CommunicateInterface {

	private final boolean D = false;
	private final String TAG = "PostCMDThread";
	private boolean isPlaying = false;
	private int audioTrackMinBufferSize;
	private int mFrequence = 44100;
	private AudioTrack mTrack;
	private final int SAMPLESPERBIT = 32;
	private Handler mHandler;
	private int mCommunicateType;
	private int sendCmdHex[] = new int[AudioUtils.MEASURE_CMD_QUERY_ID.length];

	public PostCMDThread(Handler handler) {
		Handler h = handler;
		this.mHandler = h;
		setupAudioTrack();
	}

	private void setupAudioTrack() {
		AudioTrack track = null;
		int frequence = this.mFrequence;
		int audioFormat = AudioFormat.CHANNEL_OUT_MONO;
		int audioEncoding = AudioFormat.ENCODING_PCM_16BIT;
		int audioTrackBufferSize;
		int audioTrackPlayBufferSize;
		try {
			audioTrackBufferSize = AudioTrack.getMinBufferSize(frequence,
					audioFormat, audioEncoding);
			this.audioTrackMinBufferSize = audioTrackBufferSize;
			audioTrackPlayBufferSize = audioTrackBufferSize;
			track = new AudioTrack(AudioManager.STREAM_MUSIC, frequence,
					audioFormat, audioEncoding, audioTrackPlayBufferSize,
					AudioTrack.MODE_STREAM);
			this.mTrack = track;
			if (D)
				Log.i(TAG, "PostCMDThread audioTrackBufferSize:==>"
						+ audioTrackBufferSize);
		} catch (IllegalArgumentException e) {
			mSendMessage(AudioUtils.MSG_TOAST, 0, 0, "初始化track失败");
			e.printStackTrace();
		}
	}

	private void mSendMessage(int what, int arg1, int arg2, String str) {
		if (mHandler != null) {
			Message msg = mHandler.obtainMessage();
			msg.what = what;
			msg.arg1 = arg1;
			msg.arg2 = arg2;
			msg.obj = str;
			mHandler.sendMessage(msg);
		} else {
			if (D)
				Log.i(TAG,
						"PostCMDThread send message failed: --> mHandler == null");
		}
	}

	private void getSendCmdHex() {
		switch (mCommunicateType) {
		case AudioUtils.COMMUNICATE_TYPE_QUERY: {
			System.arraycopy(AudioUtils.MEASURE_CMD_QUERY_ID, 0, sendCmdHex, 0,
					AudioUtils.MEASURE_CMD_QUERY_ID.length);
			break;
		}
		case AudioUtils.COMMUNICATE_TYPE_START: {
			System.arraycopy(AudioUtils.MEASURE_CMD_START, 0, sendCmdHex, 0,
					AudioUtils.MEASURE_CMD_START.length);
			break;
		}
		case AudioUtils.COMMUNICATE_TYPE_ADJUST: {
			System.arraycopy(AudioUtils.MEASURE_CMD_ADJUST, 0, sendCmdHex, 0,
					AudioUtils.MEASURE_CMD_ADJUST.length);
			break;
		}
		default:
			break;
		}
	}

	public void run() {

		isPlaying = true;
		// *9的原因是因为位上 预留的编码11111->111110这样的多一位编码；
		// SAMPLESPERBIT是采样点，表示一个波形，采样点数为32；
		// +1 是指 '\0'结束符；
		int bufferSize1 = sendCmdHex.length * 9 * SAMPLESPERBIT
				/ audioTrackMinBufferSize + 1;
		short[] bufferTmp = new short[bufferSize1 * audioTrackMinBufferSize];
		short[] bufferToPlay = new short[audioTrackMinBufferSize];
		// int k = 0;
		int highCount = 0;
		int allbitcount = 0;
		char bitChar = '0';
		char lastbitChar = '0';

		int bitcount = 8;
		getSendCmdHex();

		for (int k = 0; k < sendCmdHex.length; k++) {

			char[] reallySendByte;
			char[] sendByte = { '0', '0', '0', '0', '0', '0', '0', '0', '0',
					'0' };
			char[] tmpSendByte = { '0', '0', '0', '0', '0', '0', '0', '0', '0',
					'0' };
			// 将每个字节转化成二进制；
			reallySendByte = Integer.toBinaryString(sendCmdHex[k])
					.toCharArray();
			for (int bit = 0; bit < reallySendByte.length; bit++) {
				sendByte[8 - reallySendByte.length + bit] = reallySendByte[bit];
			}

			// String str = "";
			// for (int i = 0; i < sendByte.length; i++) {
			// str += sendByte[i];
			// }
			// Log.d(TAG, "sendByte==>" + str);

			// 去掉帧头 帧尾 即完整的一帧,对这一帧进行编码，将连续的5位1编码成111110
			/**
			 * 因为我们的帧头是01111110 <br/>
			 * 我们是通过识别01111110判断是否是一帧的开始<br/>
			 * 如果数据段出现01111110<br/>
			 * 我们就会误判所以必须对连续五个1加个0<br/>
			 * */
			if ((k > 6) && (k < sendCmdHex.length - 1)) {

				for (int bit = 0; bit < bitcount; bit++) {
					if (sendByte[bit] == '1')
						highCount++;
					else
						highCount = 0;

					if (highCount == 5) {
						bitcount++;
						highCount = 0;
						// 意思就是从 连续位的后一位到8位结束开始做备份；
						// 因为正常只有8位，但是我们开辟了10为的空间，所以要扣掉2位，从8位内开始计算；
						System.arraycopy(sendByte, bit + 1, tmpSendByte, 0,
								sendByte.length - bit - 2);
						// 后一位
						sendByte[bit + 1] = '0';
						// 从备份的位上开始还原
						System.arraycopy(tmpSendByte, 0, sendByte, bit + 2,
								sendByte.length - bit - 2);
					}
				}
			}

			// 对二进制流进行曼彻斯特编码
			for (int pos = 0; pos < bitcount; pos++) {
				bitChar = sendByte[pos];
				if (k == 0 && pos == 0) {
					lastbitChar = sendByte[pos];
				}

				// 采样率，不采样怎么判断是否跳变了呢？
				for (int j = 0; j < SAMPLESPERBIT / 2; j++) {

					if (sendByte[pos] == '1') {

						// lastbit_char是上一个bit，并不是最后一个bit
						if (bitChar != lastbitChar) {
							bufferTmp[allbitcount + (pos - 1) * SAMPLESPERBIT
									+ j + 16] = (short) (-32755 * Math
									.sin(Math.PI / 32 * j));
							bufferTmp[allbitcount + pos * SAMPLESPERBIT + j] = (short) (-32755 * Math
									.sin(Math.PI / 32 * j + Math.PI / 2));
							bufferTmp[allbitcount + pos * SAMPLESPERBIT + j
									+ 16] = (short) (-32755 * Math.sin(Math.PI
									/ 16 * j + Math.PI));
						} else {
							// 如果有跳变，01 -> 高位跳变；通过跳变来判断信息；
							bufferTmp[allbitcount + pos * SAMPLESPERBIT + j] = (short) (-32755 * Math
									.sin(Math.PI / 16 * j));
							bufferTmp[allbitcount + pos * SAMPLESPERBIT + j
									+ 16] = (short) (-32755 * Math.sin(Math.PI
									/ 16 * j + Math.PI));
						}

					} else {

						if (bitChar != lastbitChar) {
							bufferTmp[allbitcount + (pos - 1) * SAMPLESPERBIT
									+ j + 16] = (short) (-32755 * Math
									.sin(Math.PI / 32 * j + Math.PI));
							bufferTmp[allbitcount + pos * SAMPLESPERBIT + j] = (short) (-32755 * Math
									.sin(Math.PI / 32 * j + Math.PI / 2
											+ Math.PI));
							bufferTmp[allbitcount + pos * SAMPLESPERBIT + j
									+ 16] = (short) (-32755 * Math.sin(Math.PI
									/ 16 * j));
						} else {

							// 如果有跳变，10 -> 低位跳变；通过跳变来判断信息；
							bufferTmp[allbitcount + pos * SAMPLESPERBIT + j] = (short) (-32755 * Math
									.sin(Math.PI / 16 * j + Math.PI));
							bufferTmp[allbitcount + pos * SAMPLESPERBIT + j
									+ 16] = (short) (-32755 * Math.sin(Math.PI
									/ 16 * j));
						}
					}
				}
				lastbitChar = bitChar;
			}
			allbitcount = allbitcount + (bitcount * SAMPLESPERBIT);
		}

		try {
			if (mTrack.getState() == AudioTrack.STATE_INITIALIZED) {
				// 开始播放
				mTrack.play();
				playTrackClip();

				int i = 0;
				while (isPlaying && i < bufferSize1) {
					System.arraycopy(bufferTmp, i * audioTrackMinBufferSize,
							bufferToPlay, 0, audioTrackMinBufferSize);
					i++;
					mTrack.write(bufferToPlay, 0, bufferToPlay.length);
				}

				String cmdStr = "";
				for (int j = 7; j < (17 + sendCmdHex[15]); j++) {
					cmdStr += Integer.toHexString(
							(sendCmdHex[j] & 0x000000FF) | 0xFFFFFF00)
							.substring(6)
							+ ",";
				}
				cmdStr += Integer
						.toHexString(
								(sendCmdHex[18 + sendCmdHex[15]] & 0x000000FF) | 0xFFFFFF00)
						.substring(6);
				mSendMessage(AudioUtils.MSG_SEND_CMD_OVER, 0, 0, cmdStr);

				playTrackClip();
				// 发送完毕
				isPlaying = false;
				// 播放结束
				// track.stop();
			} else {
				Log.e(TAG, "AudioTrack is not initialized!");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void playTrackClip() {
		// 先发送一段声音，有些手机一开始发送不对 需要一段时间后发送才正常所以在发每一个指令前先发一段无关的
		// 这段声音是无任何实际效果的，是一段正弦波；
		if (mTrack.getPlayState() == AudioTrack.PLAYSTATE_PLAYING) {
			short[] buffer = new short[audioTrackMinBufferSize];
			short[] buffer1 = new short[audioTrackMinBufferSize];
			for (int i = 0; i < audioTrackMinBufferSize; i++) {
				buffer1[i] = (short) (-1000 * Math.sin(Math.PI / 32 * i
						+ Math.PI));
			}
			System.arraycopy(buffer1, 0, buffer, 0, audioTrackMinBufferSize);
			for (int j = 0; j < 3; j++) {
				mTrack.write(buffer, 0, buffer.length);
			}
		} else {
			if (D)
				Log.i(TAG, "AudioTrack is not playing!");
		}

	}

	public void cancel() {
		if (mTrack != null) {
			if (mTrack.getPlayState() == AudioTrack.PLAYSTATE_PLAYING) {
				mTrack.stop();
			}
		}
	}

	public void release() {
		if (mTrack != null) {
			mTrack.release();
			mTrack = null;
		}
	}

	public void configureFreq(int frequence) {
		// TODO Auto-generated method stub
		mFrequence = frequence;
	}

	@Override
	public void setCommunicateType(int type) {
		// TODO Auto-generated method stub
		mCommunicateType = type;
	}

	@Override
	public void configure(boolean threshold, boolean pulse, boolean standard) {
		// TODO Auto-generated method stub

	}

}

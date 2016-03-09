package com.medzone.cloud.acl.thread.bluetooth;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;

@SuppressLint("NewApi")
public class BluetoothCommun {
	private Handler mHandler;

	public BluetoothCommun(Handler handler) {
		this.mHandler = handler;
	}

	public void mSendMessage(int what, int arg1, int arg2, String str) {
		if (mHandler != null) {
			Message msg = mHandler.obtainMessage();
			msg.what = what;
			msg.arg1 = arg1;
			msg.arg2 = arg2;
			msg.obj = str;
			mHandler.sendMessage(msg);
		}
	}

	// 确定帧头位置；
	public short decodeIdentFrameHead(short[] buffer, int n) {

		short counter = 0;
		short frameHeadPos = 0;
		for (frameHeadPos = 0; frameHeadPos < n; frameHeadPos++) {
			if (buffer[frameHeadPos] == 0xAA) {
				// 170
				counter++;
				continue;
			} else if (counter >= 7 && buffer[frameHeadPos] == 0xAB) {
				// 171
				return frameHeadPos;
			} else {
				counter = 0;
			}
		}
		return frameHeadPos;
	}

	// 整形数组强制转换成字节
	public byte[] intArrayToByte(int[] array) {
		int i = 0;
		byte[] bos = new byte[12];
		for (i = 0; i < 12; i++) {
			bos[i] = (byte) array[i];
		}
		return bos;
	}

	// 将有符号数转化为无符号数(unsigned)；
	public short signedConvertToUnsigned(byte inbyte) {
		short outbyte = inbyte;
		if (inbyte < 0)
			outbyte += (short) Math.pow(2, 8);
		return outbyte;
	}
}

package com.medzone.cloud.acl.thread.bluetooth;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;

public class BluetoothBLEInputStream extends InputStream{
	private int 						MAX_LEN = 256;
	private byte[]                      mReceived = new byte[MAX_LEN];
	private ArrayList<byte[]>			mFrames = new ArrayList<byte[]>();

	BluetoothBLEInputStream()
	{
	}
	
	public void injectData(byte[] data)
	{
		int remain = data.length;
		while (remain > 0){
			if (remain > MAX_LEN){
				byte[] sub = new byte[MAX_LEN];
				System.arraycopy(data, data.length - remain, sub, 0, MAX_LEN);
				mFrames.add(sub);
				remain -= MAX_LEN;
			}
			else{
				byte[] sub = new byte[remain];
				System.arraycopy(data, data.length - remain, sub, 0, remain);
				mFrames.add(sub);
				remain = 0;
			}
		}
	}
	
	
	@Override
	public void close(){
	}

	@Override
	public int read() throws IOException {
		return 0; 
	}


	@Override
	public int available() throws IOException {
		int avaliable = 0;
		if (mFrames.size() != 0){
			for (byte[] contents: mFrames){
				avaliable += contents.length;
			}
		}

		return avaliable;
	}


	@Override
	public int read(byte[] buffer) throws IOException {
		return read(buffer, 0, buffer.length);//return super.read(buffer);
	}


	@Override
	public int read(byte[] buffer, int byteOffset, int byteCount)
			throws IOException {
		if (mFrames.size() == 0)
			return 0;
		
		int readCount = 0;
		int size = mFrames.size();
		for (int i = 0; i < size; i++) {
			byte[] content = mFrames.get(0);
			int len = Math.min(byteCount - readCount, content.length);
			System.arraycopy(content, 0, buffer, byteOffset + readCount, len);
			readCount += len;
			if (len == content.length)
				mFrames.remove(0);
			else {
				byte[] left = new byte[content.length - len];
				System.arraycopy(content, len, left, 0, content.length - len);
				mFrames.set(0, left);
				break;
			}
		}

		
		return readCount;
	}


	@Override
	public synchronized void reset() throws IOException {
		Arrays.fill(mReceived,(byte) 0);
		mFrames.clear();
		super.reset();
	}
	
	
}

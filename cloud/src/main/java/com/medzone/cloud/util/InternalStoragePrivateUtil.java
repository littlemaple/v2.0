package com.medzone.cloud.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.content.Context;

public class InternalStoragePrivateUtil {
	private Activity activity;

	public InternalStoragePrivateUtil(Activity activity) {
		this.activity = activity;
	}

	/*
	 * Writes content to internal storage making the content private to the
	 * application. The method can be easily changed to take the MODE as
	 * argument and let the caller dictate the visibility: MODE_PRIVATE,
	 * MODE_WORLD_WRITEABLE, MODE_WORLD_READABLE, etc.
	 * 
	 * @param filename - the name of the file to create
	 * 
	 * @param content - the content to write
	 */
	public void writeInternalStoragePrivate(String filename, byte[] content) {
		try {
			// MODE_PRIVATE creates/replaces a file and makes
			// it private to your application. Other modes:
			// MODE_WORLD_WRITEABLE
			// MODE_WORLD_READABLE
			// MODE_APPEND
			FileOutputStream fos = activity.openFileOutput(filename,
					Context.MODE_PRIVATE);
			fos.write(content); // 将content数组的内容写到filename指定的文件中。
			fos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// 从内部私有存储器读取数据；注意 openFileInput() 的使用。
	/**
	 * Reads a file from internal storage
	 * 
	 * @param filename
	 *            the file to read from
	 * @return the file content
	 */
	public byte[] readInternalStoragePrivate(String filename) {
		int len = 1024;
		byte[] buffer = new byte[len];
		try {
			FileInputStream fis = activity.openFileInput(filename);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			int nrb = fis.read(buffer, 0, len); // read up to len bytes
			while (nrb != -1) {
				baos.write(buffer, 0, nrb);
				nrb = fis.read(buffer, 0, len);
			}
			buffer = baos.toByteArray();
			fis.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return buffer;
	}

	/**
	 * Delete internal private file
	 * 
	 * @param filename
	 *            - the filename to delete
	 */
	public void deleteInternalStoragePrivate(String filename) {
		File file = activity.getFileStreamPath(filename);
		if (file != null) {
			file.delete();
		}
	}
}

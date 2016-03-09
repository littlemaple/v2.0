package com.medzone.cloud.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.os.Environment;

public class ExternalStoragePublicUtil {
	private Activity activity;

	public ExternalStoragePublicUtil(Activity activity) {
		this.activity = activity;
	}

	/**
	 * Helper Method to Test if external Storage is Available
	 */
	public boolean isExternalStorageAvailable() {
		boolean state = false;
		String extStorageState = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(extStorageState)) {
			state = true;
		}
		return state;
	}

	/**
	 * Helper Method to Test if external Storage is read only
	 */
	public boolean isExternalStorageReadOnly() {
		boolean state = false;
		String extStorageState = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(extStorageState)) {
			state = true;
		}
		return state;
	}

	/**
	 * Write to external public directory
	 * 
	 * @param filename
	 *            - the filename to write to
	 * @param content
	 *            - the content to write
	 */
	public void writeToExternalStoragePublic(String filename, byte[] content) {

		// API Level 7 or lower, use getExternalStorageDirectory()
		// to open a File that represents the root of the external
		// storage, but writing to root is not recommended, and instead
		// application should write to application-specific directory, as shown
		// below.

		String packageName = activity.getPackageName();
		String path = "/Android/data/" + packageName + "/files/";
		if (isExternalStorageAvailable() && !isExternalStorageReadOnly()) {
			try {
				File file = new File(path, filename);
				if (!file.exists()) {
					file.mkdirs();
				} else {
					return;
				}
				FileOutputStream fos = new FileOutputStream(file);
				fos.write(content);
				fos.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Reads a file from internal storage
	 * 
	 * @param filename
	 *            - the filename to read from
	 * @return the file contents
	 */
	public byte[] readExternallStoragePublic(String filename) {
		int len = 1024;
		byte[] buffer = new byte[len];
		String packageName = activity.getPackageName();
		String path = "/Android/data/" + packageName + "/files/";
		if (!isExternalStorageReadOnly()) {
			try {
				File file = new File(path, filename);
				FileInputStream fis = new FileInputStream(file);
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
		}
		return buffer;
	}

	/**
	 * Delete external public file
	 * 
	 * @param filename
	 *            - the filename to write to
	 */
	public void deleteExternalStoragePublicFile(String filename) {
		String packageName = activity.getPackageName();
		String path = "/Android/data/" + packageName + "/files/" + filename;
		File file = new File(path, filename);
		if (file != null) {
			file.delete();
		}
	}
}

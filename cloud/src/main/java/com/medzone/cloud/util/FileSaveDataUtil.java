package com.medzone.cloud.util;

import android.app.Activity;
import android.os.Environment;

import com.medzone.framework.util.ByteOrStringUtil;

public class FileSaveDataUtil {

	private ExternalStoragePublicUtil externalStoragePublic;
	private InternalStoragePrivateUtil internalStoragePrivate;
	private boolean existSDcard = false;
	private String FILE_NAME = "bloodoxygen.txt";

	public FileSaveDataUtil(Activity activity) {
		externalStoragePublic = new ExternalStoragePublicUtil(activity);
		internalStoragePrivate = new InternalStoragePrivateUtil(activity);
		existSDcard = existSDcard();
	}

	/**
	 * 判断存储卡是否存在
	 */
	private boolean existSDcard() {
		String STATE = Environment.getExternalStorageState();
		return Environment.MEDIA_MOUNTED.equals(STATE);
	}

	public String readData() {
		byte[] content;
		// if (existSDcard) {
		// content = externalStoragePublic
		// .readExternallStoragePublic(FILE_NAME);
		// } else {
		content = internalStoragePrivate.readInternalStoragePrivate(FILE_NAME);
		// }
		return ByteOrStringUtil.ByteToString(content);
	}

	public void writeData(String str) {
		byte[] content = ByteOrStringUtil.StringToByte(str);
		// if (existSDcard) {
		// externalStoragePublic.writeToExternalStoragePublic(FILE_NAME,
		// content);
		// } else {
		internalStoragePrivate.writeInternalStoragePrivate(FILE_NAME, content);
		// }
	}

	public void deleteDataFile() {
		if (existSDcard) {
			externalStoragePublic.deleteExternalStoragePublicFile(FILE_NAME);
		} else {
			internalStoragePrivate.deleteInternalStoragePrivate(FILE_NAME);
		}
	}

}

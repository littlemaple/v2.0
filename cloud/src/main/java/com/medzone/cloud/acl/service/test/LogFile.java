package com.medzone.cloud.acl.service.test;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;

public class LogFile{
	public static final String ECG_FILE_PATH = android.os.Environment.getExternalStorageDirectory() +"/bt_performance.txt";
	private File mFile = null;
	private PrintWriter mFileWriter = null;

	public void openFile(String filePathAndName) {
		if (mFileWriter != null)
			return;
		
		try {
			String filePath = filePathAndName;
			mFile = new File(filePath);
			if (!mFile.exists()) {
				mFile.createNewFile();
				System.out.println("create new file");
			}
			FileWriter resultFile = new FileWriter(mFile);
			mFileWriter = new PrintWriter(resultFile);
		} catch (Exception e) {
			System.out.println("open file failed");
			e.printStackTrace();
		}
	}

	public void writeFile(String content) {
		if (mFileWriter == null) {
			openFile(ECG_FILE_PATH);
			if (mFileWriter == null)
				return;
		}
		mFileWriter.println(content);
		mFileWriter.flush();
	}
}

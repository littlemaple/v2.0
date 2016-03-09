package com.medzone.cloud.acl.service.test;


public class Performance {
	public static long tsScanStart = 0;
	public static long tsScanFound = 0;
	public static long tsConnectStart = 0;
	public static long tsConnectSucceed = 0;
	public static long tsQueryReceived  = 0;
	public static long tsScanFailed = 0;
	public static long tsConnectedFailed = 0;
	public static long tsQueryFailed = 0;
	public static LogFile mLogger = new LogFile();
	public static String from;
	public static String to;
	public static int  totalTimes;
	public static int  succeedTimes;
	
	
	public static void init()
	{
		tsScanStart = 0;
		tsScanFound = 0;
		tsConnectStart = 0;
		tsConnectSucceed = 0;
		tsQueryReceived  = 0;
		tsScanFailed = 0;
		tsConnectedFailed = 0;
		tsQueryFailed = 0;
		from = null;
		to = null;
		
		mLogger.openFile(LogFile.ECG_FILE_PATH);

	}
	
	public static void closeActivity()
	{

	}
	
	public static void save()
	{
		StringBuffer buffer = new StringBuffer();
		
		if (from == null)
			from = "";
		
		if (to == null)
			to = "";
		
		float rate = succeedTimes*100.0f / totalTimes;
		buffer.append(rate + "%");
		buffer.append('\t');
		
		totalTimes ++;
		
		buffer.append(from);
		buffer.append('\t');
		buffer.append(to);
		buffer.append('\t');
		
		
		if (tsScanFailed > 0){
			buffer.append(tsScanFailed - tsScanStart);
			buffer.append('\t');
			mLogger.writeFile(buffer.toString());
			return;
		}
		
		buffer.append(tsScanFound - tsScanStart);
		buffer.append('\t');
		
		if (tsConnectedFailed > 0)
		{
			buffer.append(tsConnectedFailed - tsConnectStart);
			buffer.append('\t');
			mLogger.writeFile(buffer.toString());
			return;
		}
		
		succeedTimes++;
		
		buffer.append(tsConnectSucceed - tsConnectStart);
		buffer.append('\t');
		
		buffer.append(tsConnectSucceed - tsScanStart);
		buffer.append('\t');
		
//		if (tsQueryFailed > 0)
//		{
//			buffer.append(tsQueryFailed - tsScanStart);
//			buffer.append('\t');
//			mLogger.writeFile(buffer.toString());
//			return;
//		}
//		
//		
//		
//		buffer.append(tsQueryReceived - tsConnectSucceed);
//		buffer.append('\t');
//		
//		buffer.append(tsQueryReceived - tsScanStart);
//		buffer.append('\t');

		mLogger.writeFile(buffer.toString());
			
	}
}

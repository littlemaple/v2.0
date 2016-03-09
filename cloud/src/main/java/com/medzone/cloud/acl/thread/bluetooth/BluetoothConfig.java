package com.medzone.cloud.acl.thread.bluetooth;

import java.util.HashMap;
import java.util.Map;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import com.medzone.cloud.acl.service.test.Performance;
import com.medzone.framework.Log;


public class BluetoothConfig {
	private static final String TAG = "BluetoothConfig";
	public static boolean mbUnsafeMode      = false;       // 0x01
	public static boolean mbUseFixedChannel = false;       // 0x02
	public static boolean mbNeedSleep       = false;       // 0x04
	public static boolean mbCanClose        = false;       // 0x08
	public static boolean mbUseBLE          = false;       // BLE not support
	public static int 	  mWaitingTime;
	public static Map<String, Integer> mRulesMap = new HashMap<String, Integer>();

	static {
		// 华为
		mRulesMap.put("C8812",     0x02);
		mRulesMap.put("C8813",     0x05);
		mRulesMap.put("C8815",     0x05);
		mRulesMap.put("G610",      0x02);
		
		
		// 三星
		mRulesMap.put("GT-I9100",  0x05);
		mRulesMap.put("GT-I9500",  0x03);
		mRulesMap.put("SHV-E330K", 0x04);
		mRulesMap.put("SHV-E330S", 0x04);
		mRulesMap.put("SHV-E330L", 0x04);
		mRulesMap.put("SM-G900F",  0x04);
		
		// HTC
		mRulesMap.put("HTC D816w", 0x15);

		// MI
		mRulesMap.put("MI 2",       0x05);
		mRulesMap.put("MI 2A",      0x05);
		mRulesMap.put("MI 2S",      0x05);
		mRulesMap.put("MI 2SC",     0x05);
		mRulesMap.put("HM NOTE 1W", 0x04);
		
		//Lenovo
		mRulesMap.put("Lenovo A820", 0x04);
		mRulesMap.put("Lenovo A630t",0x02);
		
		//Oppo
		mRulesMap.put("U705T", 0x02);
		mRulesMap.put("R831T", 0x02);
		mRulesMap.put("R811",  0x02);
		
	}
	
	@SuppressLint("InlinedApi")
	public static void initRules(Context context) {
		String model     = android.os.Build.MODEL;
		Performance.from = model;
		String SDK       = android.os.Build.VERSION.RELEASE;
		Log.d(TAG, "model = " + model + "sdk" + SDK);
		boolean supportBLEFailed = false;
		
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1)
			mWaitingTime = 1800;
		else 
			mWaitingTime = 5000;
		
		if (null != mRulesMap.get(model)) {
			Integer rule = mRulesMap.get(model);
			mbUnsafeMode = (rule & 0x01) > 0;
			mbNeedSleep  = (rule & 0x04) > 0;
			mbCanClose   = (rule & 0x08) > 0;
			supportBLEFailed = (rule & 0x10) > 0;
			
			if (mbNeedSleep)
				mWaitingTime = 3000;
		} else {
			mbUnsafeMode = true;
			mbNeedSleep  = true;
		}
		
		if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR1){
			boolean hasBLE = context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE);
			mbUseBLE = hasBLE && !supportBLEFailed;
		}
		
		boolean use13 = true;
		if (use13)
			mbUseBLE = false;
		
		if (Build.VERSION.RELEASE.startsWith("2.3.5")){
			if (0 == Build.MANUFACTURER.compareToIgnoreCase("xiaomi")){
				mbUseFixedChannel = true;
			}
		}
		else if (Build.VERSION.RELEASE.startsWith("4.0")){
			if (0 == Build.MANUFACTURER.compareToIgnoreCase("samsung")){
				mbUseFixedChannel = true;
			}
			else if (0 == Build.MANUFACTURER.compareToIgnoreCase("HTC")){
				mbUseFixedChannel = true;
			}
		}
		else if (Build.VERSION.RELEASE.startsWith("4.1")){
			if (0 == Build.MANUFACTURER.compareToIgnoreCase("samsung")){
				mbUseFixedChannel = true;
			}
		}
	}
	
	public static boolean useActivity(){
		if (0 == Build.MANUFACTURER.compareToIgnoreCase("xiaomi")){
			return true;
		}
		
		return false;
	}
}

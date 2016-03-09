package com.medzone.cloud;

import com.medzone.cloud.data.PropertyCenter;

public class GlobalVars {

	public static boolean isRepeatShowUpdateDialog = false;
	private static boolean isOffLineLogined = false;

	public static void uninit() {
		isOffLineLogined = false;
		isRepeatShowUpdateDialog = false;
	}

	public static boolean isOffLineLogined() {
		return isOffLineLogined;
	}

	public static void setOffLined(boolean value) {
		boolean isChanged = false;
		if (isOffLineLogined != value) {
			isChanged = true;
		}
		isOffLineLogined = value;
		if (isChanged) {
			PropertyCenter.getInstance().firePropertyChange(
					PropertyCenter.PROPERTY_REFRESH_AVATAR, null, null);
		}
	}

	public static String formatWebSite(String url) {
		// if (!TextUtils.isEmpty(url)) {
		// int screenWidth = CloudApplication.width;
		// url.concat("&screenWidth=" + screenWidth);
		// }
		return url;
	}
}

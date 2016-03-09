/**
 * 
 */
package com.medzone.cloud.defender;

import android.content.Context;
import cn.jpush.android.api.JPushInterface;

/**
 * @author ChenJunQi.
 * 
 */
public class Defender {

	private Context mContext;

	public Defender(Context context) {
		this.mContext = context;
	}

	public void initJPush() {

		JPushInterface.setDebugMode(true);
		JPushInterface.init(mContext);
		JPushInterface.setLatestNotifactionNumber(mContext,
				CloudPush.NS_MAX_DISPLAY_NUMBER);
	}

	public void startJPush() {

		initJPush();
		if (JPushInterface.isPushStopped(mContext)) {
			JPushInterface.resumePush(mContext);
		}
	}

	public void stopJPush() {

		initJPush();
		if (JPushInterface.isPushStopped(mContext)) {
			return;
		}
		JPushInterface.stopPush(mContext);
	}

	public String getRegisterID() {
		return JPushInterface.getRegistrationID(mContext);
	}
}

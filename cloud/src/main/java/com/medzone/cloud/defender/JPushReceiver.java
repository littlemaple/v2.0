package com.medzone.cloud.defender;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import cn.jpush.android.api.JPushInterface;

import com.medzone.cloud.defender.JPushLocalNotificationHelper.OnApplicationState;
import com.medzone.framework.Log;

public class JPushReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		final Bundle bundle = intent.getExtras();

		if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
			String registrationID = bundle
					.getString(JPushInterface.EXTRA_REGISTRATION_ID);
			Log.d("[JPush] #接收到JPUSH推送下来，此次注册的JPUSH_ID >>>" + registrationID);

			JPushLocalNotificationHelper.sendBroadcast(context,
					BroadCastUtil.ACTION_REGISTRATION_ID, bundle);

		} else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent
				.getAction())) {
			Log.d("[JPush] #接收到自定义消息" + intent.getAction());

			JPushLocalNotificationHelper.showNotification(context, bundle);

		} else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent
				.getAction())) {

			String alert = bundle.getString(JPushInterface.EXTRA_ALERT);
			if (TextUtils.isEmpty(alert)) {
				return;
			}
			Log.d("[JPush] #接收到推送下来的通知:" + alert);

			JPushLocalNotificationHelper.doActionByApplicationState(context,
					null, new OnApplicationState() {

						@Override
						public void onProcessExisted(Context context,
								CloudPush push) {
							String action = JPushLocalNotificationHelper
									.getAction(bundle);
							JPushLocalNotificationHelper.sendBroadcast(context,
									action, bundle);
						}

						@Override
						public void onProcessDestoryed(Context context,
								CloudPush push) {
							// TODO Auto-generated method stub
						}
					});
		} else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent
				.getAction())) {
			Log.d("[JPush] #用户点击打开了通知");

			JPushInterface.reportNotificationOpened(context,
					bundle.getString(JPushInterface.EXTRA_MSG_ID));
			JPushLocalNotificationHelper.openNotification(context, bundle);

		} else if (JPushInterface.EXTRA_CONNECTION_CHANGE.equals(intent
				.getAction())) {

			boolean connected = bundle.getBoolean(
					JPushInterface.EXTRA_CONNECTION_CHANGE, false);
			Log.d("[JPush] #用Jpush连接状态" + connected);

		} else {
			Log.w("[JPush] Unhandled intent - " + intent.getAction());
		}
		JPushLocalNotificationHelper.printBundle(bundle);
	}
}

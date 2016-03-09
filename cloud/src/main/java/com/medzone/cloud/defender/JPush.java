/**
 * 
 */
package com.medzone.cloud.defender;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author ChenJunQi.
 * 
 */
public class JPush implements Parcelable {

	public final static String PUSH_ID = "pushID";
	public final static String NOTIFICATION_ID = "notificationID";
	public final static String MSG_ID = "msgID";
	public final static String NOTIFICATION_CONTENT_TITLE = "notificationContentTitle";
	public final static String ALERT = "alert";
	public final static String EXTRA = "extra";

	private long pushID;
	private long notificationID;
	private long msgID;

	private String alert;
	private String extra;// always json string.
	private String notificationContentTitle;

	public long getPushID() {
		return pushID;
	}

	public void setPushID(long pushID) {
		this.pushID = pushID;
	}

	public String getAlert() {
		return alert;
	}

	public void setAlert(String alert) {
		this.alert = alert;
	}

	public String getExtra() {
		return extra;
	}

	public void setExtra(String extra) {
		this.extra = extra;
	}

	public long getNotificationID() {
		return notificationID;
	}

	public void setNotificationID(long notificationID) {
		this.notificationID = notificationID;
	}

	public String getNotificationContentTitle() {
		return notificationContentTitle;
	}

	public void setNotificationContentTitle(String notificationContentTitle) {
		this.notificationContentTitle = notificationContentTitle;
	}

	public long getMsgID() {
		return msgID;
	}

	public void setMsgID(long msgID) {
		this.msgID = msgID;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		Bundle bundel = new Bundle();
		bundel.putLong(PUSH_ID, pushID);
		bundel.putLong(NOTIFICATION_ID, notificationID);
		bundel.putLong(MSG_ID, msgID);
		bundel.putString(NOTIFICATION_CONTENT_TITLE, notificationContentTitle);
		bundel.putString(ALERT, alert);
		bundel.putString(EXTRA, extra);
		dest.writeBundle(bundel);
	}

	public static final Parcelable.Creator<JPush> CREATOR = new Creator<JPush>() {

		@Override
		public JPush[] newArray(int size) {
			return new JPush[size];
		}

		@Override
		public JPush createFromParcel(Parcel source) {
			JPush jpush = new JPush();
			Bundle bundle = source.readBundle();
			jpush.setPushID(bundle.getLong(PUSH_ID));
			jpush.setNotificationID(bundle.getLong(NOTIFICATION_ID));
			jpush.setMsgID(bundle.getLong(MSG_ID));
			jpush.setNotificationContentTitle(bundle
					.getString(NOTIFICATION_CONTENT_TITLE));
			jpush.setAlert(bundle.getString(ALERT));
			jpush.setExtra(bundle.getString(EXTRA));
			return jpush;
		}
	};

}

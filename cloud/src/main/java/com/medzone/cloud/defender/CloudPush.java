package com.medzone.cloud.defender;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import cn.jpush.android.api.JPushInterface;

import com.medzone.cloud.data.CurrentAccountManager;
import com.medzone.framework.Log;
import com.medzone.framework.data.bean.imp.Account;
import com.medzone.framework.data.bean.imp.Group;
import com.medzone.framework.data.bean.imp.Message;

public class CloudPush {

	public static final int NS_MAX_DISPLAY_NUMBER = 6;
	public static final String tag = CloudPush.class.getSimpleName();
	public static final String EXTRA_KEY_TYPE = "type";
	public static final String EXTRA_KEY_GROUP_ID = "group_id";
	public static final String EXTRA_KEY_GROUP_TITLE = "group_title";
	public static final String EXTRA_KEY_GROUP_ALERT = "group_alert";
	public static final String EXTRA_KEY_GROUP_TYPE = "group_type";
	public static final String EXTRA_KEY_MSG_SENDID = "msg_sendid";
	public static final String EXTRA_KEY_MSG_ID = "msg_id";
	public static final String EXTRA_KEY_NOTIFY_ID = "syncid";
	public static final String EXTRA_KEY_NOTIFY_RESPONSE = "response";

	private long pushID;
	private String pushContent;
	private String pushTitle;
	private JSONObject json;
	private Bundle bundle;

	public CloudPush() {
		// TODO Auto-generated constructor stub
	}

	public CloudPush(Bundle bundle) {
		if (bundle == null)
			return;
		setBundle(bundle);
		// 自定义消息：
		if (bundle.containsKey(JPushInterface.EXTRA_MESSAGE)) {
			setPushContent(bundle.getString(JPushInterface.EXTRA_MESSAGE));
		}
		if (bundle.containsKey(JPushInterface.EXTRA_MSG_ID)) {
			setPushID(Long.valueOf(bundle
					.getString(JPushInterface.EXTRA_MSG_ID)));
		}
		if (bundle.containsKey(JPushInterface.EXTRA_TITLE)) {
			setPushTitle(bundle.getString(JPushInterface.EXTRA_TITLE));
		}
		if (bundle.containsKey(JPushInterface.EXTRA_EXTRA)) {
			setPushExtraJSON(bundle.getString(JPushInterface.EXTRA_EXTRA));
		}
	}

	public void setBundle(Bundle bundle) {
		this.bundle = bundle;
	}

	public Bundle getBundle() {
		return bundle;
	}

	public long getPushID() {
		return pushID;
	}

	public void setPushID(long pushID) {
		this.pushID = pushID;
	}

	public String getPushContent() {
		return pushContent;
	}

	public void setPushContent(String pushContent) {
		this.pushContent = pushContent;
	}

	public String getPushTitle() {
		return pushTitle;
	}

	public void setPushTitle(String pushTitle) {
		this.pushTitle = pushTitle;
	}

	public JSONObject getPushExtraJSON() {
		return json;
	}

	public String getPushExtraJsonString() {
		if (json != null) {
			return json.toString();
		}
		return null;
	}

	public void setPushExtraJSON(JSONObject pushExtraJSON) {
		this.json = pushExtraJSON;
	}

	public void setPushExtraJSON(String extra) {
		try {
			this.json = new JSONObject(extra);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	// ==========================
	// 获取Extra字段
	// ==========================

	public String getExtraType() {
		return (String) getValue(EXTRA_KEY_TYPE, String.class);
	}

	public int getExtraGroupID() {

		Integer ret = (Integer) getValue(EXTRA_KEY_GROUP_ID, Integer.class);
		Log.v("###EXTRA_KEY_GROUP_ID:" + ret);
		if (ret == null) {
			return (int) Group.INVALID_ID;
		}
		return ret;
	}

	public Integer getExtraGroupAlert() {
		return (Integer) getValue(EXTRA_KEY_GROUP_ALERT, Integer.class);
	}

	public String getExtraGroupName() {
		return (String) getValue(EXTRA_KEY_GROUP_TITLE, String.class);
	}

	public int getExtraGroupType() {

		Integer ret = (Integer) getValue(EXTRA_KEY_GROUP_TYPE, Integer.class);
		Log.v("###EXTRA_KEY_GROUP_TYPE:" + ret);
		if (ret == null) {
			return (int) Group.INVALID_ID;
		}
		return ret;
	}

	public int getExtraMessageSendID() {

		Integer serverMsgID = (Integer) getValue(EXTRA_KEY_MSG_SENDID,
				Integer.class);
		Log.v("###EXTRA_KEY_GROUP_TYPE:" + serverMsgID);
		if (serverMsgID == null) {
			return (int) Account.INVALID_ID;// XXX 无效ID，除此之外的其他表示为群聊天。
		}

		int ret = serverMsgID.intValue();
		if (serverMsgID.intValue() == 0) {
			return (int) Account.INVALID_ID;
		}
		if (getExtraGroupType() == Group.TYPE_SERVICE) {
			if (CurrentAccountManager.getCurAccount() == null) {
				return (int) Account.INVALID_ID;
			}
			if (serverMsgID == CurrentAccountManager.getCurAccount()
					.getAccountID()) {
				return (int) Account.INVALID_ID;
			}
		} else {
			return (int) Account.INVALID_ID;
		}
		return ret;
	}

	public int getExtraMessageID() {

		Integer ret = (Integer) getValue(EXTRA_KEY_MSG_ID, Integer.class);
		Log.v("###EXTRA_KEY_MSG_ID:" + ret);
		if (ret == null) {
			return (int) Message.INVALID_ID;
		}
		return ret;
	}

	public int getExtraNotifyMemberID() {

		Integer ret = (Integer) getValue(EXTRA_KEY_NOTIFY_ID, Integer.class);
		Log.v("###EXTRA_KEY_NOTIFY_ID:" + ret);
		if (ret == null) {
			return (int) Account.INVALID_ID;
		}
		return ret;
	}

	public boolean getExtraNotifyResponse() {
		return (String) getValue(EXTRA_KEY_NOTIFY_RESPONSE, String.class) == "Y" ? true
				: false;
	}

	// ==========================
	// 工具类
	// ==========================
	private Object getValue(String key, Class<?> clazz) {
		if (this.json != null) {
			if (json.has(key) && !json.isNull(key)) {
				try {
					if (clazz.equals(Integer.class)) {
						return json.getInt(key);
					} else if (clazz.equals(String.class)) {
						return json.getString(key);
					} else if (clazz.equals(Boolean.class)) {
						return json.getBoolean(key);
					} else {
						return json.get(key);
					}

				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}

	public static void printr(CloudPush push) {
		try {
			Log.v("#" + push.getPushID());
			Log.v("#" + push.getPushTitle());
			Log.v("#" + push.getPushContent());
			Log.v("#" + push.getPushExtraJsonString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

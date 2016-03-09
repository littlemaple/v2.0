/**
 * 
 */
package com.medzone.cloud.data;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import com.medzone.cloud.CloudApplication;

/**
 * @author ChenJunQi.
 * 
 */
public class PropertyCenter {

	public static final String PROPERTY_CONNECT_STATE = "property_connect_state";
	public static final String PROPERTY_CHECK_UPDATE = "property_check_update";
	public static final String PROPERTY_POP_UPDATE = "property_pop_update";

	public static final String PROPERTY_REFRESH_CHATROOM = "property_refresh_chatroom";
	public static final String PROPERTY_REFRESH_CHAT_AVATAR = "property_notify_chat_avatar";
	public static final String PROPERTY_REFRESH_GROUPNOTIFY = "property_refresh_groupnotify";
	public static final String PROPERTY_REFRESH_GROUPLIST = "property_refresh_grouplist";

	/** 普通群列表展开 */
	public static final String PROPERTY_REFRESH_GROUPLIST_EXPAND = "property_refresh_grouplist_expand";
	public static final String PROPERTY_REFRESH_MY_MODULES = "property_refresh_my_modules";
	public static final String PROPERTY_REFRESH_OTHER_MODULES = "property_refresh_other_modules";
	// XXX 目前没有区分，刷新指定模块的数据，表示所有测量数据都刷新一次。
	public static final String PROPERTY_REFRESH_HISTORY_DATA = "property_refresh_history_data";
	public static final String PROPERTY_REFRESH_GROUP_MEMBER = "property_refresh_group_member";
	public static final String PROPERTY_REFRESH_ACCOUNT = "property_refresh_account";
	public static final String PROPERTY_REFRESH_AVATAR = "property_refresh_avatar";
	public static final String PROPERTY_REFRESH_CHAT_MEMBER = "property_refresh_chat_member";
	public static final String PROPERTY_REFRESH_NOTIFICATIONMESSAGE = "property_refresh_notificationmessage";
	public static final String PROPERTY_REFRESH_DELETE_STAGE = "property_refresh_delete_stage";

	// List For JPush.
	public static final String PROPERTY_NOTIFY_GROUP_CHATMESSAGE = "property_notify_group_chatmessage";
	public static final String PROPERTY_NOTIFY_GROUP_KICKED = "property_notify_group_kicked";
	public static final String PROPERTY_NOTIFY_GROUP_DISSMISS = "property_notify_group_dissmiss";
	public static final String PROPERTY_NOTIFY_GROUP_QUITED = "property_notify_group_quited";

	// 关闭首页，主要针对3.0以下的机型
	public static final String PROPERTY_FINISH_HOME = "property_finish_home";

	private static PropertyChangeSupport instance;

	public static PropertyChangeSupport getInstance() {
		if (instance == null) {
			instance = new PropertyChangeSupport(CloudApplication.getInstance()
					.getApplicationContext());
		}
		return instance;
	}

	public static void addPropertyChangeListener(
			PropertyChangeListener paramPropertyChangeListener) {
		getInstance().addPropertyChangeListener(paramPropertyChangeListener);
	}

	public static void removePropertyChangeListener(
			PropertyChangeListener paramPropertyChangeListener) {
		getInstance().removePropertyChangeListener(paramPropertyChangeListener);
	}
}

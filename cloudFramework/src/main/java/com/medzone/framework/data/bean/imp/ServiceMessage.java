package com.medzone.framework.data.bean.imp;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.medzone.framework.network.NetworkClientResult;

/**
 * 
 * @author ChenJunQi.
 * 
 *         群组通知消息
 */
public class ServiceMessage {

	/**
	 */
	// private static final long serialVersionUID = -447486839535957066L;
	public static final String NAME_FIELD_MESSAGE_ID = "messageid";
	public static final String NAME_FIELD_MESSAGE_DATA = "messageData";
	public static final String NAME_FIELD_MESSAGE_TYPE = "messageType";
	public static final String NAME_FIELD_GROUP_ID = "groupid";
	public static final String NAME_FIELD_GROUPNAME = "groupname";
	public static final String NAME_FIELD_IS_READ = "isread";
	public static final String NAME_FIELD_RESPONSE = "response";

	public static final int TYPE_BUTTON_ENABLE = 0;
	public static final int TYPE_BUTTON_DISABLE = 1;

	public static final int TYPE_NORMAL = 0; // 普通文本消息
	public static final int TYPE_INVITE_GROUP = 1; // 邀请入群
	public static final int TYPE_ACCEPT_GROUP = 2; // 同意入群
	public static final int TYPE_REFUSE_GROUP = 3; // 拒绝入群
	public static final int TYPE_KICK_GROUP = 4; // 被踢出群
	public static final int TYPE_QUIT_GROUP = 5; // 用户退群
	public static final int TYPE_DISMISS_GROUP = 6; // 解散群

	private int messageId;
	private Integer groupID;
	private String groupName;
	private Account sendAccount;
	private Boolean isRead;
	private Integer messageType;
	private String groupAvatar;
	private Boolean isAcceptInvite;

	public Boolean isAcceptInvite() {
		return isAcceptInvite;
	}

	public void setAcceptInvite(Boolean isAcceptInvite) {
		this.isAcceptInvite = isAcceptInvite;
	}

	public Account getSendAccount() {
		return sendAccount;
	}

	public void setSendAccount(Account sendAccount) {
		this.sendAccount = sendAccount;
	}

	public Boolean isRead() {
		return isRead;
	}

	public void setIsRead(Boolean isRead) {
		this.isRead = isRead;
	}

	public Integer getMessageType() {
		return messageType;
	}

	public void setMessageType(Integer messageType) {
		this.messageType = messageType;
	}

	public void setGroupId(Integer groupId) {
		this.groupID = groupId;
	}

	public int getMessageId() {
		return messageId;
	}

	public void setMessageId(int messageId) {
		this.messageId = messageId;
	}

	public Integer getGroupId() {
		if (groupID == null) {
			String invalid = String.valueOf(Group.INVALID_ID);
			return Integer.valueOf(invalid);
		}
		return groupID.intValue();
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public String getGroupAvatar() {
		return groupAvatar;
	}

	public void setGroupAvatar(String groupAvatar) {
		this.groupAvatar = groupAvatar;
	}

	public static List<ServiceMessage> getMessageListByResult(
			NetworkClientResult res) {
		JSONObject jo = res.getResponseResult();
		List<ServiceMessage> list = new ArrayList<ServiceMessage>();
		try {
			if (jo != null) {
				JSONArray ja = jo.getJSONArray("root");
				for (int i = 0; i < ja.length(); i++) {
					JSONObject mJson = ja.getJSONObject(i);
					ServiceMessage am = createServiceMessage(mJson);
					list.add(am);
				}

			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return list;
	}

	/**
	 * 对邀请信息进行处理，分成未阅读，已同意，已拒绝三种情况
	 * 
	 * @param list
	 * @return
	 */
	// public static List<ServiceMessage> createServiceMessageList(
	// List<ServiceMessage> list) {
	// if (list.size() > 0) {
	// Iterator<ServiceMessage> iter = list.iterator();
	// while (iter.hasNext()) {
	// ServiceMessage msg = (ServiceMessage) iter.next();
	// }
	// }
	// return list;
	//
	// }

	private static ServiceMessage createServiceMessage(JSONObject jo) {

		ServiceMessage am = new ServiceMessage();
		return parse(jo, am);
	}

	private static ServiceMessage parse(JSONObject jo, ServiceMessage am) {

		try {
			if (jo.has("messageid") && !jo.isNull("messageid")) {
				am.setMessageId(jo.getInt("messageid"));
			}
			if (jo.has("type") && !jo.isNull("type")) {
				am.setMessageType(jo.getInt("type"));
			}
			if (jo.has("sender") && !jo.isNull("sender")) {
				JSONObject mo = jo.getJSONObject("sender");
				Account account = new Account();
				if (mo.has("imagefile") && !mo.isNull("imagefile")) {
					account.setHeadPortRait(mo.getString("imagefile"));
				}
				if (mo.has("username") && !mo.isNull("username")) {
					account.setRealName(mo.getString("username"));
				}
				if (mo.has("nickname") && !mo.isNull("nickname")) {
					account.setNickname(mo.getString("nickname"));
				}
				if (mo.has("syncid") && !mo.isNull("syncid")) {
					account.setAccountID(mo.getInt("syncid"));
				}
				am.setSendAccount(account);
			}
			if (jo.has("isread") && !jo.isNull("isread")) {
				String temp = jo.getString("isread");
				if (temp != null) {
					if (temp.equals("Y"))
						am.setIsRead(true);
					else
						am.setIsRead(false);
				}
			}
			if (jo.has("response")) {
				if (jo.isNull("response")) {
					am.setAcceptInvite(null);
				} else {
					String temp = jo.getString("response");
					if (temp.equals("Y"))
						am.setAcceptInvite(true);
					if (temp.equals("N"))
						am.setAcceptInvite(false);
				}
			}
			if (jo.has("data") && !jo.isNull("data")) {
				JSONObject mo = jo.getJSONObject("data");

				if (mo.has("group_title") && !mo.isNull("group_title")) {
					am.setGroupName(mo.getString("group_title"));
				}
				if (mo.has("group_id") && !mo.isNull("group_id")) {
					int groupid = Integer.valueOf(mo.getString("group_id"));
					am.setGroupId(groupid);
				}
				// if (mo.has("group_type") && !mo.isNull("group_type")) {
				//
				// }
				// 以下为兼容老的数据格式
				if (mo.has("groupid") && !mo.isNull("groupid")) {
					int groupid = Integer.valueOf(mo.getString("groupid"));
					am.setGroupId(groupid);
				}
				if (mo.has("title") && !mo.isNull("title")) {
					am.setGroupName(mo.getString("title"));
				}
			}
			return am;
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
	}
}

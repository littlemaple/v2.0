/**
 * 
 */
package com.medzone.framework.data.bean.imp;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextUtils;

import com.j256.ormlite.field.DatabaseField;
import com.medzone.framework.data.bean.BaseEntity;
import com.medzone.framework.network.NetworkClientResult;
import com.medzone.framework.util.TimeUtils;

/**
 * @author ChenJunQi.
 * 
 */
public class GroupMember extends BaseEntity {

	/**
	 */
	private static final long serialVersionUID = -6314284663945160363L;
	public static final String NAME_FIELD_GROUP_MEMBER_REMARK = "remark";
	public static final String NAME_FIELD_GROUP_ID = "groupID";
	public static final String NAME_FIELD_GROUP_MEMBER_ID = "accountID";
	public static final String NAME_FIELD_GROUP_MEMBER_NICKNAME = "nickname";
	public static final String NAME_FIELD_GROUP_MEMBER_REALNAME = "realname";
	public static final String NAME_FIELD_GROUP_MEMBER_AGE = "age";
	public static final String NAME_FIELD_GROUP_MEMBER_HEADPORTRAIT = "headPortRait";
	public static final String NAME_FIELD_ISVIEWHISTORY = "isViewHistory";
	public static final String NAME_FIELD_ISCARE = "isCare";
	public static final String NAME_FIELD_ISMEASURE = "isMeasure";
	public static final String NAME_FIELD_ISVIEWHISTORY_FORME = "isViewHistoryForMe";
	public static final String NAME_FIELD_ISCARE_FORME = "isCareForMe";
	public static final String NAME_FIELD_ISMEASURE_FORME = "isMeasureForMe";

	public static final int TYPE_NORMAL = 0; // 普通文本消息
	public static final int TYPE_INVITE_GROUP = 1; // 邀请入群
	public static final int TYPE_ACCEPT_GROUP = 2; // 同意入群
	public static final int TYPE_REFUSE_GROUP = 3; // 拒绝入群
	public static final int TYPE_KICK_GROUP = 4; // 被踢出群
	public static final int TYPE_QUIT_GROUP = 5; // 用户退群
	public static final int TYPE_DISMISS_GROUP = 6; // 解散群

	@DatabaseField
	private String remark;// 群成员备注 与 accountID构成关系链。

	@DatabaseField
	private Integer groupID; // 群ID

	@DatabaseField
	private Integer accountID; // 群成员的帐号ID

	@DatabaseField
	private String nickname;// 群成员昵称

	@DatabaseField
	private String realname;

	@DatabaseField
	private String headPortRait;// 群成员头像链接

	/***
	 * 
	 * I set the permissions for others.
	 */
	@DatabaseField
	private Boolean isViewHistory;

	/***
	 * 
	 * I set the permissions for others.
	 */
	@DatabaseField
	private Boolean isCare;

	/***
	 * 
	 * I set the permissions for others.
	 */
	@DatabaseField
	private Boolean isMeasure;

	@DatabaseField
	private int age;

	/**
	 * others set the permissions for me.
	 */
	@DatabaseField
	private Boolean isViewHistoryForMe;
	/**
	 * others set the permissions for me.
	 */
	@DatabaseField
	private Boolean isCareForMe;
	/**
	 * others set the permissions for me.
	 */
	@DatabaseField
	private Boolean isMeasureForMe;

	private Integer eventID;
	private Integer eventType;
	private JSONObject eventContent;
	private Group bindGroup;

	private Boolean isLongClickState = false;

	public void setAge(int age) {
		this.age = age;
	}

	public int getAge() {
		return age;
	}

	public Boolean getIsLongClickState() {
		return isLongClickState;
	}

	public void setIsLongClickState(Boolean isLongClickState) {
		this.isLongClickState = isLongClickState;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public Integer getGroupID() {
		if (groupID == null) {
			String invalid = String.valueOf(Group.INVALID_ID);
			return Integer.valueOf(invalid);
		}
		return groupID.intValue();
	}

	public void setGroupID(Integer groupID) {
		this.groupID = groupID;
	}

	public Integer getAccountID() {
		if (accountID == null) {
			String invalid = String.valueOf(Account.INVALID_ID);
			return Integer.valueOf(invalid);
		}
		return accountID.intValue();
	}

	public void setAccountID(Integer id) {
		this.accountID = id;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getHeadPortRait() {
		return headPortRait;
	}

	public void setHeadPortRait(String headPortRait) {
		this.headPortRait = headPortRait;
	}

	public Boolean isViewHistory() {
		return isViewHistory;
	}

	public void setViewHistory(Boolean isViewHistory) {
		this.isViewHistory = isViewHistory;
	}

	public Boolean isCare() {
		return isCare;
	}

	public void setCare(Boolean isCare) {
		this.isCare = isCare;
	}

	public Boolean isMeasure() {
		return isMeasure;
	}

	public void setMeasure(Boolean isMeasure) {
		this.isMeasure = isMeasure;
	}

	public Boolean isViewHistoryForMe() {
		return isViewHistoryForMe;
	}

	public void setViewHistoryForMe(Boolean isViewHistoryForMe) {
		this.isViewHistoryForMe = isViewHistoryForMe;
	}

	public Boolean isCareForMe() {
		return isCareForMe;
	}

	public void setCareForMe(Boolean isCareForMe) {
		this.isCareForMe = isCareForMe;
	}

	public Boolean isMeasureForMe() {
		return isMeasureForMe;
	}

	public void setMeasureForMe(Boolean isMeasureForMe) {
		this.isMeasureForMe = isMeasureForMe;
	}

	public Group getBindGroup() {
		return bindGroup;
	}

	public void setBindGroup(Group bindGroup) {
		this.bindGroup = bindGroup;
	}

	public Integer getEventID() {
		return eventID;
	}

	public void setEventID(Integer eventID) {
		this.eventID = eventID;
	}

	public Integer getEventType() {
		return eventType;
	}

	public void setEventType(Integer eventType) {
		this.eventType = eventType;
	}

	public JSONObject getEventContent() {
		return eventContent;
	}

	public String getLastEventContent() {

		if (getEventContent() == null)
			return "";

		String msg = "";
		String groupName = "";
		int type = 0;
		try {
			if (getEventContent().has("title")
					&& !getEventContent().isNull("title")) {
				groupName = getEventContent().getString("title");
			}
			if (getEventContent().has("type")
					&& !getEventContent().isNull("type")) {
				type = getEventContent().getInt("type");
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		switch (type) {
		case TYPE_ACCEPT_GROUP:
			msg = getNickname() + "加入了" + groupName;
			break;
		case TYPE_DISMISS_GROUP:
			msg = groupName + "群已解散";
			break;
		case TYPE_KICK_GROUP:
			msg = getNickname() + "被踢出了" + groupName;
			break;
		case TYPE_INVITE_GROUP:
			msg = getNickname() + "邀请加入了" + groupName;
			break;
		case TYPE_NORMAL:
			msg = getNickname() + "在" + groupName + "群发布了一条消息";
			break;
		case TYPE_QUIT_GROUP:
			msg = getNickname() + "退出了" + groupName;
			break;
		case TYPE_REFUSE_GROUP:
			msg = getNickname() + "拒绝加入" + groupName;
			break;
		}
		return msg;

	}

	public void setEventContent(JSONObject eventContent) {
		this.eventContent = eventContent;
	}

	public String getRealName() {
		return realname;
	}

	public void setRealName(String groupMemberRealName) {
		this.realname = groupMemberRealName;
	}

	public static List<GroupMember> createGroupMemberList(
			NetworkClientResult res, Integer groupId, Integer accountID) {
		JSONArray ja = null;
		List<GroupMember> list = null;
		JSONObject jo = res.getResponseResult();
		try {
			ja = jo.getJSONArray("root");
			list = new ArrayList<GroupMember>();
			for (int i = 0; i < ja.length(); i++) {
				GroupMember member = createGroupMember((JSONObject) ja.get(i),
						groupId, accountID);
				if (member != null)
					list.add(member);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return list;
	}

	public static List<GroupMember> createGroupMemberListExceptOwner(
			NetworkClientResult res, Integer groupId, Integer accountID) {
		List<GroupMember> list = createGroupMemberList(res, groupId, accountID);

		int index = -1;
		for (GroupMember member : list) {
			index++;
			if (member.getAccountID().equals(accountID)) {
				break;
			}
		}
		list.remove(index);
		return list;
	}

	public static List<GroupMember> createGroupMemberList(
			NetworkClientResult res, Integer accountID) {
		JSONArray ja = null;
		List<GroupMember> list = null;
		JSONObject jo = res.getResponseResult();
		try {
			ja = jo.getJSONArray("root");
			list = new ArrayList<GroupMember>();
			for (int i = 0; i < ja.length(); i++) {
				GroupMember member = createGroupMember((JSONObject) ja.get(i),
						null, accountID);
				if (member != null)
					list.add(member);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return list;
	}

	public static GroupMember createGroupMember(JSONObject jo, Integer groupId,
			Integer accountID) {

		GroupMember member = new GroupMember();
		member.setGroupID(groupId);

		return parse(jo, member);
	}

	public static GroupMember updateGroup(JSONObject jo, GroupMember member) {
		return parse(jo, member);
	}

	public static GroupMember updateGroupAuthoForMe(JSONObject jo,
			GroupMember member) {
		return parseAuthoForMe(jo, member);
	}

	/***
	 * 
	 * normal parse
	 * 
	 * @param jo
	 * @param member
	 * @return
	 */
	private static GroupMember parse(JSONObject jo, GroupMember member) {

		try {
			// Query from account .
			if (jo.has("syncid") && !jo.isNull("syncid")) {
				member.setAccountID(jo.getInt("syncid"));
			}
			if (jo.has("birthday") && !jo.isNull("birthday")) {
				Date date = TimeUtils.getDate(jo.getString("birthday"),
						TimeUtils.DATE_FORMAT_DATE);
				member.setAge((int) TimeUtils.getGapYearByBirthday(date));
			}
			if (jo.has("imagefile") && !jo.isNull("imagefile")) {
				member.setHeadPortRait(jo.getString("imagefile"));
			}
			// Query from group member.
			if (jo.has("age") && !jo.isNull("age")) {
				member.setAge(jo.getInt("age"));
			}
			if (jo.has("username") && !jo.isNull("username")) {
				member.setRealName(jo.getString("username"));
			}
			if (jo.has("nickname") && !jo.isNull("nickname")) {
				member.setNickname(jo.getString("nickname"));
			} else {
				member.setNickname("昵称:" + new Random().nextInt());
			}

			if (jo.has("lastEvent") && !jo.isNull("lastEvent")) {
				JSONObject o = jo.getJSONObject("lastEvent");
				if (o.has("eventid") && !o.isNull("eventid")) {
					member.setEventID(o.getInt("eventid"));
				}
				if (o.has("type") && !o.isNull("type")) {
					member.setEventType(o.getInt("type"));
				}
				if (o.has("data") && !o.isNull("data")) {
					member.setEventContent(o.getJSONObject("data"));
				}
			}

			// ----------------------获取我对别人设置的权限------------------------//
			if (jo.has("remark") && !jo.isNull("remark")) {
				if (jo.get("remark") != JSONObject.NULL)
					member.setRemark(jo.getString("remark"));
			}
			if (jo.has("syncid") && !jo.isNull("syncid")) {
				member.setAccountID(jo.getInt("syncid"));
			}
			if (jo.has("iscare") && !jo.isNull("iscare")) {
				member.setCare(TextUtils.equals(jo.getString("iscare"), "Y") ? true
						: false);
			}
			if (jo.has("istest") && !jo.isNull("istest")) {
				member.setMeasure(TextUtils.equals(jo.getString("istest"), "Y") ? true
						: false);
			}
			if (jo.has("isview") && !jo.isNull("isview")) {
				member.setViewHistory(TextUtils.equals(jo.getString("isview"),
						"Y") ? true : false);
			}

			return member;
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}

	}

	private static GroupMember parseAuthoForMe(JSONObject jo, GroupMember member) {
		// ----------------------获取别人对我设置的权限------------------------//
		try {
			// if (jo.has("remark") && !jo.isNull("remark")) {
			// if (jo.get("remark") != JSONObject.NULL)
			// member.setRemark(jo.getString("remark"));
			// }
			// if (jo.has("syncid") && !jo.isNull("syncid")) {
			// member.setAccountID(jo.getInt("syncid"));
			// }
			if (jo.has("iscare") && !jo.isNull("iscare")) {
				member.setCareForMe(TextUtils.equals(jo.getString("iscare"),
						"Y") ? true : false);
			}
			if (jo.has("istest") && !jo.isNull("istest")) {
				member.setMeasureForMe(TextUtils.equals(jo.getString("istest"),
						"Y") ? true : false);
			}
			if (jo.has("isview") && !jo.isNull("isview")) {
				member.setViewHistoryForMe(TextUtils.equals(
						jo.getString("isview"), "Y") ? true : false);
			}
			return member;
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public boolean equals(Object o) {

		if (o instanceof Group) {
			Group tmpGroup = (Group) o;
			int accountID = getIDValue(this.getAccountID());
			int ownerID = getIDValue(tmpGroup.getCreatorID());
			return accountID == ownerID;
		} else if (o instanceof Account) {
			Account tmpAccount = (Account) o;
			int accountID = getIDValue(this.getAccountID());
			int accountID2 = getIDValue(tmpAccount.getAccountID());
			return accountID == accountID2;
		}

		return super.equals(o);
	}

}

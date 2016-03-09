package com.medzone.framework.data.bean.imp;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextUtils;

import com.j256.ormlite.field.DatabaseField;
import com.medzone.framework.data.bean.BaseEntity;
import com.medzone.framework.network.NetworkClientResult;

/**
 * 
 * @author ChenJunQi.
 * 
 * 
 */
public class Group extends BaseEntity implements Cloneable {

	/**
	 */
	private static final long serialVersionUID = 1939008376915730062L;
	public static final String NAME_FIELD_ACCOUNT_ID = "accountID";
	public static final String NAME_FIELD_GROUP_ID = "groupID";
	public static final String NAME_FIELD_GROUP_HEADPORTRAIT = "headPortRait";
	public static final String NAME_FIELD_GROUP_NAME = "name";
	public static final String NAME_FIELD_GROUP_DESCRIPTION = "description";
	public static final String NAME_FIELD_GROUP_TYPE = "type";
	public static final String NAME_FIELD_GROUP_VERIFY = "verify";
	public static final String NAME_FIELD_GROUP_OWNER_ID = "creatorID";
	public static final String NAME_FIELD_GROUP_SETTING_ALERT = "settingAlert";
	public static final String NAME_FIELD_GROUP_SETTING_UPLOAD = "settingUpload";
	public static final String NAME_FIELD_GROUP_UNREADMESSAGENUM = "unReadMessageNum";

	public static final int TYPE_NORMAL = 0;
	public static final int TYPE_SERVICE = 1;
	public static final int TYPE_EXTRA_CAREFUL = 2;
	public static final int TYPE_COMMUNITY = 3;

	@DatabaseField
	private Integer accountID; // 用于本地离线情况获取帐号相关的群信息

	@DatabaseField
	private Integer groupID; // 群ID

	@DatabaseField
	private String headPortRait;// 群头像

	@DatabaseField
	private String name;// 群名称

	@DatabaseField
	private String description;// 群简介

	@DatabaseField
	private Integer type;// 群类别

	@DatabaseField
	private String verify;// 群认证信息

	@DatabaseField
	private Integer creatorID;// 群主ID

	@DatabaseField
	private Integer settingAlert;// 群消息的提醒方式。

	@DatabaseField
	private Boolean settingUpload;// 是否分享最新测量数据到该群，设置为 Y=分享/N=不分享。

	@DatabaseField
	private Integer unReadMessageNum;// 未读消息数量，通过刷新群列表去获取。

	private Integer memberNum;
	private Integer memberNumLimit;
	private Long readTime;

	public Integer getAccountID() {
		if (accountID == null) {
			String invalid = String.valueOf(Account.INVALID_ID);
			return Integer.valueOf(invalid);
		}
		return accountID.intValue();
	}

	public void setAccountID(Integer accountID) {
		this.accountID = accountID;
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

	public String getHeadPortRait() {
		return headPortRait;
	}

	public void setHeadPortRait(String groupHeadPortRait) {
		this.headPortRait = groupHeadPortRait;
	}

	public String getName() {
		return name;
	}

	public void setName(String groupName) {
		this.name = groupName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String groupDescription) {
		this.description = groupDescription;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer groupType) {
		this.type = groupType;
	}

	public String getVerify() {
		return verify;
	}

	public void setVerify(String verify) {
		this.verify = verify;
	}

	public Integer getCreatorID() {
		return creatorID;
	}

	public void setCreatorID(Integer creatorID) {
		this.creatorID = creatorID;
	}

	public Integer getSettingAlert() {
		return settingAlert;
	}

	public void setSettingAlert(Integer settingAlert) {
		this.settingAlert = settingAlert;
	}

	public Boolean getSettingUpload() {
		return settingUpload;
	}

	public void setSettingUpload(Boolean settingUpload) {
		this.settingUpload = settingUpload;
	}

	public Integer getUnReadMessageNum() {
		return unReadMessageNum;
	}

	public void setUnReadMessageNum(Integer unReadMessageNum) {
		this.unReadMessageNum = unReadMessageNum;
	}

	public Integer getMemberNum() {
		return memberNum;
	}

	public void setMemberNum(Integer memberNum) {
		this.memberNum = memberNum;
	}

	public Integer getMemberNumLimit() {
		return memberNumLimit;
	}

	public void setMemberNumLimit(Integer memberNumLimit) {
		this.memberNumLimit = memberNumLimit;
	}

	public Long getReadTime() {
		return readTime;
	}

	public void setReadTime(Long readTime) {
		this.readTime = readTime;
	}

	public boolean isSameRecord(Object o) {
		Group item2 = (Group) o;

		if (this.getGroupID() == INVALID_ID || item2.getGroupID() == INVALID_ID) {
			return false;
		}
		if (!this.getGroupID().equals(item2.getGroupID())
				|| !this.getAccountID().equals(item2.getAccountID())) {
			return false;
		}
		return true;
	}

	@Override
	public void cloneFrom(Object o) {
		Group item2 = (Group) o;
		this.setAccountID(item2.getAccountID());
		this.setGroupID(item2.getGroupID());
		super.cloneFrom(o);
	}

	public static List<Group> createGroupList(NetworkClientResult res,
			Integer accountID) {
		JSONArray ja = null;
		List<Group> list = null;
		JSONObject jo = res.getResponseResult();
		try {
			ja = jo.getJSONArray("root");
			list = new ArrayList<Group>();
			for (int i = 0; i < ja.length(); i++) {
				Group tmpGroup = createGroup((JSONObject) ja.get(i), accountID);
				if (tmpGroup != null)
					list.add(tmpGroup);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return list;
	}

	public static Group createGroup(JSONObject jo, Integer accountID) {

		Group group = new Group();
		group.setAccountID(accountID);

		return parse(jo, group);
	}

	public static Group updateGroup(JSONObject jo, Group group) {
		return parse(jo, group);
	}

	private static Group parse(JSONObject jo, Group group) {

		try {
			if (jo.has("setting") && !jo.isNull("setting")) {
				JSONObject o = jo.getJSONObject("setting");
				if (o.has("alert") && !o.isNull("alert")) {
					group.setSettingAlert(o.getInt("alert"));
				}
				if (o.has("isupload") && !o.isNull("isupload")) {
					String isupload = o.getString("isupload");
					group.setSettingUpload(TextUtils.equals(isupload, "Y") ? true
							: false);
				}
				if (o.has("unread") && !o.isNull("unread")) {
					group.setUnReadMessageNum(o.getInt("unread"));
				}
				if (o.has("readtime") && !o.isNull("readtime")) {
					Long readTime = o.getLong("readtime");
					group.setReadTime(readTime);
				}
			}
			// 时间戳
			if (jo.has("membernum") && !jo.isNull("membernum")) {
				int membernum = jo.getInt("membernum");
				group.setMemberNum(membernum);
			}
			if (jo.has("memberlimit") && !jo.isNull("memberlimit")) {
				int memberlimit = jo.getInt("memberlimit");
				group.setMemberNumLimit(memberlimit);
			}
			if (jo.has("chrono") && !jo.isNull("chrono")) {
				// chrono":"1402052810"
			}
			if (jo.has("title") && !jo.isNull("title")) {
				group.setName(jo.getString("title"));
			}
			if (jo.has("verify") && !jo.isNull("verify")) {
				group.setVerify(jo.getString("verify"));
			}
			if (jo.has("description") && !jo.isNull("description")) {
				group.setDescription(jo.getString("description"));
			}
			if (jo.has("owner") && !jo.isNull("owner")) {
				JSONObject o = jo.getJSONObject("owner");
				if (o.has("syncid") && !o.isNull("syncid")) {
					group.setCreatorID(o.getInt("syncid"));
				}
				if (o.has("username") && !o.isNull("username")) {
					// 群主名
				}
				if (o.has("imagefile") && !o.isNull("imagefile")) {
					// 群主头像
				}
			}
			if (jo.has("imageurl") && !jo.isNull("imageurl")) {
				group.setHeadPortRait(jo.getString("imageurl"));
			}
			if (jo.has("groupid") && !jo.isNull("groupid")) {
				group.setGroupID(jo.getInt("groupid"));
			}
			if (jo.has("type") && !jo.isNull("type")) {
				group.setType(jo.getInt("type"));
			}
			return group;
		} catch (JSONException e) {
			return null;
		}
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof Account) {
			// 是否群主
			Account tmpAccount = (Account) o;
			int ownerID = getIDValue(this.getCreatorID());
			int accontID = getIDValue(tmpAccount.getAccountID());
			return ownerID == accontID;
		} else if (o instanceof GroupMember) {
			// 查看别人是否群主
			GroupMember tmpGroupMember = (GroupMember) o;
			int ownerID = getIDValue(this.getCreatorID());
			int accontID = getIDValue(tmpGroupMember.getAccountID());
			return ownerID == accontID;
		}
		return super.equals(o);
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}
}

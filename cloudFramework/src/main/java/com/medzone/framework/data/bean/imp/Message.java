/**
 * 
 */
package com.medzone.framework.data.bean.imp;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextUtils;

import com.j256.ormlite.field.DatabaseField;
import com.medzone.framework.Log;
import com.medzone.framework.data.bean.BasePagingContent;
import com.medzone.framework.network.NetworkClientResult;

public class Message extends BasePagingContent {

	// join=加入群/quit=退群/kicked=被踢
	public static final String NOTIFY_JOIN = "join";
	public static final String NOTIFY_QUIT = "quit";
	public static final String NOTIFY_KICKED = "kicked";

	public static final long serialVersionUID = 1001010101010L;
	/**
	 * 普通文本消息
	 */
	public static final int TYPE_NORMAL = 0;
	/**
	 * 外部 URL 链接
	 */
	public static final int TYPE_LINK = 1;
	/**
	 * 测量数据分享
	 */
	public static final int TYPE_RECORD = 2;
	/**
	 * 系统消息
	 */
	public static final int TYPE_NOTIFY = 3;

	public static final String FIELD_NAME_MESSAGE_GROUP_ID = "groupID";
	@DatabaseField
	protected Integer groupID;
	@DatabaseField
	protected Long messageID;
	@DatabaseField
	protected Integer type; // 消息类型
	@DatabaseField
	protected String message;

	@DatabaseField
	protected String linkTitle;
	@DatabaseField
	protected String linkURL;
	@DatabaseField
	protected String linkDescription;
	@DatabaseField
	protected String linkImage;
	@DatabaseField
	protected Long linkTime;
	@DatabaseField
	protected Integer reportType;

	@DatabaseField
	protected String recordType;
	@DatabaseField
	protected String recordValue1;
	@DatabaseField
	protected String recordValue2;
	@DatabaseField
	protected String recordValue3;
	@DatabaseField
	protected String recordResult;
	@DatabaseField
	protected Date recordTime;
	@DatabaseField
	protected Integer recordState;
	@DatabaseField
	protected String recordURL;
	@DatabaseField
	protected int recordUnit;

	@DatabaseField
	protected String notifiedType;
	@DatabaseField
	protected String notifiedName;
	@DatabaseField
	protected Integer notifiedAccountID;
	@DatabaseField
	protected String nickname;
	@DatabaseField
	protected String remark;
	@DatabaseField
	protected String username;

	protected GroupMember bindMember;

	public void setBindMember(GroupMember bindMember) {
		this.bindMember = bindMember;
	}

	public GroupMember getBindMember() {
		return bindMember;
	}

	/**
	 * 获取与消息直接关联的昵称，但与内存中的对象可能不同步，需要手动进行同步。
	 * 
	 * @return
	 */
	public String getNickname() {
		return nickname;
	}

	private void setNickname(String nickname) {
		this.nickname = nickname;
	}

	/**
	 * 获取与消息直接关联的昵备注，但与内存中的对象可能不同步，需要手动进行同步。
	 * 
	 * @return
	 */
	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	/**
	 * 获取与消息直接关联的姓名，但与内存中的对象可能不同步，需要手动进行同步。
	 * 
	 * @return
	 */
	public String getRealName() {
		return username;
	}

	public void setRealName(String realName) {
		this.username = realName;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * @return the messageID
	 */
	public Long getMessageID() {
		return messageID;
	}

	/**
	 * @param messageID
	 *            the messageID to set
	 */
	public void setMessageID(Long messageID) {
		this.messageID = messageID;
	}

	@Override
	public String getAccountDisplayName() {

		String ret = null;
		if (!TextUtils.isEmpty(remark)) {
			ret = remark;
		} else {
			if (!TextUtils.isEmpty(nickname)) {
				ret = nickname;
			} else {
				ret = username;
			}
		}
		if (bindMember != null) {
			if (!TextUtils.isEmpty(bindMember.getRemark())) {
				ret = bindMember.getRemark();
			} else {
				if (!TextUtils.isEmpty(bindMember.getNickname())) {
					ret = bindMember.getNickname();
				} else {
					ret = bindMember.getRealName();
				}
			}
		}
		return ret;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public String getLinkTitle() {
		return linkTitle;
	}

	public void setLinkTitle(String linkTitle) {
		this.linkTitle = linkTitle;
	}

	public String getLinkURL() {
		return linkURL;
	}

	public void setLinkURL(String linkURL) {
		this.linkURL = linkURL;
	}

	public String getLinkDescription() {
		return linkDescription;
	}

	public void setLinkDescription(String linkDescription) {
		this.linkDescription = linkDescription;
	}

	public String getLinkImage() {
		return linkImage;
	}

	public void setLinkImage(String linkImage) {
		this.linkImage = linkImage;
	}

	public void setLinkTime(long linkTime) {
		this.linkTime = linkTime;
	}

	public Long getLinkTime() {
		return linkTime;
	}

	public String getRecordType() {
		return recordType;
	}

	public void setRecordType(String recordType) {
		this.recordType = recordType;
	}

	public String getRecordValue1() {
		return recordValue1;
	}

	public void setRecordValue1(String recordValue1) {
		this.recordValue1 = recordValue1;
	}

	public String getRecordValue2() {
		return recordValue2;
	}

	public void setRecordValue2(String recordValue2) {
		this.recordValue2 = recordValue2;
	}

	public void setRecordValue3(String recordValue3) {
		this.recordValue3 = recordValue3;
	}

	public String getRecordValue3() {
		return recordValue3;
	}

	public String getRecordResult() {
		return recordResult;
	}

	public void setRecordResult(String recordResult) {
		this.recordResult = recordResult;
	}

	public Date getRecordTime() {
		return recordTime;
	}

	public void setRecordTime(Date recordTime) {
		this.recordTime = recordTime;
	}

	public Integer getRecordState() {
		return recordState;
	}

	public void setRecordState(Integer recordState) {
		this.recordState = recordState;
	}

	public String getRecordURL() {
		return recordURL;
	}

	public void setRecordURL(String recordURL) {
		this.recordURL = recordURL;
	}

	public void setRecordUnit(int recordUnit) {
		this.recordUnit = recordUnit;
	}

	public int getRecordUnit() {
		return recordUnit;
	}

	public String getNotifiedType() {
		return notifiedType;
	}

	public void setNotifiedType(String notifiedType) {
		this.notifiedType = notifiedType;
	}

	public String getNotifiedName() {
		return notifiedName;
	}

	public void setNotifiedName(String notifiedName) {
		this.notifiedName = notifiedName;
	}

	public Integer getNotifiedAccountID() {
		return notifiedAccountID;
	}

	public void setNotifiedAccountID(Integer notifiedAccountID) {
		this.notifiedAccountID = notifiedAccountID;
	}

	public Integer getReportType() {
		return reportType;
	}

	public void setReportType(Integer reportType) {
		this.reportType = reportType;
	}

	public static List<Message> createMessageList(NetworkClientResult res) {
		JSONArray ja = null;
		List<Message> list = null;
		JSONObject jo = res.getResponseResult();
		try {
			ja = jo.getJSONArray("root");
			list = new ArrayList<Message>();
			for (int i = 0; i < ja.length(); i++) {
				Message msg = createMessage((JSONObject) ja.get(i));
				if (msg != null)
					list.add(msg);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return list;
	}

	public static Message createMessage(JSONObject jo) {

		Message msg = new Message();
		return parse(jo, msg);
	}

	public static Message updateMessage(JSONObject jo, Message msg) {
		return parse(jo, msg);
	}

	private static Message parse(JSONObject jo, Message msg) {

		try {
			if (jo.has("sender") && !jo.isNull("sender")) {
				JSONObject o = jo.getJSONObject("sender");
				if (o.has("imagefile") && !o.isNull("imagefile")) {
					msg.setIconUrl(o.getString("imagefile"));
				}
				if (o.has("nickname") && !o.isNull("nickname")) {
					msg.setAccountDisplayName(o.getString("nickname"));
					msg.setNickname(o.getString("nickname"));
				}
				if (o.has("username") && !o.isNull("username")) {
					msg.setRealName(o.getString("username"));
				}
				if (o.has("remark") && !o.isNull("remark")) {
					msg.setRemark(o.getString("remark"));
				}
				if (o.has("syncid") && !o.isNull("syncid")) {
					msg.setAccountID(o.getInt("syncid"));
				}
			}

			if (jo.has("chrono") && !jo.isNull("chrono")) {
				long milliseconds = jo.getLong("chrono") * 1000L;
				msg.setPostTime(new Date(milliseconds));
			}
			if (jo.has("messageid") && !jo.isNull("messageid")) {
				msg.setMessageID(jo.getLong("messageid"));
			}
			if (jo.has("type") && !jo.isNull("type")) {

				int type = jo.getInt("type");
				msg.setType(type);

				JSONObject o = null;
				if (jo.has("data") && !jo.isNull("data")) {
					switch (type) {
					case TYPE_NORMAL:
						msg.setMessage(jo.getString("data"));
						break;
					case TYPE_LINK:
						o = jo.getJSONObject("data");
						if (o.has("title") && !o.isNull("title")) {
							msg.setLinkTitle(o.getString("title"));
						}
						if (o.has("url") && !o.isNull("url")) {
							msg.setLinkURL(o.getString("url"));
							Log.v("TYPE_LINK url:" + o.getString("url"));
						}
						if (o.has("description") && !o.isNull("description")) {
							msg.setLinkDescription(o.getString("description"));
						}
						if (o.has("image") && !o.isNull("image")) {
							msg.setLinkImage(o.getString("image"));
						}
						if (o.has("report_type") && !o.isNull("report_type")) {
							msg.setReportType(o.getInt("report_type"));
						}
						if (o.has("time") && !o.isNull("time")) {
							msg.setLinkTime(o.getLong("time"));
						}
						break;
					case TYPE_RECORD:
						o = jo.getJSONObject("data");
						if (o.has("type") && !o.isNull("type")) {
							msg.setRecordType(o.getString("type"));
						}
						if (o.has("value1") && !o.isNull("value1")) {
							msg.setRecordValue1(o.getString("value1"));
						}
						if (o.has("value2") && !o.isNull("value2")) {
							msg.setRecordValue2(o.getString("value2"));
						}
						if (o.has("value3") && !o.isNull("value3")) {
							msg.setRecordValue3(o.getString("value3"));
						}
						if (o.has("time") && !o.isNull("time")) {
							long milliseconds = o.getLong("time") * 1000L;
							msg.setRecordTime(new Date(milliseconds));
						}
						if (o.has("result") && !o.isNull("result")) {
							msg.setRecordResult(o.getString("result"));
						}
						if (o.has("state") && !o.isNull("state")) {
							msg.setRecordState(o.getInt("state"));
						}
						if (o.has("url") && !o.isNull("url")) {
							msg.setRecordURL(o.getString("url"));
							Log.v("TYPE_RECORD url:" + o.getString("url"));
						}
						if (o.has("unit") && !o.isNull("unit")) {
							msg.setRecordUnit(o.getInt("unit"));
						}
						break;
					case TYPE_NOTIFY:
						o = jo.getJSONObject("data");
						if (o.has("type") && !o.isNull("type")) {
							msg.setNotifiedType(o.getString("type"));
						}
						if (o.has("nickname") && !o.isNull("nickname")) {
							msg.setNotifiedName(o.getString("nickname"));
						}
						if (o.has("syncid") && !o.isNull("syncid")) {
							msg.setNotifiedAccountID(o.getInt("syncid"));
						}
						break;
					default:
						break;
					}
				}
			}
			return msg;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof Account) {
			// 是否推送消息
			Account tmpAccount = (Account) o;
			int tmpID = getIDValue(tmpAccount.getAccountID());
			int curID = getIDValue(this.getAccountID());
			return tmpID == curID;
		}

		return super.equals(o);
	}

}

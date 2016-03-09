/**
 * 
 */
package com.medzone.framework.data.bean.imp;

import java.util.Date;

import com.j256.ormlite.field.DatabaseField;
import com.medzone.framework.data.bean.BasePagingContent;

/**
 * @author ChenJunQi.
 * 
 */
public class MessageSession extends BasePagingContent {
	/**
	 */
	private static final long serialVersionUID = -5095687336183694610L;
	public static final int SESSION_TYPE_PRIVATE = 1;
	public static final int SESSION_TYPE_GROUP = 2;

	public static final String LAST_MESSAGE_SEND_DATE_FIELD_NAME = "lastMessageSendDate";

	@DatabaseField
	protected Integer interlocutorId;
	@DatabaseField
	protected String interlocutorName;
	@DatabaseField
	protected String interlocutorDispalyName;
	@DatabaseField
	protected String interlocutorIconUrlString;

	@DatabaseField
	protected String sessionTitleString;
	@DatabaseField
	protected Date sessionlastUpdateDate; // session last update date
	@DatabaseField
	protected Integer sessionType;
	@DatabaseField
	protected Integer sessionPeopleCount;

	@DatabaseField
	protected Integer lastMessageId;
	@DatabaseField
	protected Integer lastMessageAuthorId;
	@DatabaseField
	protected String lastMessageAuthorName;
	@DatabaseField
	protected String lastMessageAuthorDispalyName;
	@DatabaseField
	protected String lastMessageSummary;
	@DatabaseField
	protected String lastMessageDetails;
	@DatabaseField
	protected Date lastMessageSendDate;

	/**
	 * interlocutorId -1表示为群聊，否则则指定为P2P聊天
	 */
	public int getInterlocutorId() {
		return interlocutorId;
	}

	public void setInterlocutorId(int interlocutorId) {
		this.interlocutorId = interlocutorId;
	}

	public String getInterlocutorName() {
		return interlocutorName;
	}

	public void setInterlocutorName(String interlocutorName) {
		this.interlocutorName = interlocutorName;
	}

	public String getInterlocutorDispalyName() {
		return interlocutorDispalyName;
	}

	public void setInterlocutorDispalyName(String interlocutorDispalyName) {
		this.interlocutorDispalyName = interlocutorDispalyName;
	}

	public String getInterlocutorIconUrlString() {
		return interlocutorIconUrlString;
	}

	public void setInterlocutorIconUrlString(String interlocutorIconUrlString) {
		this.interlocutorIconUrlString = interlocutorIconUrlString;
	}

	public String getSessionTitleString() {
		return sessionTitleString;
	}

	public void setSessionTitleString(String sessionTitleString) {
		this.sessionTitleString = sessionTitleString;
	}

	public Date getSessionlastUpdateDate() {
		return sessionlastUpdateDate;
	}

	public void setSessionlastUpdateDate(Date sessionlastUpdateDate) {
		this.sessionlastUpdateDate = sessionlastUpdateDate;
	}

	public int getSessionType() {
		return sessionType;
	}

	public void setSessionType(int sessionType) {
		this.sessionType = sessionType;
	}

	public int getSessionPeopleCount() {
		return sessionPeopleCount;
	}

	public void setSessionPeopleCount(int sessionPeopleCount) {
		this.sessionPeopleCount = sessionPeopleCount;
	}

	public int getLastMessageId() {
		return lastMessageId;
	}

	public void setLastMessageId(int lastMessageId) {
		this.lastMessageId = lastMessageId;
	}

	public int getLastMessageAuthorId() {
		return lastMessageAuthorId;
	}

	public void setLastMessageAuthorId(int lastMessageAuthorId) {
		this.lastMessageAuthorId = lastMessageAuthorId;
	}

	public String getLastMessageAuthorName() {
		return lastMessageAuthorName;
	}

	public void setLastMessageAuthorName(String lastMessageAuthorName) {
		this.lastMessageAuthorName = lastMessageAuthorName;
	}

	public String getLastMessageAuthorDispalyName() {
		return lastMessageAuthorDispalyName;
	}

	public void setLastMessageAuthorDispalyName(
			String lastMessageAuthorDispalyName) {
		this.lastMessageAuthorDispalyName = lastMessageAuthorDispalyName;
	}

	public String getLastMessageSummary() {
		return lastMessageSummary;
	}

	public void setLastMessageSummary(String lastMessageSummary) {
		this.lastMessageSummary = lastMessageSummary;
	}

	public String getLastMessageDetails() {
		return lastMessageDetails;
	}

	public void setLastMessageDetails(String lastMessageDetails) {
		this.lastMessageDetails = lastMessageDetails;
	}

	public Date getLastMessageSendDate() {
		return lastMessageSendDate;
	}

	public void setLastMessageSendDate(Date lastMessageSendDate) {
		this.lastMessageSendDate = lastMessageSendDate;
	}

}

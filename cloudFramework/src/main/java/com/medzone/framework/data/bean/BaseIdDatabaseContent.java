/**
 * 
 */
package com.medzone.framework.data.bean;

import java.util.Date;

import com.j256.ormlite.field.DatabaseField;

/**
 * @author ChenJunQi.
 * 
 */
public class BaseIdDatabaseContent extends BaseIdSyncDatabaseObject {

	private static final long serialVersionUID = 1L;
	public static final String NAME_FIELD_ACCOUNT_ID = "accountID";
	public static final String NAME_FIELD_ACCOUNT_DISPLAY_NAME = "authorDisplayName";
	public static final String NAME_FIELD_ICON_URL = "iconURL";
	public static final String NAME_FIELD_POST_TIME = "postTime";

	@DatabaseField
	protected Integer accountID;
	@DatabaseField
	protected String accountDisplayName;
	@DatabaseField
	protected String iconUrl;
	@DatabaseField
	protected Date postTime;

	protected boolean isUnRead;

	public boolean isUnRead() {
		return isUnRead;
	}

	public void setUnRead(boolean isUnRead) {
		this.isUnRead = isUnRead;
	}

	public Integer getAccountID() {
		return accountID;
	}

	public void setAccountID(Integer accountID) {
		this.accountID = accountID;
	}

	public String getAccountDisplayName() {
		return accountDisplayName;
	}

	public void setAccountDisplayName(String accountDisplayName) {
		this.accountDisplayName = accountDisplayName;
	}

	public String getIconUrl() {
		return iconUrl;
	}

	public void setIconUrl(String iconUrl) {
		this.iconUrl = iconUrl;
	}

	public Date getPostTime() {
		return postTime;
	}

	public void setPostTime(Date postTime) {
		this.postTime = postTime;
	}

}

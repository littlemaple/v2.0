/**
 * 
 */
package com.medzone.framework.data.bean;

import com.j256.ormlite.field.DatabaseField;

/**
 * @author ChenJunQi.
 * 
 */
public class BaseIdSyncDatabaseObject extends BaseIdDatabaseObject {

	private static final long serialVersionUID = 1L;
	public static final int FLAG_NOT_SYNCHRONIZED = 0;
	public static final int FLAG_SYNCHRONIZED = 1;
	public static final String NAME_FIELD_LAST_SYNC_TIME = "lastSyncTime";

	@DatabaseField
	protected Integer lastSyncTime;

	public int getLastSyncTime() {
		return lastSyncTime;
	}

	public void setLastSyncTime(int lastSyncTime) {
		this.lastSyncTime = lastSyncTime;
	}
}

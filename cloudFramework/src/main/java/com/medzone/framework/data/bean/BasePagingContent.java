package com.medzone.framework.data.bean;

import com.j256.ormlite.field.DatabaseField;

public abstract class BasePagingContent extends BaseIdDatabaseContent {

	private static final long serialVersionUID = 1L;

	public static final String IS_DIVIDER_FIELD_NAME = "isDivider";

	@DatabaseField
	protected boolean isDivider;

	public BasePagingContent() {
		super();
		isDivider = false;
	}

	public boolean isDivider() {
		return isDivider;
	}

	public void setIsDivider(boolean isDivider) {
		this.isDivider = isDivider;
	}
}

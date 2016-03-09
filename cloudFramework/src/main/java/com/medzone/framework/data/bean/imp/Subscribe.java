package com.medzone.framework.data.bean.imp;

import android.graphics.drawable.Drawable;

import com.medzone.framework.data.bean.BaseEntity;

public class Subscribe extends BaseEntity {

	/**
	 */
	private static final long serialVersionUID = -5196406784235589030L;
	private Integer type;
	private String name;
	private String note;

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	private Drawable dIcon;

	public static final Integer CLOCK_TYPE = 0;
	public static final Integer HEALTH_TYPE = 1;
	public static final Integer STORE_TYPE = 2;

	public Drawable getdIcon() {
		return dIcon;
	}

	public void setdIcon(Drawable dIcon) {
		this.dIcon = dIcon;
	}

	private String icon;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

}

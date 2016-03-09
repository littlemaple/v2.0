package com.medzone.framework.data.bean;

import java.io.Serializable;

public abstract class BaseDatabaseObject implements Serializable {

	private static final long serialVersionUID = -7659217601829428938L;
	public static final long INVALID_ID = -1;
	public static final String TAG_FORCE_UPDATE = "update";

	protected String tag;
	protected boolean isInvalidate = false;

	public BaseDatabaseObject() {
		super();
	}

	public boolean isInvalidate() {
		return isInvalidate;
	}

	public void invalidate() {
		this.isInvalidate = true;
	}

	/**
	 * @param tag
	 *            the tag to set
	 */
	public void setTag(String tag) {
		this.tag = tag;
	}

	/**
	 * @return the tag
	 */
	public String getTag() {
		return tag;
	}

	/**
	 * this will judge if the two object are the same record. In general it mean
	 * if they have same PK in db.
	 * 
	 * @param o
	 * @return
	 */
	abstract public boolean isSameRecord(Object o);

	/**
	 * clone from object. Please notice that in general this function only clone
	 * database fields. After clone, this function will set invalidate flag to
	 * true.
	 * 
	 * @param o
	 */
	public void cloneFrom(Object o) {
		invalidate();
	}
}
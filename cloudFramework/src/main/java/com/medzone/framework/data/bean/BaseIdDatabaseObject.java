package com.medzone.framework.data.bean;

import java.util.Random;

import com.j256.ormlite.field.DatabaseField;

public abstract class BaseIdDatabaseObject extends BaseDatabaseObject {

	private static final long serialVersionUID = 1L;

	public static final String FIELD_NAME_ID = "id";

	/**
	 * just flag the generatedId,should't assignment it.
	 */
	@DatabaseField(defaultValue = "0")
	protected long id;

	public BaseIdDatabaseObject() {
		super();
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	/**
	 * this will judge if the two object are the same record. In general it mean
	 * if they have same PK in db.
	 * 
	 * @param o
	 * @return
	 */
	public boolean isSameRecord(Object o) {
		BaseIdDatabaseObject item2 = (BaseIdDatabaseObject) o;
		if (this.getId() != item2.getId()) {
			return false;
		}
		return true;
	}

	@Override
	public void cloneFrom(Object o) {
		BaseIdDatabaseObject item2 = (BaseIdDatabaseObject) o;
		this.setId(item2.getId());
		super.cloneFrom(o);
	}

	protected int getIDValue(Integer id) {
		if (id == null) {
			return new Random().nextInt();
		} else {
			return id.intValue();
		}
	}

}

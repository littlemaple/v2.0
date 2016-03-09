package com.medzone.framework.data.bean.imp;

import com.j256.ormlite.field.DatabaseField;
import com.medzone.framework.data.bean.BaseEntity;

/**
 * 
 * @author ChenJunQi.
 * 
 */
public class Clock extends BaseEntity implements Cloneable {

	/**
	 */
	private static final long serialVersionUID = 9147843619546107034L;

	public static final String NAME_FIELD_ACCOUNT_ID = "accountID";
	public static final String NAME_FIELD_CLOCK_ID = "clockID";
	public static final String NAME_FIELD_TYPE = "type";
	public static final String NAME_FIELD_LABEL = "label";
	public static final String NAME_FIELD_CLOCKTIME = "clockTime";
	public static final String NAME_FIELD_REPETITION = "repetition";
	public static final String NAME_FIELD_SWITCHSTATE = "switchState";

	@DatabaseField
	private Integer accountID;

	@DatabaseField(generatedId = true)
	private Integer clockID;

	@DatabaseField
	private String type;

	@DatabaseField
	private String label;

	@DatabaseField
	private String clockTime;

	@DatabaseField
	private Integer repetition;// blob

	@DatabaseField
	private Boolean switchState;

	// 不重复时设置的时间比当前时间小，过一天设置
	@DatabaseField
	private Boolean isNext;

	public Boolean isNext() {
		return isNext;
	}

	public void setIsNext(Boolean isNext) {
		this.isNext = isNext;
	}

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

	public Integer getClockID() {
		return clockID;
	}

	public void setClockID(Integer clockID) {
		this.clockID = clockID;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getClockTime() {
		return clockTime;
	}

	public void setClockTime(String clockTime) {
		this.clockTime = clockTime;
	}

	public Integer getRepetition() {
		return repetition;
	}

	public void setRepetition(Integer repetition) {
		this.repetition = repetition;
	}

	public Boolean isSwitchState() {
		return switchState;
	}

	public void setSwitchState(Boolean switchState) {
		this.switchState = switchState;
	}

	public boolean isSameRecord(Object o) {
		Clock item2 = (Clock) o;
		if (!this.getClockID().equals(item2.getClockID())
				|| !this.getAccountID().equals(item2.getAccountID())) {
			return false;
		}
		return true;
	}

	@Override
	public void cloneFrom(Object o) {
		Clock item2 = (Clock) o;
		this.setAccountID(item2.getAccountID());
		this.setClockID(item2.getClockID());
		super.cloneFrom(o);
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}
}

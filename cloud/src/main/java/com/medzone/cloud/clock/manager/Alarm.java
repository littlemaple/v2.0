package com.medzone.cloud.clock.manager;

import java.io.Serializable;

public class Alarm implements Cloneable, Serializable {

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

	private Integer accountID;

	private Integer clockID;

	private String type;

	private String label;

	private String clockTime;

	private Integer repetition;// blob

	private Boolean switchState;

	public Alarm() {
	}

	public Alarm(Integer accountID, Integer clockID, String type, String label,
			String clockTime, Integer repetition, Boolean switchState,
			Boolean isNext) {
		super();
		this.accountID = accountID;
		this.clockID = clockID;
		this.type = type;
		this.label = label;
		this.clockTime = clockTime;
		this.repetition = repetition;
		this.switchState = switchState;
		this.isNext = isNext;
	}

	private Boolean isNext;

	public Boolean isNext() {
		return isNext;
	}

	public void setIsNext(Boolean isNext) {
		this.isNext = isNext;
	}

	public Integer getAccountID() {
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
		Alarm item2 = (Alarm) o;
		if (!this.getClockID().equals(item2.getClockID())
				|| !this.getAccountID().equals(item2.getAccountID())) {
			return false;
		}
		return true;
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}
}

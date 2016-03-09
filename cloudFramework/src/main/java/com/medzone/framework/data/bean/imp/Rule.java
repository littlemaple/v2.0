package com.medzone.framework.data.bean.imp;

import com.medzone.framework.data.bean.BasePagingContent;

/**
 * 
 * @author ChenJunQi.
 * 
 */
public class Rule extends BasePagingContent {

	/**
	 */
	private static final long serialVersionUID = 334222206218284435L;

	public static final String NAME_FIELD_TYPE = "type";
	public static final String NAME_FIELD_AGE = "age";
	public static final String NAME_FIELD_BMI = "bmi";
	public static final String NAME_FIELD_SICK = "sick";
	public static final String NAME_FIELD_MIN1 = "min1";
	public static final String NAME_FIELD_MAX1 = "max1";
	public static final String NAME_FIELD_MIN2 = "min2";
	public static final String NAME_FIELD_MAX2 = "max2";
	public static final String NAME_FIELD_STATE = "state";
	public static final String NAME_FIELD_RESULT = "result";

	private Integer type;

	private Float age;

	private Float bmi;

	private Integer sick;

	private Float min1;

	private Float max1;

	private Float min2;

	private Float max2;

	private Integer state;

	private String result;

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public Float getAge() {
		return age;
	}

	public void setAge(Float age) {
		this.age = age;
	}

	public Float getBmi() {
		return bmi;
	}

	public void setBmi(Float bmi) {
		this.bmi = bmi;
	}

	public Integer getSick() {
		return sick;
	}

	public void setSick(Integer sick) {
		this.sick = sick;
	}

	public Float getMin1() {
		return min1;
	}

	public void setMin1(Float min1) {
		this.min1 = min1;
	}

	public Float getMax1() {
		return max1;
	}

	public void setMax1(Float max1) {
		this.max1 = max1;
	}

	public Float getMin2() {
		return min2;
	}

	public void setMin2(Float min2) {
		this.min2 = min2;
	}

	public Float getMax2() {
		return max2;
	}

	public void setMax2(Float max2) {
		this.max2 = max2;
	}

	public Integer getState() {
		return state;
	}

	public void setState(Integer state) {
		this.state = state;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}
}

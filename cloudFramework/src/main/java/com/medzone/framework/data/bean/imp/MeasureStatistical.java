package com.medzone.framework.data.bean.imp;

import com.medzone.framework.data.bean.BaseEntity;

public class MeasureStatistical extends BaseEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3814567119148378917L;
	private String measureSumTimes;
	private String measureMonth;
	private String measureMonthStart;
	private String measureMonthEnd;
	private String measureExceptionTimes;

	public String getMeasureSumTimes() {
		return measureSumTimes;
	}

	public void setMeasureSumTimes(String measureSumTimes) {
		this.measureSumTimes = measureSumTimes;
	}

	public String getMeasureMonth() {
		return measureMonth;
	}

	public void setMeasureMonth(String measureMonth) {
		this.measureMonth = measureMonth;
	}

	public String getMeasureMonthStart() {
		return measureMonthStart;
	}

	public void setMeasureMonthStart(String measureMonthStart) {
		this.measureMonthStart = measureMonthStart;
	}

	public String getMeasureMonthEnd() {
		return measureMonthEnd;
	}

	public void setMeasureMonthEnd(String measureMonthEnd) {
		this.measureMonthEnd = measureMonthEnd;
	}

	public String getMeasureExceptionTimes() {
		return measureExceptionTimes;
	}

	public void setMeasureExceptionTimes(String measureExceptionTimes) {
		this.measureExceptionTimes = measureExceptionTimes;
	}

}

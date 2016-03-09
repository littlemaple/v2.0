package com.medzone.framework.data.navigation;

import java.sql.Date;

public class DateStepable implements Stepable<DateStepable> {
	private Date date;

	public DateStepable() {
		super();
	}

	/**
	 * @param date
	 */
	public DateStepable(Date date) {
		super();
		this.date = date;
	}

	public int compareTo(DateStepable another) {
		if (another == null) {
			return 1;
		}
		return date.compareTo(another.date);
	}

	public Date getDate() {
		return date;
	}

	public static Date getMaxValue() {
		return new Date(Long.MAX_VALUE);
	}

	public static Date getMaxValue(Paging<DateStepable> paging) {
		if (paging != null) {
			if (paging.getMax() != null) {
				return paging.getMax().getDate();
			}
		}
		return getMaxValue();
	}

	public static Date getMinValue() {
		return new Date(0);
	}

	public static Date getMinValue(Paging<DateStepable> paging) {
		if (paging != null) {
			if (paging.getMin() != null) {
				return paging.getMin().getDate();
			}
		}
		return getMinValue();
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public DateStepable stepUp() {
		return new DateStepable(new Date(date.getTime() + 1));
	}

	public DateStepable stepDown() {
		return new DateStepable(new Date(date.getTime() - 1));
	}

}

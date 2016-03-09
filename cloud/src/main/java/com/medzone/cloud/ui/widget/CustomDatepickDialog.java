package com.medzone.cloud.ui.widget;

import java.util.GregorianCalendar;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Build;
import android.widget.DatePicker;

import com.medzone.cloud.CloudApplication;
import com.medzone.framework.util.TimeUtils;

public class CustomDatepickDialog extends DatePickerDialog {

	private GregorianCalendar minDate;

	private long getMinTimeMillis() {
		if (minDate == null) {
			// int minYear = TimeUtils.getMinBirthdayYear();
			minDate = new GregorianCalendar(1900, 0, 1);
		}
		return minDate.getTimeInMillis();
	}

	@SuppressLint("NewApi")
	public CustomDatepickDialog(Context context, OnDateSetListener callBack,
			int year, int monthOfYear, int dayOfMonth) {
		super(context, callBack, year, monthOfYear, dayOfMonth);
		if (CloudApplication.isNewAPI(Build.VERSION_CODES.HONEYCOMB)) {
			this.getDatePicker().setMaxDate(System.currentTimeMillis());
			this.getDatePicker().setMinDate(getMinTimeMillis());
		}
	}

	@Override
	public void onDateChanged(DatePicker view, int year, int month, int day) {

		if (isDateAfter(view)) {
			view.init(TimeUtils.getCurrentYear(), TimeUtils.getCurrentMonth(),
					TimeUtils.getCurrentDay(), this);
		} else if (isDateBefore(view)) {
			view.init(TimeUtils.getMinBirthdayYear(), 0, 1, this);
		}
	}

	private boolean isDateAfter(DatePicker tempView) {
		if (tempView.getYear() > TimeUtils.getCurrentYear()) {
			return true;
		} else
			return false;
	}

	private boolean isDateBefore(DatePicker tempView) {
		if (tempView.getYear() < TimeUtils.getMinBirthdayYear()) {
			return true;
		} else
			return false;
	}
}

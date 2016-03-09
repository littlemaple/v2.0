package com.medzone.cloud.ui.widget;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Build;
import android.widget.DatePicker;

import com.medzone.cloud.CloudApplication;
import com.medzone.cloud.Constants;
import com.medzone.framework.util.TimeUtils;

public class PreborndayDatepickDialog extends DatePickerDialog {

	@SuppressLint("NewApi")
	public PreborndayDatepickDialog(Context context,
			OnDateSetListener callBack, int year, int monthOfYear,
			int dayOfMonth) {
		super(context, callBack, year, monthOfYear, dayOfMonth);
		if (CloudApplication.isNewAPI(Build.VERSION_CODES.HONEYCOMB)) {
			long min = System.currentTimeMillis();
			long max = Constants.millisecondOfDay
					* Constants.PRE_PRODUCTION_PERIOD + min;
			this.getDatePicker().setMaxDate(max);
			this.getDatePicker().setMinDate(min);
		}
	}

	@Override
	public void onDateChanged(DatePicker view, int year, int month, int day) {

		if (isDateBefore(view)) {
			view.init(TimeUtils.getCurrentYear(), TimeUtils.getCurrentMonth(),
					TimeUtils.getCurrentDay(), this);
		}
	}

	@SuppressWarnings("unused")
	private boolean isDateAfter(DatePicker tempView) {
		if (tempView.getYear() > TimeUtils.getCurrentYear()) {
			return true;
		} else
			return false;
	}

	private boolean isDateBefore(DatePicker tempView) {
		if (tempView.getYear() < TimeUtils.getCurrentYear()) {
			return true;
		} else
			return false;
	}

}

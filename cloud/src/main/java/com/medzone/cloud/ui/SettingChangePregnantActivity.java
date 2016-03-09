package com.medzone.cloud.ui;

import java.util.Calendar;
import java.util.Date;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.LayoutParams;
import com.medzone.cloud.CloudApplication;
import com.medzone.cloud.Constants;
import com.medzone.cloud.data.TemporaryData;
import com.medzone.cloud.ui.dialog.error.ErrorDialogUtil;
import com.medzone.framework.data.bean.imp.Account;
import com.medzone.framework.errorcode.ProxyErrorCode;
import com.medzone.framework.util.TimeUtils;
import com.medzone.mcloud.R;
import com.medzone.mcloud.R.drawable;
import com.medzone.mcloud.R.id;
import com.medzone.mcloud.R.string;

public class SettingChangePregnantActivity extends BaseActivity implements
		OnClickListener {

	private LinearLayout llPregnant;
	private TextView tvPregnant;

	private String prebornday;

	@Override
	protected void preLoadData(Bundle savedInstanceState) {
		super.preLoadData(savedInstanceState);
		prebornday = (String) TemporaryData.get(Account.NAME_FIELD_PREGNANT);
	}

	@Override
	protected void preInitUI() {
		initActionBar();
	}

	@Override
	protected void initUI() {
		setContentView(R.layout.activity_setting_change_pregnant_date);
		llPregnant = (LinearLayout) findViewById(id.linear_pregnant);
		tvPregnant = (TextView) findViewById(id.tv_pregnant);
	}

	private void initActionBar() {
		ActionBar actionBar = getSupportActionBar();
		ActionBar.LayoutParams params = new ActionBar.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT,
				Gravity.CENTER);
		View view = LayoutInflater.from(this).inflate(
				R.layout.custom_actionbar_with_image, null);
		TextView title = (TextView) view.findViewById(R.id.actionbar_title);
		title.setText(string.actionbar_title_setting_pregnancy);
		ImageButton leftButton = (ImageButton) view
				.findViewById(id.actionbar_left);
		leftButton.setImageResource(drawable.personalinformationview_ic_cancel);
		leftButton.setOnClickListener(this);

		ImageButton rightButton = (ImageButton) view
				.findViewById(id.actionbar_right);
		rightButton.setImageResource(drawable.personalinformationview_ic_ok);
		rightButton.setOnClickListener(this);
		actionBar.setCustomView(view, params);
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		actionBar.setDisplayShowCustomEnabled(true);
	}

	@Override
	protected void postInitUI() {
		tvPregnant.setText(prebornday);
		llPregnant.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.actionbar_left:
			finish();
			break;
		case R.id.actionbar_right:

			String tmp = tvPregnant.getText().toString();
			if (!TextUtils.isEmpty(tmp)) {
				Date date = TimeUtils.getDate(tmp, TimeUtils.DATE_FORMAT_DATE);
				@SuppressWarnings("deprecation")
				Date curDay = new Date(TimeUtils.getCurrentYear() - 1900,
						TimeUtils.getCurrentMonth(), TimeUtils.getCurrentDay());

				if (date.before(curDay)) {
					ErrorDialogUtil.showErrorToast(
							SettingChangePregnantActivity.this,
							ProxyErrorCode.TYPE_SETTING,
							ProxyErrorCode.LocalError.CODE_13205);
				} else {
					TemporaryData.save(Account.NAME_FIELD_PREGNANT, tmp);
					setResult(RESULT_OK);
					finish();
				}
			} else {
				ErrorDialogUtil.showErrorToast(
						SettingChangePregnantActivity.this,
						ProxyErrorCode.TYPE_SETTING,
						ProxyErrorCode.LocalError.CODE_13204);
			}
			break;
		case id.linear_pregnant:
			showPregnantDialog(tvPregnant);
			break;
		default:
			break;
		}
	}

	@SuppressLint("NewApi")
	private void showPregnantDialog(final TextView tv) {

		Calendar cal = Calendar.getInstance();
		if (prebornday != null) {
			Date birthday = TimeUtils.getDate(prebornday,
					TimeUtils.DATE_FORMAT_DATE);
			cal.setTimeInMillis(birthday.getTime());
		}

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		View content = getLayoutInflater().inflate(R.layout.datepick_content,
				null);
		final DatePicker pick = (DatePicker) content.findViewById(R.id.data);
		if (CloudApplication.isNewAPI(Build.VERSION_CODES.HONEYCOMB)) {
			final long min = System.currentTimeMillis();
			long max = Constants.millisecondOfDay
					* Constants.PRE_PRODUCTION_PERIOD + min;
			pick.init(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH),
					cal.get(Calendar.DAY_OF_MONTH),
					new OnDateChangedListener() {

						@Override
						public void onDateChanged(DatePicker view, int year,
								int monthOfYear, int dayOfMonth) {
							if (view.getCalendarView().getDate() < min) {
								view.init(TimeUtils.getCurrentYear(),
										TimeUtils.getCurrentMonth(),
										TimeUtils.getCurrentDay(), this);
							}
						}
					});
			// TODO 设置的最小时间跟init的时间相同时会出错？
			// pick.setMinDate(min);
			pick.setMaxDate(max);
		}
		builder.setView(content);
		builder.setTitle(R.string.setting_prebornday);
		builder.setPositiveButton(R.string.public_submit,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						int month = pick.getMonth() + 1;
						int day = pick.getDayOfMonth();
						String pregnantDate = pick.getYear() + "-"
								+ (month > 9 ? month : "0" + month) + "-"
								+ (day > 9 ? day : "0" + day);
						tv.setText(pregnantDate);
					}
				});
		builder.setNegativeButton(R.string.public_cancle,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
					}
				});
		builder.create().show();
	}
}

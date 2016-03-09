package com.medzone.cloud.clock;

import java.util.Calendar;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.LayoutParams;
import com.medzone.cloud.clock.manager.Alarm;
import com.medzone.cloud.clock.manager.AlarmManager;
import com.medzone.cloud.data.CurrentAccountManager;
import com.medzone.cloud.data.TemporaryData;
import com.medzone.cloud.data.helper.ClockHelper;
import com.medzone.cloud.ui.BaseActivity;
import com.medzone.mcloud.R;
import com.medzone.mcloud.R.id;

public class AlarmEditActivity extends BaseActivity implements OnClickListener {

	public static final int REQUEST_CODE_REPEATED = 4;
	public static final int REQUEST_CODE_LABEL = 5;

	private TimePicker mTimePicker;
	private LinearLayout llRepeat, llRemark;
	private TextView tvRemark, tvRepeat;
	private int repeat;

	//临时的clock对象
	private Alarm mClock;
	private boolean isNewAlarm;

	@Override
	protected void preLoadData(Bundle savedInstanceState) {
		super.preLoadData(savedInstanceState);
		if (TemporaryData.containsKey(Alarm.class.getName())) {
			Alarm clock = (Alarm) TemporaryData.get(Alarm.class.getName());
			try {
				mClock = (Alarm) clock.clone();
				repeat = mClock.getRepetition();
				isNewAlarm = false;
			} catch (CloneNotSupportedException e) {
				e.printStackTrace();
			}
		} else {
			isNewAlarm = true;
			mClock = new Alarm();
		}
	}

	private void initActionBar() {
		ActionBar actionBar = getSupportActionBar();
		ActionBar.LayoutParams params = new ActionBar.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT,
				Gravity.CENTER);
		View view = LayoutInflater.from(this).inflate(
				R.layout.custom_actionbar_with_image, null);
		TextView title = (TextView) view.findViewById(R.id.actionbar_title);
		title.setText(R.string.clock_remind);
		ImageButton leftButton = (ImageButton) view
				.findViewById(R.id.actionbar_left);
		leftButton
				.setImageResource(R.drawable.personalinformationview_ic_cancel);
		leftButton.setOnClickListener(this);
		ImageButton rightButton = (ImageButton) view
				.findViewById(R.id.actionbar_right);
		rightButton.setImageResource(R.drawable.personalinformationview_ic_ok);
		rightButton.setOnClickListener(this);
		actionBar.setCustomView(view, params);
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		actionBar.setDisplayShowCustomEnabled(true);
	}

	@Override
	protected void initUI() {
		initActionBar();
		setContentView(R.layout.activity_alarm_edit);
		mTimePicker = (TimePicker) findViewById(id.timePicker);
		llRepeat = (LinearLayout) findViewById(id.ll_clock_repeat);
		llRemark = (LinearLayout) findViewById(id.ll_clock_label);

		tvRepeat = (TextView) findViewById(id.tv_clock_repeat);
		tvRemark = (TextView) findViewById(id.tv_clock_label);
	}

	@Override
	protected void postInitUI() {

		if (isNewAlarm) {

			Calendar ca = Calendar.getInstance();
			mTimePicker.setCurrentHour(ca.get(Calendar.HOUR_OF_DAY));
			mTimePicker.setCurrentMinute(ca.get(Calendar.MINUTE));

		} else {

			int[] result = ClockHelper.convertToIntegerTime(mClock
					.getClockTime());
			mTimePicker.setCurrentHour(result[0]);
			mTimePicker.setCurrentMinute(result[1]);
			tvRepeat.setText(ClockHelper.getRepText(this,
					mClock.getRepetition()));
			tvRemark.setText(mClock.getLabel());

		}
		llRemark.setOnClickListener(this);
		llRepeat.setOnClickListener(this);
	}

	private void doAlarmSet() {

		mClock.setAccountID(CurrentAccountManager.getCurAccount()
				.getAccountID());
		mClock.setClockTime(ClockHelper.convertToStringTime(
				mTimePicker.getCurrentHour(), mTimePicker.getCurrentMinute()));
		mClock.setLabel(tvRemark.getText().toString());
		mClock.setRepetition(repeat);
		mClock.setSwitchState(true);
		mClock.setIsNext(true);

		if (isNewAlarm) {
//			ClockCache.getInstance().add(mClock);
			AlarmManager.addAlarm(mClock);
			Intent i = new Intent(this, AlarmService.class);
			i.putExtra(ClockHelper.CURRNET_TIME, mClock.getClockTime());
			i.putExtra(ClockHelper.REPEATITION, mClock.getRepetition());
			i.setAction(AlarmService.ACTION_SET_SHOW_NOTIF);
			startService(i);
		} else {
//			ClockCache.getInstance().addOrUpdate(mClock);
			AlarmManager.updateAlarm(mClock);
			Intent i = new Intent(this, AlarmService.class);
			i.putExtra(ClockHelper.CURRNET_TIME, mClock.getClockTime());
			i.putExtra(ClockHelper.REPEATITION, mClock.getRepetition());
			i.setAction(AlarmService.ACTION_SET_SHOW_NOTIF);
			startService(i);
		}
		// 刷新闹钟列表
		Intent intent = new Intent(AlarmService.ACTION_REFRESH);
		sendBroadcast(intent);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.actionbar_left:
			finish();
			break;
		case R.id.actionbar_right:
			
			doAlarmSet();
			Intent i = new Intent(this, AlarmService.class);
			i.setAction(AlarmService.ACTION_SET_SHOW_NOTIF);
			startService(i);
			setResult(RESULT_OK);
			AlarmEditActivity.this.finish();
			break;
		case id.ll_clock_repeat:
			Intent repeatIntent = new Intent(AlarmEditActivity.this,
					RepeatChooserActivity.class);
			repeatIntent.putExtra(RepeatChooserActivity.TAG_REPEATED,
					mClock.getRepetition());
			startActivityForResult(repeatIntent, REQUEST_CODE_REPEATED);
			break;
		case id.ll_clock_label:
			Intent alarmTagIntent = new Intent(AlarmEditActivity.this,
					AlarmTagActivity.class);
			alarmTagIntent.putExtra(AlarmTagActivity.TAG_LABEL,
					mClock.getLabel());
			startActivityForResult(alarmTagIntent, REQUEST_CODE_LABEL);
			break;
		default:
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		switch (requestCode) {
		case REQUEST_CODE_REPEATED:
			if (resultCode == RESULT_OK) {
				if (intent != null) {
					repeat = intent.getIntExtra(
							RepeatChooserActivity.TAG_REPEATED, 0);
					mClock.setRepetition(repeat);
					tvRepeat.setText(ClockHelper.getRepText(this, repeat));
				}
			}
			break;
		case REQUEST_CODE_LABEL:
			if (resultCode == RESULT_OK) {
				if (intent != null) {
					String label = intent
							.getStringExtra(AlarmTagActivity.TAG_LABEL);
					mClock.setLabel(label);
					tvRemark.setText(label);
				}
			}

			break;
		}
	}

}

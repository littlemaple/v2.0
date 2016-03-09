//package com.medzone.cloud.clock;
//
//import android.app.KeyguardManager;
//import android.app.KeyguardManager.KeyguardLock;
//import android.content.Intent;
//import android.content.res.Configuration;
//import android.view.Gravity;
//import android.view.View;
//import android.view.WindowManager;
//import android.widget.Button;
//import android.widget.LinearLayout;
//import android.widget.TextView;
//
//import com.actionbarsherlock.app.ActionBar.LayoutParams;
//import com.medzone.cloud.data.TemporaryData;
//import com.medzone.cloud.ui.BaseActivity;
//import com.medzone.framework.data.bean.imp.Clock;
//import com.medzone.mcloud.R;
//
//public class SnoozeActivity extends BaseActivity {
//	public static String ACTION_NO_SNOOZE = "no_snooze";
//	public static String EXTRA_CLOSE_ACTIVITY = "close_activity";
//
//	// private boolean mNoSnooze;
//
//	private LinearLayout mLlOuterLayout;
//	private KeyguardLock mKeyguardLock;
//
//	private Clock mClock;
//
//	private TextView tvClockLabel, tvClockTime;
//
//	@Override
//	public void onConfigurationChanged(Configuration newConfig) {
//		setContentView(mLlOuterLayout);
//		super.onConfigurationChanged(newConfig);
//	}
//
//	private void controlPosition() {
//		WindowManager.LayoutParams params = getWindow().getAttributes();
//		params.width = LayoutParams.WRAP_CONTENT;
//		params.height = LayoutParams.WRAP_CONTENT;
//		params.gravity = Gravity.CENTER;
//		getWindow().setAttributes(params);
//	}
//
//	@Override
//	protected void preLoadData() {
//		super.preLoadData();
//		mClock = (Clock) TemporaryData.get(Clock.class.getName());
//	}
//
//	@Override
//	protected void initUI() {
//		Intent i = getIntent();
//		if (i.getBooleanExtra(EXTRA_CLOSE_ACTIVITY, false)) {
//			finish();
//		} else {
//			setContentView(R.layout.alarm_snooze_layout);
//			tvClockLabel = (TextView) findViewById(R.id.tv_clock_label);
//			tvClockTime = (TextView) findViewById(R.id.tv_clock_time);
//			KeyguardManager km = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
//			mKeyguardLock = km.newKeyguardLock("AppAlarm");
//		}
//	}
//
//	@Override
//	protected void postInitUI() {
//		super.postInitUI();
//		controlPosition();
//		mLlOuterLayout = (LinearLayout) findViewById(R.id.sl_root_layout);
//		((Button) findViewById(R.id.sl_btn_dismiss))
//				.setOnClickListener(mBtnDismissOnClick);
//		fillView();
//	}
//
//	public void fillView() {
//		if (mClock != null) {
//			tvClockLabel.setText(mClock.getLabel());
//			tvClockTime.setText(mClock.getClockTime());
//		}
//	}
//
//	@Override
//	protected void onPause() {
//		mKeyguardLock.reenableKeyguard();
//		super.onPause();
//	}
//
//	@Override
//	protected void onResume() {
//		mKeyguardLock.disableKeyguard();
//		super.onResume();
//	}
//
//	private View.OnClickListener mBtnDismissOnClick = new View.OnClickListener() {
//
//		@Override
//		public void onClick(View v) {
//
//			Intent i = new Intent(getBaseContext(), AlarmService.class);
//			i.setAction(AlarmService.ACTION_STOP_ALARM);
//			startService(i);
//			finish();
//		}
//
//	};
//}

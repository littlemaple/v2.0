package com.medzone.cloud.clock;

import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.LayoutParams;
import com.medzone.cloud.cache.ClockCache;
import com.medzone.cloud.clock.manager.Alarm;
import com.medzone.cloud.clock.manager.AlarmManager;
import com.medzone.cloud.data.CurrentAccountManager;
import com.medzone.cloud.ui.BaseActivity;
import com.medzone.cloud.ui.dialog.error.ErrorDialogUtil;
import com.medzone.framework.data.bean.imp.Account;
import com.medzone.framework.errorcode.ProxyCode;
import com.medzone.framework.errorcode.ProxyErrorCode;
import com.medzone.mcloud.R;
import com.medzone.mcloud.R.id;

/**
 * 
 * @author ChenJunQi.
 * 
 *         V1.0定义闹钟为本地事件
 * 
 */
public class AlarmListActivity extends BaseActivity implements OnClickListener {

	public static final int REQUEST_CODE_REFRESH = 1;
	public static final int CLOCK_MAX_NUMBER = 20;

	private ListView lvAlarm;
	private AlarmListAdapter mAdapter;
	private List<Alarm> mList;

	private Account account;

	@Override
	protected void preLoadData(Bundle savedInstanceState) {
		super.preLoadData(savedInstanceState);
		// mList = ClockCache.getInstance().read();
		AlarmManager.initManager(this);
		account = CurrentAccountManager.getCurAccount();
		mList = AlarmManager.ReadAllAlarm(account.getAccountID());
	}

	private void initActionBar() {
		ActionBar actionBar = getSupportActionBar();
		ActionBar.LayoutParams params = new ActionBar.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT,
				Gravity.CENTER);
		View view = LayoutInflater.from(this).inflate(
				R.layout.custom_actionbar_with_image, null);
		TextView title = (TextView) view.findViewById(R.id.actionbar_title);
		title.setOnClickListener(this);
		title.setText(R.string.clock_remind);
		ImageButton leftButton = (ImageButton) view
				.findViewById(R.id.actionbar_left);
		leftButton.setImageResource(R.drawable.public_ic_back);
		leftButton.setOnClickListener(this);
		ImageButton rightButton = (ImageButton) view
				.findViewById(R.id.actionbar_right);
		rightButton.setImageResource(R.drawable.serviceview_ic_add);
		rightButton.setOnClickListener(this);
		actionBar.setCustomView(view, params);
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		actionBar.setDisplayShowCustomEnabled(true);
	}

	@Override
	protected void preInitUI() {
		super.preInitUI();
		initActionBar();
	}

	@Override
	protected void initUI() {
		super.initUI();
		setContentView(R.layout.activity_alarm_main);

		lvAlarm = (ListView) findViewById(id.lv_alarm);
		mAdapter = new AlarmListAdapter(this, mList);
		lvAlarm.setAdapter(mAdapter);
	}

	@Override
	protected void postInitUI() {
		super.postInitUI();
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(AlarmService.ACTION_REFRESH);
		registerReceiver(refreshReceiver, intentFilter);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.actionbar_title:
			mList = AlarmManager.ReadAllAlarm(account.getAccountID());
			mAdapter.setContent(mList);
			break;
		case R.id.actionbar_left:
			stopService(new Intent(this, AlarmService.class));
			this.finish();
			break;
		case R.id.actionbar_right:
			if (AlarmManager.getAllAlarmSize(account.getAccountID()) < CLOCK_MAX_NUMBER)
				JumpToTarget(AlarmEditActivity.class);
			else
				ErrorDialogUtil.showErrorDialog(AlarmListActivity.this,
						ProxyErrorCode.TYPE_SERVICE,
						ProxyCode.LocalError.CODE_14102, true);
			break;
		}
	}

	public void JumpToTarget(Class<?> clz) {
		Intent intent = new Intent(AlarmListActivity.this, clz);
		startActivityForResult(intent, REQUEST_CODE_REFRESH);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		switch (requestCode) {
		case REQUEST_CODE_REFRESH:
			if (resultCode == RESULT_OK) {
				// mList = ClockCache.getInstance().read();
				mList = AlarmManager.ReadAllAlarm(account.getAccountID());
				mAdapter.setContent(mList);
			}
			break;

		default:
			break;
		}

	}

	private BroadcastReceiver refreshReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(AlarmService.ACTION_REFRESH)) {
				// mList = ClockCache.getInstance().read();
				mList = AlarmManager.ReadAllAlarm(account.getAccountID());
				mAdapter.setContent(mList);
			}
		}

	};

	protected void onDestroy() {
		unregisterReceiver(refreshReceiver);
		super.onDestroy();
	}

}
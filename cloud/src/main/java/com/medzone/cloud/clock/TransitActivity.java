package com.medzone.cloud.clock;

import android.content.Intent;
import android.os.Bundle;

import com.medzone.cloud.ui.BaseActivity;

public class TransitActivity extends BaseActivity {

	//TODO 点击通知栏提醒时过渡 后期取代
	@Override
	protected void preLoadData(Bundle savedInstanceState) {
		super.preLoadData(savedInstanceState);
		Intent i = new Intent(getBaseContext(), AlarmService.class);
		i.setAction(AlarmService.ACTION_STOP_ALARM);
		startService(i);
		finish();
	}
}

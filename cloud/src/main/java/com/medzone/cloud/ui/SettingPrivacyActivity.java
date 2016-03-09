package com.medzone.cloud.ui;

import android.content.Intent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.LayoutParams;
import com.medzone.mcloud.R;
import com.medzone.mcloud.R.drawable;
import com.medzone.mcloud.R.id;
import com.medzone.mcloud.R.string;

public class SettingPrivacyActivity extends BaseActivity implements
		OnClickListener {
	private TextView tvTiedInfo, pwdUpdate, measurePermission, viewPermission;

	@Override
	protected void preInitUI() {
		initActionBar();
	}

	@Override
	protected void initUI() {
		setContentView(R.layout.activity_setting_privacy);

		tvTiedInfo = (TextView) this.findViewById(R.id.tv_tied_view);
		pwdUpdate = (TextView) this
				.findViewById(R.id.setting_privacy_password_update);
		measurePermission = (TextView) this
				.findViewById(R.id.setting_privacy_measure_permission);
		viewPermission = (TextView) this
				.findViewById(R.id.setting_privacy_view_permission);
	}

	private void initActionBar() {
		ActionBar actionBar = getSupportActionBar();
		ActionBar.LayoutParams params = new ActionBar.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT,
				Gravity.CENTER);
		View view = LayoutInflater.from(this).inflate(
				R.layout.custom_actionbar_with_image, null);
		TextView title = (TextView) view.findViewById(R.id.actionbar_title);
		title.setText(string.actionbar_title_setting_security_and_privacyg);

		ImageButton leftButton = (ImageButton) view
				.findViewById(id.actionbar_left);
		leftButton.setImageResource(drawable.public_ic_back);
		leftButton.setOnClickListener(this);

		actionBar.setCustomView(view, params);
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		actionBar.setDisplayShowCustomEnabled(true);
	}

	@Override
	protected void postInitUI() {
		tvTiedInfo.setOnClickListener(this);
		pwdUpdate.setOnClickListener(this);
		measurePermission.setOnClickListener(this);
		viewPermission.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_tied_view:
			JumpToTarget(SettingInfoTiedActivity.class);
			break;
		case id.setting_privacy_password_update:
			JumpToTarget(SettingChangePasswordActivity.class);
			break;
		case id.setting_privacy_measure_permission:
			JumpToTarget(SettingMeasurePermissionActivity.class);
			break;
		case id.setting_privacy_view_permission:
			JumpToTarget(SettingViewPermissionActivity.class);
			break;
		case R.id.actionbar_left:
			finish();
			break;
		default:
			break;
		}
	}

	public void JumpToTarget(Class<?> clz) {
		startActivity(new Intent(SettingPrivacyActivity.this, clz));
	}

}

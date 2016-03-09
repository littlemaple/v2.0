package com.medzone.cloud.ui;

import android.content.Intent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.LayoutParams;
import com.medzone.cloud.Constants;
import com.medzone.cloud.util.GeneralUtil;
import com.medzone.mcloud.R;
import com.medzone.mcloud.R.drawable;
import com.medzone.mcloud.R.id;

public class SettingAboutActivity extends BaseActivity implements
		OnClickListener {
	private LinearLayout checkUpdate;
	private TextView tvFeedBack, tvIntroduce, tvUserAgreement;
	private TextView tvCloudVersion;

	@Override
	protected void preInitUI() {
		initActionBar();
	}

	@Override
	protected void initUI() {
		setContentView(R.layout.activity_setting_about);
		checkUpdate = (LinearLayout) findViewById(id.setting_about_check_update);
		tvFeedBack = (TextView) findViewById(id.setting_about_feed_back);
		tvIntroduce = (TextView) findViewById(id.setting_about_mcloud_introduce);
		tvCloudVersion = (TextView) findViewById(id.tv_cloud_version);
		tvUserAgreement = (TextView) findViewById(id.setting_about_user_agreement);
		tvUserAgreement.setOnClickListener(this);
		checkUpdate.setOnClickListener(this);
		tvFeedBack.setOnClickListener(this);
		tvIntroduce.setOnClickListener(this);
	}

	private void initActionBar() {
		ActionBar actionBar = getSupportActionBar();
		ActionBar.LayoutParams params = new ActionBar.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT,
				Gravity.CENTER);
		View view = LayoutInflater.from(this).inflate(
				R.layout.custom_actionbar_with_image, null);
		TextView title = (TextView) view.findViewById(R.id.actionbar_title);
		title.setText(R.string.actionbar_title_setting_cloud_about);
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
		tvCloudVersion.setText(Constants.PUBLISH_DATE);
	}

	boolean isEvenClick = true;

	public void onTest(View view) {

		if (isEvenClick) {
			tvCloudVersion.setText(Constants.PUBLISH_DATE_FOR_TESTOR);
		} else {
			tvCloudVersion.setText(Constants.PUBLISH_DATE);
		}
		isEvenClick = !isEvenClick;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case id.setting_about_check_update:
			GeneralUtil.checkNewVersion(SettingAboutActivity.this);
			break;
		case id.setting_about_feed_back:
			// FeedbackAgent agent = new FeedbackAgent(this);
			// agent.startFeedbackActivity();
			startActivity(new Intent(SettingAboutActivity.this,
					SettingFeedBackActivity.class));
			break;
		case id.setting_about_mcloud_introduce:
			startActivity(new Intent(SettingAboutActivity.this,
					SettingIntroduceActivity.class));
			break;
		case id.setting_about_user_agreement:
			startActivity(new Intent(SettingAboutActivity.this,
					SettingAboutAgreemenActivity.class));
			break;
		case R.id.actionbar_left:
			finish();
			break;
		}
	}

}

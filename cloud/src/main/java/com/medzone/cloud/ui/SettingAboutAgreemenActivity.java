package com.medzone.cloud.ui;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.LayoutParams;
import com.medzone.mcloud.R;
import com.medzone.mcloud.R.drawable;
import com.medzone.mcloud.R.id;

public class SettingAboutAgreemenActivity extends BaseActivity implements
		OnClickListener {

	private WebView webView;

	@Override
	protected void preInitUI() {
		super.preInitUI();
		initActionBar();
	}

	@Override
	protected void initUI() {
		super.initUI();
		setContentView(R.layout.activity_setting_user_agreement);
		webView = (WebView) this.findViewById(R.id.agreement);
	}

	@Override
	protected void postInitUI() {
		super.postInitUI();
		webView.loadUrl("file:///android_asset/agreement.html");
	}

	private void initActionBar() {
		ActionBar actionBar = getSupportActionBar();
		ActionBar.LayoutParams params = new ActionBar.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT,
				Gravity.CENTER);
		View view = LayoutInflater.from(this).inflate(
				R.layout.custom_actionbar_with_image, null);
		TextView title = (TextView) view.findViewById(R.id.actionbar_title);
		title.setText(R.string.actionbar_title_setting_agreement);
		ImageButton leftButton = (ImageButton) view
				.findViewById(id.actionbar_left);
		leftButton.setImageResource(drawable.public_ic_back);
		leftButton.setOnClickListener(this);
		actionBar.setCustomView(view, params);
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		actionBar.setDisplayShowCustomEnabled(true);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.actionbar_left:
			this.finish();
			break;
		}

	}

}

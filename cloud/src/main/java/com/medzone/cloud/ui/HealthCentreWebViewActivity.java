/**
 * 
 */
package com.medzone.cloud.ui;

import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.LayoutParams;
import com.medzone.cloud.Constants;
import com.medzone.cloud.GlobalVars;
import com.medzone.cloud.data.CurrentAccountManager;
import com.medzone.cloud.data.TemporaryData;
import com.medzone.cloud.network.NetworkClient;
import com.medzone.cloud.ui.widget.CloudWebView;
import com.medzone.framework.Log;
import com.medzone.framework.data.bean.imp.Account;
import com.medzone.framework.data.bean.imp.GroupMember;
import com.medzone.mcloud.R;
import com.medzone.mcloud.R.drawable;
import com.medzone.mcloud.R.id;
import com.medzone.mcloud.R.string;

/**
 * @author ChenJunQi.
 * 
 */
public class HealthCentreWebViewActivity extends BaseActivity implements
		OnClickListener {

	private CloudWebView wvPage;
	private int accountID;
	private String url;
	private String title;

	private void initActionBar() {

		ActionBar actionBar = getSupportActionBar();
		ActionBar.LayoutParams params = new ActionBar.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT,
				Gravity.CENTER);
		View view = LayoutInflater.from(this).inflate(
				R.layout.custom_actionbar_with_image, null);
		TextView tvTitle = (TextView) view.findViewById(R.id.actionbar_title);
		tvTitle.setText(title);
		ImageButton leftButton = (ImageButton) view
				.findViewById(id.actionbar_left);
		leftButton.setImageResource(drawable.public_ic_back);
		leftButton.setOnClickListener(this);
		actionBar.setCustomView(view, params);
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		actionBar.setDisplayShowCustomEnabled(true);
	}

	@Override
	protected void preLoadData(Bundle savedInstanceState) {
		super.preLoadData(savedInstanceState);
		title = getResources().getString(string.actionbar_title_healthcentre);
		if (TemporaryData.containsKey(Constants.TEMPORARYDATA_KEY_VIEW_ACCOUNT)) {
			Account otherAccount = (Account) TemporaryData
					.get(Constants.TEMPORARYDATA_KEY_VIEW_ACCOUNT);
			accountID = otherAccount.getAccountID();
		} else if (TemporaryData
				.containsKey(Constants.TEMPORARYDATA_KEY_VIEW_GROUP_MEMBER)) {
			GroupMember otherMember = (GroupMember) TemporaryData
					.get(Constants.TEMPORARYDATA_KEY_VIEW_GROUP_MEMBER);
			accountID = otherMember.getAccountID();
		} else if (TemporaryData.containsKey(Constants.TEMPORARYDATA_KEY_URL)) {
			url = (String) TemporaryData.get(Constants.TEMPORARYDATA_KEY_URL);
			title = (String) TemporaryData
					.get(Constants.TEMPORARYDATA_KEY_TITLE);
			Log.i("group share message webview load url :" + url);
		} else {
			finish();
		}
	};

	@Override
	protected void initUI() {
		initActionBar();
		setContentView(R.layout.activity_health_centre);
		wvPage = (CloudWebView) this.findViewById(id.wvPage);
		if (url == null) {
			url = NetworkClient.getViewDataURL(accountID, CurrentAccountManager
					.getCurAccount().getAccessToken());
			Log.i("healthcentre load url :" + url);
		}

		wvPage.loadUrl(GlobalVars.formatWebSite(url));
	}

	private void onWebViewBackEvent() {
		if (wvPage.canGoBack()) {
			wvPage.goBack();
		} else {
			finish();
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (event.getAction() == KeyEvent.ACTION_DOWN) {
			switch (keyCode) {
			case KeyEvent.KEYCODE_BACK:
				onWebViewBackEvent();
				return true;
			}

		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case id.actionbar_left:
			onWebViewBackEvent();
			break;
		default:
			break;
		}
	}

}

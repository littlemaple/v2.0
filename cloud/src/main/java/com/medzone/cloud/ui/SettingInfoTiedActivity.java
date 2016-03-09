package com.medzone.cloud.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.LayoutParams;
import com.medzone.cloud.CloudImageLoader;
import com.medzone.cloud.data.CurrentAccountManager;
import com.medzone.framework.data.bean.imp.Account;
import com.medzone.framework.view.RoundedImageView;
import com.medzone.mcloud.R;
import com.medzone.mcloud.R.drawable;
import com.medzone.mcloud.R.id;
import com.medzone.mcloud.R.string;

public class SettingInfoTiedActivity extends BaseActivity implements
		OnClickListener {
	private LinearLayout usernameLL, mobileLL, idLL, emailLL, addressLL;
	private TextView usernameTV, mobileTV, idTV, emailTV, addressTV,
			accountIdTV, nicknameTV;
	private RoundedImageView personalIconIV;
	private static final int REQUEST_USERNAME = 1;
	private static final int REQUEST_MOBILE = 2;
	private static final int REQUEST_ID = 3;
	private static final int REQUEST_EMAIL = 4;
	private static final int REQUEST_ADDRESS = 5;
	private Account account;

	@Override
	protected void preLoadData(Bundle savedInstanceState) {
		super.preLoadData(savedInstanceState);
		account = CurrentAccountManager.getCurAccount();
	}

	@Override
	protected void preInitUI() {
		initActionBar();
	}

	@Override
	protected void initUI() {
		setContentView(R.layout.activity_setting_tied_info);
		usernameLL = (LinearLayout) findViewById(id.setting_tied_info_username);
		mobileLL = (LinearLayout) findViewById(id.setting_tied_info_mobile);
		idLL = (LinearLayout) findViewById(id.setting_tied_info_ID);
		emailLL = (LinearLayout) findViewById(id.setting_tied_info_email);
		addressLL = (LinearLayout) findViewById(id.setting_tied_info_address);
		usernameTV = (TextView) findViewById(id.setting_tied_info_usernameTV);
		mobileTV = (TextView) findViewById(id.setting_tied_info_mobileTV);
		idTV = (TextView) findViewById(id.setting_tied_info_IDTV);
		emailTV = (TextView) findViewById(id.setting_tied_info_emailTV);
		addressTV = (TextView) findViewById(id.setting_tied_info_addressTV);
		accountIdTV = (TextView) findViewById(id.setting_tied_info_accountIdTV);
		nicknameTV = (TextView) findViewById(id.tv_personal_nickname);
		personalIconIV = (RoundedImageView) findViewById(id.im_personal_icon);
	}

	private void initActionBar() {
		ActionBar actionBar = getSupportActionBar();
		ActionBar.LayoutParams params = new ActionBar.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT,
				Gravity.CENTER);
		View view = LayoutInflater.from(this).inflate(
				R.layout.custom_actionbar_with_image, null);
		TextView title = (TextView) view.findViewById(R.id.actionbar_title);
		title.setText(string.actionbar_title_setting_bind_information);

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
		usernameLL.setOnClickListener(this);
		mobileLL.setOnClickListener(this);
		idLL.setOnClickListener(this);
		emailLL.setOnClickListener(this);
		addressLL.setOnClickListener(this);
		fillView();

	}

	public void fillView() {
		if (account != null) {
			if (!TextUtils.isEmpty(account.getHeadPortRait()))
				CloudImageLoader
						.getInstance()
						.getImageLoader()
						.displayImage(account.getHeadPortRait(), personalIconIV);
			if (!TextUtils.isEmpty(account.getNickname()))
				nicknameTV.setText(account.getNickname());
			else
				nicknameTV.setText(R.string.not_filled);
			if (account.getAccountID() != Account.INVALID_ID)
				accountIdTV.setText("心云ID：" + account.getAccountID());
			if (!TextUtils.isEmpty(account.getRealName()))
				usernameTV.setText(account.getRealName());
			else
				usernameTV.setText(R.string.not_filled);
			if (!TextUtils.isEmpty(account.getPhone2()))
				mobileTV.setText(account.getPhone2());
			else
				mobileTV.setText(R.string.not_filled);
			if (!TextUtils.isEmpty(account.getIDCard()))
				idTV.setText(account.getIDCard());
			else
				idTV.setText(R.string.not_filled);
			if (!TextUtils.isEmpty(account.getEmail2()))
				emailTV.setText(account.getEmail2());
			else
				emailTV.setText(R.string.not_filled);
			if (!TextUtils.isEmpty(account.getLocation()))
				addressTV.setText(account.getLocation());
			else
				addressTV.setText(R.string.not_filled);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (data != null) {
			String input = data.getStringExtra("data");
			switch (requestCode) {
			case REQUEST_USERNAME:
				switch (resultCode) {
				case SettingUpdateInfoTiedActivity.RESULT_SUCCESS:
					usernameTV.setText(input);
					break;
				case SettingUpdateInfoTiedActivity.RESULT_FAILED:
					break;
				default:
					break;
				}
				break;
			case REQUEST_EMAIL:
				switch (resultCode) {
				case SettingUpdateInfoTiedActivity.RESULT_SUCCESS:
					emailTV.setText(input);
					break;
				case SettingUpdateInfoTiedActivity.RESULT_FAILED:
					break;
				default:
					break;
				}
				break;
			case REQUEST_ID:
				switch (resultCode) {
				case SettingUpdateInfoTiedActivity.RESULT_SUCCESS:
					idTV.setText(input);
					break;
				case SettingUpdateInfoTiedActivity.RESULT_FAILED:
					break;
				default:
					break;
				}
				break;
			case REQUEST_MOBILE:
				switch (resultCode) {
				case SettingUpdateInfoTiedActivity.RESULT_SUCCESS:
					mobileTV.setText(input);
					break;
				case SettingUpdateInfoTiedActivity.RESULT_FAILED:
					break;
				default:
					break;
				}
				break;
			case REQUEST_ADDRESS:
				switch (resultCode) {
				case SettingUpdateInfoTiedActivity.RESULT_SUCCESS:
					String location = data
							.getStringExtra(Account.NAME_FIELD_LOCATION);
					addressTV.setText(location);
					break;
				case SettingUpdateInfoTiedActivity.RESULT_FAILED:
					break;
				default:
					break;
				}
				break;
			default:
				break;
			}
		}
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.actionbar_left:
			finish();
			break;
		case R.id.setting_tied_info_username:
			Intent intent = new Intent(this,
					SettingUpdateInfoTiedActivity.class);
			intent.putExtra(SettingUpdateInfoTiedActivity.KEY,
					Account.NAME_FIELD_REALNAME);
			startActivityForResult(intent, REQUEST_USERNAME);
			break;
		case R.id.setting_tied_info_email:
			intent = new Intent(this, SettingUpdateInfoTiedActivity.class);
			intent.putExtra(SettingUpdateInfoTiedActivity.KEY,
					Account.NAME_FIELD_EMAIL2);
			startActivityForResult(intent, REQUEST_EMAIL);
			break;
		case R.id.setting_tied_info_mobile:
			intent = new Intent(this, SettingUpdateInfoTiedActivity.class);
			intent.putExtra(SettingUpdateInfoTiedActivity.KEY,
					Account.NAME_FIELD_PHONE2);
			startActivityForResult(intent, REQUEST_MOBILE);
			break;
		case R.id.setting_tied_info_ID:
			intent = new Intent(this, SettingUpdateInfoTiedActivity.class);
			intent.putExtra(SettingUpdateInfoTiedActivity.KEY,
					Account.NAME_FIELD_IDCARD);
			startActivityForResult(intent, REQUEST_ID);
			break;
		case R.id.setting_tied_info_address:
			intent = new Intent(this, SettingChangeAddressActivity.class);
			startActivityForResult(intent, REQUEST_ADDRESS);
			break;
		}
	}

}

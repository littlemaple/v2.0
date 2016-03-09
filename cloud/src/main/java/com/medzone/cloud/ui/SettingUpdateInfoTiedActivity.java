package com.medzone.cloud.ui;

import android.content.Intent;
import android.text.InputFilter;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.LayoutParams;
import com.medzone.cloud.Constants;
import com.medzone.cloud.data.CurrentAccountManager;
import com.medzone.cloud.data.helper.AccountHelper;
import com.medzone.cloud.ui.dialog.error.ErrorDialogUtil;
import com.medzone.cloud.ui.widget.CleanableEditText;
import com.medzone.framework.data.bean.imp.Account;
import com.medzone.framework.errorcode.ProxyCode.LocalError;
import com.medzone.framework.errorcode.ProxyErrorCode;
import com.medzone.framework.network.NetworkClientResult;
import com.medzone.framework.task.BaseResult;
import com.medzone.framework.task.TaskHost;
import com.medzone.mcloud.R;
import com.medzone.mcloud.R.drawable;
import com.medzone.mcloud.R.id;
import com.medzone.mcloud.R.integer;

public class SettingUpdateInfoTiedActivity extends BaseActivity implements
		OnClickListener {

	public static final int RESULT_SUCCESS = 1;
	public static final int RESULT_FAILED = 0;
	public static final String KEY = "key";

	private TextView tvHint;
	private CleanableEditText mCleanableEditText;

	private String type, title;
	private Account account;
	// 临时的account对象
	private Account temp;

	private void initActionBar() {
		ActionBar actionBar = getSupportActionBar();
		ActionBar.LayoutParams params = new ActionBar.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT,
				Gravity.CENTER);
		View view = LayoutInflater.from(this).inflate(
				R.layout.custom_actionbar_with_image, null);
		TextView titleTV = (TextView) view.findViewById(R.id.actionbar_title);
		titleTV.setText(title);

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
	protected void preInitUI() {
		account = CurrentAccountManager.getCurAccount();
		type = getIntent().getStringExtra(KEY);
		if (type.equals(Account.NAME_FIELD_REALNAME)) {
			title = getResources().getString(R.string.name);
		} else if (type.equals(Account.NAME_FIELD_EMAIL2)) {
			title = getResources().getString(R.string.email);
		} else if (type.equals(Account.NAME_FIELD_IDCARD)) {
			title = getResources().getString(R.string.id_card);
		} else if (type.equals(Account.NAME_FIELD_PHONE2)) {
			title = getResources().getString(R.string.phone_number);
		} else if (type.equals(Account.NAME_FIELD_LOCATION)) {
			title = getResources().getString(R.string.address);
		}
		initActionBar();
	}

	@Override
	protected void initUI() {
		setContentView(R.layout.activity_setting_update_tied_info);
		tvHint = (TextView) findViewById(id.setting_update_tied_info_hint);
		mCleanableEditText = (CleanableEditText) findViewById(id.ce_setting_update_tied_info_input);

		if (type.equals(Account.NAME_FIELD_REALNAME)) {
			initRealNameView();
		} else if (type.equals(Account.NAME_FIELD_EMAIL2)) {
			initEmailView();
		} else if (type.equals(Account.NAME_FIELD_IDCARD)) {
			initIDcardView();
		} else if (type.equals(Account.NAME_FIELD_PHONE2)) {
			initPhoneView();
		} else if (type.equals(Account.NAME_FIELD_LOCATION)) {
			initLocationView();
		}
	}

	private void initRealNameView() {
		tvHint.setText(R.string.please_input_name);
		mCleanableEditText.setMaxBytes(getResources().getInteger(
				integer.limit_realname));
		mCleanableEditText
				.setFilters(new InputFilter[] { new InputFilter.LengthFilter(
						Constants.MAX_EMS_NAME) });
		mCleanableEditText
				.setInputType(EditorInfo.TYPE_TEXT_VARIATION_PERSON_NAME);
		mCleanableEditText.setText(account.getRealName());
		if (account.getRealName() != null) {
			mCleanableEditText.setSelection(account.getRealName().length());
		}
	}

	private void initEmailView() {
		tvHint.setText(R.string.please_input_email);
		mCleanableEditText.setMaxBytes(getResources().getInteger(
				integer.limit_email));
		mCleanableEditText
				.setFilters(new InputFilter[] { new InputFilter.LengthFilter(
						Constants.MAX_EMS_EMAIL) });
		mCleanableEditText
				.setInputType(EditorInfo.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
		mCleanableEditText.setText(account.getEmail2());
		if (account.getEmail2() != null) {
			mCleanableEditText.setSelection(account.getEmail2().length());
		}
	}

	private void initIDcardView() {
		tvHint.setText(R.string.please_input_idcard);
		mCleanableEditText.setMaxBytes(getResources().getInteger(
				integer.limit_id_card));
		mCleanableEditText
				.setFilters(new InputFilter[] { new InputFilter.LengthFilter(
						Constants.MAX_EMS_IDCARD) });
		mCleanableEditText.setText(account.getIDCard());
		if (account.getIDCard() != null) {
			mCleanableEditText.setSelection(account.getIDCard().length());
		}
	}

	private void initPhoneView() {
		tvHint.setText(R.string.please_input_phone);
		mCleanableEditText.setMaxBytes(getResources().getInteger(
				integer.limit_phone));
		mCleanableEditText
				.setFilters(new InputFilter[] { new InputFilter.LengthFilter(
						Constants.MAX_EMS_PHONE) });
		mCleanableEditText.setInputType(EditorInfo.TYPE_CLASS_PHONE);
		mCleanableEditText.setText(account.getPhone2());
		if (account.getPhone2() != null) {
			mCleanableEditText.setSelection(account.getPhone2().length());
		}
	}

	private void initLocationView() {
		tvHint.setText(R.string.please_input_address);
		mCleanableEditText.setMaxBytes(getResources().getInteger(
				integer.limit_address));
		mCleanableEditText
				.setFilters(new InputFilter[] { new InputFilter.LengthFilter(
						Constants.MAX_EMS_ADDRESS) });
		if (account.getLocation() != null) {
			mCleanableEditText.setText("" + account.getLocation());
		}
	}

	@Override
	protected void postInitUI() {
		tvHint.setOnClickListener(this);
		mCleanableEditText.setOnClickListener(this);
		mCleanableEditText
				.setOnEditorActionListener(new OnEditorActionListener() {

					@Override
					public boolean onEditorAction(TextView v, int actionId,
							KeyEvent event) {
						if (actionId == EditorInfo.IME_ACTION_GO) {
							updateInfoOperator();
						}
						return false;
					}
				});
	}

	private void updateInfoOperator() {
		String result = mCleanableEditText.getText().toString()/*.trim()*/;
		try {
			temp = (Account) account.clone();
			if (type.equals(Account.NAME_FIELD_REALNAME)) {

				// 校验姓名
				int errorCode = AccountHelper.checkRealNameStyle(result);
				if (errorCode == LocalError.CODE_SUCCESS) {
					temp.setRealName(result);
				} else {
					ErrorDialogUtil.showErrorDialog(
							SettingUpdateInfoTiedActivity.this,
							ProxyErrorCode.TYPE_SETTING, errorCode, true);
					return;
				}

			} else if (type.equals(Account.NAME_FIELD_EMAIL2)) {

				// 校验邮箱2
				int errorCode = AccountHelper.checkInfoBindEmail2Style(result);
				if (errorCode == LocalError.CODE_SUCCESS) {
					temp.setEmail2(result);
				} else {
					ErrorDialogUtil.showErrorDialog(
							SettingUpdateInfoTiedActivity.this,
							ProxyErrorCode.TYPE_SETTING, errorCode, true);
					return;
				}

			} else if (type.equals(Account.NAME_FIELD_IDCARD)) {
				// 校验ID卡
				int errorCode = AccountHelper.checkIDCardStyle(result);
				if (errorCode == LocalError.CODE_SUCCESS) {
					temp.setIDCard(result);
				} else {
					ErrorDialogUtil.showErrorDialog(
							SettingUpdateInfoTiedActivity.this,
							ProxyErrorCode.TYPE_SETTING, errorCode, true);
					return;
				}

				temp.setIDCard(result);
			} else if (type.equals(Account.NAME_FIELD_PHONE2)) {
				// 校验手机号2
				int errorCode = AccountHelper.checkInfoBindPhone2Style(result);
				if (errorCode == LocalError.CODE_SUCCESS) {
					temp.setPhone2(result);
				} else {
					ErrorDialogUtil.showErrorDialog(
							SettingUpdateInfoTiedActivity.this,
							ProxyErrorCode.TYPE_SETTING, errorCode, true);
					return;
				}

			} else if (type.equals(Account.NAME_FIELD_LOCATION)) {
				// TODO 启用地址的正则判断
				temp.setLocation(result);
			}
			doUpdateAccount(temp);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.actionbar_left:
			finish();
			break;
		case R.id.actionbar_right:
			updateInfoOperator();
			break;
		default:
			break;
		}
	}

	private void doUpdateAccount(final Account account) {
		AccountHelper.doUpdateAccountTask(this, account, new TaskHost() {
			@Override
			public void onPostExecute(int requestCode, BaseResult result) {
				super.onPostExecute(requestCode, result);
				if (result.isSuccess()) {
					NetworkClientResult res = (NetworkClientResult) result;
					if (res.isServerDisposeSuccess()) {
						Intent data = new Intent();
						data.putExtra("data", mCleanableEditText.getText()
								.toString().trim());
						setResult(RESULT_SUCCESS, data);
						finish();
						updateAccount();
					} else {
						ErrorDialogUtil.showErrorDialog(
								SettingUpdateInfoTiedActivity.this,
								ProxyErrorCode.TYPE_SETTING,
								result.getErrorCode(), true);
					}
				}
			}
		});
	}

	public void updateAccount() {
		CurrentAccountManager.getCurAccount().setAddress(temp.getAddress());
		CurrentAccountManager.getCurAccount().setEmail2(temp.getEmail2());
		CurrentAccountManager.getCurAccount().setPhone2(temp.getPhone2());
		CurrentAccountManager.getCurAccount().setLocation(temp.getLocation());
		CurrentAccountManager.getCurAccount().setIDCard(temp.getIDCard());
		CurrentAccountManager.getCurAccount().setRealName(temp.getRealName());
	}

}

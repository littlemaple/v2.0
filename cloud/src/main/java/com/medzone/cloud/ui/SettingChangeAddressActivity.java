package com.medzone.cloud.ui;

import android.content.Intent;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.LayoutParams;
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

public class SettingChangeAddressActivity extends BaseActivity implements
		OnClickListener {

	public static final int RESULT_SUCCESS = 1;
	public static final int RESULT_FAILED = 0;
	private static final int REQUEST_CODE = 2;

	private EditText etLocationInput;
	private CleanableEditText etAddressInput;

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
		titleTV.setText(R.string.actionbar_title_setting_address);

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
		initActionBar();
	}

	@Override
	protected void initUI() {
		setContentView(R.layout.activity_setting_change_address);
		etLocationInput = (EditText) findViewById(id.edit_location);
		etAddressInput = (CleanableEditText) findViewById(R.id.ce_edit_address);
	}

	private void fillView() {
		// etInput.setFilters(new InputFilter[] { new InputFilter.LengthFilter(
		// Constants.MAX_EMS_ADDRESS) });
		if (account.getLocation() != null) {
			etLocationInput.setText("" + account.getLocation());
		}
		if (account.getAddress() != null) {
			etAddressInput.setText("" + account.getAddress());
		}
	}

	@Override
	protected void postInitUI() {
		fillView();
		etLocationInput.setOnClickListener(this);
		etAddressInput.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_ENTER
						&& event.getAction() == KeyEvent.ACTION_DOWN) {
					doUpdateAddress();
					return true;
				}
				return false;
			}
		});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.actionbar_left:
			finish();
			break;
		case R.id.actionbar_right:
			doUpdateAddress();
			break;
		case R.id.edit_location:
			startActivityForResult(new Intent(
					SettingChangeAddressActivity.this,
					SettingSelectCityActivity.class), REQUEST_CODE);
			break;
		default:
			break;
		}
	}

	private void doUpdateAddress() {
		String location = etLocationInput.getText().toString().trim();
		String address = etAddressInput.getText().toString().trim();

		try {
			temp = (Account) account.clone();

			final int errorCode = AccountHelper.checkInfoBindAddressStyle(
					location, address);

			if (errorCode == LocalError.CODE_SUCCESS) {
				temp.setLocation(location);
				temp.setAddress(address);
				doUpdateAccount(temp);
			} else {
				ErrorDialogUtil.showErrorDialog(
						SettingChangeAddressActivity.this,
						ProxyErrorCode.TYPE_SETTING, errorCode, true);
			}
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		if (intent != null) {
			etLocationInput.setText(intent
					.getStringExtra(Account.NAME_FIELD_LOCATION));
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
						Intent intent = new Intent();
						intent.putExtra(Account.NAME_FIELD_LOCATION,
								etLocationInput.getText().toString());
						setResult(RESULT_SUCCESS, intent);
						finish();
						updateAccount();
					} else {
						ErrorDialogUtil.showErrorDialog(
								SettingChangeAddressActivity.this,
								ProxyErrorCode.TYPE_SETTING,
								result.getErrorCode(), true);
					}
				} else {
					ErrorDialogUtil.showErrorToast(
							SettingChangeAddressActivity.this,
							ProxyErrorCode.TYPE_SETTING,
							ProxyErrorCode.LocalError.CODE_10002);
				}
			}
		});
	}

	public void updateAccount() {
		CurrentAccountManager.getCurAccount().setAddress(temp.getAddress());
		CurrentAccountManager.getCurAccount().setLocation(temp.getLocation());
	}

}

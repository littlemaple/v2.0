package com.medzone.cloud.ui;

import android.os.Bundle;
import android.text.TextUtils;
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
import com.medzone.cloud.data.CurrentAccountManager;
import com.medzone.cloud.data.helper.AccountHelper;
import com.medzone.cloud.ui.dialog.error.ErrorDialogUtil;
import com.medzone.cloud.ui.widget.CleanableEditText;
import com.medzone.framework.data.bean.imp.Account;
import com.medzone.framework.errorcode.ProxyCode;
import com.medzone.framework.errorcode.ProxyCode.LocalError;
import com.medzone.framework.errorcode.ProxyErrorCode;
import com.medzone.framework.network.NetworkClientResult;
import com.medzone.framework.task.BaseResult;
import com.medzone.framework.task.TaskHost;
import com.medzone.mcloud.R;
import com.medzone.mcloud.R.drawable;
import com.medzone.mcloud.R.id;
import com.medzone.mcloud.R.string;

public class SettingChangePasswordActivity extends BaseActivity implements
		OnClickListener {

	private CleanableEditText oldET, newET;
	private Account curAccount;

	@Override
	protected void preLoadData(Bundle savedInstanceState) {
		super.preLoadData(savedInstanceState);
		curAccount = CurrentAccountManager.getCurAccount();
	}

	@Override
	protected void preInitUI() {
		initActionBar();
	}

	@Override
	protected void initUI() {
		setContentView(R.layout.activity_setting_change_password);
		oldET = (CleanableEditText) findViewById(id.ce_setting_change_password_old);
		newET = (CleanableEditText) findViewById(id.ce_setting_change_password_new);
	}

	private void initActionBar() {
		ActionBar actionBar = getSupportActionBar();
		ActionBar.LayoutParams params = new ActionBar.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT,
				Gravity.CENTER);
		View view = LayoutInflater.from(this).inflate(
				R.layout.custom_actionbar_with_image, null);
		TextView title = (TextView) view.findViewById(R.id.actionbar_title);
		title.setText(string.actionbar_title_setting_password_changed);
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
	protected void postInitUI() {
		// TODO Auto-generated method stub
		super.postInitUI();
		newET.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				// TODO Auto-generated method stub
				if (actionId == EditorInfo.IME_ACTION_GO) {
					resetPassword();
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
			resetPassword();
			break;
		default:
			break;
		}
	}

	private void resetPassword() {
		String oldPWD = oldET.getText().toString();
		String newPWD = newET.getText().toString();
		if (TextUtils.isEmpty(oldPWD)) {
			ErrorDialogUtil.showErrorDialog(SettingChangePasswordActivity.this,
					ProxyErrorCode.TYPE_SETTING,
					ProxyCode.LocalError.CODE_13201, true);
		} else {

			int errorCode = AccountHelper.checkChangePasswordStyle(oldPWD,
					newPWD);
			if (errorCode == LocalError.CODE_SUCCESS) {
				Account temp;
				try {
					temp = (Account) curAccount.clone();
					temp.setPassword(newPWD);
					doUpdateAccount(temp);

				} catch (CloneNotSupportedException e) {
					e.printStackTrace();
				}
			} else {
				ErrorDialogUtil.showErrorDialog(
						SettingChangePasswordActivity.this,
						ProxyErrorCode.TYPE_SETTING, errorCode, true);
			}
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
						curAccount.setPassword(account.getPassword());
						ErrorDialogUtil.showErrorDialog(
								SettingChangePasswordActivity.this,
								ProxyErrorCode.TYPE_SETTING,
								LocalError.CODE_RESET_NEW_PASSWORD_SUCCESS,
								true);
						finish();
					} else {
						ErrorDialogUtil.showErrorDialog(
								SettingChangePasswordActivity.this,
								ProxyErrorCode.TYPE_SETTING,
								result.getErrorCode(), true);
					}
				} else {
					ErrorDialogUtil.showErrorToast(
							SettingChangePasswordActivity.this,
							ProxyErrorCode.TYPE_SETTING,
							ProxyErrorCode.LocalError.CODE_10002);
				}
			}
		});
	}

}

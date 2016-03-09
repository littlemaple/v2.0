package com.medzone.cloud.ui;

import android.content.Intent;
import android.os.Handler;
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
import com.medzone.cloud.data.helper.AccountHelper;
import com.medzone.cloud.task.ResetPwdTask;
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

public class ResetPwdActivity extends BaseActivity implements OnClickListener {

	private CleanableEditText newPasswordEdit;
	private String accountName, accountPass, checkCode;

	@Override
	protected void preInitUI() {
		super.preInitUI();

		ActionBar actionBar = getSupportActionBar();
		ActionBar.LayoutParams params = new ActionBar.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT,
				Gravity.CENTER);
		View view = LayoutInflater.from(this).inflate(
				R.layout.custom_actionbar_with_image, null);
		TextView title = (TextView) view.findViewById(R.id.actionbar_title);
		ImageButton leftButton = (ImageButton) view
				.findViewById(id.actionbar_left);
		leftButton.setImageResource(drawable.public_ic_back);
		leftButton.setOnClickListener(this);
		title.setText(R.string.action_title_reset_password);
		actionBar.setCustomView(view, params);
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		actionBar.setDisplayShowCustomEnabled(true);
	}

	@Override
	protected void initUI() {
		setContentView(R.layout.activity_resetpwd);
		newPasswordEdit = (CleanableEditText) this
				.findViewById(R.id.ce_edit_password);
	}

	@Override
	protected void postInitUI() {
		findViewById(R.id.subResetPwd).setOnClickListener(this);
		newPasswordEdit.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_GO) {
					resetPassword();
				}
				return false;
			}
		});
	}

	public void doResetPasswordTask() {

		ResetPwdTask task = new ResetPwdTask(this, accountName, checkCode,
				accountPass);
		task.setTaskHost(new TaskHost() {
			@Override
			public void onPostExecute(int requestCode, BaseResult result) {
				super.onPostExecute(requestCode, result);
				if (result.isSuccess()) {
					NetworkClientResult res = (NetworkClientResult) result;
					if (res.isServerDisposeSuccess()) {
						ErrorDialogUtil.showErrorDialog(ResetPwdActivity.this,
								ProxyErrorCode.TYPE_LOGIN_AND_REGISTER,
								ProxyErrorCode.LocalError.CODE_10307, true);
						handler.sendEmptyMessageDelayed(1, 2000);
					} else {
						ErrorDialogUtil.showErrorDialog(ResetPwdActivity.this,
								ProxyErrorCode.TYPE_LOGIN_AND_REGISTER,
								res.getErrorCode(), true);
					}
				}
			}
		});
		task.execute();
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.subResetPwd: {
			resetPassword();
			break;
		}
		case R.id.actionbar_left:
			this.finish();
			break;
		}
	}

	private void resetPassword() {
		int errorCode = -1;
		accountPass = newPasswordEdit.getText().toString();
		Intent intent = getIntent();
		accountName = intent.getStringExtra(Account.NAME_FIELD_TARGET);
		checkCode = intent.getStringExtra(Account.NAME_FIELD_CODE);

		errorCode = AccountHelper.checkResetParamsStyle(accountName,
				accountPass, checkCode);
		if (errorCode == LocalError.CODE_SUCCESS) {
			doResetPasswordTask();
		} else {
			ErrorDialogUtil.showErrorDialog(ResetPwdActivity.this,
					ProxyErrorCode.TYPE_LOGIN_AND_REGISTER, errorCode, true);
		}
	}

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {

			switch (msg.what) {
			case 1:
				jumpLoginActivity();
				break;
			}
		}
	};

	public void jumpLoginActivity() {
		startActivity(new Intent(ResetPwdActivity.this, LoginActivity.class));
		this.finish();
	}

}

package com.medzone.cloud.ui;

import android.content.Intent;
import android.os.Handler;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.LayoutParams;
import com.medzone.cloud.CloudApplication;
import com.medzone.cloud.Constants;
import com.medzone.cloud.data.helper.AccountHelper;
import com.medzone.cloud.ui.dialog.error.ErrorDialogUtil;
import com.medzone.cloud.ui.widget.CleanableEditText;
import com.medzone.framework.data.bean.imp.Account;
import com.medzone.framework.errorcode.ProxyCode;
import com.medzone.framework.errorcode.ProxyCode.LocalError;
import com.medzone.framework.errorcode.ProxyCode.NetError;
import com.medzone.framework.errorcode.ProxyErrorCode;
import com.medzone.framework.network.NetworkClientResult;
import com.medzone.framework.task.BaseResult;
import com.medzone.framework.task.TaskHost;
import com.medzone.mcloud.R;
import com.medzone.mcloud.R.drawable;
import com.medzone.mcloud.R.id;

public class ForgetPwdActivity extends BaseActivity implements OnClickListener {

	private CleanableEditText accountNameEdit, checkCodeEdit;
	private String accountName, checkCode;

	private int sendCount = Constants.CODE_DELAY_COUNT;
	private Runnable runnable;
	private Handler handler = new Handler();

	private Button btnSendCode;
	private Button btnSubReset;

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
		title.setText(R.string.action_title_forget_password);
		actionBar.setCustomView(view, params);
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		actionBar.setDisplayShowCustomEnabled(true);
	}

	@Override
	protected void initUI() {
		setContentView(R.layout.activity_forgetpwd);

		accountNameEdit = (CleanableEditText) this
				.findViewById(R.id.ce_edit_account);
		checkCodeEdit = (CleanableEditText) this.findViewById(R.id.edit_code);
		btnSendCode = (Button) this.findViewById(R.id.button_send_code);
		btnSubReset = (Button) this.findViewById(R.id.subResetPwd);

	}

	@Override
	protected void postInitUI() {
		btnSendCode.setOnClickListener(this);
		btnSubReset.setOnClickListener(this);

		checkCodeEdit.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView arg0, int arg1, KeyEvent arg2) {
				if (arg1 == EditorInfo.IME_ACTION_GO) {
					forgetMethod();
				}
				return false;
			}
		});

		createRunnable();
	}

	public void createRunnable() {
		runnable = new Runnable() {
			@Override
			public void run() {
				btnSendCode.setText(--sendCount + "s");
				btnSendCode.setEnabled(false);
				handler.postDelayed(runnable, 1000);
				if (sendCount == 0) {
					restoreReSendCode();
				}

			}
		};
	}

	// public void restoreSendCode() {
	// handler.removeCallbacks(runnable);
	// btnSendCode.setText(R.string.checkcode_get);
	// accountName = accountNameEdit.getText().toString();
	// if (AccountHelper.isPhoneCorrect(accountName)
	// || AccountHelper.isEmailCorrect(accountName)) {
	// btnSendCode.setEnabled(true);
	// } else {
	// btnSendCode.setEnabled(false);
	// }
	// sendCount = Constants.CODE_DELAY_COUNT;
	// }

	public void restoreReSendCode() {
		if (handler != null)
			handler.removeCallbacks(runnable);
		btnSendCode.setText(R.string.checkcode_reget);
		accountName = accountNameEdit.getText().toString();
		if (AccountHelper.isPhoneCorrect(accountName)
				|| AccountHelper.isEmailCorrect(accountName)) {
			btnSendCode.setEnabled(true);
		} else {
			btnSendCode.setEnabled(false);
		}
		sendCount = Constants.CODE_DELAY_COUNT;
	}

	/**
	 * 
	 * @param target
	 *            邮箱或手机 13588199491 1414
	 * 
	 *            发送之前检验用户名是否有效
	 */
	public void doVerifyAccountTask(final String target) {

		AccountHelper.doVerifyAccountTask(this, target, null, true, "0000",
				new TaskHost() {
					@Override
					public void onPostExecute(int requestCode, BaseResult result) {
						super.onPostExecute(requestCode, result);
						if (result.isSuccess()) {
							NetworkClientResult res = (NetworkClientResult) result;
							if (res.getErrorCode() == NetError.CODE_40106) {
								doSendCodeTask(target);
							} else {
								ErrorDialogUtil.showErrorDialog(
										ForgetPwdActivity.this,
										ProxyErrorCode.TYPE_LOGIN_AND_REGISTER,
										result.getErrorCode(), true);
								btnSendCode.setClickable(true);
							}
						} else {
							btnSendCode.setClickable(true);
						}
					}
				});
	}

	/**
	 * 
	 * @param target
	 */
	public void doSendCodeTask(final String target) {

		AccountHelper.doVerifyAccountTask(this, target, null, null, null,
				new TaskHost() {
					@Override
					public void onPostExecute(int requestCode, BaseResult result) {
						super.onPostExecute(requestCode, result);
						if (result.isSuccess()) {
							NetworkClientResult res = (NetworkClientResult) result;
							if (res.isServerDisposeSuccess()) {
								ErrorDialogUtil
										.showErrorDialog(
												CloudApplication
														.getInstance()
														.getApplicationContext(),
												ProxyErrorCode.TYPE_LOGIN_AND_REGISTER,
												ProxyCode.LocalError.CODE_10203,
												target);
								if (handler != null) {
									handler.removeCallbacks(runnable);
									handler.post(runnable);
								}
							} else {
								ErrorDialogUtil.showErrorDialog(
										CloudApplication.getInstance()
												.getApplicationContext(),
										ProxyErrorCode.TYPE_LOGIN_AND_REGISTER,
										res.getErrorCode(), target);
							}
						}
						btnSendCode.setClickable(true);
					}
				});
	}

	public void checkSendCodeTask(String target, String code) {

		AccountHelper.doVerifyAccountTask(this, target, null, null, code,
				new TaskHost() {
					@Override
					public void onPostExecute(int requestCode, BaseResult result) {
						super.onPostExecute(requestCode, result);
						if (result.isSuccess()) {
							if (result.isServerDisposeSuccess()) {
								jumpResetActivity();
							} else {
								ErrorDialogUtil.showErrorDialog(
										ForgetPwdActivity.this,
										ProxyErrorCode.TYPE_LOGIN_AND_REGISTER,
										result.getErrorCode(), true);
							}
						}else{
							ErrorDialogUtil.showErrorDialog(
									ForgetPwdActivity.this,
									ProxyErrorCode.TYPE_LOGIN_AND_REGISTER,
									ProxyCode.LocalError.CODE_10001, true);
						}
						btnSubReset.setClickable(true);
					}
				});
	}

	@Override
	public void onClick(View v) {
		int errorCode = -1;
		switch (v.getId()) {
		case R.id.button_send_code:
			btnSendCode.setClickable(false);
			accountName = accountNameEdit.getText().toString();
			errorCode = AccountHelper.checkAccountNameAvailable(accountName);
			if (errorCode == LocalError.CODE_SUCCESS) {
				doVerifyAccountTask(accountName);
			} else {
				ErrorDialogUtil
						.showErrorDialog(ForgetPwdActivity.this,
								ProxyErrorCode.TYPE_LOGIN_AND_REGISTER,
								errorCode, true);
				btnSendCode.setClickable(true);
			}
			break;
		case R.id.subResetPwd:
			btnSubReset.setClickable(false);
			forgetMethod();
			break;
		case R.id.actionbar_left:
			this.finish();
			break;
		}
	}

	public void forgetMethod() {
		accountName = accountNameEdit.getText().toString();
		checkCode = checkCodeEdit.getText().toString();
		int errorCode = AccountHelper.checkResetParam(accountName, checkCode);
		if (errorCode == LocalError.CODE_SUCCESS) {
			checkSendCodeTask(accountName, checkCode);
		} else {
			ErrorDialogUtil.showErrorDialog(ForgetPwdActivity.this,
					ProxyErrorCode.TYPE_LOGIN_AND_REGISTER, errorCode, true);
			btnSubReset.setClickable(true);
		}
	}

	public void jumpResetActivity() {
		Intent intent = new Intent(this, ResetPwdActivity.class);
		intent.putExtra(Account.NAME_FIELD_CODE, checkCode);
		intent.putExtra(Account.NAME_FIELD_TARGET, accountName);
		startActivity(intent);
		this.finish();
	}

}

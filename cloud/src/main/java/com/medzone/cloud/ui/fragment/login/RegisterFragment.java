package com.medzone.cloud.ui.fragment.login;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.LayoutParams;
import com.medzone.cloud.Constants;
import com.medzone.cloud.data.TemporaryData;
import com.medzone.cloud.data.helper.AccountHelper;
import com.medzone.cloud.ui.dialog.error.ErrorDialogUtil;
import com.medzone.cloud.ui.widget.CleanableEditText;
import com.medzone.cloud.ui.widget.CleanableEditText.TextWatcherImplCompat;
import com.medzone.framework.data.bean.imp.Account;
import com.medzone.framework.errorcode.ProxyCode.LocalError;
import com.medzone.framework.errorcode.ProxyCode.NetError;
import com.medzone.framework.errorcode.ProxyErrorCode;
import com.medzone.framework.fragment.BaseFragment;
import com.medzone.framework.network.NetworkClientResult;
import com.medzone.framework.task.BaseResult;
import com.medzone.framework.task.TaskHost;
import com.medzone.mcloud.R;
import com.medzone.mcloud.R.drawable;
import com.medzone.mcloud.R.id;
import com.medzone.mcloud.R.string;

public class RegisterFragment extends BaseFragment implements OnClickListener {

	private int sendCount = Constants.CODE_DELAY_COUNT;

	private CleanableEditText accountEdit, passEdit, codeEdit;
	private Button sendCodeBtn, registerBtn;
	private String accountName, accountPass, checkCode;
	private Runnable runnable;
	private Handler handler;

	private boolean isAccountValiable = false;

	private Drawable rightDrawable;
	private Drawable leftDrawable;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.activity_register, null);
		accountEdit = (CleanableEditText) view
				.findViewById(R.id.ce_edit_account);
		passEdit = (CleanableEditText) view.findViewById(R.id.ce_edit_password);
		codeEdit = (CleanableEditText) view.findViewById(R.id.edit_code);
		sendCodeBtn = (Button) view.findViewById(R.id.button_send_code);
		registerBtn = (Button) view.findViewById(R.id.button_register);

		initDrawable();

		sendCodeBtn.setOnClickListener(this);
		registerBtn.setOnClickListener(this);

		codeEdit.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView arg0, int actionId,
					KeyEvent arg2) {
				if (actionId == EditorInfo.IME_ACTION_GO) {
					performRegisterOnClick();
				}
				return false;
			}
		});

		accountEdit.setTextWatcherImplCompat(new TextWatcherImplCompat() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				restoreSendCode();

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onClickRightDrawable() {
				// TODO Auto-generated method stub
				
			}
		});
		return view;
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		initActionBar();
	}

	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		initActionBar();
	}

	@Override
	protected void initActionBar() {
		super.initActionBar();
		ActionBar actionBar = getSherlockActivity().getSupportActionBar();
		actionBar.show();
		ActionBar.LayoutParams params = new ActionBar.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT,
				Gravity.CENTER);
		View view = LayoutInflater.from(getActivity()).inflate(
				R.layout.custom_actionbar_with_image, null);
		TextView title = (TextView) view.findViewById(R.id.actionbar_title);
		ImageButton leftButton = (ImageButton) view
				.findViewById(id.actionbar_left);
		leftButton.setImageResource(drawable.public_ic_back);
		leftButton.setOnClickListener(this);
		title.setText(string.action_title_register);
		actionBar.setCustomView(view, params);
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		actionBar.setDisplayShowCustomEnabled(true);
	}

	public void doCheckRegEnable() {
		checkCode = codeEdit.getText().toString();
		accountName = accountEdit.getText().toString();
		accountPass = passEdit.getText().toString();
		if (TextUtils.isEmpty(checkCode)
				|| !AccountHelper.isPasswordCorrect(accountPass)
				|| !isAccountValiable) {
			enableVerifyCodeButton(false);
		} else {
			enableVerifyCodeButton(true);
		}
	}

	private void enableSendCodeButton(boolean isEnable) {
		if (isEnable) {
			sendCodeBtn.setOnClickListener(this);
			sendCodeBtn.setEnabled(true);
			replaceOkDrawable();
		} else {
			sendCodeBtn.setEnabled(false);
			sendCodeBtn.setOnClickListener(null);
		}
	}

	private void enableVerifyCodeButton(boolean isEnable) {
		if (isEnable) {
			registerBtn.setEnabled(true);
			registerBtn.setOnClickListener(this);
		} else {
			registerBtn.setEnabled(false);
			registerBtn.setOnClickListener(null);
		}
	}

	private void restoreAgainSendCode() {
		if (handler != null)
			handler.removeCallbacks(runnable);
		sendCodeBtn.setText(string.checkcode_reget);
		sendCodeBtn.setEnabled(true);
		sendCodeBtn.setOnClickListener(this);
		sendCount = Constants.CODE_DELAY_COUNT;
	}

	private void restoreSendCode() {
		if (handler != null)
			handler.removeCallbacks(runnable);
		sendCodeBtn.setText(string.checkcode_get);
		sendCodeBtn.setEnabled(true);
		sendCodeBtn.setOnClickListener(this);
		sendCount = Constants.CODE_DELAY_COUNT;
	}

	/**
	 * 
	 * @param target
	 *            邮箱或手机
	 * 
	 *            发送之前检验用户名是否有效，应该仅作对帐号的校验。
	 */
	public void doCheckTargetExist(final String target) {

		AccountHelper.doVerifyAccountTask(getActivity(), target, null, false,
				"0000", new TaskHost() {
					@Override
					public void onPostExecute(int requestCode, BaseResult result) {
						super.onPostExecute(requestCode, result);

						if (result.isSuccess()) {
							NetworkClientResult res = (NetworkClientResult) result;

							if (res.getErrorCode() == NetError.CODE_40106) {
								doSendCodeTask(target);
							} else {
								ErrorDialogUtil.showErrorDialog(getActivity(),
										ProxyErrorCode.TYPE_LOGIN_AND_REGISTER,
										res.getErrorCode(), true);
							}
						}
					}
				});
	}

	public void doSendCodeTask(final String target) {

		AccountHelper.doVerifyAccountTask(getActivity(), target, null, null,
				null, new TaskHost() {
					@Override
					public void onPostExecute(int requestCode, BaseResult result) {
						super.onPostExecute(requestCode, result);
						if (result.isSuccess()) {
							if (result.isServerDisposeSuccess()) {
								postCountDownRunnable();
								ErrorDialogUtil.showErrorDialog(getActivity(),
										ProxyErrorCode.TYPE_LOGIN_AND_REGISTER,
										LocalError.CODE_10203, target);
							} else {
								ErrorDialogUtil.showErrorDialog(getActivity(),
										ProxyErrorCode.TYPE_LOGIN_AND_REGISTER,
										result.getErrorCode(), true);
							}
						}
					}
				});
	}

	public void checkSendCodeTask(final String target, String code) {

		AccountHelper.doVerifyAccountTask(getActivity(), target, null, null,
				code, new TaskHost() {
					@Override
					public void onPostExecute(int requestCode, BaseResult result) {
						super.onPostExecute(requestCode, result);
						if (result.isSuccess()) {
							if (result.isServerDisposeSuccess()) {
								JumpToInfoActivity();
							} else {
								ErrorDialogUtil.showErrorDialog(getActivity(),
										ProxyErrorCode.TYPE_LOGIN_AND_REGISTER,
										result.getErrorCode(), true);
							}
						}
						registerBtn.setClickable(true);
					}
				});
	}

	private void createCountDownRunnable() {
		runnable = new Runnable() {
			@Override
			public void run() {
				sendCodeBtn.setText(--sendCount + "s");
				enableSendCodeButton(false);
				handler.postDelayed(runnable, 1000);
				if (sendCount == 0) {
					restoreAgainSendCode();
				}
			}
		};
	}

	private void postCountDownRunnable() {
		if (handler == null)
			handler = new Handler();
		if (runnable == null)
			createCountDownRunnable();
		handler.post(runnable);
		enableVerifyCodeButton(true);
	}

	public void performRegisterOnClick() {

		accountName = accountEdit.getText().toString();
		accountPass = passEdit.getText().toString();
		checkCode = codeEdit.getText().toString();

		int errorCode = AccountHelper.checkRegisterParamsStyle(accountName,
				accountPass, checkCode);
		if (errorCode == LocalError.CODE_SUCCESS) {
			Account registerAccount = new Account();
			registerAccount.setPassword(accountPass);
			registerAccount.setTag(checkCode);
			if (AccountHelper.isEmailCorrect(accountName)) {
				registerAccount.setEmail(accountName);
			} else {
				registerAccount.setPhone(accountName);
			}
			TemporaryData.save(Account.class.getName(), registerAccount);
			checkSendCodeTask(accountName, checkCode);
		} else {
			ErrorDialogUtil.showErrorDialog(getActivity(),
					ProxyErrorCode.TYPE_LOGIN_AND_REGISTER, errorCode, true);
			registerBtn.setClickable(true);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.button_register:
			registerBtn.setClickable(false);
			performRegisterOnClick();
			break;
		case R.id.button_send_code: {
			accountName = accountEdit.getText().toString();
			int errorCode = AccountHelper
					.checkAccountNameAvailable(accountName);

			if (errorCode == LocalError.CODE_SUCCESS) {
				doCheckTargetExist(accountName);
			} else {
				ErrorDialogUtil
						.showErrorDialog(getActivity(),
								ProxyErrorCode.TYPE_LOGIN_AND_REGISTER,
								errorCode, true);
			}
		}
			break;
		case R.id.actionbar_left:
			getFragmentManager().beginTransaction().remove(this).commit();
			break;
		}
	}

	public void JumpToInfoActivity() {
		// startActivity(new Intent(RegisterActivity.this,
		// RegisterPersonInfoActivity.class));
	}

	private void initDrawable() {
		leftDrawable = getResources()
				.getDrawable(drawable.loginview_ic_account);
		leftDrawable.setBounds(0, 0, leftDrawable.getMinimumWidth(),
				leftDrawable.getMinimumHeight());
		accountEdit.setCompoundDrawables(leftDrawable, null, null, null);
	}

	private void replaceOkDrawable() {

		rightDrawable = getResources().getDrawable(drawable.set_ic_ok);
		rightDrawable.setBounds(0, 0, rightDrawable.getMinimumWidth(),
				rightDrawable.getMinimumHeight());
		accountEdit.setCompoundDrawables(leftDrawable, null, rightDrawable,
				null);
	}

}

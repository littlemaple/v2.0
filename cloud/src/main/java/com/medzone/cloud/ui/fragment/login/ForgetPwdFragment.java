package com.medzone.cloud.ui.fragment.login;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
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
import com.medzone.cloud.CloudApplication;
import com.medzone.cloud.Constants;
import com.medzone.cloud.data.helper.AccountHelper;
import com.medzone.cloud.ui.dialog.error.ErrorDialogUtil;
import com.medzone.cloud.ui.widget.CleanableEditText;
import com.medzone.cloud.ui.widget.CleanableEditText.TextWatcherImplCompat;
import com.medzone.framework.data.bean.imp.Account;
import com.medzone.framework.errorcode.ProxyCode;
import com.medzone.framework.errorcode.ProxyCode.LocalError;
import com.medzone.framework.errorcode.ProxyErrorCode;
import com.medzone.framework.fragment.BaseFragment;
import com.medzone.framework.network.NetworkClientResult;
import com.medzone.framework.task.BaseResult;
import com.medzone.framework.task.TaskHost;
import com.medzone.mcloud.R;
import com.medzone.mcloud.R.drawable;
import com.medzone.mcloud.R.id;

public class ForgetPwdFragment extends BaseFragment implements OnClickListener {
	private CleanableEditText accountNameEdit, checkCodeEdit;
	private String accountName, checkCode;

	private int sendCount = Constants.CODE_DELAY_COUNT;
	private Runnable runnable;
	private Handler handler = new Handler();

	private Button btnSendCode;
	private Button btnSubReset;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.activity_forgetpwd, null);
		accountNameEdit = (CleanableEditText) view
				.findViewById(R.id.ce_edit_account);
		checkCodeEdit = (CleanableEditText) view.findViewById(R.id.edit_code);
		btnSendCode = (Button) view.findViewById(R.id.button_send_code);
		btnSubReset = (Button) view.findViewById(R.id.subResetPwd);

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

		accountNameEdit.setTextWatcherImplCompat(new TextWatcherImplCompat() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				restoreSendCode();
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {

			}

			@Override
			public void onClickRightDrawable() {
				// TODO Auto-generated method stub
				
			}
		});
		createRunnable();
		return view;
	}

	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		initActionBar();
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
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
		title.setText(R.string.action_title_forget_password);
		actionBar.setCustomView(view, params);
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		actionBar.setDisplayShowCustomEnabled(true);
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

	public void restoreSendCode() {
		handler.removeCallbacks(runnable);
		btnSendCode.setText(R.string.checkcode_get);
		accountName = accountNameEdit.getText().toString();
		if (AccountHelper.isPhoneCorrect(accountName)
				|| AccountHelper.isEmailCorrect(accountName)) {
			btnSendCode.setEnabled(true);
		} else {
			btnSendCode.setEnabled(false);
		}
		sendCount = Constants.CODE_DELAY_COUNT;
	}

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
	 *            邮箱或手机
	 * 
	 *            发送之前检验用户名是否有效
	 */
	public void doVerifyAccountTask(final String target) {

		AccountHelper.doVerifyAccountTask(getActivity(), target, null, true,
				null, new TaskHost() {
					@Override
					public void onPostExecute(int requestCode, BaseResult result) {
						super.onPostExecute(requestCode, result);
						if (result.isSuccess()) {
							if (result.isServerDisposeSuccess()) {
								doSendCodeTask(target);
							} else {
								ErrorDialogUtil.showErrorDialog(getActivity(),
										ProxyErrorCode.TYPE_LOGIN_AND_REGISTER,
										result.getErrorCode(), true);
							}
						}
						btnSendCode.setClickable(true);
					}
				});
	}

	/**
	 * 
	 * @param target
	 */
	public void doSendCodeTask(final String target) {

		AccountHelper.doVerifyAccountTask(getActivity(), target, null, null,
				null, new TaskHost() {
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
								handler.post(runnable);
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

		AccountHelper.doVerifyAccountTask(getActivity(), target, null, null,
				code, new TaskHost() {
					@Override
					public void onPostExecute(int requestCode, BaseResult result) {
						super.onPostExecute(requestCode, result);
						if (result.isSuccess()) {
							if (result.isServerDisposeSuccess()) {
								jumpResetActivity();
							} else {
								ErrorDialogUtil.showErrorDialog(getActivity(),
										ProxyErrorCode.TYPE_LOGIN_AND_REGISTER,
										result.getErrorCode(), true);
							}
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
						.showErrorDialog(getActivity(),
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
			getFragmentManager().popBackStack();
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
			ErrorDialogUtil.showErrorDialog(getActivity(),
					ProxyErrorCode.TYPE_LOGIN_AND_REGISTER, errorCode, true);
			btnSubReset.setClickable(true);
		}
	}

	public void jumpResetActivity() {
		// Intent intent = new Intent(this, ResetPwdActivity.class);
		// intent.putExtra(Account.NAME_FIELD_CODE, checkCode);
		// intent.putExtra(Account.NAME_FIELD_TARGET, accountName);
		// startActivity(intent);
		// this.finish();
		FragmentManager fm = getFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();

		Fragment resetFragment = new ResetPwdFragment();
		Bundle bundle = new Bundle();
		bundle.putString(Account.NAME_FIELD_CODE, checkCode);
		bundle.putString(Account.NAME_FIELD_TARGET, accountName);
		resetFragment.setArguments(bundle);
		ft.add(R.id.login_container, resetFragment);
		ft.addToBackStack(getTag());
		ft.commit();
	}

}

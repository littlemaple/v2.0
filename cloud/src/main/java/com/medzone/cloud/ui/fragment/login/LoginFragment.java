package com.medzone.cloud.ui.fragment.login;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.actionbarsherlock.app.ActionBar;
import com.medzone.cloud.CloudApplicationPreference;
import com.medzone.cloud.GlobalVars;
import com.medzone.cloud.data.PropertyCenter;
import com.medzone.cloud.data.helper.AccountHelper;
import com.medzone.cloud.guide.GuideBook;
import com.medzone.cloud.module.modules.AccountModule.IGetDetailCallBack;
import com.medzone.cloud.network.NetworkClientHelper;
import com.medzone.cloud.ui.MainTabsActivity;
import com.medzone.cloud.ui.dialog.error.ErrorDialogUtil;
import com.medzone.cloud.ui.widget.CleanableEditText;
import com.medzone.cloud.ui.widget.CustomLinearLayout;
import com.medzone.cloud.ui.widget.CustomLinearLayout.OnResizeListener;
import com.medzone.cloud.util.GeneralUtil;
import com.medzone.framework.data.bean.imp.Account;
import com.medzone.framework.errorcode.ProxyCode.LocalError;
import com.medzone.framework.errorcode.ProxyErrorCode;
import com.medzone.framework.fragment.BaseFragment;
import com.medzone.framework.network.NetworkClientResult;
import com.medzone.framework.task.BaseResult;
import com.medzone.framework.task.TaskHost;
import com.medzone.mcloud.R;
import com.umeng.update.UmengDialogButtonListener;

public class LoginFragment extends BaseFragment implements OnClickListener,
		PropertyChangeListener {

	private CleanableEditText etAccount, etPassword;
	private String accountName, accountPass;

	private Button btnLogin;
	private CustomLinearLayout container;
	private View logoTop, logoBottom;

	public void editTextState(boolean enable) {
		etAccount.setFocusable(enable);
		etPassword.setFocusable(enable);
		etAccount.setFocusableInTouchMode(enable);
		etPassword.setFocusableInTouchMode(enable);
	}

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1:
				logoTop.setVisibility(View.GONE);
				logoBottom.setVisibility(View.GONE);
				break;
			case 2:
				logoTop.setVisibility(View.VISIBLE);
				logoBottom.setVisibility(View.VISIBLE);
				break;
			}
		};
	};

	public void onStart() {
		super.onStart();
		initActionBar();
		editTextState(true);
		PropertyCenter.getInstance().addPropertyChangeListener(this);
	};

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	
	/**
	 * 如果isOfflineLogin = true 表示该次允许离线登录
	 * 
	 * @param tmpAccount
	 * @param isOfflineLogin
	 */
	private void doLoginTask(final Account tmpAccount) {
		editTextState(false);
		AccountHelper.doLoginTask(getActivity(), tmpAccount, new TaskHost() {
			@Override
			public void onPostExecute(int requestCode, BaseResult result) {
				super.onPostExecute(requestCode, result);
				editTextState(true);
				restoreBtnLogin();
				if (result.isSuccess()) {
					NetworkClientResult res = (NetworkClientResult) result;
					if (res.isServerDisposeSuccess()) {
						GlobalVars.setOffLined(false);
						AccountHelper.updateAfterLoginSuccess(
								(NetworkClientResult) result, tmpAccount,
								new IGetDetailCallBack() {
									@Override
									public void onComplete(int code) {

										switch (code) {
										case BaseResult.DISPOSE_CODE_SUCCESS:
											jumpMainTabActivity();
											break;
										default:
											ErrorDialogUtil
													.showErrorDialog(
															getActivity(),
															ProxyErrorCode.TYPE_LOGIN_AND_REGISTER,
															ProxyErrorCode.LocalError.CODE_10001,
															true);
											break;
										}
									}
								});
					} else {
						ErrorDialogUtil.showErrorDialog(getActivity(),
								ProxyErrorCode.TYPE_LOGIN_AND_REGISTER,
								res.getErrorCode(), true);
					}
				}
			}
		});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.button_register:
			jumpRegisterActivity();
			break;
		case R.id.btn_login:
			loginOperation();
			break;
		case R.id.resetPwd:
			jumpForgetPwdActivity();
			break;

		}
	}

	public void loginOperation() {
		accountName = etAccount.getText().toString().trim();
		accountPass = etPassword.getText().toString().trim();
		final int errorCode = AccountHelper.checkLoginParamsStyle(accountName,
				accountPass, null);

		if (errorCode == LocalError.CODE_SUCCESS) {
			Account tempLoginAccount = new Account();
			if (AccountHelper.isEmailCorrect(accountName)) {
				tempLoginAccount.setEmail(accountName);
			} else if (AccountHelper.isPhoneCorrect(accountName)) {
				tempLoginAccount.setPhone(accountName);
			}
			tempLoginAccount.setPassword(accountPass);
			doLoginTask(tempLoginAccount);
			btnLoginState();
		} else {
			ErrorDialogUtil.showErrorDialog(getActivity(),
					ProxyErrorCode.TYPE_LOGIN_AND_REGISTER, errorCode, true);
		}
	}

	private void jumpMainTabActivity() {
		Intent intent = new Intent(getActivity(), MainTabsActivity.class);
		startActivity(intent);
		getActivity().finish();
	}

	private void jumpForgetPwdActivity() {
		FragmentManager manager = getFragmentManager();
		FragmentTransaction ft = manager.beginTransaction();
		ft.replace(R.id.login_container, new ForgetPwdFragment());
		ft.addToBackStack(getTag());
		ft.commit();
	}

	private void jumpRegisterActivity() {
		FragmentManager manager = getFragmentManager();
		FragmentTransaction ft = manager.beginTransaction();
		ft.replace(R.id.login_container, new RegisterFragment());
		ft.addToBackStack(getTag());
		ft.commit();

	}

	public void btnLoginState() {
		btnLogin.setText(R.string.logining);
		btnLogin.setClickable(false);
	}

	public void restoreBtnLogin() {
		btnLogin.setText(R.string.login);
		btnLogin.setClickable(true);
	}

	@Override
	public void propertyChange(PropertyChangeEvent event) {
		if (event.getPropertyName().equals(PropertyCenter.PROPERTY_POP_UPDATE)) {
			doAppUpdate();
		}
	}

	private void doAppUpdate() {

		if (!GlobalVars.isRepeatShowUpdateDialog) {
			GeneralUtil.doAppForceUpdate(getActivity(),
					new UmengDialogButtonListener() {

						@Override
						public void onClick(int cmd) {
							if (cmd == 5) {
								showToast("Downloading ……");
							}
							if (NetworkClientHelper.isForcedUpdate()) {
								getActivity().finish();
							}
						}
					});
			GlobalVars.isRepeatShowUpdateDialog = true;
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup contentView,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.activity_login, null);
		if (!GuideBook.getInstance().isShowPreface()) {
			doAppUpdate();
		}
		GuideBook.getInstance().showPreface(getActivity());

		etAccount = (CleanableEditText) view.findViewById(R.id.ce_edit_account);
		etPassword = (CleanableEditText) view
				.findViewById(R.id.ce_edit_password);
		btnLogin = (Button) view.findViewById(R.id.btn_login);
		container = (CustomLinearLayout) view.findViewById(R.id.container);
		logoTop = (View) view.findViewById(R.id.logo_top);
		logoBottom = view.findViewById(R.id.logo_bottom);
		container.addOnCustomChangeListener(new OnResizeListener() {

			@Override
			public void OnResize(int w, int h, int oldw, int oldh) {
				if (oldh != 0) {
					if ((oldh - h) > 0)
						handler.sendEmptyMessage(1);
					else
						handler.sendEmptyMessage(2);
				}
			}
		});
		String target = CloudApplicationPreference.getLastLoginRecord();
		if (!TextUtils.isEmpty(target)) {
			etAccount.setText(target);
		}

		btnLogin.setOnClickListener(this);
		view.findViewById(R.id.button_register).setOnClickListener(this);
		view.findViewById(R.id.resetPwd).setOnClickListener(this);

		etPassword.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_GO) {
					loginOperation();
				}
				return false;
			}
		});
		return view;
	}

	@Override
	protected void initActionBar() {
		super.initActionBar();
		ActionBar actionbar = getSherlockActivity().getSupportActionBar();
		actionbar.hide();
	}

	@Override
	public void onResume() {
		super.onResume();
		initActionBar();
	}

}

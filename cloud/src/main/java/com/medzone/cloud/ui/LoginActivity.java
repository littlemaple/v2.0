package com.medzone.cloud.ui;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import android.content.Intent;
import android.os.Handler;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.medzone.cloud.CloudApplicationPreference;
import com.medzone.cloud.GlobalVars;
import com.medzone.cloud.data.PropertyCenter;
import com.medzone.cloud.data.helper.AccountHelper;
import com.medzone.cloud.guide.GuideBook;
import com.medzone.cloud.module.modules.AccountModule.IGetDetailCallBack;
import com.medzone.cloud.network.NetworkClientHelper;
import com.medzone.cloud.ui.dialog.error.ErrorDialogUtil;
import com.medzone.cloud.ui.dialog.global.GlobalDialogUtil;
import com.medzone.cloud.ui.widget.CleanableEditText;
import com.medzone.cloud.ui.widget.CustomLinearLayout;
import com.medzone.cloud.ui.widget.CustomLinearLayout.OnResizeListener;
import com.medzone.cloud.util.GeneralUtil;
import com.medzone.framework.Config;
import com.medzone.framework.data.bean.imp.Account;
import com.medzone.framework.errorcode.ProxyCode.LocalError;
import com.medzone.framework.errorcode.ProxyErrorCode;
import com.medzone.framework.network.NetworkClientResult;
import com.medzone.framework.task.BaseResult;
import com.medzone.framework.task.TaskHost;
import com.medzone.mcloud.R;
import com.umeng.update.UmengDialogButtonListener;

public class LoginActivity extends BaseActivity implements OnClickListener,
		PropertyChangeListener {

	private CleanableEditText etAccount, etPassword;
	private String accountName, accountPass;

	private Button btnLogin;
	private CustomLinearLayout container;
	private View logoTop, logoBottom;

	private int testCount = 0;
	private long testFirstTime = 0;
	private int testInterval = 1000;
	private int testMaxCount = 3;

	private boolean isShowKickDialog = false;

	public void onTest(View view) {

		if (Config.isDeveloperMode) {

			// long secondTime = System.currentTimeMillis();
			// if (secondTime - testFirstTime <= testInterval) {
			//
			// switch (testCount) {
			//
			// case 1:
			// etAccount.setText("13196952782");
			// etPassword.setText("123456");
			// break;
			// case 2:
			// etAccount.setText("chenjunqi.china@qq.com");
			// etPassword.setText("123456");
			// break;
			// case 3:
			// etAccount.setText("18668247775");
			// etPassword.setText("123456");
			// break;
			// default:
			// break;
			// }
			//
			// ++testCount;
			//
			// if (testCount == testMaxCount) {
			// boolean isSandBox = NetworkClient.isSandBox();
			// NetworkClient.setSandBox(!isSandBox);
			// if (NetworkClient.isSandBox()) {
			// ToastUtils.show(this, "切换到沙箱环境");
			// } else {
			// ToastUtils.show(this, "切换到生产环境");
			// }
			// }
			// } else {
			// testCount = 1;
			// }
			// testFirstTime = secondTime;

			if (testCount == 0) {
				etAccount.setText("18768177280");
				etPassword.setText("123456");
			} else if (testCount == 1) {
				etAccount.setText("13588199491");
				etPassword.setText("123456");
			} else if (testCount == 2) {
				etAccount.setText("18768177280@163.com");
				etPassword.setText("123456");
			} else {
				etAccount.setText("18768177280@qq.com");
				etPassword.setText("123456");
				testCount = 0;
			}
			testCount++;
		}

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

	protected void preLoadData() {
		isShowKickDialog = getIntent().getBooleanExtra(
				AccountHelper.KICKDIALOG, false);
	};

	@Override
	protected void onStart() {
		super.onStart();
		editTextState(true);
		restoreBtnLogin();
		PropertyCenter.getInstance().addPropertyChangeListener(this);
	}

	@Override
	protected void initUI() {
		setContentView(R.layout.activity_login);

		if (!GuideBook.getInstance().isShowPreface()) {
			doAppUpdate();
		}
		GuideBook.getInstance().showPreface(this);

		etAccount = (CleanableEditText) this.findViewById(R.id.ce_edit_account);
		etPassword = (CleanableEditText) this
				.findViewById(R.id.ce_edit_password);
		btnLogin = (Button) this.findViewById(R.id.btn_login);

		container = (CustomLinearLayout) this.findViewById(R.id.container);
		logoTop = (View) this.findViewById(R.id.logo_top);
		logoBottom = (View) this.findViewById(R.id.logo_bottom);
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
	}

	@Override
	protected void onResume() {
		super.onResume();
		KeyBoardCancle();
	}

	@Override
	protected void postInitUI() {

		String target = CloudApplicationPreference.getLastLoginRecord();
		if (!TextUtils.isEmpty(target)) {
			etAccount.setText(target);
		}

		btnLogin.setOnClickListener(this);
		findViewById(R.id.button_register).setOnClickListener(this);
		findViewById(R.id.resetPwd).setOnClickListener(this);

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
		if (isShowKickDialog) {
			GlobalDialogUtil
					.showGlobalAppDialog(this, "提示", "您的帐号在别处登录！", null);
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		PropertyCenter.getInstance().removePropertyChangeListener(this);
	}

	public void editTextState(boolean enable) {
		etAccount.setFocusable(enable);
		etPassword.setFocusable(enable);
		etAccount.setFocusableInTouchMode(enable);
		etPassword.setFocusableInTouchMode(enable);
	}

	/**
	 * 如果isOfflineLogin = true 表示该次允许离线登录
	 * 
	 * @param tmpAccount
	 * @param isOfflineLogin
	 */
	private void doLoginTask(final Account tmpAccount) {
		editTextState(false);
		AccountHelper.doLoginTask(this, tmpAccount, new TaskHost() {
			@Override
			public void onPostExecute(int requestCode, BaseResult result) {
				super.onPostExecute(requestCode, result);
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
															LoginActivity.this,
															ProxyErrorCode.TYPE_LOGIN_AND_REGISTER,
															ProxyErrorCode.LocalError.CODE_10001,
															true);
											restoreBtnLogin();
											break;
										}
									}
								});
					} else {
						ErrorDialogUtil.showErrorDialog(LoginActivity.this,
								ProxyErrorCode.TYPE_LOGIN_AND_REGISTER,
								res.getErrorCode(), true);
						restoreBtnLogin();
					}
				} else {
					restoreBtnLogin();
				}
				editTextState(true);
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
			jumpResetPwdActivity();
			break;

		}
	}

	public void loginOperation() {
		KeyBoardCancle();
		accountName = etAccount.getText().toString();
		accountPass = etPassword.getText().toString();
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
			ErrorDialogUtil.showErrorDialog(LoginActivity.this,
					ProxyErrorCode.TYPE_LOGIN_AND_REGISTER, errorCode, true);
		}
	}

	private void jumpMainTabActivity() {
		Intent intent = new Intent(this, MainTabsActivity.class);
		startActivity(intent);
		finish();
	}

	private void jumpResetPwdActivity() {
		Intent intent = new Intent(this, ForgetPwdActivity.class);
		startActivity(intent);
	}

	private void jumpRegisterActivity() {
		Intent intent = new Intent(this, RegisterActivity.class);
		startActivity(intent);
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
			GeneralUtil.doAppForceUpdate(this, new UmengDialogButtonListener() {

				@Override
				public void onClick(int cmd) {
					if (cmd == 5) {
						// showToast("Downloading ……");
					}
					if (NetworkClientHelper.isForcedUpdate()) {
						finish();
					}
				}
			});
			GlobalVars.isRepeatShowUpdateDialog = true;
		}
	}

}

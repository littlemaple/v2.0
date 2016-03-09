package com.medzone.cloud.ui;

import android.os.Bundle;
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
import com.medzone.cloud.data.PropertyCenter;
import com.medzone.cloud.data.TemporaryData;
import com.medzone.cloud.data.helper.AccountHelper;
import com.medzone.cloud.data.helper.AccountModuleHelper;
import com.medzone.cloud.ui.dialog.error.ErrorDialogUtil;
import com.medzone.cloud.ui.fragment.group.GroupMemberManagerFragment;
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
import com.medzone.mcloud.R.string;

public class SettingChangeNickNameActivity extends BaseActivity implements
		OnClickListener {

	private CleanableEditText etNickname;
	private TextView tvCharacterLimit;
	private String tmpNickname;

	private void initActionBar() {
		ActionBar actionBar = getSupportActionBar();
		ActionBar.LayoutParams params = new ActionBar.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT,
				Gravity.CENTER);
		View view = LayoutInflater.from(this).inflate(
				R.layout.custom_actionbar_with_image, null);
		TextView title = (TextView) view.findViewById(R.id.actionbar_title);
		title.setText(string.actionbar_title_setting_nickname);
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
	protected void preLoadData(Bundle savedInstanceState) {
		super.preLoadData(savedInstanceState);
		tmpNickname = (String) TemporaryData.get(Account.NAME_FIELD_NICKNAME);
	}

	@Override
	protected void preInitUI() {
		initActionBar();
	}

	@Override
	protected void initUI() {
		setContentView(R.layout.activity_setting_change_nickname);
		etNickname = (CleanableEditText) findViewById(id.ce_edit_nickname);
		tvCharacterLimit = (TextView) findViewById(id.tv_character_limit);
	}

	@Override
	protected void postInitUI() {
		etNickname.setText(tmpNickname);
		etNickname.setSelection(tmpNickname.length());
		formatCharacterLimitTv(tmpNickname.length());
		etNickname.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				if (EditorInfo.IME_ACTION_GO == actionId) {
					updateNickNameOperator();
				}
				return false;
			}
		});
	}

	private void formatCharacterLimitTv(int initLength) {
		final int characterLength = initLength;
		String template = getResources().getString(
				string.setting_nickname_character_limit);
		tvCharacterLimit.setText(String.format(template, characterLength));
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.actionbar_left:
			finish();
			break;
		case R.id.actionbar_right:
			updateNickNameOperator();
			break;
		default:
			break;
		}
	}

	private void updateNickNameOperator() {
		final String nickname = etNickname.getText().toString().trim();
		int errorCode = AccountHelper.checkNickNameStyle(nickname);
		if (errorCode == LocalError.CODE_SUCCESS) {
			Account temp;
			try {
				temp = (Account) CurrentAccountManager.getCurAccount().clone();
				temp.setNickname(nickname);
				AccountHelper.doUpdateAccountTask(this, temp, new TaskHost() {
					@Override
					public void onPostExecute(int requestCode, BaseResult result) {
						super.onPostExecute(requestCode, result);
						if (result.isSuccess()) {
							NetworkClientResult res = (NetworkClientResult) result;
							if (res.isServerDisposeSuccess()) {
								CurrentAccountManager.getCurAccount()
										.setNickname(nickname);
								AccountModuleHelper.get(
										CurrentAccountManager.getCurAccount())
										.flushCurAccountInfo();
								PropertyCenter
										.getInstance()
										.firePropertyChange(
												PropertyCenter.PROPERTY_REFRESH_ACCOUNT,
												null,
												CurrentAccountManager
														.getCurAccount());
								PropertyCenter
										.getInstance()
										.firePropertyChange(
												PropertyCenter.PROPERTY_REFRESH_CHAT_AVATAR,
												null, null);
								GroupMemberManagerFragment.saveCurrentItem();
								PropertyCenter
										.getInstance()
										.firePropertyChange(
												PropertyCenter.PROPERTY_REFRESH_GROUP_MEMBER,
												null, null);
								finish();
							} else {
								ErrorDialogUtil.showErrorDialog(
										SettingChangeNickNameActivity.this,
										ProxyErrorCode.TYPE_SETTING,
										result.getErrorCode(), true);
							}
						} else {
							ErrorDialogUtil.showErrorToast(
									SettingChangeNickNameActivity.this,
									ProxyErrorCode.TYPE_SETTING,
									ProxyErrorCode.LocalError.CODE_10001);
						}
					}
				});
			} catch (CloneNotSupportedException e) {
				e.printStackTrace();
			}
		} else {
			ErrorDialogUtil.showErrorDialog(SettingChangeNickNameActivity.this,
					ProxyErrorCode.TYPE_SETTING, errorCode, true);
		}
	}

}

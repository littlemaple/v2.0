package com.medzone.cloud.ui.fragment.login;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.LayoutParams;
import com.medzone.cloud.data.helper.AccountHelper;
import com.medzone.cloud.task.ResetPwdTask;
import com.medzone.cloud.ui.dialog.error.ErrorDialogUtil;
import com.medzone.cloud.ui.widget.CleanableEditText;
import com.medzone.framework.data.bean.imp.Account;
import com.medzone.framework.errorcode.ProxyCode.LocalError;
import com.medzone.framework.errorcode.ProxyErrorCode;
import com.medzone.framework.fragment.BaseFragment;
import com.medzone.framework.network.NetworkClientResult;
import com.medzone.framework.task.BaseResult;
import com.medzone.framework.task.TaskHost;
import com.medzone.mcloud.R;
import com.medzone.mcloud.R.drawable;
import com.medzone.mcloud.R.id;

public class ResetPwdFragment extends BaseFragment implements OnClickListener {

	private CleanableEditText newPasswordEdit;
	private String accountName, accountPass, checkCode;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.activity_resetpwd, null);
		newPasswordEdit = (CleanableEditText) view
				.findViewById(R.id.ce_edit_password);
		view.findViewById(R.id.subResetPwd).setOnClickListener(this);
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
		// TODO Auto-generated method stub
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
		title.setText(R.string.action_title_reset_password);
		actionBar.setCustomView(view, params);
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		actionBar.setDisplayShowCustomEnabled(true);
	}

	public void doResetPasswordTask() {

		ResetPwdTask task = new ResetPwdTask(getActivity(), accountName,
				checkCode, accountPass);
		task.setTaskHost(new TaskHost() {
			@Override
			public void onPostExecute(int requestCode, BaseResult result) {
				super.onPostExecute(requestCode, result);
				if (result.isSuccess()) {
					NetworkClientResult res = (NetworkClientResult) result;
					if (res.isServerDisposeSuccess()) {
						ErrorDialogUtil.showErrorDialog(getActivity(),
								ProxyErrorCode.TYPE_LOGIN_AND_REGISTER,
								ProxyErrorCode.LocalError.CODE_10307, true);
						handler.sendEmptyMessageDelayed(1, 2000);
					} else {
						ErrorDialogUtil.showErrorDialog(getActivity(),
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
		int errorCode = -1;
		switch (v.getId()) {
		case R.id.subResetPwd: {

			accountPass = newPasswordEdit.getText().toString();
			Bundle bundle = getArguments();
			accountName = bundle.getString(Account.NAME_FIELD_TARGET);
			checkCode = bundle.getString(Account.NAME_FIELD_CODE);

			errorCode = AccountHelper.checkResetParamsStyle(accountName,
					accountPass, checkCode);
			if (errorCode == LocalError.CODE_SUCCESS) {
				doResetPasswordTask();
			} else {
				ErrorDialogUtil
						.showErrorDialog(getActivity(),
								ProxyErrorCode.TYPE_LOGIN_AND_REGISTER,
								errorCode, true);
			}
			break;
		}
		case R.id.actionbar_left:
			getFragmentManager().popBackStack();
			break;
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
		// startActivity(new Intent(ResetPwdActivity.this,
		// LoginActivity.class));
		// this.finish();
		FragmentManager fm = getFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		ft.add(R.id.login_container, new LoginFragment());
		ft.addToBackStack(getTag());
		ft.commit();
	}
}

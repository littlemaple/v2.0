package com.medzone.cloud.task;

import android.content.Context;
import android.text.TextUtils;

import com.medzone.cloud.CloudApplicationPreference;
import com.medzone.cloud.network.NetworkClient;
import com.medzone.framework.data.bean.imp.Account;
import com.medzone.framework.network.NetworkClientResult;
import com.medzone.framework.task.BaseResult;

public class LoginTask extends BaseCloudTask {

	private Account account;

	public LoginTask(Context context, Account account) {
		super(context, 0);
		this.account = account;
	}

	@Override
	protected BaseResult doInBackground(Void... params) {

		NetworkClientResult res = (NetworkClientResult) NetworkClient
				.getInstance().doLogin(account);

		if (res.isSuccess() && res.isServerDisposeSuccess()) {
			// 如果登陆成功，则进行初始化JPush操作

			String target;
			if (!TextUtils.isEmpty(account.getPhone())) {
				target = account.getPhone();
			} else {
				target = account.getEmail();
			}
			CloudApplicationPreference.saveLastLoginRecord(target);
		}

		return res;
	}

}

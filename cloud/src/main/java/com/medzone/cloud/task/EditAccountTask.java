package com.medzone.cloud.task;

import android.content.Context;

import com.medzone.cloud.data.helper.AccountHelper;
import com.medzone.cloud.network.NetworkClient;
import com.medzone.framework.data.bean.imp.Account;
import com.medzone.framework.task.BaseResult;

public class EditAccountTask extends BaseCloudTask {

	private String accessToken;
	private Account account;
	private String oldCode;
	private String newCode;

	public EditAccountTask(Context context, String accessToken,
			Account account, String... code) {
		super(context, 0);
		this.accessToken = accessToken;
		this.account = account;
		if (code.length > 0) {
			this.oldCode = code[0];
		}
		if (code.length > 1) {
			this.newCode = code[1];
		}
	}

	@Override
	protected BaseResult doInBackground(Void... params) {
		if (AccountHelper.isUrlCorrect(account.getHeadPortRait())) {
			account.setHeadPortRait(null);
		}
		return NetworkClient.getInstance().editAccount(accessToken, account,
				oldCode, newCode);
	}

	@Override
	protected void checkOfflineRequest(Context context) {
		// super.checkOfflineRequest(context);
	}
}

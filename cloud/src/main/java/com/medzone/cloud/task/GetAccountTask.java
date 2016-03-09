package com.medzone.cloud.task;

import android.content.Context;

import com.medzone.cloud.network.NetworkClient;
import com.medzone.framework.data.bean.imp.Account;
import com.medzone.framework.task.BaseResult;

public class GetAccountTask extends BaseGetControllerDataTask<Account> {

	private Account tmpAccount;

	public GetAccountTask(Context context, Account tmpAccount) {
		super(context);
		this.tmpAccount = tmpAccount;
	}

	@Override
	protected BaseResult readDataInBackground(Void... params) {

		if (tmpAccount == null) {
			BaseResult res = new BaseResult();
			res.setSuccess(false);
			res.setServerDisposeSuccess(false);
			return res;
		}
		return NetworkClient.getInstance().getAccount(
				tmpAccount.getAccessToken());
	}

}

package com.medzone.cloud.task;

import android.content.Context;

import com.medzone.cloud.network.NetworkClient;
import com.medzone.framework.task.BaseResult;

public class ResetPwdTask extends BaseCloudTask {

	private String target;
	private String checkCode;
	private String passwordNew;

	public ResetPwdTask(Context context, String target, String checkCode,
			String passwordNew) {
		super(context, 0);
		this.target = target;
		this.checkCode = checkCode;
		this.passwordNew = passwordNew;
	}

	@Override
	protected BaseResult doInBackground(Void... params) {
		return NetworkClient.getInstance().resetPwd(target, checkCode,
				passwordNew);
	}

}

package com.medzone.cloud.task;

import android.content.Context;

import com.medzone.cloud.network.NetworkClient;
import com.medzone.framework.task.BaseResult;

public class GetAllGroupTask extends BaseCloudTask {

	private String accessToken;

	public GetAllGroupTask(Context context, String accessToken) {
		super(context, 0);
		this.accessToken = accessToken;

	}

	@Override
	protected BaseResult doInBackground(Void... params) {
		return NetworkClient.getInstance().getAllGroup(accessToken);
	}

}

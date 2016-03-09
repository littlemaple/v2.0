package com.medzone.cloud.task;

import android.content.Context;

import com.medzone.cloud.network.NetworkClient;
import com.medzone.framework.task.BaseResult;

public class GetAuthorizedListTask extends BaseCloudTask {

	private String accessToken;
	private String type;

	public GetAuthorizedListTask(Context context, String accessToken) {
		super(context, 0);
		this.accessToken = accessToken;
		this.type = null;// default is all.
	}

	@Override
	protected BaseResult doInBackground(Void... params) {
		return NetworkClient.getInstance().getAuthoList(accessToken, type);
	}

}

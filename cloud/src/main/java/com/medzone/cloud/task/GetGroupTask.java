package com.medzone.cloud.task;

import android.content.Context;

import com.medzone.cloud.network.NetworkClient;
import com.medzone.framework.task.BaseResult;

public class GetGroupTask extends BaseCloudTask {

	private String accessToken;
	private Integer groupid;

	public GetGroupTask(Context context, String accessToken, Integer groupid) {
		super(context, 0);
		this.accessToken = accessToken;
		this.groupid = groupid;
	}

	@Override
	protected BaseResult doInBackground(Void... params) {
		return NetworkClient.getInstance().getGroupByID(accessToken, groupid);
	}

}

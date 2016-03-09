package com.medzone.cloud.task;

import android.content.Context;

import com.medzone.cloud.network.NetworkClient;
import com.medzone.framework.task.BaseResult;

public class GetAllGroupMemberTask extends BaseCloudTask {

	private String accessToken;
	private int groupid;

	public GetAllGroupMemberTask(Context context, String accessToken,
			int groupid) {
		super(context, 0);
		this.accessToken = accessToken;
		this.groupid = groupid;
	}

	@Override
	protected BaseResult doInBackground(Void... params) {
		return NetworkClient.getInstance().getAllGroupMember(accessToken,
				groupid);
	}

}

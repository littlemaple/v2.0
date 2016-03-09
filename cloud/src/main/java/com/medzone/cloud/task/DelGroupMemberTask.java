package com.medzone.cloud.task;

import android.content.Context;

import com.medzone.cloud.network.NetworkClient;
import com.medzone.framework.task.BaseResult;

public class DelGroupMemberTask extends BaseCloudTask {

	private String accessToken;
	private Integer groupid;
	private Integer syncid;

	public DelGroupMemberTask(Context context, String accessToken,
			Integer groupid, Integer syncid) {
		super(context, 0);
		this.accessToken = accessToken;
		this.groupid = groupid;
		this.syncid = syncid;
	}

	@Override
	protected BaseResult doInBackground(Void... params) {
		return NetworkClient.getInstance().delGroupMember(accessToken, groupid,
				syncid);
	}

}

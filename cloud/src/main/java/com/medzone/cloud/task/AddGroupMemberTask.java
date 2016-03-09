package com.medzone.cloud.task;

import android.content.Context;

import com.medzone.cloud.network.NetworkClient;
import com.medzone.framework.task.BaseResult;

public class AddGroupMemberTask extends BaseCloudTask {

	private String accessToken;
	private int groupid;
	private int syncid;

	public AddGroupMemberTask(Context context, String accessToken, int groupid,
			int syncid) {
		super(context, 0);
		this.accessToken = accessToken;
		this.groupid = groupid;
		this.syncid = syncid;
	}

	@Override
	protected BaseResult doInBackground(Void... params) {
		return NetworkClient.getInstance().addGroupMember(accessToken, groupid,
				syncid);
	}

}

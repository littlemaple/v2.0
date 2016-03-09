package com.medzone.cloud.task;

import android.content.Context;

import com.medzone.cloud.network.NetworkClient;
import com.medzone.framework.data.bean.imp.Group;
import com.medzone.framework.task.BaseResult;

public class DelGroupTask extends BaseGetControllerDataTask<Group> {

	private String accessToken;
	private int groupid;

	public DelGroupTask(Context context, String accessToken, int groupid) {
		super(context);
		this.accessToken = accessToken;
		this.groupid = groupid;
	}

	@Override
	protected BaseResult readDataInBackground(Void... params) {
		return NetworkClient.getInstance().delGroup(accessToken, groupid);
	}

}

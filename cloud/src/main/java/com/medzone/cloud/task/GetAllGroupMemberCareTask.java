package com.medzone.cloud.task;

import android.content.Context;

import com.medzone.cloud.network.NetworkClient;
import com.medzone.framework.data.bean.imp.GroupMember;
import com.medzone.framework.task.BaseResult;

public class GetAllGroupMemberCareTask extends
		BaseGetControllerDataTask<GroupMember> {

	private String accessToken;

	public GetAllGroupMemberCareTask(Context context, String accessToken) {
		super(context);
		this.accessToken = accessToken;
	}

	@Override
	protected BaseResult readDataInBackground(Void... params) {
		return NetworkClient.getInstance().getGroupMemberCareList(accessToken);
	}

}

package com.medzone.cloud.task;

import android.content.Context;

import com.medzone.cloud.network.NetworkClient;
import com.medzone.framework.task.BaseResult;

public class AddGroupTask extends BaseCloudTask {

	private String accessToken;
	private String title;
	private String description;

	public AddGroupTask(Context context, String accessToken, String title,
			String description) {
		super(context, 0);
		this.accessToken = accessToken;
		this.title = title;
		this.description = description;
	}

	@Override
	protected BaseResult doInBackground(Void... params) {
		return NetworkClient.getInstance().addGroup(accessToken, title,
				description);
	}

}

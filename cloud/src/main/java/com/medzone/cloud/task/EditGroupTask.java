package com.medzone.cloud.task;

import android.content.Context;

import com.medzone.cloud.network.NetworkClient;
import com.medzone.framework.task.BaseResult;

public class EditGroupTask extends BaseCloudTask {
	private String accessToken;
	private int groupid;
	private String title;
	private String description;
	private String image;

	public EditGroupTask(Context context, String accessToken, int groupid,
			String title, String description, String image) {
		super(context, 0);
		this.accessToken = accessToken;
		this.groupid = groupid;
		this.title = title;
		this.description = description;
		this.image = image;
	}

	@Override
	protected BaseResult doInBackground(Void... params) {
		return NetworkClient.getInstance().editGroup(accessToken, groupid,
				title, description, image);
	}
}

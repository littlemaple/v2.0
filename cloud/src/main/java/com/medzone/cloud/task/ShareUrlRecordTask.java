package com.medzone.cloud.task;

import android.content.Context;

import com.medzone.cloud.network.NetworkClient;
import com.medzone.framework.task.BaseResult;

public class ShareUrlRecordTask extends BaseCloudTask {

	private String accessToken;
	private String type;
	private String month;
	private Integer recordId;
	private String recent;

	public ShareUrlRecordTask(Context context, String accessToken, String type,
			Integer recordId, String recent, String month) {
		super(context, 0);
		this.accessToken = accessToken;
		this.type = type;
		this.month = month;
		this.recordId = recordId;
		this.recent = recent;

	}

	@Override
	protected BaseResult doInBackground(Void... params) {
		return NetworkClient.getInstance().shareUrlRecord(accessToken, type,
				recordId, recent, month);
	}

}

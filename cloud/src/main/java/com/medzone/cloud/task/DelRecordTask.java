package com.medzone.cloud.task;

import android.content.Context;

import com.medzone.cloud.network.NetworkClient;
import com.medzone.framework.task.BaseResult;

public class DelRecordTask extends BaseCloudTask {

	private String accessToken;
	private String type;
	private int recordId;
	private String recordIds;
	private int flag;

	public DelRecordTask(Context context, String accessToken, String type,
			int recordId) {
		super(context, 0);
		this.accessToken = accessToken;
		this.type = type;
		this.recordId = recordId;
		flag = 1;
	}

	public DelRecordTask(Context context, String accessToken, String type,
			String recordIds) {
		super(context, 0);
		this.accessToken = accessToken;
		this.type = type;
		this.recordIds = recordIds;
		flag = 2;
	}

	@Override
	protected BaseResult doInBackground(Void... params) {
		if (flag == 1) {
			return NetworkClient.getInstance().delRecord(accessToken, type,
					recordId);
		} else {
			return NetworkClient.getInstance().delRecord(accessToken, type,
					recordIds);
		}
	}

}

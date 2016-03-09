package com.medzone.cloud.task;

import android.content.Context;

import com.medzone.cloud.network.NetworkClient;
import com.medzone.framework.task.BaseResult;

public class EditRecordTask extends BaseCloudTask {

	private String accessToken;
	private String type;
	private int recordId;
	private String readMe;
	private float value1, value2, value3;

	public EditRecordTask(Context context, String accessToken, String type,
			int recordId, String readMe, float value1, float value2,
			float value3) {
		super(context, 0);
		this.accessToken = accessToken;
		this.type = type;
		this.recordId = recordId;
		this.readMe = readMe;
		this.value1 = value1;
		this.value2 = value2;
		this.value3 = value3;
	}

	@Override
	protected BaseResult doInBackground(Void... params) {
		return NetworkClient.getInstance().editRecord(accessToken, type,
				recordId, readMe, value1, value2, value3);
	}

}

package com.medzone.cloud.task;

import android.content.Context;

import com.medzone.cloud.network.NetworkClient;
import com.medzone.framework.task.BaseResult;

public class RecordUploadTask extends BaseCloudTask {

	protected String accessToken;
	protected String type;
	protected String uploadData;
	protected Integer upSyncId;
	protected Integer downSerial;
	protected Integer downLimit;

	public RecordUploadTask(Context context, String accessToken, String type,
			String uploadData, Integer upSyncId, Integer downSerial,
			Integer downLimit) {
		super(context, 0);
		this.accessToken = accessToken;
		this.type = type;
		this.uploadData = uploadData;
		this.upSyncId = upSyncId;
		this.downSerial = downSerial;
		this.downLimit = downLimit;
	}

	@Override
	protected BaseResult doInBackground(Void... params) {
		return NetworkClient.getInstance().uploadRecord(accessToken, type,
				uploadData, upSyncId, downSerial, downLimit);
	}

}

package com.medzone.cloud.task;

import android.content.Context;

import com.medzone.cloud.network.NetworkClient;
import com.medzone.framework.task.BaseResult;

public class GetRecordTask extends BaseCloudTask {

	private String accessToken;
	private String type;
	private String timeRange;
	private Integer serial;
	private Integer endId;
	private Integer limit;
	private Integer offset;
	private String sort;
	private Integer otherSyncid;

	public GetRecordTask(Context context, String accessToken, String type,
			String timeRange, Integer endId, Integer serial, Integer limit,
			Integer offset, String sort, Integer otherSyncid) {
		super(context, 0);
		this.accessToken = accessToken;
		this.type = type;
		this.limit = limit;
		this.sort = sort;
		this.offset = offset;
		this.endId = endId;
		this.serial = serial;
		this.timeRange = timeRange;
		this.otherSyncid = otherSyncid;
	}

	@Override
	protected BaseResult doInBackground(Void... params) {
		return NetworkClient.getInstance().getRecord(accessToken, type,
				timeRange, endId, serial, limit, offset, sort, otherSyncid);
	}

}

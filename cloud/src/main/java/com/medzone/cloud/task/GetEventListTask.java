/**
 * 
 */
package com.medzone.cloud.task;

import android.content.Context;

import com.medzone.cloud.network.NetworkClient;
import com.medzone.framework.task.BaseResult;

/**
 * @author ChenJunQi.
 * 
 */
public class GetEventListTask extends BaseCloudTask {

	private String accessToken;
	private Integer syncid;
	private Integer groupid;
	private Integer limit;
	private Integer offset;

	public GetEventListTask(Context context, String accessToken,
			Integer syncid, Integer groupid, Integer limit, Integer offset) {
		super(context, 0);
		this.accessToken = accessToken;
		this.syncid = syncid;
		this.groupid = groupid;
		this.limit = limit;
		this.offset = offset;
	}

	@Override
	protected BaseResult doInBackground(Void... params) {

		return NetworkClient.getInstance().getEventList(accessToken, syncid,
				groupid, limit, offset);
	}

}
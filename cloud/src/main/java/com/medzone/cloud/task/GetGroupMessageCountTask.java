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
public class GetGroupMessageCountTask extends BaseCloudTask {

	private String accessToken;
	private Integer syncid;
	private Integer groupid;
	private Integer begin_id;
	private Integer end_id;
	private Boolean unRead;

	public GetGroupMessageCountTask(Context context, String accessToken,
			Integer syncid, Integer groupid, Integer begin_id, Integer end_id,
			Boolean unRead) {
		super(context, 0);
		this.accessToken = accessToken;
		this.syncid = syncid;
		this.groupid = groupid;
		this.begin_id = begin_id;
		this.end_id = end_id;
		this.unRead = unRead;
	}

	@Override
	protected BaseResult doInBackground(Void... params) {

		return NetworkClient.getInstance().getGroupMessageCount(accessToken,
				syncid, groupid, begin_id, end_id, unRead);
	}

}
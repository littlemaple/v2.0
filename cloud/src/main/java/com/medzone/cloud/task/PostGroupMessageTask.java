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
public class PostGroupMessageTask extends BaseCloudTask {

	private String accessToken;
	private Integer syncid;
	private Integer groupid;
	private String data;
	private Integer type;

	public PostGroupMessageTask(Context context, String accessToken,
			Integer syncid, Integer groupid, String data, Integer type) {
		super(context, 0);
		this.accessToken = accessToken;
		this.syncid = syncid;
		this.groupid = groupid;
		this.data = data;
		this.type = type;

	}

	@Override
	protected BaseResult doInBackground(Void... params) {

		return NetworkClient.getInstance().postGroupMessage(accessToken,
				syncid, groupid, data, type);
	}

}
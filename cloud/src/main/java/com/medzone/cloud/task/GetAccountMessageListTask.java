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
public class GetAccountMessageListTask extends BaseCloudTask {

	private String accessToken;
	private Integer limit;
	private Integer offset;
	private Integer system;

	public GetAccountMessageListTask(Context context, String accessToken,
			Integer limit, Integer offset, Integer system) {
		super(context, 0);
		this.accessToken = accessToken;
		this.limit = limit;
		this.offset = offset;
		this.system = system;
	}

	@Override
	protected BaseResult doInBackground(Void... params) {

		return NetworkClient.getInstance().getMessageList(accessToken, limit,
				offset, system);
	}

}
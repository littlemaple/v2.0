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
public class GetMessageListTask extends BaseCloudTask {

	private String accessToken;

	public GetMessageListTask(Context context, String accessToken) {
		super(context, 0);
		this.accessToken = accessToken;
	}

	@Override
	protected BaseResult doInBackground(Void... params) {

		return NetworkClient.getInstance().getMessageList(accessToken, Integer.MAX_VALUE,
				null, null);
	}

}
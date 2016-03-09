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
 *         获取针对当前用户的各类消息通知的总数和未读数量。
 * 
 */
public class GetMessageCountTask extends BaseCloudTask {

	private String accessToken;

	public GetMessageCountTask(Context context, String accessToken) {
		super(context, 0);
		this.accessToken = accessToken;
	}

	@Override
	protected BaseResult doInBackground(Void... params) {

		return NetworkClient.getInstance().getMessageCount(accessToken);
	}

}
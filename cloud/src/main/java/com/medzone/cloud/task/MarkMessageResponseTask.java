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
public class MarkMessageResponseTask extends BaseCloudTask {

	private String accessToken;
	private Integer messageid;
	private String response;
	private String extra;

	/**
	 * 
	 * @param accessToken
	 * @param messageid
	 * @param response
	 * @param extra
	 *            附加信息
	 */
	public MarkMessageResponseTask(Context context, String accessToken,
			Integer messageid, String response, String extra) {
		super(context, 0);
		this.accessToken = accessToken;
		this.messageid = messageid;
		this.response = response;
		this.extra = extra;
	}

	@Override
	protected BaseResult doInBackground(Void... params) {

		return NetworkClient.getInstance().markMessageResponse(accessToken,
				messageid, response, extra);
	}

}
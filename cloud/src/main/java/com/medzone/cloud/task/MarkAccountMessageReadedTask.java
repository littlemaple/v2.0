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
public class MarkAccountMessageReadedTask extends BaseCloudTask {

	private String accessToken;
	private Integer messageid;
	private Integer system;
	private Boolean isread;

	private String isRead;
	private boolean flag;

	public MarkAccountMessageReadedTask(Context context, String accessToken,
			Integer messageid, Boolean isread, Integer system) {
		super(context, 0);
		this.accessToken = accessToken;
		this.messageid = messageid;
		this.system = system;
		this.isread = isread;
		flag = true;
	}

	public MarkAccountMessageReadedTask(Context context, String accessToken,
			Integer messageid, String isRead, Integer system) {
		super(context, 0);
		this.accessToken = accessToken;
		this.messageid = messageid;
		this.system = system;
		this.isRead = isRead;
		flag = false;
	}

	@Override
	protected BaseResult doInBackground(Void... params) {

		if (flag)
			return NetworkClient.getInstance().markMessageReaded(accessToken,
					messageid, isread, system);
		return NetworkClient.getInstance().markMessageReaded(accessToken,
				messageid, isRead, system);
	}
}
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
public class GetOtherAccountTask extends BaseCloudTask {

	private String accessToken, target;
	private Integer groupID;

	/**
	 * 
	 * @param accessToken
	 * 
	 * @param target
	 *            groupMemberID,phone,email 都可以传入
	 */
	public GetOtherAccountTask(Context context, String accessToken,
			Integer groupID, String target) {
		super(context, 0);
		this.accessToken = accessToken;
		this.target = target;
		this.groupID = groupID;
	}

	@Override
	protected BaseResult doInBackground(Void... params) {

		return NetworkClient.getInstance().queryAccountInfo(accessToken,
				target, groupID);
	}

}

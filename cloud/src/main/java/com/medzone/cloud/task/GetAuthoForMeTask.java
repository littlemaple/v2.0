/**
 * 
 */
package com.medzone.cloud.task;

import android.content.Context;

import com.medzone.cloud.network.NetworkClient;
import com.medzone.framework.data.bean.imp.GroupMember;
import com.medzone.framework.network.NetworkClientResult;
import com.medzone.framework.task.BaseResult;

/**
 * @author ChenJunQi.
 * 
 */
public class GetAuthoForMeTask extends BaseCloudTask {

	private String accessToken;
	private Integer syncid;
	private String reverse = "Y";
	private GroupMember member;

	public GetAuthoForMeTask(Context context, String accessToken,
			GroupMember member) {
		super(context, 0);

		this.syncid = member.getAccountID();
		this.accessToken = accessToken;
		this.member = member;
	}

	@Override
	protected BaseResult doInBackground(Void... params) {

		NetworkClientResult res = (NetworkClientResult) NetworkClient
				.getInstance().getAuthoForMe(accessToken, syncid, reverse);
		if (res.isServerDisposeSuccess() && res.isSuccess()) {
			member = GroupMember.updateGroupAuthoForMe(res.getResponseResult(),
					member);
		}
		return res;
	}
}
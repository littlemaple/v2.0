/**
 * 
 */
package com.medzone.cloud.task;

import android.content.Context;

import com.medzone.cloud.network.NetworkClient;
import com.medzone.framework.Log;
import com.medzone.framework.data.bean.imp.Account;
import com.medzone.framework.data.bean.imp.Message;
import com.medzone.framework.data.navigation.LongStepable;
import com.medzone.framework.data.navigation.Paging;
import com.medzone.framework.network.NetworkClientResult;
import com.medzone.framework.task.BaseResult;

/**
 * @author ChenJunQi.
 * 
 */
public class GetGroupMessageListTask extends BaseGetControllerDataTask<Message> {

	private Paging<LongStepable> paging;
	private Account account;
	private String accessToken;
	/**
	 * 群主对话用户 ID，适用于非普通群群主与成员之间的单点消息。
	 */
	private Integer syncid;
	/**
	 * 提取消息所在群 ID
	 */
	private Integer groupid;
	private Integer begin_id;
	private Integer end_id;
	private Integer limit;

	public GetGroupMessageListTask(Context context, /* String accessToken */
			Account account, Paging<LongStepable> paging, Integer syncID,
			Integer groupID) {
		super(context);

		if (syncID == Account.INVALID_ID) {
			this.syncid = null;
		} else {
			this.syncid = syncID;
		}

		this.groupid = groupID;
		this.paging = paging;
		this.account = account;
		if (account != null) {
			this.accessToken = account.getAccessToken();
		}
		this.limit = paging.getPageSize();
		LongStepable min = paging.getMin();
		LongStepable max = paging.getMax();
		if (min != null) {
			this.begin_id = (int) min.getNumber() - 1;
			this.limit = null;
		} else {
			this.begin_id = null;
		}
		if (max != null) {
			this.end_id = (int) max.getNumber() + 1;
			this.limit = null;
		} else {
			this.end_id = null;
		}
	}

	@Override
	protected BaseResult readDataInBackground(Void... params) {

		NetworkClientResult res = (NetworkClientResult) NetworkClient
				.getInstance().getGroupMessageList(accessToken, syncid,
						groupid, begin_id, end_id, limit, null);

		if (res.isSuccess()) {
			if (res.isServerDisposeSuccess()) {
				newItems = Message.createMessageList(res);
			} else {
				Log.e(res.getResponseResult().toString());
			}
		}

		return res;
	}
}

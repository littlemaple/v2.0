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
public class GetRuleListTask extends BaseCloudTask {

	private Integer measuretype;

	public GetRuleListTask(Context context, Integer measuretype) {
		super(context, 0);
		this.measuretype = measuretype;
	}

	@Override
	protected BaseResult doInBackground(Void... params) {

		return NetworkClient.getInstance().getRuleList(measuretype);
	}

}
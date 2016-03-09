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
public class DoRuleMatchTask extends BaseCloudTask {

	private Integer syncid;
	private Integer measuretype;
	private Float value1;
	private Float value2;
	private Float value3;

	public DoRuleMatchTask(Context context, Integer syncid,
			Integer measuretype, Float value1, Float value2, Float value3) {
		super(context, 0);
		this.syncid = syncid;
		this.measuretype = measuretype;
		this.value1 = value1;
		this.value2 = value2;
		this.value3 = value3;
	}

	@Override
	protected BaseResult doInBackground(Void... params) {

		return NetworkClient.getInstance().doRuleMatch(syncid, measuretype,
				value1, value2, value3);
	}

}
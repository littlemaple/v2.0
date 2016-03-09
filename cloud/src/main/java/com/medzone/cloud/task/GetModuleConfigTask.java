/**
 * 
 */
package com.medzone.cloud.task;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;

import com.medzone.cloud.network.NetworkClient;
import com.medzone.framework.module.ModuleSpecification;
import com.medzone.framework.task.BaseResult;

/**
 * @author ChenJunQi.
 * 
 *         this api means to upload all module's specifications.
 */
public class GetModuleConfigTask extends BaseCloudTask {

	private String accessToken;
	private Integer syncid;
	private List<ModuleSpecification> moduleSpecList;

	/**
	 * 
	 */
	public GetModuleConfigTask(Context context, String accessToken) {
		super(context, 0);
		this.accessToken = accessToken;
		this.moduleSpecList = null;
		this.syncid = null;
	}

	public GetModuleConfigTask(Context context, String accessToken,
			Integer otherAccountID) {
		super(context, 0);
		this.accessToken = accessToken;
		this.moduleSpecList = null;
		this.syncid = otherAccountID;
	}

	public GetModuleConfigTask(Context context, String accessToken,
			List<ModuleSpecification> upDataList) {
		super(context, 0);
		this.accessToken = accessToken;
		this.moduleSpecList = upDataList;
		this.syncid = null;
	}

	public GetModuleConfigTask(Context context, String accessToken,
			ModuleSpecification moduleSpecification) {
		super(context, 0);
		this.accessToken = accessToken;
		this.syncid = null;
		moduleSpecList = new ArrayList<ModuleSpecification>();
		moduleSpecList.add(moduleSpecification);
	}

	@Override
	protected BaseResult doInBackground(Void... params) {
		return NetworkClient.getInstance().getAppModule(accessToken,
				moduleSpecList, syncid);
	}

}

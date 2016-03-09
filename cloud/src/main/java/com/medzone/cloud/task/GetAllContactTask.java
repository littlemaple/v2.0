//package com.medzone.cloud.task;
//
//import com.medzone.cloud.network.NetworkClient;
//import com.medzone.framework.task.BaseResult;
//import com.medzone.framework.task.BaseTask;
//
//public class GetAllContactTask extends BaseTask {
//
//	private String accessToken;
//
//	public GetAllContactTask(String accessToken) {
//		super(0);
//		this.accessToken = accessToken;
//	}
//
//	@Override
//	protected BaseResult doInBackground(Void... params) {
//		return NetworkClient.getInstance().getAllContact(accessToken);
//	}
//
// }

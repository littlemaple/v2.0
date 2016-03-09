//package com.medzone.cloud.task;
//
//import com.medzone.cloud.network.NetworkClient;
//import com.medzone.framework.task.BaseResult;
//import com.medzone.framework.task.BaseTask;
//
//public class DelContactTask extends BaseTask {
//
//	private String accessToken;
//	private String contactId;
//
//	public DelContactTask(String contactId, String accessToken) {
//		super(0);
//		this.contactId = contactId;
//		this.accessToken = accessToken;
//	}
//
//	@Override
//	protected BaseResult doInBackground(Void... params) {
//		return NetworkClient.getInstance().delContact(accessToken, contactId);
//	}
//
// }

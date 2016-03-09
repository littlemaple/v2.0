//package com.medzone.cloud.task;
//
//import com.medzone.cloud.network.NetworkClient;
//import com.medzone.framework.task.BaseResult;
//import com.medzone.framework.task.BaseTask;
//
//public class AddContactTask extends BaseTask {
//
//	private String accessToken;
//	private String name;
//	private String phone;
//	private String email;
//
//	public AddContactTask(String accessToken, String name, String phone,
//			String email) {
//		super(0);
//		this.accessToken = accessToken;
//		this.name = name;
//		this.phone = phone;
//		this.email = email;
//	}
//
//	@Override
//	protected BaseResult doInBackground(Void... params) {
//		// TODO Auto-generated method stub
//		return NetworkClient.getInstance().addContact(accessToken, name, phone,
//				email);
//	}
//
//}

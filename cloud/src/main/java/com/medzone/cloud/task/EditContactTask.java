//package com.medzone.cloud.task;
//
//import android.content.Context;
//
//import com.medzone.cloud.network.NetworkClient;
//import com.medzone.framework.task.BaseResult;
//
//public class EditContactTask extends BaseCloudTask {
//
//	private String accessToken;
//	private String contactId;
//	private String name;
//	private String phone;
//	private String email;
//
//	public EditContactTask(Context context, String accessToken,
//			String contactId, String name, String phone, String email) {
//		super(context, 0);
//		this.accessToken = accessToken;
//		this.contactId = contactId;
//		this.name = name;
//		this.phone = phone;
//		this.email = email;
//	}
//
//	@Override
//	protected BaseResult doInBackground(Void... params) {
//		return NetworkClient.getInstance().editContact(accessToken, contactId,
//				name, phone, email);
//	}
//
//}

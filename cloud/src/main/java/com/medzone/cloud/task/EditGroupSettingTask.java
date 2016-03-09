package com.medzone.cloud.task;

import android.content.Context;

import com.medzone.cloud.Constants;
import com.medzone.cloud.network.NetworkClient;
import com.medzone.framework.data.bean.imp.Group;
import com.medzone.framework.task.BaseResult;

public class EditGroupSettingTask extends BaseGetControllerDataTask<Group> {

	private String accessToken;
	private Integer groupid;
	private Boolean isupload;
	private Integer alert;

	public EditGroupSettingTask(Context context, String accessToken,
			Integer groupid, Boolean isupload, Integer alert) {
		super(context);
		this.accessToken = accessToken;
		this.groupid = groupid;
		this.isupload = isupload;
		this.alert = alert;

		if (this.alert == null) {
			this.alert = Constants.ALERT_POPUP;
		}
		if (isupload == null) {
			isupload = true;
		}
	}

	@Override
	protected BaseResult readDataInBackground(Void... params) {
		return NetworkClient.getInstance().editGroupMemberSetting(accessToken,
				groupid, isupload, alert);
	}

}

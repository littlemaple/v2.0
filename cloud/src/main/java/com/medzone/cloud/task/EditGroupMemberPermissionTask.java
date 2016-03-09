package com.medzone.cloud.task;

import android.content.Context;

import com.medzone.cloud.data.CurrentAccountManager;
import com.medzone.cloud.network.NetworkClient;
import com.medzone.framework.data.bean.imp.GroupMember;
import com.medzone.framework.task.BaseResult;

public class EditGroupMemberPermissionTask extends
		BaseGetControllerDataTask<GroupMember> {

	private String accessToken;
	private Integer syncid;
	private Integer groupid;
	private Boolean isView;
	private Boolean isCare;
	private Boolean isTest;
	private String remark;

	public EditGroupMemberPermissionTask(Context context, String accessToken,
			Integer syncid, Integer groupid, Boolean isView, Boolean isCare,
			Boolean isTest, String remark) {
		super(context);
		this.accessToken = accessToken;
		this.syncid = syncid;
		this.groupid = groupid;
		this.isView = isView;
		this.isCare = isCare;
		this.isTest = isTest;
		this.remark = remark;
	}

	public EditGroupMemberPermissionTask(Context context, String accessToken,
			Integer syncid, Integer groupid) {
		super(context);
		this.accessToken = accessToken;
		this.syncid = syncid;
		this.groupid = groupid;
	}

	@Override
	protected BaseResult readDataInBackground(Void... params) {

		BaseResult result = NetworkClient.getInstance().editGroupMemberPerm(
				accessToken, syncid, groupid, isView, isCare, isTest, remark);
		if (result.isSuccess() && result.isServerDisposeSuccess()) {
			CurrentAccountManager.getAuthorizedList(true, false);
		}
		return result;
	}

}

package com.medzone.cloud.task;

import android.content.Context;

import com.medzone.cloud.network.NetworkClient;
import com.medzone.cloud.ui.dialog.error.ErrorDialogUtil;
import com.medzone.framework.errorcode.ProxyErrorCode;
import com.medzone.framework.task.BaseResult;

public class GetAllGroupMemberTestTask extends BaseCloudTask {

	private String accessToken;

	public GetAllGroupMemberTestTask(Context context, String accessToken) {
		super(context, 0);
		this.accessToken = accessToken;
	}

	@Override
	protected BaseResult doInBackground(Void... params) {
		return NetworkClient.getInstance().getGroupMemberTestList(accessToken);
	}

	@Override
	protected void checkOfflineRequest(Context context) {
		ErrorDialogUtil.showErrorToast(context, ProxyErrorCode.TYPE_MEASURE,
				ProxyErrorCode.LocalError.CODE_MEASUREMENT_FOR_OTHERS);
	}

}

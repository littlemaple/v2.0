/**
 * 
 */
package com.medzone.cloud.task;

import android.content.Context;

import com.medzone.cloud.CloudApplication;
import com.medzone.cloud.data.helper.AccountHelper;
import com.medzone.cloud.ui.dialog.error.ErrorDialogUtil;
import com.medzone.framework.Log;
import com.medzone.framework.errorcode.ProxyCode.LocalError;
import com.medzone.framework.errorcode.ProxyCode.NetError;
import com.medzone.framework.errorcode.ProxyErrorCode;
import com.medzone.framework.network.NetworkClientResult;
import com.medzone.framework.task.BaseResult;
import com.medzone.framework.task.BaseTask;

/**
 * @author ChenJunQi
 *         <p>
 *         BaseTask extends AsyncTask with IProgress updating
 *         </p>
 * 
 */
public abstract class BaseCloudTask extends BaseTask {

	protected Context context;

	public BaseCloudTask(Context context, int requestCode) {
		super(requestCode);
		this.context = context;
	}

	@Override
	protected void onPostExecute(BaseResult result) {
		super.onPostExecute(result);
		if (result.isSuccess()) {
			checkErrorCode(context, result);
		} else {
			if (result.getErrorCode() != LocalError.CODE_10005) {
				checkOfflineRequest(context);
			} else {
				checkServerExceptionRequest(context);
			}
		}
	}

	protected void checkErrorCode(Context context, BaseResult result) {
		if (context == null)
			return;
		NetworkClientResult res = (NetworkClientResult) result;
		switch (res.getErrorCode()) {
		case NetError.CODE_40504:
		case NetError.CODE_40002:
			Log.v("key:show kicked dialog.");
			AccountHelper.showKickedDialog(context);
			break;
		default:
			Log.v("key:auto start jpush when api is arrived.");
			CloudApplication.getInstance().initDefenderService(true);
			break;
		}
	}

	protected void checkServerExceptionRequest(Context context) {
		if (context == null)
			return;
		// 这里的type是不准确的，目前因为是公共的，所以也不单独划分一个错误类别了
		ErrorDialogUtil.showErrorDialog(context, ProxyErrorCode.TYPE_HOME,
				LocalError.CODE_10005, true);
	}

	protected void checkOfflineRequest(Context context) {
		if (context == null)
			return;
		// 这里的type是不准确的，目前因为是公共的，所以也不单独划分一个错误类别了
		ErrorDialogUtil.showErrorDialog(context, ProxyErrorCode.TYPE_HOME,
				LocalError.CODE_10001, true);
	}
}

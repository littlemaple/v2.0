package com.medzone.cloud.module.modules;

import com.medzone.cloud.controller.AccountController;
import com.medzone.cloud.data.CurrentAccountManager;
import com.medzone.cloud.data.helper.AccountHelper;
import com.medzone.framework.Log;
import com.medzone.framework.data.bean.imp.Account;
import com.medzone.framework.errorcode.ProxyCode.LocalError;
import com.medzone.framework.network.NetworkClientResult;
import com.medzone.framework.task.BaseResult;
import com.medzone.framework.task.TaskHost;

/**
 * 
 * @author Local_ChenJunQi
 * 
 * 
 */
public class AccountModule extends
		CloudUseTaskCacheControllerModule<AccountController> {

	public interface IGetDetailCallBack {
		public void onComplete(int resultCode);

	}

	public AccountModule() {
	}

	@Override
	protected AccountController createCacheController() {
		return new AccountController();
	}

	/**
	 * 
	 * @param callback
	 *            {@link IGetDetailCallBack} params always to handler event
	 *            after request is responsed.
	 * 
	 */
	public void updateAccountInfo(final IGetDetailCallBack callback) {
		Log.i("Update account information in the...");
		TaskHost task = new TaskHost() {
			public void onPostExecute(int requestCode, BaseResult result) {
				if (result.isSuccess()) {
					NetworkClientResult res = (NetworkClientResult) result;
					if (res.isServerDisposeSuccess()) {
						Account loginAccount = CurrentAccountManager
								.getCurAccount();
						// Use net response info to fill account object.
						AccountHelper.updateLoginAccountInfo(res, loginAccount);
						// Make sure the DB-event you wanted,then call
						// invalidate.
						loginAccount.invalidate();
						controller.getCache().flush(loginAccount);
						if (callback != null) {
							callback.onComplete(BaseResult.DISPOSE_CODE_SUCCESS);
						}
					} else {
						if (callback != null) {
							callback.onComplete(res.getErrorCode());
						}
					}
				} else {
					if (callback != null) {
						callback.onComplete(LocalError.CODE_10001);
					}
				}
			};
		};
		controller.getNewItemsFromServer(null, task);
	}

	public void flushCurAccountInfo() {
		Account loginAccount = CurrentAccountManager.getCurAccount();
		loginAccount.invalidate();
		controller.getCache().flush(loginAccount);
	}

}

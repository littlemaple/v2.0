/**
 * 
 */
package com.medzone.cloud.controller;

import com.medzone.cloud.cache.AccountCache;
import com.medzone.cloud.task.BaseGetControllerDataTask;
import com.medzone.cloud.task.GetAccountTask;
import com.medzone.framework.data.bean.imp.Account;

/**
 * @author ChenJunQi.
 * 
 */
public class AccountController extends
		AbstractUseTaskCacheController<Account, AccountCache> {

	/**
	 * 
	 */

	@Override
	protected AccountCache createCache() {
		return new AccountCache();
	}

	@Override
	protected BaseGetControllerDataTask<Account> createGetDataTask(
			Object... params) {
		return new GetAccountTask(null, getAttachInfo().mAccount);
	}

	@Override
	public boolean isVaild() {
		if (getAttachInfo().mAccount == null)
			return false;
		return getAttachInfo().mAccount.getAccountID() != Account.INVALID_ID;
	}

	public boolean clearLoginHistory() {
		return sCache.clearHistory();
	}

	@Override
	protected void onAttach() {
		super.onAttach();
		firstReadLocalData();
	}
}

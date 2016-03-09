/**
 * 
 */
package com.medzone.cloud.data.helper;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;

import com.medzone.cloud.module.modules.AccountModule;
import com.medzone.framework.data.bean.imp.Account;
import com.medzone.framework.data.bean.imp.AttachInfo;

/**
 * @author ChenJunQi.
 * 
 */
public final class AccountModuleHelper {

	private static List<AccountModule> memoryAccountModuleList = new ArrayList<AccountModule>();

	public static List<AccountModule> getMemoryAccountModuleList() {
		return memoryAccountModuleList;
	}

	/**
	 * 该方法将不会检查所添加module是否重复，如需检查请使用
	 * {@link #applyAccountModule(Context, Account)}
	 * 
	 * @param am
	 */
	private static void add(AccountModule am) {
		memoryAccountModuleList.add(am);
	}

	public static void remove(AccountModule am) {
		memoryAccountModuleList.remove(am);
	}

	public static void clear() {
		memoryAccountModuleList.clear();
		memoryAccountModuleList = null;
	}

	/**
	 * 为用户创建帐号模块对象，这是最底层的操作，在有帐号模块的基础上，才能继续获取帐号相关的其他信息
	 * 
	 * @param context
	 * @param account
	 * @return
	 */
	public static AccountModule applyAccountModule(Context context,
			Account account) {

		int location = checkModulePosition(account);
		if (location < 0) {
			AttachInfo ai = new AttachInfo();
			ai.mAccount = account;
			AccountModule am = new AccountModule();
			am.init(context, ai);
			add(am);
			return am;
		} else {
			return memoryAccountModuleList.get(location);
		}
	}

	public static AccountModule get(Account account) {
		int location = checkModulePosition(account);
		if (location < 0) {
			return null;
		}
		return memoryAccountModuleList.get(location);
	}

	private static int checkModulePosition(Account account) {

		for (int i = 0; i < memoryAccountModuleList.size(); i++) {
			AccountModule tam = memoryAccountModuleList.get(i);
			if (account == null)
				return -1;
			if (tam == null)
				return -1;
			if (tam.getAttachInfo() == null)
				return -1;
			if (account.equals(tam.getAttachInfo().mAccount)) {
				return i;
			}
		}
		return -1;
	}

}

/**
 * 
 */
package com.medzone.cloud;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

import com.medzone.cloud.guide.GuideBook;
import com.medzone.framework.data.bean.imp.Account;

/**
 * @author ChenJunQi.
 * 
 */
public class CloudApplicationPreference {

	public static final String KEY_LOGIN_MODE = "login_mode";
	public static final String KEY_LOGIN_TARGET = "login_target";
	public static final String KEY_LOGIN_PHONE = "login_phone";
	public static final String KEY_LOGIN_EMAIL = "login_email";
	public static final String KEY_LOGIN_ID = "login_id";
	public static final String KEY_LOGIN_PASSWORD = "login_password";
	public static final String KEY_ACCESS_TOKEN = "access_token";

	public static final String KEY_PREFACE_VERSION = "preface_version";
	public static final String KEY_DEFTAB_POSITION = "deftab_position";
	public static final String KEY_IS_SANDBOX = "is_sandbox";

	private static SharedPreferences defaultSharedPreferences;

	public static void init(Context appContext) {
		defaultSharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(appContext);
	}

	public static void saveLoginMode(boolean loginMode) {
		Editor edit = defaultSharedPreferences.edit();
		edit.putBoolean(KEY_LOGIN_MODE, loginMode);
		edit.commit();
	}

	public static boolean getLoginMode(boolean defaultLoginMode) {
		boolean loginMode;
		try {
			loginMode = defaultSharedPreferences.getBoolean(KEY_LOGIN_MODE,
					defaultLoginMode);
		} catch (Exception e) {
			loginMode = defaultLoginMode;
		}
		return loginMode;
	}

	public static void saveLoginAccount(Account loginAccount) {
		String encodePassword = Account.encodePassword(loginAccount
				.getPassword());
		Editor edit = defaultSharedPreferences.edit();
		edit.putString(KEY_LOGIN_EMAIL, loginAccount.getEmail());
		edit.putInt(KEY_LOGIN_ID, loginAccount.getAccountID());
		edit.putString(KEY_LOGIN_PHONE, loginAccount.getPhone());
		edit.putString(KEY_LOGIN_PASSWORD, encodePassword);
		edit.putString(KEY_ACCESS_TOKEN, loginAccount.getAccessToken());
		edit.commit();
	}

	public static Account getLoginAccount() {
		String email = defaultSharedPreferences
				.getString(KEY_LOGIN_EMAIL, null);
		String phone = defaultSharedPreferences
				.getString(KEY_LOGIN_PHONE, null);
		String accountPassword = defaultSharedPreferences.getString(
				KEY_LOGIN_PASSWORD, null);
		String accessToken = defaultSharedPreferences.getString(
				KEY_ACCESS_TOKEN, null);
		Integer id = defaultSharedPreferences.getInt(KEY_LOGIN_ID, 0);
		accountPassword = Account.decodePassword(accountPassword);

		if (accountPassword != null) {
			Account account = new Account();
			account.setPassword(accountPassword);
			account.setEmail(email);
			account.setPhone(phone);
			account.setPassword(accountPassword);
			account.setAccessToken(accessToken);
			account.setAccountID(id);
			return account;
		}
		return null;
	}

	public static void saveLastLoginRecord(String target) {
		Editor edit = defaultSharedPreferences.edit();
		edit.putString(KEY_LOGIN_TARGET, target);
		edit.commit();
	}

	public static String getLastLoginRecord() {
		return defaultSharedPreferences.getString(KEY_LOGIN_TARGET, null);
	}

	public static void clearLoginAccount() {
		Editor edit = defaultSharedPreferences.edit();
		edit.remove(KEY_LOGIN_EMAIL);
		edit.remove(KEY_LOGIN_PHONE);
		edit.remove(KEY_LOGIN_PASSWORD);
		edit.remove(KEY_ACCESS_TOKEN);
		edit.commit();
	}

	public static void clearLoginAccountOldToken() {
		Editor edit = defaultSharedPreferences.edit();
		edit.remove(KEY_LOGIN_PASSWORD);
		edit.remove(KEY_ACCESS_TOKEN);
		edit.commit();
	}

	public static void saveGuideViewVersionCode(int version) {
		Editor edit = defaultSharedPreferences.edit();
		edit.putInt(KEY_PREFACE_VERSION, version);
		edit.commit();
	}

	public static boolean isShowPreface() {
		int oldVersionCode = defaultSharedPreferences.getInt(
				KEY_PREFACE_VERSION, 0);
		int nowVersionCode = GuideBook.PREFACE_VERSION;
		return nowVersionCode > oldVersionCode;
	}

	// TODO 如果用户标注了不需要自动登录，则这边也应该进行标志位判断，默认设为自动登录
	public static boolean isAutoLogin() {
		return getLoginAccount() == null ? false : true;
	}

	// 仅适用于自动登陆时的情景，仅记录最后一次离开的tab
	// public static void saveDefTabPosition(int defPosition) {
	// Editor edit = defaultSharedPreferences.edit();
	// edit.putInt(KEY_DEFTAB_POSITION, defPosition);
	// edit.commit();
	// }

	public static int getDefTabPosition(int defPosition) {
		return defaultSharedPreferences
				.getInt(KEY_DEFTAB_POSITION, defPosition);
	}

	public static void saveSandBox(boolean isSandBox) {
		Editor edit = defaultSharedPreferences.edit();
		edit.putBoolean(KEY_IS_SANDBOX, isSandBox);
		edit.commit();
	}

	public static boolean isSandBox(boolean defSandBox) {
		return defaultSharedPreferences.getBoolean(KEY_IS_SANDBOX, defSandBox);
	}
}

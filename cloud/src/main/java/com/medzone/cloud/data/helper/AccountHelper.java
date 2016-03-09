/**
 * 
 */
package com.medzone.cloud.data.helper;

import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask.Status;
import android.os.Build;
import android.text.TextUtils;
import cn.jpush.android.api.JPushInterface;

import com.medzone.cloud.CloudApplication;
import com.medzone.cloud.CloudApplicationPreference;
import com.medzone.cloud.GlobalVars;
import com.medzone.cloud.cache.AccountCache;
import com.medzone.cloud.data.CurrentAccountManager;
import com.medzone.cloud.data.PropertyCenter;
import com.medzone.cloud.data.UnreadMessageCenter;
import com.medzone.cloud.module.CloudMeasureModuleCentreRoot;
import com.medzone.cloud.module.modules.AccountModule;
import com.medzone.cloud.module.modules.AccountModule.IGetDetailCallBack;
import com.medzone.cloud.network.NetworkClient;
import com.medzone.cloud.task.EditAccountTask;
import com.medzone.cloud.task.GetAccountTask;
import com.medzone.cloud.task.GetMessageCountTask;
import com.medzone.cloud.task.GetOtherAccountTask;
import com.medzone.cloud.task.LoginTask;
import com.medzone.cloud.task.MarkAccountMessageReadedTask;
import com.medzone.cloud.task.VerifyAccountTask;
import com.medzone.cloud.ui.LoginActivity;
import com.medzone.cloud.ui.dialog.global.GlobalDialogUtil;
import com.medzone.cloud.ui.dialog.global.GlobalDialogUtil.onGlobalClickListener;
import com.medzone.framework.Log;
import com.medzone.framework.data.bean.imp.Account;
import com.medzone.framework.data.controller.CentreControllerRoot;
import com.medzone.framework.errorcode.ProxyCode.LocalError;
import com.medzone.framework.errorcode.ProxyErrorCode;
import com.medzone.framework.network.NetworkClientResult;
import com.medzone.framework.task.BaseResult;
import com.medzone.framework.task.Progress;
import com.medzone.framework.task.TaskHost;
import com.medzone.framework.util.TaskUtil;

/***
 * 
 * 封装帐号层级的事情，涉及到基于帐号的逻辑诸如帐号切换等，使用{@link #CurrentAccountManager}
 * 
 */
@SuppressLint("InlinedApi")
public final class AccountHelper {

	private static boolean isKickOffEventExist = false;
	public static final String KICKDIALOG = "kick_dialog";

	public static void setKickOffEventExist(boolean isKickOffEventExist) {
		AccountHelper.isKickOffEventExist = isKickOffEventExist;
	}

	public static boolean isKickOffEventExist() {
		return isKickOffEventExist;
	}

	// ==========================
	// 错误码逻辑判断
	// ==========================
	public static int checkLoginParamsStyle(String accountName,
			String accountPass, String checkCode) {

		final boolean isEmail = isEmailCorrect(accountName);

		if (isEmail) {
			if (TextUtils.isEmpty(accountName)) {
				return LocalError.CODE_EMAIL_EMPTY;
			}
			if (!isEmailCorrect(accountName) && !isPhoneCorrect(accountName)) {
				return LocalError.CODE_ACCOUNT_ILLAGE;
			}

		} else {
			if (TextUtils.isEmpty(accountName)) {
				return LocalError.CODE_PHONE_EMPTY;
			}
			if (!isEmailCorrect(accountName) && !isPhoneCorrect(accountName)) {
				return LocalError.CODE_ACCOUNT_ILLAGE;
			}
		}
		if (TextUtils.isEmpty(accountPass)) {
			return LocalError.CODE_PASSWORD_EMPTY;
		}
		if (!isPasswordCorrect(accountPass)) {
			return LocalError.CODE_PASSWORD_ERROR;
		}
		if (checkCode != null) {
			if (TextUtils.isEmpty(checkCode)) {
				return LocalError.CODE_10076;
			}
		}
		return LocalError.CODE_SUCCESS;
	}

	public static int checkRegisterParamsStyle(String accountName,
			String accountPass, String checkCode) {

		final boolean isEmail = isEmailCorrect(accountName);

		if (isEmail) {
			if (TextUtils.isEmpty(accountName)) {
				return LocalError.CODE_EMAIL_EMPTY;
			}
			if (!isEmailCorrect(accountName) && !isPhoneCorrect(accountName)) {
				return LocalError.CODE_ACCOUNT_ILLAGE;
			}

		} else {
			if (TextUtils.isEmpty(accountName)) {
				return LocalError.CODE_PHONE_EMPTY;
			}
			if (!isEmailCorrect(accountName) && !isPhoneCorrect(accountName)) {
				return LocalError.CODE_ACCOUNT_ILLAGE;
			}
		}
		if (TextUtils.isEmpty(accountPass)) {
			return LocalError.CODE_PASSWORD_EMPTY;
		}
		if (!isPasswordCorrect(accountPass)) {
			return LocalError.CODE_PASSWORD_ILLAGE;
		}
		if (checkCode != null) {
			if (TextUtils.isEmpty(checkCode)) {
				return LocalError.CODE_10076;
			}
		}
		return LocalError.CODE_SUCCESS;
	}

	public static int checkResetParamsStyle(String accountName,
			String accountPass, String checkCode) {

		final boolean isEmail = isEmailCorrect(accountName);

		if (isEmail) {
			if (TextUtils.isEmpty(accountName)) {
				return LocalError.CODE_EMAIL_EMPTY;
			}
			if (!isEmailCorrect(accountName) && !isPhoneCorrect(accountName)) {
				return LocalError.CODE_ACCOUNT_ILLAGE;
			}

		} else {
			if (TextUtils.isEmpty(accountName)) {
				return LocalError.CODE_PHONE_EMPTY;
			}
			if (!isEmailCorrect(accountName) && !isPhoneCorrect(accountName)) {
				return LocalError.CODE_ACCOUNT_ILLAGE;
			}
		}
		if (TextUtils.isEmpty(accountPass)) {
			return LocalError.CODE_10305;
		}
		if (!isPasswordCorrect(accountPass)) {
			return LocalError.CODE_PASSWORD_ILLAGE;
		}
		if (checkCode != null) {
			if (TextUtils.isEmpty(checkCode)) {
				return LocalError.CODE_10076;
			}
		}
		return LocalError.CODE_SUCCESS;
	}

	public static int checkRegisterPersonDetail(String nickname,
			String birthday, String gender) {

		if (TextUtils.isEmpty(nickname)) {
			return LocalError.CODE_NICKNAME_EMPTY;
		}

		if (TextUtils.isEmpty(birthday)) {
			return LocalError.CODE_10212;
		}

		if (TextUtils.isEmpty(gender)) {
			return LocalError.CODE_10213;
		}

		return AccountHelper.checkNickNameStyle(nickname);
	}

	public static int checkAccountNameAvailable(String accountName) {
		if (TextUtils.isEmpty(accountName)) {
			return LocalError.CODE_ACCOUNT_EMPTY;
		}
		if (isEmailCorrect(accountName) == false
				&& isPhoneCorrect(accountName) == false) {
			return LocalError.CODE_ACCOUNT_ILLAGE;
		}
		return LocalError.CODE_SUCCESS;
	}

	/**
	 * 
	 * 
	 * @param accountName
	 * @param checkCode
	 * @return
	 */
	public static int checkResetParam(String accountName, String checkCode) {
		if (TextUtils.isEmpty(accountName)) {
			return LocalError.CODE_ACCOUNT_EMPTY;
		}
		if (isEmailCorrect(accountName) == false
				&& isPhoneCorrect(accountName) == false) {
			return LocalError.CODE_ACCOUNT_ILLAGE;
		}
		if (TextUtils.isEmpty(checkCode)) {
			return LocalError.CODE_10076;
		}
		return LocalError.CODE_SUCCESS;
	}

	public static int checkInfoBindAddressStyle(String Provinces,
			String addressDetail) {
		if (TextUtils.isEmpty(Provinces)) {
			return LocalError.CODE_13109;
		}
		// TODO 详细地址是否可以为空？
		if (TextUtils.isEmpty(addressDetail)) {
			return LocalError.CODE_13112;
		}

		final int addressDetailLen = getContentBytesLength(addressDetail);
		if (addressDetailLen <= 100) {
			if (!isCnEnNumCorrect(addressDetail)) {
				return LocalError.CODE_13110;
			} else {
				return LocalError.CODE_SUCCESS;
			}
		} else {
			return LocalError.CODE_13111;
		}
	}

	/**
	 * 
	 * @param realName
	 * @return
	 */
	public static int checkRealNameStyle(String realName) {

		if (TextUtils.isEmpty(realName)) {
			return LocalError.CODE_REALNAME_EMPTY;
		}

		int realNameLen = getContentBytesLength(realName);
		if (realNameLen <= 12) {

			return isCnEnNumCorrect(realName) ? LocalError.CODE_SUCCESS
					: LocalError.CODE_REALNAME_ILLAGE;
		} else {
			return LocalError.CODE_REALNAME_TOO_LONG;
		}

	}

	public static int checkInfoBindEmail2Style(final String email2) {
		if (TextUtils.isEmpty(email2)) {
			return LocalError.CODE_13105;
		}
		boolean ret = isEmailCorrect(email2);
		return ret ? LocalError.CODE_SUCCESS : LocalError.CODE_13106;
	}

	public static int checkInfoBindPhone2Style(final String phone) {
		if (TextUtils.isEmpty(phone)) {
			return LocalError.CODE_INFO_BIND_PHONE_NULL;
		}
		boolean ret = isPhoneCorrect(phone);
		return ret ? LocalError.CODE_SUCCESS : LocalError.CODE_13107;
	}

	public static int checkChangePasswordStyle(final String olds,
			final String news) {
		if (TextUtils.isEmpty(olds)) {
			return LocalError.CODE_13201;
		}
		if (TextUtils.isEmpty(news)) {
			return LocalError.CODE_RESET_NEW_PASSWORD_NULL;
		}

		String correctPass = CurrentAccountManager.getCurAccount()
				.getPassword();
		if (!olds.equals(correctPass)) {
			return LocalError.CODE_13202;
		}

		if (isPasswordCorrect(news)) {
			return LocalError.CODE_SUCCESS;
		} else {
			return LocalError.CODE_PASSWORD_ILLAGE;
		}
	}

	/**
	 * 
	 * @param str
	 * @return
	 */
	public static int checkNickNameStyle(String str) {

		if (TextUtils.isEmpty(str)) {
			return LocalError.CODE_NICKNAME_EMPTY;
		}
		int len = getContentBytesLength(str);
		if (len <= 12) {
			return isCnEnNumCorrect(str) ? LocalError.CODE_SUCCESS
					: LocalError.CODE_NICKNAME_ILLAGE;
		} else {
			return LocalError.CODE_NICKNAME_TOO_LONG;
		}

	}

	public static int checkIDCardStyle(final String iDCard) {

		if (TextUtils.isEmpty(iDCard)) {
			return LocalError.CODE_INFO_BIND_IDCRAD_NULL;
		}
		return isIDCardCorrect(iDCard) ? LocalError.CODE_SUCCESS
				: LocalError.CODE_INFO_BIND_IDCRAD_ILLAGE;
	}

	// ========================
	// Task Helper
	// ========================

	private static LoginTask mLoginTask;
	private static GetAccountTask mGetAccountTask;
	private static EditAccountTask mEditAccountTask;
	// XXX 因为在注册页面，大量复用了这个接口，并且有多个情景。这些情景是不能被复用的。
	// private static VerifyAccountTask mVerifyAccountTask;
	private static GetOtherAccountTask mGetOtherAccountTask;
	private static GetMessageCountTask mGetMessageCountTask;
	private static MarkAccountMessageReadedTask mMarkAccountMessageReadedTask;

	public static void updateAfterLoginSuccess(NetworkClientResult res,
			Account tmpAccount, IGetDetailCallBack callback) {

		Account loginAccount;
		Account restoreLoginAccount = CurrentAccountManager.getCurAccount();
		// 说明此次登陆时恢复登陆
		if (restoreLoginAccount != null) {
			loginAccount = Account.updateAccount(res, restoreLoginAccount);
		} else {
			loginAccount = Account.createAccount(res);
		}
		// 这时的帐号对象是不完整的
		loginAccount.setEmail(tmpAccount.getEmail());
		loginAccount.setPhone(tmpAccount.getPhone());
		loginAccount.setPassword(tmpAccount.getPassword());
		CloudApplicationPreference.saveLoginAccount(loginAccount);
		CurrentAccountManager.setCurAccount(loginAccount);
		// 以登陆的帐号，申请对应的帐号模块
		AccountModuleHelper.applyAccountModule(CloudApplication.getInstance()
				.getApplicationContext(), loginAccount);
		AccountModuleHelper.get(loginAccount).updateAccountInfo(callback);
	}

	public static void updateLoginAccountInfo(NetworkClientResult res,
			Account account) {
		// parse response result.
		Account loginAccount = Account.updateAccount(res, account);
		// save local preference.
		CloudApplicationPreference.saveLoginAccount(loginAccount);
		// make the current account complete.
		CurrentAccountManager.setCurAccount(loginAccount);
	}

	/**
	 * 获取最后一次离线登陆帐号,仅用于一次性获取，不维护该cache用完即回收
	 * 
	 */
	public static Account getLastestOffLineAccount(Account tmpAccount) {
		AccountCache cache = new AccountCache();

		if (tmpAccount == null) {
			tmpAccount = CloudApplicationPreference.getLoginAccount();
		}
		String phone = tmpAccount.getPhone();
		String email = tmpAccount.getEmail();
		if (!TextUtils.isEmpty(phone)) {
			return cache.readAccountByPhone(phone);
		}
		if (!TextUtils.isEmpty(email)) {
			return cache.readAccountByEmail(email);
		}
		Log.w("Have no history on record！");
		return null;
	}

	/**
	 * 
	 * 请确保在异步线程中调用
	 * 
	 * @param tmpAccount
	 * @return
	 */
	public static NetworkClientResult doLogin(Account tmpAccount) {
		int count = 0;
		int MAX_CONNECT_COUNT = 3;
		NetworkClientResult result = null;
		tmpAccount.setPushID(JPushInterface.getRegistrationID(CloudApplication
				.getInstance().getApplicationContext()));

		while (count < MAX_CONNECT_COUNT) {
			count++;
			result = (NetworkClientResult) NetworkClient.getInstance().doLogin(
					tmpAccount);
			if (result.isSuccess()) {
				CloudApplication.defenderServiceManager.initJPush();
				if (result.isServerDisposeSuccess()) {
					CloudApplication.defenderServiceManager.startJPush();
				}
				break;
			}
		}
		return result;
	}

	public static void doLoginTask(Context context, Account tmpAccount,
			TaskHost taskHost) {

		if (mLoginTask != null && mLoginTask.getStatus() == Status.RUNNING) {
			return;
		}

		tmpAccount.setPushID(JPushInterface.getRegistrationID(CloudApplication
				.getInstance().getApplicationContext()));
		mLoginTask = new LoginTask(context, tmpAccount);
		mLoginTask.setTaskHost(taskHost);
		mLoginTask.execute();
	}

	/**
	 * 获取当前账号，并进行自动续期（JPUSH/AccessToken），若非存在顶号事件，则续期总是会成功。
	 * 
	 * @param context
	 */
	public static void doAutoLoginTask(final Context context) {

		if (mLoginTask != null && mLoginTask.getStatus() == Status.RUNNING) {
			return;
		}

		final Account account = CurrentAccountManager.getCurAccount();
		if (account == null) {
			return;
		}

		account.setPushID(JPushInterface.getRegistrationID(CloudApplication
				.getInstance().getApplicationContext()));
		mLoginTask = new LoginTask(context, account);
		mLoginTask.setTaskHost(new TaskHost() {
			@Override
			public void onPostExecute(int requestCode, BaseResult result) {
				super.onPostExecute(requestCode, result);
				if (result.isSuccess()) {
					NetworkClientResult res = (NetworkClientResult) result;
					if (res.isServerDisposeSuccess()) {
						Log.v(">>>续期恢复登录成功！");
						GlobalVars.setOffLined(false);
						AccountHelper.updateAfterLoginSuccess(
								(NetworkClientResult) result, account,
								new IGetDetailCallBack() {
									@Override
									public void onComplete(int code) {
										switch (code) {
										case BaseResult.DISPOSE_CODE_SUCCESS:
											Log.v(">>>获取账号资料成功！");
											break;
										default:
											Log.v(">>>获取账号资料失败！");
											break;
										}
									}
								});
					} else {
						Log.v("后台自动续期登陆，验证当前账号是否安全>>>"
								+ ProxyErrorCode.getInstance().getErrorMessage(
										ProxyErrorCode.TYPE_LOGIN_AND_REGISTER,
										res.getErrorCode()));
					}
				} else {
					// Net off line.
				}
			}
		});
		mLoginTask.execute();
	}

	public static void doGetAccountTask(Context context, Account tmpAccount,
			TaskHost taskHost) {
		if (mGetAccountTask != null
				&& mGetAccountTask.getStatus() == Status.RUNNING) {
			return;
		}
		mGetAccountTask = new GetAccountTask(context, tmpAccount);
		mGetAccountTask.setTaskHost(taskHost);
		mGetAccountTask.execute();
	}

	public static void doUpdateAccountTask(Context context, Account account,
			TaskHost taskHost) {
		if (mEditAccountTask != null
				&& mEditAccountTask.getStatus() == Status.RUNNING) {
			return;
		}
		mEditAccountTask = new EditAccountTask(context,
				account.getAccessToken(), account);
		mEditAccountTask.setTaskHost(taskHost);
		mEditAccountTask.execute();
	}

	public static void doVerifyAccountTask(Context context, String target,
			String template, Boolean checkExist, String checkCode,
			TaskHost taskHost) {
		VerifyAccountTask mVerifyAccountTask = new VerifyAccountTask(context,
				target, template, checkExist, checkCode);
		mVerifyAccountTask.setTaskHost(taskHost);
		mVerifyAccountTask.execute();
	}

	/**
	 * 获取指定群成员的个人信息
	 * 
	 * @param groupMemberID
	 *            群成员ID
	 * 
	 * @param taskHost
	 */
	public static void doGetAccountDetail(Context context, String target,
			Integer groupID, Progress progress, TaskHost taskHost) {
		String accessToken = CurrentAccountManager.getCurAccount()
				.getAccessToken();
		if (mGetOtherAccountTask != null
				&& mGetOtherAccountTask.getStatus() == Status.RUNNING) {
			return;
		}
		mGetOtherAccountTask = new GetOtherAccountTask(context, accessToken,
				groupID, target);
		if (progress != null) {
			mGetOtherAccountTask.setProgress(progress);
		}
		mGetOtherAccountTask.setTaskHost(taskHost);
		mGetOtherAccountTask.execute();

	}

	public static void doGetMessageCountTask(Context context,
			String accessToken, TaskHost taskHost) {
		if (mGetMessageCountTask != null
				&& mGetMessageCountTask.getStatus() == Status.RUNNING) {
			return;
		}
		mGetMessageCountTask = new GetMessageCountTask(context, accessToken);
		mGetMessageCountTask.setTaskHost(taskHost);
		mGetMessageCountTask.execute();
	}

	/**
	 * 
	 * @param accessToken
	 * @param msgID
	 * @param isRead
	 *            true/已读/false=未读
	 * @param taskHost
	 */
	public static void doMarkNotificationProcessTask(Context context,
			String accessToken, Integer msgID, Boolean isRead, TaskHost taskHost) {
		if (mMarkAccountMessageReadedTask != null
				&& mMarkAccountMessageReadedTask.getStatus() == Status.RUNNING) {
			return;
		}
		mMarkAccountMessageReadedTask = new MarkAccountMessageReadedTask(
				context, accessToken, msgID, isRead, null);
		mMarkAccountMessageReadedTask.setTaskHost(taskHost);
		mMarkAccountMessageReadedTask.execute();
	}

	/**
	 * 
	 * @param accessToken
	 * @param msgID
	 * @param isRead
	 *            Y=已读/N=未读/D=删除
	 * @param taskHost
	 */
	public static void doMarkNotificationProcessTask(Context context,
			String accessToken, Integer msgID, String isRead, TaskHost taskHost) {
		if (mMarkAccountMessageReadedTask != null
				&& mMarkAccountMessageReadedTask.getStatus() == Status.RUNNING) {
			return;
		}
		mMarkAccountMessageReadedTask = new MarkAccountMessageReadedTask(
				context, accessToken, msgID, isRead, null);
		mMarkAccountMessageReadedTask.setTaskHost(taskHost);
		mMarkAccountMessageReadedTask.execute();
	}

	// ==========================
	// 检查正则
	// ==========================

	/**
	 * 6-16位数字英文字母
	 * 
	 * @param pass
	 * @return
	 */
	public static boolean isPasswordCorrect(final String pass) {

		String check = "^[a-zA-Z0-9]{6,16}$";
		Pattern regex = Pattern.compile(check);
		Matcher matcher = regex.matcher(pass);
		return matcher.matches();
	}

	/**
	 * @param accountName
	 * @return
	 */
	public static boolean isEmailCorrect(final String accountName) {
		String check = "^\\w+([-+._]\\w+)*@\\w+([-._]\\w+)*\\.\\w+([-._]\\w+)*$";
		Pattern regex = Pattern.compile(check);
		Matcher matcher = regex.matcher(accountName);
		return matcher.matches();
	}

	/**
	 * 
	 * @param accountName
	 * @return
	 */
	public static boolean isPhoneCorrect(final String accountName) {
		String check = "^1[0-9]{10}$";
		Pattern regex = Pattern.compile(check);
		Matcher matcher = regex.matcher(accountName);
		return matcher.matches();
	}

	/**
	 * 
	 * 检查汉字、数字或英文字母
	 * 
	 * @param content
	 * @return
	 */
	public static boolean isCnEnNumCorrect(final String content) {
		String rex = "^[\u4e00-\u9fa5a-zA-Z0-9]+$";
		Pattern pattern = Pattern.compile(rex);
		Matcher matcher = pattern.matcher(content);
		return matcher.matches();
	}

	public static boolean isUrlCorrect(String url) {
		if (url != null) {
			String check = "(http|ftp|https):\\/\\/[\\w\\-_]+(\\.[\\w\\-_]+)+([\\w\\-\\.,@?^=%&amp;:/~\\+#]*[\\w\\-\\@?^=%&amp;/~\\+#])?";
			Pattern regex = Pattern.compile(check);
			Matcher matcher = regex.matcher(url);
			return matcher.matches();
		}
		return false;
	}

	public static boolean isIDCardCorrect(final String iDCard) {
		String temp = iDCard.toUpperCase();
		// String patrn = "^(\\d{15}$|^\\d{18}$|^\\d{17}(\\d|X|x))$";
		String patrn = "^(\\d{18}$|^\\d{17}(\\d|X|x))$";

		Pattern regex = Pattern.compile(patrn);
		Matcher matcher = regex.matcher(temp);

		return matcher.matches();
	}

	/**
	 * 获取字符串长度，中文=2字节长度
	 * 
	 * @param content
	 * @return
	 */
	public static int getContentBytesLength(final String content) {
		String temp = content;
		try {
			temp = new String(temp.getBytes("gb2312"), "iso-8859-1");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return temp.length();
	}

	public static String subStr(String params, final int maxBytesLen) {
		if (params == null)
			return "";
		else {
			try {
				int tempSubLength = maxBytesLen;
				String ret = params.substring(0,
						params.length() < maxBytesLen ? params.length()
								: maxBytesLen);

				int retSize = getContentBytesLength(ret);

				while (retSize > maxBytesLen) {
					int subSLengthTemp = --tempSubLength;
					ret = params.substring(0,
							subSLengthTemp > params.length() ? params.length()
									: subSLengthTemp);
					retSize = getContentBytesLength(ret);
				}
				return ret;
			} catch (Exception e) {
				return "";
			}
		}
	}

	// ==================================
	// 顶号事件
	// ==================================

	public static void showKickedDialog(final Context context) {

		if (!isKickOffEventExist()) {
			setKickOffEventExist(true);
			CloudApplicationPreference.clearLoginAccountOldToken();

			if (TaskUtil.isRunningForeground(context)) {

				Log.w("key:do action when in forebackground.");
				GlobalDialogUtil.showGlobalAppDialog(context, "提示",
						"您的帐号在别处登录！", new onGlobalClickListener() {

							@Override
							public void onClick() {
								logout(context, true);
								setKickOffEventExist(false);
							}
						});
			} else {
				System.out.println("key:do action when in background.");
				// logout(context, false);
				setKickOffEventExist(false);
				// ActivityManager manager = (ActivityManager) context
				// .getSystemService(Context.ACTIVITY_SERVICE);
				// manager.killBackgroundProcesses(context.getPackageName());
				// System.exit(0);
			}

		}
	}

	public static void logout(Context context, boolean isJumpLoginActivity) {

		try {
			// 即便没有网络，也进行注销JPUSH操作
			CloudApplication.defenderServiceManager.stopJPush();
			CloudApplicationPreference.clearLoginAccount();
			Account curAccount = CurrentAccountManager.getCurAccount();
			AccountModule module = AccountModuleHelper.get(curAccount);
			AccountModuleHelper.remove(module);
			CloudMeasureModuleCentreRoot.removeAll(curAccount);
			CentreControllerRoot.getInstance().removeAllController();
			CurrentAccountManager.setCurAccount(null);
			UnreadMessageCenter.recyle();
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (!TaskUtil.isProcessRunning(context)) {
			return;
		}
		if (isJumpLoginActivity) {
			if (TaskUtil.isRunningForeground(context)) {
				// logout and remove all back stack activity.
				Intent intent = new Intent();
				intent.setClass(context, LoginActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				if (CloudApplication.isNewAPI(Build.VERSION_CODES.HONEYCOMB)) {
					intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
				} else {
					PropertyCenter.getInstance().firePropertyChange(
							PropertyCenter.PROPERTY_FINISH_HOME, null, null);
				}
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				context.startActivity(intent);
			}
		}
	}

	public static void logout(Context context, boolean isJumpLoginActivity,
			boolean isShowKickDialog) {
		try {
			// 即便没有网络，也进行注销JPUSH操作
			CloudApplication.defenderServiceManager.stopJPush();
			CloudApplicationPreference.clearLoginAccount();
			Account curAccount = CurrentAccountManager.getCurAccount();
			AccountModule module = AccountModuleHelper.get(curAccount);
			AccountModuleHelper.remove(module);
			CloudMeasureModuleCentreRoot.removeAll(curAccount);
			CentreControllerRoot.getInstance().removeAllController();
			CurrentAccountManager.setCurAccount(null);
			UnreadMessageCenter.recyle();
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (!TaskUtil.isProcessRunning(context)) {
			return;
		}
		if (isJumpLoginActivity) {
			if (TaskUtil.isRunningForeground(context)) {
				// logout and remove all back stack activity.
				Intent intent = new Intent();
				intent.setClass(context, LoginActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				if (CloudApplication.isNewAPI(Build.VERSION_CODES.HONEYCOMB)) {
					intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
				}
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				intent.putExtra(KICKDIALOG, isShowKickDialog);
				context.startActivity(intent);
			}
		}
	}
}

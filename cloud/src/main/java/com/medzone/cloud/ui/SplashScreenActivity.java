/**
 * 
 */
package com.medzone.cloud.ui;

import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.LinearLayout;
import cn.jpush.android.api.JPushInterface;

import com.medzone.cloud.CloudApplication;
import com.medzone.cloud.CloudApplicationPreference;
import com.medzone.cloud.CloudImageLoader;
import com.medzone.cloud.Constants;
import com.medzone.cloud.GlobalVars;
import com.medzone.cloud.data.CurrentAccountManager;
import com.medzone.cloud.data.PropertyCenter;
import com.medzone.cloud.data.TemporaryData;
import com.medzone.cloud.data.UnreadMessageCenter;
import com.medzone.cloud.data.helper.AccountHelper;
import com.medzone.cloud.data.helper.AccountModuleHelper;
import com.medzone.cloud.data.helper.GroupHelper;
import com.medzone.cloud.database.DatabaseHelper;
import com.medzone.cloud.defender.CloudPush;
import com.medzone.cloud.defender.JPushLocalNotificationHelper;
import com.medzone.cloud.guide.GuideBook;
import com.medzone.cloud.module.modules.AccountModule.IGetDetailCallBack;
import com.medzone.cloud.network.NetworkClient;
import com.medzone.cloud.task.LoadMainInBackgroundTask;
import com.medzone.cloud.ui.dialog.error.ErrorDialogUtil;
import com.medzone.cloud.ui.dialog.global.GlobalDialogUtil;
import com.medzone.cloud.ui.dialog.global.GlobalDialogUtil.onGlobalClickListener;
import com.medzone.cloud.util.GeneralUtil;
import com.medzone.framework.Config;
import com.medzone.framework.Log;
import com.medzone.framework.data.bean.imp.Account;
import com.medzone.framework.data.bean.imp.Group;
import com.medzone.framework.data.bean.imp.GroupMember;
import com.medzone.framework.errorcode.ProxyCode.NetError;
import com.medzone.framework.errorcode.ProxyErrorCode;
import com.medzone.framework.module.ModuleSpecificationXMLUtil;
import com.medzone.framework.network.NetworkClientResult;
import com.medzone.framework.task.BackgroundLoadable;
import com.medzone.framework.task.BaseResult;
import com.medzone.framework.task.TaskHost;
import com.medzone.framework.util.ToastUtils;
import com.medzone.mcloud.R;

/**
 * 
 * 不仅仅负责初始化一些耗时事物，同时也是Intent分发中心，由该类去根据条件选择待跳转的界面
 * 
 */
public class SplashScreenActivity extends BaseActivity implements
		BackgroundLoadable {

	private LinearLayout backgroundLayout;
	private LoadMainInBackgroundTask task;

	@Override
	protected void initUI() {
		setContentView(R.layout.activity_splash);
		backgroundLayout = (LinearLayout) findViewById(R.id.backgroundLayout);
	}

	@Override
	protected void postInitUI() {
		task = new LoadMainInBackgroundTask(this, Constants.SPLASH_TIME);
		task.execute();
	}

	private void startAnimation() {
		AlphaAnimation mSplash = new AlphaAnimation(0.1f, 1.0f);
		mSplash.setDuration(Constants.SPLASH_TIME);
		backgroundLayout.startAnimation(mSplash);
	}

	@Override
	public void onPreLoad() {
		startAnimation();
	}

	private void initLongRunningOperations() {
		// Initialize the database.
		DatabaseHelper.init(getApplicationContext());
		// Initializes the network client.
		NetworkClient.init(getApplicationContext());
		// Initialize the help guide.
		GuideBook.init(getApplicationContext());

		// Initialize the image loader.
		CloudImageLoader.getInstance().initImageLoader(getApplicationContext());
		// Initialize the XML-UTILS.
		ModuleSpecificationXMLUtil.init(getApplicationContext());
	}

	// 最长耗时为9秒
	private void checkApiCompatibility() {
		GeneralUtil.checkServerApiVersion(getApplicationContext());
	}

	private void CheckIsOfflineLogin() {
		boolean isOfflineLogin = true;
		boolean isAutoLogin = CloudApplicationPreference.isAutoLogin();
		if (isAutoLogin) {
			Account tmpAccount = CloudApplicationPreference.getLoginAccount();

			NetworkClientResult loginRes = AccountHelper.doLogin(tmpAccount);
			if (loginRes.isSuccess()) {
				if (loginRes.isServerDisposeSuccess()) {
					Log.v("Automatic login successfully!");
					AccountHelper.updateAfterLoginSuccess(loginRes, tmpAccount,
							new IGetDetailCallBack() {

								@Override
								public void onComplete(int code) {
									switch (code) {
									case BaseResult.DISPOSE_CODE_SUCCESS:
										GlobalVars.setOffLined(false);
										PropertyCenter
												.getInstance()
												.firePropertyChange(
														PropertyCenter.PROPERTY_REFRESH_ACCOUNT,
														null, null);
										break;
									default:
										break;
									}

								}
							});
				} else {
					if (loginRes.getErrorCode() == NetError.CODE_40504) {
						AccountHelper.setKickOffEventExist(true);
					}
				}

			} else {
				Log.v("Automatic landing network anomalies, switching to offline automatic landing!");

				if (CurrentAccountManager.getCurAccount() == null) {

					if (isOfflineLogin && tmpAccount != null) {
						Log.v("Offline log in successfully!");
						GlobalVars.setOffLined(true);
						CurrentAccountManager.setCurAccount(tmpAccount);
						AccountModuleHelper.applyAccountModule(
								getApplicationContext(),
								CurrentAccountManager.getCurAccount());
					} else {
						Log.w("Automatic landing network connection timeout!");
					}
				} else {
					Log.w("Restore login……");
				}
			}
		}
	}

	@Override
	public View loadInBackground() {

		initLongRunningOperations();

		checkApiCompatibility();

		CheckIsOfflineLogin();

		return null;
	}

	@Override
	public void onPostLoad(View view) {

		if (Config.isDeveloperMode) {
			if (NetworkClient.isSandBox()) {
				ToastUtils.show(this, "当前为沙箱模式");
			} else {
				ToastUtils.show(this, "当前为生产模式");
			}
		}
		finish();
		jumpOnPostload();
	}

	@Override
	protected void onResume() {
		super.onResume();
		JPushInterface.onResume(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		JPushInterface.onPause(this);
	}

	private void jumpOnPostload() {

		if (AccountHelper.isKickOffEventExist()) {
			GlobalDialogUtil.showGlobalDialog(CloudApplication.getInstance()
					.getApplicationContext(), "提示", "您的帐号在别处登录！",
					new onGlobalClickListener() {

						@Override
						public void onClick() {
							// 否则下次不会弹出顶号对话框
							AccountHelper.setKickOffEventExist(false);
						}
					});
		}

		if (CurrentAccountManager.getCurAccount() != null) {

			if (isNotificationBarClick()) {
				if (TemporaryData.containsKey(CloudPush.class.getName())) {
					CloudPush push = (CloudPush) TemporaryData
							.get(CloudPush.class.getName());
					preDoActionCloudPush(push);
				} else {
					jumpMainTabsActivity();
				}
			} else {
				jumpMainTabsActivity();
			}
		} else {
			jumpLoginActivity();
		}

	}

	// ================================
	// 处理通知栏跳转
	// ================================

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		Log.e("#>>>onNewIntent");
	}

	private boolean isNotificationBarClick() {
		Intent i = getIntent();
		if (i.hasExtra(CloudPush.tag)) {
			Bundle bundle = i.getBundleExtra(CloudPush.tag);
			CloudPush push = new CloudPush(bundle);
			TemporaryData.save(CloudPush.class.getName(), push);
			return true;
		}
		return false;
	}

	private void preDoActionCloudPush(CloudPush push) {

		int intentType = JPushLocalNotificationHelper.getIntentType(push);
		switch (intentType) {
		case JPushLocalNotificationHelper.TYPE_INTENT_CHATROOM:
			loadMustGroupList(push.getExtraGroupID(),
					push.getExtraMessageSendID());
			break;
		case JPushLocalNotificationHelper.TYPE_INTENT_NOTIFY_LIST:
			jumpNotifyListActivity();
			break;
		case JPushLocalNotificationHelper.TYPE_INTENT_NOTIFY_LOGIN:
			Log.v("kicked:open notification then show kicked dialog.");
			AccountHelper.doAutoLoginTask(this);
			break;
		default:
			break;
		}
	}

	private void loadMustGroupList(final int interlocutorGroupID,
			final int interlocutorID) {
		// 获取群列表，匹配获取制定群id详情
		GroupHelper.doGetAllGroupTask(this, CurrentAccountManager
				.getCurAccount().getAccessToken(), new TaskHost() {
			@Override
			public void onPostExecute(int requestCode, BaseResult result) {
				super.onPostExecute(requestCode, result);
				if (result.isSuccess()) {
					NetworkClientResult res = (NetworkClientResult) result;
					if (res.isServerDisposeSuccess()) {
						List<Group> list = Group.createGroupList(res,
								CurrentAccountManager.getCurAccount()
										.getAccountID());
						GroupHelper.setList(list);
						UnreadMessageCenter
								.requestUnReadChatMessageByCollect(list);

						Group group = GroupHelper
								.getGroupByID(interlocutorGroupID);

						if (group != null) {
							TemporaryData.save(Group.class.getName(), group);

							if (interlocutorID != Account.INVALID_ID) {
								loadMustGroupMemberList(interlocutorGroupID,
										interlocutorID);
							} else {
								jumpChatRoomActivity();
							}

						} else {
							Log.w(">>>因为参数不足，放弃跳转到群聊天页面。");
						}
					} else {
						ErrorDialogUtil.showErrorDialog(
								SplashScreenActivity.this,
								ProxyErrorCode.TYPE_GROUP, res.getErrorCode(),
								true);
					}
				}

			}
		});
	}

	private void loadMustGroupMemberList(final int interlocutorGroupID,
			final int interlocutorID) {

		String target = String.valueOf(interlocutorID);
		AccountHelper.doGetAccountDetail(this, target, interlocutorGroupID,
				null, new TaskHost() {
					public void onPostExecute(int requestCode, BaseResult result) {
						if (result.isSuccess()) {
							NetworkClientResult res = (NetworkClientResult) result;
							if (res.isServerDisposeSuccess()) {
								GroupMember groupMember = GroupMember
										.createGroupMember(
												res.getResponseResult(),
												interlocutorGroupID,
												interlocutorID);
								Group bindGroup = GroupHelper
										.getGroupByID(interlocutorGroupID);
								groupMember.setBindGroup(bindGroup);
								TemporaryData.save(Group.class.getName(),
										bindGroup);
								TemporaryData.save(GroupMember.class.getName(),
										groupMember);
								jumpChatRoomActivity();
							} else {
								ErrorDialogUtil.showErrorDialog(
										SplashScreenActivity.this,
										ProxyErrorCode.TYPE_GROUP,
										res.getErrorCode(), true);
							}
						}
					};
				});
	}

	private void jumpChatRoomActivity() {
		Intent intent = new Intent(this, GroupChatActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		startActivity(intent);
	}

	private void jumpMainTabsActivity() {
		Intent intent = new Intent(this, MainTabsActivity.class);
		startActivity(intent);
	}

	private void jumpLoginActivity() {
		Intent intent = new Intent(this, LoginActivity.class);
		startActivity(intent);
	}

	private void jumpNotifyListActivity() {
		Intent intent = new Intent(this, GroupNotifyActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		startActivity(intent);
	}
}

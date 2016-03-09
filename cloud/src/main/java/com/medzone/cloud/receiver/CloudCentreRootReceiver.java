/**
 * 
 */
package com.medzone.cloud.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.medzone.cloud.CloudApplication;
import com.medzone.cloud.GlobalVars;
import com.medzone.cloud.data.PropertyCenter;
import com.medzone.cloud.data.UnreadMessageCenter;
import com.medzone.cloud.data.helper.AccountHelper;
import com.medzone.cloud.defender.BroadCastUtil;
import com.medzone.cloud.defender.CloudPush;
import com.medzone.framework.Config;
import com.medzone.framework.util.ToastUtils;

/**
 * 
 * 只有当应用进程存在的时候，该广播接收器才会接收到指定广播。
 * 
 * @author ChenJunQi.
 * 
 */
public class CloudCentreRootReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(final Context context, Intent intent) {

		String action = intent.getAction();

		if (Config.isDeveloperMode) {
			ToastUtils.show(context, action);
		}

		if (TextUtils.equals(action, BroadCastUtil.ACTION_REGISTRATION_ID)) {

			AccountHelper.doAutoLoginTask(context);

		} else if (TextUtils.equals(action, BroadCastUtil.ACTION_CHECKUPDATE)) {

			notifierCheckUpdate();

		} else if (TextUtils.equals(action, BroadCastUtil.ACTION_NET_CONNECTED)) {

			AccountHelper.doAutoLoginTask(context);
			notifierConnectionStateChanged(true);

		} else if (TextUtils.equals(action,
				BroadCastUtil.ACTION_NET_DISCONNECTED)) {

			GlobalVars.setOffLined(true);
			CloudApplication.defenderServiceManager.stopJPush();
			notifierConnectionStateChanged(false);

		} else if (TextUtils.equals(action,
				BroadCastUtil.ACTION_PUSH_LOGIN_INVALID)) {

			AccountHelper.doAutoLoginTask(context);

		} else if (TextUtils.equals(action,
				BroadCastUtil.ACTION_PUSH_GROUP_POST_MESSAGE)) {

			CloudPush push = new CloudPush(intent.getExtras());
			receiveNewChatMessage(context, push);

		} else if (TextUtils
				.equals(action, BroadCastUtil.ACTION_PUSH_GROUP_ADD)) {

			CloudPush push = new CloudPush(intent.getExtras());
			receiveActionNotificationMessage(context, push);

		} else if (TextUtils.equals(action,
				BroadCastUtil.ACTION_PUSH_GROUP_KICKED)) {

			CloudPush push = new CloudPush(intent.getExtras());
			doActionGroupDel(push, false, false);
			receiveActionNotificationMessage(context, push);

		} else if (TextUtils.equals(action,
				BroadCastUtil.ACTION_PUSH_GROUP_QUITED)) {

			CloudPush push = new CloudPush(intent.getExtras());
			doActionGroupDel(push, false, true);
			receiveActionNotificationMessage(context, push);

		} else if (TextUtils.equals(action,
				BroadCastUtil.ACTION_PUSH_GROUP_DISMISS)) {
			CloudPush push = new CloudPush(intent.getExtras());
			doActionGroupDel(push, true, false);
			receiveActionNotificationMessage(context, push);

		} else if (TextUtils.equals(action,
				BroadCastUtil.ACTION_PUSH_GROUP_FEEDBACK)) {

			CloudPush push = new CloudPush(intent.getExtras());
			receiveActionNotificationMessage(context, push);
		}

	}

	// private void receiveActionGroupDel(final Context context,
	// final CloudPush push, final boolean isDissmissed,
	// final boolean isQuited) {
	//
	// final String accessToken = CurrentAccountManager.getCurAccount()
	// .getAccessToken();
	// final int groupID = push.getExtraGroupID();
	// final int memberID = push.getExtraNotifyMemberID();
	// GroupHelper.doGetMemberPermissionTask(null, accessToken, groupID,
	// memberID, new TaskHost() {
	// @Override
	// public void onPostExecute(int requestCode, BaseResult result) {
	// super.onPostExecute(requestCode, result);
	// if (result.isSuccess()) {
	// NetworkClientResult res = (NetworkClientResult) result;
	// if (res.isServerDisposeSuccess()) {
	// // 标记该条推送的状态无效
	// } else {
	// if (res.getErrorCode() == NetError.CODE_43201
	// || res.getErrorCode() == NetError.CODE_43202) {
	// doActionGroupDel(push, isDissmissed,
	// isQuited);
	// }
	// }
	// }
	// }
	// });
	// }

	private void doActionGroupDel(CloudPush push, boolean isDissmissed,
			boolean isQuited) {
		if (isDissmissed) {
			doActionDissmissed(push);
		} else if (isQuited) {
			doActionQuited(push);
		} else {
			doActionKicked(push);
		}
	}

	private void doActionQuited(CloudPush push) {
		PropertyCenter.getInstance().firePropertyChange(
				PropertyCenter.PROPERTY_NOTIFY_GROUP_QUITED, null, push);
	}

	private void doActionKicked(CloudPush push) {
		PropertyCenter.getInstance().firePropertyChange(
				PropertyCenter.PROPERTY_NOTIFY_GROUP_KICKED, null, push);
	}

	private void doActionDissmissed(CloudPush push) {
		PropertyCenter.getInstance().firePropertyChange(
				PropertyCenter.PROPERTY_NOTIFY_GROUP_DISSMISS, null, push);
	}

	private void receiveActionNotificationMessage(Context context,
			CloudPush push) {

		if (push.getExtraMessageID() != 0) {
			UnreadMessageCenter.addNotification(push.getExtraMessageID());
			PropertyCenter.getInstance().firePropertyChange(
					PropertyCenter.PROPERTY_REFRESH_GROUPNOTIFY, null, push);
		}

	}

	private void receiveNewChatMessage(Context context, CloudPush push) {

		if (push.getExtraMessageSendID() != 0) {

			final int msgID = push.getExtraMessageID();
			final int groupID = push.getExtraGroupID();
			final int groupType = push.getExtraGroupType();

			UnreadMessageCenter.addChatMessage(groupType, groupID,
					push.getExtraMessageSendID(), msgID);
			PropertyCenter.getInstance().firePropertyChange(
					PropertyCenter.PROPERTY_REFRESH_CHATROOM, null, null);
		}

	}

	private void notifierCheckUpdate() {
		PropertyCenter.getInstance().firePropertyChange(
				PropertyCenter.PROPERTY_CHECK_UPDATE, null, null);
	}

	private void notifierConnectionStateChanged(boolean connectState) {
		PropertyCenter.getInstance().firePropertyChange(
				PropertyCenter.PROPERTY_CONNECT_STATE, null, connectState);
	}

}

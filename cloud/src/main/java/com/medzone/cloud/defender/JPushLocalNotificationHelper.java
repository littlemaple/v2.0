package com.medzone.cloud.defender;

import java.util.LinkedList;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.data.JPushLocalNotification;

import com.medzone.cloud.CloudApplication;
import com.medzone.cloud.Constants;
import com.medzone.cloud.data.PropertyCenter;
import com.medzone.cloud.data.helper.GroupHelper;
import com.medzone.cloud.ui.GroupChatActivity;
import com.medzone.cloud.ui.SplashScreenActivity;
import com.medzone.framework.Log;
import com.medzone.framework.util.TaskUtil;

public final class JPushLocalNotificationHelper {

	public static final String LOGIN_KICKED = "kicked";// 登录踢号
	public static final String GROUP_MESSAGE = "group_message";// 发布群消息
	public static final String GROUP_INVITE = "invite_group";// 邀请入群
	public static final String GROUP_KCIKED = "group_kicked";// 踢人
	public static final String GROUP_QUIT = "group_quit";// 退群
	public static final String GROUP_JOIN = "group_join";// 同意/拒绝入群
	public static final String GROUP_DISMISS = "group_dismiss";// 解散群

	// =============================
	// 管理本地消息队列
	// =============================

	// FIFO 溢出逻辑
	private static LinkedList<CloudPush> notificationQueue = new LinkedList<CloudPush>();

	// FIXME 设置的NS_MAX_DISPLAY_NUMBER无效
	private static void addFromQueue(CloudPush push, boolean isFirstAdd) {
		if (notificationQueue.size() >= CloudPush.NS_MAX_DISPLAY_NUMBER) {
			// CloudPush p = notificationQueue.getFirst();
			// JPushInterface.removeLocalNotification(CloudApplication
			// .getInstance().getApplicationContext(), p.getPushID());
			notificationQueue.removeFirst();
		}
		if (!isFirstAdd) {
			notificationQueue.addLast(push);
		} else {
			notificationQueue.addFirst(push);
		}
	}

	private static void removeFromQueue(CloudPush push) {
		if (notificationQueue.size() <= 0) {
			return;
		}
		notificationQueue.remove(push);
	}

	// ============================
	// 处理通知创建事件
	// ============================

	/**
	 * 根据JPUSH返回的自定义消息，构建本地通知进行展示。 目前均为标准样式，所以暂不设置BuilderID
	 * 
	 * @param push
	 * @return
	 */
	private static void createNotification(CloudPush push) {

		JPushLocalNotification notifacation = new JPushLocalNotification();

		notifacation.setNotificationId(push.getPushID());
		notifacation.setTitle(push.getPushTitle());
		notifacation.setContent(push.getPushContent());
		notifacation.setExtras(push.getPushExtraJsonString());
		notifacation.setBroadcastTime(System.currentTimeMillis());

		JPushInterface.addLocalNotification(CloudApplication.getInstance()
				.getApplicationContext(), notifacation);
		addFromQueue(push, false);
	}

	public static void showNotification(Context context, Bundle bundle) {

		CloudPush push = new CloudPush(bundle);

		doActionByApplicationState(context, push, new OnApplicationState() {

			@Override
			public void onProcessExisted(Context context, CloudPush push) {

				sendBroadcast(context, getAction(push.getBundle()),
						push.getBundle());

				String type = push.getExtraType();

				if (TextUtils.equals(type, GROUP_MESSAGE)) {

					if (!isRunningForeground(context)) {
						if (push.getExtraGroupAlert() == Constants.ALERT_POPUP) {
							createNotification(push);
						}
					}

				} else if (TextUtils.equals(type, LOGIN_KICKED)
						|| TextUtils.equals(type, GROUP_INVITE)) {
					if (!isRunningForeground(context)) {
						createNotification(push);
					}
				}
			}

			@Override
			public void onProcessDestoryed(Context context, CloudPush push) {

				String type = push.getExtraType();
				if (TextUtils.equals(type, GROUP_MESSAGE)) {

					if (push.getExtraGroupAlert() == Constants.ALERT_POPUP) {
						createNotification(push);
					}

				} else if (TextUtils.equals(type, GROUP_INVITE)
						|| TextUtils.equals(type, LOGIN_KICKED)) {
					createNotification(push);
				}
			}
		});
	}

	protected static void createNotificationExceptCurrentPage(Context context,
			CloudPush push) {

		if (isCurrentPage(context, push)) {
			Log.d("Detect page current" + push.getExtraType()
					+ ",don't create notification.");
		} else {
			createNotification(push);
		}
	}

	/**
	 * 一般情况下，进程存在才会给进程内的广播接收器发送广播事件，调用该方法时，需注意是否依赖进程存在。
	 * 
	 * @param context
	 * @param action
	 * @param bundle
	 */
	public static void sendBroadcast(Context context, String action,
			Bundle bundle) {
		Intent intent = new Intent(action);
		if (bundle != null) {
			intent.putExtras(bundle);
		}
		context.sendBroadcast(intent);
	}

	// ============================
	// 处理通知打开事件
	// ============================

	// 根据以下3个情况处理通知栏打开效果：
	// 1.软件进程不在；
	// 2.软件进程在，点击通知栏直接恢复到当前页面了，需求是跳转到指定页面
	// 3.软件进程在，且在当前页面即为指定页面，则不进行通知栏呈现。
	public static void openNotification(Context context, Bundle bundle) {

		CloudPush push = new CloudPush(bundle);
		removeFromQueue(push);

		doActionByApplicationState(context, push, new OnApplicationState() {

			@Override
			public void onProcessDestoryed(Context context, CloudPush push) {
				openNotificationProcessDestoryed(context, push);
			}

			@Override
			public void onProcessExisted(Context context, CloudPush push) {
				openNotificationProcessExisted(context, push);
			}
		});
	}

	private static void openNotificationProcessDestoryed(Context context,
			CloudPush push) {

		Intent i = new Intent();
		i.setClass(context, SplashScreenActivity.class);
		i.addCategory(Intent.CATEGORY_LAUNCHER);
		i.addCategory(Intent.ACTION_MAIN);
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
				| Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
		i.putExtra(CloudPush.tag, push.getBundle());
		context.startActivity(i);
	}

	private static void openNotificationProcessExisted(Context context,
			CloudPush push) {

		// 目前只有群聊天推送，存在如果点开通知要跳转的聊天室界面，即为当前页面的情况。
		if (isCurrentPage(context, push)) {
			openNotificationProcessExistCurrentPage(context, push);
		} else {
			openNotificationProcessExistNotCurrentPage(context, push);
		}

	}

	// 2.软件进程在，点击通知栏直接恢复到当前页面了，需求是跳转到指定页面
	private static void openNotificationProcessExistNotCurrentPage(
			Context context, CloudPush push) {

		Intent i = new Intent();
		i.setClass(context, SplashScreenActivity.class);
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		i.putExtra(CloudPush.tag, push.getBundle());
		context.startActivity(i);

	}

	// 3.软件进程在，且在当前页面即为指定页面
	// 通知已经存在，但是页面已经被切换成即将要跳转的当前页面，那么直接跳转为当前页面即可。
	private static void openNotificationProcessExistCurrentPage(
			Context context, CloudPush push) {

		Intent i = new Intent();
		i.setClass(context, SplashScreenActivity.class);
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
				| Intent.FLAG_ACTIVITY_SINGLE_TOP);
		context.startActivity(i);
	}

	// ==================================
	// 工具类
	// ==================================

	/** 打印所有的 intent extra 数据 */
	public static String printBundle(Bundle bundle) {
		if (bundle == null) {
			return new String("");
		}
		StringBuilder sb = new StringBuilder();
		for (String key : bundle.keySet()) {
			if (key.equals(JPushInterface.EXTRA_NOTIFICATION_ID)) {
				Log.v("\nkey:" + key + ", value:" + bundle.getInt(key));
			} else if (key.equals(JPushInterface.EXTRA_CONNECTION_CHANGE)) {
				Log.v("\nkey:" + key + ", value:" + bundle.getBoolean(key));
			} else {
				Log.v("\nkey:" + key + ", value:" + bundle.getString(key));
			}
		}
		return sb.toString();
	}

	/** 获取广播分类，Util For JPushReceiver.class */
	public static String getAction(Bundle bundle) {
		String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
		if (!TextUtils.isEmpty(extras)) {
			try {
				JSONObject extraJson = new JSONObject(extras);
				if (extraJson.has(CloudPush.EXTRA_KEY_TYPE)
						&& !extraJson.isNull(CloudPush.EXTRA_KEY_TYPE)) {
					String type = extraJson.getString(CloudPush.EXTRA_KEY_TYPE);
					return getAction(type);
				}

			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return new String("");
	}

	private static String getAction(String type) {
		if (TextUtils.equals(type, LOGIN_KICKED)) {
			return BroadCastUtil.ACTION_PUSH_LOGIN_INVALID;

		} else if (TextUtils.equals(type, GROUP_MESSAGE)) {
			return BroadCastUtil.ACTION_PUSH_GROUP_POST_MESSAGE;

		} else if (TextUtils.equals(type, GROUP_INVITE)) {
			return BroadCastUtil.ACTION_PUSH_GROUP_ADD;

		} else if (TextUtils.equals(type, GROUP_KCIKED)) {
			return BroadCastUtil.ACTION_PUSH_GROUP_KICKED;

		} else if (TextUtils.equals(type, GROUP_DISMISS)) {
			return BroadCastUtil.ACTION_PUSH_GROUP_DISMISS;

		} else if (TextUtils.equals(type, GROUP_QUIT)) {
			return BroadCastUtil.ACTION_PUSH_GROUP_QUITED;

		} else if (TextUtils.equals(type, GROUP_JOIN)) {
			return BroadCastUtil.ACTION_PUSH_GROUP_FEEDBACK;
		}
		return new String("");
	}

	private static boolean isRunningForeground(Context context) {
		return TaskUtil.isRunningForeground(context);
	}

	private static boolean isCurrentPage(Context context, CloudPush push) {

		String topClassName = TaskUtil.getTopActivityName(context);

		if (TextUtils.equals(push.getExtraType(), GROUP_MESSAGE)) {
			String chatRoomClassName = GroupChatActivity.class
					.getCanonicalName();

			if (TextUtils.equals(chatRoomClassName, topClassName)
					&& GroupHelper.isCurrentInterlocutor(
							push.getExtraMessageSendID(),
							push.getExtraGroupID(), push.getExtraGroupType())) {

				PropertyCenter.getInstance().firePropertyChange(
						PropertyCenter.PROPERTY_NOTIFY_GROUP_CHATMESSAGE, null,
						null);

				return true;
			}
		}
		return false;
	}

	protected interface OnApplicationState {

		public void onProcessDestoryed(Context context, CloudPush push);

		public void onProcessExisted(Context context, CloudPush push);

	}

	public static void doActionByApplicationState(Context context,
			CloudPush push, OnApplicationState i) {
		if (!TaskUtil.isProcessRunning(context)) {
			i.onProcessDestoryed(context, push);
		} else {
			i.onProcessExisted(context, push);
		}
	}

	// ===============================
	// 获取跳转意图分类,Util For SplashScreenActivity.class
	// ===============================

	public static final int TYPE_INTENT_INVALID = 0x100;
	public static final int TYPE_INTENT_CHATROOM = TYPE_INTENT_INVALID + 1;
	public static final int TYPE_INTENT_NOTIFY_LIST = TYPE_INTENT_INVALID + 2;
	public static final int TYPE_INTENT_NOTIFY_DETAIL = TYPE_INTENT_INVALID + 3;
	public static final int TYPE_INTENT_NOTIFY_LOGIN = TYPE_INTENT_INVALID + 4;

	public static int getIntentType(CloudPush push) {

		String type = push.getExtraType();
		if (TextUtils.equals(type, LOGIN_KICKED)) {

			return TYPE_INTENT_NOTIFY_LOGIN;

		} else if (TextUtils.equals(type, GROUP_MESSAGE)) {

			return TYPE_INTENT_CHATROOM;

		} else if (TextUtils.equals(type, GROUP_INVITE)
				|| TextUtils.equals(type, GROUP_KCIKED)
				|| TextUtils.equals(type, GROUP_QUIT)
				|| TextUtils.equals(type, GROUP_JOIN)
				|| TextUtils.equals(type, GROUP_DISMISS)) {
			return TYPE_INTENT_NOTIFY_LIST;
		}
		return TYPE_INTENT_INVALID;
	}
}

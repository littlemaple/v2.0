package com.medzone.cloud.data.helper;

import java.util.List;

import android.content.Context;
import android.os.AsyncTask.Status;
import android.text.TextUtils;

import com.medzone.cloud.CloudApplication;
import com.medzone.cloud.data.CurrentAccountManager;
import com.medzone.cloud.task.DelGroupMemberTask;
import com.medzone.cloud.task.EditGroupMemberPermissionTask;
import com.medzone.cloud.task.GetAllGroupMemberTask;
import com.medzone.cloud.task.GetAllGroupMemberTestTask;
import com.medzone.cloud.task.GetAllGroupTask;
import com.medzone.cloud.task.GetAuthoForMeTask;
import com.medzone.cloud.task.GetAuthorizedListTask;
import com.medzone.cloud.task.GetGroupCareListTask;
import com.medzone.cloud.task.GetGroupMessageCountTask;
import com.medzone.cloud.task.GetGroupTask;
import com.medzone.cloud.task.PostGroupMessageTask;
import com.medzone.cloud.task.ShareUrlRecordTask;
import com.medzone.framework.data.bean.imp.Account;
import com.medzone.framework.data.bean.imp.Group;
import com.medzone.framework.data.bean.imp.GroupMember;
import com.medzone.framework.errorcode.ProxyCode;
import com.medzone.framework.errorcode.ProxyCode.LocalError;
import com.medzone.framework.task.TaskHost;
import com.medzone.mcloud.R;

public class GroupHelper {

	// ============================
	// 判断群聊天页面是否为当前页面
	// ============================
	private static long interlocutorID = Account.INVALID_ID;
	private static long interlocutorGroupID = Group.INVALID_ID;

	private static long lastSeconds = 0;
	private static final int INTERVAL_SECOND = 2000;

	public static void setCurrentInterlocutorID(long accountID, long groupID) {
		interlocutorID = accountID;
		interlocutorGroupID = groupID;
	}

	public static boolean isCurrentInterlocutor(long accountID, long groupID,
			int groupType) {

		if (groupType == Group.TYPE_SERVICE) {
			if (accountID == CurrentAccountManager.getCurAccount()
					.getAccountID()) {
				accountID = -1;
			}
		} else {
			accountID = -1;
		}

		if (accountID == interlocutorID && groupID == interlocutorGroupID) {
			return true;
		}
		return false;
	}

	/***
	 * 
	 * 
	 * 检查当前登陆者是否为指定群id的群主
	 * 
	 * @param groupid
	 * @return
	 */
	public static boolean isServerGroupOwner(int groupid) {
		Group group = getGroupByID(groupid);
		if (group != null) {
			if (group.getCreatorID() == CurrentAccountManager.getCurAccount()
					.getAccountID()) {
				return true;
			}
		}
		return false;
	}

	// ============================
	// 缓存群列表对象
	// ============================

	private static List<Group> list;

	public static void setList(List<Group> list) {
		GroupHelper.list = list;
	}

	public static List<Group> getList() {
		return list;
	}

	public static boolean isServerGroup(int groupid) {
		if (list == null || list.size() < 0) {
			return false;
		}
		for (Group group : list) {
			if (group.getGroupID().intValue() == groupid) {
				if (group.getType().intValue() == Group.TYPE_SERVICE) {
					return true;
				} else {
					return false;
				}
			}
		}
		return false;
	}

	public static Group getGroupByID(int groupid) {
		if (list == null || list.size() < 0) {
			return null;
		}
		for (Group group : list) {
			if (group.getGroupID().intValue() == groupid) {
				return group;
			}
		}
		return null;
	}

	// ============================
	// 匹配规则
	// ============================

	/**
	 * 群名称的格式限制是不超过20个字节
	 * 
	 * @param groupname
	 * @return
	 */
	public static int checkGroupNameStyle(final String groupname) {
		if (TextUtils.isEmpty(groupname)) {
			return LocalError.CODE_12101;
		}

		int length = AccountHelper.getContentBytesLength(groupname);
		if (length < 21) {
			boolean ret = AccountHelper.isCnEnNumCorrect(groupname);
			return ret ? LocalError.CODE_SUCCESS : LocalError.CODE_12102;
		} else {
			return LocalError.CODE_12103;
		}
	}

	/**
	 * 
	 * @param description
	 * @return
	 */
	public static int checkGroupDescription(final String description) {
		if (TextUtils.isEmpty(description)) {
			return LocalError.CODE_SUCCESS;
		}
		int length = AccountHelper.getContentBytesLength(description);
		if (length < 71) {
			return LocalError.CODE_SUCCESS;
		} else {
			return LocalError.CODE_GROUP_INTRODUCE_TOO_LONG;
		}

	}

	public static int checkInviteAccountName(final String accountName) {
		if (TextUtils.isEmpty(accountName)) {
			return LocalError.CODE_12201;
		}

		if (AccountHelper.isEmailCorrect(accountName)
				|| AccountHelper.isPhoneCorrect(accountName)) {
			return LocalError.CODE_SUCCESS;
		}
		return LocalError.CODE_12202;
	}

	public static int checkChatContent(final String content) {

		if (TextUtils.isEmpty(content)) {
			return LocalError.CODE_12302;
		}
		if (TextUtils.isEmpty(content.trim())) {
			return LocalError.CODE_12303;
		}
		if (lastSeconds == 0) {
			lastSeconds = System.currentTimeMillis();
			if (isChatContentValid(content))
				return LocalError.CODE_SUCCESS;
			return LocalError.CODE_12301;
		} else {
			long currentSeconds = System.currentTimeMillis();
			if (currentSeconds - lastSeconds < INTERVAL_SECOND) {
				return ProxyCode.LocalError.CODE_12304;
			} else {
				lastSeconds = currentSeconds;
				if (isChatContentValid(content))
					return LocalError.CODE_SUCCESS;
				return LocalError.CODE_12301;
			}
		}
	}

	private static boolean isChatContentValid(String content) {
		int length = AccountHelper.getContentBytesLength(content);
		if (length <= CloudApplication.getInstance().getApplicationContext()
				.getResources().getInteger(R.integer.limit_chat_content))
			return true;
		return false;
	}

	public static int checkRemarkStyle(final String remark) {

		if (TextUtils.isEmpty(remark)) {
			return LocalError.CODE_SUCCESS;
		}
		int length = AccountHelper.getContentBytesLength(remark);
		if (length <= CloudApplication.getInstance().getApplicationContext()
				.getResources().getInteger(R.integer.limit_remark)) {
			boolean ret = AccountHelper.isCnEnNumCorrect(remark);
			return ret ? LocalError.CODE_SUCCESS : LocalError.CODE_12401;
		} else {
			return LocalError.CODE_12402;
		}
	}

	// ============================
	// 异步任务
	// ============================

	private static GetAuthoForMeTask mGetAuthoForMeTask;
	private static PostGroupMessageTask mP2PPostGroupMessageTask;
	private static DelGroupMemberTask mDelGroupMemberTask;
	private static ShareUrlRecordTask mShareUrlRecordTask;
	private static GetAllGroupMemberTestTask mGetAllGroupMemberTestTask;
	private static GetAuthorizedListTask mGetAuthorizedListTask;
	private static EditGroupMemberPermissionTask mEditGroupMemberPermissionTask;
	private static GetAllGroupMemberTask mGetAllGroupMemberTask;
	private static GetGroupTask mGetGroupTask;
	private static GetAllGroupTask mGetAllGroupTask;
	private static GetGroupCareListTask mGetGroupCareListTask;
	private static GetGroupMessageCountTask mGetGroupMessageCountTask;

	/**
	 * 
	 * 
	 * access_token true string 在登录授权后得到，参见如何登入。 groupid true int 要退或踢人的群 ID。
	 * syncid false int 要踢的人的 ID，如果自己退群此项不需要填写。
	 * 
	 */
	public static void groupMemberDel(Context context, String accessToken,
			Integer groupID, Integer syncID, TaskHost taskHost) {
		if (mDelGroupMemberTask != null
				&& mDelGroupMemberTask.getStatus() == Status.RUNNING) {
			return;
		}
		mDelGroupMemberTask = new DelGroupMemberTask(context, accessToken,
				groupID, syncID);
		mDelGroupMemberTask.setTaskHost(taskHost);
		mDelGroupMemberTask.execute();
	}

	/**
	 * 
	 * 发送群消息
	 * 
	 * @param curAccount
	 * @param groupid
	 * @param data
	 * @param type
	 *            TYPE_NORMAL/TYPE_LINK/TYPE_RECORD/TYPE_NOTIFY
	 */
	public static void sendMessageTask(Context context, Account curAccount,
			Integer groupid, String data, Integer type, TaskHost taskHost) {
		String accessToken = curAccount.getAccessToken();
		Integer syncid = null;
		// CustomDialogProgressWithImage progress = new
		// CustomDialogProgressWithImage(
		// context, context.getResources().getDrawable(
		// drawable.set_ic_load));

		PostGroupMessageTask mPostGroupMessageTask = new PostGroupMessageTask(
				context, accessToken, syncid, groupid, data, type);
		// mPostGroupMessageTask.setProgress(null);
		mPostGroupMessageTask.setTaskHost(taskHost);
		mPostGroupMessageTask.execute();
	}

	/**
	 * 
	 * 发送群内P2P消息
	 * 
	 * @param curAccount
	 * @param groupid
	 * @param data
	 * @param type
	 *            TYPE_NORMAL/TYPE_LINK/TYPE_RECORD/TYPE_NOTIFY
	 */
	public static void sendP2PMessageTask(Context context, Account curAccount,
			Integer groupid, Integer syncid, String data, Integer type,
			TaskHost taskHost) {
		if (mP2PPostGroupMessageTask != null
				&& mP2PPostGroupMessageTask.getStatus() == Status.RUNNING) {
			return;
		}
		// CustomDialogProgressWithImage progress = new
		// CustomDialogProgressWithImage(
		// context, context.getResources().getDrawable(
		// drawable.set_ic_load));
		String accessToken = curAccount.getAccessToken();
		mP2PPostGroupMessageTask = new PostGroupMessageTask(context,
				accessToken, syncid, groupid, data, type);
		mP2PPostGroupMessageTask.setTaskHost(taskHost);
		mP2PPostGroupMessageTask.execute();
	}

	/**
	 * 
	 * @param accessToken
	 * @param type
	 *            要查看的测量类型，bp = 血压，et = 耳温，oxy = 血氧。
	 * @param recordId
	 * @param recent
	 * @param month
	 * @param taskHost
	 */
	public static void doShareUrlRecordTask(Context context,
			String accessToken, String type, Integer recordId,
			String recentUID, String month, TaskHost taskHost/*
															 * , Progress
															 * progress
															 */) {

		if (mShareUrlRecordTask != null
				&& mShareUrlRecordTask.getStatus() == Status.RUNNING) {
			return;
		}
		mShareUrlRecordTask = new ShareUrlRecordTask(context, accessToken,
				type, recordId, recentUID, month);
		mShareUrlRecordTask.setTaskHost(taskHost);
		mShareUrlRecordTask.execute();
	}

	public static void doGetMeasureList(Context context, String accessToken,
			TaskHost taskHost) {
		if (mGetAllGroupMemberTestTask != null
				&& mGetAllGroupMemberTestTask.getStatus() == Status.RUNNING) {
			return;
		}
		mGetAllGroupMemberTestTask = new GetAllGroupMemberTestTask(context,
				accessToken);
		mGetAllGroupMemberTestTask.setTaskHost(taskHost);
		mGetAllGroupMemberTestTask.execute();
	}

	public static void doGetAuthorizedListTask(Context context,
			String accessToken, TaskHost taskHost) {
		if (mGetAuthorizedListTask != null
				&& mGetAuthorizedListTask.getStatus() == Status.RUNNING) {
			return;
		}
		mGetAuthorizedListTask = new GetAuthorizedListTask(context, accessToken);
		mGetAuthorizedListTask.setTaskHost(taskHost);
		mGetAuthorizedListTask.execute();
	}

	public static void doGetReserveAuthorizedListTask(Context context,
			String accessToken, GroupMember member, TaskHost taskHost) {
		if (mGetAuthoForMeTask != null
				&& mGetAuthoForMeTask.getStatus() == Status.RUNNING) {
			return;
		}
		mGetAuthoForMeTask = new GetAuthoForMeTask(context, accessToken, member);
		mGetAuthoForMeTask.setTaskHost(taskHost);
		mGetAuthoForMeTask.execute();
	}

	/**
	 * 目前这个接口不仅承载获取权限的功能，同时也承载判断对象是否在群内的功能
	 * 
	 * @param context
	 * @param accessToken
	 * @param groupID
	 * @param memberID
	 * @param taskHost
	 */
	public static void doGetMemberPermissionTask(Context context,
			String accessToken, int groupID, int memberID, TaskHost taskHost) {
		if (mEditGroupMemberPermissionTask != null
				&& mEditGroupMemberPermissionTask.getStatus() == Status.RUNNING) {
			return;
		}
		mEditGroupMemberPermissionTask = new EditGroupMemberPermissionTask(
				context,
				CurrentAccountManager.getCurAccount().getAccessToken(),
				memberID, groupID);
		mEditGroupMemberPermissionTask.setTaskHost(taskHost);
		mEditGroupMemberPermissionTask.execute();
	}

	public static void doGetMembersList(Context context, String accessToken,
			int groupID, TaskHost taskHost) {
		if (mGetAllGroupMemberTask != null
				&& mGetAllGroupMemberTask.getStatus() == Status.RUNNING) {
			return;
		}
		mGetAllGroupMemberTask = new GetAllGroupMemberTask(context,
				accessToken, groupID);
		mGetAllGroupMemberTask.setTaskHost(taskHost);
		mGetAllGroupMemberTask.execute();
	}

	public static void doGetGroupTask(Context context, String accessToken,
			int groupID, TaskHost taskHost) {
		if (mGetGroupTask != null
				&& mGetGroupTask.getStatus() == Status.RUNNING) {
			return;
		}
		mGetGroupTask = new GetGroupTask(context, accessToken, groupID);
		mGetGroupTask.setTaskHost(taskHost);
		mGetGroupTask.execute();
	}

	public static void doGetAllGroupTask(Context context, String accessToken,
			TaskHost taskHost) {
		if (mGetAllGroupTask != null
				&& mGetAllGroupTask.getStatus() == Status.RUNNING) {
			return;
		}
		mGetAllGroupTask = new GetAllGroupTask(context, accessToken);
		mGetAllGroupTask.setTaskHost(taskHost);
		mGetAllGroupTask.execute();
	}

	public static void doGetGroupCareListTask(Context context,
			String accessToken, TaskHost taskHost) {
		if (mGetGroupCareListTask != null
				&& mGetGroupCareListTask.getStatus() == Status.RUNNING) {
			return;
		}
		mGetGroupCareListTask = new GetGroupCareListTask(context, accessToken);
		mGetGroupCareListTask.setTaskHost(taskHost);
		mGetGroupCareListTask.execute();
	}

	/**
	 * 获取未读/已读消息数目，不传groupid，则获取所有群级别的groupid。否则，获取服务群群内的消息数目。
	 * 
	 * @param accessToken
	 * @param groupid
	 * @param unRead
	 * @param taskHost
	 */
	public static void doGetGroupMessageCount(Context context,
			String accessToken, Integer groupid, boolean unRead,
			TaskHost taskHost) {
		if (mGetGroupMessageCountTask != null
				&& mGetGroupMessageCountTask.getStatus() == Status.RUNNING) {
			return;
		}
		mGetGroupMessageCountTask = new GetGroupMessageCountTask(context,
				accessToken, null, groupid, null, null, true);
		mGetGroupMessageCountTask.setTaskHost(taskHost);
		mGetGroupMessageCountTask.execute();
	}
}

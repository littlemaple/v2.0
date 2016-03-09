package com.medzone.cloud.data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONException;
import org.json.JSONObject;

import com.medzone.cloud.data.helper.AccountHelper;
import com.medzone.cloud.data.helper.GroupHelper;
import com.medzone.framework.Log;
import com.medzone.framework.data.bean.imp.Account;
import com.medzone.framework.data.bean.imp.Group;
import com.medzone.framework.data.bean.imp.GroupMember;
import com.medzone.framework.network.NetworkClientResult;
import com.medzone.framework.task.BaseResult;
import com.medzone.framework.task.TaskHost;
import com.medzone.framework.util.JSONUtils;

/**
 * 仅在有无新消息标志位变更的时候，才通知红点进行刷新呈现
 * 
 * 
 * @author junqi
 * 
 */
public final class UnreadMessageCenter {

	private final static String NORMAL_TYPE_STR = "normal";
	private final static String SERVICE_TYPE_STR = "service";
	private final static String PLACEHOLDER = "#";

	private final static String NOTIFICATION_KEY = "notification_key";
	/**
	 * 临时占位符，用于统计未读消息次数
	 */
	// private final static int PLACEHOLDER_VALUE = -0x999;
	// private static HashMap<String, List<Integer>> unReadMessage = new
	// HashMap<String, List<Integer>>();
	private static HashMap<String, Integer> unReadMessageCounts = new HashMap<String, Integer>();

	public static synchronized void print(String clientName) {
		Log.v(clientName + "#");
		Set<String> keySet = unReadMessageCounts.keySet();
		if (keySet.isEmpty()) {
			return;
		}
		for (String key : keySet) {
			Log.v(clientName + "#" + key + "#中包含新消息数目>>>" + getValue(key));
		}
	}

	private static Integer getValue(String key) {
		if (!unReadMessageCounts.containsKey(key)) {
			unReadMessageCounts.put(key, 0);
		}
		return unReadMessageCounts.get(key);
	}

	private static void putValue(String key, int value) {

		boolean isChanged = isValueStateChanged(getValue(key), value);
		if (isChanged) {
			unReadMessageCounts.put(key, value);
			if (NOTIFICATION_KEY.equals(key)) {
				noticeNotiicationRefresh();
			} else {
				noticeChatMessageRefresh();
			}
		}
		Integer num = unReadMessageCounts.get(NOTIFICATION_KEY);
		if (num != null && num > 0) {
			noticeNotiicationRefresh();
		}
	}

	private static boolean isValueStateChanged(int oldValue, int newValue) {
		if (oldValue == 0 && newValue != 0) {
			return true;
		}
		if (oldValue != 0 && newValue == 0) {
			return true;
		}
		return false;
	}

	/**
	 * 
	 * @param groupID
	 * @param interlocutorID
	 *            -1 表示该对话是群对话级别,0表示是管理员级别,其它>0数值表示服务群群主发起的对话
	 * @return "service#109#26","service#109#-1","normal#109#-1"
	 */
	private static String getSparseKey(int type, int groupID, int interlocutorID) {

		String typeStr;
		String ret = null;

		if (CurrentAccountManager.getCurAccount() != null) {

			if (type == Group.TYPE_SERVICE) {
				typeStr = SERVICE_TYPE_STR;

				if (interlocutorID == CurrentAccountManager.getCurAccount()
						.getAccountID()) {
					interlocutorID = -1;
				}
			} else {
				typeStr = NORMAL_TYPE_STR;
				interlocutorID = -1;
			}

			String retGroupid = String.valueOf(groupID);
			String retSyncid = String.valueOf(interlocutorID);

			ret = typeStr + PLACEHOLDER + retGroupid + PLACEHOLDER + retSyncid;
		} else {
			Log.w("getSparseKey#CurrentAccountManager.getCurAccount() is null.");
		}
		return ret;
	}

	private static boolean isGroupList(Object obj) {
		if (obj instanceof Group) {
			return true;
		} else if (obj instanceof GroupMember) {
			return false;
		}
		throw new IllegalArgumentException(
				"requestUnReadChatMessage#请传入正确的类型，Group或者GroupMember");
	}

	/**
	 * @param params
	 *            群列表或者群成员列表
	 */
	public static void requestUnReadChatMessageByCollect(List<?> params) {

		Log.w("requestUnReadChatMessageByCollect#");

		if (CurrentAccountManager.getCurAccount() == null) {
			return;
		}
		if (params == null || params.size() == 0) {
			return;
		}
		Boolean isGroupList = null;
		for (Object obj : params) {
			if (isGroupList == null) {
				isGroupList = isGroupList(obj);
			}
			if (isGroupList) {
				// 更新群集合
				updateUnReadMessageCountsByGroup((Group) obj);
			} else {
				// TODO 更新群成员集合,但是API没有提供到指定的member下面的未读数量
			}
		}
	}

	/**
	 * 从服务端恢复上次的未读消息数据,不传groupid为group级别，传了groupid，则为服务群群主
	 */
	public static void requestUnReadChatMessage(final Integer groupid) {
		Log.w("requestUnReadChatMessage#groupid:" + groupid);
		if (GroupHelper.getList() == null) {
			return;
		}
		final Account mAccount = CurrentAccountManager.getCurAccount();
		GroupHelper.doGetGroupMessageCount(null, mAccount.getAccessToken(),
				groupid, true, new TaskHost() {
					@Override
					public void onPostExecute(int requestCode, BaseResult result) {
						super.onPostExecute(requestCode, result);
						if (result.isSuccess()
								&& result.isServerDisposeSuccess()) {
							NetworkClientResult res = (NetworkClientResult) result;
							JSONObject json = res.getResponseResult();
							if (groupid != null) {
								try {
									// 获取服务群内
									if (json.has("members")
											&& !json.isNull("members")) {

										JSONObject jo = json
												.getJSONObject("members");
										Map<String, String> unReadCountsMap = JSONUtils
												.parseKeyAndValueToMap(jo);
										updateUnReadMessageCountsByMemberCollect(
												groupid.intValue(),
												unReadCountsMap);
										// 当服务群群主身份下，会构造一个为-1的群未读消息，此时获取到群成员列表就该移除-1的标示
										// 因为普通群群成员，只有一个对话，所以默认为-1（实际可以考虑使用其他标志位区分）
										removeServiceHelperValue(groupid
												.intValue());

									}
								} catch (JSONException e) {
									e.printStackTrace();
								}
							} else {
								try {
									// 获取所有群消息
									if (json.has("groups")
											&& !json.isNull("groups")) {
										JSONObject jo = json
												.getJSONObject("groups");
										Map<String, String> unReadCountsMap = JSONUtils
												.parseKeyAndValueToMap(jo);
										updateUnReadMessageCountsByGroupCollect(unReadCountsMap);
									}
								} catch (JSONException e) {
									e.printStackTrace();
								}
							}
						}
					}
				});
	}

	private static void updateUnReadMessageCountsByGroup(Group group) {
		Log.w("updateUnReadMessageCountsByGroup#");
		int groupID = group.getGroupID().intValue();
		int type = group.getType();
		String sparseKey = getSparseKey(type, groupID, -1);
		int value = group.getUnReadMessageNum();
		putValue(sparseKey, value);
	}

	private static void updateUnReadMessageCountsByGroupCollect(
			Map<String, String> unReadCountsMap) {

		Log.w("updateUnReadMessageCountsByGroupCollect#");

		for (String key : unReadCountsMap.keySet()) {
			int groupID = Integer.valueOf(key);
			int type = GroupHelper.isServerGroup(groupID) ? Group.TYPE_SERVICE
					: Group.TYPE_NORMAL;
			// XXX 因为服务端的接口返回是群级别的，导致这里服务群群主身份下的集合信息是所有对话的总数。
			String sparseKey = getSparseKey(type, groupID, -1);
			int value = Integer.valueOf(unReadCountsMap.get(key));
			putValue(sparseKey, value);
		}
	}

	// private void updateUnReadMessageCountsByMember(GroupMember member) {
	// int type = Group.TYPE_SERVICE;
	// int syncid = member.getAccountID().intValue();
	// int value = Integer.valueOf(unReadCountsMap.get(key));
	// }

	private static void updateUnReadMessageCountsByMemberCollect(
			final int groupID, Map<String, String> unReadCountsMap) {

		Log.w("updateUnReadMessageCountsByMemberCollect#");

		for (String key : unReadCountsMap.keySet()) {
			int type = Group.TYPE_SERVICE;
			int syncid = Integer.valueOf(key);
			int value = Integer.valueOf(unReadCountsMap.get(key));
			String sparseKey = getSparseKey(type, groupID, syncid);
			putValue(sparseKey, value);
		}
	}

	// ======================================
	// 群聊天中心
	// ======================================

	private static synchronized void noticeChatMessageRefresh() {
		PropertyCenter.getInstance().firePropertyChange(
				PropertyCenter.PROPERTY_NOTIFY_GROUP_CHATMESSAGE, null, null);
	}

	/**
	 * 为指定对话场景新消息记录容器中，增加一条新消息记录。
	 * 
	 * @param groupID
	 * @param interlocutorID
	 * @param msgID
	 */
	public static synchronized void addChatMessage(int type, int groupID,
			int interlocutorID, int msgID) {

		if (type == Group.TYPE_SERVICE) {
			if (!GroupHelper.isServerGroupOwner(groupID)) {
				interlocutorID = -1;
			}
		}

		Log.w("addChatMessage#groupID:" + groupID + ",interlocutorID："
				+ interlocutorID);

		String key = getSparseKey(type, groupID, interlocutorID);
		int chatMessage = getValue(key);
		putValue(key, ++chatMessage);
	}

	/**
	 * 为指定对话场景新消息记录容器中，移除所有新消息记录。
	 * 
	 * @param groupID
	 * @param interlocutorID
	 *            -1 表示该对话是群对话级别,0表示是管理员级别,其它>0数值表示服务群群主发起的对话
	 * 
	 */
	public static synchronized void removeInterlocutorChatMessage(int type,
			int groupID, int interlocutorID) {
		Log.e("removeInterlocutorChatMessage#type:" + type + "|groupID:"
				+ groupID + "|interlocutorID:" + interlocutorID);
		// 移除指定群内的所有对话记录，包括普通群/服务群（群主/群成员）
		if (type == Group.TYPE_SERVICE) {

			// if (!GroupHelper.isServerGroupOwner(groupID)) {
			// removeServiceChatMessage(type, groupID);
			// } else {
			removeInterlocutors(type, groupID, interlocutorID);

			Log.w("检测到该次对话是服务群群主身份下的#移除-1的辅助数据");
			// 服务群群主身份下，-1是为了兼容服务端列表只返回我群级别的信息做作的辅助变量
			// 当对服务群进行移除时，应该置空该变量
			String key = getSparseKey(type, groupID, -1);
			putValue(key, 0);
			// }
		} else {
			removeInterlocutors(type, groupID, interlocutorID);
		}
	}

	/**
	 * 因为API的缺陷，所以才提供了此方法，用于清除错误的群组新消息统计信息。请在进入群成员列表界面时调用该方法，以确保冗余的群组（群主身份）
	 * 数据能够得到清除的机会。
	 * 
	 * @param groupID
	 */
	private static synchronized void removeServiceHelperValue(int groupID) {
		Log.w("removeServiceHelperValue#groupID:" + groupID);
		removeInterlocutors(Group.TYPE_SERVICE, groupID, -1);
	}

	/**
	 * 移除指定对话场景下的所有新消息
	 * 
	 * @param type
	 * @param groupID
	 * @param interlocutorID
	 */
	private static synchronized void removeInterlocutors(int type, int groupID,
			int interlocutorID) {
		Log.e("removeInterlocutors#type:" + type + "|groupID:" + groupID
				+ "|interlocutorID:" + interlocutorID);
		String key = getSparseKey(type, groupID, interlocutorID);
		putValue(key, 0);
	}

	/**
	 * 群主身份，同一个groupid可能对应多条记录，这里采用遍历方式去移除。
	 * 
	 * @param groupID
	 */
	private static void removeServiceChatMessage(int type, int groupID) {

		Log.e("removeServiceChatMessage#type:" + type + "|groupID:" + groupID);

		String tag = SERVICE_TYPE_STR + PLACEHOLDER + String.valueOf(groupID)
				+ PLACEHOLDER;

		Set<String> keySet = unReadMessageCounts.keySet();
		if (keySet.isEmpty()) {
			return;
		}
		for (String key : keySet) {
			if (key.startsWith(tag)) {
				putValue(key, 0);
			}
		}
		String key = getSparseKey(type, groupID, -1);
		putValue(key, 0);
	}

	/**
	 * 获取指定对话下的新消息集数目，服务群（非群主）也可调用本方法
	 * 
	 * @param type
	 * @param groupID
	 * @param interlocutorID
	 * @return
	 */
	public static int getChatMessageCollectLength(int type, int groupID,
			int interlocutorID) {

		Log.w("getChatMessageCollectLength#");

		String key = getSparseKey(type, groupID, interlocutorID);
		return getValue(key);
	}

	/**
	 * （群主）获取指定服务群下所有对话的新消息集合数目
	 * 
	 * @return
	 */
	public static int getChatMessageCollectLengthByServiceOwner(int type,
			int groupid) {

		Log.w("getChatMessageCollectLengthByServiceOwner#");

		if (type != Group.TYPE_SERVICE) {
			throw new IllegalArgumentException("请传入正确的参数，确保type类型为服务群类型！");
		}
		int ret = 0;
		String tag = SERVICE_TYPE_STR + PLACEHOLDER + String.valueOf(groupid)
				+ PLACEHOLDER;

		Set<String> keySet = unReadMessageCounts.keySet();
		if (keySet.isEmpty()) {
			return ret;
		}
		for (String key : keySet) {
			if (key.startsWith(tag)) {
				ret += getValue(key);
			}
		}

		return ret;
	}

	/**
	 * 获取指定群类别下所有对话的的新消息总数目
	 * 
	 * @param type
	 * @return
	 */
	public static int getChatMessageCollectLength(int type) {
		Log.w("getChatMessageCollectLength#");

		int ret = 0;
		String tag = NORMAL_TYPE_STR;
		if (type == Group.TYPE_SERVICE) {
			tag = SERVICE_TYPE_STR;
		}
		tag += PLACEHOLDER;
		Set<String> keySet = unReadMessageCounts.keySet();
		if (keySet.isEmpty()) {
			return ret;
		}
		for (String key : keySet) {
			if (key.startsWith(tag)) {
				ret += getValue(key);
			}
		}
		return ret;
	}

	// ======================================
	// 群消息通知中心
	// ======================================

	/**
	 * 从服务端获取最新未读通知的数量
	 */
	public static void requestUnReadNotification() {

		putValue(NOTIFICATION_KEY, 0);

		final Account mAccount = CurrentAccountManager.getCurAccount();
		if (mAccount == null) {
			return;
		}
		AccountHelper.doGetMessageCountTask(null, mAccount.getAccessToken(),
				new TaskHost() {
					@Override
					public void onPostExecute(int requestCode, BaseResult result) {
						super.onPostExecute(requestCode, result);
						if (result.isSuccess()) {
							NetworkClientResult res = (NetworkClientResult) result;
							if (res.isServerDisposeSuccess()) {
								try {

									JSONObject json = res.getResponseResult();
									if (json.has("unread")
											&& !json.isNull("unread")) {
										int unread = json.getInt("unread");
										putValue(NOTIFICATION_KEY, unread);
									}
								} catch (JSONException e) {
									e.printStackTrace();
								}
							}
						}
					}
				});

	}

	private static void noticeNotiicationRefresh() {
		PropertyCenter.getInstance()
				.firePropertyChange(
						PropertyCenter.PROPERTY_REFRESH_NOTIFICATIONMESSAGE,
						null, null);
	}

	public static void addNotification(int msgID) {
		Log.e("key#addNotification()#" + msgID);
		String key = NOTIFICATION_KEY;
		int notificationCounts = getValue(key);
		putValue(key, ++notificationCounts);
	}

	public static void removeAllNotification() {
		Log.e("key#removeAllNotification()");
		String key = NOTIFICATION_KEY;
		putValue(key, 0);
	}

	public static int getNewNotiicationCollectLength() {
		String key = NOTIFICATION_KEY;
		int ret = getValue(key);
		return ret;
	}

	public static void recyle() {
		if (unReadMessageCounts != null) {
			unReadMessageCounts.clear();
		}
	}
}

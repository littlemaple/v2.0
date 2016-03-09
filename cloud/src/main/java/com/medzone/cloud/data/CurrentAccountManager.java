package com.medzone.cloud.data;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import com.medzone.cloud.data.helper.GroupHelper;
import com.medzone.framework.data.bean.imp.Account;
import com.medzone.framework.data.bean.imp.GroupMember;
import com.medzone.framework.network.NetworkClientResult;
import com.medzone.framework.task.BaseResult;
import com.medzone.framework.task.TaskHost;

/**
 * 
 * @author ChenJunQi.
 * 
 *         1. 登录成功isLoginSuccess= true 2. 登陆失败isLoginSuccess= false &&
 *         curAccount == null 3. 离线后信息登陆，但未登陆成功isLoginSuccess= false &&
 *         curAccount != null
 * 
 */
public final class CurrentAccountManager {

	private static Account curAccount;
	private static List<GroupMember> curAuthorizedMembers = new LinkedList<GroupMember>();
	private static List<GroupMember> measureList = new ArrayList<GroupMember>();
	private static List<GroupMember> viewList = new ArrayList<GroupMember>();

	private CurrentAccountManager() {
	}

	public static void setCurAccount(Account mAccountBean) {
		if (mAccountBean != CurrentAccountManager.curAccount) {
			CurrentAccountManager.curAccount = mAccountBean;
			// TODO 通知主框架与插件模块，帐号信息已经变更
		}
	}

	public static Account getCurAccount() {
		return curAccount;
	}

	/**
	 * 
	 * @param isForceRead
	 *            如果设置为true，则调用API更新代测列表
	 * @param isLocked
	 *            如果设置为true，则阻塞时调用，等待API有返回时，该方法才进行返回
	 * @return
	 */
	public static List<GroupMember> getAuthorizedList(
			final boolean isForceRead, final boolean isLocked) {

		final CountDownLatch latch = new CountDownLatch(1);

		if (isForceRead) {
			GroupHelper.doGetAuthorizedListTask(null, getCurAccount()
					.getAccessToken(), new TaskHost() {
				@Override
				public void onPostExecute(int requestCode, BaseResult result) {
					super.onPostExecute(requestCode, result);
					if (result.isSuccess()) {
						NetworkClientResult res = (NetworkClientResult) result;
						if (res.isServerDisposeSuccess()) {
							curAuthorizedMembers = GroupMember
									.createGroupMemberList(res,
											CurrentAccountManager
													.getCurAccount()
													.getAccountID());

						}
					}
					if (isLocked) {
						latch.countDown();
					}

				}
			});
			if (isLocked) {
				try {
					latch.await(2000, TimeUnit.MILLISECONDS);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			return curAuthorizedMembers;
		} else {
			if (curAuthorizedMembers == null) {
				getAuthorizedList(true, true);
			}
			return curAuthorizedMembers;
		}
	}

	public static List<GroupMember> getMeasureList() {
		if (curAuthorizedMembers == null) {
			getAuthorizedList(true, true);
		}
		measureList.clear();
		for (GroupMember am : curAuthorizedMembers) {
			if (am.isMeasure()) {
				measureList.add(am);
			}
		}
		return measureList;
	}

	public static List<GroupMember> getViewList() {
		if (curAuthorizedMembers == null) {
			getAuthorizedList(true, true);
		}
		viewList.clear();
		for (GroupMember am : curAuthorizedMembers) {
			if (am.isViewHistory()) {
				viewList.add(am);
			}
		}
		return viewList;
	}

	public static GroupMember getAuthorizedMember(GroupMember member) {

		GroupMember retAm = null;

		List<GroupMember> memberList = CurrentAccountManager.getAuthorizedList(
				false, false);
		for (GroupMember GroupMember : memberList) {
			if (GroupMember.equals(member)) {
				retAm = GroupMember;
				break;
			}
		}
		if (retAm == null) {
			retAm = new GroupMember();
			retAm.setMeasure(false);
			retAm.setViewHistory(false);
		}
		return retAm;
	}
}

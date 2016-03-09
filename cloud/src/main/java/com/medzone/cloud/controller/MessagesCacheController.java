/**
 * 
 */
package com.medzone.cloud.controller;

import java.util.List;
import java.util.Observer;

import com.medzone.cloud.cache.MessageCache;
import com.medzone.cloud.data.CurrentAccountManager;
import com.medzone.cloud.data.UnreadMessageCenter;
import com.medzone.cloud.task.BaseGetControllerDataTask;
import com.medzone.cloud.task.GetGroupMessageListTask;
import com.medzone.cloud.task.ProcessNewItemsTaskHost;
import com.medzone.framework.data.bean.imp.Message;
import com.medzone.framework.data.bean.imp.MessageSession;
import com.medzone.framework.data.navigation.LongStepable;
import com.medzone.framework.data.navigation.Paging;
import com.medzone.framework.task.Progress;
import com.medzone.framework.task.TaskHost;

/**
 * @author ChenJunQi.
 * 
 */
public class MessagesCacheController
		extends
		AbstractUsePagingTaskCacheController<Message, LongStepable, MessageCache> {

	/**
	 * 
	 */
	private Observer newDataLoadedObserver;
	/**
	 * 用来标识是否是新数据的加载行为，新数据的加载行为需要做特殊处理， 当前的特殊处理是用newDataLoadedObserver来实现加载到后的通知
	 * 只要实现了setNewDataLoadedObserver就可监测到是新数据的了。
	 */
	private boolean isNewDataLoaded = true;
	private boolean isFirstReadData = true;

	@Override
	protected MessageCache createCache() {
		return new MessageCache();
	}

	@Override
	protected void cacheChanged() {
		super.cacheChanged();

		if (isNewDataLoaded) {
			isNewDataLoaded = false;
			if (newDataLoadedObserver != null) {
				newDataLoadedObserver.update(this, null);
			}
		}
	}

	/**
	 * 
	 * @param sessionID
	 *            对话id
	 * @param groupID
	 *            群id
	 */
	public void setInterlocutorID(long interlocutorID, long groupID,
			int groupType) {

		// 如果是同一段对话，则不清除cache
		if (interlocutorID == sCache.getInterlocutorID()) {
			return;
		}
		clearCache();
		sCache.setInterlocutorID(interlocutorID);
		sCache.setGroupID(groupID);
		sCache.setGroupType(groupType);
		firstReadLocalData();
	}

	@Override
	protected boolean firstReadLocalData() {
		List<Message> list = sCache.read();
		if (list != null && list.size() > 0) {
			isNewDataLoaded = true;
		}
		return addItemsToCacheTail(list);
	}

	@Override
	protected LongStepable getStepable(Message item) {

		return new LongStepable(item.getMessageID());
	}

	@Override
	protected boolean executeDataLoadingTask(
			int mBaseGetControllerDataTaskType, Progress progress,
			TaskHost taskHost, Paging<LongStepable> paging, Message divider) {
		if (!isTaskCanExec(mBaseGetControllerDataTask)) {
			if (progress != null) {
				progress.finishProgress();
			}
			return false;
		}
		mBaseGetControllerDataTask = createGetDataTask(paging);
		mBaseGetControllerDataTask.setProgress(progress);
		mBaseGetControllerDataTask.setTaskHost(taskHost);
		// 处理未读消息
		// mBaseGetControllerDataTask
		// .setPriorityHost(baseGetControllerDataTaskPriorityHost);
		final int mBaseGetControllerDataTaskType2 = mBaseGetControllerDataTaskType;
		mBaseGetControllerDataTask
				.setProcessNewItemsTaskHost(new ProcessNewItemsTaskHost<Message>() {

					@Override
					public void onPostExecuteProcessBalance(List<Message> list) {
						computeTime();
						if (mBaseGetControllerDataTaskType2 == GET_DOWN_PAGING_ITEMS_FROM_SERVER
								&& (list == null || list.size() < 1)) {
							// no data get from server.
						}
						switch (mBaseGetControllerDataTaskType2) {
						case GET_UP_PAGING_ITEMS_FROM_SERVER:

							if (list != null && list.size() > 0) {
								isNewDataLoaded = true;
							}
							addItemsToCacheTail(list);
							noticeUnReadMessageCenter();
							break;
						case GET_DOWN_PAGING_ITEMS_FROM_SERVER:
							if (isFirstReadData) {
								isFirstReadData = false;
								isNewDataLoaded = true;
							}
							addItemsToCacheHead(list);
							noticeUnReadMessageCenter();
							break;
						default:
							break;
						}
					}
				});
		mBaseGetControllerDataTask.execute();
		return true;
	}

	private void noticeUnReadMessageCenter() {
		UnreadMessageCenter.removeInterlocutorChatMessage(
				sCache.getGroupType(), (int) sCache.getGroupID(),
				(int) sCache.getInterlocutorID());
	}

	@Override
	protected BaseGetControllerDataTask<Message> createGetDataTask(
			Object... params) {
		@SuppressWarnings("unchecked")
		Paging<LongStepable> paging = (Paging<LongStepable>) params[0];

		return new GetGroupMessageListTask(null,
				CurrentAccountManager.getCurAccount(), paging,
				(int) sCache.getInterlocutorID(), (int) sCache.getGroupID());
	}

	@Override
	public boolean isVaild() {
		return sCache.getGroupID() != MessageSession.INVALID_ID;
	}

	public void setNewDataLoadedObserver(Observer newDataLoadedObserver) {
		this.newDataLoadedObserver = newDataLoadedObserver;
	}

}

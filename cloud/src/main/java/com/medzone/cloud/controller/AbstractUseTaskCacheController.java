package com.medzone.cloud.controller;

import java.util.Date;
import java.util.List;

import android.os.AsyncTask.Status;
import android.util.SparseArray;
import android.util.SparseIntArray;

import com.medzone.cloud.CloudApplication;
import com.medzone.cloud.cache.AbstractDBObjectListCache;
import com.medzone.cloud.cache.IDataSynchronous;
import com.medzone.cloud.data.CurrentAccountManager;
import com.medzone.cloud.module.CloudMeasureModule;
import com.medzone.cloud.task.BaseGetControllerDataTask;
import com.medzone.cloud.task.SynchronizationCacheTask;
import com.medzone.framework.data.bean.BaseIdDatabaseObject;
import com.medzone.framework.data.bean.imp.AttachInfo;
import com.medzone.framework.data.bean.imp.BaseMeasureData;
import com.medzone.framework.data.bean.imp.Rule;
import com.medzone.framework.data.controller.AbstractController;
import com.medzone.framework.task.BaseTask;
import com.medzone.framework.task.Progress;
import com.medzone.framework.task.TaskHost;
import com.medzone.framework.util.NetUtil;

public abstract class AbstractUseTaskCacheController<T extends BaseIdDatabaseObject, C extends AbstractDBObjectListCache<T>>
		extends AbstractController<C> {

	/**
	 * 
	 */
	protected CloudMeasureModule<?> mModule;
	protected SynchronizationCacheTask<T, AbstractUseTaskCacheController<T, ?>> mSynchrCacheTask;
	protected BaseGetControllerDataTask<T> mBaseGetControllerDataTask;
	protected SparseArray<T> synchronizationItemSpares = new SparseArray<T>();
	protected Date mLastUseTaskTime;
	private SparseIntArray idsArray = new SparseIntArray();

	/**
	 * @param module
	 *            the module to set
	 */
	public void setBindModule(CloudMeasureModule<?> module) {
		this.mModule = module;
	}

	@Override
	public void setAttachInfo(AttachInfo mAttachInfo) {
		if (sCache.getAttachInfo() == null) {
			sCache.setAttachInfo(mAttachInfo);
			setControllerManager(null);
			getControllerManager().addController(this);
		}
	}

	@Override
	public AttachInfo getAttachInfo() {
		return sCache.getAttachInfo();
	}

	protected abstract BaseGetControllerDataTask<T> createGetDataTask(
			Object... params);

	public void autoUpdates(TaskHost taskHost) {
		getNewItemsFromServer(null, taskHost);
	}

	public synchronized boolean getNewItemsFromServer(Progress progress,
			TaskHost taskHost) {

		if (!isTaskCanExec(mBaseGetControllerDataTask)) {
			if (progress != null) {
				progress.finishProgress();
			}
			return false;
		}
		mBaseGetControllerDataTask = createGetDataTask();
		mBaseGetControllerDataTask.setProgress(progress);
		mBaseGetControllerDataTask.setTaskHost(taskHost);
		mBaseGetControllerDataTask.execute();

		return true;
	}

	protected boolean isTaskCanExec(BaseTask task) {
		if (NetUtil.isTaskNetAvailable(CloudApplication.getInstance()
				.getApplicationContext())
				&& NetUtil.isTaskUnExecution(task)
				&& isVaild()) {
			return true;
		}
		return false;
	}

	protected void computeTime() {
		if (mLastUseTaskTime == null) {
			mLastUseTaskTime = new Date();
		} else {
			mLastUseTaskTime.setTime(System.currentTimeMillis());
		}
	}

	public BaseGetControllerDataTask<T> getTask() {
		return mBaseGetControllerDataTask;
	}

	protected boolean isItemRealData(T item) {
		return true;
	}

	protected boolean firstReadLocalData() {
		return addItemsToCacheHead(sCache.read());
	}

	public boolean addItemsToCacheHead(List<T> itemList) {
		int firstPosition = 0;
		return addItemsToCache(firstPosition, itemList);
	}

	public boolean addItemsToCacheTail(List<T> itemList) {
		int lastPosition = sCache.size();
		return addItemsToCache(lastPosition, itemList);
	}

	public void updateItemInCache(T item) {
		asyncFlushCacheItem(item);
		cacheChanged();
	}

	protected boolean addItemsToCache(int position, List<T> itemList) {
		if (itemList == null || itemList.size() == 0)
			return false;
		sCache.addAll(position, itemList);
		onItemUpdate(itemList);
		return true;
	}

	public void removeItemInCache(int location) {
		if (location < 0 || location >= sCache.size()) {
			return;
		}
		T item = sCache.get(location);
		removeItemInCache(item);
	}

	public void removeItemInCache(T item) {
		if (item == null) {
			return;
		}
		sCache.remove(item);
		onItemDelete(item);
	}

	public void replaceItemInCache(T item1, T item2) {
		if (item2 == null) {
			return;
		}
		sCache.set(sCache.indexOf(item1), item2);
		cacheChanged();
	}

	public void removeItemInCache(List<T> items) {
		if (items == null) {
			return;
		}
		for (T item : items) {
			sCache.remove(item);
		}
		onItemDelete(items);

	}

	@Override
	public void clearCache() {
		if (mBaseGetControllerDataTask != null
				&& mBaseGetControllerDataTask.getStatus() == Status.RUNNING) {
			mBaseGetControllerDataTask.cancel(true);
		}
		super.clearCache();
	}

	public Date getLastUseTaskTime() {
		return mLastUseTaskTime;
	}

	protected void onItemUpdate(T item) {
		cacheChanged();
		asyncFlushCacheItem(item);
	}

	protected void onItemUpdate(List<T> newList) {
		cacheChanged();
		asyncFlushCacheItem(newList);
	}

	protected void onItemDelete(T item) {
		cacheChanged();
		asyncDeleteCacheItem(item);
	}

	protected void onItemDelete(List<T> newList) {
		cacheChanged();
		asyncDeleteCacheItem(newList);
	}

	public void asyncFlushCacheItem(T item) {
		if (item == null) {
			return;
		}
		sCache.asyncFlush(item);
	}

	public void asyncFlushCacheItem(List<T> newList) {
		if (newList == null || newList.size() == 0) {
			return;
		}
		sCache.asyncFlush(newList);
	}

	public void asyncDeleteCacheItem(T item) {
		if (item == null) {
			return;
		}
		sCache.asyncDelete(item);
	}

	public void asyncDeleteCacheItem(List<T> newList) {
		if (newList == null || newList.size() == 0) {
			return;
		}
		sCache.asyncDelete(newList);
	}

	@SuppressWarnings("unchecked")
	public void synchronousCacheTask(final int getTaskType,
			final Progress progress, TaskHost priorityHost) {

		IDataSynchronous<T> synchrHandler;
		if (!(getCache() instanceof IDataSynchronous)) {
			return;
		} else {
			synchrHandler = (IDataSynchronous<T>) getCache();

			String curAccessToken = CurrentAccountManager.getCurAccount()
					.getAccessToken();
			// 如果使用其他的帐号登陆,那这里的accessToken为null
			// String accessToken = getAttachInfo().mAccount.getAccessToken()
			String moduleID = getAttachInfo().mModuleId;
			Integer downSerial = mModule.getDownSerial();
			Integer downLimit = 0;
			List<T> dataList = synchrHandler.readUploadData();
			String uploadData = synchrHandler.readUploadDataJSON();

			if (mSynchrCacheTask != null
					&& mSynchrCacheTask.getStatus() == Status.RUNNING) {
				return;
			}

			// 标记本地待同步的数据集合
			idsArray.clear();

			for (T t : dataList) {
				int tId = (int) t.getId();
				if (!isItemSyncing(tId)) {
					idsArray.append(tId, (int) t.getId());
					synchronizationItemSpares.append(tId, t);
				}
			}

			mSynchrCacheTask = new SynchronizationCacheTask<T, AbstractUseTaskCacheController<T, ?>>(
					null, curAccessToken, this, moduleID, uploadData, null,
					downSerial, downLimit, idsArray, getTaskType);
			mSynchrCacheTask.setProgress(progress);
			mSynchrCacheTask.setPriorityHost(priorityHost);
			mSynchrCacheTask.execute();
		}
	}

	// 本地定制删除规则，key+value形式； 如果key存在，value为空，则表明数据已经被删除
	// 其余情况则依据updatetime，来选择更新与否
	public SparseArray<T> synchrInBackground(List<T> retList, int downSerial) {
		SparseArray<T> newItemArray = new SparseArray<T>();

		// 保存此次同步的序列号
		mModule.updateModuleDownSerial(downSerial);

		// 凡是服务端返回的数据都进行处理，这边已经假设服务端为我们做好了去重排序等工作
		for (int i = 0; i < retList.size(); i++) {
			T t = retList.get(i);
			t.invalidate();
			if (isDeleteStatus(t)) {
				// 均依据itemid进行操作
				sCache.delete(t.getId());
				newItemArray.append(i, null);
			} else {
				if (t instanceof BaseMeasureData) {
					// 对t进行更新，检查关键字段如，state规则库是否丢失
					t = synchroDownloadItemInBackground(t);
				}
				sCache.flush(t);
				newItemArray.append(i, t);
			}
		}

		return newItemArray;
	}

	/**
	 * 上传成功的数据，需要进行更新本地cache
	 * 
	 * @param retList
	 * @param downSerial
	 */
	public SparseArray<T> synchroUploadInBackground(List<T> retList,
			int downSerial) {

		SparseArray<T> newItemArray = new SparseArray<T>();
		// 保存此次同步的序列号
		mModule.updateModuleDownSerial(downSerial);

		for (int i = 0; i < retList.size(); i++) {
			T t = retList.get(i);

			if (t instanceof BaseMeasureData) {
				t = synchroUploadItemInBackground(t);
			}
			newItemArray.append(i, t);
			sCache.flush(t);
		}
		return newItemArray;
	}

	/**
	 * 检查测量模块数据同步上传时，必要的参数是否携带，如："state"。若发现未携带，则进行本地构建。
	 * 
	 * @param t
	 */
	protected T synchroUploadItemInBackground(T t) {
		return t;
	}

	/**
	 * 检查测量模块数据同步下载时，必要的参数是否携带，如："state"。若发现未携带，则进行本地构建。
	 * 
	 * @param t
	 */
	protected T synchroDownloadItemInBackground(T t) {

		BaseMeasureData data = ((BaseMeasureData) t);
		Integer state = data.getAbnormal();
		if (state == null || state == 0) {
			Rule rule = RuleController.getInstance().getRulebyData(data);
			data.setStateFlag(BaseMeasureData.FLAG_NOT_SYNCHRONIZED);
			data.setAbnormal(rule.getState());
		}
		return (T) data;
	}

	// FIXME 测量数据删除的状态判断尚未实现
	private boolean isDeleteStatus(T t) {
		return false;
	}

	// 我们可以根据这个做处理，在这个函数里，我们获取新的数据，同时更新下数据库
	// 但是我们不更新内存中的项，因为内存中的项异步调用可能会引发难以预测的后果
	// 内存中的项我们交给callInTaskPost函数来更新
	public void synchrCallInPostExcute(SparseArray<T> newItem,
			SparseIntArray idsArray, int getTaskType) {
		// 传入newItem，他是服务端传来的带修改的items，以id为key
		// 如果value是null说明这个是需要删除的项

		if (newItem == null) {
			return;
		}

		if (newItem.size() < 0) {
			return;
		}

		// 同步内存中的数据，与本地封装的待同步数据集合进行对比
		for (int i = 0; i < newItem.size(); i++) {
			T t = newItem.valueAt(i);
			T oldItem = getSyncItem(newItem.keyAt(i));
			if (oldItem != null) {
				if (t == null) {
					sCache.remove(oldItem);
				} else {
					int position = sCache.indexOf(oldItem);
					if (position >= 0 && position < sCache.size()) {
						sCache.set(position, t);
					}
				}
			}
		}

		if (idsArray != null) {
			for (int i = 0; i < idsArray.size(); i++) {
				synchronizationItemSpares.remove(idsArray.keyAt(i));
			}
		}
		cacheChanged();
	}

	/**
	 * 在同步记录表中通过id获取该项
	 * 
	 * @param itemId
	 * @return 如果存在返回该项，否则返回null
	 */
	protected T getSyncItem(int itemId) {
		return synchronizationItemSpares.get(itemId);
	}

	/**
	 * 指定ID的项是否在同步
	 * 
	 * @param itemId
	 * @return 如果在同步返回true，否则为false
	 */
	protected boolean isItemSyncing(int itemId) {
		return synchronizationItemSpares.indexOfKey(itemId) < 0 ? false : true;
	}

}

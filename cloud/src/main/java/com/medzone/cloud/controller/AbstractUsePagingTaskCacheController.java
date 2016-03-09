package com.medzone.cloud.controller;

import java.util.List;

import android.util.SparseArray;
import android.util.SparseIntArray;

import com.medzone.cloud.cache.AbstractPagingListCache;
import com.medzone.framework.Log;
import com.medzone.framework.data.bean.BasePagingContent;
import com.medzone.framework.data.navigation.Paging;
import com.medzone.framework.data.navigation.Stepable;
import com.medzone.framework.task.Progress;
import com.medzone.framework.task.TaskHost;

public abstract class AbstractUsePagingTaskCacheController<T extends BasePagingContent, S extends Stepable<S>, C extends AbstractPagingListCache<T, S>>
		extends AbstractUseTaskCacheController<T, C> {

	/**
	 * 
	 */
	protected static final int GET_UP_PAGING_ITEMS_FROM_SERVER = 1;
	protected static final int GET_DOWN_PAGING_ITEMS_FROM_SERVER = 2;

	protected Paging<S> paging = new Paging<S>();

	protected Paging<S> unCahcePaging = new Paging<S>();

	protected void onUnCahcePagingItemUpdate(List<T> itemList) {
		if (itemList != null) {
			unCahcePaging.setMin(min(unCahcePaging.getMin(), getMin(itemList)));
			unCahcePaging.setMax(max(unCahcePaging.getMax(), getMax(itemList)));
		}
		super.onItemUpdate(itemList);
	}

	protected void onUnCahcePagingItemUpdate(T item) {
		if (item != null) {
			unCahcePaging
					.setMin(min(unCahcePaging.getMin(), getStepable(item)));
			unCahcePaging
					.setMax(max(unCahcePaging.getMax(), getStepable(item)));
		}
		super.onItemUpdate(item);
	}

	@Override
	protected boolean firstReadLocalData() {
		List<T> list = readLocalItems(new Paging<S>());
		if (list != null && list.size() > 0) {
			int last = list.size() - 1;
			T dividerT = list.get(last);
			if (dividerT.isDivider()) {
				list.remove(last);
			}
		}
		return addItemsToCacheHead(list);
	}

	public List<T> readUpPageFromLocalWithoutCache() {
		List<T> itemList = readLocalItems(unCahcePaging.getUpPaging());
		if (itemList != null && itemList.size() > 0) {
			int last = itemList.size() - 1;
			T dividerT = itemList.get(last);
			if (dividerT.isDivider()) {
				itemList.remove(last);
			}
		}
		if (itemList != null && itemList.size() == 0)
			return null;
		onUnCahcePagingItemUpdate(itemList);
		return itemList;
	}

	public List<T> readDownPageFromLocalWithoutCache(boolean isBegin) {

		if (isBegin) {
			unCahcePaging.reset();
		}

		List<T> itemList = readLocalItems(unCahcePaging.getDownPaging());
		if (itemList != null && itemList.size() > 0) {
			int last = itemList.size() - 1;
			T dividerT = itemList.get(last);
			if (dividerT.isDivider()) {
				itemList.remove(last);
			}
		}
		if (itemList != null && itemList.size() == 0)
			return null;
		onUnCahcePagingItemUpdate(itemList);
		return itemList;
	}

	public boolean readUpPageFromLocal() {
		List<T> itemList = readLocalItems(paging.getUpPaging());
		if (itemList != null && itemList.size() > 0) {
			int last = itemList.size() - 1;
			T dividerT = itemList.get(last);
			if (dividerT.isDivider()) {
				itemList.remove(last);
			}
		}
		if (itemList != null && itemList.size() == 0) {
			return false;
		}
		return addItemsToCacheHead(itemList);
	}

	public boolean readDownPageFromLocal() {
		List<T> itemList = readLocalItems(paging.getDownPaging());
		if (itemList != null && itemList.size() > 0) {
			int last = itemList.size() - 1;
			T dividerT = itemList.get(last);
			if (dividerT.isDivider()) {
				itemList.remove(last);
			}
		}
		if (itemList != null && itemList.size() == 0)
			return false;
		return addItemsToCacheTail(itemList);
	}

	public List<T> readLocalItems(Paging<S> paging) {
		List<T> list = sCache.read(paging);
		// TODO 可能存在安全隐患，所以需要每次都同步内存中cache
		// if (list != null && list.size() > 0) {
		// synchronizePartList(list);
		// }
		return list;
	}

	public void clearCache() {
		if (paging != null) {
			paging.reset();
		}
		super.clearCache();
	}

	@Override
	public void autoUpdates(TaskHost taskHost) {
		executeDataLoadingTask(GET_UP_PAGING_ITEMS_FROM_SERVER, null, taskHost,
				paging.getUpPaging(), null);
	}

	/**
	 * 
	 * And the service side agreement, all of the refresh data are from local,
	 * synchronous operation is responsible for synchronization of local data
	 * maintenance, when paging is not maintained
	 */
	@Override
	public synchronized boolean getNewItemsFromServer(Progress progress,
			TaskHost taskHost) {

		if (!readUpPageFromLocal()) {
			executeDataLoadingTask(GET_UP_PAGING_ITEMS_FROM_SERVER, progress,
					taskHost, paging.getUpPaging(), null);
		}
		return true;
	}

	public boolean getDownPageItemsFromServer(Progress progress,
			TaskHost taskHost) {

		if (!readDownPageFromLocal()) {
			executeDataLoadingTask(GET_DOWN_PAGING_ITEMS_FROM_SERVER, progress,
					taskHost, paging.getDownPaging(), null);
		}
		return true;
	}

	protected boolean executeDataLoadingTask(final int getTaskType,
			Progress progress, TaskHost taskHost, Paging<S> paging, T divider) {

		synchronousCacheTask(getTaskType, progress, taskHost);

		/*
		 * if (!isTaskCanExec(mBaseGetControllerDataTask)) return false;
		 * 
		 * mBaseGetControllerDataTask = createGetDataTask(paging, getTaskType);
		 * mBaseGetControllerDataTask.setProgress(progress);
		 * mBaseGetControllerDataTask.setTaskHost(taskHost);
		 * mBaseGetControllerDataTask.setPriorityHost(new TaskHost() {
		 * 
		 * @Override public void onPostExecute(int requestCode, BaseResult
		 * result) { super.onPostExecute(requestCode, result); if
		 * (result.isSuccess() && result.isServerDisposeSuccess()) { } } });
		 * mBaseGetControllerDataTask.execute();
		 */

		return true;
	}

	@Override
	public void synchrCallInPostExcute(SparseArray<T> newItem,
			SparseIntArray idsArray, int getTaskType) {
		super.synchrCallInPostExcute(newItem, idsArray, getTaskType);
		switch (getTaskType) {
		case GET_UP_PAGING_ITEMS_FROM_SERVER:
			if (!readUpPageFromLocal())
				Log.w("The current data is the latest!");
			break;
		case GET_DOWN_PAGING_ITEMS_FROM_SERVER:
			if (!readDownPageFromLocal())
				Log.w("The current data is the oldest!");
			break;
		default:
			break;
		}
	}

	/***
	 * 更新页码操作
	 * 
	 * @param item
	 * @return
	 */

	protected abstract S getStepable(T item);

	public S min(S stepable1, S stepable2) {
		if (stepable1 != null) {
			if (stepable1.compareTo(stepable2) < 0) {
				return stepable1;
			}
		}
		return stepable2;
	}

	public S max(S stepable1, S stepable2) {
		if (stepable1 != null) {
			if (stepable1.compareTo(stepable2) > 0) {
				return stepable1;
			}
		}
		return stepable2;
	}

	public S getMin(List<T> itemList) {
		S min = null;
		if (itemList != null && itemList.size() > 0) {
			for (T item : itemList) {
				min = min(min, getStepable(item));
			}
		}
		return min;
	}

	public S getMax(List<T> itemList) {
		S max = null;
		if (itemList != null && itemList.size() > 0) {
			for (T item : itemList) {
				max = max(max, getStepable(item));
			}
		}
		return max;
	}

	public void setPaging(Paging<S> paging) {
		this.paging = paging;
	}

	public Paging<S> getPaging() {
		return paging;
	}

	@Override
	protected void onItemUpdate(List<T> itemList) {
		if (itemList != null) {
			paging.setMin(min(paging.getMin(), getMin(itemList)));
			paging.setMax(max(paging.getMax(), getMax(itemList)));
		}
		super.onItemUpdate(itemList);
	}

	@Override
	protected void onItemUpdate(T item) {
		if (item != null) {
			paging.setMin(min(paging.getMin(), getStepable(item)));
			paging.setMax(max(paging.getMax(), getStepable(item)));
		}
		super.onItemUpdate(item);
	}

	protected T getItem(int id) {
		for (int i = 0; i < sCache.size(); i++) {
			T item = sCache.get(i);
			if (item != null && item.getId() == id) {
				return sCache.get(i);
			}
		}
		return null;
	}

	@Override
	protected boolean isItemRealData(T item) {
		if (item.isDivider()) {
			return false;
		}
		return super.isItemRealData(item);
	}
}

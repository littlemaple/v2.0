///**
// * 
// */
//package com.medzone.cloud.controller;
//
//import java.util.List;
//
//import com.medzone.cloud.cache.ClockCache;
//import com.medzone.cloud.task.BaseGetControllerDataTask;
//import com.medzone.cloud.task.ProcessNewItemsTaskHost;
//import com.medzone.framework.data.bean.imp.Clock;
//import com.medzone.framework.data.navigation.LongStepable;
//import com.medzone.framework.data.navigation.Paging;
//import com.medzone.framework.task.Progress;
//import com.medzone.framework.task.TaskHost;
//
///**
// * @author ChenJunQi.
// * 
// */
//public class ClockCacheController extends
//		AbstractUsePagingTaskCacheController<Clock, LongStepable, ClockCache> {
//
//	@Override
//	protected LongStepable getStepable(Clock item) {
//		return new LongStepable(item.getClockID());
//	}
//
//	@Override
//	protected boolean executeDataLoadingTask(
//			final int mBaseGetControllerDataTaskType, Progress progress,
//			TaskHost taskHost, Paging<LongStepable> paging, Clock divider) {
//		if (!isTaskCanExec(mBaseGetControllerDataTask)) {
//			if (progress != null) {
//				progress.finishProgress();
//			}
//			return false;
//		}
//		mBaseGetControllerDataTask = createGetDataTask(paging);
//		mBaseGetControllerDataTask.setProgress(progress);
//		mBaseGetControllerDataTask.setTaskHost(taskHost);
//		// 处理未读消息
//		// mBaseGetControllerDataTask
//		// .setPriorityHost(baseGetControllerDataTaskPriorityHost);
//		mBaseGetControllerDataTask
//				.setProcessNewItemsTaskHost(new ProcessNewItemsTaskHost<Clock>() {
//
//					@Override
//					public void onPostExecuteProcessBalance(List<Clock> list) {
//						computeTime();
//						if (mBaseGetControllerDataTaskType == GET_DOWN_PAGING_ITEMS_FROM_SERVER
//								&& (list == null || list.size() < 1)) {
//							// no data get from server.
//						}
//						switch (mBaseGetControllerDataTaskType) {
//						case GET_UP_PAGING_ITEMS_FROM_SERVER:
//							addItemsToCacheTail(list);
//							break;
//						case GET_DOWN_PAGING_ITEMS_FROM_SERVER:
//							addItemsToCacheHead(list);
//							break;
//						default:
//							break;
//						}
//					}
//				});
//		mBaseGetControllerDataTask.execute();
//		return true;
//	}
//
//	@Override
//	protected BaseGetControllerDataTask<Clock> createGetDataTask(
//			Object... params) {
//		Paging<LongStepable> paging = (Paging<LongStepable>) params[0];
//
//		return null;
//	}
//
//	@Override
//	protected ClockCache createCache() {
//		return new ClockCache();
//	}
//
//	@Override
//	public boolean isVaild() {
//		return sCache.getClockID() != Clock.INVALID_ID;
//	}
//
// }

package com.medzone.cloud.task;

import java.util.List;

import android.content.Context;

import com.medzone.framework.data.bean.BaseDatabaseObject;
import com.medzone.framework.task.BaseResult;
import com.medzone.framework.task.TaskHost;

public abstract class BaseGetControllerDataTask<T extends BaseDatabaseObject>
		extends BaseCloudTask {

	private TaskHost priorityHost;

	protected List<T> newItems;
	private ProcessNewItemsTaskHost<T> processNewItemsTaskHost;

	public BaseGetControllerDataTask(Context context) {
		super(context, 0);
	}

	@Override
	protected final BaseResult doInBackground(Void... params) {
		BaseResult baseResult = readDataInBackground(params);
		return baseResult;
	}

	protected abstract BaseResult readDataInBackground(Void... params);

	@Override
	protected void onPostExecute(BaseResult result) {
		super.onPostExecute(result);

		if (result.isSuccess() && !isCancelled()) {
			if (processNewItemsTaskHost != null) {
				processNewItemsTaskHost.onPostExecuteProcessBalance(newItems);
			}
		}

		if (priorityHost != null) {
			priorityHost.onPostExecute(requestCode, result);
		}
		super.onPostExecute(result);
	}

	public void setProcessNewItemsTaskHost(
			ProcessNewItemsTaskHost<T> processNewItemsTaskHost) {
		this.processNewItemsTaskHost = processNewItemsTaskHost;
	}

	/**
	 * 设置优先处理priorityHost,这个会比taskHost要早执行
	 * 
	 * @param priorityHost
	 */
	public void setPriorityHost(TaskHost priorityHost) {
		this.priorityHost = priorityHost;
	}

}

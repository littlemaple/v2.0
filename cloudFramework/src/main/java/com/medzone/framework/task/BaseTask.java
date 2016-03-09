/**
 * 
 */
package com.medzone.framework.task;

import android.os.AsyncTask;
import android.util.Log;

/**
 * @author ChenJunQi
 *         <p>
 *         BaseTask extends AsyncTask with IProgress updating
 *         </p>
 * 
 */
public abstract class BaseTask extends AsyncTask<Void, Integer, BaseResult> {
	private static final String TAG = BaseTask.class.getSimpleName();

	protected Progress progress;
	protected TaskHost taskHost;
	protected int requestCode;

	/**
	 * 
	 */
	public BaseTask(int requestCode) {
		super();
		this.requestCode = requestCode;
	}

	@Override
	protected abstract BaseResult doInBackground(Void... params);

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.os.AsyncTask#onCancelled()
	 */
	@Override
	protected void onCancelled() {
		super.onCancelled();
		if (progress != null) {
			progress.finishProgress();
		}
		if (taskHost != null) {
			taskHost.onCancelled(requestCode);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
	 */
	@Override
	protected void onPostExecute(BaseResult result) {
		Log.i(TAG, "post exec: " + this.getClass());
		super.onPostExecute(result);
		if (taskHost != null) {
			taskHost.onPostExecute(requestCode, result);
		}
		if (progress != null) {
			progress.updateProgress(100);
			progress.finishProgress();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.os.AsyncTask#onPreExecute()
	 */
	@Override
	protected void onPreExecute() {
		Log.i(TAG, "pre exec: " + this.getClass());
		if (progress != null) {
			progress.startProgress();
			progress.updateProgress(0);
		}
		if (taskHost != null) {
			taskHost.onPreExecute(requestCode);
		}
		super.onPreExecute();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.os.AsyncTask#onProgressUpdate(Progress[])
	 */
	@Override
	protected void onProgressUpdate(Integer... values) {
		super.onProgressUpdate(values);
		if (progress != null) {
			progress.updateProgress(values[0]);
		}
	}

	public void setProgress(Progress progress) {
		this.progress = progress;
	}

	/**
	 * @param taskHost
	 *            the taskHost to set
	 */
	public void setTaskHost(TaskHost taskHost) {
		this.taskHost = taskHost;
	}

}

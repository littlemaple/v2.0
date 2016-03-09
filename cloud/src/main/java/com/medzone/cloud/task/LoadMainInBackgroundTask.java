/**
 * 
 */
package com.medzone.cloud.task;

import android.os.AsyncTask;
import android.view.View;

import com.medzone.framework.task.BackgroundLoadable;

/**
 * @author ChenJunQi.
 * 
 */
public class LoadMainInBackgroundTask extends AsyncTask<Void, Object, View> {
	BackgroundLoadable activity;
	// using when splash, we should have a mini load time for splash screen show
	// to user.
	int minLoadTime;

	public LoadMainInBackgroundTask(BackgroundLoadable activity, int minLoadTime) {
		this.activity = activity;
		this.minLoadTime = minLoadTime;
	}

	@Override
	protected void onPreExecute() {
		activity.onPreLoad();
	}

	@Override
	protected View doInBackground(Void... params) {
		long time1 = System.currentTimeMillis();
		View view = activity.loadInBackground();
		long time2 = System.currentTimeMillis();
		if ((time2 - time1) < minLoadTime) {
			try {
				Thread.sleep(minLoadTime - (time2 - time1));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return view;
	}

	@Override
	protected void onPostExecute(View result) {
		activity.onPostLoad(result);
	}

}

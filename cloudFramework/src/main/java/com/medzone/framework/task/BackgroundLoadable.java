package com.medzone.framework.task;

import android.view.View;

public interface BackgroundLoadable {

	public void onPreLoad();

	public View loadInBackground();

	public void onPostLoad(View view);
}

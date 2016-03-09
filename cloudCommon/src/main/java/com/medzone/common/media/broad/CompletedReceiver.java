package com.medzone.common.media.broad;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class CompletedReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Controller.listener.onComplete();
	}

}

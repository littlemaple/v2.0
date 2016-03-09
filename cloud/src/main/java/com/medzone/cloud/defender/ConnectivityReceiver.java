/**
 * 
 */
package com.medzone.cloud.defender;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * @author ChenJunQi.
 * 
 */
public class ConnectivityReceiver extends BroadcastReceiver {

	private ConnectivityManager connectivityManager;
	private NetworkInfo info;

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
			connectivityManager = (ConnectivityManager)

			context.getSystemService(Context.CONNECTIVITY_SERVICE);
			info = connectivityManager.getActiveNetworkInfo();
			if (info != null && info.isAvailable()) {
				String name = info.getTypeName();

				Intent mIntent = new Intent(BroadCastUtil.ACTION_NET_CONNECTED);
				mIntent.putExtra("extra", name);
				context.sendBroadcast(mIntent);

			} else {

				Intent mIntent = new Intent(
						BroadCastUtil.ACTION_NET_DISCONNECTED);
				context.sendBroadcast(mIntent);

			}
		}
	}
}

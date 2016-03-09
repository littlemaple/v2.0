package com.medzone.cloud.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Looper;
import android.widget.Toast;

import com.medzone.cloud.network.NetworkClientHelper;
import com.medzone.framework.Log;
import com.medzone.framework.util.NetUtil;
import com.medzone.mcloud.R;
import com.umeng.update.UmengDialogButtonListener;
import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UmengUpdateListener;
import com.umeng.update.UpdateResponse;

public class GeneralUtil {

	public static void checkNewVersion(final Context context) {
		UmengUpdateAgent.setUpdateAutoPopup(false);
		UmengUpdateAgent.setUpdateOnlyWifi(false);
		UmengUpdateAgent.setDownloadListener(null);
		UmengUpdateAgent.setUpdateListener(null);
		UmengUpdateAgent.setDialogListener(null);
		UmengUpdateAgent.setUpdateListener(new UmengUpdateListener() {

			@Override
			public void onUpdateReturned(int updateStatus,
					UpdateResponse updateInfo) {
				switch (updateStatus) {
				case 0:
					// has update
					UmengUpdateAgent.showUpdateDialog(context, updateInfo);
					Log.e(">>>>>>>>>>>>>>>>>>> has  update"+updateInfo.updateLog);
					break;
				case 1:
					// has no update
					Toast.makeText(
							context,
							context.getString(R.string.check_update_no_new_version),
							Toast.LENGTH_SHORT).show();
					Log.e(">>>>>>>>>>>>>>>>>>> has  no update");
					break;
				case 2:
					// no wifi. only when mUpdateOnlyWifi = true.
					// so we can ignore this here
					Log.e(">>>>>>>>>>>>>>>>>>> no wifi has  update");
					break;
				case 3:
					// time out
					Toast.makeText(
							context,
							context.getString(R.string.check_update_connection_timeout),
							Toast.LENGTH_SHORT).show();
					Log.e(">>>>>>>>>>>>>>>>>>>  time out");
					break;
				}
			}

		});
		UmengUpdateAgent.update(context);
	}

	public static void checkServerApiVersion(Context mContext) {
		if (NetUtil.isTaskNetAvailable(mContext)) {

			if (!NetworkClientHelper.isCheckServerApiVersionSuccess()) {
				NetworkClientHelper.checkServerApiVersion();
			}
		}
	}

	public static void checkAppForceUpdate(Context mContext,
			UmengDialogButtonListener mUmengDialogButtonListener) {

		if (null == mContext)
			return;
		if (NetworkClientHelper.isForcedUpdate()) {
			doAppForceUpdate(mContext, mUmengDialogButtonListener);
		}
	}

	public static void doAppForceUpdate(Context mContext,
			UmengDialogButtonListener mUmengDialogButtonListener) {
		UmengUpdateAgent.setUpdateOnlyWifi(false);
		UmengUpdateAgent.forceUpdate(mContext);
		UmengUpdateAgent.setDialogListener(mUmengDialogButtonListener);
	}

	public static boolean isInUIThread() {
		return Thread.currentThread().getId() == Looper.getMainLooper()
				.getThread().getId();
	}

	public static String getAppVersionName(Context context) {
		try {
			PackageManager pm = context.getPackageManager();
			PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
			return pi.versionName;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static int getAppVersionCode(Context context) {
		try {
			PackageManager pm = context.getPackageManager();
			PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
			return pi.versionCode;
		} catch (Exception e) {
			return 0;
		}
	}

}

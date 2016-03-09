/**
 * 
 */
package com.medzone.cloud.network;

import org.json.JSONException;
import org.json.JSONObject;

import com.medzone.cloud.Constants;
import com.medzone.framework.Log;
import com.medzone.framework.network.NetworkClientResult;

/**
 * @author ChenJunQi.
 * 
 */
public final class NetworkClientHelper {

	public final static int TRY_CONNECT_SERVER_TIMES = 3;
	public final static int TRY_DOWNLOAD_API_TIMES = 3;

	private static boolean isCheckServerApiVersionSuccess = false;
	private static boolean isForcedUpdate = false;

	public static boolean isCheckServerApiVersionSuccess() {
		return isCheckServerApiVersionSuccess;
	}

	public static boolean isForcedUpdate() {
		return isForcedUpdate;
	}

	public static void checkServerApiVersion() {
		for (int i = 0; i < TRY_DOWNLOAD_API_TIMES; i++) {
			if (detectAppIsForcedUpdate()) {
				isCheckServerApiVersionSuccess = true;
				return;
			}
		}
		isCheckServerApiVersionSuccess = false;
	}

	// Only return the state of server request.
	private static boolean detectAppIsForcedUpdate() {
		NetworkClientResult res = (NetworkClientResult) NetworkClient
				.getInstance().getVersion();
		if (res.isSuccess() && res.isServerDisposeSuccess()) {

			JSONObject jo = res.getResponseResult();

			String apiVersion = null;
			// String rulesVersion = null;
			try {
				apiVersion = jo.getString("apiversion");
				// rulesVersion = jo.getString("rulesversion");
				int highMajorVersion = utilParseVersion(apiVersion);
				if (highMajorVersion > Constants.LOCAL_SERVER_API_HIGH_VERSION) {
					isForcedUpdate = true;
				}
				return true;
			} catch (JSONException e) {
				return false;
			}

		} else {
			Log.i("check the API network error");
			return false;
		}
	}

	// TODO 完善版本强制更新内容
	private static int utilParseVersion(String apiVersion) {
		Log.d(">>>" + apiVersion);
		// int[] res = new int[3];
		int index = apiVersion.indexOf(".");
		int highMajorVersion = Integer.valueOf(apiVersion.substring(0, index));
		// String egVersionModified = apiVersion.substring(index + 1);
		// int indexModified = egVersionModified.indexOf(".");
		// int lowMajorVersion = Integer.valueOf(apiVersion.substring(index + 1,
		// index + indexModified + 1));
		// int totalMajorCounts = Integer.valueOf(egVersionModified
		// .substring(indexModified + 1));
		// res[0] = highMajorVersion;
		// res[1] = lowMajorVersion;
		// res[2] = totalMajorCounts;
		// res[0] = 1;
		// res[1] = 0;
		// res[2] = 0;
		return highMajorVersion;
	}

	public static String getAvatarUrl(int groupid) {
		if (NetworkClient.isSandBox()) {
			return "http://d11.sunbo.com/~hightman/mhealth/index.php/app/groupImage?groupid="
					+ groupid;
		} else {

			return "http://v2.mcloudlife.com/app/groupImage?groupid=" + groupid;
		}
	}

}

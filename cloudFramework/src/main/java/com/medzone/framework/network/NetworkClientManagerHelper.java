package com.medzone.framework.network;

import java.util.HashMap;

import android.content.Context;
import android.text.TextUtils;

import com.medzone.framework.Log;
import com.medzone.framework.util.NetUtil;

/**
 * 
 * @author ChenJunQi.
 * 
 *         <p>
 *         <b> Provide a template operation，(TODO)here define more templates.
 *         </p>
 *         <p>
 *         </b> Check the client installation and unloading behavior, business
 *         logic is reserved to the concrete operation in the API.
 *         </p>
 * */

public class NetworkClientManagerHelper {

	// private static boolean isServerReady = false;
	private static Context context;
	private static HashMap<String, Boolean> connectStateMap;

	public static void init(Context mContext) {
		context = mContext;
		connectStateMap = new HashMap<String, Boolean>();
	}

	public static boolean isServerReady(String baseUri) {
		if (connectStateMap == null) {
			throw new IllegalArgumentException("NetworkClient尚未初始化");
		}
		if (connectStateMap.containsKey(baseUri)) {
			return connectStateMap.get(baseUri).booleanValue();
		} else {
			return false;
		}

	}

	public static void readyClient(String baseUri) {
		if (NetUtil.isTaskNetAvailable(context)) {
			// TODO Check the response,choose a channel with good response.
			if (!TextUtils.isEmpty(baseUri)) {
				NetworkClientManager.loadClient(new JsonRestClientAdapter(
						baseUri));
				connectStateMap.put(baseUri, true);
				// isServerReady = true;
				return;
			}
		}
	}

	public static void discardClient(String baseUri) {
		// isServerReady = false;
		connectStateMap.put(baseUri, false);
		NetworkClientManager.loadClient(null);
		Log.w("readyClient failed ,discard it .");
	}

}

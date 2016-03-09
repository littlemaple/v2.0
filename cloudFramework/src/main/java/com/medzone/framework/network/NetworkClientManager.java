package com.medzone.framework.network;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextUtils;

import com.medzone.framework.Log;
import com.medzone.framework.errorcode.ProxyCode.LocalError;
import com.medzone.framework.network.exception.RestException;
import com.medzone.framework.task.BaseResult;

/**
 * 
 * @author ChenJunQi.
 * 
 */
public final class NetworkClientManager {

	private static final NetworkClientManager instance = new NetworkClientManager();
	private static final int BIG_DATA_CONNECTION_TIME_OUT = 15000;
	private static final int BIG_DATA_SOKET_TIME_OUT = 15000;

	private static INetworkClient mClient;

	private NetworkClientManager() {
	}

	public static NetworkClientManager getInstance() {
		return instance;
	}

	public static void loadClient(INetworkClient mINetworkClient) {
		mClient = mINetworkClient;
	}

	public static INetworkClient getClient() {
		return mClient;
	}

	private void print(String resource, JSONObject params) {
		if (params != null) {
			Log.d("call:" + resource);
			Log.d("params:" + params.toString());
		}
	}

	public synchronized BaseResult call(String resource, JSONObject params) {

		print(resource, params);

		NetworkClientResult result = new NetworkClientResult();
		result.setSuccess(false);

		if (!TextUtils.isEmpty(resource)) {
			try {
				Object obj = null;
				if (mClient == null) {
					throw new RestException(
							"It is necessary to load the network client");
				}
				if (params == null) {
					obj = mClient.call(resource);
				} else {
					obj = mClient.callEx(resource, params);
				}

				if (obj instanceof JSONArray) {
					JSONObject jsonObj = new JSONObject();
					jsonObj.put("root", obj);
					obj = jsonObj;
				}

				result.setResponseResult((JSONObject) obj);
				if (isServerExceptionExist((JSONObject) obj)) {
					result.setSuccess(false);
				} else {
					result.setSuccess(true);
				}

			} catch (RestException e) {
				e.printStackTrace();
				result.setSuccess(false);
			} catch (JSONException e) {
				e.printStackTrace();
				result.setSuccess(false);
			}
		}

		return result;
	}

	private boolean isServerExceptionExist(JSONObject json) {
		if (json.has("errcode") && !json.isNull("errcode")) {
			try {
				int errCode = json.getInt("errcode");
				if (errCode == LocalError.CODE_10005) {
					return true;
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	public synchronized BaseResult call(String resource, JSONObject params,
			boolean isHasBigFile) {

		print(resource, params);

		NetworkClientResult result = new NetworkClientResult();
		result.setSuccess(false);

		if (!TextUtils.isEmpty(resource)) {
			try {
				Object obj = null;
				if (mClient == null) {
					throw new RestException(
							"It is necessary to load the network client");
				}
				if (params == null) {
					obj = mClient.call(resource);
				} else {

					if (isHasBigFile)
						obj = mClient.callEx(resource, (JSONObject) params,
								BIG_DATA_CONNECTION_TIME_OUT,
								BIG_DATA_SOKET_TIME_OUT);
					else
						obj = mClient.callEx(resource, params);

				}

				if (obj instanceof JSONArray) {
					JSONObject jsonObj = new JSONObject();
					jsonObj.put("root", obj);
					obj = jsonObj;
				}

				result.setResponseResult((JSONObject) obj);
				if (isServerExceptionExist((JSONObject) obj)) {
					result.setSuccess(false);
				} else {
					result.setSuccess(true);
				}
			} catch (RestException e) {
				e.printStackTrace();
				result.setSuccess(false);
			} catch (JSONException e) {
				e.printStackTrace();
				result.setSuccess(false);
			}
		}

		return result;
	}

}

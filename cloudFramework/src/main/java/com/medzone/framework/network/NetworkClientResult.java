package com.medzone.framework.network;

import org.json.JSONException;
import org.json.JSONObject;

import com.medzone.framework.Log;
import com.medzone.framework.task.BaseResult;

/**
 * 
 * @author ChenJunQi.
 * 
 */
public class NetworkClientResult extends BaseResult {

	// If the error Code is less than zero, then is the local definition of
	// error Code
	public static final int DISPOSE_CODE_VAILD_RESULTMAP = DISPOSE_CODE_SUCCESS - 1;

	protected JSONObject jsonObject;

	public NetworkClientResult() {
		super();
	}

	public void setResponseResult(JSONObject jsonObject) {
		this.jsonObject = jsonObject;
		checkErrorMessage(jsonObject);
	}

	public JSONObject getResponseResult() {
		return jsonObject;
	}

	private boolean isResultMapValid() {
		return jsonObject != null;
	}

	private void checkErrorMessage(JSONObject jo) {
		if (isResultMapValid()) {

			Log.d("Response:" + jsonObject.toString());
			try {
				setErrorCode(DISPOSE_CODE_SUCCESS);
				setServerDisposeSuccess(true);

				if (jo.has("errcode") && !jo.isNull("errcode")) {
					Double temp = jo.getDouble("errcode");
					setErrorCode(temp.intValue());

					if (errorCode == DISPOSE_CODE_SUCCESS) {
						setServerDisposeSuccess(true);
					} else {
						setServerDisposeSuccess(false);
					}
				}
				if (jo.has("errmsg") && !jo.isNull("errmsg")) {
					String errorMessage = jo.getString("errmsg");
					setErrorMessage(errorMessage);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else {
			setErrorCode(DISPOSE_CODE_VAILD_RESULTMAP);
			setErrorMessage("The server response the result set of the abnormal!");
		}
		// ProxyErrorCode.getInstance().addErrorAction(type, code);
	}

}

package com.medzone.framework.network;

import org.json.JSONObject;

import com.medzone.framework.network.client.JsonRestClient;
import com.medzone.framework.network.exception.RestException;

/**
 * 
 * @author ChenJunQi.
 * 
 */
public class JsonRestClientAdapter extends RestClientAdapter {

	private JsonRestClient mJsonRestClient;

	public JsonRestClientAdapter(String baseUri) {
		createClient(baseUri);
	}

	@Override
	public Object call(String resource) throws RestException {
		return mJsonRestClient.call(resource);
	}

	@Override
	public Object callEx(String resource, Object params) throws RestException {
		return mJsonRestClient.callEx(resource, (JSONObject) params);
	}

	@Override
	public Object callEx(String resource, Object params, int connectionTimeOut,
			int soketTimeOut) throws RestException {
		return mJsonRestClient.callEx(resource, (JSONObject) params,
				connectionTimeOut, soketTimeOut);
	}

	public void createClient(String baseUri) {
		mJsonRestClient = new JsonRestClient(baseUri);
	}

	@Override
	void clearCookies() {
		mJsonRestClient.clearCookies();
	}

}

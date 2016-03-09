package com.medzone.framework.network.serializer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.medzone.framework.Log;

public class SerializerJsonImp implements ISerializer<Object> {

	public SerializerJsonImp() {
	}

	@Override
	public String serialize(Object obj) {
		if (obj == null)
			return new String("");
		if (obj instanceof JSONObject)
			return ((JSONObject) obj).toString();
		return ((JSONArray) obj).toString();
	}

	@Override
	public Object deserialize(String json) {

		try {
			return new JSONObject(json);
		} catch (JSONException e) {
			try {
				return new JSONArray(json);
			} catch (Exception e1) {
				Log.v("#" + json);
			}
		}
		return null;
	}

}

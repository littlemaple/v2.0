package com.medzone.framework.network.client;

import java.lang.reflect.Type;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.medzone.framework.errorcode.ProxyCode.LocalError;
import com.medzone.framework.network.exception.RestException;
import com.medzone.framework.network.serializer.SerializerJsonImp;

public class JsonRestClient extends NetworkCommon<JSONObject> {

	private static final int DEFAULT_CONNECTION_TIMEOUT = 6000;
	public static final int DEFAULT_SOKET_TIMEOUT = 6000;

	private HttpClient client;
	private HttpPost postMethod;
	private HttpParams httpParams;

	private HashMap<String, String> cookies;
	private String baseUri;

	@Override
	void initISerializer() {
		iSerializer = new SerializerJsonImp();
	}

	public JsonRestClient(String uri) {
		super();
		baseUri = uri;
	}

	private void setCookie(String paramString) {
		String[] arrayOfString = paramString.split("=");

		if (arrayOfString.length != 2)
			return;

		if (cookies == null) {
			cookies = new HashMap<String, String>();
		}
		if (cookies.containsKey(arrayOfString[0])) {
			cookies.remove(arrayOfString[0]);
		}
		cookies.put(arrayOfString[0], arrayOfString[1]);
	}

	@SuppressWarnings("unchecked")
	private String getCookiesString() {
		StringBuilder cookiesStringBuilder = new StringBuilder();

		Iterator<?> cookieIterator;
		if ((this.cookies != null) && (this.cookies.size() > 0)) {
			cookieIterator = this.cookies.entrySet().iterator();
			while (cookieIterator.hasNext()) {

				Map.Entry<Object, Object> cookieEntry = (Map.Entry<Object, Object>) cookieIterator
						.next();
				cookiesStringBuilder.append((String) cookieEntry.getKey());
				cookiesStringBuilder.append("=");
				cookiesStringBuilder.append((String) cookieEntry.getValue());
				cookiesStringBuilder.append(";");
			}

		}
		return cookiesStringBuilder.toString();
	}

	public void clearCookies() {
		if (cookies != null)
			cookies.clear();
	}

	/**
	 * Is different from the way of RPC, Rest need to build a new request each
	 * time.
	 */
	private void createClient(/* URI uri */String resource) {

		if (TextUtils.isEmpty(baseUri)) {
			try {
				throw new RestException(
						"Please specify the address of the client.");
			} catch (RestException e) {
				e.printStackTrace();
			}
		}

		String postResource = baseUri.concat(resource);
		URI uri = URI.create(postResource);

		// 我们通常使用 Accept 来设置我们接受的返回结果的内容格式
		// 用 Accept-Charset 来设置字符集，
		// 用Accept-Encoding 来设置数据传输格式，
		// 用 Accept-Language 来设置语言。
		postMethod = new HttpPost(uri);
		postMethod.setHeader("User-Agent", System.getProperty("http.agent"));
		postMethod.addHeader("Accept", "application/json");
		postMethod
				.addHeader("Accept-Language",
						"en;q=1, fr;q=0.9, de;q=0.8, zh-Hans;q=0.7, zh-Hant;q=0.6, ja;q=0.5");

		postMethod.addHeader("Accept-Encoding", "UTF-8");
		// postMethod.addHeader("Content-Type", "text/json; charset=utf-8");
		// 如果要指定，需要从api地址中进行拆分，不能够写死
		// postMethod.addHeader("Host", "v2.mcloudlife.com");

		String cookiesString = getCookiesString();
		postMethod.removeHeaders("Cookie");
		if (cookiesString != null && cookiesString.length() > 0) {
			postMethod.addHeader("Cookie", cookiesString);
		}

		httpParams = postMethod.getParams();
		HttpProtocolParams.setUseExpectContinue(httpParams, false);
		HttpConnectionParams.setConnectionTimeout(httpParams,
				DEFAULT_CONNECTION_TIMEOUT);
		HttpConnectionParams.setSoTimeout(httpParams, DEFAULT_SOKET_TIMEOUT);
		client = new DefaultHttpClient();

	}

	/**
	 * 发起一次HTTP请求，依赖于{@link #createClient(String)}方法。
	 * 
	 * @param resource
	 * @param params
	 * @return
	 * @throws RestException
	 */
	private Object callExWithoutCreateClient(String resource, JSONObject params)
			throws RestException {

		if (client == null) {
			throw new NullPointerException(
					"请确保发起HTTP请求前，调用createClient(String)创建请求客户端。");
		}
		try {
			if (params != null) {
				Type type = new TypeToken<Map<String, String>>() {
				}.getType();
				List<NameValuePair> list = new ArrayList<NameValuePair>();
				Gson gson = new Gson();
				Map<String, String> map = gson
						.fromJson(params.toString(), type);
				for (String key : map.keySet()) {
					list.add(new BasicNameValuePair(key, map.get(key)));
				}
				postMethod
						.setEntity(new UrlEncodedFormEntity(list, HTTP.UTF_8));
			}

			HttpResponse response = client.execute(postMethod);

			Header[] headers = response.getHeaders("Set-Cookie");
			for (Header header : headers) {
				String[] parmsStrings = header.getValue().split(";");
				if (parmsStrings.length > 0) {
					setCookie(parmsStrings[0]);
				}
			}
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode != HttpStatus.SC_OK) {

				JSONObject exceptionJson = new JSONObject();
				exceptionJson.put("errcode", LocalError.CODE_10005);

				return exceptionJson;
				// throw new RestException("HTTP status code: " + statusCode
				// + " != " + HttpStatus.SC_OK);
			}
			HttpEntity callBackEntity = response.getEntity();
			String jsonCallBack = EntityUtils.toString(callBackEntity);
			return iSerializer.deserialize(jsonCallBack);
		} catch (Exception e) {
			throw new RestException(e);
		} finally {
			destoryClient();
		}

	}

	public Object callEx(String resource, JSONObject params)
			throws RestException {

		createClient(resource);

		return callExWithoutCreateClient(resource, params);
	}

	public Object callEx(String resource, JSONObject params,
			int connectionTimeOut, int soketTimeOut) throws RestException {

		createClient(resource);

		HttpConnectionParams
				.setConnectionTimeout(httpParams, connectionTimeOut);
		HttpConnectionParams.setSoTimeout(httpParams, soketTimeOut);
		Object object = callExWithoutCreateClient(resource, params);
		// 因为是无状态链接，所以无需恢复初始配置
		// HttpConnectionParams
		// .setConnectionTimeout(httpParams, connectionTimeOut);
		// HttpConnectionParams.setSoTimeout(httpParams, soketTimeOut);
		// HttpConnectionParams.setConnectionTimeout(httpParams,
		// DEFAULT_CONNECTION_TIMEOUT);
		// HttpConnectionParams.setSoTimeout(httpParams, DEFAULT_SOKET_TIMEOUT);
		return object;
	}

	public Object call(String resource) throws RestException {

		createClient(resource);

		return callExWithoutCreateClient(resource, null);
	}

	private void destoryClient() {
		postMethod = null;
		httpParams = null;
		client = null;
	}

}

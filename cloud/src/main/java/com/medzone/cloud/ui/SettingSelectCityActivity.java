package com.medzone.cloud.ui;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;

import com.medzone.cloud.ui.widget.wheelview.OnWheelChangedListener;
import com.medzone.cloud.ui.widget.wheelview.WheelView;
import com.medzone.cloud.ui.widget.wheelview.adapter.ArrayWheelAdapter;
import com.medzone.framework.data.bean.imp.Account;
import com.medzone.mcloud.R;

/**
 * 
 * @author zhy
 * 
 */
public class SettingSelectCityActivity extends Activity implements
		OnWheelChangedListener {
	/**
	 * 把全国的省市区的信息以json的格式保存，解析完成后赋值为null
	 */
	private JSONObject mJsonObj;
	/**
	 * 省的WheelView控件
	 */
	private WheelView mProvince;
	/**
	 * 市的WheelView控件
	 */
	private WheelView mCity;
	/**
	 * 区的WheelView控件
	 */
	private WheelView mArea;

	/**
	 * 所有省
	 */
	private String[] mProvinceDatas;
	/**
	 * key - 省 value - 市s
	 */
	private Map<String, String[]> mCitisDatasMap = new HashMap<String, String[]>();
	/**
	 * key - 市 values - 区s
	 */
	private Map<String, String[]> mAreaDatasMap = new HashMap<String, String[]>();

	/**
	 * 当前省的名称
	 */
	private String mCurrentProviceName;
	/**
	 * 当前市的名称
	 */
	private String mCurrentCityName;
	/**
	 * 当前区的名称
	 */
	private String mCurrentAreaName = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_city_choose);
		controlPosition();

		initJsonData();

		mProvince = (WheelView) findViewById(R.id.id_province);
		mCity = (WheelView) findViewById(R.id.id_city);
		mArea = (WheelView) findViewById(R.id.id_area);
		initDatas();

		mProvince.setViewAdapter(new ArrayWheelAdapter<String>(this,
				mProvinceDatas));
		// 添加change事件
		mProvince.addChangingListener(this);
		// 添加change事件
		mCity.addChangingListener(this);
		// 添加change事件
		mArea.addChangingListener(this);

		mProvince.setVisibleItems(5);
		mCity.setVisibleItems(5);
		mArea.setVisibleItems(5);
		updateCities();
		updateAreas();

	}

	private void controlPosition() {
		WindowManager.LayoutParams lp = getWindow().getAttributes();
		lp.width = LayoutParams.MATCH_PARENT;
		lp.gravity = Gravity.BOTTOM;
		getWindow().setAttributes(lp);
		// getWindow().setLayout(LayoutParams.MATCH_PARENT,
		// LayoutParams.WRAP_CONTENT);
	}

	/**
	 * 根据当前的市，更新区WheelView的信息
	 */
	private void updateAreas() {
		int pCurrent = mCity.getCurrentItem();
		if (mCitisDatasMap.get(mCurrentProviceName) != null
				&& mCitisDatasMap.get(mCurrentProviceName).length > pCurrent) {
			mCurrentCityName = mCitisDatasMap.get(mCurrentProviceName)[pCurrent];

			if (mAreaDatasMap.get(mCurrentCityName) != null
					&& mAreaDatasMap.get(mCurrentCityName).length > 0)
				mCurrentAreaName = mAreaDatasMap.get(mCurrentCityName)[0];

			String[] areas = mAreaDatasMap.get(mCurrentCityName);

			if (areas == null) {
				areas = new String[] { "" };
			}
			mArea.setViewAdapter(new ArrayWheelAdapter<String>(this, areas));
			mArea.setCurrentItem(0);
		}
	}

	/**
	 * 根据当前的省，更新市WheelView的信息
	 */
	private void updateCities() {
		int pCurrent = mProvince.getCurrentItem();
		mCurrentProviceName = mProvinceDatas[pCurrent];
		String[] cities = mCitisDatasMap.get(mCurrentProviceName);
		if (cities == null) {
			cities = new String[] { "" };
		}
		mCity.setViewAdapter(new ArrayWheelAdapter<String>(this, cities));
		mCity.setCurrentItem(0);
		updateAreas();
	}

	/**
	 * 解析整个Json对象，完成后释放Json对象的内存
	 */
	private void initDatas() {
		try {
			JSONArray jsonArray = mJsonObj.getJSONArray("citylist");
			mProvinceDatas = new String[jsonArray.length()];
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject jsonP = jsonArray.getJSONObject(i);// 每个省的json对象
				String province = jsonP.getString("p");// 省名字
				mProvinceDatas[i] = province;

				JSONArray jsonCs = null;
				try {
					/**
					 * Throws JSONException if the mapping doesn't exist or is
					 * not a JSONArray.
					 */
					jsonCs = jsonP.getJSONArray("c");
				} catch (Exception e1) {
					continue;
				}
				String[] mCitiesDatas = new String[jsonCs.length()];
				for (int j = 0; j < jsonCs.length(); j++) {
					JSONObject jsonCity = jsonCs.getJSONObject(j);
					String city = jsonCity.getString("n");// 市名字
					mCitiesDatas[j] = city;
					JSONArray jsonAreas = null;
					try {
						/**
						 * Throws JSONException if the mapping doesn't exist or
						 * is not a JSONArray.
						 */
						jsonAreas = jsonCity.getJSONArray("a");
					} catch (Exception e) {
						continue;
					}

					String[] mAreasDatas = new String[jsonAreas.length()];// 当前市的所有区
					for (int k = 0; k < jsonAreas.length(); k++) {
						String area = jsonAreas.getJSONObject(k).getString("s");// 区域的名称
						mAreasDatas[k] = area;
					}
					mAreaDatasMap.put(city, mAreasDatas);
				}

				mCitisDatasMap.put(province, mCitiesDatas);
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}
		mJsonObj = null;
	}

	/**
	 * 从assert文件夹中读取省市区的json文件，然后转化为json对象
	 */
	private void initJsonData() {
		try {
			StringBuffer sb = new StringBuffer();
			InputStream is = getAssets().open("city.json");

			InputStreamReader reader = new InputStreamReader(is,
					Charset.forName("UTF-8"));

			char buff[] = new char[1024];
			int len = 0;

			while ((len = reader.read(buff, 0, 1024)) > 0) {
				System.out.println(String.valueOf(buff));
				sb.append(new String(buff, 0, len));
			}
			is.close();
			//解决字符串带bom而多出的三个字节 efbbbf，android4.0自身可以处理处理
			String res = sb.toString();
			if (res != null && res.startsWith("\ufeff")) {
				res = res.substring(1);
			}
			mJsonObj = new JSONObject(res);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * change事件的处理
	 */
	@Override
	public void onChanged(WheelView wheel, int oldValue, int newValue) {
		if (wheel == mProvince) {
			mCurrentAreaName = "";
			mCurrentCityName = "";
			updateCities();
		} else if (wheel == mCity) {
			mCurrentAreaName = "";
			updateAreas();
		} else if (wheel == mArea) {
			if (mAreaDatasMap.get(mCurrentCityName) != null
					&& mAreaDatasMap.get(mCurrentCityName).length > newValue)
				mCurrentAreaName = mAreaDatasMap.get(mCurrentCityName)[newValue];
		}
	}

	public void submitSelectCity(View view) {
		Intent intent = new Intent();
		String location = mCurrentProviceName;
		if (!TextUtils.isEmpty(mCurrentCityName))
			location += "-" + mCurrentCityName;
		if (!TextUtils.isEmpty(mCurrentAreaName))
			location += "-" + mCurrentAreaName;
		intent.putExtra(Account.NAME_FIELD_LOCATION, location);
		setResult(SettingChangeAddressActivity.RESULT_SUCCESS, intent);
		this.finish();
	}

	public void cancleSelectCity(View view) {
		setResult(SettingChangeAddressActivity.RESULT_FAILED);
		this.finish();
	}
}

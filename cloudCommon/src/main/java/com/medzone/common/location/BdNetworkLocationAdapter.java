package com.medzone.common.location;

import java.util.HashMap;

import android.content.Context;
import android.location.LocationManager;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;

public class BdNetworkLocationAdapter {

	// private GeofenceClient mGeofenceClient;
	private LocationClient mLocationClient;
	private OnLocationStateChanged mOnLocationStateChanged = null;
	private BdLocationListenner mBdLocationListenner = new BdLocationListenner();
	private Context mContext;

	public BdNetworkLocationAdapter(Context context) {
		this.mContext = context;
		mLocationClient = new LocationClient(mContext);
		mLocationClient.registerLocationListener(mBdLocationListenner);
	}

	public void init() {
		setLocationOption();
	}

	public void start() {
		mLocationClient.start();
	}

	public void requestLocation() {
		mLocationClient.requestLocation();
	}

	public boolean requestLocationState() {
		return mLocationClient.isStarted();
	}

	public int requestOfflineLocation() {
		return mLocationClient.requestOfflineLocation();
	}

	public void requestPoi() {
		mLocationClient.requestOfflineLocation();
	}

	public void stop() {
		mLocationClient.stop();
	}

	public void recycle() {
		if (mLocationClient.isStarted()) {
			mLocationClient.stop();
		}
		mLocationClient = null;
	}

	public void setOnLocationStateChangedListener(
			OnLocationStateChanged onChanged) {
		this.mOnLocationStateChanged = onChanged;
	}

	public class BdLocationListenner implements BDLocationListener {
		@Override
		public void onReceiveLocation(BDLocation location) {

			if (location == null)
				return;
			HashMap<String, Object> locationMap = new HashMap<String, Object>();
			locationMap.put(IBaiduLocationAdapter.LOCATION_TIME,
					location.getTime());
			locationMap.put(IBaiduLocationAdapter.LOCATION_ERROR_CODE,
					location.getLocType());
			locationMap.put(IBaiduLocationAdapter.LOCATION_LATITUDE,
					location.getLatitude());
			locationMap.put(IBaiduLocationAdapter.LOCATION_LONGITUDE,
					location.getLongitude());
			locationMap.put(IBaiduLocationAdapter.LOCATION_RADIUS,
					location.getRadius());

			if (location.getLocType() == BDLocation.TypeGpsLocation) {
				locationMap.put(IBaiduLocationAdapter.LOCATION_SPEED,
						location.getSpeed());
				locationMap.put(IBaiduLocationAdapter.LOCATION_SATELLITE,
						location.getSatelliteNumber());
			} else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {
				locationMap.put(IBaiduLocationAdapter.LOCATION_PROVINCE,
						location.getProvince());
				locationMap.put(IBaiduLocationAdapter.LOCATION_CITY,
						location.getCity());
				locationMap.put(IBaiduLocationAdapter.LOCATION_DISTRICT,
						location.getDistrict());
				locationMap.put(IBaiduLocationAdapter.LOCATION_ADDRSTR,
						location.getAddrStr());
			}
			// sb.append(mLocationClient.getVersion());
			// sb.append(location.isCellChangeFlag());

			if (null != mOnLocationStateChanged) {
				mOnLocationStateChanged.onLocationStateChanged(locationMap);
			}
		}

		@Override
		public void onReceivePoi(BDLocation poiLocation) {
			if (poiLocation == null) {
				return;
			}
			HashMap<String, Object> locationMap = new HashMap<String, Object>();
			locationMap.put(IBaiduLocationAdapter.LOCATION_TIME,
					poiLocation.getTime());
			locationMap.put(IBaiduLocationAdapter.LOCATION_ERROR_CODE,
					poiLocation.getLocType());
			locationMap.put(IBaiduLocationAdapter.LOCATION_LATITUDE,
					poiLocation.getLatitude());
			locationMap.put(IBaiduLocationAdapter.LOCATION_LONGITUDE,
					poiLocation.getLongitude());
			locationMap.put(IBaiduLocationAdapter.LOCATION_RADIUS,
					poiLocation.getRadius());
			if (poiLocation.getLocType() == BDLocation.TypeNetWorkLocation) {
				locationMap.put(IBaiduLocationAdapter.LOCATION_ADDRSTR,
						poiLocation.getAddrStr());
			}
			if (poiLocation.hasPoi()) {
				locationMap.put(IBaiduLocationAdapter.LOCATION_POI,
						poiLocation.getPoi());
			}

			if (null != mOnLocationStateChanged) {
				mOnLocationStateChanged.onLocationStateChanged(locationMap);
			}
		}
	}

	private void setLocationOption() {
		LocationClientOption option = new LocationClientOption();
		option.setCoorType("bd09ll");
		option.setScanSpan(5000);
		option.setLocationMode(LocationMode.Hight_Accuracy);// 设置定位模式
		option.setIsNeedAddress(true);// 返回的定位结果包含地�?���?
		mLocationClient.setLocOption(option);
	}

	public boolean getGpsState() {

		LocationManager mLocationManager = (LocationManager) mContext
				.getSystemService(Context.LOCATION_SERVICE);
		return mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
	}
}

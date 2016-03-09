package com.medzone.common.location;


public interface ILocationAdapter {

	public static final String LOCATION_TIME = "LOCATION_TIME";
	public static final String LOCATION_ERROR_CODE = "LOCATION_ERROR_CODE";
	public static final String LOCATION_LATITUDE = "LOCATION_LATITUDE";
	public static final String LOCATION_LONGITUDE = "LOCATION_LONGITUDE";
	public static final String LOCATION_RADIUS = "LOCATION_RADIUS";

	public void init();

	public void start();

	public void setOnLocationStateChangedListener(
			OnLocationStateChanged onChanged);

	public void requestLocation();

	public int requestOfflineLocation();

	public void requestPoi();

	public boolean requestLocationState();

	public void stop();

	public void recycle();

}

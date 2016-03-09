package com.medzone.common.location;

public class BdNetworkLocationTarget implements IBaiduLocationAdapter {

	private BdNetworkLocationAdapter mAdaptee;

	public BdNetworkLocationTarget(BdNetworkLocationAdapter adaptee) {
		this.mAdaptee = adaptee;
	}

	@Override
	public void init() {
		mAdaptee.init();
	}

	@Override
	public void start() {
		mAdaptee.start();

	}

	@Override
	public void requestLocation() {
		mAdaptee.requestLocation();
	}

	@Override
	public boolean requestLocationState() {
		return mAdaptee.requestLocationState();
	}

	@Override
	public int requestOfflineLocation() {
		return mAdaptee.requestOfflineLocation();
	}

	@Override
	public void requestPoi() {
		mAdaptee.requestPoi();
	}

	@Override
	public void stop() {
		mAdaptee.stop();
	}

	@Override
	public void recycle() {
		mAdaptee.recycle();
	}

	@Override
	public void setOnLocationStateChangedListener(
			OnLocationStateChanged onChanged) {
		mAdaptee.setOnLocationStateChangedListener(onChanged);
	}

}

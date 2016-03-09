package com.medzone.framework.data.bean.imp;

import java.io.Serializable;

public class Device implements Serializable {
	/**
	 * 设备对象
	 */
	private static final long serialVersionUID = 1L;

	private String deviceCategory;
	private String deviceType;

	public Device() {

	}

	public Device(String category, String type) {
		this.deviceCategory = category;
		this.deviceType = type;
	}

	public String getDeviceCategory() {
		return deviceCategory;
	}

	public void setDeviceCategory(String deviceCategory) {
		this.deviceCategory = deviceCategory;
	}

	public String getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(String deviceType) {
		this.deviceType = deviceType;
	}

}

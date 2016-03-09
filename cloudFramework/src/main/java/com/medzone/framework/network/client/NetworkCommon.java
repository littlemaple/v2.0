package com.medzone.framework.network.client;

import com.medzone.framework.network.serializer.ISerializer;

public abstract class NetworkCommon<T> {

	protected ISerializer<?> iSerializer;

	public NetworkCommon() {
		initISerializer();
	}

	public void setSerializer(ISerializer<T> serializer) {
		this.iSerializer = serializer;
	}

	abstract void initISerializer();

}

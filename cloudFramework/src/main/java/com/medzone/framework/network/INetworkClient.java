package com.medzone.framework.network;

import com.medzone.framework.network.exception.RestException;

/**
 * 
 * @author ChenJunQi.
 * 
 */
public interface INetworkClient {

	/* public void createClient(); */

	/* public void distoryClient(); */

	/**
	 * TODO 使用泛型+反射的机制去返回一个希望返回的类型，替代Object
	 * 
	 * @param resource
	 * @return responseObject.
	 * @throws RestException
	 */
	public Object call(String resource) throws RestException;

	public Object callEx(String resource, Object params) throws RestException;

	public Object callEx(String resource, Object params, int connectionTimeOut,
			int soketTimeOut) throws RestException;

}

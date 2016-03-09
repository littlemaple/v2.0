/**
 * 
 */
package com.medzone.cloud.cache;

import java.util.List;

/**
 * @author ChenJunQi. 2014年9月10日
 * 
 */
public interface IDataSynchronous<T> {

	/**
	 * 
	 * 
	 */
	public List<T> readUploadData();

	public String readUploadDataJSON();

}

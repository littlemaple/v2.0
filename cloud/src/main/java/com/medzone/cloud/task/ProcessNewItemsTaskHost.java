/**
 * 
 */
package com.medzone.cloud.task;

import java.util.List;

import com.medzone.framework.data.bean.BaseDatabaseObject;

/**
 * @author ChenJunQi.
 * 
 */
public class ProcessNewItemsTaskHost<T extends BaseDatabaseObject> {

	public void onPostExecuteProcessBalance(List<T> list) {

	}
}
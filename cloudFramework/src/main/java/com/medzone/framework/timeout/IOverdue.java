/**
 * 
 */
package com.medzone.framework.timeout;

import android.content.Context;

/**
 * @author ChenJunQi. 2014年10月2日
 * 
 */
public interface IOverdue {

	void updateOverdue(int key, long value);

	long getCurrentOverdueValue(Context context);

	boolean isTimeOut(Context context, long lastUpdateTime);
}

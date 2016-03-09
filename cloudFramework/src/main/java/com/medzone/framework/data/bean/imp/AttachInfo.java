/**
 * 
 */
package com.medzone.framework.data.bean.imp;

import android.os.Handler;

/**
 * @author ChenJunQi.
 * 
 *         AttachInfo与Controller为1：1关系
 */
public class AttachInfo {

	public AttachInfo() {
	}

	public String mModuleId;

	public int mNestCount = 0;

	public Account mAccount;

	public Handler mHandler;

}
/**
 * 
 */
package com.medzone.cloud.module;

import android.content.Context;

import com.medzone.framework.data.bean.imp.AttachInfo;
import com.medzone.framework.module.BaseModule;

/**
 * @author ChenJunQi.
 */
public abstract class CloudBaseModule extends BaseModule {

	protected AttachInfo mAttachInfo;

	public CloudBaseModule() {
		super();
	}

	public void init(Context context, AttachInfo attachInfo) {
		super.init(context);
		this.mAttachInfo = attachInfo;
	}

}

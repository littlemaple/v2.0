/**
 * 
 */
package com.medzone.cloud.module.modules;

import android.content.Context;

import com.medzone.cloud.controller.AbstractUseTaskCacheController;
import com.medzone.cloud.module.CloudBaseModule;
import com.medzone.framework.data.bean.imp.AttachInfo;

/**
 * @author ChenJunQi.
 * 
 */
public abstract class CloudUseTaskCacheControllerModule<T extends AbstractUseTaskCacheController<?, ?>>
		extends CloudBaseModule {

	protected T controller;

	@Override
	public void init(Context context, AttachInfo attachInfo) {
		super.init(context, attachInfo);
		controller = createCacheController();
		if (controller != null) {
			controller.setAttachInfo(mAttachInfo);
		}
	}

	public T getCacheController() {
		return controller;
	}

	public AttachInfo getAttachInfo() {
		return controller.getAttachInfo();
	}

	protected abstract T createCacheController();

}

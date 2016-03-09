/**
 * 
 */
package com.medzone.cloud.module;

import com.medzone.framework.module.IModuleNotificationHost;
import com.medzone.framework.module.ModuleSpecification;

/**
 * @author ChenJunQi.
 * 
 */
public class CloudModuleNotificationHost implements IModuleNotificationHost {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.medzone.common.module.IModuleNotificationHost#pushNotification(com
	 * .medzone.common.module.ModuleSpecification, java.lang.String, int)
	 */
	@Override
	public void pushNotification(ModuleSpecification ms, String content,
			int count) {
		// TODO 在这里加载一个通知中心，以便于实现处于给各个模块发送新的消息

	}

}

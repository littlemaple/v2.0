/**
 * 
 */
package com.medzone.framework.module;

/**
 * @author ChenJunQi.
 * 
 * @see 废弃原因：因为模块已经使用外部全局的{@link PropertyCenter},界面呈现无需再去管理新消息事件。
 */
@Deprecated
public interface IModuleNotificationHost {

	/**
	 * Push notification to host. This will be call when plugin have new message
	 * want to notify the main application.
	 * 
	 * @param ms
	 * @param content
	 * @param count
	 */
	public void pushNotification(ModuleSpecification ms, String content,
			int count);

}

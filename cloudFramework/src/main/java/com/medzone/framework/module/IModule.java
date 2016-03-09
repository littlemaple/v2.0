/**
 * 
 */
package com.medzone.framework.module;

import android.graphics.drawable.Drawable;

/**
 * @author ChenJunQi.
 * 
 */
public interface IModule {

	/**
	 * 
	 * @return 模块图标的Drawable对象，若模块初始化时未配置头像，则返回NULL。
	 */
	public Drawable getDrawable();

	/**
	 * 
	 * @return 模块的介绍说明文案。
	 */
	public String getIntroduce();

	/**
	 * 
	 * @return 模块名。
	 */
	public String getName();
}

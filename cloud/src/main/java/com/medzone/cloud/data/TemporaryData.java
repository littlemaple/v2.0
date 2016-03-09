/**
 * 
 */
package com.medzone.cloud.data;

import java.util.HashMap;

/**
 * @author ChenJunQi.
 *
 */
/**
 * 
 * 主要用于临时保存javaBean对象 <br/>
 * <br/>
 * 
 * 1、可用于Activity必需数据的保存，即使Activity被销毁，再次启动也能恢复数据<br/>
 * 
 * 2、Activity之间跳转时用于传递数据、由于用intent传递对象需序列化的复杂性，用HashMap保存可简化<br/>
 * 
 * 3、存储其他重要数据，应用可以快速恢复启动<br/>
 */
public class TemporaryData {

	private static HashMap<String, Object> hm = new HashMap<String, Object>();

	public static void save(String key, Object value) {
		hm.put(key, value);
	}

	public static Object get(String key) {
		return hm.remove(key);
	}

	public static boolean containsKey(String key) {
		return hm.containsKey(key);
	}

	public static void clear() {
		hm.clear();
	}

}

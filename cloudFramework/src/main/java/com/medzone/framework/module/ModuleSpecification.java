/**
 * 
 */
package com.medzone.framework.module;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.medzone.framework.network.NetworkClientResult;

/**
 * @author ChenJunQi.
 * 
 */
public class ModuleSpecification implements Cloneable {

	public static final String EXTRA_ATTRIBUTES_DEFAULT_INSTALL = "default_install";
	public static final String EXTRA_ATTRIBUTES_DEFAULT_SHOW_IN_HOMEPAGE = "default_show_in_homepage";
	public static final String EXTRA_ATTRIBUTES_IS_UNINSTALLABLE = "is_uninstallable";

	/** 模块ID */
	protected String mModuleID;
	/** 模块的类别 */
	protected Integer mCategory;
	/** 模块显示的顺序 */
	protected Integer mOrder;
	/** 模块的状态，安装，显示，隐藏，卸载 */
	protected ModuleStatus mModuleStatus;
	/** 包名 */
	protected String mPackageName;
	/** 入口类名 */
	protected String mClassName;
	/** 模块的APK下载地址 */
	protected String mDownLoadLink;
	/**
	 * 该模块在xml配置文件中所处的位置，从上到下从0递增，该顺序不允许变更
	 * 
	 * @see 废弃原因：{@link #mOrder}调整为魔法数，可替代{@link #mOrderItem}。
	 * */
	@Deprecated
	protected Integer mOrderItem;
	/** 该模块的同步序列号 */
	protected Integer mDownSerial;
	/** 该模块的设置 */
	protected String mSetting;

	/**
	 * 存储额外的配置属性集合，例如{@link #EXTRA_ATTRIBUTES_DEFAULT_INSTALL},
	 * {@link #EXTRA_ATTRIBUTES_DEFAULT_SHOW_IN_HOMEPAGE},
	 * {@link #EXTRA_ATTRIBUTES_IS_UNINSTALLABLE}等.
	 */
	protected HashMap<String, String> extraAttribs;

	public String getModuleID() {
		return mModuleID;
	}

	public void setModuleID(String mModuleID) {
		this.mModuleID = mModuleID;
	}

	public Integer getCategory() {
		return mCategory;
	}

	public void setCategory(Integer mCategory) {
		this.mCategory = mCategory;
	}

	public Integer getOrder() {
		return mOrder;
	}

	public void setOrder(Integer mOrder) {
		this.mOrder = mOrder;
	}

	public ModuleStatus getModuleStatus() {
		return mModuleStatus;
	}

	public void setModuleStatus(ModuleStatus mModuleStatus) {
		this.mModuleStatus = mModuleStatus;
	}

	public String getPackageName() {
		return mPackageName;
	}

	public void setPackageName(String mPackageName) {
		this.mPackageName = mPackageName;
	}

	public String getClassName() {
		return mClassName;
	}

	public void setClassName(String mClassName) {
		this.mClassName = mClassName;
	}

	public String getDownLoadLink() {
		return mDownLoadLink;
	}

	public void setDownLoadLink(String mDownLoadLink) {
		this.mDownLoadLink = mDownLoadLink;
	}

	public Integer getDownSerial() {
		return mDownSerial;
	}

	public void setDownSerial(Integer mDownSerial) {
		this.mDownSerial = mDownSerial;
	}

	public String getSetting() {
		return mSetting;
	}

	public void setSetting(String mSetting) {
		this.mSetting = mSetting;
	}

	public void putExtraAttribute(String key, String value) {
		if (extraAttribs == null) {
			extraAttribs = new HashMap<String, String>();
		}
		extraAttribs.put(key, value);
	}

	public String getExtraAttributeString(String key) {
		if (extraAttribs != null) {
			return extraAttribs.get(key);
		}
		return null;
	}

	public boolean getExtraAttributeBool(String key, boolean defaultValue) {
		String attrib = getExtraAttributeString(key);
		if (attrib != null) {
			return Boolean.parseBoolean(attrib);
		}
		return defaultValue;
	}

	@Override
	public Object clone() {
		ModuleSpecification clone = null;
		try {
			clone = (ModuleSpecification) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return clone;
	}

	public static List<ModuleSpecification> createModuleSpecificationList(
			NetworkClientResult res) {
		JSONObject root = res.getResponseResult();
		try {
			JSONArray ja = root.getJSONArray("root");
			List<ModuleSpecification> list = new ArrayList<ModuleSpecification>();
			for (int i = 0; i < ja.length(); i++) {
				ModuleSpecification ms = createModuleSpecification(ja
						.getJSONObject(i));
				if (ms != null) {
					list.add(ms);
				}
			}
			return list;
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
	}

	private static ModuleSpecification createModuleSpecification(JSONObject o) {

		try {
			ModuleSpecification ms = new ModuleSpecification();

			if (o.has("moduleid")) {
				ms.setModuleID(o.getString("moduleid"));
			}
			if (o.has("category")) {
				ms.setCategory(o.getInt("category"));
			}
			if (o.has("classname")) {
				ms.setClassName(o.getString("classname"));
			}
			if (o.has("link")) {
				ms.setDownLoadLink(o.getString("link"));
			}
			if (o.has("packagename")) {
				ms.setPackageName(o.getString("packagename"));
			}
			if (o.has("order")) {
				ms.setOrder(o.getInt("order"));
			}
			if (o.has("status")) {
				// HIDDEN(-2), UNINSTALL(-1), INITIAL(0), INSTALL(1),
				// DISPLAY(2);
				switch (o.getInt("status")) {
				case -2:
					ms.setModuleStatus(ModuleStatus.HIDDEN);
					break;
				case -1:
					ms.setModuleStatus(ModuleStatus.UNINSTALL);
					break;
				case 0:
					ms.setModuleStatus(ModuleStatus.INITIAL);
					break;
				case 1:
					ms.setModuleStatus(ModuleStatus.INSTALL);
					break;
				case 2:
					ms.setModuleStatus(ModuleStatus.DISPLAY);
					break;
				default:
					break;
				}
			}
			if (o.has("settings") && !o.isNull("settings")) {
				ms.setSetting(o.getString("settings"));
			}
			if (o.has("default_install")) {
				ms.putExtraAttribute(EXTRA_ATTRIBUTES_DEFAULT_INSTALL,
						o.getString("default_install"));
			}
			if (o.has("default_show_in_homepage")) {
				ms.putExtraAttribute(EXTRA_ATTRIBUTES_DEFAULT_SHOW_IN_HOMEPAGE,
						o.getString("default_show_in_homepage"));
			}
			if (o.has("is_uninstallable")) {
				ms.putExtraAttribute(EXTRA_ATTRIBUTES_IS_UNINSTALLABLE,
						o.getString("is_uninstallable"));
			}

			return ms;
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}

	}
}

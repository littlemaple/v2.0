///**
// * 
// */
//package com.medzone.framework.module;
//
//import android.content.Context;
//
//import com.medzone.framework.data.bean.imp.AttachInfo;
//
//public abstract class BaseMeasureModule extends BaseModule {
//
//	protected AttachInfo mAttachInfo;
//	
//
//	/**
//	 * 
//	 */
//	public BaseMeasureModule() {
//		// TODO Auto-generated constructor stub
//	}
//	/**
//	 * 
//	 * 需要调用 {@link #init(Context, AttachInfo, ModuleSpecification)} 去初始化测量模块配置。
//	 * 
//	 * @author Local_ChenJunQi
//	 * 
//	 */
//	public void init(Context context, AttachInfo attachInfo,
//			ModuleSpecification spec) {
//		super.init(context);
//		mAttachInfo = attachInfo;
//		mModuleSpecification = spec;
//	}
//
//	public ModuleSpecification getDefaultSpecification() {
//		return null;
//	};
//
//	public boolean isDefaultInstall() {
//		String isDefaultInstall = mModuleSpecification
//				.getExtraAttributeString(ModuleSpecification.EXTRA_ATTRIBUTES_DEFAULT_INSTALL);
//		return Boolean.valueOf(isDefaultInstall);
//	}
//
//	public boolean isShowInHomePage() {
//		String isShowInHomePage = mModuleSpecification
//				.getExtraAttributeString(ModuleSpecification.EXTRA_ATTRIBUTES_DEFAULT_SHOW_IN_HOMEPAGE);
//		return Boolean.valueOf(isShowInHomePage);
//	}
//
//	public boolean isUnInstallable() {
//		String isUnInstallable = mModuleSpecification
//				.getExtraAttributeString(ModuleSpecification.EXTRA_ATTRIBUTES_IS_UNINSTALLABLE);
//		return Boolean.valueOf(isUnInstallable);
//	}
//
//	public String getModuleDownLoadPath() {
//		return mModuleSpecification.getDownLoadLink();
//	}
//
//	public String getModulePackagePath() {
//		return mModuleSpecification.getPackageName();
//	}
//
//	public String getModuleActivityPath() {
//		return mModuleSpecification.getClassName();
//	}
//
//	public int getOrder() {
//		return mModuleSpecification.getOrder();
//	}
//
//	public String getExtraAttributeString(String key) {
//		return mModuleSpecification.getExtraAttributeString(key);
//	}
//
//	public Boolean getExtraAttributeBool(String key, boolean defaultValue) {
//		return mModuleSpecification.getExtraAttributeBool(key, defaultValue);
//	}
//
//	public String getModuleID() {
//		return mModuleSpecification.getModuleID();
//	}
//
//	public int getCategory() {
//		return mModuleSpecification.getCategory();
//	}
//
//	public ModuleStatus getModuleStatus() {
//		return mModuleSpecification.getModuleStatus();
//	}
//
//	public String getModuleSetting() {
//		return mModuleSpecification.getSetting();
//	}
//
//	public int getDownSerial() {
//		if (mModuleSpecification.getDownSerial() == null) {
//			mModuleSpecification.setDownSerial(Integer.valueOf(0));
//		}
//		return mModuleSpecification.getDownSerial().intValue();
//	}
//
//	public ModuleSpecification getModuleSpecification() {
//		return mModuleSpecification;
//	}
//
//	public void setModuleSpecification(ModuleSpecification moduleSpecification) {
//		this.mModuleSpecification = moduleSpecification;
//	}
//
//}

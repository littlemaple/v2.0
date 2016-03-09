/**
 * 
 */
package com.medzone.cloud.module.modules;

import java.util.HashMap;
import java.util.List;

import com.medzone.cloud.CloudApplication;
import com.medzone.cloud.controller.EarTemperatureController;
import com.medzone.cloud.module.CloudMeasureModule;
import com.medzone.cloud.ui.fragment.temperature.EarTemperatureFragment;
import com.medzone.framework.data.bean.imp.Account;
import com.medzone.framework.data.bean.imp.EarTemperature;
import com.medzone.framework.module.ModuleSpecification;
import com.medzone.framework.module.ModuleStatus;
import com.medzone.framework.task.TaskHost;
import com.medzone.mcloud.R.drawable;

/**
 * 	
 */
public class EarTemperatureModule extends
		CloudMeasureModule<EarTemperatureController> implements
		IMonthlyStatInterface {

	public static final String TYPE = EarTemperature.EARTEMPERATURE_TAG;

	public static final int DEFAULT_DOWN_SERIAL = 0;
	public static final int ORDER_IN_CATEGORY = 2;
	public static final int CATEGORY = 0;

	public static final String NAME = "耳温";
	public static final String ID = "TemperatureID";
	public static final String DOWNLOAD_LINK = "www.mcloudlife.com";

	public static ModuleSpecification defaultSpec;

	private EarTemperatureModule() {
	}

	@Override
	protected EarTemperatureController createCacheController() {
		EarTemperatureController controller = new EarTemperatureController();
		controller.setBindModule(this);
		return controller;
	}

	@Override
	protected void createFragment(int key) {
		switch (key) {
		case FRAGMENT_TYPE_HISTORY:
			EarTemperatureFragment fragment = new EarTemperatureFragment();
			fragment.setController(getCacheController());
			addFragment(key, fragment);
			break;

		default:
			break;
		}
	}

	@Override
	public int getMonthlyIndicator() {
		return drawable.myxinyunview_ic_earthermometer;
	}

	@Override
	public int getMonthlyAllCounts(Integer year, Integer month) {
		return getCacheController().getMonthlyAllCounts(year, month);
	}

	@Override
	public int getMonthlyExceptionCounts(Integer year, Integer month) {
		return getCacheController().getMonthlyExceptionCounts(year, month);
	}

	public List<HashMap<String, String>> getEarTemperatureRecord(Integer year) {
		return getCacheController().getEarTemperatureRecord(year);
	}

	public List<EarTemperature> getMonthlyAllData(Integer year, Integer month) {
		return getCacheController().getMonthlyAllData(year, month);
	}

	public List<EarTemperature> getMonthlyLimitData(Integer year, Integer month) {
		return getCacheController().getMonthlyLimitData(year, month);
	}

	public List<HashMap<String, String>> getYearMonthAbnormal(Integer year,
			Integer month) {
		return getCacheController().getYearMonthAbnormal(year, month);
	}

	public EarTemperature getYearMonthMaxTemperature(Integer year, Integer month) {
		return getCacheController().getYearMonthMaxTemperature(year, month);
	}

	public void deleteCloudEarTemperature(int recordID, Account account,
			TaskHost taskHost) {
		getCacheController().deleteCloudEarTemperature(recordID, account,
				taskHost);
	}

	public void deleteLocalEarTemperature(EarTemperature item) {
		getCacheController().deleteLocalEarTemperature(item);
	}

	public String getFristMeasureTime(Account account) {
		return getCacheController().getFristMeasureTime(account);
	}

	public EarTemperature getMeasureEarTemperature(String measureUid) {
		return getCacheController().getMeasureEarTemperature(measureUid);
	}

	@Override
	public ModuleSpecification getDefaultSpecification() {
		return getDefaultSpecification(true);
	}

	public static ModuleSpecification getDefaultSpecification(boolean isDisplay) {

		if (defaultSpec == null) {
			defaultSpec = new ModuleSpecification();
		}

		defaultSpec.setOrder(ORDER_IN_CATEGORY);
		defaultSpec.setDownSerial(DEFAULT_DOWN_SERIAL);
		defaultSpec.setModuleID(ID);
		defaultSpec.setCategory(CATEGORY);
		defaultSpec.setDownLoadLink(DOWNLOAD_LINK);
		defaultSpec.setClassName(EarTemperatureModule.class.getCanonicalName());
		// defaultSpec.setModuleStatus(isDisplay ? ModuleStatus.DISPLAY
		// : ModuleStatus.HIDDEN);
		defaultSpec.setModuleStatus(ModuleStatus.UNINSTALL);
		defaultSpec.setPackageName(CloudApplication.getInstance()
				.getPackageName());

		defaultSpec.putExtraAttribute(
				ModuleSpecification.EXTRA_ATTRIBUTES_DEFAULT_INSTALL, "true");
		defaultSpec.putExtraAttribute(
				ModuleSpecification.EXTRA_ATTRIBUTES_DEFAULT_SHOW_IN_HOMEPAGE,
				"true");
		defaultSpec.putExtraAttribute(
				ModuleSpecification.EXTRA_ATTRIBUTES_IS_UNINSTALLABLE, "true");
		return defaultSpec;
	}

}

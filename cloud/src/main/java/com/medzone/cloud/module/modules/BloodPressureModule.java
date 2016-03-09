/**
 * 
 */
package com.medzone.cloud.module.modules;

import java.util.HashMap;
import java.util.List;

import com.medzone.cloud.CloudApplication;
import com.medzone.cloud.controller.BloodPressureController;
import com.medzone.cloud.module.CloudMeasureModule;
import com.medzone.cloud.ui.fragment.bloodpressure.BloodPressureFragment;
import com.medzone.framework.Log;
import com.medzone.framework.data.bean.imp.Account;
import com.medzone.framework.data.bean.imp.BloodPressure;
import com.medzone.framework.data.bean.imp.GroupMember;
import com.medzone.framework.module.ModuleSpecification;
import com.medzone.framework.module.ModuleStatus;
import com.medzone.framework.task.TaskHost;
import com.medzone.mcloud.R.drawable;

/**
 * 	
 */
public class BloodPressureModule extends
		CloudMeasureModule<BloodPressureController> implements
		IMonthlyStatInterface {
	public static final String TYPE = BloodPressure.BLOODPRESSURE_TAG;

	public static final int DEFAULT_DOWN_SERIAL = 0;
	public static final int ORDER_IN_CATEGORY = 0;
	public static final int CATEGORY = 0;

	public static final String NAME = "血压";
	public static final String ID = "BloodPressureID";
	public static final String DOWNLOAD_LINK = "www.mcloudlife.com";

	public static final int FLAG_POSITION_UNIT_SWITCH = 0;
	public static final char FLAG_POSITION_UNIT_SWITCH_KPA = '1';
	public static final char FLAG_POSITION_UNIT_SWITCH_MMHG = '0';

	public static ModuleSpecification defaultSpec;

	private BloodPressureModule() {
	}

	@Override
	protected BloodPressureController createCacheController() {
		BloodPressureController controller = new BloodPressureController();
		controller.setBindModule(this);
		return controller;
	}

	@Override
	protected void createFragment(int key) {
		switch (key) {
		case FRAGMENT_TYPE_HISTORY:
			BloodPressureFragment fragment = new BloodPressureFragment();
			fragment.setController(getCacheController());
			addFragment(key, fragment);
			break;

		default:
			break;
		}
	}

	public boolean isKpaMode() {
		char setting = getPositionSetting(BloodPressureModule.FLAG_POSITION_UNIT_SWITCH);
		if (setting == DEFAULT_MODULE_SETTING) {
			return false;
		} else if (setting == FLAG_POSITION_UNIT_SWITCH_KPA) {
			return true;
		} else if (setting == FLAG_POSITION_UNIT_SWITCH_MMHG) {
			return false;
		}
		Log.w("detect uncatched branch at isKpaMode().");
		return false;
	}

	@Override
	public int getMonthlyIndicator() {
		return drawable.myxinyunview_ic_bloodpressure;
	}

	@Override
	public int getMonthlyAllCounts(Integer year, Integer month) {
		return getCacheController().getMonthlyAllCounts(year, month);
	}

	@Override
	public int getMonthlyExceptionCounts(Integer year, Integer month) {
		return getCacheController().getMonthlyExceptionCounts(year, month);
	}

	public List<HashMap<String, String>> getBloodPressureRecord(Integer year) {
		return getCacheController().getBloodPressureRecord(year);
	}

	public List<BloodPressure> getMonthlyAllData(Integer year, Integer month) {
		return getCacheController().getMonthlyAllData(year, month);
	}

	public void deleteCloudBloodPressure(int recordID, Account account,
			TaskHost taskHost) {
		getCacheController().deleteCloudBloodPressure(recordID, account,
				taskHost);
	}

	public void deleteLocalBloodPressure(BloodPressure bp) {
		getCacheController().deleteLocalBloodPressure(bp);
	}

	public List<HashMap<String, String>> getYearMonthAbnormal(Integer year,
			Integer month) {
		return getCacheController().getYearMonthAbnormal(year, month);
	}

	public BloodPressure getYearMonthMaxPressure(Integer year, Integer month) {
		return getCacheController().getYearMonthMaxPressure(year, month);
	}

	public List<BloodPressure> getMonthlyLimitData(Integer year, Integer month) {
		return getCacheController().getMonthlyLimitData(year, month);
	}

	public List<BloodPressure> getUpPageLocalData() {
		return getCacheController().readUpPageFromLocalWithoutCache();
	}

	public List<BloodPressure> getDownPageLocalData(boolean isBegin) {
		return getCacheController().readDownPageFromLocalWithoutCache(isBegin);
	}

	public Long getFristMeasureTime() {
		return getCacheController().getFristMeasureTime();
	}

	public BloodPressure getMeasureBloodPressure(String measureUid) {
		return getCacheController().getMeasureBloodPressure(measureUid);
	}

	public void synchroRecordUpload(GroupMember groupmember,
			BloodPressure bloodpressure, TaskHost taskHost) {
		getCacheController().synchroRecordUpload(groupmember, bloodpressure,
				taskHost);
	}

	public void getNearlyRecord(Account account, BloodPressure bloodpressure,
			TaskHost taskHost) {
		getCacheController().getNearlyRecord(account, bloodpressure, taskHost);
	}

	public void getDoctorAdvised(BloodPressure bloodpressure, TaskHost taskHost) {
		getCacheController().getDoctorAdvised(bloodpressure, taskHost);
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
		defaultSpec.setClassName(BloodPressureModule.class.getCanonicalName());
		defaultSpec.setSetting("0");// mmhg

		defaultSpec.setModuleStatus(isDisplay ? ModuleStatus.DISPLAY
				: ModuleStatus.HIDDEN);
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

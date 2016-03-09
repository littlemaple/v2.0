/**
 * 
 */
package com.medzone.cloud.module.modules;

import java.util.HashMap;
import java.util.List;

import com.medzone.cloud.CloudApplication;
import com.medzone.cloud.controller.BloodOxygenController;
import com.medzone.cloud.module.CloudMeasureModule;
import com.medzone.cloud.ui.fragment.bloodoxygen.BloodOxygenFragment;
import com.medzone.framework.data.bean.imp.Account;
import com.medzone.framework.data.bean.imp.BloodOxygen;
import com.medzone.framework.data.bean.imp.GroupMember;
import com.medzone.framework.module.ModuleSpecification;
import com.medzone.framework.module.ModuleStatus;
import com.medzone.framework.task.TaskHost;
import com.medzone.mcloud.R;

/**
 * 	
 */
public class BloodOxygenModule extends
		CloudMeasureModule<BloodOxygenController> implements
		IMonthlyStatInterface {

	public static final String TYPE = BloodOxygen.BLOODOXYGEN_TAG;

	// 模块ID必须与服务端事先约定好，保证与XML配置中的一致
	public static final int DEFAULT_DOWN_SERIAL = 0;
	public static final int ORDER_IN_CATEGORY = 1;
	public static final int CATEGORY = 0;

	public static final String NAME = "血氧";
	public static final String ID = "BloodOxygenID";
	public static final String DOWNLOAD_LINK = "www.mcloudlife.com";

	public static ModuleSpecification defaultSpec;

	/** 血氧警报 */
	public static final int FLAG_POSITION_ALERT = 0;

	private BloodOxygenModule() {
	}

	@Override
	protected BloodOxygenController createCacheController() {
		BloodOxygenController controller = new BloodOxygenController();
		controller.setBindModule(this);
		return controller;
	}

	@Override
	public int getMonthlyIndicator() {
		return R.drawable.myxinyunview_ic_oxygen;
	}

	@Override
	public int getMonthlyAllCounts(Integer year, Integer month) {
		return getCacheController().getMonthlyAllCounts(year, month);
	}

	@Override
	public int getMonthlyExceptionCounts(Integer year, Integer month) {
		return getCacheController().getMonthlyExceptionCounts(year, month);
	}

	@Override
	protected void createFragment(int key) {
		switch (key) {
		case FRAGMENT_TYPE_HISTORY:
			BloodOxygenFragment fragment = new BloodOxygenFragment();
			fragment.setController(getCacheController());
			addFragment(key, fragment);
			break;

		default:
			break;
		}

	}

	// ================================
	// 临时处理成外部接口，以下方式应当被废弃。
	// ================================
	public List<HashMap<String, String>> getBloodOxygenRecord(Integer year) {
		return getCacheController().getBloodOxygenRecord(year);
	}

	public List<BloodOxygen> getMonthlyLimitData(Integer year, Integer month) {
		return getCacheController().getMonthlyLimitData(year, month);
	}

	public List<BloodOxygen> getMonthlyAllData(Integer year, Integer month) {
		return getCacheController().getMonthlyAllData(year, month);
	}

	public List<HashMap<String, String>> getYearMonthAbnormal(Integer year,
			Integer month) {
		return getCacheController().getYearMonthAbnormal(year, month);
	}

	public BloodOxygen getYearMonthMinOxygen(Integer year, Integer month) {
		return getCacheController().getYearMonthMinOxygen(year, month);
	}

	public void deleteCloudBloodOxygen(int recordID, Account account,
			TaskHost taskHost) {
		getCacheController()
				.deleteCloudBloodOxygen(recordID, account, taskHost);
	}

	public void deleteLocalBloodOxygen(BloodOxygen ox) {
		getCacheController().deleteLocalBloodOxygen(ox);
	}

	public Long getFristMeasureTime() {
		return getCacheController().getFristMeasureTime();
	}

	public BloodOxygen getMeasureBloodOxygen(String measureUid) {
		return getCacheController().getMeasureBloodOxygen(measureUid);
	}

	public void synchroRecordUpload(GroupMember groupmember,
			BloodOxygen bloodoxygen, TaskHost taskHost) {
		getCacheController().synchroRecordUpload(groupmember, bloodoxygen,
				taskHost);
	}

	public void getNearlyRecord(Account account, BloodOxygen bloodoxygen,
			TaskHost taskHost) {
		getCacheController().getNearlyRecord(account, bloodoxygen, taskHost);
	}

	public void getDoctorAdvised(BloodOxygen bloodoxygen, TaskHost taskHost) {
		getCacheController().getDoctorAdvised(bloodoxygen, taskHost);
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
		defaultSpec.setClassName(BloodOxygenModule.class.getCanonicalName());
		defaultSpec.setPackageName(CloudApplication.getInstance()
				.getPackageName());
		defaultSpec.setModuleStatus(isDisplay ? ModuleStatus.DISPLAY
				: ModuleStatus.HIDDEN);
		defaultSpec.setSetting("1");

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

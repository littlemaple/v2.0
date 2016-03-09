package com.medzone.cloud.controller;

import java.util.HashMap;
import java.util.List;

import com.medzone.cloud.cache.EarTemperatureCache;
import com.medzone.cloud.data.PropertyCenter;
import com.medzone.cloud.task.BaseGetControllerDataTask;
import com.medzone.cloud.task.DelRecordTask;
import com.medzone.framework.data.bean.imp.Account;
import com.medzone.framework.data.bean.imp.BaseMeasureData;
import com.medzone.framework.data.bean.imp.BloodPressure;
import com.medzone.framework.data.bean.imp.EarTemperature;
import com.medzone.framework.data.bean.imp.Rule;
import com.medzone.framework.data.navigation.LongStepable;
import com.medzone.framework.task.TaskHost;

public class EarTemperatureController
		extends
		AbstractUsePagingTaskCacheController<EarTemperature, LongStepable, EarTemperatureCache> {

	/**
	 * 
	 */

	public EarTemperatureController() {
	}

	@Override
	protected EarTemperatureCache createCache() {
		return new EarTemperatureCache();
	}

	@Override
	protected LongStepable getStepable(EarTemperature item) {
		return new LongStepable(item.getMeasureTime());
	}

	@Override
	protected BaseGetControllerDataTask<EarTemperature> createGetDataTask(
			Object... params) {

		// 因为使用同步的方式进行同步服务端的数据，所以该方法暂时被废弃，除非提供服务端分页API
		return null;
	}

	@Override
	public boolean isVaild() {
		if (getAttachInfo().mAccount == null) {
			return false;
		}
		return getAttachInfo().mAccount.getAccountID() != Account.INVALID_ID;
	}

	@Override
	protected void onAttach() {
		firstReadLocalData();
	}

	public int getMonthlyAllCounts(Integer year, Integer month) {
		return getCache().readMonthMeasureCounts(year, month);
	}

	public int getMonthlyExceptionCounts(Integer year, Integer month) {
		return getCache().readMonthMeasureExceptionCounts(year, month);
	}

	public List<HashMap<String, String>> getEarTemperatureRecord(Integer year) {
		return getCache().readStatListByYear(year);
	}

	public List<EarTemperature> getMonthlyAllData(Integer year, Integer month) {
		return getCache().readMonthlyAllData(year, month);
	}

	public List<EarTemperature> getMonthlyLimitData(Integer year, Integer month) {
		return getCache().readMonthlyLimitData(year, month);
	}

	public List<HashMap<String, String>> getYearMonthAbnormal(Integer year,
			Integer month) {
		return getCache().readStatListByMonth(year, month);
	}

	public EarTemperature getYearMonthMaxTemperature(Integer year, Integer month) {
		return getCache().readYearMonthMaxTemperature(year, month);
	}

	public EarTemperature getMeasureEarTemperature(String measureUid) {
		return getCache().read(measureUid);
	}

	public void deleteCloudEarTemperature(int recordID, Account account,
			TaskHost taskHost) {
		DelRecordTask task = new DelRecordTask(null, account.getAccessToken(),
				EarTemperature.EARTEMPERATURE_TAG, recordID);
		task.setTaskHost(taskHost);
		task.execute();
	}

	public void deleteLocalEarTemperature(EarTemperature item) {
		getCache().delete(item);
		cacheChanged();
		PropertyCenter.getInstance().firePropertyChange(
				PropertyCenter.PROPERTY_REFRESH_MY_MODULES, null, null);
	}

	public String getFristMeasureTime(Account account) {
		return getCache().readFristMeasureTime(account);
	}

	@Override
	protected EarTemperature synchroUploadItemInBackground(EarTemperature t) {
		super.synchroUploadItemInBackground(t);

		EarTemperature local = t;

		String measureUID = t.getMeasureUID();
		Integer record = t.getRecordID();

		if (t.getRecordID() != null) {

			local = getCache().read(measureUID);
			local.setRecordID(record.intValue());
			local.setStateFlag(BloodPressure.FLAG_SYNCHRONIZED);
			local.invalidate();

			// 如果本地的数据没有规则信息，则会读取本地构建规则库信息
			// 这里的处理是为了APP本地存在脏数据，新数据是不会产品该问题
			Integer state = local.getAbnormal();
			if (state == null || state == 0) {
				Rule rule = RuleController.getInstance().getRulebyData(local);
				local.setAbnormal(rule.getState());
				local.setStateFlag(BaseMeasureData.FLAG_NOT_SYNCHRONIZED);
				local.invalidate();
			}
		}
		return local;
	}

}

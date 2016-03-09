package com.medzone.cloud.controller;

import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.medzone.cloud.cache.BloodPressureCache;
import com.medzone.cloud.data.CurrentAccountManager;
import com.medzone.cloud.data.PropertyCenter;
import com.medzone.cloud.task.BaseGetControllerDataTask;
import com.medzone.cloud.task.DelRecordTask;
import com.medzone.cloud.task.DoRuleMatchTask;
import com.medzone.cloud.task.GetRecordTask;
import com.medzone.cloud.task.RecordUploadTask;
import com.medzone.framework.data.bean.imp.Account;
import com.medzone.framework.data.bean.imp.BaseMeasureData;
import com.medzone.framework.data.bean.imp.BloodPressure;
import com.medzone.framework.data.bean.imp.GroupMember;
import com.medzone.framework.data.bean.imp.Rule;
import com.medzone.framework.data.navigation.LongStepable;
import com.medzone.framework.task.TaskHost;
import com.medzone.framework.util.TimeUtils;

public class BloodPressureController
		extends
		AbstractUsePagingTaskCacheController<BloodPressure, LongStepable, BloodPressureCache> {

	/**
	 * 
	 */

	public BloodPressureController() {
	}

	@Override
	protected BloodPressureCache createCache() {
		return new BloodPressureCache();
	}

	@Override
	protected LongStepable getStepable(BloodPressure item) {
		return new LongStepable(item.getMeasureTime());
	}

	@Override
	@Deprecated
	protected BaseGetControllerDataTask<BloodPressure> createGetDataTask(
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

	/**
	 * 获取指定年份、月份下的测量次数
	 * 
	 * @param year
	 * @param month
	 * @return
	 */
	public int getMonthlyAllCounts(Integer year, Integer month) {
		return getCache().readMonthMeasureCounts(year, month);
	}

	/**
	 * 获取指定年份、月份下的异常次数
	 * 
	 * @param year
	 * @param month
	 * @return
	 */
	public int getMonthlyExceptionCounts(Integer year, Integer month) {
		return getCache().readMonthMeasureExceptionCounts(year, month);
	}

	/**
	 * 获取指定年份下按月统计测量次数与异常次数
	 * 
	 * @param year
	 * @return
	 */
	public List<HashMap<String, String>> getBloodPressureRecord(Integer year) {
		return getCache().readStatListByYear(year);
	}

	/**
	 * 获取指定年份、月份下每一天中最后一条测量数据
	 * 
	 * @param year
	 * @param month
	 * @return
	 */
	public List<BloodPressure> getMonthlyLimitData(Integer year, Integer month) {
		return getCache().readMonthlyLimitData(year, month);
	}

	/**
	 * 获取指定年份、月份下按等级统计测量数据
	 * 
	 * @param year
	 * @param month
	 * @return
	 */
	public List<HashMap<String, String>> getYearMonthAbnormal(Integer year,
			Integer month) {
		return getCache().readStatListByMonth(year, month);
	}

	/**
	 * 获取指定年份、月份下血压值最高的测量数据
	 * 
	 * @param year
	 * @param month
	 * @return
	 */
	public BloodPressure getYearMonthMaxPressure(Integer year, Integer month) {
		return getCache().readYearMonthMaxPressure(year, month);
	}

	/**
	 * 获取指定年份、月份下所有的测量数据集合
	 * 
	 * @param year
	 * @param month
	 * @return
	 */
	public List<BloodPressure> getMonthlyAllData(Integer year, Integer month) {
		return getCache().readMonthlyAllData(year, month);
	}

	/**
	 * 获取指定measureUid的测量数据
	 * 
	 * @param measureUid
	 * @return
	 */
	public BloodPressure getMeasureBloodPressure(String measureUid) {
		return getCache().read(measureUid);
	}

	/**
	 * 删除云端指定recordID、账户下的测量数据
	 * 
	 * @param recordID
	 * @param account
	 * @param taskHost
	 */
	public void deleteCloudBloodPressure(int recordID, Account account,
			TaskHost taskHost) {

		DelRecordTask task = new DelRecordTask(null, account.getAccessToken(),
				BloodPressure.BLOODPRESSURE_TAG, recordID);
		task.setTaskHost(taskHost);
		task.execute();
	}

	/**
	 * 删除本地指定measureUID的测量数据
	 * 
	 * @param measureUID
	 */
	public void deleteLocalBloodPressure(BloodPressure item) {
		getCache().delete(item);
		cacheChanged();
		PropertyCenter.getInstance().firePropertyChange(
				PropertyCenter.PROPERTY_REFRESH_MY_MODULES, null, null);
	}

	/**
	 * 获取第一条测量数据的测量时间
	 * 
	 * @return
	 */
	public Long getFristMeasureTime() {
		return getCache().readFristMeasureTime();
	}

	/**
	 * 同步上传替他人测量的数据
	 * 
	 * @param groupmember
	 *            被代测的人
	 * @param bloodpressure
	 *            测量数据
	 * @param taskHost
	 */
	public void synchroRecordUpload(GroupMember groupmember,
			BloodPressure bloodpressure, TaskHost taskHost) {
		JSONArray jsons = new JSONArray();
		JSONObject json = new JSONObject();
		try {
			json.put("measureuid", bloodpressure.getMeasureUID());
			json.put("readme", bloodpressure.getReadme());
			json.put("source", null);
			// json.put("x", 0);
			// json.put("y", 0);
			json.put("state", bloodpressure.getAbnormal());
			json.put("value1", bloodpressure.getHigh());
			json.put("value2", bloodpressure.getLow());
			json.put("value3", bloodpressure.getRate());
			jsons.put(json);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		RecordUploadTask task = new RecordUploadTask(null,
				CurrentAccountManager.getCurAccount().getAccessToken(),
				BloodPressure.BLOODPRESSURE_TAG, jsons.toString(),
				groupmember.getAccountID(), null, null);
		task.setTaskHost(taskHost);
		task.execute();
	}

	/**
	 * 获取指定账户、测量数据下近三条测量数据
	 * 
	 * @param account
	 * @param bloodpressure
	 * @param taskHost
	 */
	public void getNearlyRecord(Account account, BloodPressure bloodpressure,
			TaskHost taskHost) {

		GetRecordTask task = null;
		task = new GetRecordTask(null, account.getAccessToken(),
				BloodPressure.BLOODPRESSURE_TAG, "-"
						+ TimeUtils.getYYYYMMDDHHMMSS(bloodpressure
								.getMeasureTime()), null, null, 3, null,
				"desc ", null);
		task.setTaskHost(taskHost);
		task.execute();
	}

	/**
	 * 获取指定测量数据的医生建议
	 * 
	 * @param bloodpressure
	 * @param taskHost
	 */
	public void getDoctorAdvised(BloodPressure bloodpressure, TaskHost taskHost) {
		DoRuleMatchTask task = new DoRuleMatchTask(null, CurrentAccountManager
				.getCurAccount().getAccountID(),
				BloodPressure.BLOODPRESSURE_ID, Float.parseFloat(String
						.valueOf(bloodpressure.getHigh())),
				Float.parseFloat(String.valueOf(bloodpressure.getLow())),
				Float.parseFloat(String.valueOf(bloodpressure.getRate())));
		task.setTaskHost(taskHost);
		task.execute();
	}

	@Override
	protected BloodPressure synchroUploadItemInBackground(BloodPressure t) {
		super.synchroUploadItemInBackground(t);

		BloodPressure local = t;

		String measureUID = t.getMeasureUID();
		Integer record = t.getRecordID();

		if (record != null) {
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

package com.medzone.cloud.controller;

import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.medzone.cloud.cache.BloodOxgenCache;
import com.medzone.cloud.data.CurrentAccountManager;
import com.medzone.cloud.data.PropertyCenter;
import com.medzone.cloud.task.BaseGetControllerDataTask;
import com.medzone.cloud.task.DelRecordTask;
import com.medzone.cloud.task.DoRuleMatchTask;
import com.medzone.cloud.task.GetRecordTask;
import com.medzone.cloud.task.RecordUploadTask;
import com.medzone.framework.data.bean.imp.Account;
import com.medzone.framework.data.bean.imp.BaseMeasureData;
import com.medzone.framework.data.bean.imp.BloodOxygen;
import com.medzone.framework.data.bean.imp.BloodPressure;
import com.medzone.framework.data.bean.imp.GroupMember;
import com.medzone.framework.data.bean.imp.Rule;
import com.medzone.framework.data.navigation.LongStepable;
import com.medzone.framework.task.TaskHost;
import com.medzone.framework.util.TimeUtils;

public class BloodOxygenController
		extends
		AbstractUsePagingTaskCacheController<BloodOxygen, LongStepable, BloodOxgenCache> {

	/**
	 * 
	 */

	public BloodOxygenController() {
	}

	@Override
	protected BloodOxgenCache createCache() {
		return new BloodOxgenCache();
	}

	@Override
	protected LongStepable getStepable(BloodOxygen item) {
		return new LongStepable(item.getMeasureTime());
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

	@Override
	@Deprecated
	protected BaseGetControllerDataTask<BloodOxygen> createGetDataTask(
			Object... params) {

		// 因为使用同步的方式进行同步服务端的数据，所以该方法暂时被废弃，除非提供服务端分页API

		return null;
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
	 * 获取指定年份下按月份统计测量次数与异常次数
	 * 
	 * @param year
	 * @return
	 */
	public List<HashMap<String, String>> getBloodOxygenRecord(Integer year) {
		return getCache().readStatListByYear(year);
	}

	/**
	 * 获取指定年份、月份下的所有测量数据集合
	 * 
	 * @param year
	 * @param month
	 * @return
	 */
	public List<BloodOxygen> getMonthlyAllData(Integer year, Integer month) {
		return getCache().readMonthlyAllData(year, month);
	}

	/**
	 * 获取指定年份、月份下每天最后一条测量数据集合
	 * 
	 * @param year
	 * @param month
	 * @return
	 */
	public List<BloodOxygen> getMonthlyLimitData(Integer year, Integer month) {
		return getCache().readMonthlyLimitData(year, month);
	}

	/**
	 * 获取指定年份、月份下按等级划分进行统计
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
	 * 获取指定年份、月份下血氧值最小的测量数据
	 * 
	 * @param year
	 * @param month
	 * @return
	 */
	public BloodOxygen getYearMonthMinOxygen(Integer year, Integer month) {
		return getCache().readYearMonthMinOxygen(year, month);
	}

	/**
	 * 获取指定measureUid的测量数据
	 * 
	 * @param measureUid
	 * @return
	 */
	public BloodOxygen getMeasureBloodOxygen(String measureUid) {
		return getCache().read(measureUid);
	}

	/**
	 * 删除云端指定账户、recordID的测量数据
	 * 
	 * @param recordID
	 * @param account
	 * @param taskHost
	 */
	public void deleteCloudBloodOxygen(int recordID, Account account,
			TaskHost taskHost) {

		DelRecordTask task = new DelRecordTask(null, account.getAccessToken(),
				BloodOxygen.BLOODOXYGEN_TAG, recordID);
		task.setTaskHost(taskHost);
		task.execute();
	}

	/**
	 * 删除本地指定measureUID的测量数据
	 * 
	 * @param measureUID
	 */
	public void deleteLocalBloodOxygen(BloodOxygen item) {
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
	 * 获取指定账户、测量数据下近三条测量数据
	 * 
	 * @param account
	 * @param bloodoxygen
	 * @param taskHost
	 */
	public void getNearlyRecord(Account account, BloodOxygen bloodoxygen,
			TaskHost taskHost) {
		GetRecordTask task = null;
		task = new GetRecordTask(null, account.getAccessToken(),
				BloodOxygen.BLOODOXYGEN_TAG, "-"
						+ TimeUtils.getYYYYMMDDHHMMSS(bloodoxygen
								.getMeasureTime()), null, null, 3, null,
				"desc ", null);
		task.setTaskHost(taskHost);
		task.execute();
	}

	/**
	 * 获取指定测量数据的医生建议
	 * 
	 * @param bloodoxygen
	 * @param taskHost
	 */
	public void getDoctorAdvised(BloodOxygen bloodoxygen, TaskHost taskHost) {
		DoRuleMatchTask task = new DoRuleMatchTask(null, CurrentAccountManager
				.getCurAccount().getAccountID(), BloodOxygen.BLOODOXYGEN_ID,
				Float.parseFloat(String.valueOf(bloodoxygen.getOxygen())),
				Float.parseFloat(String.valueOf(bloodoxygen.getRate())), null);
		task.setTaskHost(taskHost);
		task.execute();
	}

	/**
	 * 同步上传代测的测量数据
	 * 
	 * @param groupmember
	 *            被代测的人
	 * @param bloodoxygen
	 *            测量数据
	 * @param taskHost
	 */
	public void synchroRecordUpload(GroupMember groupmember,
			BloodOxygen bloodoxygen, TaskHost taskHost) {
		JSONArray jsons = new JSONArray();
		JSONObject json = new JSONObject();
		try {
			json.put("measureuid", bloodoxygen.getMeasureUID());
			json.put("readme", bloodoxygen.getReadme());
			json.put("source", null);
			// json.put("x", 0);
			// json.put("y", 0);
			json.put("state", bloodoxygen.getAbnormal());
			json.put("value1", bloodoxygen.getOxygen());
			json.put("value2", bloodoxygen.getRate());
			jsons.put(json);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		RecordUploadTask task = new RecordUploadTask(null,
				CurrentAccountManager.getCurAccount().getAccessToken(),
				BloodOxygen.BLOODOXYGEN_TAG, jsons.toString(),
				groupmember.getAccountID(), null, null);
		task.setTaskHost(taskHost);
		task.execute();
	}

	@Override
	protected BloodOxygen synchroUploadItemInBackground(BloodOxygen t) {
		super.synchroUploadItemInBackground(t);

		BloodOxygen local = t;
		// 获取api返回值的参数
		String measureUID = t.getMeasureUID();
		Integer record = t.getRecordID();
		// 读取本地的完整字段结构，并更新recordid
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

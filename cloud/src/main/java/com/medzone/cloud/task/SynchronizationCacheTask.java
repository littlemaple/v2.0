/**
 * 
 */
package com.medzone.cloud.task;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.text.TextUtils;
import android.util.SparseArray;
import android.util.SparseIntArray;

import com.medzone.cloud.controller.AbstractUseTaskCacheController;
import com.medzone.cloud.data.CurrentAccountManager;
import com.medzone.cloud.data.PropertyCenter;
import com.medzone.cloud.module.modules.BloodOxygenModule;
import com.medzone.cloud.module.modules.BloodPressureModule;
import com.medzone.cloud.module.modules.EarTemperatureModule;
import com.medzone.cloud.network.NetworkClient;
import com.medzone.framework.Log;
import com.medzone.framework.data.bean.BaseIdDatabaseObject;
import com.medzone.framework.data.bean.imp.BloodOxygen;
import com.medzone.framework.data.bean.imp.BloodPressure;
import com.medzone.framework.data.bean.imp.EarTemperature;
import com.medzone.framework.network.NetworkClientResult;
import com.medzone.framework.task.BaseResult;

/**
 * @author ChenJunQi.
 * 
 * 
 *         上传,同步测量数据
 * 
 */
public class SynchronizationCacheTask<T extends BaseIdDatabaseObject, C extends AbstractUseTaskCacheController<T, ?>>
		extends BaseGetControllerDataTask<T> {

	// for sychro
	protected SparseIntArray idsArray;
	protected SparseArray<T> newItemArray;
	protected Integer getTaskType;
	protected C controller;
	// for upload
	protected String accessToken;
	protected String moduleId;
	protected String uploadData;
	protected Integer upSyncId;
	// for download
	private String timeRange;
	private Integer endID;
	private Integer downOffset;
	protected Integer downSerial;
	protected Integer downLimit;
	private String sort;
	private Integer downSyncID;

	private boolean isSynchroType;
	protected boolean isDownLoadValues;
	protected boolean isUploadLoadValues;

	private String measureType;

	/**
	 * 同步测量数据,用于登陆帐号的个人数据获取,以及给待测人员添加数据
	 * 
	 * @param context
	 * @param accessToken
	 * @param controller
	 * @param moduleId
	 * @param uploadData
	 * @param upSyncId
	 * @param downSerial
	 * @param downLimit
	 * @param idsArray
	 * @param getTaskType
	 */
	public SynchronizationCacheTask(Context context, String accessToken,
			C controller, String moduleId, String uploadData, Integer upSyncId,
			Integer downSerial, Integer downLimit, SparseIntArray idsArray,
			Integer getTaskType) {
		super(context);
		this.controller = controller;
		this.accessToken = accessToken;
		this.moduleId = moduleId;
		this.uploadData = uploadData;
		this.upSyncId = upSyncId;
		this.downSerial = downSerial;
		this.downLimit = downLimit;
		this.idsArray = idsArray;
		this.getTaskType = getTaskType;
		if (downSerial.intValue() == 0)
			downLimit = null;
		isSynchroType = true;
	}

	/**
	 * @param access_token
	 *            true string 在登录授权后得到，参见如何登入。
	 * @param type
	 *            true string 要查看的测量类型，bp = 血压，et = 耳温，oxy = 血氧。
	 * @param time_range
	 *            false string
	 *            要查看的测量时间范围，格式：YYYYMMDDHHIISS-YYYYMMDDHHIISS，起止时间可以省略其中一个
	 *            ，包含起点但不包含终点。
	 * @param end_id
	 *            false int 限定只返回这个主键 ID 之前数据。
	 * @param down_serial
	 *            false int 下载序列号，限定只返回此序列号之后的新数据。（序列号在本次下载后一并提供，须 APP
	 *            端自行记录，首次可用 0 代替）
	 * @param limit
	 *            false int 最大获取条数，默认为 100（若提供 down_serial 参数，当 down_serial 为 0
	 *            时默认 1000 否则默认为 0 提供全部数据）。
	 * @param offset
	 *            false int 要跳过的数据条数据，默认为 0。
	 * @param sort
	 *            false string 排序方式，使用 asc 或 desc 分别表示旧的优先或新的优先，默认为 desc。
	 * @param down_syncid
	 *            false int 根据 ID 下载别人的数据，必须要有对方授权。
	 */

	public SynchronizationCacheTask(Context context, String accessToken,
			C controller, String moduleId, String timeRange, Integer endID,
			Integer downSerial, Integer limit, Integer offset, String sort,
			Integer downSyncID, Integer getTaskType) {
		super(context);
		this.accessToken = accessToken;
		this.controller = controller;
		this.moduleId = moduleId;
		this.timeRange = timeRange;
		this.endID = endID;
		this.downSerial = downSerial;
		this.downLimit = limit;
		this.downOffset = offset;
		this.sort = sort;
		this.downSyncID = downSyncID;
		this.getTaskType = getTaskType;
		isSynchroType = false;
	}

	@Override
	protected synchronized BaseResult readDataInBackground(Void... params) {
		NetworkClientResult result;
		measureType = getModuleTag(moduleId);
		if (isSynchroType) {
			result = (NetworkClientResult) NetworkClient.getInstance()
					.uploadRecord(accessToken, measureType, uploadData,
							upSyncId, Integer.valueOf(downSerial),
							Integer.valueOf(downLimit));
		} else {
			result = (NetworkClientResult) NetworkClient
					.getInstance()
					.getRecord(accessToken, measureType, timeRange, endID,
							downSerial, downLimit, downOffset, sort, downSyncID);
		}

		if (result.isSuccess() && result.isServerDisposeSuccess()) {

			JSONObject jo = result.getResponseResult();
			int downSerial = 0;
			try {
				if (jo.has("down_serial")) {
					downSerial = jo.getInt("down_serial");
					if (jo.has("down") && !jo.isNull("down")) {
						JSONArray downData = jo.getJSONArray("down");
						List<T> ret = getRetList(downData, moduleId);
						isDownLoadValues = false;
						if (ret.size() > 0) {
							isDownLoadValues = true;
						}
						newItemArray = controller.synchrInBackground(ret,
								downSerial);
					}
					if (jo.has("up") && !jo.isNull("up")) {

						JSONArray upData = jo.getJSONArray("up");
						List<T> ret = getRetList(upData, moduleId);
						isUploadLoadValues = false;
						if (ret.size() > 0) {
							isUploadLoadValues = true;
						}
						newItemArray = controller.synchroUploadInBackground(
								ret, downSerial);
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	@Override
	protected void onPostExecute(BaseResult result) {
		super.onPostExecute(result);
		if (result.isSuccess() && result.isServerDisposeSuccess()) {

			controller.synchrCallInPostExcute(newItemArray, idsArray,
					getTaskType);

			// 一般情况下，下载新数据时才会对View进行更新
			if (isDownLoadValues || isUploadLoadValues) {
				Log.v("下载到新数据，通知刷新#PROPERTY_REFRESH_HISTORY_DATA");
				PropertyCenter.getInstance().firePropertyChange(
						PropertyCenter.PROPERTY_REFRESH_HISTORY_DATA, null,
						measureType);
			}
		}
	}

	/**
	 * convert moduleID to moduleTAG,to prepare serverAPI params.
	 */
	private String getModuleTag(String moduleId) {
		String tag = null;
		if (TextUtils.equals(moduleId, BloodPressureModule.ID)) {
			tag = BloodPressure.BLOODPRESSURE_TAG;
		} else if (TextUtils.equals(moduleId, BloodOxygenModule.ID)) {
			tag = BloodOxygen.BLOODOXYGEN_TAG;
		} else if (TextUtils.equals(moduleId, EarTemperatureModule.ID)) {
			tag = EarTemperature.EARTEMPERATURE_TAG;
		}
		if (tag == null) {
			Log.e("getModuleTag()>>> get tag is empty.");
		}
		return tag;
	}

	@SuppressWarnings("unchecked")
	private List<T> getRetList(JSONArray jsonArray, String moduleId)
			throws JSONException {
		List<T> retList = null;
		Integer accountID = null;
		if (CurrentAccountManager.getCurAccount() != null) {
			accountID = CurrentAccountManager.getCurAccount().getAccountID();
		}
		if (TextUtils.equals(moduleId, BloodPressureModule.ID)) {
			retList = (List<T>) BloodPressure.createBloodPressureList(
					jsonArray, accountID);
		} else if (TextUtils.equals(moduleId, BloodOxygenModule.ID)) {
			retList = (List<T>) BloodOxygen.createBloodOxygenList(jsonArray,
					accountID);
		} else if (TextUtils.equals(moduleId, EarTemperatureModule.ID)) {
			retList = (List<T>) EarTemperature.createEarTemperatureList(
					jsonArray, accountID);
		}
		return retList;
	}
}

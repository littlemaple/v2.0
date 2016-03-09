//package com.medzone.cloud.task;
//
//import java.util.List;
//
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import com.medzone.cloud.controller.EarTemperatureController;
//import com.medzone.cloud.data.CurrentAccountManager;
//import com.medzone.cloud.network.NetworkClient;
//import com.medzone.framework.data.bean.imp.EarTemperature;
//import com.medzone.framework.data.navigation.LongStepable;
//import com.medzone.framework.data.navigation.Paging;
//import com.medzone.framework.network.NetworkClientResult;
//import com.medzone.framework.task.BaseResult;
//
//public class GetTemperatureDataTask extends
//		BaseGetControllerDataTask<EarTemperature> {
//
//	protected Paging<LongStepable> paging;
//	protected String uploadData;
//	protected String accessToken;
//	protected Integer eventType; // down event or up event
//	protected String deviceType = "et";
//	protected Integer downSerial = 0;
//	protected Integer downLimit = 0;
//	protected EarTemperatureController controller;
//	protected Integer otherSyncid;
//
//	public GetTemperatureDataTask(String accessToken, String uploadData,
//			Paging<LongStepable> paging, Integer eventType, Integer downSerial,
//			Integer otherSyncid, EarTemperatureController controller) {
//		super(0);
//		this.accessToken = accessToken;
//		this.uploadData = uploadData;
//		this.paging = paging;
//		this.eventType = eventType;
//		this.downSerial = downSerial;
//		this.downLimit = paging.getPageSize();
//		this.otherSyncid = otherSyncid;
//		this.controller = controller;
//	}
//
//	@Override
//	protected BaseResult readDataInBackground(Void... params) {
//
//		NetworkClientResult result = (NetworkClientResult) NetworkClient
//				.getInstance().getRecord(accessToken, deviceType, null, null,
//						downSerial, paging.getPageSize(), null, null,
//						otherSyncid);
//
//		if (result.isSuccess() && result.isServerDisposeSuccess()) {
//			JSONObject jo = result.getResponseResult();
//			int downSerial = 0;
//			try {
//				if (jo.has("down_serial")) {
//					downSerial = jo.getInt("down_serial");
//				}
//				JSONArray downData = jo.getJSONArray("down");
//				Integer accountID = CurrentAccountManager.getCurAccount()
//						.getAccountID();
//				List<EarTemperature> retList = EarTemperature
//						.createEarTemperatureList(downData, accountID);
//				controller.synchrInBackground(retList, downSerial);
//			} catch (JSONException e) {
//				e.printStackTrace();
//			}
//
//		}
//
//		return result;
//	}
//
//}

package com.medzone.cloud.ui.dialog.share;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.medzone.cloud.data.CurrentAccountManager;
import com.medzone.cloud.data.TemporaryData;
import com.medzone.cloud.data.helper.GroupHelper;
import com.medzone.cloud.data.helper.MeasureDataUtil;
import com.medzone.cloud.module.CloudMeasureModuleCentreRoot;
import com.medzone.cloud.module.modules.BloodPressureModule;
import com.medzone.framework.data.bean.imp.BloodPressure;
import com.medzone.framework.data.bean.imp.Message;
import com.medzone.framework.task.TaskHost;
import com.medzone.framework.util.TimeUtils;
import com.medzone.mcloud.R.id;
import com.medzone.mcloud.R.layout;

public class BloodPressureSinglePage extends CloudShareDialogPage {

	private BloodPressure mBloodPressure;
	private String errorAlert;
	private char unit = '0';// default unit is mmhg.
	private Context context;

	public BloodPressureSinglePage(Context context) {
		super(context);
		this.context = context;
	}

	@Override
	public View getView() {

		View view;
		if (mBloodPressure != null) {
			LayoutInflater inflater = LayoutInflater.from(mContext);
			view = inflater.inflate(layout.dialog_share_item_single, null);
			ImageView ivResultIcon = (ImageView) view
					.findViewById(id.iv_result_icon);
			TextView tvTitle = (TextView) view.findViewById(id.tv_title);
			TextView tvUnit1 = (TextView) view.findViewById(id.tv_unit_1);
			TextView tvValue1 = (TextView) view.findViewById(id.tv_value_1);
			TextView tvUnitEn1 = (TextView) view.findViewById(id.tv_unit_en_1);
			TextView tvUnit2 = (TextView) view.findViewById(id.tv_unit_2);
			TextView tvValue2 = (TextView) view.findViewById(id.tv_value_2);
			TextView tvUnitEn2 = (TextView) view.findViewById(id.tv_unit_en_2);
			TextView tvMeasureTime = (TextView) view
					.findViewById(id.tv_measure_time);

			tvUnit2.setText("心率");
			tvValue2.setText("" + mBloodPressure.getRate().intValue());
			tvUnitEn2.setText(BloodPressure.BLOODPRESSURE_UNIT_RATE);

			tvTitle.setText("血压");
			tvUnit1.setText("收缩压/舒张压");

			BloodPressureModule bpm = (BloodPressureModule) CloudMeasureModuleCentreRoot
					.applyModule(CurrentAccountManager.getCurAccount(),
							BloodPressureModule.class.getCanonicalName());

			if (bpm.isKpaMode()) {
				float highValue = mBloodPressure.getHighKPA();
				float lowValue = mBloodPressure.getLowKPA();
				tvValue1.setText(highValue + "/" + lowValue);
			} else {
				int high = mBloodPressure.getHigh().intValue();
				int low = mBloodPressure.getLow().intValue();
				tvValue1.setText(high + "/" + low);
			}
			tvUnitEn1.setText(mBloodPressure.getPressureUnit(bpm.isKpaMode()));

			// TODO 时间转成毫秒值？
			// String dateFormat = TimeUtils.getTime(mBloodPressure
			// .getMeasureTime().longValue(),
			// TimeUtils.YYYY_MM_DD_HH_MM_SS);
			String dateFormat = TimeUtils.getYearToSecond(mBloodPressure
					.getMeasureTime().longValue());

			tvMeasureTime.setText(dateFormat);
			MeasureDataUtil.BloodPressureFlag(ivResultIcon,
					mBloodPressure.getAbnormal());
		} else {
			TextView tv = new TextView(mContext);
			tv.setText(errorAlert);
			view = tv;
		}
		return view;
	}

	@Override
	public void prepareData() {
		if (TemporaryData.containsKey(BloodPressure.class.getName())) {
			mBloodPressure = (BloodPressure) TemporaryData
					.get(BloodPressure.class.getName());
		} else {
			errorAlert = "未获取到血压单次数据";
		}
	}

	@Override
	public void doGetShareURL(TaskHost taskHost) {

		String accessToken = CurrentAccountManager.getCurAccount()
				.getAccessToken();
		String type = BloodPressureModule.TYPE;

		// XXX 临时方案，解决分享中出现41904错误，待缓存这块重构
		String uid = mBloodPressure.getMeasureUID();

		BloodPressureModule bpModule = (BloodPressureModule) CloudMeasureModuleCentreRoot
				.applyModule(CurrentAccountManager.getCurAccount(),
						BloodPressureModule.class.getCanonicalName());

		BloodPressure tmp = bpModule.getCacheController()
				.getMeasureBloodPressure(uid);

		Integer recordID = tmp.getRecordID();

		GroupHelper.doShareUrlRecordTask(context, accessToken, type, recordID,
				null, null, taskHost);
	}

	@Override
	public void doSendShareMessage(TaskHost taskHost, int groupid,
			String shareURL) {

		JSONObject jo = new JSONObject();
		try {
			jo.put("type", BloodPressureModule.TYPE);
			jo.put("value1", mBloodPressure.getHigh().toString());
			jo.put("value2", mBloodPressure.getLow().toString());
			jo.put("value3", mBloodPressure.getRate().toString());
			jo.put("time", mBloodPressure.getMeasureTime().longValue());
			// jo.put("result", null);
			jo.put("state", mBloodPressure.getAbnormal());
			jo.put("url", shareURL);
			jo.put("report_type", TYPE_RECORD);
			jo.put("unit", unit == '1' ? 1 : 0);// 用于标注分享时内容的单位

		} catch (JSONException e) {
			e.printStackTrace();
		}
		GroupHelper.sendMessageTask(context,
				CurrentAccountManager.getCurAccount(), groupid, jo.toString(),
				Message.TYPE_RECORD, taskHost);
	}

}

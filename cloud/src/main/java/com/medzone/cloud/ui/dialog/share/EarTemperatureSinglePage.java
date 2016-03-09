package com.medzone.cloud.ui.dialog.share;

import java.util.Date;

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
import com.medzone.cloud.module.CloudMeasureModuleCentreRoot;
import com.medzone.cloud.module.modules.EarTemperatureModule;
import com.medzone.framework.data.bean.imp.EarTemperature;
import com.medzone.framework.data.bean.imp.Message;
import com.medzone.framework.task.TaskHost;
import com.medzone.framework.util.TimeUtils;
import com.medzone.mcloud.R.drawable;
import com.medzone.mcloud.R.id;
import com.medzone.mcloud.R.layout;
import com.medzone.mcloud.R.string;

public class EarTemperatureSinglePage extends CloudShareDialogPage {

	private EarTemperature mEarTemperature;
	private String errorAlert;
	private Context context;

	public EarTemperatureSinglePage(Context context) {
		super(context);
		this.context = context;
	}

	@Override
	public View getView() {

		View view;
		if (mEarTemperature != null) {
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

			tvTitle.setText("单次耳温监测数据");

			tvUnit1.setText("摄氏度");
			tvValue1.setText(String.valueOf(mEarTemperature.getTemperature()));
			tvUnitEn1.setText(string.ear_temperature_unit);

			tvUnit2.setVisibility(View.GONE);
			tvValue2.setVisibility(View.GONE);
			tvUnitEn2.setVisibility(View.GONE);

			Date mDate = TimeUtils.getDate(mEarTemperature.getMeasureTime()
					.longValue(), TimeUtils.YYYYMMDD_HHMMSS);
			String dateFormat = TimeUtils.getTime(mDate.getTime(),
					TimeUtils.YYYY_MM_DD_HH_MM_SS);

			tvMeasureTime.setText(dateFormat);

			switch (mEarTemperature.getAbnormal()) {
			case EarTemperature.TEMPERATURE_STATE_NORMAL:
				ivResultIcon
						.setImageResource(drawable.testresultsview_testresult_graph_normal);
				break;
			case EarTemperature.TEMPERATURE_STATE_FEVER:
				ivResultIcon
						.setImageResource(drawable.testresultsview_testresult_graph_fare);
				break;
			case EarTemperature.TEMPERATURE_STATE_LOW:
				ivResultIcon
						.setImageResource(drawable.testresultsview_testresult_graph_dire);
				break;
			case EarTemperature.TEMPERATURE_STATE_HIGH_FEVER:
				ivResultIcon
						.setImageResource(drawable.testresultsview_testresult_graph_gaore);
				break;
			}
		} else {
			TextView tv = new TextView(mContext);
			tv.setText(errorAlert);
			view = tv;
		}
		return view;
	}

	@Override
	public void prepareData() {
		if (TemporaryData.containsKey(EarTemperature.class.getName())) {
			mEarTemperature = (EarTemperature) TemporaryData
					.get(EarTemperature.class.getName());
		} else {
			errorAlert = "未获取到耳温单次数据";
		}
	}

	@Override
	public void doGetShareURL(TaskHost taskHost) {

		String accessToken = CurrentAccountManager.getCurAccount()
				.getAccessToken();
		String type = EarTemperatureModule.TYPE;

		// XXX 临时方案，解决分享中出现41904错误，待缓存这块重构
		String uid = mEarTemperature.getMeasureUID();

		EarTemperatureModule etModule = (EarTemperatureModule) CloudMeasureModuleCentreRoot
				.applyModule(CurrentAccountManager.getCurAccount(),
						EarTemperatureModule.class.getCanonicalName());

		EarTemperature tmp = etModule.getCacheController()
				.getMeasureEarTemperature(uid);

		Integer recordID = tmp.getRecordID();

		GroupHelper.doShareUrlRecordTask(context, accessToken, type, recordID,
				null, null, taskHost);
	}

	@Override
	public void doSendShareMessage(TaskHost taskHost, int groupid,
			String shareURL) {

		JSONObject jo = new JSONObject();
		try {
			jo.put("type", EarTemperatureModule.TYPE);
			jo.put("value1", mEarTemperature.getTemperature().toString());
			// jo.put("value2", null);
			jo.put("time", mEarTemperature.getMeasureTime().longValue());
			// jo.put("result", null);
			jo.put("state", mEarTemperature.getAbnormal());
			jo.put("url", shareURL);
			jo.put("report_type", TYPE_RECORD);

		} catch (JSONException e) {
			e.printStackTrace();
		}
		GroupHelper.sendMessageTask(context,
				CurrentAccountManager.getCurAccount(), groupid, jo.toString(),
				Message.TYPE_RECORD, taskHost);
	}

}

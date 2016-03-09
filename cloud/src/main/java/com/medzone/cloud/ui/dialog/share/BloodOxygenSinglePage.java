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
import com.medzone.cloud.module.CloudMeasureModuleCentreRoot;
import com.medzone.cloud.module.modules.BloodOxygenModule;
import com.medzone.framework.data.bean.imp.BloodOxygen;
import com.medzone.framework.data.bean.imp.Message;
import com.medzone.framework.task.TaskHost;
import com.medzone.framework.util.TimeUtils;
import com.medzone.mcloud.R;
import com.medzone.mcloud.R.id;
import com.medzone.mcloud.R.layout;

public class BloodOxygenSinglePage extends CloudShareDialogPage {

	private BloodOxygen mBloodOxygen;
	private String errorAlert;
	private Context context;

	public BloodOxygenSinglePage(Context context) {
		super(context);
		this.context = context;
	}

	@Override
	public View getView() {

		View view;
		if (mBloodOxygen != null) {
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

			tvTitle.setText("血氧");

			tvUnit1.setText("血氧饱和度");
			tvValue1.setText("" + mBloodOxygen.getOxygen().intValue());
			tvUnitEn1.setText("%");

			tvUnit2.setText("心率");
			tvValue2.setText("" + mBloodOxygen.getRate().intValue());
			tvUnitEn2.setText("bpm");

			// Date mDate = TimeUtils.getDate(mBloodOxygen.getMeasureTime()
			// .longValue(), TimeUtils.YYYYMMDD_HHMMSS);
			// String dateFormat = TimeUtils.getTime(mDate.getTime(),
			// TimeUtils.YYYY_MM_DD_HH_MM_SS);
			String dateFormat = TimeUtils.getYearToSecond(mBloodOxygen
					.getMeasureTime().longValue());

			tvMeasureTime.setText(dateFormat);

			switch (mBloodOxygen.getAbnormal()) {
			case BloodOxygen.OXYGEN_STATE_LOW:
				ivResultIcon
						.setImageResource(R.drawable.group_chat_testresult_hypoxemia);
				break;
			case BloodOxygen.OXYGEN_STATE_IDEAL:
				ivResultIcon
						.setImageResource(R.drawable.group_chat_testresult_normal);
				break;
			case BloodOxygen.OXYGEN_STATE_MISSING:
				ivResultIcon
						.setImageResource(R.drawable.group_chat_testresult_lossofoxygensaturation);
				break;
			default:
				ivResultIcon
						.setImageResource(R.drawable.group_chat_testresult_normal);
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
		if (TemporaryData.containsKey(BloodOxygen.class.getName())) {
			mBloodOxygen = (BloodOxygen) TemporaryData.get(BloodOxygen.class
					.getName());
		} else {
			errorAlert = "未获取到血氧单次数据";
		}
	}

	@Override
	public void doGetShareURL(TaskHost taskHost) {

		String accessToken = CurrentAccountManager.getCurAccount()
				.getAccessToken();
		String type = BloodOxygenModule.TYPE;

		// XXX 临时方案，解决分享中出现41904错误，待缓存这块重构
		String uid = mBloodOxygen.getMeasureUID();

		BloodOxygenModule oxyModule = (BloodOxygenModule) CloudMeasureModuleCentreRoot
				.applyModule(CurrentAccountManager.getCurAccount(),
						BloodOxygenModule.class.getCanonicalName());

		BloodOxygen tmpOxygen = oxyModule.getCacheController()
				.getMeasureBloodOxygen(uid);

		Integer recordID = tmpOxygen.getRecordID();
		GroupHelper.doShareUrlRecordTask(context, accessToken, type, recordID,
				null, null, taskHost);
	}

	@Override
	public void doSendShareMessage(TaskHost taskHost, int groupid,
			String shareURL) {

		JSONObject jo = new JSONObject();
		try {
			jo.put("type", BloodOxygenModule.TYPE);
			jo.put("value1", mBloodOxygen.getOxygen().toString());
			jo.put("value2", mBloodOxygen.getRate().toString());
			jo.put("time", mBloodOxygen.getMeasureTime().longValue());
			// jo.put("result", null);
			jo.put("state", mBloodOxygen.getAbnormal());
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

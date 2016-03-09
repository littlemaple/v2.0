package com.medzone.cloud.ui.dialog.share;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.medzone.cloud.data.CurrentAccountManager;
import com.medzone.cloud.data.TemporaryData;
import com.medzone.cloud.data.helper.GroupHelper;
import com.medzone.cloud.module.modules.BloodPressureModule;
import com.medzone.framework.Log;
import com.medzone.framework.data.bean.imp.Message;
import com.medzone.framework.data.bean.imp.ReportEntity;
import com.medzone.framework.task.TaskHost;
import com.medzone.framework.util.TimeUtils;
import com.medzone.mcloud.R.id;
import com.medzone.mcloud.R.layout;

public class BloodPressureRecentlyPage extends CloudShareDialogPage {

	private ReportEntity mReportEntity;
	private String errorAlert;
	private long seconds;

	private TextView tvTitle;
	private TextView tvUnit1;
	private TextView tvValue1;
	private TextView tvMeasureTime;
	private Context context;

	public BloodPressureRecentlyPage(Context context) {
		super(context);
		this.context = context;
	}

	@Override
	public View getView() {

		View view;
		if (mReportEntity != null) {
			LayoutInflater inflater = LayoutInflater.from(mContext);
			view = inflater.inflate(layout.dialog_share_item_recently, null);
			tvTitle = (TextView) view.findViewById(id.tv_title);
			tvUnit1 = (TextView) view.findViewById(id.tv_unit_1);
			tvValue1 = (TextView) view.findViewById(id.tv_value_1);
			tvMeasureTime = (TextView) view.findViewById(id.tv_measure_time);

			tvTitle.setText("血压趋势");
			tvUnit1.setText("趋势统计：");
			tvValue1.setText("近" + mReportEntity.totalCounts
					+ "次检测结果中，血压异常情况出现" + mReportEntity.abnormalCounts + "次");

			seconds = mReportEntity.startDate;
			String timeFormat = TimeUtils.getTime(seconds * 1000,
					TimeUtils.YY_MM_DD);
			Log.v("share#bo#"+timeFormat);
			tvMeasureTime.setText("" + timeFormat);

		} else {
			TextView tv = new TextView(mContext);
			tv.setText(errorAlert);
			view = tv;
		}
		return view;
	}

	@Override
	public void prepareData() {
		if (TemporaryData.containsKey(ReportEntity.class.getName())) {
			mReportEntity = (ReportEntity) TemporaryData.get(ReportEntity.class
					.getName());
		} else {
			errorAlert = "未获取到血压近期数据";
		}
	}

	@Override
	public void doGetShareURL(TaskHost taskHost) {

		String accessToken = CurrentAccountManager.getCurAccount()
				.getAccessToken();
		GroupHelper.doShareUrlRecordTask(context, accessToken,
				BloodPressureModule.TYPE, null, mReportEntity.measureUID, null,
				taskHost);
	}

	@Override
	public void doSendShareMessage(TaskHost taskHost, int groupid,
			String shareURL) {

		JSONObject jo = new JSONObject();
		try {
			jo.put("title", tvTitle.getText().toString());
			jo.put("url", shareURL);
			jo.put("time", seconds);
			jo.put("description", tvValue1.getText().toString());
			jo.put("report_type", TYPE_RECENT);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		GroupHelper.sendMessageTask(context,
				CurrentAccountManager.getCurAccount(), groupid, jo.toString(),
				Message.TYPE_LINK, taskHost);
	}
}

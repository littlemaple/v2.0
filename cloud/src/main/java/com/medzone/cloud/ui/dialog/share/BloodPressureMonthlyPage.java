package com.medzone.cloud.ui.dialog.share;

import java.util.Date;

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
import com.medzone.framework.data.bean.imp.Message;
import com.medzone.framework.data.bean.imp.ReportEntity;
import com.medzone.framework.task.TaskHost;
import com.medzone.framework.util.TimeUtils;
import com.medzone.mcloud.R.id;
import com.medzone.mcloud.R.layout;

public class BloodPressureMonthlyPage extends CloudShareDialogPage {

	private ReportEntity mReportEntity;
	private String errorAlert;

	private TextView tvTitle;
	private TextView tvUnit1;
	private TextView tvValue1;
	private TextView tvMeasureTime;

	private long seconds;
	private Context context;

	public BloodPressureMonthlyPage(Context context) {
		super(context);
		this.context = context;
	}

	@Override
	public View getView() {

		View view;
		if (mReportEntity != null) {
			LayoutInflater inflater = LayoutInflater.from(mContext);
			view = inflater.inflate(layout.dialog_share_item_monthly, null);
			// ImageView ivResultIcon = (ImageView) view
			// .findViewById(id.iv_result_icon);
			tvTitle = (TextView) view.findViewById(id.tv_title);
			tvUnit1 = (TextView) view.findViewById(id.tv_unit_1);
			tvValue1 = (TextView) view.findViewById(id.tv_value_1);
			tvMeasureTime = (TextView) view.findViewById(id.tv_measure_time);

			Date date = TimeUtils.getDate(mReportEntity.curYearMonth,
					TimeUtils.YYYYMM);
			seconds = date.getTime() / 1000;

			tvTitle.setText("血压月报");
			tvUnit1.setText("本月统计：");
			tvValue1.setText("本月共检测血压" + mReportEntity.totalCounts + "次，其中异常"
					+ mReportEntity.abnormalCounts + "次");
			String yyMMcn = TimeUtils.getShareMonthTime(seconds);
			tvMeasureTime.setText("" + yyMMcn);
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
			errorAlert = "未获取到血压月报数据";
		}
	}

	@Override
	public void doGetShareURL(TaskHost taskHost) {

		String accessToken = CurrentAccountManager.getCurAccount()
				.getAccessToken();
		String month = mReportEntity.curYearMonth;
		GroupHelper.doShareUrlRecordTask(context, accessToken,
				BloodPressureModule.TYPE, null, null, month, taskHost);
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
			jo.put("report_type", TYPE_MONTH);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		GroupHelper.sendMessageTask(context,
				CurrentAccountManager.getCurAccount(), groupid, jo.toString(),
				Message.TYPE_LINK, taskHost);
	}
}

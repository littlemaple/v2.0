package com.medzone.cloud.ui.fragment.temperature;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.achartengine.GraphicalView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.medzone.cloud.Constants;
import com.medzone.cloud.data.CurrentAccountManager;
import com.medzone.cloud.data.TemporaryData;
import com.medzone.cloud.data.helper.MeasureDataUtil;
import com.medzone.cloud.module.modules.EarTemperatureModule;
import com.medzone.cloud.ui.MeasureDataActivity;
import com.medzone.cloud.ui.ShareActivity;
import com.medzone.cloud.ui.chart.EarTemperatureMonthlyReportChart;
import com.medzone.cloud.ui.dialog.CloudShareDialogFactory;
import com.medzone.cloud.ui.dialog.error.ErrorDialogUtil;
import com.medzone.framework.data.bean.imp.EarTemperature;
import com.medzone.framework.data.bean.imp.ReportEntity;
import com.medzone.framework.errorcode.ProxyErrorCode;
import com.medzone.framework.fragment.BaseFragment;
import com.medzone.framework.util.TimeUtils;
import com.medzone.mcloud.R;
import com.medzone.mcloud.R.id;
import com.medzone.mcloud.R.layout;
import com.medzone.mcloud.R.string;

public class ETMonthlyFragment extends BaseFragment implements
		View.OnClickListener {

	private TextView allTimes, errorTimes, lowTV, nomalTV, mildTV, serialTV,
			timeTV, valueTV, yearTV, monthTV;
	private ImageView leftIV, rightIV;
	private LinearLayout historyListLL, totalLL, etLineChartLL;
	private List<HashMap<String, String>> totalList = null;
	private int systemYear, systemMonth, currentYear, currentMonth;
	private int fristYear;
	private int fristMonth;
	private MeasureDataActivity mdActivity;
	private EarTemperatureModule etModule;
	private ImageView ivEarTemperatureFlag;

	private EarTemperatureMonthlyReportChart monthlyChart;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mdActivity = (MeasureDataActivity) activity;
		systemYear = TimeUtils.getCurrentYear();
		systemMonth = TimeUtils.getCurrentMonth() + 1;
		currentYear = systemYear;
		currentMonth = systemMonth;
		etModule = (EarTemperatureModule) mdActivity.getAttachModule();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		return initViewMonth(inflater, container);
	}

	public void fillImageView(int status) {
		switch (status) {
		case EarTemperature.TEMPERATURE_STATE_LOW:
			ivEarTemperatureFlag.setImageDrawable(getResources().getDrawable(
					R.drawable.testresultsview_testresult_graph_dire));
			break;
		case EarTemperature.TEMPERATURE_STATE_NORMAL:
			ivEarTemperatureFlag.setImageDrawable(getResources().getDrawable(
					R.drawable.testresultsview_testresult_graph_normal));
			break;
		case EarTemperature.TEMPERATURE_STATE_HIGH_FEVER:
			ivEarTemperatureFlag.setImageDrawable(getResources().getDrawable(
					R.drawable.testresultsview_testresult_graph_gaore));
			break;
		case EarTemperature.TEMPERATURE_STATE_FEVER:
			ivEarTemperatureFlag.setImageDrawable(getResources().getDrawable(
					R.drawable.testresultsview_testresult_graph_fare));
			break;

		}
	}

	private View initViewMonth(LayoutInflater inflater, ViewGroup container) {
		View view = inflater.inflate(
				layout.fragment_temperature_history_trend_month, container,
				false);
		leftIV = (ImageView) view
				.findViewById(R.id.temperature_history_trend_month_top_left);
		rightIV = (ImageView) view
				.findViewById(R.id.temperature_history_trend_month_top_right);
		historyListLL = (LinearLayout) view
				.findViewById(R.id.temperature_history_trend_list);
		yearTV = (TextView) view
				.findViewById(R.id.temperature_history_trend_month_top_year);
		monthTV = (TextView) view
				.findViewById(R.id.temperature_history_trend_month_top_month);
		allTimes = (TextView) view
				.findViewById(R.id.temperature_history_trend_month_all_times);
		errorTimes = (TextView) view
				.findViewById(R.id.temperature_history_trend_month_error_times);
		lowTV = (TextView) view
				.findViewById(R.id.temperature_history_trend_month_low);
		nomalTV = (TextView) view
				.findViewById(R.id.temperature_history_trend_month_normal);
		mildTV = (TextView) view
				.findViewById(R.id.temperature_history_trend_month_mild);
		serialTV = (TextView) view
				.findViewById(R.id.temperature_history_trend_month_serial);
		timeTV = (TextView) view
				.findViewById(R.id.temperature_history_trend_month_time);
		valueTV = (TextView) view
				.findViewById(R.id.temperature_history_trend_month_value);
		totalLL = (LinearLayout) view
				.findViewById(id.temperature_history_trend_month_total);
		etLineChartLL = (LinearLayout) view
				.findViewById(id.temperature_history_trend_line_chart);
		leftIV.setOnClickListener(this);
		rightIV.setOnClickListener(this);
		historyListLL.setOnClickListener(this);
		fillMonthChartView(currentYear, currentMonth);
		initDateAddView(currentYear, currentMonth);
		return view;
	}

	public void fillMonthChartView(int currentYear, int currentMonth) {
		monthlyChart = new EarTemperatureMonthlyReportChart(getActivity(),
				etModule, currentYear, currentMonth);
		final GraphicalView chartView = monthlyChart.getView();
		etLineChartLL.removeAllViews();
		etLineChartLL.addView(chartView);
	}

	private void initDateAddView(Integer year, Integer month) {
		getFristMeasureTime();
		totalList = new ArrayList<HashMap<String, String>>();
		totalList = etModule.getYearMonthAbnormal(year, month);
		yearTV.setText("" + currentYear);
		monthTV.setText("" + currentMonth);
		if (totalList != null && totalList.size() > 0) {
			totalLL.setVisibility(View.VISIBLE);
			EarTemperature et = etModule
					.getYearMonthMaxTemperature(year, month);
			int allTotalTimes = etModule.getMonthlyAllCounts(year, month);
			if (allTotalTimes < 10) {
				allTimes.setText("0" + allTotalTimes);
			} else {
				allTimes.setText(String.valueOf(allTotalTimes));
			}
			int errorTotalTimes = etModule.getMonthlyExceptionCounts(year,
					month);
			if (errorTotalTimes < 10) {
				errorTimes.setText("0" + errorTotalTimes);
			} else {
				errorTimes.setText(String.valueOf(errorTotalTimes));
			}
			int count = totalList.size();
			for (int i = 0; i < count; i++) {
				int value = Integer.valueOf(totalList.get(i).get(
						Constants.KEY_ABNORMAL));
				String textStr = totalList.get(i).get(Constants.KEY_COUNT);
				if (value == EarTemperature.TEMPERATURE_STATE_LOW) {
					lowTV.setText(textStr);
				} else if (value == EarTemperature.TEMPERATURE_STATE_NORMAL) {
					nomalTV.setText(textStr);
				} else if (value == EarTemperature.TEMPERATURE_STATE_FEVER) {
					mildTV.setText(textStr);
				} else if (value == EarTemperature.TEMPERATURE_STATE_HIGH_FEVER) {
					serialTV.setText(textStr);
				}
			}

			timeTV.setText(TimeUtils.getYearToSecond(et.getMeasureTime()));
			String temperature = String.valueOf(et.getTemperature());
			if (temperature != null)
				valueTV.setText(getString(string.ear_temperature) + temperature
						+ getString(string.ear_temperature_unit));
		} else {
			allTimes.setText("00");
			errorTimes.setText("00");
			lowTV.setText("00");
			nomalTV.setText("00");
			mildTV.setText("00");
			serialTV.setText("00");
			totalLL.setVisibility(View.GONE);
			ErrorDialogUtil.showErrorToast(mdActivity,
					ProxyErrorCode.TYPE_MEASURE,
					ProxyErrorCode.LocalError.CODE_11405);
		}
	}

	private void getFristMeasureTime() {
		String time = etModule.getFristMeasureTime(CurrentAccountManager
				.getCurAccount());
		if (time != null && !time.equals("")) {
			fristYear = Integer.valueOf(time.substring(0, 4));
			fristMonth = Integer.valueOf(time.substring(4, 6));
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case id.temperature_history_trend_list:
			mdActivity.toEarTemperatureHistoryListFragment(this);
			break;
		case id.temperature_history_trend_month_top_left:
			toNextMonth();
			break;
		case id.temperature_history_trend_month_top_right:
			toLastMonth();
			break;
		default:
			break;
		}
	}

	private void toNextMonth() {
		if (currentYear == fristYear && currentMonth > fristMonth) {
			currentMonth--;
			fillMonthChartView(currentYear, currentMonth);
			initDateAddView(currentYear, currentMonth);
		} else if (currentYear > fristYear) {
			currentMonth--;
			if (currentMonth <= 0) {
				currentYear--;
				currentMonth = 12;
			}
			fillMonthChartView(currentYear, currentMonth);
			initDateAddView(currentYear, currentMonth);
		} else {
			ErrorDialogUtil.showErrorToast(mdActivity,
					ProxyErrorCode.TYPE_MEASURE,
					ProxyErrorCode.LocalError.CODE_11401);
		}
	}

	private void toLastMonth() {
		if (currentYear == systemYear && currentMonth < systemMonth) {
			currentMonth++;
			fillMonthChartView(currentYear, currentMonth);
			initDateAddView(currentYear, currentMonth);

		} else if (currentYear < systemYear) {
			currentMonth++;
			if (currentMonth > 12) {
				currentYear++;
				currentMonth = 1;
			}
			fillMonthChartView(currentYear, currentMonth);
			initDateAddView(currentYear, currentMonth);
		}
	}

	public void doShare() {

		ReportEntity entity = new ReportEntity();

		TemporaryData.save(Constants.TEMPORARYDATA_KEY_SHARE_TYPE,
				CloudShareDialogFactory.SHARE_TYPE_MONTHLY);

		List<Integer> timeValueList = monthlyChart.getCurDate();
		int year = timeValueList.get(0);
		int month = timeValueList.get(1);
		String dateSet = "";
		if (month < 10) {
			dateSet = "0";
		}
		dateSet += month;
		dateSet = year + dateSet;

		entity.totalCounts = etModule.getMonthlyAllCounts(year, month);
		entity.abnormalCounts = etModule.getMonthlyExceptionCounts(year, month);
		entity.curYearMonth = dateSet;

		TemporaryData.save(ReportEntity.class.getName(), entity);
		TemporaryData.save(Constants.TEMPORARYDATA_KEY_MEASURE_TYPE,
				EarTemperatureModule.TYPE);

		Intent intent = new Intent(getActivity(), ShareActivity.class);
		startActivity(intent);
	}
}

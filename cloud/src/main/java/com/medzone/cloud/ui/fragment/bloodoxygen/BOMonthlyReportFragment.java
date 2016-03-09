package com.medzone.cloud.ui.fragment.bloodoxygen;

import java.util.HashMap;
import java.util.List;

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
import com.medzone.cloud.data.TemporaryData;
import com.medzone.cloud.data.helper.MeasureDataUtil;
import com.medzone.cloud.module.modules.BloodOxygenModule;
import com.medzone.cloud.ui.MeasureDataActivity;
import com.medzone.cloud.ui.ShareActivity;
import com.medzone.cloud.ui.chart.BloodOxygenMonthlyReportChart;
import com.medzone.cloud.ui.dialog.CloudShareDialogFactory;
import com.medzone.cloud.ui.dialog.error.ErrorDialogUtil;
import com.medzone.framework.data.bean.imp.BloodOxygen;
import com.medzone.framework.data.bean.imp.ReportEntity;
import com.medzone.framework.errorcode.ProxyErrorCode;
import com.medzone.framework.fragment.BaseModuleFragment;
import com.medzone.framework.util.TimeUtils;
import com.medzone.mcloud.R;
import com.medzone.mcloud.R.layout;
import com.medzone.mcloud.R.string;

public class BOMonthlyReportFragment extends BaseModuleFragment implements
		View.OnClickListener {

	private TextView allTimes, errorTimes, lowTV, idealTV, missingTV, timeTV,
			valueTV, yearTV, monthTV;
	private ImageView leftIV, rightIV;
	private LinearLayout historyListLL, totalLL, oxygenLineChartLL,
			heartLineChartLL;
	private MeasureDataActivity mdActivity;
	private BloodOxygenModule boModule;
	private List<HashMap<String, String>> abnormalLevelMap;
	private int fristYear;
	private int fristMonth;
	private int systemYear, systemMonth, currentYear, currentMonth;
	private int lastDay;
	private BloodOxygenMonthlyReportChart oxygenMonthlyReport;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mdActivity = (MeasureDataActivity) activity;
		systemYear = TimeUtils.getCurrentYear();
		systemMonth = TimeUtils.getCurrentMonth() + 1;
		currentYear = systemYear;
		currentMonth = systemMonth;
		boModule = (BloodOxygenModule) mdActivity.getAttachModule();
		oxygenMonthlyReport = new BloodOxygenMonthlyReportChart(mdActivity,
				boModule);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		return initViewMonth(inflater, container);
	}

	private View initViewMonth(LayoutInflater inflater, ViewGroup container) {
		View view = inflater.inflate(
				layout.fragment_oxygen_history_trend_month, container, false);
		leftIV = (ImageView) view
				.findViewById(R.id.oxygen_history_trend_month_top_left);
		rightIV = (ImageView) view
				.findViewById(R.id.oxygen_history_trend_month_top_right);
		historyListLL = (LinearLayout) view
				.findViewById(R.id.oxygen_history_trend_list);
		yearTV = (TextView) view
				.findViewById(R.id.oxygen_history_trend_month_top_year);
		monthTV = (TextView) view
				.findViewById(R.id.oxygen_history_trend_month_top_month);
		allTimes = (TextView) view
				.findViewById(R.id.oxygen_history_trend_month_all_times);
		errorTimes = (TextView) view
				.findViewById(R.id.oxygen_history_trend_month_error_times);
		lowTV = (TextView) view
				.findViewById(R.id.oxygen_history_trend_month_low);
		idealTV = (TextView) view
				.findViewById(R.id.oxygen_history_trend_month_ideal);
		missingTV = (TextView) view
				.findViewById(R.id.oxygen_history_trend_month_missing);
		timeTV = (TextView) view
				.findViewById(R.id.oxygen_history_trend_month_time);
		valueTV = (TextView) view
				.findViewById(R.id.oxygen_history_trend_month_value);
		totalLL = (LinearLayout) view
				.findViewById(R.id.oxygen_history_trend_month_total);
		oxygenLineChartLL = (LinearLayout) view
				.findViewById(R.id.oxygen_history_trend_oxygen_line_chart);
		heartLineChartLL = (LinearLayout) view
				.findViewById(R.id.oxygen_history_trend_heart_line_chart);
		leftIV.setOnClickListener(this);
		rightIV.setOnClickListener(this);
		historyListLL.setOnClickListener(this);
		fillMonthChartView(currentYear, currentMonth);
		initDateAddView(currentYear, currentMonth);
		return view;
	}

	private void fillMonthChartView(int currentYear, int currentMonth) {
		getLastDay();
		oxygenLineChartLL.removeAllViews();
		oxygenLineChartLL.addView(oxygenMonthlyReport.getOxygenView(
				currentYear, currentMonth, lastDay), -1, -1);
		heartLineChartLL.removeAllViews();
		heartLineChartLL.addView(oxygenMonthlyReport.getHeartView(), -1, -1);
	}

	private void getLastDay() {
		lastDay = TimeUtils.getLastdayNOMonth(currentYear + "-" + currentMonth);
	}

	private void getFristMeasureTime() {
		Long time = boModule.getFristMeasureTime();
		if (time != null) {
			fristYear = TimeUtils.getYear(time);
			fristMonth = TimeUtils.getMonth(time);
		} else {
			fristYear = systemYear;
			fristMonth = systemMonth;
		}
	}

	private void initDateAddView(Integer year, Integer month) {
		getFristMeasureTime();
		abnormalLevelMap = boModule.getYearMonthAbnormal(year, month);
		yearTV.setText(String.valueOf(currentYear));
		monthTV.setText(String.valueOf(currentMonth));
		emptyDataFileView();
		if (abnormalLevelMap != null && abnormalLevelMap.size() > 0) {
			BloodOxygen bo = boModule.getYearMonthMinOxygen(year, month);
			totalLL.setVisibility(View.VISIBLE);
			int allTotalTimes = boModule.getMonthlyAllCounts(year, month);

			allTimes.setText(MeasureDataUtil.StringConcatenation(allTotalTimes));
			int errorTotalTimes = boModule.getMonthlyExceptionCounts(year,
					month);
			errorTimes.setText(MeasureDataUtil
					.StringConcatenation(errorTotalTimes));
			int count = abnormalLevelMap != null ? abnormalLevelMap.size() : 0;

			for (int i = 0; i < count; i++) {
				int value = Integer.valueOf(abnormalLevelMap.get(i).get(
						Constants.KEY_ABNORMAL));
				String textStr = abnormalLevelMap.get(i).get(
						Constants.KEY_COUNT);
				if (value == BloodOxygen.OXYGEN_STATE_LOW) {
					lowTV.setText(textStr);
				} else if (value == BloodOxygen.OXYGEN_STATE_IDEAL) {
					idealTV.setText(textStr);
				} else if (value == BloodOxygen.OXYGEN_STATE_MISSING) {
					missingTV.setText(textStr);
				}
			}
			timeTV.setText(TimeUtils.getYearToSecond(bo.getMeasureTime()));
			String oxygen = String.valueOf(bo.getOxygen());
			if (oxygen != null)
				valueTV.setText(getString(string.blood_oxygen_saturation)
						+ String.valueOf(bo.getOxygen()
								+ getString(string.blood_oxygen_unit)) + "、"
						+ getString(string.heart_rate)
						+ String.valueOf(bo.getRate())
						+ getString(string.heart_rate_unit));
		} else {
			ErrorDialogUtil.showErrorToast(mdActivity,
					ProxyErrorCode.TYPE_MEASURE,
					ProxyErrorCode.LocalError.CODE_11405);
		}
	}

	private void emptyDataFileView() {
		allTimes.setText(getString(string.total_no_data));
		errorTimes.setText(getString(string.total_no_data));
		lowTV.setText(getString(string.total_no_data));
		idealTV.setText(getString(string.total_no_data));
		missingTV.setText(getString(string.total_no_data));
		totalLL.setVisibility(View.GONE);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.oxygen_history_trend_list:
			mdActivity.toBloodOxygenHistoryListFragment(this);
			break;
		case R.id.oxygen_history_trend_month_top_left:
			toNextMonth();
			break;
		case R.id.oxygen_history_trend_month_top_right:
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

		List<Integer> timeValueList = oxygenMonthlyReport.getCurDate();
		int year = timeValueList.get(0);
		int month = timeValueList.get(1);
		String dateSet = "";
		if (month < 10) {
			dateSet = "0";
		}
		dateSet += month;
		dateSet = year + dateSet;

		entity.totalCounts = boModule.getMonthlyAllCounts(year, month);
		entity.abnormalCounts = boModule.getMonthlyExceptionCounts(year, month);
		entity.curYearMonth = dateSet;

		TemporaryData.save(ReportEntity.class.getName(), entity);
		TemporaryData.save(Constants.TEMPORARYDATA_KEY_MEASURE_TYPE,
				BloodOxygenModule.TYPE);

		Intent intent = new Intent(getActivity(), ShareActivity.class);
		startActivity(intent);
	}
}

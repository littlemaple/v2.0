/**
 * 
 */
package com.medzone.cloud.ui.fragment.bloodpressure;

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
import com.medzone.cloud.data.TemporaryData;
import com.medzone.cloud.data.helper.MeasureDataUtil;
import com.medzone.cloud.module.modules.BloodPressureModule;
import com.medzone.cloud.ui.MeasureDataActivity;
import com.medzone.cloud.ui.ShareActivity;
import com.medzone.cloud.ui.chart.BloodPressureMothlyReportChart;
import com.medzone.cloud.ui.dialog.CloudShareDialogFactory;
import com.medzone.cloud.ui.dialog.error.ErrorDialogUtil;
import com.medzone.framework.data.bean.imp.BloodPressure;
import com.medzone.framework.data.bean.imp.ReportEntity;
import com.medzone.framework.errorcode.ProxyErrorCode;
import com.medzone.framework.fragment.BaseFragment;
import com.medzone.framework.util.TimeUtils;
import com.medzone.mcloud.R;
import com.medzone.mcloud.R.id;
import com.medzone.mcloud.R.layout;
import com.medzone.mcloud.R.string;

public class BPMonthlyReportFragment extends BaseFragment implements
		View.OnClickListener {

	private TextView allTimes, errorTimes, timeTV, valueTV, yearTV, monthTV;
	private TextView lowTV, idealTV, normalTV, normalHighTV, mildTV,
			moderateTV, seriousTV;
	private ImageView leftIV, rightIV;
	private LinearLayout historyListLL, totalLL, bpMonthReportTrendChartLL;
	private MeasureDataActivity mdActivity;
	private BloodPressureModule bpModule;
	private List<HashMap<String, String>> totalList = null;
	private int systemYear, systemMonth, currentYear, currentMonth;
	private int fristYear;
	private int fristMonth;

	private BloodPressureMothlyReportChart monthlyChart;
	private boolean isKpa;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mdActivity = (MeasureDataActivity) activity;
		systemYear = TimeUtils.getCurrentYear();
		systemMonth = TimeUtils.getCurrentMonth() + 1;
		currentYear = systemYear;
		currentMonth = systemMonth;
		bpModule = (BloodPressureModule) mdActivity.getAttachModule();
		isKpa = bpModule.isKpaMode();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		return initViewMonth(inflater, container);
	}

	private View initViewMonth(LayoutInflater inflater, ViewGroup container) {
		View view = inflater.inflate(
				layout.fragment_pressure_history_trend_month, container, false);
		historyListLL = (LinearLayout) view
				.findViewById(R.id.pressure_history_trend_list);
		historyListLL.setOnClickListener(this);

		leftIV = (ImageView) view
				.findViewById(R.id.pressure_history_trend_month_top_left);
		rightIV = (ImageView) view
				.findViewById(R.id.pressure_history_trend_month_top_right);
		historyListLL = (LinearLayout) view
				.findViewById(R.id.pressure_history_trend_list);
		leftIV.setOnClickListener(this);
		rightIV.setOnClickListener(this);
		historyListLL.setOnClickListener(this);

		yearTV = (TextView) view
				.findViewById(R.id.pressure_history_trend_month_top_year);
		monthTV = (TextView) view
				.findViewById(R.id.pressure_history_trend_month_top_month);
		allTimes = (TextView) view
				.findViewById(R.id.pressure_history_trend_month_all_times);
		errorTimes = (TextView) view
				.findViewById(R.id.pressure_history_trend_month_error_times);
		lowTV = (TextView) view
				.findViewById(R.id.pressure_history_trend_month_low);
		idealTV = (TextView) view
				.findViewById(R.id.pressure_history_trend_month_ideal);
		normalTV = (TextView) view
				.findViewById(R.id.pressure_history_trend_month_normal);
		mildTV = (TextView) view
				.findViewById(R.id.pressure_history_trend_month_mild);
		normalHighTV = (TextView) view
				.findViewById(R.id.pressure_history_trend_month_nomal_high);
		moderateTV = (TextView) view
				.findViewById(R.id.pressure_history_trend_month_moderate);
		seriousTV = (TextView) view
				.findViewById(R.id.pressure_history_trend_month_serious);
		timeTV = (TextView) view
				.findViewById(R.id.pressure_history_trend_month_time);
		valueTV = (TextView) view
				.findViewById(R.id.pressure_history_trend_month_value);
		totalLL = (LinearLayout) view
				.findViewById(id.pressure_history_trend_month_total);
		bpMonthReportTrendChartLL = (LinearLayout) view
				.findViewById(R.id.bp_trend_chart_mothly_report);

		fillMonthChartView(currentYear, currentMonth);

		initDateAddView(currentYear, currentMonth);
		return view;
	}

	public void fillMonthChartView(int currentYear, int currentMonth) {
		monthlyChart = new BloodPressureMothlyReportChart(getActivity(),
				bpModule, currentYear, currentMonth);
		final GraphicalView chartView = monthlyChart.getView();
		bpMonthReportTrendChartLL.removeAllViews();
		bpMonthReportTrendChartLL.addView(chartView);
	}

	private void initDateAddView(Integer year, Integer month) {
		getFristMeasureTime();
		totalList = new ArrayList<HashMap<String, String>>();
		totalList = bpModule.getYearMonthAbnormal(year, month);
		yearTV.setText(String.valueOf(currentYear));
		monthTV.setText(String.valueOf(currentMonth));
		emptyDataFillView();
		if (totalList != null && totalList.size() > 0) {
			totalLL.setVisibility(View.VISIBLE);
			BloodPressure pressure = bpModule.getYearMonthMaxPressure(year,
					month);
			int allTotalTimes = bpModule.getMonthlyAllCounts(year, month);
			allTimes.setText(MeasureDataUtil.StringConcatenation(allTotalTimes));
			int errorTotalTimes = bpModule.getMonthlyExceptionCounts(year,
					month);
			errorTimes.setText(MeasureDataUtil
					.StringConcatenation(errorTotalTimes));
			int count = totalList != null ? totalList.size() : 0;
			for (int i = 0; i < count; i++) {
				int value = Integer.valueOf(totalList.get(i).get(
						Constants.KEY_ABNORMAL));
				String textStr = totalList.get(i).get(Constants.KEY_COUNT);
				switch (value) {
				case BloodPressure.PRESSURE_STATE_LOW:
					lowTV.setText(textStr);
					break;
				case BloodPressure.PRESSURE_STATE_IDEAL:
					idealTV.setText(textStr);
					break;
				case BloodPressure.PRESSURE_STATE_NORMAL:
					normalTV.setText(textStr);
					break;
				case BloodPressure.PRESSURE_STATE_NORMAL_HIGH:
					normalHighTV.setText(textStr);
					break;
				case BloodPressure.PRESSURE_STATE_MILD:
					mildTV.setText(textStr);
					break;
				case BloodPressure.PRESSURE_STATE_MODERATE:
					moderateTV.setText(textStr);
					break;
				case BloodPressure.PRESSURE_STATE_SERIOUS:
					seriousTV.setText(textStr);
					break;
				default:
					break;
				}
			}
			timeTV.setText(TimeUtils.getYearToSecond(pressure.getMeasureTime()));
			String value = String.valueOf(pressure.getHigh());
			if (value != null)
				if (isKpa) {

				} else {

				}
			valueTV.setText(getMonthlyReportResultString(pressure));
		} else {
			ErrorDialogUtil.showErrorToast(mdActivity,
					ProxyErrorCode.TYPE_MEASURE,
					ProxyErrorCode.LocalError.CODE_11405);
		}
	}

	public String getMonthlyReportResultString(BloodPressure bp) {
		String ret = new String("");
		String placeHolder = "、";
		ret += getString(string.systolic_pressure);
		if (isKpa) {
			float highValue = bp.getHighKPA();
			float lowValue = bp.getLowKPA();
			ret += highValue;
			ret += bp.getPressureUnit(isKpa);
			ret += placeHolder;
			ret += getString(string.diastolic_pressure);
			ret += lowValue;
			ret += bp.getPressureUnit(isKpa);
			ret += placeHolder;
		} else {
			int highValue = bp.getHigh().intValue();
			int lowValue = bp.getLow().intValue();
			ret += highValue;
			ret += bp.getPressureUnit(isKpa);
			ret += placeHolder;
			ret += getString(string.diastolic_pressure);
			ret += lowValue;
			ret += bp.getPressureUnit(isKpa);
			ret += placeHolder;
		}
		ret += getString(string.heart_rate);
		ret += bp.getRate();
		ret += bp.getRateUnit();

		return ret;
	}

	private void emptyDataFillView() {
		allTimes.setText(getString(string.total_no_data));
		errorTimes.setText(getString(string.total_no_data));
		lowTV.setText(getString(string.total_no_data));
		idealTV.setText(getString(string.total_no_data));
		normalTV.setText(getString(string.total_no_data));
		normalHighTV.setText(getString(string.total_no_data));
		mildTV.setText(getString(string.total_no_data));
		moderateTV.setText(getString(string.total_no_data));
		seriousTV.setText(getString(string.total_no_data));
		totalLL.setVisibility(View.GONE);
	}

	private void getFristMeasureTime() {
		Long time = bpModule.getFristMeasureTime();
		if (time != null) {
			fristYear = TimeUtils.getYear(time);
			fristMonth = TimeUtils.getMonth(time);
		} else {
			fristYear = systemYear;
			fristMonth = systemMonth;
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case id.pressure_history_trend_list:
			mdActivity.toBloodPressureHistoryListFragment(this);
			break;
		case id.pressure_history_trend_month_top_left:
			toNextMonth();
			break;
		case id.pressure_history_trend_month_top_right:
			toLastMonth();
			break;
		default:
			break;
		}
	}

	private void toNextMonth() {
		if (currentYear == fristYear && currentMonth > fristMonth) {
			currentMonth--;
			initDateAddView(currentYear, currentMonth);
			fillMonthChartView(currentYear, currentMonth);
		} else if (currentYear > fristYear) {
			currentMonth--;
			if (currentMonth <= 0) {
				currentYear--;
				currentMonth = 12;
			}
			initDateAddView(currentYear, currentMonth);
			fillMonthChartView(currentYear, currentMonth);
		} else {
			ErrorDialogUtil.showErrorToast(mdActivity,
					ProxyErrorCode.TYPE_MEASURE,
					ProxyErrorCode.LocalError.CODE_11401);
		}
	}

	private void toLastMonth() {
		if (currentYear == systemYear && currentMonth < systemMonth) {
			currentMonth++;
			initDateAddView(currentYear, currentMonth);
			fillMonthChartView(currentYear, currentMonth);
		} else if (currentYear < systemYear) {
			currentMonth++;
			if (currentMonth > 12) {
				currentYear++;
				currentMonth = 1;
			}
			initDateAddView(currentYear, currentMonth);
			fillMonthChartView(currentYear, currentMonth);
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

		entity.totalCounts = bpModule.getMonthlyAllCounts(year, month);
		entity.abnormalCounts = bpModule.getMonthlyExceptionCounts(year, month);
		entity.curYearMonth = dateSet;

		TemporaryData.save(ReportEntity.class.getName(), entity);
		TemporaryData.save(Constants.TEMPORARYDATA_KEY_MEASURE_TYPE,
				BloodPressureModule.TYPE);

		Intent intent = new Intent(getActivity(), ShareActivity.class);
		startActivity(intent);
	}

}

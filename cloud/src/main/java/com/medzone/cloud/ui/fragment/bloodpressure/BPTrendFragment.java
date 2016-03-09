package com.medzone.cloud.ui.fragment.bloodpressure;

import java.util.Collections;
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
import android.widget.ListView;
import android.widget.TextView;

import com.medzone.cloud.Constants;
import com.medzone.cloud.data.TemporaryData;
import com.medzone.cloud.data.helper.ComparatorBaseMeasureDataUtil;
import com.medzone.cloud.data.helper.MeasureDataUtil;
import com.medzone.cloud.module.modules.BloodPressureModule;
import com.medzone.cloud.ui.MeasureDataActivity;
import com.medzone.cloud.ui.ShareActivity;
import com.medzone.cloud.ui.adapter.RecentExceptionMeasureDataAdapter;
import com.medzone.cloud.ui.chart.BloodPressureMothlyReportChart;
import com.medzone.cloud.ui.chart.BloodPressureRecentlyReportChart;
import com.medzone.cloud.ui.chart.IConvertDataListener;
import com.medzone.cloud.ui.dialog.CloudShareDialogFactory;
import com.medzone.framework.data.bean.imp.BaseMeasureData;
import com.medzone.framework.data.bean.imp.BloodPressure;
import com.medzone.framework.data.bean.imp.ReportEntity;
import com.medzone.framework.fragment.BaseFragment;
import com.medzone.framework.util.TimeUtils;
import com.medzone.mcloud.R;
import com.medzone.mcloud.R.id;
import com.medzone.mcloud.R.layout;

public class BPTrendFragment extends BaseFragment implements
		View.OnClickListener {

	private LinearLayout historyListLL, bpMonthReportTrendChartLL,
			bpRecentlyReportTrendChartLL;
	private MeasureDataActivity mdActivity;
	private BloodPressureModule bpModule;
	private TextView tvRecentlyHigh, tvRecentlyLow, tvRecentlyPluse,
			tvHighUnit, tvLowUnit;
	private ImageView ivBloodPressureFlag;
	private TextView tvMeasureTime;

	private ListView lvException;
	private TextView tvException;
	private RecentExceptionMeasureDataAdapter recentExceptionMeasureDataAdapter;

	private List<BaseMeasureData> curXAxisList;
	private List<BaseMeasureData> curXAxisException;

	private BloodPressureRecentlyReportChart recentlyChart;
	private BloodPressureMothlyReportChart monthlyChart;

	private boolean isKpa;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mdActivity = (MeasureDataActivity) activity;
		bpModule = (BloodPressureModule) mdActivity.getAttachModule();
		isKpa = bpModule.isKpaMode();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		return initViewRecent(inflater, container);
	}

	private View initViewRecent(LayoutInflater inflater, ViewGroup container) {
		View view = inflater.inflate(layout.fragment_pressure_history_trend,
				container, false);
		tvRecentlyPluse = (TextView) view
				.findViewById(R.id.recently_pressure_result_details_hplTV);
		tvRecentlyHigh = (TextView) view
				.findViewById(R.id.recently_pressure_result_details_highTV);

		tvRecentlyLow = (TextView) view
				.findViewById(R.id.recently_pressure_result_details_lowTV);
		tvHighUnit = (TextView) view
				.findViewById(R.id.pressure_result_details_high_unitTV);
		tvLowUnit = (TextView) view
				.findViewById(R.id.pressure_result_details_low_unitTV);

		historyListLL = (LinearLayout) view
				.findViewById(R.id.pressure_history_trend_list);
		tvMeasureTime = (TextView) view.findViewById(R.id.measure_time);
		ivBloodPressureFlag = (ImageView) view
				.findViewById(R.id.iv_recently_blood_pressure);
		lvException = (ListView) view
				.findViewById(id.lv_recently_exception_record);
		tvException = (TextView) view
				.findViewById(R.id.tv_history_trend_exception_text);
		bpRecentlyReportTrendChartLL = (LinearLayout) view
				.findViewById(R.id.bp_trend_chart_recently_report);

		PostRecentlyView();
		return view;
	}

	public void PostRecentlyView() {
		historyListLL.setOnClickListener(this);

		if (isKpa) {
			tvLowUnit.setText(BloodPressure.BLOODPRESSURE_UNIT_KPA);
			tvHighUnit.setText(BloodPressure.BLOODPRESSURE_UNIT_KPA);
		} else {
			tvLowUnit.setText(BloodPressure.BLOODPRESSURE_UNIT_MMHG);
			tvHighUnit.setText(BloodPressure.BLOODPRESSURE_UNIT_MMHG);
		}

		recentlyChart = new BloodPressureRecentlyReportChart(getActivity(),
				bpModule, -1);
		recentlyChart.setiSelectedPointListener(new IConvertDataListener() {

			@Override
			public void OnSelected(BaseMeasureData data) {
				BloodPressure bloodPressure = (BloodPressure) data;
				tvRecentlyPluse.setText(String.valueOf(bloodPressure.getRate()));

				if (isKpa) {
					tvRecentlyHigh.setText(bloodPressure.getHighKPA() + "");
					tvRecentlyLow.setText(bloodPressure.getLowKPA() + "");
				} else {
					tvRecentlyHigh.setText(bloodPressure.getHigh().intValue()
							+ "");
					tvRecentlyLow.setText(bloodPressure.getLow().intValue()
							+ "");
				}

				MeasureDataUtil.BloodPressureFlag(ivBloodPressureFlag,
						bloodPressure.getAbnormal());
				tvMeasureTime.setText(TimeUtils.getYearToSecond(data
						.getMeasureTime()));
			}

			@Override
			public void OnFirstPointPosition(BaseMeasureData bean) {
				BloodPressure bloodPressure = (BloodPressure) bean;
				tvRecentlyPluse.setText(String.valueOf(bloodPressure.getRate()));

				if (isKpa) {
					tvRecentlyHigh.setText(bloodPressure.getHighKPA() + "");
					tvRecentlyLow.setText(bloodPressure.getLowKPA() + "");
				} else {
					tvRecentlyHigh.setText(bloodPressure.getHigh().intValue()
							+ "");
					tvRecentlyLow.setText(bloodPressure.getLow().intValue()
							+ "");
				}
				MeasureDataUtil.BloodPressureFlag(ivBloodPressureFlag,
						bloodPressure.getAbnormal());
				tvMeasureTime.setText(TimeUtils.getYearToSecond(bean
						.getMeasureTime()));
			}

			@Override
			public void ConvertExceptionList(List<BaseMeasureData> curXAxis,
					List<BaseMeasureData> exceptions) {

				curXAxisList = curXAxis;
				curXAxisException = exceptions;

				ComparatorBaseMeasureDataUtil comparatorBaseMeasureDataUtil = new ComparatorBaseMeasureDataUtil();
				Collections.sort(curXAxisException,
						comparatorBaseMeasureDataUtil);
				recentExceptionMeasureDataAdapter = new RecentExceptionMeasureDataAdapter(
						mdActivity);
				recentExceptionMeasureDataAdapter.setContent(exceptions);
				lvException.setAdapter(recentExceptionMeasureDataAdapter);
				if (exceptions != null && exceptions.size() > 0) {
					tvException.setVisibility(View.GONE);
				} else {
					tvException.setVisibility(View.VISIBLE);
				}
			}
		});
		bpRecentlyReportTrendChartLL.removeAllViews();
		bpRecentlyReportTrendChartLL.addView(recentlyChart.getView());
	}

	public void fillMonthChartView(int currentYear, int currentMonth) {
		monthlyChart = new BloodPressureMothlyReportChart(getActivity(),
				bpModule, currentYear, currentMonth);
		final GraphicalView chartView = monthlyChart.getView();
		bpMonthReportTrendChartLL.removeAllViews();
		bpMonthReportTrendChartLL.addView(chartView);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case id.pressure_history_trend_list:
			mdActivity.toBloodPressureHistoryListFragment(this);
			break;
		default:
			break;
		}
	}

	public void doShare() {

		ReportEntity entity = new ReportEntity();

		TemporaryData.save(Constants.TEMPORARYDATA_KEY_SHARE_TYPE,
				CloudShareDialogFactory.SHARE_TYPE_RECENTLY);

		entity.totalCounts = curXAxisList != null ? curXAxisList.size() : 0;
		entity.abnormalCounts = curXAxisException != null ? curXAxisException
				.size() : 0;
		if (curXAxisList != null && curXAxisList.size() > 0) {
			// 对7个点进行排序，取时间最近的点
			Collections.sort(curXAxisList, new ComparatorBaseMeasureDataUtil());
			entity.startDate = curXAxisList.get(0).getMeasureTime();
			entity.measureUID = curXAxisList.get(0).getMeasureUID();
		}
		TemporaryData.save(ReportEntity.class.getName(), entity);
		TemporaryData.save(Constants.TEMPORARYDATA_KEY_MEASURE_TYPE,
				BloodPressureModule.TYPE);

		Intent intent = new Intent(getActivity(), ShareActivity.class);
		startActivity(intent);
	}

}

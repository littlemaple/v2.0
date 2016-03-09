package com.medzone.cloud.ui.fragment.temperature;

import java.util.Collections;
import java.util.List;

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
import com.medzone.cloud.module.modules.EarTemperatureModule;
import com.medzone.cloud.ui.MeasureDataActivity;
import com.medzone.cloud.ui.ShareActivity;
import com.medzone.cloud.ui.adapter.RecentExceptionMeasureDataAdapter;
import com.medzone.cloud.ui.chart.EarTemperatureRecentReportChart;
import com.medzone.cloud.ui.chart.IConvertDataListener;
import com.medzone.cloud.ui.dialog.CloudShareDialogFactory;
import com.medzone.framework.data.bean.imp.BaseMeasureData;
import com.medzone.framework.data.bean.imp.EarTemperature;
import com.medzone.framework.data.bean.imp.ReportEntity;
import com.medzone.framework.fragment.BaseFragment;
import com.medzone.framework.util.TimeUtils;
import com.medzone.mcloud.R;
import com.medzone.mcloud.R.id;
import com.medzone.mcloud.R.layout;

public class ETTrendFragment extends BaseFragment implements
		View.OnClickListener {

	private LinearLayout historyListLL, etRecentChartLL;
	private MeasureDataActivity mdActivity;
	private EarTemperatureModule etModule;
	private ImageView ivEarTemperatureFlag;
	private TextView tvMeasureTime;

	private TextView recentlyData;
	private ListView lvException;
	private TextView tvException;
	private RecentExceptionMeasureDataAdapter recentExceptionMeasureDataAdapter;

	private List<BaseMeasureData> curXAxisList;
	private List<BaseMeasureData> curXAxisException;

	private EarTemperatureRecentReportChart recentlyChart;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mdActivity = (MeasureDataActivity) activity;
		etModule = (EarTemperatureModule) mdActivity.getAttachModule();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		return initViewRecent(inflater, container);
	}

	private View initViewRecent(LayoutInflater inflater, ViewGroup container) {
		View view = inflater.inflate(layout.fragment_temperature_history_trend,
				container, false);
		recentlyData = (TextView) view
				.findViewById(R.id.recently_pressure_result_details_hplTV);
		tvMeasureTime = (TextView) view.findViewById(R.id.measure_time);
		ivEarTemperatureFlag = (ImageView) view
				.findViewById(R.id.iv_temperature_flag);
		historyListLL = (LinearLayout) view
				.findViewById(R.id.temperature_history_trend_list);
		etRecentChartLL = (LinearLayout) view
				.findViewById(R.id.temperature_history_trend_recent_chart);
		lvException = (ListView) view
				.findViewById(id.lv_recently_exception_record);
		tvException = (TextView) view
				.findViewById(R.id.tv_history_trend_exception_text);

		PostRecentlyView();
		return view;
	}

	public void PostRecentlyView() {
		historyListLL.setOnClickListener(this);
		recentlyChart = new EarTemperatureRecentReportChart(mdActivity,
				etModule, -1);
		recentlyChart.setiSelectedPointListener(new IConvertDataListener() {

			@Override
			public void OnSelected(BaseMeasureData data) {
				recentlyData.setText(((EarTemperature) data).getTemperature()
						+ "");
				fillImageView(data.getAbnormal());
				tvMeasureTime.setText(TimeUtils.getYearToSecond(data
						.getMeasureTime()));
			}

			@Override
			public void OnFirstPointPosition(BaseMeasureData bean) {
				recentlyData.setText(((EarTemperature) bean).getTemperature()
						+ "");
				fillImageView(bean.getAbnormal());
				tvMeasureTime.setText(TimeUtils.getYearToSecond(bean
						.getMeasureTime()));
			}

			@Override
			public void ConvertExceptionList(List<BaseMeasureData> curXAxis,
					List<BaseMeasureData> curXAxisException) {
				recentExceptionMeasureDataAdapter = new RecentExceptionMeasureDataAdapter(
						mdActivity);
				ComparatorBaseMeasureDataUtil comparatorBaseMeasureDataUtil = new ComparatorBaseMeasureDataUtil();
				Collections.sort(curXAxisException,
						comparatorBaseMeasureDataUtil);
				recentExceptionMeasureDataAdapter.setContent(curXAxisException);
				lvException.setAdapter(recentExceptionMeasureDataAdapter);
				if (curXAxisException != null && curXAxisException.size() > 0) {
					tvException.setVisibility(View.GONE);
				} else {
					tvException.setVisibility(View.VISIBLE);
				}
			}
		});
		etRecentChartLL.addView(recentlyChart.getView(), -1, -1);
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

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case id.temperature_history_trend_list:
			mdActivity.toEarTemperatureHistoryListFragment(this);
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
				EarTemperatureModule.TYPE);

		Intent intent = new Intent(getActivity(), ShareActivity.class);
		startActivity(intent);
	}
}

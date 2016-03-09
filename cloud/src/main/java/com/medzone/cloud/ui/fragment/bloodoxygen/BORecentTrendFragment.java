package com.medzone.cloud.ui.fragment.bloodoxygen;

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
import com.medzone.cloud.data.helper.MeasureDataUtil;
import com.medzone.cloud.module.modules.BloodOxygenModule;
import com.medzone.cloud.ui.MeasureDataActivity;
import com.medzone.cloud.ui.ShareActivity;
import com.medzone.cloud.ui.adapter.RecentExceptionMeasureDataAdapter;
import com.medzone.cloud.ui.chart.BloodOxygenRecentlyReportChart;
import com.medzone.cloud.ui.chart.IConvertDataListener;
import com.medzone.cloud.ui.dialog.CloudShareDialogFactory;
import com.medzone.framework.data.bean.imp.BaseMeasureData;
import com.medzone.framework.data.bean.imp.BloodOxygen;
import com.medzone.framework.data.bean.imp.ReportEntity;
import com.medzone.framework.fragment.BaseModuleFragment;
import com.medzone.framework.util.TimeUtils;
import com.medzone.mcloud.R;
import com.medzone.mcloud.R.id;
import com.medzone.mcloud.R.layout;

public class BORecentTrendFragment extends BaseModuleFragment implements
		View.OnClickListener {

	private TextView rencentTimeTV;
	private ImageView flagIV;
	private LinearLayout historyListLL, oxygenRecentLL, heartRecentLL;
	private MeasureDataActivity mdActivity;
	private BloodOxygenModule boModule;
	private BloodOxygenRecentlyReportChart oxygenRecentChart;
	private TextView oxygenTV, heartTV;
	private ListView listView;
	private TextView exceptionTV;
	private RecentExceptionMeasureDataAdapter recentExceptionMeasureDataAdapter;

	private List<BaseMeasureData> curXAxisList;
	private List<BaseMeasureData> curXAxisException;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mdActivity = (MeasureDataActivity) activity;
		boModule = (BloodOxygenModule) mdActivity.getAttachModule();
		oxygenRecentChart = new BloodOxygenRecentlyReportChart(mdActivity,
				boModule, -1);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		return initViewRecent(inflater, container);
	}

	private View initViewRecent(LayoutInflater inflater, ViewGroup container) {
		View view = inflater.inflate(layout.fragment_oxygen_history_trend,
				container, false);
		historyListLL = (LinearLayout) view
				.findViewById(R.id.oxygen_history_trend_list);
		oxygenRecentLL = (LinearLayout) view
				.findViewById(R.id.oxygen_history_trend_oxygen_recent_chart);
		heartRecentLL = (LinearLayout) view
				.findViewById(R.id.oxygen_history_trend_heart_recent_chart);
		oxygenTV = (TextView) view
				.findViewById(R.id.oxygen_history_trend_oxygenTV);
		heartTV = (TextView) view
				.findViewById(R.id.oxygen_history_trend_heartTV);
		flagIV = (ImageView) view
				.findViewById(R.id.oxygen_history_trend_flagIV);
		rencentTimeTV = (TextView) view
				.findViewById(R.id.oxygen_history_trend_recent_time);
		listView = (ListView) view
				.findViewById(id.oxygen_history_trend_exception_list);
		exceptionTV = (TextView) view
				.findViewById(R.id.oxygen_history_trend_exception_text);

		PostRecentlyView();
		return view;
	}

	private void PostRecentlyView() {
		historyListLL.setOnClickListener(this);
		oxygenRecentChart.setiSelectedPointListener(new IConvertDataListener() {
			@Override
			public void OnSelected(BaseMeasureData data) {
				fillView(data);
			}

			@Override
			public void OnFirstPointPosition(BaseMeasureData bean) {
				fillView(bean);
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
				listView.setAdapter(recentExceptionMeasureDataAdapter);
				if (exceptions != null && exceptions.size() > 0) {
					exceptionTV.setVisibility(View.GONE);
				} else {
					exceptionTV.setVisibility(View.VISIBLE);
				}
			}
		});
		oxygenRecentLL.removeAllViews();
		oxygenRecentLL.addView(oxygenRecentChart.getOxygenView(), -1, -1);
		heartRecentLL.removeAllViews();
		heartRecentLL.addView(oxygenRecentChart.getHeartView(), -1, -1);
	}

	private void fillView(BaseMeasureData data) {
		BloodOxygen bloodOxygen = (BloodOxygen) data;
		oxygenTV.setText(String.valueOf(bloodOxygen.getOxygen()));
		heartTV.setText(String.valueOf(bloodOxygen.getRate()));
		rencentTimeTV.setText(TimeUtils.getYearToSecond(bloodOxygen
				.getMeasureTime()));
		if (bloodOxygen.getAbnormal() != null) {
			MeasureDataUtil.BloodOxygenFlag(flagIV, bloodOxygen.getAbnormal());
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.oxygen_history_trend_list:
			mdActivity.toBloodOxygenHistoryListFragment(this);
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
				BloodOxygenModule.TYPE);

		Intent intent = new Intent(getActivity(), ShareActivity.class);
		startActivity(intent);
	}
}

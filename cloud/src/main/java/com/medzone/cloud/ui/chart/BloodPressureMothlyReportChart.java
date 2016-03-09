package com.medzone.cloud.ui.chart;

import java.util.ArrayList;
import java.util.List;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;

import com.medzone.cloud.module.modules.BloodPressureModule;
import com.medzone.framework.data.bean.imp.BloodPressure;
import com.medzone.framework.data.bean.imp.BloodPressure.BloodPressureUtil;
import com.medzone.framework.util.TimeUtils;
import com.medzone.mcloud.R;

public class BloodPressureMothlyReportChart extends AbstractChart {

	private Context context;
	private String chartName;
	private String chartDesc;
	private int dbpColor;
	private int sbpColor;
	private BloodPressureModule bpModule;
	private XYMultipleSeriesRenderer renderer;
	private Integer currentYear, currentMonth;

	private XYSeries sbpSeries = new XYSeries("sbp");
	private XYSeries dbpSeries = new XYSeries("dbp");

	private XYSeriesRenderer sbpRenderer = new XYSeriesRenderer();
	private XYSeriesRenderer dbpRenderer = new XYSeriesRenderer();

	private XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
	private GraphicalView view;

	private List<BloodPressure> bpList;
	private List<Integer> timeValueList = new ArrayList<Integer>();
	private boolean isKpa;

	public BloodPressureMothlyReportChart(Context context, String chartName,
			String chartDesc) {
		// TODO add more detail construct
	}

	public BloodPressureMothlyReportChart(Context context,
			BloodPressureModule bpModule, Integer currentYear,
			Integer currentMonth) {

		timeValueList.clear();
		timeValueList.add(currentYear);
		timeValueList.add(currentMonth);

		this.context = context;
		this.bpModule = bpModule;

		isKpa = bpModule.isKpaMode();
		dbpColor = context.getResources()
				.getColor(R.color.chart_ox_trend_nomal);
		sbpColor = context.getResources().getColor(
				R.color.chart_sbp_trend_normal);
		this.currentMonth = currentMonth;
		this.currentYear = currentYear;

	}

	public XYMultipleSeriesRenderer getRenderer() {
		return renderer;
	}

	public void LoadData() {
		bpList = bpModule.getMonthlyLimitData(currentYear, currentMonth);
		for (int i = 0; i < bpList.size(); i++) {
			BloodPressure bp = bpList.get(i);
			dbpSeries.add(TimeUtils.getDay(bp.getMeasureTime()),
					(Double.valueOf(String.valueOf(bp.getHigh())) - 10) / 30);
			sbpSeries.add(TimeUtils.getDay(bp.getMeasureTime()),
					(Double.valueOf(String.valueOf(bp.getLow())) - 10) / 30);
		}
	}

	@Override
	public String getName() {
		return chartName;
	}

	@Override
	public String getDesc() {
		return chartDesc;
	}

	@Override
	public Intent getIntent() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public GraphicalView getView() {

		LoadData();
		int maxDayOfMonth = TimeUtils.getDayCountOfMonth(currentMonth);

		int[] colors = new int[] { sbpColor, dbpColor };
		renderer = buildRenderer(context, colors, null);
		renderer.setPointSize(context.getResources().getDimension(
				R.dimen.chart_month_point_size));

		if (renderer != null && renderer.getSeriesRendererCount() > 1) {
			sbpRenderer = (XYSeriesRenderer) renderer.getSeriesRendererAt(0);
			dbpRenderer = (XYSeriesRenderer) renderer.getSeriesRendererAt(1);
		}

		setChartSettings(renderer, "", "", "", 0d,
				Double.valueOf(maxDayOfMonth), -0.1d, 7.1d, Color.LTGRAY,
				Color.LTGRAY);
		renderer.setApplyBackgroundColor(true);
		renderer.setBackgroundColor(Color.TRANSPARENT);
		renderer.setMarginsColor(Color.WHITE);
		renderer.setOrientation(XYMultipleSeriesRenderer.Orientation.HORIZONTAL);
		renderer.setShowGridX(true);
		renderer.setXAxisMax(maxDayOfMonth);
		renderer.setShowAxes(false);
		renderer.setPanEnabled(true, false);
		renderer.setDisplayValues(true);
		// renderer.setShowCustomTextGrid(true);
		renderer.setPanLimits(new double[] { 1, maxDayOfMonth, 0, 0 });
		renderer.setZoomEnabled(false, false);
		renderer.setShowLegend(false);
		renderer.setXLabels(0);
		renderer.setYLabels(8);

		for (int i = 0; i < 8; i++) {
			if (i == 0 || i == 7) {
				renderer.addYTextLabel(i, "  ");
			} else {

				String ret;
				int mmhgValue = 10 + 30 * i;
				ret = String.valueOf(mmhgValue);
				if (isKpa) {
					float kpaValue = BloodPressureUtil
							.convertMMHG2KPA(mmhgValue);
					ret = String.valueOf(kpaValue);
				}
				renderer.addYTextLabel(i, ret);

			}
		}
		for (int j = 0; j <= maxDayOfMonth; j++) {
			if (j % 2 == 0)
				renderer.addXTextLabel(j, "");
			else
				renderer.addXTextLabel(j, j + "");
		}

		dataset.addSeries(dbpSeries);
		dataset.addSeries(sbpSeries);

		// handle the critical values
		extraHandleSeries(dbpSeries, 7d, 0.33d);
		extraHandleSeries(sbpSeries, 7d, 0.33d);

		if (dbpSeries.getItemCount() < 2 && sbpSeries.getItemCount() < 2
				&& dbpSeries.getItemCount() > 0 && sbpSeries.getItemCount() > 0) {
			sbpRenderer.setPointStyle(PointStyle.CIRCLE);
			dbpRenderer.setPointStyle(PointStyle.CIRCLE);
			sbpRenderer.setFillPoints(true);
			dbpRenderer.setFillPoints(true);

		} else if (sbpSeries.getItemCount() <= 0
				&& dbpSeries.getItemCount() <= 0) {
			// 随便加个点，避免了没有值时折线图显示的异常，对于其他没影响
			sbpSeries.add(-100, 100);
			dbpSeries.add(-100, -100);
			sbpRenderer.setPointStyle(PointStyle.CIRCLE);
			dbpRenderer.setPointStyle(PointStyle.CIRCLE);
		} else {
			sbpRenderer.setPointStyle(PointStyle.POINT);
			dbpRenderer.setPointStyle(PointStyle.POINT);
		}
		view = ChartFactory.getLineChartView(context, dataset, renderer);
		return view;
	}

	public void extraHandleSeries(XYSeries series, double criticalMax,
			double criticalMin) {
		for (int i = 0; i < series.getItemCount(); i++) {
			if (series.getY(i) > criticalMax) {
				double x = series.getX(i);
				series.remove(i);
				series.add(x, criticalMax);
			}

			if (series.getY(i) < criticalMin) {
				double x = series.getX(i);
				series.remove(i);
				series.add(x, criticalMin);
			}
		}
	}

	public List<Integer> getCurDate() {
		return timeValueList;
	}
}

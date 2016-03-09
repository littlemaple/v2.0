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

import com.medzone.cloud.module.modules.EarTemperatureModule;
import com.medzone.framework.data.bean.imp.EarTemperature;
import com.medzone.framework.util.TimeUtils;
import com.medzone.mcloud.R;

public class EarTemperatureMonthlyReportChart extends AbstractChart {

	private Context context;
	private String chartName;
	private String chartDesc;
	private int etColor;
	private EarTemperatureModule etModule;
	private XYMultipleSeriesRenderer renderer;
	private Integer currentYear, currentMonth;

	private XYSeries etSeries = new XYSeries("sbp");

	private XYSeriesRenderer etRenderer = new XYSeriesRenderer();

	private XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
	private GraphicalView view;

	private List<EarTemperature> etList;
	private List<Integer> timeValueList = new ArrayList<Integer>();

	public EarTemperatureMonthlyReportChart(Context context, String chartName,
			String chartDesc) {
		// TODO add more detail construct
	}

	public EarTemperatureMonthlyReportChart(Context context,
			EarTemperatureModule etModule, Integer currentYear,
			Integer currentMonth) {

		timeValueList.clear();
		timeValueList.add(currentYear);
		timeValueList.add(currentMonth);

		this.context = context;
		this.etModule = etModule;
		etColor = context.getResources().getColor(R.color.chart_ox_trend_nomal);
		this.currentMonth = currentMonth;
		this.currentYear = currentYear;
	}

	public XYMultipleSeriesRenderer getRenderer() {
		return renderer;
	}

	public void LoadData() {
		etList = etModule.getMonthlyLimitData(currentYear, currentMonth);
		for (int i = 0; i < etList.size(); i++) {
			EarTemperature et = etList.get(i);
			String time = String.valueOf(et.getMeasureTime());

			etSeries.add(Double.valueOf(time.substring(6, 8)),
					Double.valueOf(et.getTemperature()));
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

		int[] colors = new int[] { etColor };
		renderer = buildRenderer(context, colors, null);
		renderer.setPointSize(context.getResources().getDimension(
				R.dimen.chart_month_point_size));

		if (renderer != null) {
			etRenderer = (XYSeriesRenderer) renderer.getSeriesRendererAt(0);
		}

		setChartSettings(renderer, "", "", "", 0d,
				Double.valueOf(maxDayOfMonth), 35.5d, 39.5d, Color.LTGRAY,
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
		// renderer.setShowLabels(true);
		renderer.setPanLimits(new double[] { 1, maxDayOfMonth, 0, 0 });
		renderer.setZoomEnabled(false, false);
		renderer.setShowLegend(false);
		renderer.setXLabels(0);
		renderer.setYLabels(9);
		for (int i = 0; i < 9; i++) {
			if (i == 0 || i == 8) {
				renderer.addYTextLabel(35.5 + 0.5 * i, "  ");
			} else {
				renderer.addYTextLabel(35.5 + 0.5 * i, 35.5 + 0.5 * i + "℃"
						+ "  ");
			}
		}
		for (int j = 0; j <= maxDayOfMonth; j++) {
			if (j % 2 == 0)
				renderer.addXTextLabel(j, "");
			else
				renderer.addXTextLabel(j, j + "");
		}

		dataset.addSeries(etSeries);

		// handle the critical values
		extraHandleSeries(etSeries, 41, 35);

		if (etSeries.getItemCount() < 2 && etSeries.getItemCount() > 0) {
			etRenderer.setPointStyle(PointStyle.CIRCLE);
			etRenderer.setFillPoints(true);
		} else if (etSeries.getItemCount() <= 0) {
			// 随便加个点，避免了没有值时折线图显示的异常，对于其他没影响
			etSeries.add(-10, 10);
			etRenderer.setPointStyle(PointStyle.POINT);
		} else {
			etRenderer.setPointStyle(PointStyle.POINT);
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
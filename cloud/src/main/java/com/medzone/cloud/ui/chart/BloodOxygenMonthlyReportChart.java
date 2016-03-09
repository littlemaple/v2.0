package com.medzone.cloud.ui.chart;

import java.util.ArrayList;
import java.util.List;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

import com.medzone.cloud.module.modules.BloodOxygenModule;
import com.medzone.framework.data.bean.imp.BloodOxygen;
import com.medzone.framework.util.TimeUtils;
import com.medzone.mcloud.R;
import com.medzone.mcloud.R.color;

public class BloodOxygenMonthlyReportChart extends AbstractChart {

	private XYMultipleSeriesDataset oxygenDataset, heartDataset;
	private XYMultipleSeriesRenderer oxygenRenderer, heartRenderer;
	private GraphicalView oxygenGraph, heartGraph;
	private int monthLastDay;
	private BloodOxygenModule boModule;
	private Context context;
	// 初始化数据
	private double[] oxygenLineX;
	private double[] oxygenLineY;
	private double[] heartLineX;
	private double[] heartLineY;

	private List<BloodOxygen> boLists;
	private List<Integer> timeValueList = new ArrayList<Integer>();

	public BloodOxygenMonthlyReportChart(Context context,
			BloodOxygenModule boModule) {
		this.context = context;
		this.boModule = boModule;
	}

	@Override
	public String getName() {
		return null;
	}

	@Override
	public String getDesc() {
		return null;
	}

	public View getOxygenView(int year, int month, int lastDay) {
		initData(year, month, lastDay);
		timeValueList.clear();
		timeValueList.add(year);
		timeValueList.add(month);

		monthLastDay = lastDay;
		return initOxygenView(context, oxygenLineX, oxygenLineY);
	}

	public View getHeartView() {
		// TODO Auto-generated method stub
		return initHeartView(context, heartLineX, heartLineY);
	}

	private void initData(int year, int month, int lastDay) {
		// 初始化数据
		boLists = boModule.getCacheController()
				.getMonthlyLimitData(year, month);

		int count = boLists != null ? boLists.size() : 0;
		oxygenLineX = new double[count];
		oxygenLineY = new double[count];
		heartLineX = new double[count];
		heartLineY = new double[count];
		for (int i = 0; i < count; i++) {
			double day = TimeUtils.getDay(boLists.get(i).getMeasureTime());
			oxygenLineX[i] = day;
			heartLineX[i] = day;
			int oxygen = boLists.get(i).getOxygen();
			if (oxygen < 75) {
				oxygenLineY[i] = 75;
			} else if (oxygen > 100) {
				oxygenLineY[i] = 100;
			} else {
				oxygenLineY[i] = oxygen;
			}
			int heart = boLists.get(i).getRate();
			double temp = heart / 25.0;
			if (temp < 1) {
				heartLineY[i] = 1;
			} else if (temp > 5) {
				heartLineY[i] = 5;
			} else {
				heartLineY[i] = temp;
			}
		}
	}

	private View initOxygenView(Context context, double[] x, double[] y) {
		List<double[]> lineX = new ArrayList<double[]>();
		List<double[]> lineY = new ArrayList<double[]>();

		PointStyle[] lineStyle;
		if (x != null && x.length == 1) {
			lineStyle = new PointStyle[] { PointStyle.CIRCLE };
		} else if (x != null && x.length <= 0) {
			// 随便加个点，避免了没有值时折线图显示的异常，对于其他没影响
			x = new double[] { -10 };
			y = new double[] { 10 };
			lineStyle = new PointStyle[] { PointStyle.POINT };
		} else {
			lineStyle = new PointStyle[] { PointStyle.POINT };
		}
		lineX.add(x);
		lineY.add(y);

		// 构建折线图
		String[] lineTitle = new String[lineX.size()];
		int[] lineColor = new int[] { context.getResources().getColor(
				color.chart_ox_trend_nomal) };
		oxygenRenderer = buildRenderer(context, lineColor, lineStyle);
		oxygenRenderer.setPointSize(context.getResources().getDimension(
				R.dimen.chart_month_point_size));
		setChartSettings(oxygenRenderer, "", "", "", 1d,
				Double.valueOf(monthLastDay), 72d, 102d, Color.WHITE,
				Color.WHITE);
		setRenderer(oxygenRenderer);
		oxygenRenderer.setXLabelsColor(Color.WHITE);
		oxygenRenderer.setYLabels(6);
		for (int i = 0; i < 6; i++) {
			if (i == 0) {
				oxygenRenderer.addYTextLabel(75 + i, "");
			} else {
				oxygenRenderer.addYTextLabel(75 + i * 5, 75 + i * 5 + "  ");
			}
		}
		oxygenRenderer.setXLabels(monthLastDay / 2);

		if (lineX != null && lineX.size() == 1) {
			XYSeriesRenderer renderer = (XYSeriesRenderer) oxygenRenderer
					.getSeriesRendererAt(0);
			renderer.setFillPoints(true);
		}
		oxygenDataset = buildDataset(lineTitle, lineX, lineY);
		oxygenGraph = ChartFactory.getLineChartView(context, oxygenDataset,
				oxygenRenderer);
		oxygenGraph.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				heartGraph.onTouchEvent(event);
				return false;
			}
		});
		return oxygenGraph;
	}

	private View initHeartView(Context context, double[] x, double[] y) {
		List<double[]> lineX = new ArrayList<double[]>();
		List<double[]> lineY = new ArrayList<double[]>();

		PointStyle[] lineStyle;
		if (x != null && x.length == 1) {
			lineStyle = new PointStyle[] { PointStyle.CIRCLE };
		} else if (x != null && x.length <= 0) {
			// 随便加个点，避免了没有值时折线图显示的异常，对于其他没影响
			x = new double[] { -10 };
			y = new double[] { 10 };
			lineStyle = new PointStyle[] { PointStyle.POINT };
		} else {
			lineStyle = new PointStyle[] { PointStyle.POINT };
		}
		lineX.add(x);
		lineY.add(y);

		// 构建折线图
		String[] lineTitle = new String[lineX.size()];
		int[] lineColor = new int[] { context.getResources().getColor(
				color.chart_heart_trend_nomal) };

		heartRenderer = buildRenderer(context, lineColor, lineStyle);
		heartRenderer.setPointSize(context.getResources().getDimension(
				R.dimen.chart_month_point_size));
		setChartSettings(heartRenderer, "", "", "", 1d,
				Double.valueOf(monthLastDay), 0.5d, 5d, Color.WHITE, Color.WHITE);
		setRenderer(heartRenderer);
		heartRenderer.setYLabels(5);
		for (int i = 0; i < 5; i++) {
			if (i == 0 || i == 4) {
				heartRenderer.addYTextLabel(1 + i, " ");
			} else {
				heartRenderer.addYTextLabel(1 + i, 25 * (1 + i) + "  ");
			}
		}
		heartRenderer.setXLabels(0);
		for (int j = 0; j <= monthLastDay; j++) {
			if (j % 2 == 0)
				heartRenderer.addXTextLabel(j, " ");
			else
				heartRenderer.addXTextLabel(j, j + "");
		}

		if (lineX != null && lineX.size() == 1) {
			XYSeriesRenderer renderer = (XYSeriesRenderer) heartRenderer
					.getSeriesRendererAt(0);
			renderer.setFillPoints(true);
		}
		heartDataset = buildDataset(lineTitle, lineX, lineY);
		heartGraph = ChartFactory.getLineChartView(context, heartDataset,
				heartRenderer);
		heartGraph.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				oxygenGraph.onTouchEvent(event);
				return false;
			}
		});
		return heartGraph;
	}

	private XYMultipleSeriesRenderer setRenderer(
			XYMultipleSeriesRenderer renderer) {
		// 设置是否显示背景色
		renderer.setApplyBackgroundColor(true);
		// 设置背景色
		renderer.setBackgroundColor(Color.TRANSPARENT);
		// 设置报表周边颜色
		renderer.setMarginsColor(Color.WHITE);
		renderer.setOrientation(XYMultipleSeriesRenderer.Orientation.HORIZONTAL);
		renderer.setShowGridX(true);
		renderer.setXAxisMax(monthLastDay);
		renderer.setShowAxes(false);
		renderer.setPanEnabled(true, false);
		renderer.setDisplayValues(true);
		renderer.setPanLimits(new double[] { 1, monthLastDay, 0, 0 });
		renderer.setZoomEnabled(false, false);
		renderer.setShowLegend(false); // 不显示图例
		return renderer;

	}

	@Override
	public Intent getIntent() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public GraphicalView getView() {
		// TODO Auto-generated method stub
		return null;
	}

	public List<Integer> getCurDate() {
		return timeValueList;
	}

}

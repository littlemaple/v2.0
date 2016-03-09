package com.medzone.cloud.ui.chart;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.LineChart;
import org.achartengine.chart.PointStyle;
import org.achartengine.chart.ScatterChart;
import org.achartengine.model.SeriesSelection;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;
import org.achartengine.tools.PanListener;
import org.achartengine.tools.SingleTapListener;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

import com.medzone.cloud.module.modules.BloodOxygenModule;
import com.medzone.framework.data.bean.imp.BaseMeasureData;
import com.medzone.framework.data.bean.imp.BloodOxygen;
import com.medzone.mcloud.R;

public class BloodOxygenRecentlyReportChart extends AbstractChart {

	private Context context;

	private Map<Double, BloodOxygen> allData = new HashMap<Double, BloodOxygen>();

	private final static double MAX_X = -0.5d;
	private final static double MIN_X = -7.5d;
	private final static double OXYGEN_MIN_Y = 72d;
	private final static double OXYGEN_MAX_Y = 102d;
	private final static double HEART_MIN_Y = 0.6d;
	private final static double HEART_MAX_Y = 5.5d;

	private int oxygenNormalColor;
	private int heartNormalColor;
	private int exceptionColor;

	private XYSeries abnormalOxygenSeries = new XYSeries("aOxygen");
	private XYSeries allOxygenSeries = new XYSeries("allOxygen");
	private XYSeries selectedOxygenSeries = new XYSeries("selectOxygen");

	private XYSeries allHeartSeries = new XYSeries("allHeart");
	private XYSeries abnormalHeartSeries = new XYSeries("aHeart");
	private XYSeries selectedHeartSeries = new XYSeries("selectHeart");

	private XYSeriesRenderer selectedOxygenRenderer = new XYSeriesRenderer();
	private XYSeriesRenderer selectedHeartRenderer = new XYSeriesRenderer();

	private XYSeriesRenderer abnormalOxygenRenderer = new XYSeriesRenderer();
	private XYSeriesRenderer abnormalHeartRenderer = new XYSeriesRenderer();

	private XYMultipleSeriesRenderer oxygenRenderer, heartRenderer;
	private XYMultipleSeriesDataset oxygenDataset, heartDataset;

	private BloodOxygenModule boModule;
	private GraphicalView oxygenView, heartView;
	private Integer recordId;

	private boolean isAdd = true;

	private IConvertDataListener iConvertDataListener;

	public void setiSelectedPointListener(
			IConvertDataListener iConvertDataListener) {
		this.iConvertDataListener = iConvertDataListener;
	}

	public BloodOxygenRecentlyReportChart(Context context,
			BloodOxygenModule boModule, Integer recordId) {
		this.context = context;

		this.boModule = boModule;
		oxygenNormalColor = context.getResources().getColor(
				R.color.chart_ox_trend_nomal);
		heartNormalColor = context.getResources().getColor(
				R.color.chart_heart_trend_nomal);
		exceptionColor = context.getResources().getColor(
				R.color.point_bp_history_abnormal);
		this.recordId = recordId;
	}

	@Override
	public String getName() {
		return null;
	}

	@Override
	public String getDesc() {
		return null;
	}

	@Override
	public Intent getIntent() {
		return null;
	}

	public Integer oxygneCriticalMethod(Integer value) {
		if (value == null) {
			return null;
		}
		if (value > 100)
			return 100;
		if (value < 75)
			return 75;
		return value;

	}

	public double heartCriticalMethod(Integer value) {
		if (value == null) {
			return (Double) null;
		}
		double heart = value / 25.0;
		if (heart > 5)
			return 5;
		if (heart < 1)
			return 1;
		return heart;

	}

	public void preLoadData(boolean isReset) {
		List<BloodOxygen> boList = boModule.getCacheController()
				.readDownPageFromLocalWithoutCache(isReset);
		if (boList == null) {
			if (oxygenRenderer != null)
				oxygenRenderer.setPanLimits(new double[] {
						allOxygenSeries.getMinX() - 1, 0, 0, 0 });
			if (heartRenderer != null)
				heartRenderer.setPanLimits(new double[] {
						allHeartSeries.getMinX() - 1, 0, 0, 0 });
			isAdd = false;
			// 若无数据，模拟一条数据
			BloodOxygen bo = new BloodOxygen();
			bo.setOxygen(100);
			bo.setRate(100);
			allData.put(-100d, bo);
			Integer oxygen = oxygneCriticalMethod(bo.getOxygen());
			Double rate = heartCriticalMethod(bo.getRate());
			if (oxygen != null && rate != null) {
				allOxygenSeries.add(-100d, oxygen);
				allHeartSeries.add(-100d, rate);
			}
		} else {
			double index = 0;
			if (boList.size() < 7)
				index = -7 + boList.size();
			for (BloodOxygen bo : boList) {
				index--;
				if (isAdd) {
					if (recordId == null
							|| recordId.intValue() == -1
							|| recordId.intValue() == 0
							|| bo.getRecordID().intValue() == recordId
									.intValue()) {
						loadRecentlySelectedData(index, bo);
						isAdd = false;
					} else {
						isAdd = false;
					}

				}
				allData.put(index, bo);
				Integer oxygen = oxygneCriticalMethod(bo.getOxygen());
				Double rate = heartCriticalMethod(bo.getRate());
				if (oxygen != null && rate != null) {
					allOxygenSeries.add(index, oxygen);
					allHeartSeries.add(index, rate);
					if (bo.getAbnormal() != BloodOxygen.OXYGEN_STATE_IDEAL) {
						abnormalOxygenSeries.add(index, oxygen);
						abnormalHeartSeries.add(index, rate);
					}
				}
			}
		}
		List<BaseMeasureData> curXAxisList = getCurXAxisList(-7.5d, -0.5d);
		List<BaseMeasureData> curXAxisExceptions = getExceptionAtRange(-7.5d,
				-0.5d);
		iConvertDataListener.ConvertExceptionList(curXAxisList,
				curXAxisExceptions);
	}

	/**
	 * get exception between xValue1 and xValue2
	 * 
	 * @param min
	 * @param max
	 * @return
	 */
	public List<BaseMeasureData> getExceptionAtRange(double min, double max) {
		List<BaseMeasureData> exceptions = new ArrayList<BaseMeasureData>();

		Iterator<?> iter = allData.entrySet().iterator();
		while (iter.hasNext()) {
			Entry<?, ?> entry = (Entry<?, ?>) iter.next();
			double key = (Double) entry.getKey();
			if (key > min && key < max) {
				BloodOxygen bo = (BloodOxygen) entry.getValue();
				if (bo.getAbnormal() != BloodOxygen.OXYGEN_STATE_IDEAL) {
					exceptions.add(bo);
				}
			}
		}
		return exceptions;
	}

	public List<BaseMeasureData> getCurXAxisList(double min, double max) {
		List<BaseMeasureData> alls = new ArrayList<BaseMeasureData>();

		Iterator<?> iter = allData.entrySet().iterator();
		while (iter.hasNext()) {
			Entry<?, ?> entry = (Entry<?, ?>) iter.next();
			double key = (Double) entry.getKey();
			if (key > min && key < max) {
				BloodOxygen bo = (BloodOxygen) entry.getValue();
				alls.add(bo);
			}
		}
		return alls;
	}

	public void loadRecentlySelectedData(double index, BloodOxygen bo) {

		selectedOxygenSeries.add(index, oxygneCriticalMethod(bo.getOxygen()));
		selectedHeartSeries.add(index, heartCriticalMethod(bo.getRate()));
		if (bo.getAbnormal() != BloodOxygen.OXYGEN_STATE_IDEAL) {
			selectedOxygenRenderer.setColor(exceptionColor);
			selectedHeartRenderer.setColor(exceptionColor);
		} else {
			selectedOxygenRenderer.setColor(oxygenNormalColor);
			selectedHeartRenderer.setColor(heartNormalColor);
		}
		iConvertDataListener.OnFirstPointPosition(bo);
	}

	public void LoadData() {
		List<BloodOxygen> boList = boModule.getCacheController()
				.readDownPageFromLocalWithoutCache(false);
		if (boList == null) {
			if (oxygenRenderer != null)
				oxygenRenderer.setPanLimits(new double[] {
						allOxygenSeries.getMinX() - 1, 0, 0, 0 });
			if (heartRenderer != null)
				heartRenderer.setPanLimits(new double[] {
						allHeartSeries.getMinX() - 1, 0, 0, 0 });
			isAdd = false;
		} else {
			double index = allOxygenSeries.getMinX();
			for (BloodOxygen bo : boList) {
				index--;
				if (isAdd) {
					// TODO暂时处理
					if (recordId == null
							|| recordId.intValue() == -1
							|| bo.getRecordID().intValue() == recordId
									.intValue()) {
						loadRecentlySelectedData(index, bo);
						isAdd = false;
					} else {
						isAdd = false;
					}

				}
				allData.put(index, bo);
				Integer oxygen = oxygneCriticalMethod(bo.getOxygen());
				Double rate = heartCriticalMethod(bo.getRate());
				if (oxygen != null && rate != null) {
					allOxygenSeries.add(index, oxygen);
					allHeartSeries.add(index, rate);
					if (bo.getAbnormal() != BloodOxygen.OXYGEN_STATE_IDEAL) {
						abnormalOxygenSeries.add(index, oxygen);
						abnormalHeartSeries.add(index, rate);
					}
				}
			}
		}
	}

	public void initChart() {

	}

	@Override
	public GraphicalView getView() {
		return null;

	}

	public GraphicalView getOxygenView() {

		int[] colors = new int[] { oxygenNormalColor };
		PointStyle[] styles = new PointStyle[] { PointStyle.CIRCLE };
		oxygenRenderer = buildRenderer(context, colors, styles);
		int length = oxygenRenderer.getSeriesRendererCount();

		for (int i = 0; i < length; i++) {
			XYSeriesRenderer r = (XYSeriesRenderer) oxygenRenderer
					.getSeriesRendererAt(i);
			r.setFillPoints(false);
		}
		setChartSettings(oxygenRenderer, "", "", "", MIN_X, MAX_X,
				OXYGEN_MIN_Y, OXYGEN_MAX_Y, Color.LTGRAY, Color.LTGRAY);

		oxygenRenderer.setBackgroundColor(Color.WHITE);
		oxygenRenderer.setMarginsColor(Color.WHITE);
		oxygenRenderer
				.setOrientation(XYMultipleSeriesRenderer.Orientation.HORIZONTAL);
		oxygenRenderer.setShowGridX(true);
		oxygenRenderer.setShowAxes(false);
		oxygenRenderer.setXLabelsColor(Color.WHITE);
		oxygenRenderer.setPanEnabled(true, false);
		oxygenRenderer.setPanLimits(new double[] { -999999, 0, 0, 0 });
		oxygenRenderer.setZoomEnabled(false, false);
		oxygenRenderer.setShowLegend(false);
		oxygenRenderer.setSelectableBuffer(context.getResources().getDimension(
				R.dimen.chart_point_select_buffer));
		oxygenRenderer.setYLabels(6);
		for (int i = 0; i < 6; i++) {
			if (i == 0) {
				oxygenRenderer.addYTextLabel(75, " ");
			} else {
				oxygenRenderer.addYTextLabel(i * 5 + 75, i * 5 + 75 + " ");
			}
		}

		abnormalOxygenRenderer.setColor(exceptionColor);
		abnormalOxygenRenderer.setPointStyle(PointStyle.CIRCLE);

		selectedOxygenSeries.setStatus(XYSeries.SELECTED_STATUS);
		selectedOxygenSeries.setSelectSize(context.getResources().getDimension(
				R.dimen.chart_select_point_size));
		// selectedOxygenRenderer.setColor(oxygenNormalColor);
		selectedOxygenRenderer.setPointStyle(PointStyle.CIRCLE);
		selectedOxygenRenderer.setFillPoints(true);

		oxygenDataset = new XYMultipleSeriesDataset();
		oxygenDataset.addSeries(allOxygenSeries);
		oxygenDataset.addSeries(abnormalOxygenSeries);
		oxygenDataset.addSeries(selectedOxygenSeries);
		oxygenRenderer.addSeriesRenderer(abnormalOxygenRenderer);
		oxygenRenderer.addSeriesRenderer(selectedOxygenRenderer);
		String[] types = new String[] { LineChart.TYPE, ScatterChart.TYPE,
				ScatterChart.TYPE, };
		preLoadData(true);
		while (isAdd) {
			preLoadData(false);
		}

		oxygenView = ChartFactory.getCombinedXYChartView(context,
				oxygenDataset, oxygenRenderer, types);

		oxygenView.addSingleTapListener(new SingleTapListener() {
			@Override
			public void onSingleTap(MotionEvent e) {

				SeriesSelection point = oxygenView.getCurrentSeriesAndPoint();
				if (point != null) {
					BloodOxygen temp = allData.get(point.getXValue());
					selectedOxygenSeries.clear();
					selectedOxygenSeries.add(point.getXValue(),
							oxygneCriticalMethod(temp.getOxygen()));
					selectedHeartSeries.clear();
					selectedHeartSeries.add(point.getXValue(),
							heartCriticalMethod(temp.getRate()));
					iConvertDataListener.OnSelected(temp);
					if (temp.getAbnormal() != BloodOxygen.OXYGEN_STATE_IDEAL) {
						selectedOxygenRenderer.setColor(exceptionColor);
						selectedHeartRenderer.setColor(exceptionColor);
					} else {
						selectedOxygenRenderer.setColor(oxygenNormalColor);
						selectedHeartRenderer.setColor(heartNormalColor);
					}
					heartView.repaint();
					oxygenView.repaint();
				}
			}

		});
		oxygenView.addPanListener(new myPanListener());
		oxygenView.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				heartView.onTouchEvent(event);
				return false;
			}
		});
		return oxygenView;
	}

	public GraphicalView getHeartView() {
		int[] colors = new int[] { heartNormalColor };
		PointStyle[] styles = new PointStyle[] { PointStyle.CIRCLE };
		heartRenderer = buildRenderer(context, colors, styles);
		int length = heartRenderer.getSeriesRendererCount();

		for (int i = 0; i < length; i++) {
			XYSeriesRenderer r = (XYSeriesRenderer) heartRenderer
					.getSeriesRendererAt(i);
			r.setFillPoints(false);
		}
		setChartSettings(heartRenderer, "", "", "", MIN_X, MAX_X, HEART_MIN_Y,
				HEART_MAX_Y, Color.LTGRAY, Color.LTGRAY);

		heartRenderer.setBackgroundColor(Color.WHITE);
		heartRenderer.setMarginsColor(Color.WHITE);
		heartRenderer
				.setOrientation(XYMultipleSeriesRenderer.Orientation.HORIZONTAL);
		heartRenderer.setShowGridX(true);
		heartRenderer.setShowAxes(false);
		heartRenderer.setXLabelsColor(Color.WHITE);
		heartRenderer.setPanEnabled(true, false);
		heartRenderer.setPanLimits(new double[] { -999999, 0, 0, 0 });
		heartRenderer.setZoomEnabled(false, false);
		heartRenderer.setShowLegend(false);
		oxygenRenderer.setSelectableBuffer(context.getResources().getDimension(
				R.dimen.chart_point_select_buffer));
		heartRenderer.setYLabels(5);
		for (int i = 0; i < 5; i++) {
			if (i == 0 || i == 4) {
				heartRenderer.addYTextLabel(1 + i, "  ");
			} else {
				heartRenderer.addYTextLabel(1 + i, (1 + i) * 25 + "  ");
			}
		}

		abnormalHeartRenderer.setColor(exceptionColor);
		abnormalHeartRenderer.setPointStyle(PointStyle.CIRCLE);

		selectedHeartSeries.setStatus(XYSeries.SELECTED_STATUS);
		selectedHeartSeries.setSelectSize(context.getResources().getDimension(
				R.dimen.chart_select_point_size));
		// selectedHeartRenderer.setColor(heartNormalColor);
		selectedHeartRenderer.setPointStyle(PointStyle.CIRCLE);
		selectedHeartRenderer.setFillPoints(true);

		heartDataset = new XYMultipleSeriesDataset();
		heartDataset.addSeries(allHeartSeries);
		heartDataset.addSeries(abnormalHeartSeries);
		heartDataset.addSeries(selectedHeartSeries);
		heartRenderer.addSeriesRenderer(abnormalHeartRenderer);
		heartRenderer.addSeriesRenderer(selectedHeartRenderer);
		String[] types = new String[] { LineChart.TYPE, ScatterChart.TYPE,
				ScatterChart.TYPE };
		preLoadData(true);
		while (isAdd) {
			preLoadData(false);
		}

		heartView = ChartFactory.getCombinedXYChartView(context, heartDataset,
				heartRenderer, types);

		heartView.addSingleTapListener(new SingleTapListener() {
			@Override
			public void onSingleTap(MotionEvent e) {

				SeriesSelection point = heartView.getCurrentSeriesAndPoint();
				if (point != null) {
					BloodOxygen temp = allData.get(point.getXValue());
					selectedOxygenSeries.clear();
					selectedOxygenSeries.add(point.getXValue(),
							oxygneCriticalMethod(temp.getOxygen()));
					selectedHeartSeries.clear();
					selectedHeartSeries.add(point.getXValue(),
							heartCriticalMethod(temp.getRate()));
					iConvertDataListener.OnSelected(temp);
					if (temp.isHealthState()) {
						selectedOxygenRenderer.setColor(oxygenNormalColor);
						selectedHeartRenderer.setColor(heartNormalColor);
					} else {
						selectedOxygenRenderer.setColor(exceptionColor);
						selectedHeartRenderer.setColor(exceptionColor);
					}
					heartView.repaint();
					oxygenView.repaint();
				}
			}

		});
		heartView.addPanListener(new myPanListener());
		heartView.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				oxygenView.onTouchEvent(event);
				return false;
			}
		});
		return heartView;
	}

	class myPanListener implements PanListener {

		@Override
		public void panApplied() {
			LoadData();
			heartView.repaint();
			oxygenView.repaint();
			// x轴当前min
			double min = oxygenRenderer.getXAxisMin();
			// y轴当前max
			double max = oxygenRenderer.getXAxisMax();

			List<BaseMeasureData> curXAxisList = getCurXAxisList(min, max);
			List<BaseMeasureData> curXAxisExceptions = getExceptionAtRange(min,
					max);
			iConvertDataListener.ConvertExceptionList(curXAxisList,
					curXAxisExceptions);
		}
	}
}
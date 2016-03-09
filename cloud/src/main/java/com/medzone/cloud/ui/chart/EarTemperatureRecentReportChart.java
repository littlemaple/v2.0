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

import com.medzone.cloud.module.modules.EarTemperatureModule;
import com.medzone.framework.data.bean.imp.BaseMeasureData;
import com.medzone.framework.data.bean.imp.EarTemperature;
import com.medzone.mcloud.R;

public class EarTemperatureRecentReportChart extends AbstractChart {

	private Context context;
	private Map<Double, EarTemperature> memoryMap = new HashMap<Double, EarTemperature>();
	private EarTemperatureModule etModule;

	private int normalColor;
	private int exceptionColor;

	private XYSeries completeSeries = new XYSeries("complete data");
	private XYSeries abnormalSeries = new XYSeries("abnormal data");
	private XYSeries selectedPointSeries = new XYSeries("selected point data");

	private XYMultipleSeriesRenderer renderer;// for all
	private XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();

	private XYSeriesRenderer abnormalRenderer;
	private XYSeriesRenderer selectedPointRenderer;

	private Integer recordId;
	private boolean isAdd = true;

	private GraphicalView view;
	private IConvertDataListener iConvertDataListener;

	public void setiSelectedPointListener(
			IConvertDataListener iConvertDataListener) {
		this.iConvertDataListener = iConvertDataListener;
	}

	public EarTemperatureRecentReportChart(Context context,
			EarTemperatureModule etModule) {
		this.context = context;
		this.etModule = etModule;
		normalColor = context.getResources().getColor(
				R.color.chart_ox_trend_nomal);
		exceptionColor = context.getResources().getColor(
				R.color.point_bp_history_abnormal);
	}

	public EarTemperatureRecentReportChart(Context context,
			EarTemperatureModule etModule, Integer recordId) {
		this.context = context;
		this.etModule = etModule;
		normalColor = context.getResources().getColor(
				R.color.chart_ox_trend_nomal);
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

	public Float handleCriticalMethod(Float value) {
		if (value == null) {
			return null;
		}
		if (value > 41)
			return 41f;
		if (value < 35)
			return 35f;
		return value;

	}

	public void preLoadData(boolean isReset) {
		List<EarTemperature> etList = etModule.getCacheController()
				.readDownPageFromLocalWithoutCache(isReset);
		if (etList == null) {
			if (renderer != null)
				renderer.setPanLimits(new double[] {
						completeSeries.getMinX() - 1, 0, 0, 0 });
			isAdd = false;
		} else {
			double index = 0;
			if (etList.size() < 7)
				index = -7 + etList.size();
			for (EarTemperature et : etList) {
				index--;
				if (isAdd) {
					// TODO暂时处理
					if (recordId == null || recordId == -1 || recordId == 0
							|| et.getRecordID().intValue() == recordId) {
						loadRecentlySelectedData(index, et);
						isAdd = false;
					} else {
						isAdd = false;
					}

				}
				memoryMap.put(index, et);
				Float temperature = handleCriticalMethod(et.getTemperature());
				if (temperature != null) {
					completeSeries.add(index, temperature);
					if (et.getAbnormal() != EarTemperature.TEMPERATURE_STATE_NORMAL) {
						abnormalSeries.add(index, temperature);
					}
				}
			}
		}
		List<BaseMeasureData> list = getExceptionAtRange(-7.5d, -0.5d);
		List<BaseMeasureData> all = getAllAtRange(-7.5d, -0.5d);
		iConvertDataListener.ConvertExceptionList(all, list);
	}

	/**
	 * get exception between xValue1 and xValue2
	 * 
	 * @param min
	 * @param max
	 * @return
	 */
	public List<BaseMeasureData> getExceptionAtRange(double min, double max) {
		List<BaseMeasureData> list = new ArrayList<BaseMeasureData>();

		Iterator<?> iter = memoryMap.entrySet().iterator();
		while (iter.hasNext()) {
			Entry<?, ?> entry = (Entry<?, ?>) iter.next();
			double key = (Double) entry.getKey();
			if (key > min && key < max) {
				EarTemperature et = (EarTemperature) entry.getValue();
				if (et.getAbnormal() != EarTemperature.TEMPERATURE_STATE_NORMAL) {
					list.add(et);
				}
			}
		}
		return list;
	}

	public List<BaseMeasureData> getAllAtRange(double min, double max) {
		List<BaseMeasureData> list = new ArrayList<BaseMeasureData>();

		Iterator<?> iter = memoryMap.entrySet().iterator();
		while (iter.hasNext()) {
			Entry<?, ?> entry = (Entry<?, ?>) iter.next();
			double key = (Double) entry.getKey();
			if (key > min && key < max) {
				EarTemperature et = (EarTemperature) entry.getValue();
				list.add(et);
			}
		}
		return list;
	}

	public void loadRecentlySelectedData(double index, EarTemperature et) {

		selectedPointSeries.add(index, et.getTemperature());
		if (et.getAbnormal() != EarTemperature.TEMPERATURE_STATE_NORMAL) {
			selectedPointRenderer.setColor(exceptionColor);
		} else {
			selectedPointRenderer.setColor(normalColor);
		}
		iConvertDataListener.OnFirstPointPosition(et);
	}

	public void LoadData() {
		List<EarTemperature> etList = etModule.getCacheController()
				.readDownPageFromLocalWithoutCache(false);
		if (etList == null) {
			if (renderer != null)
				renderer.setPanLimits(new double[] { completeSeries.getMinX(),
						0, 0, 0 });
		} else {
			double index = completeSeries.getMinX();
			for (EarTemperature et : etList) {
				index--;
				if (isAdd) {
					// TODO暂时处理
					if (recordId == null
							|| recordId == -1
							|| et.getRecordID().intValue() == recordId
									.intValue()) {
						loadRecentlySelectedData(index, et);
						isAdd = false;
					} else {
						isAdd = false;
					}

				}
				memoryMap.put(index, et);
				Float temperature = handleCriticalMethod(et.getTemperature());
				if (temperature != null) {
					completeSeries.add(index, temperature);
					if (et.getAbnormal() != EarTemperature.TEMPERATURE_STATE_NORMAL) {
						abnormalSeries.add(index, temperature);
					}
				}
			}
		}
	}

	@Override
	public GraphicalView getView() {

		// builder renderer
		if (renderer == null) {
			int[] colors = new int[] { normalColor };
			PointStyle[] styles = new PointStyle[] { PointStyle.CIRCLE };
			renderer = buildRenderer(context, colors, styles);

			int length = renderer.getSeriesRendererCount();
			for (int i = 0; i < length; i++) {
				XYSeriesRenderer r = (XYSeriesRenderer) renderer
						.getSeriesRendererAt(i);
				r.setFillPoints(false);
			}
			setChartSettings(renderer, "", "", "", -7.5d, -0.5d, 35.3d, 39.7d,
					Color.LTGRAY, Color.LTGRAY);

			renderer.setBackgroundColor(Color.WHITE);
			renderer.setMarginsColor(Color.WHITE);
			renderer.setOrientation(XYMultipleSeriesRenderer.Orientation.HORIZONTAL);
			renderer.setShowGridX(true);
			renderer.setSelectableBuffer(50);
			renderer.setShowAxes(false);
			renderer.setXLabelsColor(Color.WHITE);
			renderer.setPanEnabled(true, false);
			renderer.setPanLimits(new double[] { -999999, 0, 0, 0 });
			renderer.setZoomEnabled(false, false);
			renderer.setShowLegend(false);
			renderer.setYLabels(9);
			for (int i = 0; i < 9; i++) {
				if (i == 0 || i == 8) {
					renderer.addYTextLabel(35.5 + 0.5 * i, "  ");
				} else {
					renderer.addYTextLabel(35.5 + 0.5 * i, 35.5 + 0.5 * i + "℃"
							+ "  ");
				}
			}

			if (abnormalRenderer == null) {
				abnormalRenderer = new XYSeriesRenderer();
				abnormalRenderer.setColor(exceptionColor);
				abnormalRenderer.setPointStyle(PointStyle.CIRCLE);
			}

			if (selectedPointRenderer == null) {
				selectedPointRenderer = new XYSeriesRenderer();
				selectedPointRenderer.setColor(normalColor);
				selectedPointRenderer.setPointStyle(PointStyle.CIRCLE);
				selectedPointRenderer.setFillPoints(true);
			}

			selectedPointSeries.setStatus(XYSeries.SELECTED_STATUS);
			dataset.addSeries(completeSeries); // 所有
			dataset.addSeries(abnormalSeries);// 异常
			dataset.addSeries(selectedPointSeries);// 选中
			renderer.addSeriesRenderer(abnormalRenderer);// 异常
			renderer.addSeriesRenderer(selectedPointRenderer);// 选中

			preLoadData(true);
			while (isAdd) {
				preLoadData(false);
			}
			String[] types = new String[] { LineChart.TYPE, ScatterChart.TYPE,
					ScatterChart.TYPE };
			view = ChartFactory.getCombinedXYChartView(context, dataset,
					renderer, types);
			view.addSingleTapListener(singleTapListener);
			view.addPanListener(panListener);
		}
		return view;
	}

	private SingleTapListener singleTapListener = new SingleTapListener() {

		@Override
		public void onSingleTap(MotionEvent e) {

			SeriesSelection point = view.getCurrentSeriesAndPoint();
			if (point != null) {
				EarTemperature et = memoryMap.get(point.getXValue());
				selectedPointSeries.clear();
				selectedPointSeries.add(point.getXValue(), et.getTemperature());
				selectedPointRenderer.setColor(et.isHealthState() ? normalColor
						: exceptionColor);
				iConvertDataListener.OnSelected(et);
				view.repaint();
			}
		}
	};

	private PanListener panListener = new PanListener() {

		@Override
		public void panApplied() {
			LoadData();
			view.repaint();
			double min = renderer.getXAxisMin();
			double max = renderer.getXAxisMax();
			List<BaseMeasureData> all = getAllAtRange(min, max);
			List<BaseMeasureData> list = getExceptionAtRange(min, max);
			iConvertDataListener.ConvertExceptionList(all, list);
		}
	};

}

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

import com.medzone.cloud.module.modules.BloodPressureModule;
import com.medzone.framework.data.bean.imp.BaseMeasureData;
import com.medzone.framework.data.bean.imp.BloodPressure;
import com.medzone.framework.data.bean.imp.BloodPressure.BloodPressureUtil;
import com.medzone.mcloud.R;

public class BloodPressureRecentlyReportChart extends AbstractChart {

	private Context context;

	private Map<Double, BloodPressure> allData = new HashMap<Double, BloodPressure>();

	private int normalColor;
	private int exceptionColor;

	private XYSeries abnormalDbpSeries = new XYSeries("adbp");
	private XYSeries abnormalSbpSeries = new XYSeries("asbp");
	private XYSeries allDbpSeries = new XYSeries("allDbp");
	private XYSeries allSbpSeries = new XYSeries("allSbp");
	private XYSeries selectedDbpSeries = new XYSeries("selectDbp");
	private XYSeries selectedSbpSeries = new XYSeries("selectSbp");

	private XYSeriesRenderer selectedDbpRenderer = new XYSeriesRenderer();
	private XYSeriesRenderer selectedSbpRenderer = new XYSeriesRenderer();

	private XYSeriesRenderer abnormalDbpRenderer = new XYSeriesRenderer();
	private XYSeriesRenderer abnormalSopRenderer = new XYSeriesRenderer();

	private XYMultipleSeriesRenderer renderer;
	private XYMultipleSeriesDataset dataset;

	private BloodPressureModule bpModule;
	private Integer recordId;

	private boolean isAdd = true;
	private boolean isKpa;

	private IConvertDataListener iConvertDataListener;

	public void setiSelectedPointListener(
			IConvertDataListener iConvertDataListener) {
		this.iConvertDataListener = iConvertDataListener;
	}

	public BloodPressureRecentlyReportChart(Context context,
			BloodPressureModule bpModule) {
		this.context = context;
		this.bpModule = bpModule;
		isKpa = bpModule.isKpaMode();
		normalColor = context.getResources().getColor(
				R.color.chart_ox_trend_nomal);
		exceptionColor = context.getResources().getColor(
				R.color.point_bp_history_abnormal);
	}

	public BloodPressureRecentlyReportChart(Context context,
			BloodPressureModule bpModule, Integer recordId) {
		this.context = context;
		this.bpModule = bpModule;
		isKpa = bpModule.isKpaMode();
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

	public double handleCriticalMethod(Integer value) {
		if (value == null) {
			return 0;
		}
		double result = (value - 10) / 30d;
		if (result > 7)
			return 7;
		if (result < 0.33)
			return 0.33;
		return result;

	}

	public void preLoadData(boolean isReset) {
		List<BloodPressure> bpList = bpModule.getDownPageLocalData(isReset);
		if (bpList == null) {
			if (renderer != null)
				renderer.setPanLimits(new double[] {
						allDbpSeries.getMinX() - 1, 0, 0, 0 });
			isAdd = false;

			// 若无数据，模拟一条数据
			BloodPressure bp = new BloodPressure();
			bp.setAbnormal(0);
			bp.setHigh(150f);
			bp.setLow(150f);
			allData.put(-100d, bp);
			double high = handleCriticalMethod(bp.getHigh().intValue());
			double low = handleCriticalMethod(bp.getLow().intValue());
			allDbpSeries.add(-100d, high);
			allSbpSeries.add(-100d, low);
		} else {
			double index = 0;
			if (bpList.size() < 7)
				index = -7 + bpList.size();
			for (BloodPressure bp : bpList) {
				index--;
				if (isAdd) {
					// TODO暂时处理
					if (recordId == null
							|| recordId.intValue() == -1
							|| recordId.intValue() == 0
							|| bp.getRecordID().intValue() == recordId
									.intValue()) {
						loadRecentlySelectedData(index, bp);
						isAdd = false;
					} else {
						isAdd = false;
					}

				}
				allData.put(index, bp);
				double high = handleCriticalMethod(bp.getHigh().intValue());
				double low = handleCriticalMethod(bp.getLow().intValue());
				allDbpSeries.add(index, high);
				allSbpSeries.add(index, low);
				if (!bp.isHealthState()) {
					abnormalDbpSeries.add(index, high);
					abnormalSbpSeries.add(index, low);
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

		Iterator<?> iter = allData.entrySet().iterator();
		while (iter.hasNext()) {
			Entry<?, ?> entry = (Entry<?, ?>) iter.next();
			double key = (Double) entry.getKey();
			if (key > min && key < max) {
				BloodPressure bp = (BloodPressure) entry.getValue();
				if (!bp.isHealthState()) {
					list.add(bp);
				}
			}
		}
		return list;
	}

	/**
	 * get all data between min and max
	 * 
	 * @param min
	 * @param max
	 * @return
	 */
	public List<BaseMeasureData> getAllAtRange(double min, double max) {
		List<BaseMeasureData> list = new ArrayList<BaseMeasureData>();

		Iterator<?> iter = allData.entrySet().iterator();
		while (iter.hasNext()) {
			Entry<?, ?> entry = (Entry<?, ?>) iter.next();
			double key = (Double) entry.getKey();
			if (key > min && key < max) {
				BloodPressure bp = (BloodPressure) entry.getValue();
				list.add(bp);
			}
		}
		return list;
	}

	public void loadRecentlySelectedData(double index, BloodPressure bp) {

		selectedDbpSeries.add(index, handleCriticalMethod(bp.getLow()
				.intValue()));
		selectedSbpSeries.add(index, handleCriticalMethod(bp.getHigh()
				.intValue()));
		if (!bp.isHealthState()) {
			selectedDbpRenderer.setColor(exceptionColor);
			selectedSbpRenderer.setColor(exceptionColor);
		} else {
			selectedDbpRenderer.setColor(normalColor);
			selectedSbpRenderer.setColor(normalColor);
		}
		iConvertDataListener.OnFirstPointPosition(bp);
	}

	public void LoadData() {
		List<BloodPressure> bpList = bpModule.getCacheController()
				.readDownPageFromLocalWithoutCache(false);
		if (bpList == null) {
			if (renderer != null)
				renderer.setPanLimits(new double[] {
						allDbpSeries.getMinX() - 1, 0, 0, 0 });
			isAdd = false;
		} else {
			double index = allDbpSeries.getMinX();
			for (BloodPressure bp : bpList) {
				index--;
				if (isAdd) {
					// TODO暂时处理
					if (recordId == null
							|| recordId.intValue() == -1
							|| bp.getRecordID().intValue() == recordId
									.intValue()) {
						loadRecentlySelectedData(index, bp);
						isAdd = false;
					} else {
						isAdd = false;
					}

				}
				allData.put(index, bp);
				double high = handleCriticalMethod(bp.getHigh().intValue());
				double low = handleCriticalMethod(bp.getLow().intValue());
				if (high > 0 && low > 0) {
					allDbpSeries.add(index, high);
					allSbpSeries.add(index, low);
					if (!bp.isHealthState()) {
						abnormalDbpSeries.add(index, high);
						abnormalSbpSeries.add(index, low);
					}
				}
			}
		}
	}

	@Override
	public GraphicalView getView() {

		// allData.clear();

		int[] colors = new int[] { normalColor, normalColor };
		PointStyle[] styles = new PointStyle[] { PointStyle.CIRCLE,
				PointStyle.CIRCLE };
		renderer = buildRenderer(context, colors, styles);
		int length = renderer.getSeriesRendererCount();

		for (int i = 0; i < length; i++) {
			XYSeriesRenderer r = (XYSeriesRenderer) renderer
					.getSeriesRendererAt(i);
			r.setFillPoints(false);
		}
		setChartSettings(renderer, "", "", "", -7.5d, -0.5d, -0.1d, 7.5d,
				Color.LTGRAY, Color.LTGRAY);

		renderer.setBackgroundColor(Color.WHITE);
		renderer.setMarginsColor(Color.WHITE);
		renderer.setOrientation(XYMultipleSeriesRenderer.Orientation.HORIZONTAL);
		renderer.setShowGridX(true);
		renderer.setShowAxes(false);
		renderer.setXLabelsColor(Color.WHITE);
		renderer.setPanEnabled(true, false);
		renderer.setPanLimits(new double[] { -999999, 0, 0, 0 });
		renderer.setZoomEnabled(false, false);
		renderer.setShowLegend(false);
		renderer.setSelectableBuffer(context.getResources().getDimension(
				R.dimen.chart_point_select_buffer));
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

		abnormalDbpRenderer.setColor(exceptionColor);
		abnormalDbpRenderer.setPointStyle(PointStyle.CIRCLE);

		abnormalSopRenderer.setColor(exceptionColor);
		abnormalSopRenderer.setPointStyle(PointStyle.CIRCLE);

		selectedDbpSeries.setStatus(XYSeries.SELECTED_STATUS);
		selectedDbpSeries.setSelectSize(context.getResources().getDimension(
				R.dimen.chart_select_point_size));
		selectedSbpRenderer.setColor(normalColor);
		selectedDbpRenderer.setPointStyle(PointStyle.CIRCLE);
		selectedDbpRenderer.setFillPoints(true);

		selectedSbpSeries.setStatus(XYSeries.SELECTED_STATUS);
		selectedSbpSeries.setSelectSize(context.getResources().getDimension(
				R.dimen.chart_select_point_size));
		selectedSbpRenderer.setColor(normalColor);
		selectedSbpRenderer.setPointStyle(PointStyle.CIRCLE);
		selectedSbpRenderer.setFillPoints(true);

		dataset = new XYMultipleSeriesDataset();
		dataset.addSeries(allDbpSeries);
		dataset.addSeries(allSbpSeries);
		dataset.addSeries(abnormalDbpSeries);
		dataset.addSeries(abnormalSbpSeries);
		dataset.addSeries(selectedDbpSeries);
		dataset.addSeries(selectedSbpSeries);
		renderer.addSeriesRenderer(abnormalDbpRenderer);
		renderer.addSeriesRenderer(abnormalSopRenderer);
		renderer.addSeriesRenderer(selectedDbpRenderer);
		renderer.addSeriesRenderer(selectedSbpRenderer);
		String[] types = new String[] { LineChart.TYPE, LineChart.TYPE,
				ScatterChart.TYPE, ScatterChart.TYPE, ScatterChart.TYPE,
				ScatterChart.TYPE };
		preLoadData(true);
		while (isAdd) {
			preLoadData(false);
		}

		final GraphicalView view = ChartFactory.getCombinedXYChartView(context,
				dataset, renderer, types);

		view.addSingleTapListener(new SingleTapListener() {

			@Override
			public void onSingleTap(MotionEvent e) {

				SeriesSelection point = view.getCurrentSeriesAndPoint();
				if (point != null) {
					BloodPressure temp = allData.get(point.getXValue());
					selectedDbpSeries.clear();
					selectedDbpSeries.add(point.getXValue(),
							handleCriticalMethod(temp.getLow().intValue()));
					selectedSbpSeries.clear();
					selectedSbpSeries.add(point.getXValue(),
							handleCriticalMethod(temp.getHigh().intValue()));
					iConvertDataListener.OnSelected(temp);
					if (!temp.isHealthState()) {
						selectedDbpRenderer.setColor(exceptionColor);
						selectedSbpRenderer.setColor(exceptionColor);
					} else {
						selectedDbpRenderer.setColor(normalColor);
						selectedSbpRenderer.setColor(normalColor);
					}
					view.repaint();
				}
			}
		});
		view.addPanListener(new PanListener() {

			@Override
			public void panApplied() {
				LoadData();
				view.repaint();
				double min = renderer.getXAxisMin();
				double max = renderer.getXAxisMax();
				List<BaseMeasureData> list = getExceptionAtRange(min, max);
				List<BaseMeasureData> all = getAllAtRange(min, max);
				iConvertDataListener.ConvertExceptionList(all, list);
			}
		});
		return view;
	}
}
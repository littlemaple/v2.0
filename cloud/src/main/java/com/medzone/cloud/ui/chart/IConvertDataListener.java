package com.medzone.cloud.ui.chart;

import java.util.List;

import com.medzone.framework.data.bean.imp.BaseMeasureData;

public interface IConvertDataListener {

	public void OnSelected(BaseMeasureData data);

	public void OnFirstPointPosition(BaseMeasureData bean);

	public void ConvertExceptionList(List<BaseMeasureData> curXAxis,
			List<BaseMeasureData> curXAxisException);
}

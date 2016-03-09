package com.medzone.cloud.data.helper;

import java.util.Comparator;

import com.medzone.framework.data.bean.imp.BaseMeasureData;
/**
 * 
 * 对测量数据集合进行排序：根据测量时间降序排序
 * 
 * @author Liwm
 *
 */
public class ComparatorBaseMeasureDataUtil implements Comparator<Object> {
	public int compare(Object arg0, Object arg1) {
		BaseMeasureData data0 = (BaseMeasureData) arg0;
		BaseMeasureData data1 = (BaseMeasureData) arg1;
		return data1.getMeasureTime().compareTo(data0.getMeasureTime());

	}

}

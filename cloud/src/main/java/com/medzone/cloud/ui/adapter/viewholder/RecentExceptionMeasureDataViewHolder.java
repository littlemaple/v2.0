package com.medzone.cloud.ui.adapter.viewholder;

import android.view.View;
import android.widget.TextView;

import com.medzone.cloud.data.CurrentAccountManager;
import com.medzone.cloud.module.CloudMeasureModuleCentreRoot;
import com.medzone.cloud.module.modules.BloodPressureModule;
import com.medzone.framework.data.bean.imp.BaseMeasureData;
import com.medzone.framework.data.bean.imp.BloodOxygen;
import com.medzone.framework.data.bean.imp.BloodPressure;
import com.medzone.framework.data.bean.imp.EarTemperature;
import com.medzone.framework.util.TimeUtils;
import com.medzone.mcloud.R.id;

public class RecentExceptionMeasureDataViewHolder extends BaseViewHolder {

	private TextView timeTV, valueTV;
	private BloodPressureModule mBloodPressureModule;
	private boolean isKpa;

	/**
	 * @param rootView
	 */
	public RecentExceptionMeasureDataViewHolder(View rootView) {
		super(rootView);
		mBloodPressureModule = (BloodPressureModule) CloudMeasureModuleCentreRoot
				.applyModule(CurrentAccountManager.getCurAccount(),
						BloodPressureModule.class.getCanonicalName());
		isKpa = mBloodPressureModule.isKpaMode();
	}

	@Override
	public void init(View view) {
		timeTV = (TextView) view
				.findViewById(id.recent_exception_measure_data_time);
		valueTV = (TextView) view
				.findViewById(id.recent_exception_measure_data_value);
	}

	@Override
	public void fillFromItem(Object item) {
		super.fillFromItem(item);
		final BaseMeasureData data = (BaseMeasureData) item;
		if (data instanceof BloodOxygen) {
			BloodOxygen bo = (BloodOxygen) data;
			timeTV.setText(TimeUtils.getYearToSecond(bo.getMeasureTime()));
			valueTV.setText(bo.getOxygen() + "%");
		} else if (data instanceof BloodPressure) {
			BloodPressure bp = (BloodPressure) data;
			timeTV.setText(TimeUtils.getYearToSecond(bp.getMeasureTime()));

			if (isKpa) {
				float highValue = bp.getHighKPA();
				float lowValue = bp.getLowKPA();
				valueTV.setText(highValue + "/" + lowValue);
			} else {
				int highValue = bp.getHigh().intValue();
				int lowValue = bp.getLow().intValue();
				valueTV.setText(highValue + "/" + lowValue);
			}
//			valueTV.append("/");
			valueTV.append(bp.getPressureUnit(isKpa));

		} else if (data instanceof EarTemperature) {
			EarTemperature et = (EarTemperature) data;
			timeTV.setText(TimeUtils.getYearToSecond(et.getMeasureTime()));
			valueTV.setText(et.getTemperature() + "℃");
		}
	}

}

/**
 * 
 */
package com.medzone.cloud.ui.adapter.viewholder;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.medzone.cloud.data.CurrentAccountManager;
import com.medzone.cloud.data.helper.MeasureDataUtil;
import com.medzone.cloud.module.CloudMeasureModuleCentreRoot;
import com.medzone.cloud.module.modules.BloodPressureModule;
import com.medzone.cloud.ui.MeasureDataActivity;
import com.medzone.framework.Log;
import com.medzone.framework.data.bean.imp.BloodPressure;
import com.medzone.framework.util.TimeUtils;
import com.medzone.mcloud.R.id;
import com.medzone.mcloud.R.string;

/**
 * @author ChenJunQi.
 * 
 */
public class BloodPressureViewHolder extends BaseViewHolder {

	public TextView tvPressure, tvRate, tvLastTime, tvType, tvPressureHint,
			tvRateHint, tvPressureUnit, tvRateUnit;
	private ImageView IvResultIcon;
	private View view;
	private Context context;
	private BloodPressureModule mBloodPressureModule;
	private boolean isKpa;

	/**
	 * @param rootView
	 */
	public BloodPressureViewHolder(View rootView, Context context) {
		super(rootView);
		this.context = context;
		view = rootView;
		mBloodPressureModule = (BloodPressureModule) CloudMeasureModuleCentreRoot
				.applyModule(CurrentAccountManager.getCurAccount(),
						BloodPressureModule.class.getCanonicalName());
		if (mBloodPressureModule != null)
			isKpa = mBloodPressureModule.isKpaMode();
	}

	@Override
	public void init(View view) {
		tvPressure = (TextView) view.findViewById(id.tv_value_1);
		tvRate = (TextView) view.findViewById(id.tv_value_2);
		tvLastTime = (TextView) view.findViewById(id.tv_measure_time);
		tvType = (TextView) view.findViewById(id.tv_measure_type);
		tvPressureHint = (TextView) view.findViewById(id.tv_unit_1);
		tvRateHint = (TextView) view.findViewById(id.tv_unit_2);
		tvPressureUnit = (TextView) view.findViewById(id.tv_unit_en_1);
		tvRateUnit = (TextView) view.findViewById(id.tv_unit_en_2);
		IvResultIcon = (ImageView) view.findViewById(id.iv_result_icon);

	}

	@Override
	public void fillFromItem(Object item) {
		final BloodPressure bp = (BloodPressure) item;

		if (isKpa) {
			float highValue = bp.getHighKPA();
			float lowValue = bp.getLowKPA();
			tvPressure.setText(highValue + "/" + lowValue);
		} else {
			int highValue = bp.getHigh().intValue();
			int lowValue = bp.getLow().intValue();
			tvPressure.setText(highValue + "/" + lowValue);
		}
		tvPressureUnit.setText(bp.getPressureUnit(isKpa));

		MeasureDataUtil.BloodPressureFlag(IvResultIcon, bp.getAbnormal());

		tvRate.setText(bp.getRate() + "");
		tvType.setText(context.getString(string.blood_pressure));
		tvLastTime.setText(TimeUtils.getYearToSecond(bp.getMeasureTime()));
		tvPressureHint.setText(context.getString(string.systolic_pressure)
				+ "/" + context.getString(string.diastolic_pressure));
		tvRateHint.setText(context.getString(string.heart_rate));
		tvRateUnit.setText(context.getString(string.heart_rate_unit));

		view.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(context, MeasureDataActivity.class);
				intent.putExtra("type", "bp");
				Bundle bundle = new Bundle();
				bundle.putSerializable("measureUid", bp.getMeasureUID());
				intent.putExtras(bundle);
				context.startActivity(intent);
			}
		});
	}

}

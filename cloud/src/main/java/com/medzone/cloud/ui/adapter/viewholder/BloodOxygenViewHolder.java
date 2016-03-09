/**
 * 
 */
package com.medzone.cloud.ui.adapter.viewholder;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.medzone.cloud.data.helper.MeasureDataUtil;
import com.medzone.cloud.ui.MeasureDataActivity;
import com.medzone.framework.data.bean.imp.BloodOxygen;
import com.medzone.framework.util.TimeUtils;
import com.medzone.mcloud.R.id;
import com.medzone.mcloud.R.string;

/**
 * @author ChenJunQi.
 * 
 */
public class BloodOxygenViewHolder extends BaseViewHolder {

	public TextView tvOxygen, tvRate, tvLastTime, tvType, tvOxygenHint,
			tvRateHint, tvOxygenUnit, tvRateUnit;
	private ImageView IvResultIcon;
	private View view;
	private Context context;

	/**
	 * @param rootView
	 */
	public BloodOxygenViewHolder(View rootView, Context context) {
		super(rootView);
		view = rootView;
		this.context = context;
	}

	@Override
	public void init(View view) {
		tvOxygen = (TextView) view.findViewById(id.tv_value_1);
		tvRate = (TextView) view.findViewById(id.tv_value_2);
		tvLastTime = (TextView) view.findViewById(id.tv_measure_time);
		tvType = (TextView) view.findViewById(id.tv_measure_type);
		tvOxygenHint = (TextView) view.findViewById(id.tv_unit_1);
		tvRateHint = (TextView) view.findViewById(id.tv_unit_2);
		tvOxygenUnit = (TextView) view.findViewById(id.tv_unit_en_1);
		tvRateUnit = (TextView) view.findViewById(id.tv_unit_en_2);
		IvResultIcon = (ImageView) view.findViewById(id.iv_result_icon);
	}

	@Override
	public void fillFromItem(Object item) {
		final BloodOxygen bloodOxygen = (BloodOxygen) item;
		tvOxygen.setText(bloodOxygen.getOxygen() + "");
		tvRate.setText(bloodOxygen.getRate() + "");
		tvLastTime.setText(TimeUtils.getYearToSecond(bloodOxygen
				.getMeasureTime()));
		tvType.setText(context.getString(string.blood_oxygen));
		tvOxygenHint.setText(context.getString(string.blood_oxygen_saturation));
		tvRateHint.setText(context.getString(string.heart_rate));
		tvRateUnit.setText(context.getString(string.heart_rate_unit));
		tvOxygenUnit.setText(context.getString(string.blood_oxygen_unit));
		MeasureDataUtil
				.BloodOxygenFlag(IvResultIcon, bloodOxygen.getAbnormal());
		view.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(context, MeasureDataActivity.class);
				intent.putExtra("type", "bo");
				intent.putExtra("measureUid", bloodOxygen.getMeasureUID());
				context.startActivity(intent);
			}
		});
	}

}

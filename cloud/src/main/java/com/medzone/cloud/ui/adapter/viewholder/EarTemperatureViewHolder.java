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

import com.medzone.cloud.ui.MeasureDataActivity;
import com.medzone.framework.data.bean.imp.EarTemperature;
import com.medzone.framework.util.TimeUtils;
import com.medzone.mcloud.R;
import com.medzone.mcloud.R.id;
import com.medzone.mcloud.R.string;

/**
 * @author ChenJunQi.
 * 
 */
public class EarTemperatureViewHolder extends BaseViewHolder {

	public TextView tvTemperature, tvLastTime, tvType, tvTemperatureHint,
			tvTemperatureUnit;
	private ImageView IvResultIcon;
	private View view;
	private Context context;

	/**
	 * @param rootView
	 */
	public EarTemperatureViewHolder(View rootView, Context context) {
		super(rootView);
		view = rootView;
		this.context = context;

	}

	@Override
	public void init(View view) {
		tvTemperature = (TextView) view.findViewById(id.tv_value);
		tvLastTime = (TextView) view.findViewById(id.tv_measure_time);
		tvType = (TextView) view.findViewById(id.tv_measure_type);
		tvTemperatureHint = (TextView) view.findViewById(id.tv_hint);
		tvTemperatureUnit = (TextView) view.findViewById(id.tv_unit);
		IvResultIcon = (ImageView) view.findViewById(id.iv_result_icon);
	}

	@Override
	public void fillFromItem(Object item) {
		final EarTemperature temperature = (EarTemperature) item;
		tvTemperature.setText(temperature.getTemperature() + "");
		tvLastTime.setText(TimeUtils.getYearToSecond(temperature
				.getMeasureTime()));
		tvType.setText(context.getString(string.ear_temperature));
		tvTemperatureHint.setText(context.getString(string.ear_temperature));
		tvTemperatureUnit.setText(context
				.getString(string.ear_temperature_unit));
		switch (temperature.getAbnormal()) {
		case EarTemperature.TEMPERATURE_STATE_HIGH_FEVER:
			IvResultIcon
					.setImageResource(R.drawable.testresultsview_testresult_graph_gaore);
			break;
		case EarTemperature.TEMPERATURE_STATE_LOW:
			IvResultIcon
					.setImageResource(R.drawable.testresultsview_testresult_graph_dire);
			break;
		case EarTemperature.TEMPERATURE_STATE_FEVER:
			IvResultIcon
					.setImageResource(R.drawable.testresultsview_testresult_graph_fare);
			break;
		case EarTemperature.TEMPERATURE_STATE_NORMAL:
			IvResultIcon
					.setImageResource(R.drawable.testresultsview_testresult_graph_normal);
			break;
		default:
			IvResultIcon
					.setImageResource(R.drawable.testresultsview_testresult_graph_normal);
		}
		view.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(context, MeasureDataActivity.class);
				intent.putExtra("type", "et");
				Bundle bundle = new Bundle();
				bundle.putString("measureUid", temperature.getMeasureUID());
				intent.putExtras(bundle);
				context.startActivity(intent);
			}
		});
	}

}

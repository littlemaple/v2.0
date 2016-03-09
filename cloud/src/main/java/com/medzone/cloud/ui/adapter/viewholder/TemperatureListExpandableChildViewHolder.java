package com.medzone.cloud.ui.adapter.viewholder;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.medzone.cloud.data.helper.MeasureDataUtil;
import com.medzone.framework.data.bean.imp.EarTemperature;
import com.medzone.framework.util.TimeUtils;
import com.medzone.mcloud.R;
import com.medzone.mcloud.R.color;

public class TemperatureListExpandableChildViewHolder extends BaseViewHolder {

	private TextView temperature;
	private TextView time;
	private final static int EXCEPTION_TYPE = 1;
	private Context context;

	public TemperatureListExpandableChildViewHolder(View rootView) {
		super(rootView);
		// TODO Auto-generated constructor stub
		this.context = rootView.getContext();
	}

	@Override
	public void init(View view) {
		temperature = (TextView) view
				.findViewById(R.id.temperature_history_list_child_temperature);
		time = (TextView) view
				.findViewById(R.id.temperature_history_list_child_time);

	}

	@Override
	public void fillFromItem(Object item, Object type) {
		// TODO Auto-generated method stub
		super.fillFromItem(item, type);
		int curType = ((Integer) type).intValue();
		if (curType == EXCEPTION_TYPE) {
			temperature.setTextColor(context.getResources().getColor(
					color.font_abnormal_red));
		}

		final EarTemperature et = (EarTemperature) item;
		temperature.setText(String.valueOf(et.getTemperature()));
		time.setText(TimeUtils.getMonthToSecond(et.getMeasureTime()));

	}

}

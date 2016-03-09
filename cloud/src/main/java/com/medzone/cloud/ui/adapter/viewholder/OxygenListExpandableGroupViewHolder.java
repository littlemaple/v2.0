package com.medzone.cloud.ui.adapter.viewholder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.medzone.cloud.data.helper.MeasureDataUtil;
import com.medzone.framework.data.bean.imp.MeasureStatistical;
import com.medzone.mcloud.R;

public class OxygenListExpandableGroupViewHolder extends BaseViewHolder {
	private TextView monthTV;
	private TextView sumTimesTV;
	private TextView errorTimesTV;
	private TextView monthStartTV;
	private TextView monthEndTV;
	private ImageView image;

	public OxygenListExpandableGroupViewHolder(View rootView) {
		super(rootView);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void init(View view) {
		monthTV = (TextView) view.findViewById(R.id.oxygen_history_list_month);
		sumTimesTV = (TextView) view
				.findViewById(R.id.oxygen_history_list_sum_times);
		errorTimesTV = (TextView) view
				.findViewById(R.id.oxygen_history_list_error_times);
		monthStartTV = (TextView) view
				.findViewById(R.id.oxygen_history_list_month_start);
		monthEndTV = (TextView) view
				.findViewById(R.id.oxygen_history_list_month_end);
		image = (ImageView) view
				.findViewById(R.id.oxygen_history_list_orientation);
	}

	@Override
	public void fillFromItem(Object item, Object type) {
		// TODO Auto-generated method stub
		super.fillFromItem(item, type);
		boolean isExpanded = (Boolean) type;
		MeasureStatistical statistical = (MeasureStatistical) item;
		String measureTimes = statistical.getMeasureSumTimes();
		sumTimesTV.setText(MeasureDataUtil.StringConcatenation(measureTimes));

		monthTV.setText(statistical.getMeasureMonth());
		String errorTimes = statistical.getMeasureExceptionTimes();
		errorTimesTV.setText(MeasureDataUtil.StringConcatenation(errorTimes));

		monthStartTV.setText(statistical.getMeasureMonthStart());
		monthEndTV.setText(statistical.getMeasureMonthEnd());
		if (isExpanded) {
			image.setBackgroundResource(R.drawable.group_ic_pullup);
		} else {
			image.setBackgroundResource(R.drawable.group_ic_pulldown);
		}
	}

}

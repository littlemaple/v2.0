package com.medzone.cloud.ui.adapter.viewholder;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.medzone.framework.data.bean.imp.BloodOxygen;
import com.medzone.framework.util.TimeUtils;
import com.medzone.mcloud.R;
import com.medzone.mcloud.R.color;

public class OxygenListExpandableChildViewHolder extends BaseViewHolder {

	private TextView oxygen;
	private TextView heart;
	private TextView time;
	private final static int EXCEPTION_TYPE = 1;
	private Context context;

	public OxygenListExpandableChildViewHolder(View rootView) {
		super(rootView);
		// TODO Auto-generated constructor stub
		this.context = rootView.getContext();
	}

	@Override
	public void init(View view) {
		oxygen = (TextView) view
				.findViewById(R.id.oxygen_history_list_child_oxygen);
		time = (TextView) view
				.findViewById(R.id.oxygen_history_list_child_time);
		heart = (TextView) view
				.findViewById(R.id.oxygen_history_list_child_heart);

	}

	@Override
	public void fillFromItem(Object item, Object type) {
		// TODO Auto-generated method stub
		super.fillFromItem(item, type);
		int curType = ((Integer) type).intValue();
		if (curType == EXCEPTION_TYPE) {
			oxygen.setTextColor(context.getResources().getColor(
					color.font_abnormal_red));
		}

		final BloodOxygen bo = (BloodOxygen) item;
		oxygen.setText(String.valueOf(bo.getOxygen()));
		time.setText(TimeUtils.getMonthToSecond(bo.getMeasureTime()));
		heart.setText(String.valueOf(bo.getRate()));

	}

}

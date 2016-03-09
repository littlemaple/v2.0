package com.medzone.cloud.clock;

import android.content.Context;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.medzone.cloud.ui.adapter.viewholder.BaseViewHolder;
import com.medzone.mcloud.R;

public class AlarmRepeatViewHolder extends BaseViewHolder {

	public CheckBox checkBox;
	public TextView tv;
	public Context context;

	public AlarmRepeatViewHolder(View rootView, Context context) {
		super(rootView);
		this.context = context;
	}

	@Override
	public void init(View view) {

		checkBox = (CheckBox) view.findViewById(R.id.cb_repeat);
		tv = (TextView) view.findViewById(R.id.tv_alarm_week);

	}

	@Override
	public void fillFromItem(Object item, Object type) {

		Integer temp = (Integer) item;
		Boolean isCheck = temp.intValue() == 1 ? true : false;
		Integer position = (Integer) type;

		String[] str = context.getResources().getStringArray(
				R.array.every_week_list);
		tv.setText(str[position] + "");
		checkBox.setChecked(isCheck);
	}

}

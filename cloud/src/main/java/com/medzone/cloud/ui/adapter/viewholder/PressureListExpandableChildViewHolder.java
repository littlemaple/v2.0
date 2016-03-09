package com.medzone.cloud.ui.adapter.viewholder;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.medzone.cloud.data.CurrentAccountManager;
import com.medzone.cloud.module.CloudMeasureModuleCentreRoot;
import com.medzone.cloud.module.modules.BloodPressureModule;
import com.medzone.framework.data.bean.imp.BloodPressure;
import com.medzone.framework.util.TimeUtils;
import com.medzone.mcloud.R;
import com.medzone.mcloud.R.color;
import com.medzone.mcloud.R.id;

public class PressureListExpandableChildViewHolder extends BaseViewHolder {

	private TextView tvPressure;
	private TextView tvPressureUnit;
	private TextView tvRate;
	private TextView tvTime;
	private final static int EXCEPTION_TYPE = 1;
	private Context context;

	private BloodPressureModule mBloodPressureModule;
	private boolean isKpa;

	public PressureListExpandableChildViewHolder(View rootView) {
		super(rootView);
		this.context = rootView.getContext();
		mBloodPressureModule = (BloodPressureModule) CloudMeasureModuleCentreRoot
				.applyModule(CurrentAccountManager.getCurAccount(),
						BloodPressureModule.class.getCanonicalName());
		isKpa = mBloodPressureModule.isKpaMode();
	}

	@Override
	public void init(View view) {
		tvPressure = (TextView) view
				.findViewById(R.id.pressure_history_list_child_pressure);
		tvPressureUnit = (TextView) view
				.findViewById(id.pressure_history_list_child_pressure_unit);
		tvTime = (TextView) view
				.findViewById(R.id.pressure_history_list_child_time);
		tvRate = (TextView) view
				.findViewById(R.id.pressure_history_list_child_heart);

	}

	@Override
	public void fillFromItem(Object item, Object type) {
		super.fillFromItem(item, type);

		final BloodPressure bp = (BloodPressure) item;

		int curType = ((Integer) type).intValue();
		if (curType == EXCEPTION_TYPE) {
			tvPressure.setTextColor(context.getResources().getColor(
					color.font_abnormal_red));
		}

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

		tvTime.setText(TimeUtils.getMonthToSecond(bp.getMeasureTime()));
		tvRate.setText(String.valueOf(bp.getRate()));

	}

}

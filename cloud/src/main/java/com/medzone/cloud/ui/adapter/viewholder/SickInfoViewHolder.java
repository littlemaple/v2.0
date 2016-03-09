/**
 * 
 */
package com.medzone.cloud.ui.adapter.viewholder;

import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.medzone.framework.data.bean.imp.SickInfo;
import com.medzone.mcloud.R.id;

/**
 * @author ChenJunQi.
 * 
 */
public class SickInfoViewHolder extends BaseViewHolder {

	public TextView tvDiseaseName;
	public CheckBox cbisDiseaseExist;

	public SickInfoViewHolder(View rootView) {
		super(rootView);
	}

	@Override
	public void init(View view) {
		tvDiseaseName = (TextView) view.findViewById(id.tv_illness);
		cbisDiseaseExist = (CheckBox) view.findViewById(id.cb_illness);
	}

	@Override
	public void fillFromItem(Object item) {
		final SickInfo bs = (SickInfo) item;
		// cbisDiseaseExist
		// .setOnCheckedChangeListener(new OnCheckedChangeListener() {
		//
		// @Override
		// public void onCheckedChanged(CompoundButton buttonView,
		// boolean isChecked) {
		// bs.setDiseaseExist(isChecked);
		// }
		// });
		tvDiseaseName.setText(bs.getDiseaseName());
		cbisDiseaseExist.setChecked(bs.isDiseaseExist());
	}

}

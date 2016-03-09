/**
 * 
 */
package com.medzone.cloud.ui.adapter.viewholder;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import com.medzone.cloud.Constants;
import com.medzone.cloud.data.TemporaryData;
import com.medzone.cloud.module.CloudMeasureModule;
import com.medzone.cloud.ui.MeasureActivity;
import com.medzone.cloud.ui.SettingManagerDeviceActivity;
import com.medzone.framework.data.bean.imp.GroupMember;
import com.medzone.framework.util.ViewUtils;
import com.medzone.mcloud.R.id;
import com.medzone.mcloud.R.string;

/**
 * @author ChenJunQi.
 * 
 */
public class DeviceViewHolder extends BaseViewHolder {

	private View rootView;
	private ImageView imgView;
	private Context context;
	private GroupMember groupmember;

	public DeviceViewHolder(View rootView, GroupMember groupmember) {
		super(rootView);
		this.rootView = rootView;
		this.groupmember = groupmember;
		this.context = rootView.getContext();
	}

	@Override
	public void init(View view) {

		imgView = (ImageView) view.findViewById(id.equipment_icon);
	}

	@Override
	public void fillFromItem(Object item) {
		final CloudMeasureModule<?> module = (CloudMeasureModule<?>) item;
		if (module.getDrawable() != null) {
			imgView.setImageDrawable(module.getDrawable());
		}
		imgView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (ViewUtils.isFastDoubleClick()) {
					return;
				}

				if (TextUtils.equals(module.getName(),
						context.getString(string.action_add))) {
					jump2DeviceManager();
				} else if (TextUtils.equals(module.getName(),
						context.getString(string.place_holder))) {
					((Activity) context).finish();
				} else {
					jump2MeasureActivity(module);
				}
			}
		});
		rootView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				((Activity) context).finish();
			}
		});

	}

	private void jump2DeviceManager() {
		Intent intent = new Intent(context, SettingManagerDeviceActivity.class);
		context.startActivity(intent);
	}

	private void jump2MeasureActivity(CloudMeasureModule<?> module) {
		// 确保跳转测量模块总是会携带Constants.TEMPORARYDATA_KEY_TEST_ACCOUNT信息
		TemporaryData.save(Constants.TEMPORARYDATA_KEY_TEST_ACCOUNT,
				groupmember);
		Intent intent = new Intent(context, MeasureActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.putExtra(MeasureActivity.MEASURE_DEVICE_KEY,
				module.getModuleID());

		context.startActivity(intent);
		((Activity) context).finish();
	}
}

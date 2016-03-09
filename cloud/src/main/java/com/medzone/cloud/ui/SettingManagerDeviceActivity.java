package com.medzone.cloud.ui;

import java.util.List;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.medzone.cloud.data.CurrentAccountManager;
import com.medzone.cloud.module.CloudMeasureModuleCentreRoot;
import com.medzone.cloud.module.IUpdateSpecificationListener;
import com.medzone.cloud.module.modules.BloodOxygenModule;
import com.medzone.cloud.module.modules.BloodPressureModule;
import com.medzone.cloud.module.modules.EarTemperatureModule;
import com.medzone.cloud.ui.dialog.error.ErrorDialogUtil;
import com.medzone.framework.data.bean.imp.Account;
import com.medzone.framework.errorcode.ProxyErrorCode;
import com.medzone.framework.module.ModuleSpecification;
import com.medzone.framework.module.ModuleStatus;
import com.medzone.mcloud.R;
import com.medzone.mcloud.R.drawable;
import com.medzone.mcloud.R.id;
import com.medzone.mcloud.R.string;

public class SettingManagerDeviceActivity extends BaseActivity implements
		OnClickListener {

	private CheckBox deviceBloodPressureCheckBox;
	private CheckBox deviceBloodOxygenCheckBox;
	private CheckBox deviceEarTemperatureCheckBox;

	private ModuleStatus bpStatus;
	private ModuleStatus boStatus;
	private ModuleStatus etStatus;

	private List<ModuleSpecification> mSpecs;// Just For Display
	private Account curAccount;

	private void initActionBar() {
		ActionBar actionBar = getSupportActionBar();
		ActionBar.LayoutParams params = new ActionBar.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT,
				Gravity.CENTER);
		View view = LayoutInflater.from(this).inflate(
				R.layout.custom_actionbar_with_image, null);
		TextView title = (TextView) view.findViewById(R.id.actionbar_title);
		title.setText(string.actionbar_title_setting_device_manager);
		ImageButton leftButton = (ImageButton) view
				.findViewById(id.actionbar_left);
		leftButton.setImageResource(drawable.personalinformationview_ic_cancel);
		leftButton.setOnClickListener(this);

		ImageButton rightButton = (ImageButton) view
				.findViewById(id.actionbar_right);
		rightButton.setImageResource(drawable.personalinformationview_ic_ok);
		rightButton.setOnClickListener(this);
		actionBar.setCustomView(view, params);
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		actionBar.setDisplayShowCustomEnabled(true);
	}

	@Override
	protected void preLoadData(Bundle savedInstanceState) {
		super.preLoadData(savedInstanceState);
		curAccount = CurrentAccountManager.getCurAccount();
		mSpecs = CloudMeasureModuleCentreRoot
				.getAllModuleSpcification(curAccount);
	}

	@Override
	protected void preInitUI() {
		initActionBar();
	}

	@Override
	protected void initUI() {
		setContentView(R.layout.activity_setting_manager_device);
		deviceBloodPressureCheckBox = (CheckBox) findViewById(id.checkbox_device_bloodpressure);
		deviceBloodOxygenCheckBox = (CheckBox) findViewById(id.checkbox_device_bloodoxygen);
		deviceEarTemperatureCheckBox = (CheckBox) findViewById(id.checkbox_device_eartemperature);

	}

	@Override
	protected void postInitUI() {

		// XXX 这里的顺序也要保持与配置中的顺序一致
		if (mSpecs != null && mSpecs.size() != 0) {

			bpStatus = mSpecs.get(BloodPressureModule.ORDER_IN_CATEGORY)
					.getModuleStatus();
			if (bpStatus == ModuleStatus.UNINSTALL) {
				deviceBloodPressureCheckBox.setVisibility(View.GONE);
			} else {
				deviceBloodPressureCheckBox.setChecked(isChecked(bpStatus));
			}

			boStatus = mSpecs.get(BloodOxygenModule.ORDER_IN_CATEGORY)
					.getModuleStatus();
			if (boStatus == ModuleStatus.UNINSTALL) {
				deviceBloodOxygenCheckBox.setVisibility(View.GONE);
			} else {
				deviceBloodOxygenCheckBox.setChecked(isChecked(boStatus));
			}

			etStatus = mSpecs.get(EarTemperatureModule.ORDER_IN_CATEGORY)
					.getModuleStatus();
			if (etStatus == ModuleStatus.UNINSTALL) {
				deviceEarTemperatureCheckBox.setVisibility(View.GONE);
			} else {
				deviceEarTemperatureCheckBox.setChecked(isChecked(etStatus));
			}

		}
	}

	private boolean isChecked(ModuleStatus status) {
		boolean isDisplay = false;
		if (status == ModuleStatus.DISPLAY) {
			isDisplay = true;
		}
		return isDisplay;
	}

	private ModuleStatus getModuleStatus(boolean isDisplay) {
		if (isDisplay)
			return ModuleStatus.DISPLAY;
		return ModuleStatus.HIDDEN;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.actionbar_left:
			finish();
			break;
		case R.id.actionbar_right:
			CloudMeasureModuleCentreRoot.doUpdateAllSpecification(curAccount,
					prepareUpdateSpecs(), new IUpdateSpecificationListener() {

						@Override
						public void onComplete(boolean isSuccess) {
							if (isSuccess) {
								finish();
							} else {
								ErrorDialogUtil.showErrorToast(
										SettingManagerDeviceActivity.this,
										ProxyErrorCode.TYPE_SETTING,
										ProxyErrorCode.LocalError.CODE_10002);
							}
						}
					});
			break;
		default:
			break;
		}
	}

	private List<ModuleSpecification> prepareUpdateSpecs() {
		final List<ModuleSpecification> ret = CloudMeasureModuleCentreRoot
				.getAllModuleSpcification(curAccount);
		if (ret != null && ret.size() != 0) {

			if (bpStatus != ModuleStatus.UNINSTALL) {
				ret.get(BloodPressureModule.ORDER_IN_CATEGORY)
						.setModuleStatus(
								getModuleStatus(deviceBloodPressureCheckBox
										.isChecked()));
			}
			if (boStatus != ModuleStatus.UNINSTALL) {
				ret.get(BloodOxygenModule.ORDER_IN_CATEGORY).setModuleStatus(
						getModuleStatus(deviceBloodOxygenCheckBox.isChecked()));
			}
			if (etStatus != ModuleStatus.UNINSTALL) {
				ret.get(EarTemperatureModule.ORDER_IN_CATEGORY)
						.setModuleStatus(
								getModuleStatus(deviceEarTemperatureCheckBox
										.isChecked()));
			}
		}
		return ret;
	}

}

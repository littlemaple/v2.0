package com.medzone.cloud.ui;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.LayoutParams;
import com.medzone.cloud.controller.BloodPressureController;
import com.medzone.cloud.data.CurrentAccountManager;
import com.medzone.cloud.data.helper.AccountHelper;
import com.medzone.cloud.data.helper.FlagHelper;
import com.medzone.cloud.module.CloudMeasureModule;
import com.medzone.cloud.module.CloudMeasureModuleCentreRoot;
import com.medzone.cloud.module.IUpdateSpecificationListener;
import com.medzone.cloud.module.modules.BloodOxygenModule;
import com.medzone.cloud.module.modules.BloodPressureModule;
import com.medzone.cloud.ui.dialog.error.ErrorDialogUtil;
import com.medzone.framework.data.bean.imp.Account;
import com.medzone.framework.errorcode.ProxyCode.LocalError;
import com.medzone.framework.errorcode.ProxyErrorCode;
import com.medzone.framework.module.ModuleStatus;
import com.medzone.framework.network.NetworkClientResult;
import com.medzone.framework.task.BaseResult;
import com.medzone.framework.task.TaskHost;
import com.medzone.mcloud.R;
import com.medzone.mcloud.R.drawable;
import com.medzone.mcloud.R.id;
import com.medzone.mcloud.R.string;

public class SettingMeasureActivity extends BaseActivity implements
		OnClickListener {

	private CheckBox cbVoiceSetting, cbOxygenAlert;
	private TextView pressureUnitTV;
	private LinearLayout llBloodOxygen, llBloodPressure;
	private int dialogInit = 0;
	private String[] selects = { "mmHg", "kpa" };
	private Account account;
	private List<CloudMeasureModule<?>> allModuleList = new ArrayList<CloudMeasureModule<?>>();
	private CloudMeasureModule<?> oxModule, bloodPressureBaseModule;
	private int flag;

	@Override
	protected void preLoadData(Bundle savedInstanceState) {
		super.preLoadData(savedInstanceState);
		account = CurrentAccountManager.getCurAccount();
		flag = account.getFlag();
		allModuleList = CloudMeasureModuleCentreRoot.getModules(account);
	}

	@Override
	protected void preInitUI() {
		initActionBar();
	}

	@Override
	protected void initUI() {
		setContentView(R.layout.activity_setting_measure);
		cbVoiceSetting = (CheckBox) findViewById(id.cb_setting_measure_voice);
		cbOxygenAlert = (CheckBox) findViewById(id.cb_setting_measure_oxygen_alert);
		llBloodPressure = (LinearLayout) findViewById(id.setting_measure_pressure_ll);
		llBloodOxygen = (LinearLayout) findViewById(id.setting_measure_oxygen_ll);
		pressureUnitTV = (TextView) findViewById(id.tv_setting_measure_pressure_unitTV);
	}

	private void initActionBar() {
		ActionBar actionBar = getSupportActionBar();
		ActionBar.LayoutParams params = new ActionBar.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT,
				Gravity.CENTER);
		View view = LayoutInflater.from(this).inflate(
				R.layout.custom_actionbar_with_image, null);
		TextView title = (TextView) view.findViewById(R.id.actionbar_title);
		title.setText(string.actionbar_title_setting_measure);

		ImageButton leftButton = (ImageButton) view
				.findViewById(id.actionbar_left);
		leftButton.setImageResource(drawable.public_ic_back);
		leftButton.setOnClickListener(this);
		actionBar.setCustomView(view, params);
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		actionBar.setDisplayShowCustomEnabled(true);
	}

	@Override
	protected void postInitUI() {

		llBloodPressure.setOnClickListener(this);

		fillView();
	}

	public void fillView() {

		// 初始化全局的设置
		initGeneralSetting();

		// 初始化其他单独模块的设置
		if (allModuleList != null) {
			int count = allModuleList.size();
			for (int i = 0; i < count; i++) {
				CloudMeasureModule<?> cbm = allModuleList.get(i);
				if (cbm instanceof BloodPressureModule) {
					initBloodPressureUI(cbm);
				} else if (cbm instanceof BloodOxygenModule) {
					initBloodOxygenUI(cbm);
				}
			}
		}

	}

	// 语音播报
	public void initGeneralSetting() {

		// For Voice
		boolean isChecked = FlagHelper.getSetValueInFlag(flag,
				FlagHelper.FLAG_POSITION_VOICE);

		FlagHelper.put(FlagHelper.FLAG_POSITION_VOICE, isChecked);

		cbVoiceSetting.setChecked(isChecked);
		cbVoiceSetting
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton arg0,
							boolean isChecked) {

						FlagHelper.put(FlagHelper.FLAG_POSITION_VOICE,
								isChecked);
						updateGeneralSetting();
					}
				});
	}

	@SuppressWarnings("unchecked")
	public void initBloodOxygenUI(CloudMeasureModule<?> cloudBaseModule) {

		// TODO 目前不需要血氧警报，开启血氧长期测量后添加
		// oxModule = (BloodOxygenModule) cloudBaseModule;
		//
		// if (oxModule.getModuleStatus().equals(ModuleStatus.DISPLAY)) {
		// llBloodOxygen.setVisibility(View.VISIBLE);
		//
		// char setValue = ((CloudMeasureModule<BloodOxygenController>)
		// oxModule)
		// .getPositionSetting(BloodOxygenModule.FLAG_POSITION_ALERT);
		//
		// cbOxygenAlert.setChecked(setValue == '1' ? true : false);
		// cbOxygenAlert
		// .setOnCheckedChangeListener(new OnCheckedChangeListener() {
		//
		// @Override
		// public void onCheckedChanged(CompoundButton arg0,
		// boolean isChecked) {
		//
		// char value = isChecked ? '1' : '0';
		// ((CloudMeasureModule<BloodOxygenController>) oxModule)
		// .modifiedSetting(
		// BloodOxygenModule.FLAG_POSITION_ALERT,
		// value,
		// new IUpdateSpecificationListener() {
		// @Override
		// public void onComplete(
		// boolean isSuccess) {
		// if (!isSuccess) {
		// fillView();
		// ErrorDialogUtil
		// .showErrorToast(
		// SettingMeasureActivity.this,
		// ProxyErrorCode.TYPE_SETTING,
		// LocalError.CODE_10001);
		// }
		// }
		// });
		// }
		// });
		// } else {
		// cbOxygenAlert.setOnCheckedChangeListener(null);
		// llBloodOxygen.setVisibility(View.GONE);
		// }
	}

	public void initBloodPressureUI(CloudMeasureModule<?> cloudBaseModule) {
		bloodPressureBaseModule = (BloodPressureModule) cloudBaseModule;
		if (bloodPressureBaseModule.getModuleStatus().equals(
				ModuleStatus.DISPLAY)) {
			llBloodPressure.setVisibility(View.VISIBLE);

			@SuppressWarnings("unchecked")
			char unitValue = ((CloudMeasureModule<BloodPressureController>) bloodPressureBaseModule)
					.getPositionSetting(BloodPressureModule.FLAG_POSITION_UNIT_SWITCH);
			if (unitValue == '1') {
				this.dialogInit = 1;
				pressureUnitTV.setText(selects[dialogInit]);
			} else {
				this.dialogInit = 0;
				pressureUnitTV.setText(selects[dialogInit]);
			}
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.actionbar_left:
			this.finish();
			break;
		case R.id.setting_measure_pressure_ll:
			showUnitsDialog();
			break;
		default:
			break;
		}
	}

	public void showUnitsDialog() {
		new AlertDialog.Builder(this)
				.setIcon(android.R.drawable.ic_dialog_info)
				.setSingleChoiceItems(selects, this.dialogInit,
						new DialogInterface.OnClickListener() {
							@SuppressWarnings("unchecked")
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
								dialogInit = which;
								pressureUnitTV.setText(selects[which]);
								// switch unit
								char value = dialogInit == 0 ? '0' : '1';
								((CloudMeasureModule<BloodPressureController>) bloodPressureBaseModule)
										.modifiedSetting(
												BloodPressureModule.FLAG_POSITION_UNIT_SWITCH,
												value,
												new IUpdateSpecificationListener() {
													@Override
													public void onComplete(
															boolean isSuccess) {
														if (!isSuccess) {
															fillView();
															// Log.e();
															ErrorDialogUtil
																	.showErrorToast(
																			SettingMeasureActivity.this,
																			ProxyErrorCode.TYPE_SETTING,
																			LocalError.CODE_10001);
														}

													}
												});
							}
						}).show().setCanceledOnTouchOutside(true);
		
	}

	/**
	 * 更新全局的配置
	 * 
	 */
	private void updateGeneralSetting() {
		Account tempAccount;
		try {
			tempAccount = (Account) account.clone();
		} catch (Exception e) {
			tempAccount = CurrentAccountManager.getCurAccount();
			e.printStackTrace();
		}
		final int flag = FlagHelper.calculateFlag();
		tempAccount.setFlag(flag);

		AccountHelper.doUpdateAccountTask(this, tempAccount, new TaskHost() {
			@Override
			public void onPostExecute(int requestCode, BaseResult result) {
				super.onPostExecute(requestCode, result);
				if (result.isSuccess()) {
					NetworkClientResult res = (NetworkClientResult) result;
					if (res.isServerDisposeSuccess()) {
						account.setFlag(flag);
					} else {
						ErrorDialogUtil.showErrorDialog(
								SettingMeasureActivity.this,
								ProxyErrorCode.TYPE_SETTING,
								res.getErrorCode(), true);
						fillView();
					}
				} else {
					ErrorDialogUtil.showErrorToast(SettingMeasureActivity.this,
							ProxyErrorCode.TYPE_SETTING, LocalError.CODE_10001);
					fillView();
				}
			}
		});

	}

}

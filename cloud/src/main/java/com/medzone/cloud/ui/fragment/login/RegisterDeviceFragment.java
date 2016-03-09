package com.medzone.cloud.ui.fragment.login;

import java.util.List;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.LayoutParams;
import com.medzone.cloud.module.CloudMeasureModuleCentreRoot;
import com.medzone.cloud.module.modules.BloodOxygenModule;
import com.medzone.cloud.module.modules.BloodPressureModule;
import com.medzone.cloud.module.modules.EarTemperatureModule;
import com.medzone.cloud.task.GetModuleConfigTask;
import com.medzone.cloud.ui.RegisterDeviceActivity;
import com.medzone.cloud.ui.SplashScreenActivity;
import com.medzone.cloud.ui.dialog.error.ErrorDialogUtil;
import com.medzone.framework.data.bean.imp.Account;
import com.medzone.framework.errorcode.ProxyCode;
import com.medzone.framework.errorcode.ProxyErrorCode;
import com.medzone.framework.fragment.BaseFragment;
import com.medzone.framework.module.ModuleSpecification;
import com.medzone.framework.module.ModuleStatus;
import com.medzone.framework.task.BaseResult;
import com.medzone.framework.task.TaskHost;
import com.medzone.framework.task.progress.CustomDialogProgress;
import com.medzone.mcloud.R;
import com.medzone.mcloud.R.id;
import com.medzone.mcloud.R.string;

public class RegisterDeviceFragment extends BaseFragment implements
		OnClickListener {

	private CheckBox deviceBloodPressureCheckBox;
	private CheckBox deviceBloodOxygenCheckBox;
	private CheckBox deviceEarTemperatureCheckBox;
	private CheckBox deviceFetalCheckBox;
	private CheckBox deviceBloodSugarCheckBox;

	private Button completeButton;

	private Account curAccount;
	private long firstClickBackKey;

	@Override
	protected void initActionBar() {
		ActionBar actionBar = getSherlockActivity().getSupportActionBar();
		actionBar.show();
		ActionBar.LayoutParams params = new ActionBar.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT,
				Gravity.CENTER);
		View view = LayoutInflater.from(getActivity()).inflate(
				R.layout.custom_actionbar_with_image, null);
		TextView title = (TextView) view.findViewById(R.id.actionbar_title);
		title.setText(string.action_title_register_choose_device);
		actionBar.setCustomView(view, params);
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		actionBar.setDisplayShowCustomEnabled(true);
		super.initActionBar();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		initActionBar();
		View view = inflater.inflate(R.layout.activity_register_device, null);
		deviceBloodPressureCheckBox = (CheckBox) view
				.findViewById(id.checkbox_device_bloodpressure);
		deviceBloodOxygenCheckBox = (CheckBox) view
				.findViewById(id.checkbox_device_bloodoxygen);
		deviceEarTemperatureCheckBox = (CheckBox) view
				.findViewById(id.checkbox_device_eartemperature);
		deviceFetalCheckBox = (CheckBox) view
				.findViewById(id.checkbox_device_fetal);
		deviceBloodSugarCheckBox = (CheckBox) view
				.findViewById(id.checkbox_device_bloodsugar);
		completeButton = (Button) view.findViewById(id.btn_complete);

		ModuleStatus bpStatus = BloodPressureModule.getDefaultSpecification(
				true).getModuleStatus();
		if (bpStatus == ModuleStatus.UNINSTALL) {
			deviceBloodPressureCheckBox.setVisibility(View.GONE);
		}
		ModuleStatus boStatus = BloodOxygenModule.getDefaultSpecification(true)
				.getModuleStatus();
		if (boStatus == ModuleStatus.UNINSTALL) {
			deviceBloodOxygenCheckBox.setVisibility(View.GONE);
		}

		ModuleStatus etStatus = EarTemperatureModule.getDefaultSpecification(
				true).getModuleStatus();
		if (etStatus == ModuleStatus.UNINSTALL) {
			deviceEarTemperatureCheckBox.setVisibility(View.GONE);
		}

		completeButton.setOnClickListener(this);

		return view;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.actionbar_left:
			getActivity().finish();
			break;
		case R.id.btn_complete:
			performCompleteButtonClick();
			break;
		default:
			break;
		}
	}

	public void performCompleteButtonClick() {

		if (curAccount != null) {
			doUpdateModuleConfigTask(curAccount);
		} else {
			ErrorDialogUtil.showErrorDialog(getActivity(),
					ProxyErrorCode.TYPE_LOGIN_AND_REGISTER,
					ProxyCode.LocalError.CODE_10214, true);
			getActivity().finish();
		}
	}

	private void doUpdateModuleConfigTask(Account account) {
		boolean[] chosenArray = { deviceBloodOxygenCheckBox.isChecked(),
				deviceBloodPressureCheckBox.isChecked(),
				deviceEarTemperatureCheckBox.isChecked() };
		List<ModuleSpecification> list = CloudMeasureModuleCentreRoot
				.initDefaultMeasureModules(chosenArray);
		GetModuleConfigTask task = new GetModuleConfigTask(getActivity(),
				account.getAccessToken(), list);
		task.setProgress(new CustomDialogProgress(getActivity(), "Loading..."));
		task.setTaskHost(new TaskHost() {
			@Override
			public void onPostExecute(int requestCode, BaseResult result) {

				super.onPostExecute(requestCode, result);
				if (result.isSuccess()) {
					if (result.isServerDisposeSuccess()) {
						jumpToLoginActivity();
					} else {
						ErrorDialogUtil.showErrorDialog(getActivity(),
								ProxyErrorCode.TYPE_LOGIN_AND_REGISTER,
								result.getErrorCode(), true);
					}
				}
			}
		});
		task.execute();
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void jumpToLoginActivity() {

		// logout and remove all back stack activity.
		Intent intent = new Intent();
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setClass(getActivity(), SplashScreenActivity.class);
		startActivity(intent);

		getActivity().finish();
	}

	private View.OnKeyListener backListener = new OnKeyListener() {

		@Override
		public boolean onKey(View v, int keyCode, KeyEvent event) {
			if (keyCode == KeyEvent.KEYCODE_BACK) {
				long secondClickBackKey = System.currentTimeMillis();
				if (secondClickBackKey - firstClickBackKey > 3000) {
					// TODO 暂时留置
					Toast.makeText(getActivity(), R.string.press_back_exit_tip,
							Toast.LENGTH_SHORT).show();
					firstClickBackKey = System.currentTimeMillis();
					return true;
				} else {
					getActivity().finish();
				}
			}
			return false;
		}
	};

}

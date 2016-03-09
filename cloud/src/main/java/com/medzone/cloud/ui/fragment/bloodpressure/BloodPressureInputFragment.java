package com.medzone.cloud.ui.fragment.bloodpressure;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.LayoutParams;
import com.medzone.cloud.Constants;
import com.medzone.cloud.data.CurrentAccountManager;
import com.medzone.cloud.data.helper.MeasureDataUtil;
import com.medzone.cloud.module.modules.BloodPressureModule;
import com.medzone.cloud.ui.MeasureActivity;
import com.medzone.cloud.ui.dialog.error.ErrorDialogUtil;
import com.medzone.cloud.ui.fragment.BluetoothFragment;
import com.medzone.framework.data.bean.imp.BloodPressure;
import com.medzone.framework.data.bean.imp.BloodPressure.BloodPressureUtil;
import com.medzone.framework.errorcode.ProxyCode.LocalError;
import com.medzone.framework.errorcode.ProxyErrorCode;
import com.medzone.framework.util.NumberPickerUtil;
import com.medzone.mcloud.R;
import com.medzone.mcloud.R.drawable;
import com.medzone.mcloud.R.id;
import com.medzone.mcloud.R.layout;
import com.medzone.mcloud.R.string;

public class BloodPressureInputFragment extends BluetoothFragment implements
		View.OnClickListener {

	private MeasureActivity mActivity;
	private TextView tvRate, tvHigh, tvLow;

	private int inputRateValue = (int) BloodPressure.INVALID_ID;
	private float inputHighValue = BloodPressure.INVALID_ID;
	private float inputLowValue = BloodPressure.INVALID_ID;

	private boolean isKpa = false;
	private BloodPressureModule mModule;

	private void getArgument() {
		mModule = (BloodPressureModule) mActivity.getAttachModule();
		isKpa = mModule.isKpaMode();
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mActivity = (MeasureActivity) activity;
		getArgument();
	}

	@Override
	protected void initActionBar() {
		ActionBar actionBar = getSherlockActivity().getSupportActionBar();
		ActionBar.LayoutParams params = new ActionBar.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT,
				Gravity.CENTER);
		View view = LayoutInflater.from(getSherlockActivity()).inflate(
				R.layout.custom_actionbar_with_image, null);
		TextView title = (TextView) view.findViewById(R.id.actionbar_title);
		title.setText(CurrentAccountManager.getCurAccount().getFriendsDisplay(
				mActivity.getGroupmember()));

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
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		initActionBar();
		View view = inflater.inflate(layout.fragment_pressure_input, container,
				false);
		tvHigh = (TextView) view.findViewById(id.tv_input_high);
		tvLow = (TextView) view.findViewById(id.tv_input_low);
		tvRate = (TextView) view.findViewById(id.tv_input_rate);
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		tvHigh.setOnClickListener(this);
		tvLow.setOnClickListener(this);
		tvRate.setOnClickListener(this);
		NumberPickerUtil.loadActivityContext(mActivity);
	}

	private void toResultFragment() {
		sendStopMeasure();
		mActivity
				.setBluetoothState(MeasureActivity.BLUETOOTH_STATE_DISCONNTECTION);

		int errorCode = MeasureDataUtil.checkBloodPressureMeasureResult(
				inputHighValue, inputLowValue, inputRateValue);

		if (errorCode == LocalError.CODE_SUCCESS) {
			// TODO 是否也采用通过activity来完成
			BloodPressureResultFragment mResultFragment = new BloodPressureResultFragment();
			Bundle bundle = new Bundle();
			//TODO 传送测量值统一用mmhg的方式，在需要展示的地方根据单位显示不同的值
			bundle.putString(Constants.HIGH_PRESSURE,
					String.valueOf(inputHighValue));
			bundle.putString(Constants.LOW_PRESSURE,
					String.valueOf(inputLowValue));
			bundle.putString(Constants.RATE, String.valueOf(inputRateValue));
			mResultFragment.setArguments(bundle);
			FragmentManager fm = getFragmentManager();
			FragmentTransaction ft = fm.beginTransaction();
			ft.remove(this);
			ft.add(id.measure_container, mResultFragment);
			ft.disallowAddToBackStack();
			ft.commitAllowingStateLoss();
		} else {
			ErrorDialogUtil.showErrorToast(mActivity,
					ProxyErrorCode.TYPE_MEASURE, errorCode);
		}

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case id.actionbar_left:
			mActivity.comeBackBloodPressureConnect(this);
			break;
		case id.actionbar_right:
			toResultFragment();
			break;
		case id.tv_input_high:
			inputHigh();
			break;
		case id.tv_input_low:
			inputLow();
			break;
		case id.tv_input_rate:
			inputRate();
			break;
		default:
			break;
		}
	}

	private void inputHigh() {

		if (isKpa) {
			final float multiple = 10.0f;
			float defValue = BloodPressureUtil
					.convertMMHG2KPA(Constants.SYSTOLIC_PRESSURE_DEFAULT_HIGH);// default
			float minRange = BloodPressureUtil
					.convertMMHG2KPA(Constants.SYSTOLIC_PRESSURE_LOW);
			float maxRange = BloodPressureUtil
					.convertMMHG2KPA(Constants.SYSTOLIC_PRESSURE_HIGH);

			if (!tvHigh.getText().toString().isEmpty()) {
				defValue = inputHighValue;
			}
			final int curValue = (int) (defValue * multiple);
			NumberPickerUtil.showDoubleNumberPicker(curValue, minRange,
					maxRange, getString(string.systolic_pressure),
					getString(string.pressure_unit_kpa), multiple,
					new NumberPickerUtil.onDialogChooseListener() {

						@Override
						public void onConfirm(Object value) {
							tvHigh.setText(String.valueOf(value));
							// 传送测量值统一用mmhg的方式，在需要展示的地方根据单位显示不同的值
							inputHighValue = BloodPressureUtil
									.convertKPA2MMHG(Float
											.valueOf((String) value));
						}

						@Override
						public void onCancel() {

						}

					});

		} else {
			int defValue = Constants.SYSTOLIC_PRESSURE_DEFAULT_HIGH;
			if (!tvHigh.getText().toString().isEmpty()) {
				defValue = (int) inputHighValue;
			}
			NumberPickerUtil.showNumberPicker(defValue,
					Constants.SYSTOLIC_PRESSURE_LOW,
					Constants.SYSTOLIC_PRESSURE_HIGH,
					getString(string.systolic_pressure),
					getString(string.pressure_unit_mmhg),
					new NumberPickerUtil.onDialogChooseListener() {

						@Override
						public void onConfirm(Object value) {
							tvHigh.setText(String.valueOf(value));
							inputHighValue = (Integer) value;
						}

						@Override
						public void onCancel() {

						}
					});
		}
	}

	private void inputLow() {
		if (isKpa) {

			final float multiple = 10.0f;
			float defValue = BloodPressureUtil
					.convertMMHG2KPA(Constants.DIASTOLIC_PRESSURE_DEFAULT_LOW);
			float minRange = BloodPressureUtil
					.convertMMHG2KPA(Constants.SYSTOLIC_PRESSURE_LOW);
			float maxRange = BloodPressureUtil
					.convertMMHG2KPA(Constants.SYSTOLIC_PRESSURE_HIGH);
			if (!tvLow.getText().toString().isEmpty()) {
				defValue = inputLowValue;
			}
			final int curValue = (int) (defValue * multiple);
			NumberPickerUtil.showDoubleNumberPicker(curValue, minRange,
					maxRange, getString(string.diastolic_pressure),
					getString(string.pressure_unit_kpa), 10.0f,
					new NumberPickerUtil.onDialogChooseListener() {

						@Override
						public void onConfirm(Object value) {
							tvLow.setText(String.valueOf(value));
							// 传送测量值统一用mmhg的方式，在需要展示的地方根据单位显示不同的值
							inputLowValue = BloodPressureUtil
									.convertKPA2MMHG(Float
											.valueOf((String) value));
						}

						@Override
						public void onCancel() {

						}

					});
		} else {
			int defValue = Constants.DIASTOLIC_PRESSURE_DEFAULT_LOW;
			if (!tvLow.getText().toString().isEmpty()) {
				defValue = (int) inputLowValue;
			}
			NumberPickerUtil.showNumberPicker(defValue,
					Constants.DIASTOLIC_PRESSURE_LOW,
					Constants.DIASTOLIC_PRESSURE_HIGH,
					getString(string.diastolic_pressure),
					getString(string.pressure_unit_mmhg),
					new NumberPickerUtil.onDialogChooseListener() {

						@Override
						public void onConfirm(Object value) {
							tvLow.setText(String.valueOf(value));
							inputLowValue = (Integer) value;
						}

						@Override
						public void onCancel() {

						}
					});
		}
	}

	private void inputRate() {
		int defValue = Constants.BLOODPRESSURE_DEFAULT_RATE;
		if (!tvRate.getText().toString().isEmpty()) {
			defValue = Integer.valueOf(tvRate.getText().toString());
		}
		NumberPickerUtil.showNumberPicker(defValue, Constants.HEART_RATE_LOW,
				Constants.HEART_RATE_HIGH, getString(string.heart_rate),
				getString(string.heart_rate_unit),
				new NumberPickerUtil.onDialogChooseListener() {

					@Override
					public void onConfirm(Object value) {
						tvRate.setText(String.valueOf(value));
						inputRateValue = (Integer) value;
					}

					@Override
					public void onCancel() {

					}
				});
	}
}

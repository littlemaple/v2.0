package com.medzone.cloud.ui.fragment.bloodoxygen;

/**
 * 
 */
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
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
import com.medzone.cloud.ui.MeasureActivity;
import com.medzone.cloud.ui.dialog.error.ErrorDialogUtil;
import com.medzone.cloud.ui.fragment.BluetoothFragment;
import com.medzone.framework.errorcode.ProxyErrorCode;
import com.medzone.framework.util.NumberPickerUtil;
import com.medzone.mcloud.R;
import com.medzone.mcloud.R.drawable;
import com.medzone.mcloud.R.id;
import com.medzone.mcloud.R.layout;
import com.medzone.mcloud.R.string;

/**
 * @author ChenJunQi.
 * 
 */
public class BloodOxygenInputFragment extends BluetoothFragment implements
		View.OnClickListener {

	private TextView rateTV, oxygenTV;
	private String oxygen, rate;
	private MeasureActivity mActivity;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mActivity = (MeasureActivity) activity;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(layout.fragment_oxygen_input, container,
				false);
		rateTV = (TextView) view.findViewById(id.oxygen_input_rateTV);
		oxygenTV = (TextView) view.findViewById(id.oxygen_input_oxygenTV);
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		initActionBar();
		oxygenTV.setOnClickListener(this);
		rateTV.setOnClickListener(this);
		NumberPickerUtil.loadActivityContext(mActivity);
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
	public void onClick(View v) {
		switch (v.getId()) {
		case id.actionbar_left:
			mActivity.comeBackBloodOxygenConnect(this);
			break;
		case id.actionbar_right:
			toResultFragment();
			break;
		case id.oxygen_input_rateTV:
			inputRate();
			break;
		case id.oxygen_input_oxygenTV:
			inputOxygen();
			break;
		default:
			break;
		}
	}

	private void toResultFragment() {
		oxygen = oxygenTV.getText().toString().trim();
		rate = rateTV.getText().toString().trim();
		if (TextUtils.isEmpty(oxygen)) {
			ErrorDialogUtil.showErrorToast(mActivity,
					ProxyErrorCode.TYPE_MEASURE,
					ProxyErrorCode.LocalError.CODE_BLOOD_OXYGEN_EMPTY);
			return;
		}
		if (TextUtils.isEmpty(rate)) {
			ErrorDialogUtil.showErrorToast(mActivity,
					ProxyErrorCode.TYPE_MEASURE,
					ProxyErrorCode.LocalError.CODE_HEART_RATE_EMPTY);
			return;
		}
		sendStopMeasure();
		mActivity
				.setBluetoothState(MeasureActivity.BLUETOOTH_STATE_DISCONNTECTION);
		mActivity.setMeasureOrInput(Constants.INPUT);

		// TODO 这里是否也采用activity来实现
		BloodOxygenResultFragment mResultFragment = new BloodOxygenResultFragment();
		Bundle bundle = new Bundle();
		bundle.putString(Constants.OXYGEN, oxygen);
		bundle.putString(Constants.RATE, rate);
		mResultFragment.setArguments(bundle);
		FragmentManager fm = getFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		ft.remove(this);
		ft.add(id.measure_container, mResultFragment);
		ft.disallowAddToBackStack();
		ft.commitAllowingStateLoss();
	}

	private void inputOxygen() {
		int defValue = Constants.OXIMETRY_DEFAULT_HIGH;
		if (!oxygenTV.getText().toString().isEmpty()) {
			defValue = Integer.valueOf(oxygenTV.getText().toString());
		}
		NumberPickerUtil.showNumberPicker(defValue, Constants.OXIMETRY_LOW,
				Constants.OXIMETRY_HIGH, getString(string.blood_oxygen),
				getString(string.blood_oxygen_unit),
				new NumberPickerUtil.onDialogChooseListener() {

					@Override
					public void onConfirm(Object value) {
						oxygenTV.setText(String.valueOf(value));
					}

					@Override
					public void onCancel() {

					}
				});
	}

	private void inputRate() {
		int defValue = Constants.OXIMETRY_RATE;
		if (!rateTV.getText().toString().isEmpty()) {
			defValue = Integer.valueOf(rateTV.getText().toString());
		}
		NumberPickerUtil.showNumberPicker(defValue, Constants.HEART_RATE_LOW,
				Constants.HEART_RATE_HIGH, getString(string.heart_rate),
				getString(string.heart_rate_unit),
				new NumberPickerUtil.onDialogChooseListener() {

					@Override
					public void onConfirm(Object value) {
						rateTV.setText(String.valueOf(value));
					}

					@Override
					public void onCancel() {

					}
				});
	}

}

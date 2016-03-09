package com.medzone.cloud.ui.fragment.temperature;

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
import com.medzone.cloud.ui.MeasureActivity;
import com.medzone.cloud.ui.dialog.error.ErrorDialogUtil;
import com.medzone.framework.data.bean.imp.GroupMember;
import com.medzone.framework.errorcode.ProxyErrorCode;
import com.medzone.framework.fragment.BaseFragment;
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
public class EarTemperatureInputFragment extends BaseFragment implements
		View.OnClickListener {

	private TextView etTV;
	private MeasureActivity mActivity;
	private String temperature;
	private String[] displayedValues;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mActivity = (MeasureActivity) activity;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(layout.fragment_temperature_input,
				container, false);
		initActionBar();
		etTV = (TextView) view.findViewById(id.temperature_input_etTV);
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		etTV.setOnClickListener(this);
		// initNumberPicker();
		NumberPickerUtil.loadActivityContext(mActivity);
	}

	// private void initNumberPicker() {
	// NumberPickerUtil.loadActivityContext(mActivity);
	// displayedValues = new String[(int) (Constants.EAR_TEMPERATURE_HIGH * 10 -
	// Constants.EAR_TEMPERATURE_LOW * 10)];
	// for (int i = 0; i < displayedValues.length; i++) {
	// displayedValues[i] = String
	// .valueOf((float) (Constants.EAR_TEMPERATURE_LOW * 10 + i) / 10);
	// }
	//
	// }

	@Override
	protected void initActionBar() {
		ActionBar actionBar = getSherlockActivity().getSupportActionBar();
		ActionBar.LayoutParams params = new ActionBar.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT,
				Gravity.CENTER);
		View view = LayoutInflater.from(getSherlockActivity()).inflate(
				R.layout.custom_actionbar_with_image, null);
		TextView title = (TextView) view.findViewById(R.id.actionbar_title);
		GroupMember groupmember = mActivity.getGroupmember();
		if (groupmember.getRemark() != null) {
			title.setText(groupmember.getRemark());
		} else {
			title.setText(groupmember.getNickname());
		}
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

	private void toResultFragment() {
		temperature = etTV.getText().toString().trim();
		if (TextUtils.isEmpty(temperature)) {
			ErrorDialogUtil.showErrorToast(mActivity,
					ProxyErrorCode.TYPE_MEASURE,
					ProxyErrorCode.LocalError.CODE_TEMPERATURE_EMPTY);
			return;
		}
		EarTemperatureResultFragment fragment = new EarTemperatureResultFragment();
		Bundle bundle = new Bundle();
		bundle.putString(Constants.TEMPERATURE, temperature);
		fragment.setArguments(bundle);
		FragmentManager fm = getFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		ft.remove(this);
		ft.add(id.measure_container, fragment);
		ft.disallowAddToBackStack();
		ft.commitAllowingStateLoss();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case id.actionbar_left:
			mActivity.comeBackEarTemperatureConnect(this);
			break;
		case id.actionbar_right:
			toResultFragment();
			break;
		case id.temperature_input_etTV:
			inputTemperature();
			break;
		default:
			break;
		}
	}

	private void inputTemperature() {

		final float multiple = 10.0f;
		float defValue = Constants.EAR_TEMPERATURE_DEFAULT;
		if (!etTV.getText().toString().isEmpty()) {
			defValue = Float.valueOf(etTV.getText().toString());
		}
		final int curValue = (int) (defValue * multiple);
		NumberPickerUtil.showDoubleNumberPicker(curValue,
				Constants.EAR_TEMPERATURE_LOW, Constants.EAR_TEMPERATURE_HIGH,
				getString(string.ear_temperature),
				getString(string.ear_temperature_unit), multiple,
				new NumberPickerUtil.onDialogChooseListener() {

					@Override
					public void onConfirm(Object value) {
						etTV.setText(String.valueOf(value));
					}

					@Override
					public void onCancel() {
					}
				});

	}

}

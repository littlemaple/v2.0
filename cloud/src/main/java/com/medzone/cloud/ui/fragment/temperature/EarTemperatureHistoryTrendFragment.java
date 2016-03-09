package com.medzone.cloud.ui.fragment.temperature;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.LayoutParams;
import com.medzone.cloud.Constants;
import com.medzone.cloud.ui.MeasureDataActivity;
import com.medzone.cloud.ui.dialog.error.ErrorDialogUtil;
import com.medzone.framework.errorcode.ProxyCode.LocalError;
import com.medzone.framework.errorcode.ProxyErrorCode;
import com.medzone.framework.fragment.BaseFragment;
import com.medzone.framework.util.NetUtil;
import com.medzone.mcloud.R;
import com.medzone.mcloud.R.color;
import com.medzone.mcloud.R.drawable;
import com.medzone.mcloud.R.id;
import com.medzone.mcloud.R.layout;
import com.medzone.mcloud.R.string;

public class EarTemperatureHistoryTrendFragment extends BaseFragment implements
		View.OnClickListener {

	private TextView titleLeft, titleRight;
	private LinearLayout titleLL;
	private ActionBar actionBar;
	private MeasureDataActivity mdActivity;

	private boolean isRecentState = true;
	private ETMonthlyFragment monthFragment;
	private ETTrendFragment recentFragment;

	@Override
	protected void initActionBar() {
		actionBar = getSherlockActivity().getSupportActionBar();
		ActionBar.LayoutParams params = new ActionBar.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT,
				Gravity.CENTER);
		View view = LayoutInflater.from(getSherlockActivity()).inflate(
				R.layout.custom_actionbar_with_image_text, null);
		titleLeft = (TextView) view.findViewById(R.id.actionbar_title_left);
		titleRight = (TextView) view.findViewById(R.id.actionbar_title_right);
		titleLL = (LinearLayout) view.findViewById(R.id.actionbar_center);
		ImageButton rightButton = (ImageButton) view
				.findViewById(id.actionbar_right);
		ImageButton leftButton = (ImageButton) view
				.findViewById(id.actionbar_left);

		titleLeft.setText(getString(string.history_trend_recent));
		titleRight.setText(getString(string.history_trend_monthly_report));
		leftButton.setImageResource(drawable.public_ic_back);
		rightButton.setImageResource(drawable.detailsoftheresultsview_ic_share);

		titleRight.setOnClickListener(this);
		titleLeft.setOnClickListener(this);
		leftButton.setOnClickListener(this);
		rightButton.setOnClickListener(this);

		switchActionBar(isRecentState);

		actionBar.setCustomView(view, params);
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		actionBar.setDisplayShowCustomEnabled(true);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mdActivity = (MeasureDataActivity) activity;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		initActionBar();
		return inflater.inflate(layout.fragment_container, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if (mdActivity.getTrendCategory() == Constants.VALUE_MONTH) {
			switchMonthlyReportFragment();
		} else {
			switchRecentlyReportFragment();
		}
	}

	private void switchMonthlyReportFragment() {
		switchActionBar(false);

		FragmentManager fm = getChildFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		if (monthFragment == null) {
			monthFragment = new ETMonthlyFragment();
		}
		if (!monthFragment.isAdded()) {
			if (recentFragment != null) {
				ft.hide(recentFragment);
			}
			ft.add(id.fragment_container, monthFragment);
		} else {
			if (recentFragment != null) {
				ft.hide(recentFragment);
			}
			ft.show(monthFragment);
		}
		ft.commitAllowingStateLoss();

	}

	private void switchRecentlyReportFragment() {

		switchActionBar(true);

		FragmentManager fm = getChildFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		if (recentFragment == null) {
			recentFragment = new ETTrendFragment();
		}
		if (!recentFragment.isAdded()) {
			if (monthFragment != null) {
				ft.hide(monthFragment);
			}
			ft.add(id.fragment_container, recentFragment);
		} else {
			if (monthFragment != null) {
				ft.hide(monthFragment);
			}
			ft.show(recentFragment);
		}
		ft.commitAllowingStateLoss();
	}

	private void switchActionBar(final boolean isRecent) {
		if (isRecent) {
			titleLeft.setTextColor(getResources().getColor(
					color.font_measure_title_lightblue));
			titleRight.setTextColor(getResources().getColor(
					android.R.color.white));
			titleLL.setBackgroundResource(drawable.detectionhistory_switch_recent);
		} else {
			titleLeft.setTextColor(getResources().getColor(
					android.R.color.white));
			titleRight.setTextColor(getResources().getColor(
					color.font_measure_title_lightblue));
			titleLL.setBackgroundResource(drawable.detectionhistory_switch_monthly);
		}
		isRecentState = isRecent;
	}

	private void doShare() {

		if (!NetUtil.isConnect(getActivity())) {
			ErrorDialogUtil.showErrorDialog(getActivity(),
					ProxyErrorCode.TYPE_MEASURE, LocalError.CODE_18100, true);
		} else {

			if (isRecentState) {
				recentFragment.doShare();
			} else {
				monthFragment.doShare();
			}
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case id.actionbar_left:
			mdActivity.popBackStack();
			break;
		case id.actionbar_right:
			doShare();
			break;
		case id.actionbar_title_left:
			switchRecentlyReportFragment();
			break;
		case id.actionbar_title_right:
			switchMonthlyReportFragment();
			break;
		default:
			break;
		}
	}

}

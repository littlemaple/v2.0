package com.medzone.cloud.ui.fragment.bloodpressure;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.LayoutParams;
import com.medzone.cloud.Constants;
import com.medzone.cloud.GlobalVars;
import com.medzone.cloud.data.CurrentAccountManager;
import com.medzone.cloud.data.TemporaryData;
import com.medzone.cloud.data.helper.MeasureDataUtil;
import com.medzone.cloud.module.modules.BloodPressureModule;
import com.medzone.cloud.ui.MeasureDataActivity;
import com.medzone.cloud.ui.ShareActivity;
import com.medzone.cloud.ui.dialog.CloudShareDialogFactory;
import com.medzone.cloud.ui.dialog.error.ErrorDialogUtil;
import com.medzone.cloud.ui.widget.CloudWebView;
import com.medzone.framework.Log;
import com.medzone.framework.data.bean.imp.Account;
import com.medzone.framework.data.bean.imp.BloodPressure;
import com.medzone.framework.errorcode.ProxyCode;
import com.medzone.framework.errorcode.ProxyErrorCode;
import com.medzone.framework.fragment.BaseFragment;
import com.medzone.framework.network.NetworkClientResult;
import com.medzone.framework.task.BaseResult;
import com.medzone.framework.task.TaskHost;
import com.medzone.framework.util.TimeUtils;
import com.medzone.mcloud.R;
import com.medzone.mcloud.R.drawable;
import com.medzone.mcloud.R.id;
import com.medzone.mcloud.R.layout;
import com.medzone.mcloud.R.string;

public class BloodPressureResultDetailsFragment extends BaseFragment implements
		OnClickListener {
	private MeasureDataActivity mdActivity;
	private TextView highTV, lowTV, rateTV, hplTV, readMeTV, timeTV, timeOneTV,
			timeTwoTV, timeThreeTV, valueOneTV, valueTwoTV, valueThreeTV,
			highUnitTV, lowUnitTV, hplUnitTV;
	private ImageView flagIV, imageOne, imageTwo, imageThree;
	private CloudWebView suggestWV;
	private TextView suggestTV;
	private LinearLayout historyTrendLL, readMeLL;
	private Account account;
	private BloodPressureModule mModule;
	private BloodPressure bp;
	private List<BloodPressure> bloodPressures;

	private boolean isKpa;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mdActivity = (MeasureDataActivity) activity;
		account = CurrentAccountManager.getCurAccount();
		mModule = (BloodPressureModule) mdActivity.getAttachModule();
		isKpa = mModule.isKpaMode();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(layout.fragment_pressure_result_details,
				container, false);
		timeTV = (TextView) view
				.findViewById(id.pressure_result_details_time_tv);
		flagIV = (ImageView) view
				.findViewById(id.pressure_result_details_flag_iv);
		highTV = (TextView) view
				.findViewById(id.pressure_result_details_highTV);
		lowTV = (TextView) view.findViewById(id.pressure_result_details_lowTV);
		highUnitTV = (TextView) view
				.findViewById(id.pressure_result_details_high_unitTV);
		lowUnitTV = (TextView) view
				.findViewById(id.pressure_result_details_low_unitTV);
		hplUnitTV = (TextView) view
				.findViewById(id.pressure_result_details_hpl_unitTV);
		rateTV = (TextView) view
				.findViewById(id.pressure_result_details_rateTV);
		readMeTV = (TextView) view
				.findViewById(id.pressure_result_details_readmeTV);
		suggestTV = (TextView) view
				.findViewById(id.pressure_result_details_suggestTV);
		suggestWV = (CloudWebView) view
				.findViewById(id.pressure_result_details_suggestWV);
		historyTrendLL = (LinearLayout) view
				.findViewById(id.pressure_result_details_history_trend);
		readMeLL = (LinearLayout) view
				.findViewById(id.pressure_result_details_readmeLL);
		hplTV = (TextView) view.findViewById(id.pressure_result_details_hplTV);
		imageOne = (ImageView) view
				.findViewById(id.pressure_result_details_history_trend_image_one);
		imageTwo = (ImageView) view
				.findViewById(id.pressure_result_details_history_trend_image_two);
		imageThree = (ImageView) view
				.findViewById(id.pressure_result_details_history_trend_image_three);
		timeOneTV = (TextView) view
				.findViewById(id.pressure_result_details_history_trend_time_one);
		timeTwoTV = (TextView) view
				.findViewById(id.pressure_result_details_history_trend_time_two);
		timeThreeTV = (TextView) view
				.findViewById(id.pressure_result_details_history_trend_time_three);
		valueOneTV = (TextView) view
				.findViewById(id.pressure_result_details_history_trend_value_one);
		valueTwoTV = (TextView) view
				.findViewById(id.pressure_result_details_history_trend_value_two);
		valueThreeTV = (TextView) view
				.findViewById(id.pressure_result_details_history_trend_value_three);
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		fillView();
		getDoctorAdvised();
		getNearlyRecord();

	}

	private void fillView() {
		if (mdActivity.isSourceHistory()) {
			historyTrendLL.setVisibility(View.GONE);
			bp = mdActivity.bpFromHistory;
		} else {
			bp = mdActivity.bpFromHome;
			historyTrendLL.setOnClickListener(this);
		}

		timeTV.setText(TimeUtils.getYearToSecond(bp.getMeasureTime()));
		if (isKpa) {

			float highKpa = bp.getHighKPA();
			float lowKpa = bp.getLowKPA();
			float hplKpa = Float.parseFloat(new DecimalFormat("0.0")
					.format(highKpa - lowKpa));
			highTV.setText(String.valueOf(highKpa));
			lowTV.setText(String.valueOf(lowKpa));
			hplTV.setText(String.valueOf(hplKpa));
			highUnitTV.setText(getString(string.pressure_unit_kpa));
			lowUnitTV.setText(getString(string.pressure_unit_kpa));
			hplUnitTV.setText(getString(string.pressure_unit_kpa));
		} else {
			highTV.setText(String.valueOf(bp.getHigh().intValue()));
			lowTV.setText(String.valueOf(bp.getLow().intValue()));
			hplTV.setText(String.valueOf((bp.getHigh().intValue() - bp.getLow()
					.intValue())));
		}
		rateTV.setText(String.valueOf(bp.getRate()));
		String readMe = bp.getReadme();
		if (TextUtils.isEmpty(readMe)) {
			readMeLL.setVisibility(View.GONE);
		} else {
			readMeTV.setText(readMe);
		}
		MeasureDataUtil.BloodPressureFlag(flagIV, bp.getAbnormal());

	}

	@Override
	protected void initActionBar() {
		ActionBar actionBar = getSherlockActivity().getSupportActionBar();
		ActionBar.LayoutParams params = new ActionBar.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT,
				Gravity.CENTER);
		View view = LayoutInflater.from(getSherlockActivity()).inflate(
				R.layout.custom_actionbar_with_image, null);
		ImageButton leftButton = (ImageButton) view
				.findViewById(id.actionbar_left);
		leftButton.setImageResource(drawable.public_ic_back);
		leftButton.setOnClickListener(this);
		ImageButton rightButton = (ImageButton) view
				.findViewById(id.actionbar_right);
		rightButton.setImageResource(drawable.detailsoftheresultsview_ic_share);
		rightButton.setOnClickListener(this);
		TextView title = (TextView) view.findViewById(R.id.actionbar_title);
		title.setText(getString(string.result_details_title));
		actionBar.setCustomView(view, params);
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		actionBar.setDisplayShowCustomEnabled(true);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case id.pressure_result_details_history_trend:

			mdActivity.setTrendCategory(Constants.VALUE_RECENT);
			mdActivity.toBloodPressureHistoryTrendFragment();
			break;
		case id.actionbar_left:
			mdActivity.popBackStack();
			break;
		case id.actionbar_right:
			doShare();
			break;
		default:
			break;
		}
	}

	private boolean isNetAvaliable() {
		ConnectivityManager connectivityManager = (ConnectivityManager) getActivity()
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = connectivityManager.getActiveNetworkInfo();
		if (info != null && info.isAvailable()) {
			return true;
		} else
			return false;
	}

	private void doShare() {
		// FragmentTransaction ft = getFragmentManager().beginTransaction();
		// CustomPlatformFragment newFragment = new CustomPlatformFragment();
		// newFragment.show(ft, CustomPlatformFragment.class.getName());

		String uid = bp.getMeasureUID();
		BloodPressure tmp = mModule.getCacheController()
				.getMeasureBloodPressure(uid);

		if (!isNetAvaliable()) {
			ErrorDialogUtil.showErrorDialog(getActivity(),
					ProxyErrorCode.TYPE_MEASURE,
					ProxyCode.LocalError.CODE_18100, true);
		} else {
			if (tmp.getRecordID() == null) {
				ErrorDialogUtil.showErrorDialog(getActivity(),
						ProxyErrorCode.TYPE_MEASURE,
						ProxyCode.LocalError.CODE_18101, true);
			} else {
				Intent intent = new Intent(getActivity(), ShareActivity.class);
				startActivity(intent);

				// 为了能够准确分享，需要传递如下值
				TemporaryData.save(BloodPressure.class.getName(), bp);
				TemporaryData.save(Constants.TEMPORARYDATA_KEY_SHARE_TYPE,
						CloudShareDialogFactory.SHARE_TYPE_SINGLE);
				TemporaryData.save(Constants.TEMPORARYDATA_KEY_MEASURE_TYPE,
						BloodPressureModule.TYPE);
			}
		}

	}

	private void getDoctorAdvised() {
		mModule.getDoctorAdvised(bp, DoctorAdvisedTaskHost);
	}

	private TaskHost DoctorAdvisedTaskHost = new TaskHost() {
		@Override
		public void onPostExecute(int requestCode, BaseResult result) {
			super.onPostExecute(requestCode, result);
			if (result.isSuccess()) {
				if (result.isServerDisposeSuccess()) {
					NetworkClientResult temp = (NetworkClientResult) result;
					JSONObject json = temp.getResponseResult();
					String url = null;
					try {
						url = json.getString("url");
					} catch (JSONException e) {
						e.printStackTrace();
					}
					suggestWV.loadUrl(GlobalVars.formatWebSite(url));
				} else {
					ErrorDialogUtil.showErrorToast(mdActivity,
							ProxyErrorCode.TYPE_MEASURE, result.getErrorCode());
				}
			} else {
				suggestWV.setVisibility(View.GONE);
				suggestTV.setVisibility(View.VISIBLE);
			}
		}
	};

	private void getNearlyRecord() {
		mModule.getNearlyRecord(account, bp, NearlyRecordTaskHost);
	}

	private TaskHost NearlyRecordTaskHost = new TaskHost() {
		@Override
		public void onPostExecute(int requestCode, BaseResult result) {
			super.onPostExecute(requestCode, result);
			if (result.isSuccess()) {
				if (result.isServerDisposeSuccess()) {
					bloodPressures = new ArrayList<BloodPressure>();
					NetworkClientResult temp = (NetworkClientResult) result;
					JSONObject json = temp.getResponseResult();
					try {
						JSONArray jsons = json.getJSONArray("down");
						int length = jsons != null ? jsons.length() : 0;
						for (int i = 0; i < length; i++) {
							BloodPressure bloodPressure = new BloodPressure();
							json = jsons.getJSONObject(i);
							String measureUid = json.getString("measureuid");
							bloodPressure.setMeasureTime(TimeUtils
									.getMillisecondDate(measureUid.substring(0,
											measureUid.length() - 6)));
							bloodPressure.setHigh(Float.valueOf(json
									.getString("value1")));
							bloodPressure.setLow(Float.valueOf(json
									.getString("value2")));
							bloodPressures.add(bloodPressure);
						}

						fillNearlyThreeTimesView();
					} catch (Exception e) {
						e.printStackTrace();
					}

				} else {
					ErrorDialogUtil.showErrorToast(mdActivity,
							ProxyErrorCode.TYPE_MEASURE, result.getErrorCode());
				}
			}
		}
	};

	private String getPressureString(BloodPressure bp) {
		String ret = new String("");
		if (isKpa) {
			float highValue = bp.getHighKPA();
			float lowValue = bp.getLowKPA();
			ret = highValue + "/" + lowValue;
		} else {
			int high = bp.getHigh().intValue();
			int low = bp.getLow().intValue();
			ret = high + "/" + low;
		}
		ret += bp.getPressureUnit(isKpa);
		return ret;
	}

	private void fillNearlyThreeTimesView() {
		if (bloodPressures != null && bloodPressures.size() > 0) {
			timeOneTV.setText(TimeUtils.getYearToSecond(bloodPressures.get(0)
					.getMeasureTime()));
			valueOneTV.setText(getPressureString(bloodPressures.get(0)));

			if (bloodPressures.size() > 1) {
				timeTwoTV.setText(TimeUtils.getYearToSecond(bloodPressures.get(
						1).getMeasureTime()));
				valueTwoTV.setText(getPressureString(bloodPressures.get(1)));
			} else {
				imageTwo.setVisibility(View.GONE);
				imageThree.setVisibility(View.GONE);
				timeTwoTV
						.setText(getString(string.result_details_nearly_three_time_tv));
				timeThreeTV
						.setText(getString(string.result_details_nearly_three_time_tv));
			}
			if (bloodPressures.size() > 2) {
				timeThreeTV.setText(TimeUtils.getYearToSecond(bloodPressures
						.get(2).getMeasureTime()));
				valueThreeTV.setText(getPressureString(bloodPressures.get(2)));
			} else {
				imageThree.setVisibility(View.GONE);
				timeThreeTV
						.setText(getString(string.result_details_nearly_three_time_tv));
			}
		} else {
			imageOne.setVisibility(View.GONE);
			imageTwo.setVisibility(View.GONE);
			imageThree.setVisibility(View.GONE);
			timeOneTV
					.setText(getString(string.result_details_nearly_three_time_tv));
			timeTwoTV
					.setText(getString(string.result_details_nearly_three_time_tv));
			timeThreeTV
					.setText(getString(string.result_details_nearly_three_time_tv));
		}
	}

	@Override
	public void onDestroyView() {
		// TODO Auto-generated method stub
		super.onDestroyView();
		mdActivity.setSourceHistory(false);
	}

}

package com.medzone.cloud.ui.fragment.bloodoxygen;

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
import com.medzone.cloud.module.CloudMeasureModuleCentreRoot;
import com.medzone.cloud.module.modules.BloodOxygenModule;
import com.medzone.cloud.ui.MeasureDataActivity;
import com.medzone.cloud.ui.ShareActivity;
import com.medzone.cloud.ui.dialog.CloudShareDialogFactory;
import com.medzone.cloud.ui.dialog.error.ErrorDialogUtil;
import com.medzone.cloud.ui.widget.CloudWebView;
import com.medzone.framework.data.bean.imp.Account;
import com.medzone.framework.data.bean.imp.BloodOxygen;
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

public class BloodOxygenResultDetailsFragment extends BaseFragment implements
		OnClickListener {
	private MeasureDataActivity mdActivity;
	private TextView oxygenTV, rateTV, timeTV, readMeTV, timeOneTV, timeTwoTV,
			timeThreeTV, valueOneTV, valueTwoTV, valueThreeTV;;
	private ImageView flagIV, imageOne, imageTwo, imageThree;
	private CloudWebView suggestWV;
	private TextView suggestTV;
	private LinearLayout historyTrendLL, readMeLL;
	private BloodOxygen bo;
	private List<BloodOxygen> bloodOxygens;
	private BloodOxygenModule mModule;
	private Account account;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		account = CurrentAccountManager.getCurAccount();
		mdActivity = (MeasureDataActivity) activity;
		mModule = (BloodOxygenModule) mdActivity.getAttachModule();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(layout.fragment_oxygen_result_details,
				container, false);
		initActionBar();
		oxygenTV = (TextView) view
				.findViewById(id.oxygen_result_details_oxygenTV);
		rateTV = (TextView) view.findViewById(id.oxygen_result_details_rateTV);
		suggestWV = (CloudWebView) view
				.findViewById(id.oxygen_result_details_suggestWV);
		suggestTV = (TextView) view
				.findViewById(id.oxygen_result_details_suggestTV);
		timeTV = (TextView) view.findViewById(id.oxygen_result_details_time_tv);
		readMeTV = (TextView) view
				.findViewById(id.oxygen_result_details_readmeTV);
		historyTrendLL = (LinearLayout) view
				.findViewById(id.oxygen_result_details_history_trend);
		readMeLL = (LinearLayout) view
				.findViewById(id.oxygen_result_details_readmeLL);
		flagIV = (ImageView) view
				.findViewById(id.oxygen_result_details_flag_iv);
		imageOne = (ImageView) view
				.findViewById(id.oxygen_result_details_history_trend_image_one);
		imageTwo = (ImageView) view
				.findViewById(id.oxygen_result_details_history_trend_image_two);
		imageThree = (ImageView) view
				.findViewById(id.oxygen_result_details_history_trend_image_three);
		timeOneTV = (TextView) view
				.findViewById(id.oxygen_result_details_history_trend_time_one);
		timeTwoTV = (TextView) view
				.findViewById(id.oxygen_result_details_history_trend_time_two);
		timeThreeTV = (TextView) view
				.findViewById(id.oxygen_result_details_history_trend_time_three);
		valueOneTV = (TextView) view
				.findViewById(id.oxygen_result_details_history_trend_value_one);
		valueTwoTV = (TextView) view
				.findViewById(id.oxygen_result_details_history_trend_value_two);
		valueThreeTV = (TextView) view
				.findViewById(id.oxygen_result_details_history_trend_value_three);
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
			bo = mdActivity.boFromHistory;
			historyTrendLL.setVisibility(View.GONE);
		} else {
			bo = mdActivity.boFromHome;
			historyTrendLL.setOnClickListener(this);
		}
		timeTV.setText(TimeUtils.getYearToSecond(bo.getMeasureTime()));
		oxygenTV.setText(String.valueOf(bo.getOxygen()));
		rateTV.setText(String.valueOf(bo.getRate()));
		String readMe = bo.getReadme();
		if (TextUtils.isEmpty(readMe)) {
			readMeLL.setVisibility(View.GONE);
		} else {
			readMeTV.setText(readMe);
		}
		MeasureDataUtil.BloodOxygenFlag(flagIV, bo.getAbnormal());
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
		case id.oxygen_result_details_history_trend:
			mdActivity.setTrendCategory(Constants.VALUE_RECENT);
			mdActivity.toBloodOxygenHistoryTrendFragment();
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

		String uid = bo.getMeasureUID();

		BloodOxygen tmpOxygen = mModule.getCacheController()
				.getMeasureBloodOxygen(uid);

		if(!isNetAvaliable()){
			ErrorDialogUtil.showErrorDialog(getActivity(),
					ProxyErrorCode.TYPE_MEASURE,
					ProxyCode.LocalError.CODE_18100, true);
		}else{
			if (tmpOxygen.getRecordID() == null) {
				ErrorDialogUtil.showErrorDialog(getActivity(),
						ProxyErrorCode.TYPE_MEASURE,
						ProxyCode.LocalError.CODE_18101, true);
			} else {
				// 为了能够准确分享，需要传递如下值
				TemporaryData.save(BloodOxygen.class.getName(), bo);
				TemporaryData.save(Constants.TEMPORARYDATA_KEY_SHARE_TYPE,
						CloudShareDialogFactory.SHARE_TYPE_SINGLE);
				TemporaryData.save(Constants.TEMPORARYDATA_KEY_MEASURE_TYPE,
						BloodOxygenModule.TYPE);
	
				// FragmentTransaction ft = getFragmentManager().beginTransaction();
				// CustomPlatformFragment newFragment = new
				// CustomPlatformFragment();
				// newFragment.show(ft, CustomPlatformFragment.class.getName());
				Intent intent = new Intent(getActivity(), ShareActivity.class);
				startActivity(intent);
		}
		}
		
		
	}

	private void getDoctorAdvised() {
		mModule.getDoctorAdvised(bo, DoctorAdvisedTaskHost);
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
		mModule.getNearlyRecord(account, bo, NearlRecordTaskHost);
	}

	private TaskHost NearlRecordTaskHost = new TaskHost() {
		@Override
		public void onPostExecute(int requestCode, BaseResult result) {
			super.onPostExecute(requestCode, result);
			if (result.isSuccess()) {
				if (result.isServerDisposeSuccess()) {
					bloodOxygens = new ArrayList<BloodOxygen>();
					NetworkClientResult temp = (NetworkClientResult) result;
					JSONObject json = temp.getResponseResult();
					try {
						JSONArray jsons = json.getJSONArray("down");
						int length = jsons != null ? jsons.length() : 0;
						for (int i = 0; i < length; i++) {
							BloodOxygen bloodOxygen = new BloodOxygen();
							json = jsons.getJSONObject(i);
							String measureUid = json.getString("measureuid");
							bloodOxygen.setMeasureTime(TimeUtils
									.getMillisecondDate(measureUid.substring(0,
											measureUid.length() - 6)));
							bloodOxygen.setOxygen(Integer.valueOf(json
									.getString("value1")));
							bloodOxygen.setRate(Integer.valueOf(json
									.getString("value2")));
							bloodOxygens.add(bloodOxygen);
						}
						fillNearlyThreeTimesView();
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else {
					ErrorDialogUtil.showErrorToast(mdActivity,
							ProxyErrorCode.TYPE_MEASURE, result.getErrorCode());
				}
			} else {
				// ErrorDialogUtil.showErrorToast(mdActivity,
				// ProxyErrorCode.TYPE_MEASURE,
				// ProxyErrorCode.LocalError.CODE_10002);
			}
		}
	};

	private void fillNearlyThreeTimesView() {
		String oxygenStr = getString(string.blood_oxygen);
		String oxygenUnit = getString(string.blood_oxygen_unit);
		String heartUnit = getString(string.heart_rate_unit);
		if (bloodOxygens != null && bloodOxygens.size() > 0) {
			timeOneTV.setText(TimeUtils.getYearToSecond(bloodOxygens.get(0)
					.getMeasureTime()));
			valueOneTV.setText(oxygenStr + bloodOxygens.get(0).getOxygen()
					+ oxygenUnit + "  " + bloodOxygens.get(0).getRate()
					+ heartUnit);
			if (bloodOxygens.size() > 1) {
				timeTwoTV.setText(TimeUtils.getYearToSecond(bloodOxygens.get(1)
						.getMeasureTime()));
				valueTwoTV.setText(oxygenStr + bloodOxygens.get(1).getOxygen()
						+ oxygenUnit + "  " + bloodOxygens.get(1).getRate()
						+ heartUnit);
			} else {
				imageTwo.setVisibility(View.GONE);
				imageThree.setVisibility(View.GONE);
				timeTwoTV
						.setText(getString(string.result_details_nearly_three_time_tv));
				timeThreeTV
						.setText(getString(string.result_details_nearly_three_time_tv));
			}
			if (bloodOxygens.size() > 2) {
				timeThreeTV.setText(TimeUtils.getYearToSecond(bloodOxygens.get(
						2).getMeasureTime()));

				valueThreeTV.setText(oxygenStr
						+ bloodOxygens.get(2).getOxygen() + oxygenUnit + "  "
						+ bloodOxygens.get(2).getRate() + heartUnit);
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

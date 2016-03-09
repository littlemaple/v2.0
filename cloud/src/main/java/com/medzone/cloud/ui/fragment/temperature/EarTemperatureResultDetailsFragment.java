package com.medzone.cloud.ui.fragment.temperature;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
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
import com.medzone.cloud.module.modules.EarTemperatureModule;
import com.medzone.cloud.task.DoRuleMatchTask;
import com.medzone.cloud.task.GetRecordTask;
import com.medzone.cloud.ui.MeasureDataActivity;
import com.medzone.cloud.ui.ShareActivity;
import com.medzone.cloud.ui.dialog.CloudShareDialogFactory;
import com.medzone.cloud.ui.dialog.error.ErrorDialogUtil;
import com.medzone.cloud.ui.widget.CloudWebView;
import com.medzone.framework.data.bean.imp.Account;
import com.medzone.framework.data.bean.imp.EarTemperature;
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

public class EarTemperatureResultDetailsFragment extends BaseFragment implements
		OnClickListener {
	private MeasureDataActivity mdActivity;
	private TextView etTV, readMeTV, timeTV, timeOneTV, timeTwoTV, timeThreeTV,
			valueOneTV, valueTwoTV, valueThreeTV;
	private CloudWebView suggestWV;
	private LinearLayout suggestLL;
	private ImageView flagIV, imageOne, imageTwo, imageThree;
	private LinearLayout historyTrendLL, readMeLL;
	private Account account;
	private List<EarTemperature> ets;
	private EarTemperature et;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mdActivity = (MeasureDataActivity) activity;
		account = CurrentAccountManager.getCurAccount();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(
				layout.fragment_temperature_result_details, container, false);
		initActionBar();
		etTV = (TextView) view.findViewById(id.et_result_details_etTV);
		timeTV = (TextView) view.findViewById(id.et_result_details_time_tv);
		historyTrendLL = (LinearLayout) view
				.findViewById(id.et_result_details_history_trend);
		readMeLL = (LinearLayout) view
				.findViewById(id.et_result_details_readmeLL);
		readMeTV = (TextView) view.findViewById(id.et_result_details_readmeTV);
		flagIV = (ImageView) view.findViewById(id.et_result_details_flag_iv);
		suggestWV = (CloudWebView) view
				.findViewById(id.et_result_details_suggestWV);
		suggestLL = (LinearLayout) view
				.findViewById(id.et_result_details_suggestLL);
		imageOne = (ImageView) view
				.findViewById(id.et_result_details_history_trend_image_one);
		imageTwo = (ImageView) view
				.findViewById(id.et_result_details_history_trend_image_two);
		imageThree = (ImageView) view
				.findViewById(id.et_result_details_history_trend_image_three);
		timeOneTV = (TextView) view
				.findViewById(id.et_result_details_history_trend_time_one);
		timeTwoTV = (TextView) view
				.findViewById(id.et_result_details_history_trend_time_two);
		timeThreeTV = (TextView) view
				.findViewById(id.et_result_details_history_trend_time_three);
		valueOneTV = (TextView) view
				.findViewById(id.et_result_details_history_trend_value_one);
		valueTwoTV = (TextView) view
				.findViewById(id.et_result_details_history_trend_value_two);
		valueThreeTV = (TextView) view
				.findViewById(id.et_result_details_history_trend_value_three);

		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		fillView();
		doRuleMatch();
		GetRecord();
	}

	private void fillView() {
		if (mdActivity.isSourceHistory()) {
			historyTrendLL.setVisibility(View.GONE);
			et = mdActivity.etFromHistory;
		} else {
			historyTrendLL.setOnClickListener(this);
			et = mdActivity.etFromHome;
		}

		timeTV.setText(TimeUtils.getYearToSecond(et.getMeasureTime()));
		etTV.setText(et.getTemperature() + "");

		String readMe = et.getReadme();
		if (TextUtils.isEmpty(readMe)) {
			readMeLL.setVisibility(View.GONE);
		} else {
			readMeTV.setText(readMe);
		}
		if (et.getAbnormal() == EarTemperature.TEMPERATURE_STATE_LOW) {
			flagIV.setImageResource(drawable.testresultsview_testresult_graph_dire);
		} else if (et.getAbnormal() == EarTemperature.TEMPERATURE_STATE_NORMAL) {
			flagIV.setImageResource(drawable.testresultsview_testresult_graph_normal);
		} else if (et.getAbnormal() == EarTemperature.TEMPERATURE_STATE_FEVER) {
			flagIV.setImageResource(drawable.testresultsview_testresult_graph_fare);
		} else if (et.getAbnormal() == EarTemperature.TEMPERATURE_STATE_HIGH_FEVER) {
			flagIV.setImageResource(drawable.testresultsview_testresult_graph_gaore);
		}

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
		case id.et_result_details_history_trend:

			mdActivity.setTrendCategory(Constants.VALUE_RECENT);
			mdActivity.toEarTemperatureHistoryTrendFragment();
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

	private void doShare() {

		// FragmentTransaction ft = getFragmentManager().beginTransaction();
		// CustomPlatformFragment newFragment = new CustomPlatformFragment();
		// newFragment.show(ft, CustomPlatformFragment.class.getName());

		Intent intent = new Intent(getActivity(), ShareActivity.class);
		startActivity(intent);

		// 为了能够准确分享，需要传递如下值
		TemporaryData.save(EarTemperature.class.getName(), et);
		TemporaryData.save(Constants.TEMPORARYDATA_KEY_SHARE_TYPE,
				CloudShareDialogFactory.SHARE_TYPE_SINGLE);
		TemporaryData.save(Constants.TEMPORARYDATA_KEY_MEASURE_TYPE,
				EarTemperatureModule.TYPE);
	}

	private void doRuleMatch() {
		DoRuleMatchTask task = new DoRuleMatchTask(getActivity(),
				CurrentAccountManager.getCurAccount().getAccountID(), 3,
				et.getTemperature(), null, null);
		task.setTaskHost(new TaskHost() {
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
								ProxyErrorCode.TYPE_MEASURE,
								result.getErrorCode());
					}
				} else {
					suggestLL.setVisibility(View.GONE);
					ErrorDialogUtil
							.showErrorToast(
									mdActivity,
									ProxyErrorCode.TYPE_MEASURE,
									ProxyErrorCode.LocalError.CODE_CANNOT_DISPLAY_SUGGEST);
				}
			}
		});
		task.execute();
	}

	private void GetRecord() {
		GetRecordTask task = null;
		// TODO move to controller
		task = new GetRecordTask(getActivity(), account.getAccessToken(), "et",
				"-" + et.getMeasureTime(), -1, -1, 3, 0, "desc ", null);
		task.setTaskHost(new TaskHost() {
			@Override
			public void onPostExecute(int requestCode, BaseResult result) {
				super.onPostExecute(requestCode, result);
				if (result.isSuccess()) {
					if (result.isServerDisposeSuccess()) {
						ets = new ArrayList<EarTemperature>();
						NetworkClientResult temp = (NetworkClientResult) result;
						JSONObject json = temp.getResponseResult();
						try {
							JSONArray jsons = json.getJSONArray("down");
							int length = jsons != null ? jsons.length() : 0;
							for (int i = 0; i < length; i++) {
								EarTemperature et = new EarTemperature();
								json = jsons.getJSONObject(i);
								String measureUid = json
										.getString("measureuid");
								et.setMeasureTime(Long.valueOf(measureUid
										.substring(0, measureUid.length() - 6)));
								et.setTemperature(Float.valueOf(json
										.getString("value1")));
								ets.add(et);
							}

							fillNearlyThreeTimesView();
						} catch (Exception e) {
							e.printStackTrace();
						}

					} else {
						ErrorDialogUtil.showErrorToast(mdActivity,
								ProxyErrorCode.TYPE_MEASURE,
								result.getErrorCode());
					}
				} else {
					ErrorDialogUtil.showErrorToast(mdActivity,
							ProxyErrorCode.TYPE_MEASURE,
							ProxyErrorCode.LocalError.CODE_10002);
				}
			}
		});
		task.execute();
	}

	private void fillNearlyThreeTimesView() {
		String etUnit = getString(string.ear_temperature_unit);
		if (ets != null && ets.size() > 0) {
			timeOneTV.setText(TimeUtils.getYearToSecond(ets.get(0)
					.getMeasureTime()));
			valueOneTV.setText(ets.get(0).getTemperature() + etUnit);
			if (ets.size() > 1) {
				timeTwoTV.setText(TimeUtils.getYearToSecond(ets.get(1)
						.getMeasureTime()));
				valueTwoTV.setText(ets.get(1).getTemperature() + etUnit);
			} else {
				imageTwo.setVisibility(View.GONE);
				imageThree.setVisibility(View.GONE);
				timeTwoTV
						.setText(getString(string.result_details_nearly_three_time_tv));
				timeThreeTV
						.setText(getString(string.result_details_nearly_three_time_tv));
			}
			if (ets.size() > 2) {
				timeThreeTV.setText(TimeUtils.getYearToSecond(ets.get(2)
						.getMeasureTime()));
				valueThreeTV.setText(ets.get(2).getTemperature() + etUnit);
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

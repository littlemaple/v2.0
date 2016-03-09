package com.medzone.cloud.ui.fragment.temperature;

/**
 * 
 */
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.LayoutParams;
import com.medzone.cloud.Constants;
import com.medzone.cloud.controller.RuleController;
import com.medzone.cloud.data.CurrentAccountManager;
import com.medzone.cloud.data.helper.FlagHelper;
import com.medzone.cloud.module.modules.EarTemperatureModule;
import com.medzone.cloud.task.RecordUploadTask;
import com.medzone.cloud.ui.MeasureActivity;
import com.medzone.cloud.ui.MeasureDataActivity;
import com.medzone.cloud.ui.dialog.error.ErrorDialogUtil;
import com.medzone.cloud.ui.fragment.BaseResultFragment;
import com.medzone.cloud.ui.widget.CleanableEditText;
import com.medzone.cloud.ui.widget.CustomLinearLayout;
import com.medzone.cloud.ui.widget.CustomLinearLayout.OnResizeListener;
import com.medzone.cloud.ui.widget.ErrorDialog;
import com.medzone.cloud.ui.widget.ErrorDialog.ErrorDialogListener;
import com.medzone.cloud.util.TextWatcherUtil;
import com.medzone.cloud.util.VoiceMaterialUtil;
import com.medzone.common.media.bean.Media;
import com.medzone.framework.data.bean.imp.BaseMeasureData;
import com.medzone.framework.data.bean.imp.EarTemperature;
import com.medzone.framework.data.bean.imp.GroupMember;
import com.medzone.framework.data.bean.imp.Rule;
import com.medzone.framework.errorcode.ProxyErrorCode;
import com.medzone.framework.task.BaseResult;
import com.medzone.framework.task.TaskHost;
import com.medzone.mcloud.R;
import com.medzone.mcloud.R.drawable;
import com.medzone.mcloud.R.id;
import com.medzone.mcloud.R.layout;
import com.medzone.mcloud.R.string;

/**
 * @author ChenJunQi.
 * 
 */
public class EarTemperatureResultFragment extends BaseResultFragment implements
		View.OnClickListener {

	private LinearLayout completeLL, againLL;
	private CustomLinearLayout customLinearLayout;
	private TextView etTV, againTV;
	private String temperature, readMe;
	private EarTemperatureModule mModule;
	private MeasureActivity mActivity;
	private CleanableEditText readmeET;
	private ImageView flagIV;

	private AudioManager audioManager;
	private Dialog dialog;

	private long measureTime;
	private String measureId;
	private int state;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mActivity = (MeasureActivity) activity;
		mModule = (EarTemperatureModule) mActivity.getAttachModule();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(layout.fragment_temperature_result,
				container, false);
		etTV = (TextView) view.findViewById(id.et_result_temperatureTV);
		completeLL = (LinearLayout) view
				.findViewById(id.measure_bottom_completeLL);
		againLL = (LinearLayout) view.findViewById(id.measure_bottom_againLL);
		againTV = (TextView) view.findViewById(id.measure_bottom_againTV);
		flagIV = (ImageView) view.findViewById(id.et_result_flag_iv);
		readmeET = (CleanableEditText) view
				.findViewById(id.ce_et_result_readme);
		customLinearLayout = (CustomLinearLayout) view
				.findViewById(id.et_container);
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		if (mActivity.measureOrInput.equals(Constants.MEASURE)) {
			againTV.setText(getString(string.remeasure));
		} else if (mActivity.measureOrInput.equals(Constants.INPUT)) {
			againTV.setText(getString(string.reinput));
		}
		temperature = getArguments().getString(Constants.TEMPERATURE);
		temperature = round(temperature);
		etTV.setText(temperature);
		completeLL.setOnClickListener(this);
		againLL.setOnClickListener(this);
		readmeET.addTextChangedListener(new TextWatcherUtil(mActivity, readmeET));
		customLinearLayout.addOnCustomChangeListener(new MyOnResizeListener());
		measureTime = BaseMeasureData.Util.createMeasureTime();
		measureId = BaseMeasureData.Util.createMeasureID();
		doRuleMatch();
	}

	private class MyOnResizeListener implements OnResizeListener {

		@Override
		public void OnResize(int w, int h, int oldw, int oldh) {
			if (oldh != 0) {
				if ((oldh - h) > 0)
					handler.sendEmptyMessage(1);
				else
					handler.sendEmptyMessage(2);
			}
		}
	}

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1:
				flagIV.setVisibility(View.GONE);
				break;
			case 2:
				flagIV.setVisibility(View.VISIBLE);
				break;
			}
		};
	};

	private String round(String et) {
		int etSize = et.indexOf(".");
		if (et.length() == 5) {
			int data = Integer.valueOf(et.substring(etSize + 2, etSize + 3));
			if (data >= 5) {
				et = et.replace(
						et.substring(etSize + 1, etSize + 3),
						(Integer.valueOf(et.substring(etSize + 1, etSize + 2)) + 1)
								+ "");
			} else {
				et = et.replace(et.substring(etSize + 1, etSize + 3),
						(Integer.valueOf(et.substring(etSize + 1, etSize + 2)))
								+ "");
			}
		}
		return et;
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
		rightButton
				.setImageResource(drawable.testresultsview_ic_voicebroadcast);
		rightButton.setOnClickListener(this);
		if (mActivity.measureOrInput.equals(Constants.MEASURE)) {
			rightButton.setVisibility(View.VISIBLE);
		} else if (mActivity.measureOrInput.equals(Constants.INPUT)) {
			rightButton.setVisibility(View.GONE);
		}
		actionBar.setCustomView(view, params);
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		actionBar.setDisplayShowCustomEnabled(true);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case id.measure_bottom_completeLL:
			readMe = readmeET.getText().toString().trim();
			if (!CurrentAccountManager.getCurAccount().getAccountID()
					.equals(mActivity.getGroupmember().getAccountID())) {
				recordUpload();
			} else {
				updateMeasureData();
			}
			break;
		case id.measure_bottom_againLL:
			mActivity.comeBackEarTemperatureMeasureOrInput(this);
			break;
		case id.actionbar_left:
			mActivity.comeBackEarTemperatureMeasureOrInput(this);
			break;
		case id.actionbar_right:
			SpeakerphoneOn();
			setVoiceMaterial(initVoiceMaterial());
			playVoice();
			break;
		default:
			break;
		}
	}

	private void updateMeasureData() {
		EarTemperature item = new EarTemperature();
		item.setMeasureTime(measureTime);
		item.setReadme(readMe);
		item.setIsDivider(false);
		item.setMeasureUID(measureId);
		item.setTemperature(Float.valueOf(temperature));
		item.setAccountID(mActivity.getGroupmember().getAccountID());
		item.setStateFlag(EarTemperature.FLAG_NOT_SYNCHRONIZED);
		item.setAbnormal(state);
		item.invalidate();
		mModule.getCacheController().getCache().flush(item);
		mModule.getCacheController().autoUpdates(null);
		jump2ResultDetail(measureId);
	}

	private void jump2ResultDetail(final String measureId) {
		Intent intent = new Intent(mActivity, MeasureDataActivity.class);
		intent.putExtra("type", "et");
		intent.putExtra("measureUid", measureId);
		mActivity.startActivity(intent);
		mActivity.finish();
	}

	// open speaker phone on
	private void SpeakerphoneOn() {
		if (audioManager == null)
			audioManager = (AudioManager) mActivity
					.getSystemService(Context.AUDIO_SERVICE);
		audioManager.setMode(AudioManager.STREAM_MUSIC);
		audioManager.setMicrophoneMute(false);
		audioManager.setSpeakerphoneOn(true);
	}

	// TODO finish后关闭扩音
	// close speaker phone off
	private void SpeakerphoneOff() {
		if (audioManager == null)
			audioManager = (AudioManager) mActivity
					.getSystemService(Context.AUDIO_SERVICE);
		audioManager.setMode(AudioManager.STREAM_MUSIC);
		audioManager.setMicrophoneMute(true);
		audioManager.setSpeakerphoneOn(false);
	}

	private void audoPlayVoice() {
		if (CurrentAccountManager.getCurAccount().getFlag() != null) {
			boolean isPlay = FlagHelper.getSetValueInFlag(CurrentAccountManager
					.getCurAccount().getFlag(), FlagHelper.FLAG_POSITION_VOICE);

			if (isPlay) {
				playVoice();
			}
		}
	}

	public void onStart() {
		initActionBar();
		super.onStart();
	}

	public void setTemperature(String temperature) {
		this.temperature = temperature;
	}

	private void doRuleMatch() {
		EarTemperature et = new EarTemperature();
		et.setType(EarTemperature.EARTEMPERATURE_ID);
		et.setTemperature(Float.valueOf(temperature));

		Rule rule = RuleController.getInstance().getRulebyData(et);

		state = rule.getState();
		if (state == EarTemperature.TEMPERATURE_STATE_LOW) {
			flagIV.setImageResource(drawable.testresultsview_testresult_graph_dire);
		} else if (state == EarTemperature.TEMPERATURE_STATE_NORMAL) {
			flagIV.setImageResource(drawable.testresultsview_testresult_graph_normal);
		} else if (state == EarTemperature.TEMPERATURE_STATE_FEVER) {
			flagIV.setImageResource(drawable.testresultsview_testresult_graph_fare);
		} else if (state == EarTemperature.TEMPERATURE_STATE_HIGH_FEVER) {
			flagIV.setImageResource(drawable.testresultsview_testresult_graph_gaore);
		}
		SpeakerphoneOn();
		setVoiceMaterial(initVoiceMaterial());
		audoPlayVoice();
	}

	private void recordUpload() {
		JSONArray jsons = new JSONArray();
		JSONObject json = new JSONObject();
		try {
			json.put("measureuid", measureId);
			json.put("readme", readMe);
			json.put("source", null);
			json.put("x", 0);
			json.put("y", 0);
			json.put("state", state);
			json.put("value1", temperature);
			jsons.put(json);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		RecordUploadTask task = new RecordUploadTask(getActivity(),
				CurrentAccountManager.getCurAccount().getAccessToken(), "et",
				jsons.toString(), mActivity.getGroupmember().getAccountID(),
				-1, -1);
		task.setTaskHost(new TaskHost() {
			@Override
			public void onPostExecute(int requestCode, BaseResult result) {
				super.onPostExecute(requestCode, result);
				if (result.isSuccess()) {
					if (result.isServerDisposeSuccess()) {
						ErrorDialogUtil
								.showErrorToast(
										mActivity,
										ProxyErrorCode.TYPE_MEASURE,
										ProxyErrorCode.LocalError.CODE_SOMEONT_ELSE_IS_FINISED);

					} else {
						showPopupWindow(result.getErrorMessage(),
								getString(string.save_data_to_mcloud_error));
					}
				} else {
					showPopupWindow(getString(string.network_connection_error),
							getString(string.save_data_to_mcloud_error));
				}

			}
		});
		task.execute();
		mActivity.finish();
	}

	@Override
	protected List<Media> initVoiceMaterial() {

		List<Media> list = new ArrayList<Media>();
		List<Integer> voiceFileList = VoiceMaterialUtil
				.prepareEarTemperatureVoiceFiles(temperature);
		for (Integer resId : voiceFileList) {
			Media media = new Media();
			media.setRawId(resId);
			list.add(media);
		}
		return list;
	}

	// 初始化弹出信息
	private void initPopupWindow(String title, String content) {
		if (dialog == null) {
			ErrorDialogListener listener = new ErrorDialogListener() {
				@Override
				public void restart() {
					sendStopMeasure();
					mActivity.stopBluetoothService();
					dialog.dismiss();
					mActivity.finish();
				}

				@Override
				public void exit() {
					dialog.dismiss();
				}
			};
			dialog = new ErrorDialog(mActivity, ErrorDialog.TYPE, listener,
					title, content, getString(string.action_confirm),
					getString(string.action_cancel)).dialogFactory();
		}
	}

	private void showPopupWindow(String title, String content) {
		if (mActivity.isFinishing())
			return;
		if (dialog == null) {
			initPopupWindow(title, content);
		}
		dialog.show();
	}
}

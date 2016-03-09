package com.medzone.cloud.ui.fragment.bloodpressure;

import java.text.DecimalFormat;
import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
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
import com.medzone.cloud.data.helper.MeasureDataUtil;
import com.medzone.cloud.module.modules.BloodPressureModule;
import com.medzone.cloud.ui.MeasureActivity;
import com.medzone.cloud.ui.MeasureDataActivity;
import com.medzone.cloud.ui.dialog.error.ErrorDialogUtil;
import com.medzone.cloud.ui.fragment.BaseResultFragment;
import com.medzone.cloud.ui.widget.CleanableEditText;
import com.medzone.cloud.ui.widget.CustomLinearLayout;
import com.medzone.cloud.ui.widget.CustomLinearLayout.OnResizeListener;
import com.medzone.cloud.util.TextWatcherUtil;
import com.medzone.cloud.util.VoiceMaterialUtil;
import com.medzone.common.media.bean.Media;
import com.medzone.framework.Log;
import com.medzone.framework.data.bean.imp.BaseMeasureData;
import com.medzone.framework.data.bean.imp.BloodPressure;
import com.medzone.framework.data.bean.imp.BloodPressure.BloodPressureUtil;
import com.medzone.framework.data.bean.imp.Rule;
import com.medzone.framework.errorcode.ProxyCode.LocalError;
import com.medzone.framework.errorcode.ProxyErrorCode;
import com.medzone.framework.task.BaseResult;
import com.medzone.framework.task.TaskHost;
import com.medzone.framework.util.TimeUtils;
import com.medzone.mcloud.R;
import com.medzone.mcloud.R.drawable;
import com.medzone.mcloud.R.id;
import com.medzone.mcloud.R.layout;
import com.medzone.mcloud.R.string;

public class BloodPressureResultFragment extends BaseResultFragment implements
		View.OnClickListener {

	private LinearLayout completeLL, againLL;
	private TextView rateTV, highTV, lowTV, againTV, hplTV, highUnitTV,
			lowUnitTV, hplUnitTV;
	private String readMe;
	private float argumentHigh;
	private float argumentLow;
	private int argumentRate;

	private BloodPressureModule mModule;
	private CustomLinearLayout customLinearLayout;
	private MeasureActivity mActivity;
	private CleanableEditText readmeET;
	private ImageView flagIV;
	private boolean isKpa = false;
	private long measureTime;
	private String measureId;
	private int state;
	private BloodPressure bloodpressure;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mActivity = (MeasureActivity) activity;
		mModule = (BloodPressureModule) mActivity.getAttachModule();

		isKpa = mModule.isKpaMode();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(layout.fragment_pressure_result,
				container, false);
		initActionBar();
		completeLL = (LinearLayout) view
				.findViewById(id.measure_bottom_completeLL);
		againLL = (LinearLayout) view.findViewById(id.measure_bottom_againLL);
		againTV = (TextView) view.findViewById(id.measure_bottom_againTV);
		flagIV = (ImageView) view.findViewById(id.pressure_result_flag_iv);
		rateTV = (TextView) view.findViewById(id.pressure_result_rateTV);
		highTV = (TextView) view.findViewById(id.pressure_result_highTV);
		lowTV = (TextView) view.findViewById(id.pressure_result_lowTV);
		hplTV = (TextView) view.findViewById(id.pressure_result_hplTV);
		highUnitTV = (TextView) view
				.findViewById(id.pressure_result_high_unitTV);
		lowUnitTV = (TextView) view.findViewById(id.pressure_result_low_unitTV);
		hplUnitTV = (TextView) view.findViewById(id.pressure_result_hpl_unitTV);
		readmeET = (CleanableEditText) view
				.findViewById(id.ce_pressure_result_readme);
		customLinearLayout = (CustomLinearLayout) view
				.findViewById(id.pressure_container);
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		argumentHigh = Float.valueOf(getArguments().getString(
				Constants.HIGH_PRESSURE));
		argumentLow = Float.valueOf(getArguments().getString(
				Constants.LOW_PRESSURE));
		argumentRate = Integer
				.valueOf(getArguments().getString(Constants.RATE));

		int errorCode = MeasureDataUtil.checkBloodPressureMeasureResult(
				argumentHigh, argumentLow, argumentRate);
		if (errorCode != LocalError.CODE_SUCCESS) {
			return;
		}

		fillView();
		regisiterEvent();
		measureTime = BaseMeasureData.Util.createMeasureTime();
		measureId = BaseMeasureData.Util.createMeasureID();
		doRuleMatch();
	}

	private void fillView() {
		// 底部确认区域变更
		if (mActivity.measureOrInput.equals(Constants.MEASURE)) {
			againTV.setText(getString(string.remeasure));
		} else if (mActivity.measureOrInput.equals(Constants.INPUT)) {
			againTV.setText(getString(string.reinput));
		}
		// 填充View视图
		if (isKpa) {
			float tempHigh = (float) BloodPressureUtil
					.convertMMHG2KPA(argumentHigh);
			float tempLow = (float) BloodPressureUtil
					.convertMMHG2KPA(argumentLow);
			highTV.setText(String.valueOf(new DecimalFormat("0.0")
					.format(tempHigh)));
			lowTV.setText(String.valueOf(new DecimalFormat("0.0")
					.format(tempLow)));
			float hplKpa = BloodPressureUtil.convertMMHG2KPA(Float
					.parseFloat(new DecimalFormat("0.0").format(argumentHigh
							- argumentLow)));
			hplTV.setText(String.valueOf(hplKpa));
			highUnitTV.setText(getString(string.pressure_unit_kpa));
			lowUnitTV.setText(getString(string.pressure_unit_kpa));
			hplUnitTV.setText(getString(string.pressure_unit_kpa));

		} else {
			highTV.setText(String.valueOf(new DecimalFormat("0")
					.format(argumentHigh)));
			lowTV.setText(String.valueOf(new DecimalFormat("0")
					.format(argumentLow)));
			hplTV.setText(String.valueOf(new DecimalFormat("0")
					.format(argumentHigh - argumentLow)));
		}
		rateTV.setText(String.valueOf(argumentRate));
	}

	private void regisiterEvent() {
		completeLL.setOnClickListener(this);
		againLL.setOnClickListener(this);
		readmeET.addTextChangedListener(new TextWatcherUtil(mActivity, readmeET));
		customLinearLayout.addOnCustomChangeListener(new MyOnResizeListener());
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
			completeMeasure();
			break;
		case id.measure_bottom_againLL:
			mActivity.comeBackBloodPressureMeasureOrInput(this);
			break;
		case id.actionbar_left:
			mActivity.finish();
			break;
		case id.actionbar_right:
			setVoiceMaterial(initVoiceMaterial());
			playVoice();
			break;
		default:
			break;
		}
	}

	private void completeMeasure() {
		readMe = readmeET.getText().toString().trim();
		fillBloodPressure();
		if (!mActivity.getGroupmember().getAccountID()
				.equals(CurrentAccountManager.getCurAccount().getAccountID())) {
			recordUpload();
		} else {
			updateMeasureData();
		}

		sendStopMeasure();
		mActivity.stopBluetoothService();
	}

	private void fillBloodPressure() {
		bloodpressure = new BloodPressure();
		bloodpressure.setMeasureTime(measureTime);
		bloodpressure.setMeasureTimeHelp(TimeUtils
				.getYYYYMMDDHHMMSS(measureTime));
		bloodpressure.setReadme(readMe);
		bloodpressure.setIsDivider(false);
		bloodpressure.setMeasureUID(measureId);
		bloodpressure.setHigh(argumentHigh);
		bloodpressure.setLow(argumentLow);
		bloodpressure.setRate(argumentRate);
		bloodpressure.setStateFlag(BloodPressure.FLAG_NOT_SYNCHRONIZED);
		bloodpressure.setAbnormal(state);
		bloodpressure.setAccountID(mActivity.getGroupmember().getAccountID());
		bloodpressure.invalidate();
	}

	private void updateMeasureData() {
		mModule.getCacheController().getCache().flush(bloodpressure);
		mModule.getCacheController().autoUpdates(new TaskHost() {
			@Override
			public void onPostExecute(int requestCode, BaseResult result) {
				super.onPostExecute(requestCode, result);
				if (result.isSuccess()) {
					if (result.isServerDisposeSuccess()) {
						Log.e(">>>>>>数据上传成功");
					} else {
						Log.e(">>>>>>数据上传失败" + result.getErrorMessage()
								+ result.getErrorCode());
					}
				} else {
					Log.e(">>>>>>网络错误" + result.getErrorMessage()
							+ result.getErrorCode());
				}
			}
		});
		jump2ResultDetail(measureId);

	}

	private void jump2ResultDetail(final String measureId) {
		Intent intent = new Intent(mActivity, MeasureDataActivity.class);
		intent.putExtra("type", "bp");
		intent.putExtra("measureUid", measureId);
		mActivity.startActivity(intent);
		mActivity.finish();
	}

	private void autoPlayVoice() {
		if (CurrentAccountManager.getCurAccount().getFlag() != null) {
			boolean isPlay = FlagHelper.getSetValueInFlag(CurrentAccountManager
					.getCurAccount().getFlag(), FlagHelper.FLAG_POSITION_VOICE);
			if (isPlay) {
				playVoice();
			}
		}
	}

	private void doRuleMatch() {
		BloodPressure pressure = new BloodPressure();
		pressure.setType(BloodPressure.BLOODPRESSURE_ID);
		pressure.setHigh(argumentHigh);
		pressure.setLow(argumentLow);

		Rule rule = RuleController.getInstance().getRulebyData(pressure);
		state = rule.getState();
		MeasureDataUtil.BloodPressureFlag(flagIV, state);

		if (mActivity.measureOrInput.equals(Constants.MEASURE)) {
			new AutoPlayVoiceThread().start();
		}
	}

	class AutoPlayVoiceThread extends Thread {
		@Override
		public void run() {
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			setVoiceMaterial(initVoiceMaterial());
			autoPlayVoice();
		}

	}

	private void recordUpload() {
		mModule.synchroRecordUpload(mActivity.getGroupmember(), bloodpressure,
				RecordUploadTaskHost);
		mActivity.finish();
	}

	private TaskHost RecordUploadTaskHost = new TaskHost() {
		@Override
		public void onPostExecute(int requestCode, BaseResult result) {
			super.onPostExecute(requestCode, result);
			if (result.isSuccess()) {
				if (result.isServerDisposeSuccess()) {
					ErrorDialogUtil.showErrorToast(mActivity,
							ProxyErrorCode.TYPE_MEASURE,
							LocalError.CODE_SOMEONT_ELSE_IS_FINISED);

				}
			} else {
				ErrorDialogUtil.showErrorToast(mActivity,
						ProxyErrorCode.TYPE_MEASURE,
						LocalError.CODE_DATA_UPLOAD_FAILURE);
			}
		}
	};

	@Override
	protected List<Media> initVoiceMaterial() {
		List<Media> list = new LinkedList<Media>();
		List<Integer> voiceFileList;

		if (!isKpa) {
			voiceFileList = VoiceMaterialUtil.preparePressureVoiceFiles(
					String.valueOf((int) argumentHigh),
					String.valueOf((int) argumentLow), false);
		} else {
			float tempHigh = BloodPressureUtil.convertMMHG2KPA(argumentHigh);
			float tempLow = BloodPressureUtil.convertMMHG2KPA(argumentLow);
			voiceFileList = VoiceMaterialUtil.preparePressureVoiceFiles(
					String.valueOf(tempHigh), String.valueOf(tempLow), true);
		}
		for (Integer resId : voiceFileList) {
			Media media = new Media();
			media.setRawId(resId);
			list.add(media);
		}
		return list;
	}
}

package com.medzone.cloud.ui.fragment.bloodoxygen;

import java.util.ArrayList;
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
import com.medzone.cloud.module.modules.BloodOxygenModule;
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
import com.medzone.framework.data.bean.imp.BaseMeasureData;
import com.medzone.framework.data.bean.imp.BloodOxygen;
import com.medzone.framework.data.bean.imp.Rule;
import com.medzone.framework.errorcode.ProxyErrorCode;
import com.medzone.framework.task.BaseResult;
import com.medzone.framework.task.TaskHost;
import com.medzone.framework.util.TimeUtils;
import com.medzone.mcloud.R;
import com.medzone.mcloud.R.drawable;
import com.medzone.mcloud.R.id;
import com.medzone.mcloud.R.layout;
import com.medzone.mcloud.R.string;

public class BloodOxygenResultFragment extends BaseResultFragment implements
		View.OnClickListener {

	private LinearLayout completeLL, againLL;
	private CustomLinearLayout customLinearLayout;
	private TextView rateTV, oxygenTV, againTV;
	private String rate, oxygen, typeFragment, readMe;
	private BloodOxygenModule mModule;
	private MeasureActivity mActivity;
	private CleanableEditText readmeET;
	private ImageView flagIV;

	private int state;
	private long measureTime;
	private String measureId;
	private BloodOxygen bloodoxygen;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mActivity = (MeasureActivity) activity;
		mModule = (BloodOxygenModule) mActivity.getAttachModule();
		typeFragment = mActivity.measureOrInput;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(layout.fragment_oxygen_result, container,
				false);
		initActionBar();
		completeLL = (LinearLayout) view
				.findViewById(id.measure_bottom_completeLL);
		againLL = (LinearLayout) view.findViewById(id.measure_bottom_againLL);
		againTV = (TextView) view.findViewById(id.measure_bottom_againTV);
		flagIV = (ImageView) view.findViewById(id.oxygen_result_flag_iv);
		rateTV = (TextView) view.findViewById(id.oxygen_result_rateTV);
		oxygenTV = (TextView) view.findViewById(id.oxygen_result_oxygenTV);
		readmeET = (CleanableEditText) view
				.findViewById(id.ce_oxygen_result_readme);
		customLinearLayout = (CustomLinearLayout) view
				.findViewById(id.oxygen_container);
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		oxygen = getArguments().getString(Constants.OXYGEN);
		rate = getArguments().getString(Constants.RATE);
		if (typeFragment.equals(Constants.MEASURE)) {
			againTV.setText(getString(string.remeasure));
		} else if (typeFragment.equals(Constants.INPUT)) {
			againTV.setText(getString(string.reinput));
		}
		rateTV.setText(rate);
		oxygenTV.setText(oxygen);
		completeLL.setOnClickListener(this);
		againLL.setOnClickListener(this);
		// XXX
		readmeET.addTextChangedListener(new TextWatcherUtil(mActivity, readmeET));
		measureTime = BaseMeasureData.Util.createMeasureTime();
		measureId = BaseMeasureData.Util.createMeasureID();
		customLinearLayout.addOnCustomChangeListener(new MyOnResizeListener());
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
		if (typeFragment.equals(Constants.MEASURE)) {
			rightButton.setVisibility(View.VISIBLE);
		} else if (typeFragment.equals(Constants.INPUT)) {
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
			mActivity.comeBackBloodOxygenMeasureOrInput(this);
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
		sendStopMeasure();
		mActivity.stopBluetoothService();
		fillBloodOxygen();
		if (!mActivity.getGroupmember().getAccountID()
				.equals(CurrentAccountManager.getCurAccount().getAccountID())) {
			recordUpload();
		} else {
			updateMeasureData();
		}
	}

	private void fillBloodOxygen() {
		bloodoxygen = new BloodOxygen();
		bloodoxygen.setMeasureTime(measureTime);
		bloodoxygen.setReadme(readMe);
		bloodoxygen.setIsDivider(false);
		bloodoxygen.setMeasureUID(measureId);
		bloodoxygen
				.setMeasureTimeHelp(TimeUtils.getYYYYMMDDHHMMSS(measureTime));
		bloodoxygen.setOxygen(Integer.valueOf(oxygen));
		bloodoxygen.setRate(Integer.valueOf(rate));
		bloodoxygen.setStateFlag(BloodOxygen.FLAG_NOT_SYNCHRONIZED);
		bloodoxygen.setAbnormal(state);
		bloodoxygen.setAccountID(mActivity.getGroupmember().getAccountID());
		bloodoxygen.invalidate();
	}

	private void updateMeasureData() {
		mModule.getCacheController().getCache().flush(bloodoxygen);
		mModule.getCacheController().autoUpdates(null);
		jump2ResultDetail(measureId);
	}

	private void jump2ResultDetail(String measureUid) {
		// 页面切换变更
		Intent intent = new Intent(mActivity, MeasureDataActivity.class);
		intent.putExtra("type", "bo");
		intent.putExtra("measureUid", measureUid);
		mActivity.startActivity(intent);
		mActivity.finish();
	}

	// TODO 提取到基类中去完成语音播报主体功能
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
		BloodOxygen bloodOxygen = new BloodOxygen();
		bloodOxygen.setType(BloodOxygen.BLOODOXYGEN_ID);
		bloodOxygen.setOxygen(Integer.valueOf(oxygen));

		Rule rule = RuleController.getInstance().getRulebyData(bloodOxygen);
		state = rule.getState();
		MeasureDataUtil.BloodOxygenFlag(flagIV, state);

		if (readmeET.hasFocus()) {
			flagIV.setVisibility(View.GONE);
		} else {
			flagIV.setVisibility(View.VISIBLE);
		}

		if (typeFragment.equals(Constants.MEASURE)) {
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
		mActivity.finish();
		mModule.synchroRecordUpload(mActivity.getGroupmember(), bloodoxygen,
				RecordUploadTaskHost);
	}

	private TaskHost RecordUploadTaskHost = new TaskHost() {
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
				}
			} else {
				ErrorDialogUtil.showErrorToast(mActivity,
						ProxyErrorCode.TYPE_MEASURE,
						ProxyErrorCode.LocalError.CODE_DATA_UPLOAD_FAILURE);
			}
		}
	};

	@Override
	protected List<Media> initVoiceMaterial() {

		List<Media> list = new ArrayList<Media>();
		List<Integer> voiceFileList = VoiceMaterialUtil
				.prepareOxygenVoiceFiles(oxygen, rate);
		for (Integer resId : voiceFileList) {
			Media media = new Media();
			media.setRawId(resId);
			list.add(media);
		}
		return list;
	}

}

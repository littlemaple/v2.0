package com.medzone.cloud.ui.fragment.temperature;

import java.util.Timer;
import java.util.TimerTask;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.LayoutParams;
import com.medzone.cloud.CloudApplication;
import com.medzone.cloud.Constants;
import com.medzone.cloud.acl.service.MeasureConnectListening;
import com.medzone.cloud.ui.MeasureActivity;
import com.medzone.cloud.ui.widget.ErrorDialog;
import com.medzone.cloud.ui.widget.ErrorDialog.ErrorDialogListener;
import com.medzone.cloud.util.TranslateUtil;
import com.medzone.cloud.util.WakeLockUtil;
import com.medzone.framework.data.bean.imp.GroupMember;
import com.medzone.framework.data.bean.imp.Transmit;
import com.medzone.framework.fragment.BaseFragment;
import com.medzone.framework.util.AudioUtils;
import com.medzone.mcloud.R;
import com.medzone.mcloud.R.drawable;
import com.medzone.mcloud.R.id;
import com.medzone.mcloud.R.string;

public class AudioConnectFragment extends BaseFragment implements
		OnClickListener {
	private int connectFlag = 0;
	private int slidingFlag = 3;
	private int openDeviceFlag = 5;

	private final static int CONNECT_FLAG_START = 0;
	private final static int CONNECT_FLAG_IN = 1;
	private final static int CONNECT_FLAG_END = 2;
	private final static int SLIDING_FLAG_INIT = 3;
	private final static int OPEN_DEVICE_FLAG_INIT = 5;

	private TextView flagTV, titleTV;
	private Button startMeasureBtn;
	private View view;
	private MeasureActivity attachActivity;
	private LinearLayout connectionLL, successLL;
	private ImageView handerIV, flagIV;
	private FrameLayout openDeviceFL;
	private boolean isError = false;
	private Dialog dialog;

	private Timer audioConnectionTimer, handSlidingTimer, openDeviceTimer;
	private TimerTask audioConnectionTask, handSlidingTask, openDeviceTask;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		attachActivity = (MeasureActivity) activity;
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void updateViewByAudioState() {
		switch (attachActivity.audio_state) {
		case MeasureActivity.AUDIO_STATE_INSERT_OUT:
			flagTV.setText(attachActivity.transmit.getMsg());
			break;
		case MeasureActivity.AUDIO_STATE_INSERT_IN:
			flagTV.setText(string.measure_connect_open_device);
			break;
		case MeasureActivity.AUDIO_STATE_CONNECT_ERROR:
			successLL.setVisibility(View.GONE);
			connectionLL.setVisibility(View.VISIBLE);
			startMeasureBtn
					.setBackgroundResource(drawable.guideview_btn_start_disabled);
			if (!isError) {
				showPopupWindow(getString(string.audio_connection_error),
						attachActivity.transmit.getMsg());
			}
			break;
		case MeasureActivity.AUDIO_STATE_CONNECTING:
			flagTV.setText(string.measure_connect_open_device_in);
			if (CloudApplication.isNewAPI(Build.VERSION_CODES.HONEYCOMB_MR2)) {
				openDeviceFL.setAlpha(1f);
			}
			stopTimer();
			startaudioConnectionTimer();
			break;
		case MeasureActivity.AUDIO_STATE_CONNECT_SUCCESS:
			stopTimer();
			successLL.setVisibility(View.VISIBLE);
			connectionLL.setVisibility(View.GONE);
			startMeasureBtn
					.setBackgroundResource(drawable.btn_connect_start_highlight);
			break;
		default:
			break;
		}
	}

	public void onStart() {
		WakeLockUtil.acquireWakeLock();
		initActionBar();
		super.onStart();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_temperature_connect,
				container, false);
		handerIV = (ImageView) view.findViewById(id.device_oximeter_hand);
		flagIV = (ImageView) view.findViewById(id.audio_connect_flag_iv);
		flagTV = (TextView) view.findViewById(R.id.audio_connect_textTV);
		successLL = (LinearLayout) view
				.findViewById(id.audio_connect_success_ll);
		connectionLL = (LinearLayout) view
				.findViewById(id.audio_connection_in_ll);
		startMeasureBtn = (Button) view
				.findViewById(R.id.audio_connect_startBtn);
		openDeviceFL = (FrameLayout) view.findViewById(id.audio_open_device_fl);
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		startMeasureBtn.setOnClickListener(this);

		if (dialog != null)
			dialog.dismiss();
		attachActivity.changeBluetoothState(new MeasureConnectListening() {
			@Override
			public void updateMeasureConectState(int state) {
				updateViewByAudioState();
			}
		});
		comeBackDealWith();
	}

	private void comeBackDealWith() {
		if (attachActivity.audio_state == MeasureActivity.AUDIO_STATE_UNCONNECTED) {
			startHandSlidingTimer();
			startOpenDeviceTimer();
		} else if (attachActivity.audio_state == MeasureActivity.AUDIO_STATE_CONNECT_ERROR) {
			sendAudioDetection();
			startHandSlidingTimer();
			startOpenDeviceTimer();
			isError = false;
		} else {
			updateViewByAudioState();
		}
	}

	private void alpha(View view) {
		openDeviceFL.setVisibility(View.VISIBLE);
		Animation animationAlpha = AnimationUtils.loadAnimation(attachActivity,
				R.anim.open_device_alpha);
		view.startAnimation(animationAlpha);
	}

	private void startHandSlidingTimer() {
		if (handSlidingTimer == null)
			handSlidingTimer = new Timer();
		if (handSlidingTask == null)
			handSlidingTask = new TimerTask() {
				@Override
				public void run() {
					Message message = new Message();
					message.what = slidingFlag;
					handler.sendMessage(message);
					if (slidingFlag == SLIDING_FLAG_INIT) {
						slidingFlag = -1;
					}
				}
			};
		handSlidingTimer.schedule(handSlidingTask, 0, 1600);
	}

	private void startOpenDeviceTimer() {
		if (openDeviceTimer == null)
			openDeviceTimer = new Timer();
		if (openDeviceTask == null)
			openDeviceTask = new TimerTask() {
				@Override
				public void run() {
					Message message = new Message();
					message.what = openDeviceFlag;
					handler.sendMessage(message);
					// if (openDeviceFlag == OPEN_DEVICE_FLAG_INIT) {
					// openDeviceFlag = -1;
					// }
				}
			};
		openDeviceTimer.schedule(openDeviceTask, 1600, 800);
	}

	private void startaudioConnectionTimer() {
		if (audioConnectionTimer == null)
			audioConnectionTimer = new Timer();
		if (audioConnectionTask == null)
			audioConnectionTask = new TimerTask() {
				@Override
				public void run() {
					Message message = new Message();
					message.what = connectFlag;
					handler.sendMessage(message);
					if (connectFlag == CONNECT_FLAG_END) {
						connectFlag = -1;
					}
					connectFlag++;
				}
			};
		audioConnectionTimer.schedule(audioConnectionTask, 0, 800);
	}

	private void stopTimer() {
		if (audioConnectionTimer != null) {
			audioConnectionTimer.cancel();
			audioConnectionTimer = null;
		}
		if (audioConnectionTask != null) {
			audioConnectionTask.cancel();
			audioConnectionTask = null;
		}
		if (handSlidingTimer != null) {
			handSlidingTimer.cancel();
			handSlidingTimer = null;
		}
		if (handSlidingTask != null) {
			handSlidingTask.cancel();
			handSlidingTask = null;
		}

		if (openDeviceTimer != null) {
			openDeviceTimer.cancel();
			openDeviceTimer = null;
		}
		if (openDeviceTask != null) {
			openDeviceTask.cancel();
			openDeviceTask = null;
		}
	}

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case CONNECT_FLAG_START:
				flagIV.setImageResource(drawable.guideview_connection_01);
				break;
			case CONNECT_FLAG_IN:
				flagIV.setImageResource(drawable.guideview_connection_02);
				break;
			case CONNECT_FLAG_END:
				flagIV.setImageResource(drawable.guideview_connection_03);
				break;
			case SLIDING_FLAG_INIT:
				new TranslateUtil(handerIV, -1.1f, -0.1f);
				break;
			case OPEN_DEVICE_FLAG_INIT:
				alpha(openDeviceFL);
				break;
			default:
				break;
			}
		}
	};

	@Override
	protected void initActionBar() {
		ActionBar actionBar = getSherlockActivity().getSupportActionBar();
		ActionBar.LayoutParams params = new ActionBar.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT,
				Gravity.CENTER);
		View view = LayoutInflater.from(getSherlockActivity()).inflate(
				R.layout.custom_actionbar_with_image, null);
		titleTV = (TextView) view.findViewById(R.id.actionbar_title);
		GroupMember groupmember = attachActivity.getGroupmember();
		if (groupmember.getRemark() != null) {
			titleTV.setText(groupmember.getRemark());
		} else {
			titleTV.setText(groupmember.getNickname());
		}
		ImageView iv = (ImageView) view.findViewById(R.id.actionbar_iv);
		iv.setImageResource(drawable.guideview_ic_cutoveruser);
		iv.setOnClickListener(this);

		ImageButton leftButton = (ImageButton) view
				.findViewById(id.actionbar_left);
		leftButton.setImageResource(drawable.personalinformationview_ic_cancel);
		leftButton.setOnClickListener(this);

		ImageButton rightButton = (ImageButton) view
				.findViewById(id.actionbar_right);
		rightButton.setImageResource(drawable.actionbar_icon_add);
		rightButton.setOnClickListener(this);
		actionBar.setCustomView(view, params);
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		actionBar.setDisplayShowCustomEnabled(true);
	}

	private void sendAudioDetection() {
		Intent intent = new Intent(AudioUtils.ACTION_AUDIO_DETECTION);
		Transmit bean = new Transmit();
		bean.setMsg("restart");
		intent.putExtra(AudioUtils.DATA, bean);
		attachActivity.sendBroadcast(intent);
	}

	@Override
	public void onDestroyView() {
		WakeLockUtil.releaseWakeLock();
		stopTimer();
		isError = true;
		if (dialog != null) {
			dialog.dismiss();
		}
		super.onDestroyView();
	}

	// 初始化弹出信息
	private void initPopupWindow(String title, String content) {
		if (dialog == null) {
			ErrorDialogListener listener = new ErrorDialogListener() {
				@Override
				public void restart() {
					isError = false;
					dialog.dismiss();
					openDeviceFL.setVisibility(View.GONE);
					connectFlag = 0;
					slidingFlag = 3;
					openDeviceFlag = 5;
					startHandSlidingTimer();
					startOpenDeviceTimer();
					sendAudioDetection();
					flagTV.setText(string.measure_connect_open_device);
				}

				@Override
				public void exit() {
					dialog.dismiss();
					attachActivity.finish();
				}
			};
			dialog = new ErrorDialog(attachActivity, ErrorDialog.TYPE,
					listener, title, content, getString(string.action_restart),
					getString(string.action_exitmeasure)).dialogFactory();
		}
	}

	private void showPopupWindow(String title, String content) {
		isError = true;
		stopTimer();
		if (attachActivity.isFinishing())
			return;

		if (dialog == null) {
			initPopupWindow(title, content);
		}
		dialog.show();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case id.audio_connect_startBtn:
			if (attachActivity.audio_state == MeasureActivity.AUDIO_STATE_CONNECT_SUCCESS) {
				attachActivity.setMeasureOrInput(Constants.MEASURE);
				attachActivity.comeBackEarTemperatureMeasure(this);
			}
			break;

		case id.actionbar_right:
			attachActivity.setMeasureOrInput(Constants.INPUT);
			attachActivity.comeBackEarTemperatureInput(this);
			break;
		case id.actionbar_left:
			attachActivity.finish();
			break;
		case id.actionbar_iv:
			attachActivity.comeBackPersonnelBeingMeasured(this);
			break;
		default:
			break;
		}
	}

}

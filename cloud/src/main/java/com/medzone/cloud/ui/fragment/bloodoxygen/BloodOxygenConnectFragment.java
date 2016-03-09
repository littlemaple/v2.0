package com.medzone.cloud.ui.fragment.bloodoxygen;

import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
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
import android.widget.CheckBox;
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
import com.medzone.cloud.data.CurrentAccountManager;
import com.medzone.cloud.ui.MeasureActivity;
import com.medzone.cloud.ui.fragment.BluetoothFragment;
import com.medzone.cloud.ui.widget.ErrorDialog;
import com.medzone.cloud.ui.widget.ErrorDialog.ErrorDialogListener;
import com.medzone.cloud.util.TranslateUtil;
import com.medzone.cloud.util.WakeLockUtil;
import com.medzone.mcloud.R;
import com.medzone.mcloud.R.drawable;
import com.medzone.mcloud.R.id;
import com.medzone.mcloud.R.string;

public class BloodOxygenConnectFragment extends BluetoothFragment implements
		OnClickListener {

	private int connectFlag = 0;
	private int slidingFlag = 3;
	private int openDeviceFlag = 5;
	private boolean isError = false;
	private View rootView;
	private TextView textTV, titleTV;
	private Button startMeasureBtn;
	private ImageView flagIV, oximeterIV;
	private LinearLayout connectionLL, successLL;
	private FrameLayout openDeviceFL;
	private CheckBox cbLongMeasure;
	private Dialog dialog;
	private MeasureActivity attachActivity;
	private Timer bluetoothConnectionTimer, handSlidingTimer, openDeviceTimer;
	private TimerTask bluetoothConnectionTask, handSlidingTask, openDeviceTask;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		attachActivity = (MeasureActivity) activity;
	}

	@Override
	public void onStart() {
		super.onStart();
		WakeLockUtil.acquireWakeLock();
		initActionBar();
	}

	private void comeBackDealWith() {
		if (attachActivity.bluetooth_state == MeasureActivity.BLUETOOTH_STATE_UNCONNECTED) {
			startHandSlidingTimer();
			startOpenDeviceTimer();
		} else if (attachActivity.bluetooth_state == MeasureActivity.BLUETOOTH_STATE_ERROR
				|| attachActivity.bluetooth_state == MeasureActivity.BLUETOOTH_STATE_DISCONNTECTION
				|| attachActivity.bluetooth_state == MeasureActivity.BLUETOOTH_STATE_NO_FOND_DEVICE) {
			sendStartDiscover(attachActivity.getAttachDevice());
			startHandSlidingTimer();
			startOpenDeviceTimer();
			isError = false;
		} else {
			updateViewByBluetoothState();
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_oxygen_connect,
				container, false);
		connectionLL = (LinearLayout) rootView
				.findViewById(id.oxygen_connect_in_ll);
		successLL = (LinearLayout) rootView
				.findViewById(id.oxygen_connect_success_ll);
		flagIV = (ImageView) rootView.findViewById(id.oxygen_connect_flag_iv);
		oximeterIV = (ImageView) rootView
				.findViewById(id.device_oximeter_hander);
		textTV = (TextView) rootView.findViewById(id.oxygen_connect_textTV);
		startMeasureBtn = (Button) rootView
				.findViewById(id.oxygen_connect_start_btn);
		cbLongMeasure = (CheckBox) rootView.findViewById(id.oxygen_connect_iv);
		openDeviceFL = (FrameLayout) rootView
				.findViewById(id.oxygen_open_device_fl);
		return rootView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		cbLongMeasure.setChecked(attachActivity.isBloodOxygenLongMeasureMent);

		if (dialog != null)
			dialog.dismiss();
		startMeasureBtn.setOnClickListener(this);
		attachActivity.changeBluetoothState(new MeasureConnectListening() {
			@Override
			public void updateMeasureConectState(int state) {
				updateViewByBluetoothState();
			}
		});
		comeBackDealWith();
	}

	@Override
	protected void initActionBar() {
		ActionBar actionBar = getSherlockActivity().getSupportActionBar();
		ActionBar.LayoutParams params = new ActionBar.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT,
				Gravity.CENTER);
		View view = LayoutInflater.from(getSherlockActivity()).inflate(
				R.layout.custom_actionbar_with_image, null);
		titleTV = (TextView) view.findViewById(R.id.actionbar_title);
		titleTV.setText(CurrentAccountManager.getCurAccount()
				.getFriendsDisplay(attachActivity.getGroupmember()));
		ImageView iv = (ImageView) view.findViewById(R.id.actionbar_iv);
		if (!attachActivity.isComeFromOtherHomePage) {
			iv.setImageResource(drawable.guideview_ic_cutoveruser);
			iv.setOnClickListener(this);
		}
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

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case id.oxygen_connect_start_btn:
			if (attachActivity.bluetooth_state == MeasureActivity.BLUETOOTH_STATE_CONNECTED) {
				attachActivity.setMeasureOrInput(Constants.MEASURE);
				attachActivity.setOxygenLongTime(cbLongMeasure.isChecked());
				attachActivity.comeBackBloodOxygenMeasure(this);
			}
			break;
		case id.actionbar_right:
			attachActivity.setMeasureOrInput(Constants.INPUT);
			attachActivity.setOxygenLongTime(cbLongMeasure.isChecked());
			attachActivity.comeBackBloodOxygenInput(this);
			break;
		case id.actionbar_left:
			attachActivity.finish();
			break;
		case id.actionbar_iv:
			attachActivity.setOxygenLongTime(cbLongMeasure.isChecked());
			attachActivity.comeBackPersonnelBeingMeasured(this);
			break;
		default:
			break;
		}
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
					dialog.dismiss();
					openDeviceFL.setVisibility(View.GONE);
					connectFlag = 0;
					slidingFlag = 3;
					openDeviceFlag = 5;
					isError = false;
					textTV.setText(string.measure_connect_open_device);
					flagIV.setImageResource(drawable.guideview_connection_01);
					sendStartDiscover(attachActivity.getAttachDevice());
					startHandSlidingTimer();
					startOpenDeviceTimer();
				}

				@Override
				public void exit() {
					dialog.dismiss();
					attachActivity.finish();
				}
			};
			dialog = new ErrorDialog(attachActivity, ErrorDialog.TYPE,
					listener, title, content, getString(string.reconnect),
					getString(string.action_exitmeasure)).dialogFactory();
		}
	}

	private void showPopupWindow(String title, String content) {
		stopTimer();
		isError = true;
		if (attachActivity==null||!attachActivity.isActive)
			return;
		if (dialog == null) {
			initPopupWindow(title, content);
		}
		dialog.show();
	}

	@SuppressLint("NewApi")
	private void receiverDeviceFound() {
		stopTimer();
		textTV.setText(string.measure_connect_open_device_in);
		startBluetoothConnectionTimer();
		openDeviceFL.setVisibility(View.VISIBLE);
		if (CloudApplication.isNewAPI(Build.VERSION_CODES.HONEYCOMB_MR2)) {
			openDeviceFL.setAlpha(1f);
		}
	}

	private void receiverDeviceConnected() {
		stopTimer();
//		startMeasureBtn
//				.setBackgroundResource(drawable.guideview_btn_start_highlight);
		startMeasureBtn.setEnabled(true);
		connectionLL.setVisibility(View.GONE);
		successLL.setVisibility(View.VISIBLE);
	}

	private void receiverDeviceConnectError() {
		connectionLL.setVisibility(View.VISIBLE);
		successLL.setVisibility(View.GONE);
//		startMeasureBtn
//				.setBackgroundResource(drawable.guideview_btn_start_disabled);
		startMeasureBtn.setEnabled(false);
		if (!isError) {
			showPopupWindow(getString(string.bluetooth_connection_failure),
					getString(string.bluetooth_connection_mistakes));
		}
	}

	private void receiverDeviceDisconnected() {
		// 蓝牙断开连接
		connectionLL.setVisibility(View.VISIBLE);
		successLL.setVisibility(View.GONE);
//		startMeasureBtn
//				.setBackgroundResource(drawable.guideview_btn_start_disabled);
		startMeasureBtn.setEnabled(false);
		if (attachActivity.bluetooth_state != MeasureActivity.BLUETOOTH_STATE_UNCONNECTED
				&& attachActivity.bluetooth_state != MeasureActivity.BLUETOOTH_STATE_CONNECTED_AND_DETECTED) {

			if (!isError) {
				showPopupWindow(getString(string.bluetooth_connection_error),
						getString(string.bluetooth_disconnect));
			}
		}
	}

	private void receiverDeviceNotFound() {
		connectionLL.setVisibility(View.VISIBLE);
		successLL.setVisibility(View.GONE);
		if (!isError) {
			showPopupWindow(getString(string.not_find_device),
					getString(string.bluetooth_no_fond_device));
		}
	}

	private void updateViewByBluetoothState() {
		switch (attachActivity.bluetooth_state) {
		case MeasureActivity.BLUETOOTH_STATE_CONNECTED_AND_DETECTED:
			receiverDeviceFound();
			break;
		case MeasureActivity.BLUETOOTH_STATE_CONNECTED:
			receiverDeviceConnected();
			break;
		case MeasureActivity.BLUETOOTH_STATE_ERROR:
			receiverDeviceConnectError();
			break;
		case MeasureActivity.BLUETOOTH_STATE_DISCONNTECTION:
			receiverDeviceDisconnected();
			break;
		case MeasureActivity.BLUETOOTH_STATE_NO_FOND_DEVICE:
			receiverDeviceNotFound();
			break;
		case MeasureActivity.BLUETOOTH_STATE_NOT_SUPPORT_BLUETOOTH:
			receiverNotSupportBluetooth();
			break;
		default:
			break;
		}
	}

	private void receiverNotSupportBluetooth() {
		ErrorDialogListener listener = new ErrorDialogListener() {
			@Override
			public void restart() {
				dialog.dismiss();
				attachActivity.finish();
			}

			@Override
			public void exit() {
			}
		};
		dialog = new ErrorDialog(attachActivity, ErrorDialog.OTHER_TYPE,
				listener, getString(string.device_not_support),
				getString(string.device_not_support_details),
				getString(string.public_submit), null).dialogFactory();
		dialog.show();
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
					if (slidingFlag == Constants.SLIDING_FLAG_INIT) {
						slidingFlag = -1;
					}
				}
			};
		handSlidingTimer.schedule(handSlidingTask, 0,
				Constants.MEASURE_TIME_ONE);
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
				}
			};
		openDeviceTimer.schedule(openDeviceTask, Constants.MEASURE_TIME_ONE,
				Constants.MEASURE_TIME_TWO);
	}

	private void startBluetoothConnectionTimer() {
		if (bluetoothConnectionTimer == null)
			bluetoothConnectionTimer = new Timer();
		if (bluetoothConnectionTask == null)
			bluetoothConnectionTask = new TimerTask() {
				@Override
				public void run() {
					Message message = new Message();
					message.what = connectFlag;
					handler.sendMessage(message);
					if (connectFlag == Constants.CONNECT_FLAG_END) {
						connectFlag = -1;
					}
					connectFlag++;
				}
			};
		bluetoothConnectionTimer.schedule(bluetoothConnectionTask, 0,
				Constants.MEASURE_TIME_TWO);
	}

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case Constants.CONNECT_FLAG_START:
				flagIV.setImageResource(drawable.guideview_connection_01);
				break;
			case Constants.CONNECT_FLAG_IN:
				flagIV.setImageResource(drawable.guideview_connection_02);
				break;
			case Constants.CONNECT_FLAG_END:
				flagIV.setImageResource(drawable.guideview_connection_03);
				break;
			case Constants.SLIDING_FLAG_INIT:
				new TranslateUtil(oximeterIV, -1.2f, 0.1f);
				break;
			case Constants.OPEN_DEVICE_FLAG_INIT:
				alpha(openDeviceFL);
				break;
			default:
				break;
			}
		}
	};

	private void stopTimer() {
		if (bluetoothConnectionTimer != null) {
			bluetoothConnectionTimer.cancel();
			bluetoothConnectionTimer = null;
		}
		if (bluetoothConnectionTask != null) {
			bluetoothConnectionTask.cancel();
			bluetoothConnectionTask = null;
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
}

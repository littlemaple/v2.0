package com.medzone.cloud.ui.fragment.bloodpressure;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.LayoutParams;
import com.medzone.cloud.CloudApplication;
import com.medzone.cloud.Constants;
import com.medzone.cloud.data.CurrentAccountManager;
import com.medzone.cloud.data.helper.MeasureDataUtil;
import com.medzone.cloud.module.modules.BloodPressureModule;
import com.medzone.cloud.ui.MeasureActivity;
import com.medzone.cloud.ui.fragment.BluetoothFragment;
import com.medzone.cloud.ui.widget.ErrorDialog;
import com.medzone.cloud.ui.widget.ErrorDialog.ErrorDialogListener;
import com.medzone.cloud.util.WakeLockUtil;
import com.medzone.framework.data.bean.imp.Device;
import com.medzone.framework.data.bean.imp.Transmit;
import com.medzone.framework.util.BluetoothUtils;
import com.medzone.mcloud.R;
import com.medzone.mcloud.R.drawable;
import com.medzone.mcloud.R.id;
import com.medzone.mcloud.R.string;

public class BloodPressureMeasureFragment extends BluetoothFragment implements
		OnClickListener {

	private Animation myAnimationScale;

	private View view;
	private boolean isSuccess = false;
	private boolean isError = false;
	private boolean disConnection = false;
	private boolean isPause = false;
	private String high, low, rate, staticBP;
	private TextView tvStatic;
	private Device attachDevice;
	private MeasureActivity mActivity;
	private Timer timerHeart;
	private TimerTask taskHeart;
	private ImageView redHeart, whileHeart, bigHeart;

	private BloodPressureModule mModule;
	private boolean isKpa = false;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mActivity = (MeasureActivity) activity;
		mActivity.setMeasureView(true);
		attachDevice = mActivity.getAttachDevice();
		mModule = (BloodPressureModule) mActivity.getAttachModule();
		isKpa = mModule.isKpaMode();
	}

	@Override
	public void onPause() {
		isPause = true;
		super.onPause();
	}

	@Override
	public void onResume() {
		isPause = false;
		super.onResume();
	}

	@Override
	public void onStart() {
		super.onStart();
		WakeLockUtil.acquireWakeLock();
		stopTimer();
		startTimer();
		if (isPause && isSuccess) {
			toResultFragment();
		} else if (!isPause) {
			registerMeasureBroadCast();
			sendStartMeasureBroadCast(attachDevice);
			scriptUIAction();
		}
	}

	private void scale(View view) {
		myAnimationScale = AnimationUtils.loadAnimation(mActivity,
				R.anim.heart_scale_action);
		view.startAnimation(myAnimationScale);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_pressure_measure, container,
				false);
		initActionBar();
		tvStatic = (TextView) view.findViewById(id.pressure_measure_changeTV);
		redHeart = (ImageView) view
				.findViewById(id.pressure_measure_red_heart_iv);
		whileHeart = (ImageView) view
				.findViewById(id.pressure_measure_while_heart_iv);
		bigHeart = (ImageView) view
				.findViewById(id.pressure_measure_big_heart_iv);
		return view;
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

		title.setText(CurrentAccountManager.getCurAccount().getFriendsDisplay(
				mActivity.getGroupmember()));
		ImageButton leftButton = (ImageButton) view
				.findViewById(id.actionbar_left);
		leftButton.setImageResource(drawable.personalinformationview_ic_cancel);
		leftButton.setOnClickListener(this);

		actionBar.setCustomView(view, params);
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		actionBar.setDisplayShowCustomEnabled(true);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case id.actionbar_left:
			mActivity.comeBackBloodPressureConnect(this);
			break;
		default:
			break;
		}
	}

	private void startTimer() {
		if (timerHeart == null)
			timerHeart = new Timer();
		if (taskHeart == null)
			taskHeart = new TimerTask() {
				@Override
				public void run() {
					Message message = new Message();
					message.what = Constants.MEASURE_STATE;
					handler.sendMessage(message);
				}
			};
		timerHeart.schedule(taskHeart, 0, Constants.MEASURE_TIME_TWO);
	}

	private void stopTimer() {
		if (timerHeart != null) {
			timerHeart.cancel();
			timerHeart = null;
		}
		if (taskHeart != null) {
			taskHeart.cancel();
			taskHeart = null;
		}
	}

	private void toResultFragment() {
		if (mActivity.dialog != null)
			mActivity.dialog.dismiss();
		BloodPressureResultFragment mResultFragment = new BloodPressureResultFragment();
		Bundle bundle = new Bundle();

		// if (isKpa) {
		// float highFloat = BloodPressureUtil.convertMMHG2KPA(high);
		// float lowFloat = BloodPressureUtil.convertMMHG2KPA(low);
		//
		// bundle.putString(Constants.HIGH_PRESSURE, String.valueOf(highFloat));
		// bundle.putString(Constants.LOW_PRESSURE, String.valueOf(lowFloat));
		// bundle.putString(Constants.RATE, rate);
		// } else {
		// bundle.putString(Constants.HIGH_PRESSURE, high);
		// bundle.putString(Constants.LOW_PRESSURE, low);
		// bundle.putString(Constants.RATE, rate);
		// }

		bundle.putString(Constants.HIGH_PRESSURE, high);
		bundle.putString(Constants.LOW_PRESSURE, low);
		bundle.putString(Constants.RATE, rate);

		mResultFragment.setArguments(bundle);
		FragmentManager fm = getFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		ft.remove(this);
		ft.add(id.measure_container, mResultFragment);
		ft.disallowAddToBackStack();
		ft.commitAllowingStateLoss();
	}

	@Override
	public void onDestroyView() {
		WakeLockUtil.releaseWakeLock();
		mActivity.unregisterReceiver(broadcastReceiver);
		stopTimer();
		super.onDestroyView();
	}

	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case Constants.MEASURE_PENDING:
				break;
			case Constants.MEASURE_COMPLETE:
				if (isSuccess && !isPause) {
					toResultFragment();
				}
				break;
			case Constants.MEASURE_TIMEOUT: {
				if (!isError) {
					showPopupWindow(
							CloudApplication.getInstance()
									.getApplicationContext()
									.getString(string.measure_time_out_title),
							CloudApplication.getInstance()
									.getApplicationContext()
									.getString(string.measure_time_out),
							CloudApplication.getInstance()
									.getApplicationContext()
									.getString(string.alert_restart));
				}
			}
				break;
			case Constants.MEASURE_STATE:
				scale(redHeart);
				scale(whileHeart);
				scale(bigHeart);
				break;
			default:
				break;
			}
		}
	};

	private void scriptUIAction() {
		Message message = handler.obtainMessage();
		message.what = Constants.MEASURE_PENDING;
		handler.sendMessage(message);

		message = handler.obtainMessage();
		message.what = Constants.MEASURE_TIMEOUT;
		handler.sendMessageDelayed(message, 120000);

	}

	private Dialog dialog;

	// 初始化弹出信息
	private void initPopupWindow(String title, String content, String leftTV) {
		if (dialog == null) {
			ErrorDialogListener listener = new ErrorDialogListener() {
				@Override
				public void restart() {
					dialog.dismiss();
					if (disConnection) {
						mActivity
								.comeBackBloodPressureConnect(BloodPressureMeasureFragment.this);
					} else {
						startTimer();
						disConnection = false;
						isError = false;
						sendStartMeasureBroadCast(attachDevice);
					}
				}

				@Override
				public void exit() {
					dialog.dismiss();
					mActivity.finish();
				}
			};
			dialog = new ErrorDialog(mActivity, ErrorDialog.TYPE, listener,
					title, content, leftTV,
					getString(string.action_exitmeasure)).dialogFactory();
		}
	}

	private void showPopupWindow(String title, String content, String leftTV) {
		if (!isDetached()) {
			isError = true;
			stopTimer();
			if (mActivity == null || !mActivity.isActive)
				return;

			if (dialog == null) {
				initPopupWindow(title, content, leftTV);
			}

			dialog.show();
		}
	}

	private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context c, Intent intent) {
			String action = intent.getAction();
			if (BluetoothUtils.ACTION_DATA_TO_GAME.equals(action)) {
				registerDataDealWith(intent);
			} else if (BluetoothUtils.ACTION_DISCONNECTION_DEVICE
					.equals(action)) {
				registerBluetoothDisconnection();
			}
		}
	};

	private void registerDataDealWith(Intent intent) {
		// 接收数据
		Transmit data = (Transmit) intent
				.getSerializableExtra(BluetoothUtils.DATA);
		int what = data.getWhat();
		if (what == 0) {
			if (!isError) {
				errorShow(data);
			}
		} else if (what == 1) {
			String[] values = data.getMsg().split(";");
			int count = values != null ? values.length : 0;
			if (count > 1) {
				isSuccess = true;
				high = values[0];
				low = values[1];
				rate = values[2];
				handler.removeMessages(Constants.MEASURE_TIMEOUT);

				Message message = handler.obtainMessage();
				message.what = Constants.MEASURE_COMPLETE;
				handler.sendMessage(message);
			}
		} else if (what == 2) {
			staticBP = data.getMsg();
			tvStatic.setText(MeasureDataUtil.StringConcatenationThree(staticBP));
		} else {
			isSuccess = false;
		}
	}

	private void errorShow(Transmit data) {
		switch (data.getArg1()) {
		case 0:
			showPopupWindow(getString(string.measure_time_out_title),
					data.getMsg(), getString(string.alert_restart));
			break;
		case 1:
			showPopupWindow(getString(string.equipment_abnormal),
					data.getMsg(), getString(string.alert_restart));
			break;
		case 2:
			receiverLowBattery(getString(string.low_battery), data.getMsg());
			break;
		case 3:
			showPopupWindow(getString(string.equipment_abnormal),
					data.getMsg(), getString(string.alert_restart));
			break;
		default:
			break;
		}
	}

	private void registerBluetoothDisconnection() {
		// 蓝牙断开连接
		disConnection = true;
		if (!isError) {
			showPopupWindow(getString(string.bluetooth_connection_error),
					getString(string.bluetooth_disconnect),
					getString(string.reconnect));
		}
	}

	private void registerMeasureBroadCast() {
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(BluetoothUtils.ACTION_DATA_TO_GAME);
		intentFilter.addAction(BluetoothUtils.ACTION_DISCONNECTION_DEVICE);
		mActivity.registerReceiver(broadcastReceiver, intentFilter);
	}

	private void receiverLowBattery(String title, String content) {
		ErrorDialogListener listener = new ErrorDialogListener() {
			@Override
			public void restart() {
				dialog.dismiss();
				mActivity.finish();
			}

			@Override
			public void exit() {
			}
		};
		dialog = new ErrorDialog(mActivity, ErrorDialog.OTHER_TYPE, listener,
				title, content, getString(string.public_submit), null)
				.dialogFactory();
		dialog.show();
	}

}

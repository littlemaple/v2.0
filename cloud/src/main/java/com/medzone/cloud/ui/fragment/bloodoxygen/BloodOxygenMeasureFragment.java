package com.medzone.cloud.ui.fragment.bloodoxygen;

import java.util.LinkedList;
import java.util.List;
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
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.LayoutParams;
import com.medzone.cloud.CloudApplication;
import com.medzone.cloud.Constants;
import com.medzone.cloud.data.CurrentAccountManager;
import com.medzone.cloud.module.modules.BloodOxygenModule;
import com.medzone.cloud.ui.MeasureActivity;
import com.medzone.cloud.ui.fragment.BluetoothFragment;
import com.medzone.cloud.ui.widget.ErrorDialog;
import com.medzone.cloud.ui.widget.ErrorDialog.ErrorDialogListener;
import com.medzone.cloud.ui.widget.OxygenHistogramViewUtil;
import com.medzone.cloud.ui.widget.OxygenWaveViewUtil;
import com.medzone.cloud.util.FileSaveDataUtil;
import com.medzone.cloud.util.WakeLockUtil;
import com.medzone.common.media.bean.Media;
import com.medzone.common.media.broad.Controller;
import com.medzone.common.media.inf.IOnServiceConnectComplete;
import com.medzone.common.media.player.PlayMode;
import com.medzone.common.media.player.PlayState;
import com.medzone.common.media.service.ServiceManager;
import com.medzone.framework.data.bean.imp.Device;
import com.medzone.framework.data.bean.imp.Transmit;
import com.medzone.framework.util.BluetoothUtils;
import com.medzone.mcloud.R;
import com.medzone.mcloud.R.drawable;
import com.medzone.mcloud.R.id;
import com.medzone.mcloud.R.raw;
import com.medzone.mcloud.R.string;

public class BloodOxygenMeasureFragment extends BluetoothFragment implements
		OnClickListener, IOnServiceConnectComplete {

	private int TEMP_TIME = 0;
	private boolean TIME_IN = false;
	private OxygenHistogramViewUtil histogramView; // 柱状图
	private OxygenWaveViewUtil waveView; // 波形
	private LinearLayout waveViewLayout; // 画波形的布局
	private LinearLayout histogramViewLayout;// 柱状图的布局
	private View view;
	private boolean isSuccess = false;
	private boolean isLong = false;
	private boolean isError = false;
	private boolean disConnection = false;
	private boolean isPause = false;
	private boolean isMeasureZero = false;
	private String oxygen, rate;
	private TextView oxygenOxy, oxygenRate, blankTV;
	private Button completeOxygenMeasure;
	private Device attachDevice;
	private BloodOxygenModule attachModule;
	private MeasureActivity mActivity;
	private FileSaveDataUtil bloodOxygenData;
	private char value;
	private String boData = "";
	private Timer timer;
	private TimerTask task;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mActivity = (MeasureActivity) activity;
		attachDevice = mActivity.getAttachDevice();
		attachModule = (BloodOxygenModule) mActivity.getAttachModule();
		isLong = mActivity.isBloodOxygenLongMeasureMent;
		value = attachModule
				.getPositionSetting(BloodOxygenModule.FLAG_POSITION_ALERT);
		mActivity.setMeasureView(true);
	}

	@Override
	public void onPause() {
		isPause = true;
		super.onPause();
	}

	@Override
	public void onStop() {
		super.onStop();
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
		if (isPause && isSuccess && !isLong) {
			toResultFragment();
		} else if (!isPause) {
			registerMeasureBroadCast();
			sendStartMeasureBroadCast(attachDevice);

		}
	}

	private void toResultFragment() {
		if (Integer.valueOf(oxygen) > 0 && Integer.valueOf(rate) > 0) {
			if (mActivity.dialog != null)
				mActivity.dialog.dismiss();
			isMeasureZero = false;
			sendPauseMeasure();
			mActivity.setMeasureOrInput(Constants.MEASURE);
			BloodOxygenResultFragment mResultFragment = new BloodOxygenResultFragment();
			Bundle bundle = new Bundle();
			bundle.putString(Constants.OXYGEN, oxygen);
			bundle.putString(Constants.RATE, rate);
			mResultFragment.setArguments(bundle);
			FragmentManager fm = getFragmentManager();
			FragmentTransaction ft = fm.beginTransaction();
			ft.remove(this);
			ft.add(id.measure_container, mResultFragment);
			ft.disallowAddToBackStack();
			ft.commitAllowingStateLoss();
		} else {
			isMeasureZero = true;
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_oxygen_measure, container,
				false);
		initActionBar();
		oxygenOxy = (TextView) view
				.findViewById(id.oxygen_measure_value_oxygenTV);
		oxygenRate = (TextView) view
				.findViewById(id.oxygen_measure_value_rateTV);
		waveViewLayout = (LinearLayout) view.findViewById(R.id.oxwave_ly);
		histogramViewLayout = (LinearLayout) view
				.findViewById(R.id.pulse_wave_histogram);

		completeOxygenMeasure = (Button) view
				.findViewById(id.oxygen_measure_complete);
		blankTV = (TextView) view.findViewById(id.oxygen_measure_blankTV);

		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		if (isLong) {
			bloodOxygenData = new FileSaveDataUtil(mActivity);
			completeOxygenMeasure.setVisibility(View.VISIBLE);
			completeOxygenMeasure.setOnClickListener(this);
			blankTV.setVisibility(View.GONE);
		} else {
			completeOxygenMeasure.setVisibility(View.GONE);
			blankTV.setVisibility(View.VISIBLE);
		}
		initWaveForm();
		initServiceManager();
	}

	private void initWaveForm() {
		waveView = new OxygenWaveViewUtil(mActivity, null,
				CloudApplication.width, CloudApplication.height);
		waveViewLayout.addView(waveView);

		histogramView = new OxygenHistogramViewUtil(mActivity, null,
				CloudApplication.width, CloudApplication.height);
		histogramViewLayout.addView(histogramView);
		// histogramViewLayout
		// .setBackgroundColor(OxygenHistogramViewUtil.REPORT_SHARE_MESSAHE);
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
		case id.oxygen_measure_complete:
			bloodOxygenData.writeData(boData);
			sendStopMeasure();
			mActivity.stopBluetoothService();
			mActivity.finish();
			// TODO 界面跳转
			break;
		case id.actionbar_left:
			mActivity.comeBackBloodOxygenConnect(this);
			break;
		default:
			break;
		}
	}

	@Override
	public void onDestroyView() {
		WakeLockUtil.releaseWakeLock();
		mActivity.unregisterReceiver(broadcastReceiver);
		stopTimer();
		if (dialog != null) {
			dialog.dismiss();
		}
		if (serviceManager != null) {
			serviceManager.stop();
			serviceManager.exit();
			serviceManager.disconnectService();
			serviceManager = null;
		}
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
				} else {
					if (!isError) {
						showPopupWindow(
								CloudApplication
										.getInstance()
										.getApplicationContext()
										.getString(
												string.measure_time_out_title),
								CloudApplication.getInstance()
										.getApplicationContext()
										.getString(string.measure_time_out),
								CloudApplication.getInstance()
										.getApplicationContext()
										.getString(string.alert_restart));
					}
				}
				break;

			default:
				break;
			}
		}
	};

	private void startTimer() {
		if (timer == null)
			timer = new Timer();
		if (task == null)
			task = new TimerTask() {
				@Override
				public void run() {
					if (TEMP_TIME == Constants.MEASURE_PENDING) {
						TEMP_TIME++;
						Message message = new Message();
						message.what = Constants.MEASURE_PENDING;
						handler.sendMessage(message);
					} else if (TEMP_TIME == Constants.MEASURE_COMPLETE) {
						TEMP_TIME = 0;
						Message message = new Message();
						message.what = Constants.MEASURE_COMPLETE;
						handler.sendMessage(message);
					}
				}
			};
		timer.schedule(task, 0, 15000);
	}

	private void stopTimer() {
		if (timer != null) {
			timer.cancel();
			timer = null;
		}
		if (task != null) {
			task.cancel();
			task = null;
		}
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
						// 蓝牙断开连接，重新开始连接
						mActivity
								.comeBackBloodOxygenConnect(BloodOxygenMeasureFragment.this);
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
		isError = true;
		stopTimer();
		if (mActivity == null || !mActivity.isActive)
			return;
		if (dialog == null) {
			initPopupWindow(title, content, leftTV);
		}
		dialog.show();
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
				if (isError) {
					if (dialog != null)
						dialog.dismiss();
					disConnection = false;
					TIME_IN = false;
					isError = false;
					if (!isLong) {
						sendStartMeasureBroadCast(attachDevice);
					}
				}
				isSuccess = true;
				oxygen = values[0];
				rate = values[1];

				if (isLong) {
					longMeasureData(data);
				} else {
					if (Integer.valueOf(oxygen) > 0
							&& Integer.valueOf(rate) > 0) {
						if (!TIME_IN) {
							TEMP_TIME = 0;
							TIME_IN = true;
							stopTimer();
							startTimer();
						}
					}
				}

				if (Integer.valueOf(oxygen) > 0) {
					oxygenOxy.setText(oxygen);
				} else {
					oxygenOxy.setText(getString(string.no_value));
				}
				if (Integer.valueOf(rate) > 0) {
					oxygenRate.setText(rate);
				} else {
					oxygenRate.setText(getString(string.no_value));
				}
				if (isMeasureZero) {
					toResultFragment();
				}
			}
		} else if (what == 2) {
			initDataChart(data);
		} else {
			isSuccess = false;
		}
	}

	private void errorShow(Transmit data) {
		switch (data.getArg1()) {
		case 1:
			receiverLowBattery(getString(string.low_battery), data.getMsg());
			break;
		case 2:
			showPopupWindow(getString(string.measure_abnormal), data.getMsg(),
					getString(string.continue_measure));
			break;
		case 3:
			receiverLowBattery(getString(string.low_battery), data.getMsg());
			break;
		default:
			break;
		}
	}

	private void initDataChart(Transmit data) {
		// 绘画折线图与柱状图的数据
		Message oxWaveDataMsg = OxygenWaveViewUtil.oxWaveHandler
				.obtainMessage();
		oxWaveDataMsg.obj = data.getMsg();
		oxWaveDataMsg.sendToTarget();

		Message oxHistogramMsg = OxygenHistogramViewUtil.oxHistogramHandler
				.obtainMessage();
		oxHistogramMsg.obj = data.getMsg();
		oxHistogramMsg.sendToTarget();
	}

	private void longMeasureData(Transmit data) {
		if (value == '1') {
			if (hasFile()) {
				if (Integer.valueOf(oxygen) > 0 && Integer.valueOf(oxygen) < 90) {
					serviceManager.start();
				} else {
					serviceManager.stop();
				}
			}
		}
		boData = boData + data.getMsg() + "|";
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

	@Override
	public void OnServiceConnectComplete() {
		List<Media> list = new LinkedList<Media>();
		Media media = new Media();
		media.setRawId(raw.warn);
		list.add(media);
		serviceManager.setList(list);
		serviceManager.setPlayMode(PlayMode.SINGLE_LOOP_PLAY);
	}

	private ServiceManager serviceManager;

	private void initServiceManager() {
		serviceManager = new ServiceManager(mActivity);
		Controller.service = serviceManager;
		serviceManager.setOnServiceConnectComplete(this);
		serviceManager.connectService();
	}

	private boolean hasFile() {
		if (serviceManager.getPlayState() == PlayState.NOFILE) {
			return false;
		}
		return true;
	}

}

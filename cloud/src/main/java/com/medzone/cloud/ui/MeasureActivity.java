package com.medzone.cloud.ui;

import java.util.List;

import android.app.Dialog;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.KeyEvent;

import com.medzone.cloud.Constants;
import com.medzone.cloud.acl.service.AudioClientService;
import com.medzone.cloud.acl.service.BluetoothClientService;
import com.medzone.cloud.acl.service.MeasureConnectListening;
import com.medzone.cloud.data.CurrentAccountManager;
import com.medzone.cloud.data.TemporaryData;
import com.medzone.cloud.module.CloudMeasureModule;
import com.medzone.cloud.module.CloudMeasureModuleCentreRoot;
import com.medzone.cloud.module.modules.BloodOxygenModule;
import com.medzone.cloud.module.modules.BloodPressureModule;
import com.medzone.cloud.module.modules.EarTemperatureModule;
import com.medzone.cloud.ui.fragment.PersonnelBeingMeasuredFragment;
import com.medzone.cloud.ui.fragment.bloodoxygen.BloodOxygenConnectFragment;
import com.medzone.cloud.ui.fragment.bloodoxygen.BloodOxygenInputFragment;
import com.medzone.cloud.ui.fragment.bloodoxygen.BloodOxygenMeasureFragment;
import com.medzone.cloud.ui.fragment.bloodoxygen.BloodOxygenResultFragment;
import com.medzone.cloud.ui.fragment.bloodpressure.BloodPressureConnectFragment;
import com.medzone.cloud.ui.fragment.bloodpressure.BloodPressureInputFragment;
import com.medzone.cloud.ui.fragment.bloodpressure.BloodPressureMeasureFragment;
import com.medzone.cloud.ui.fragment.bloodpressure.BloodPressureResultFragment;
import com.medzone.cloud.ui.fragment.temperature.AudioConnectFragment;
import com.medzone.cloud.ui.fragment.temperature.AudioMeasureFragment;
import com.medzone.cloud.ui.fragment.temperature.EarTemperatureInputFragment;
import com.medzone.cloud.ui.fragment.temperature.EarTemperatureResultFragment;
import com.medzone.cloud.ui.widget.ErrorDialog;
import com.medzone.cloud.ui.widget.ErrorDialog.ErrorDialogListener;
import com.medzone.framework.Log;
import com.medzone.framework.data.bean.imp.Device;
import com.medzone.framework.data.bean.imp.GroupMember;
import com.medzone.framework.data.bean.imp.Transmit;
import com.medzone.framework.util.AudioUtils;
import com.medzone.framework.util.BluetoothUtils;
import com.medzone.mcloud.R.id;
import com.medzone.mcloud.R.layout;
import com.medzone.mcloud.R.string;

/**
 * 
 * @author ChenJunQi.
 * 
 *         仅仅是测量模块的容器，需要外部告知，此次盛装的对象。
 * 
 */
public class MeasureActivity extends BaseActivity {

	public int bluetooth_state;
	public int audio_state;

	public final static int BLUETOOTH_STATE_ERROR = -1;
	public final static int BLUETOOTH_STATE_UNCONNECTED = 0;
	public final static int BLUETOOTH_STATE_CONNECTING = 1;
	public final static int BLUETOOTH_STATE_CONNECTED = 2;
	public final static int BLUETOOTH_STATE_CONNECTED_AND_DETECTED = 3;
	public final static int BLUETOOTH_STATE_DISCONNTECTION = 4;
	public final static int BLUETOOTH_STATE_NO_FOND_DEVICE = 5;
	public final static int BLUETOOTH_STATE_NOT_SUPPORT_BLUETOOTH = 6;

	public final static int AUDIO_STATE_INSERT_IN = 1;
	public final static int AUDIO_STATE_INSERT_OUT = 2;
	public final static int AUDIO_STATE_CONNECTING = 3;
	public final static int AUDIO_STATE_CONNECT_SUCCESS = 4;
	public final static int AUDIO_STATE_UNCONNECTED = 0;
	public final static int AUDIO_STATE_CONNECT_ERROR = -1;

	public final static String MEASURE_DEVICE_KEY = "device";

	private MeasureConnectListening listening;
	private boolean isMeasureView;

	public String measureOrInput;

	// TODO 是否长期测量应该挪到血氧模块中去判定
	public boolean isBloodOxygenLongMeasureMent;
	public String type;
	public Transmit transmit;

	// 总是用当前的Account去申请Module，在具体的fragment中使用登陆者的设置作为测量模块的设置
	private CloudMeasureModule<?> attachModule;

	private GroupMember groupmember;
	// 设备模块的信息
	private Device attachDevice = new Device();

	/**
	 * 如果从他的主业进入替他人测量，则不允许再次更改测量的对象
	 */
	public boolean isComeFromOtherHomePage = false;

	public void setMeasureView(boolean isMeasureView) {
		this.isMeasureView = isMeasureView;
	}

	public void setOxygenLongTime(boolean oxygenLongTime) {
		this.isBloodOxygenLongMeasureMent = oxygenLongTime;
	}

	public void setBluetoothState(int bluetoothSatae) {
		this.bluetooth_state = bluetoothSatae;
	}

	public GroupMember getGroupmember() {
		return groupmember;
	}

	public void setGroupmember(GroupMember groupmember) {
		this.groupmember = groupmember;
	}

	public void setMeasureOrInput(String measureOrInput) {
		this.measureOrInput = measureOrInput;
	}

	public void changeBluetoothState(MeasureConnectListening listenting) {
		this.listening = listenting;
	}

	public CloudMeasureModule<?> getAttachModule() {
		return attachModule;
	}

	public Device getAttachDevice() {
		return attachDevice;
	}

	@Override
	protected void initUI() {
		setContentView(layout.activity_measure);
	}

	@Override
	protected void postInitUI() {

		initModule();

		initConnectChannel();

	}

	@Override
	protected void preLoadData(Bundle savedInstanceState) {
		super.preLoadData(savedInstanceState);
		if (TemporaryData.containsKey(Constants.TEMPORARYDATA_KEY_TEST_ACCOUNT)) {
			GroupMember tmpAccount = (GroupMember) TemporaryData
					.get(Constants.TEMPORARYDATA_KEY_TEST_ACCOUNT);
			setGroupmember(tmpAccount);
			if (tmpAccount.equals(CurrentAccountManager.getCurAccount())) {
				isComeFromOtherHomePage = false;
			} else {
				isComeFromOtherHomePage = true;
			}
		} else {
			isComeFromOtherHomePage = false;
		}
	}

	private void initModule() {
		Intent intent = getIntent();
		String moduleID = intent.getStringExtra(MEASURE_DEVICE_KEY);

		if (TextUtils.equals(moduleID, BloodOxygenModule.ID)) {
			initBloodOxygen();
			type = Constants.mCloud_O;
		} else if (TextUtils.equals(moduleID, BloodPressureModule.ID)) {
			initBloodPressure();
			type = Constants.mCloud_P;
		} else if (TextUtils.equals(moduleID, EarTemperatureModule.ID)) {
			initEarTemperature();
			type = Constants.mCloud_ET;
		}
	}

	private void initConnectChannel() {
		if (TextUtils.equals(Constants.BLUETOOTH_DEVICE,
				attachDevice.getDeviceCategory())) {
			registerBluetoothConnectReceiver();
			startBluetoothService();
		}
		if (TextUtils.equals(Constants.AUDIO_DEVICE,
				attachDevice.getDeviceCategory())) {
			registerAudioConnectReceiver();
			startAudioService();
		}
	}

	private void initBloodOxygen() {
		attachModule = (BloodOxygenModule) CloudMeasureModuleCentreRoot
				.applyModule(CurrentAccountManager.getCurAccount(),
						BloodOxygenModule.class.getCanonicalName());
		attachDevice.setDeviceCategory(Constants.BLUETOOTH_DEVICE);
		attachDevice.setDeviceType(Constants.mCloud_O);

		FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		ft.replace(id.measure_container, new BloodOxygenConnectFragment());
		ft.disallowAddToBackStack();
		ft.commitAllowingStateLoss();

	}

	private void initBloodPressure() {
		attachModule = (BloodPressureModule) CloudMeasureModuleCentreRoot
				.applyModule(CurrentAccountManager.getCurAccount(),
						BloodPressureModule.class.getCanonicalName());
		attachDevice.setDeviceCategory(Constants.BLUETOOTH_DEVICE);
		attachDevice.setDeviceType(Constants.mCloud_P);

		FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		ft.replace(id.measure_container, new BloodPressureConnectFragment());
		ft.disallowAddToBackStack();
		ft.commitAllowingStateLoss();
	}

	private void initEarTemperature() {
		attachModule = (EarTemperatureModule) CloudMeasureModuleCentreRoot
				.applyModule(CurrentAccountManager.getCurAccount(),
						EarTemperatureModule.class.getCanonicalName());
		attachDevice.setDeviceCategory(Constants.AUDIO_DEVICE);
		attachDevice.setDeviceType(Constants.mCloud_ET);

		FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		ft.replace(id.measure_container, new AudioConnectFragment());
		ft.disallowAddToBackStack();
		ft.commitAllowingStateLoss();
	}

	public void startBluetoothService() {
		Intent intent = new Intent(this, BluetoothClientService.class);
		intent.putExtra(BluetoothUtils.DATA, attachDevice);
		Log.e("attachDevice#startBluetoothService$" + attachDevice);
		startService(intent);
	}

	public void stopBluetoothService() {
		Intent stopServiceIntent = new Intent(
				BluetoothUtils.ACTION_STOP_SERVICE);
		sendBroadcast(stopServiceIntent);
	}

	public void startAudioService() {
		Intent intent = new Intent(this, AudioClientService.class);
		startService(intent);
	}

	public void stopAudioService() {
		Intent stopServiceIntent = new Intent(
				AudioUtils.AUDIO_ACTION_STOP_SERVICE);
		sendBroadcast(stopServiceIntent);
	}

	private void sendStopMeasure() {
		Intent intent = new Intent(BluetoothUtils.ACTION_STOP_MEASURE);
		sendBroadcast(intent);
	}

	private void registerBluetoothConnectReceiver() {
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(BluetoothUtils.ACTION_NOT_FOUND_DEVICE);
		intentFilter.addAction(BluetoothUtils.ACTION_FOUND_DEVICE);
		intentFilter.addAction(BluetoothUtils.ACTION_CONNECT_IN);
		intentFilter.addAction(BluetoothUtils.ACTION_CONNECT_ERROR);
		intentFilter.addAction(BluetoothUtils.ACTION_CONNECT_SUCCESS);
		intentFilter.addAction(BluetoothUtils.ACTION_DISCONNECTION_DEVICE);
		intentFilter.addAction(BluetoothUtils.ACTION_NOT_SUPPORT_BLUETOOTH);
		registerReceiver(bluetoothBroadcastReceiver, intentFilter);
	}

	private BroadcastReceiver bluetoothBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context c, Intent intent) {
			String action = intent.getAction();
			if (BluetoothUtils.ACTION_FOUND_DEVICE.equals(action)) {
				bluetooth_state = MeasureActivity.BLUETOOTH_STATE_CONNECTED_AND_DETECTED;
				receiverDeviceConnect(intent);
			} else if (BluetoothUtils.ACTION_CONNECT_IN.equals(action)) {
				bluetooth_state = MeasureActivity.BLUETOOTH_STATE_CONNECTING;
			} else if (BluetoothUtils.ACTION_CONNECT_SUCCESS.equals(action)) {
				bluetooth_state = MeasureActivity.BLUETOOTH_STATE_CONNECTED;
			} else if (BluetoothUtils.ACTION_CONNECT_ERROR.equals(action)) {
				bluetooth_state = MeasureActivity.BLUETOOTH_STATE_ERROR;
			} else if (BluetoothUtils.ACTION_DISCONNECTION_DEVICE
					.equals(action)) {
				bluetooth_state = MeasureActivity.BLUETOOTH_STATE_DISCONNTECTION;
			} else if (BluetoothUtils.ACTION_NOT_FOUND_DEVICE.equals(action)) {
				bluetooth_state = MeasureActivity.BLUETOOTH_STATE_NO_FOND_DEVICE;
			} else if (BluetoothUtils.ACTION_NOT_SUPPORT_BLUETOOTH
					.equals(action)) {
				bluetooth_state = MeasureActivity.BLUETOOTH_STATE_NOT_SUPPORT_BLUETOOTH;
			}
			listening.updateMeasureConectState(bluetooth_state);
		}
	};

	private void receiverDeviceConnect(Intent intent) {
		// 获取到设备对象
		BluetoothDevice device = (BluetoothDevice) intent.getExtras().get(
				BluetoothUtils.DEVICE);
		if (device != null && device.getName() != null) {
			if (device.getName().equals(attachDevice.getDeviceType())) {
				// 连接设备
				Intent selectDeviceIntent = new Intent(
						BluetoothUtils.ACTION_SELECTED_DEVICE);
				selectDeviceIntent.putExtra(BluetoothUtils.DEVICE, device);
				sendBroadcast(selectDeviceIntent);
			}
		}
	}

	private void registerAudioConnectReceiver() {
		// 注册BoradcasrReceiver
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(AudioUtils.HEADEST_NOT_INSERT);
		intentFilter.addAction(AudioUtils.HEADEST_IS_INSERT);
		intentFilter.addAction(AudioUtils.ACTION_AUDIO_CONNECT_FAILURE);
		intentFilter.addAction(AudioUtils.ACTION_AUDIO_CONNECT_SUCCESS);
		registerReceiver(audioBroadcastReceiver, intentFilter);
	}

	// 广播接收器
	private BroadcastReceiver audioBroadcastReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			transmit = new Transmit();
			transmit = (Transmit) intent.getSerializableExtra(AudioUtils.DATA);
			String action = intent.getAction();
			if (AudioUtils.HEADEST_NOT_INSERT.equals(action)) {
				audio_state = AUDIO_STATE_INSERT_OUT;
			} else if (AudioUtils.HEADEST_IS_INSERT.equals(action)) {
				audio_state = AUDIO_STATE_INSERT_IN;
			} else if (AudioUtils.ACTION_AUDIO_CONNECT_FAILURE.equals(action)) {
				audio_state = AUDIO_STATE_CONNECT_ERROR;
			} else if (AudioUtils.ACTION_AUDIO_CONNECT_SUCCESS.equals(action)) {
				switch (transmit.getWhat()) {
				case 0:// 连接设备
					audio_state = AUDIO_STATE_CONNECTING;
					break;
				case 1:// 连接成功
					audio_state = AUDIO_STATE_CONNECT_SUCCESS;
					break;
				default:
					break;
				}
			}
			listening.updateMeasureConectState(bluetooth_state);
		}
	};

	@Override
	public void finish() {
		super.finish();
		if (TextUtils.equals(Constants.BLUETOOTH_DEVICE,
				attachDevice.getDeviceCategory())) {
			sendStopMeasure();
			stopBluetoothService();
			unregisterReceiver(bluetoothBroadcastReceiver);
		}
		if (TextUtils.equals(Constants.AUDIO_DEVICE,
				attachDevice.getDeviceCategory())) {
			stopAudioService();
			unregisterReceiver(audioBroadcastReceiver);
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			manageAllFragment();
		}
		return false;
	}

	private void manageAllFragment() {
		FragmentManager fm = getSupportFragmentManager();
		List<Fragment> fs = fm.getFragments();
		int count = fs != null ? fs.size() : 0;
		if (count > 0) {
			Fragment curFragment = fs.get(count - 1);
			if (curFragment instanceof BloodOxygenConnectFragment
					|| curFragment instanceof BloodPressureConnectFragment
					|| curFragment instanceof AudioConnectFragment) {
				finish();
			}

			if (curFragment instanceof BloodOxygenInputFragment
					|| curFragment instanceof PersonnelBeingMeasuredFragment
					|| curFragment instanceof BloodOxygenMeasureFragment) {
				comeBackBloodOxygenConnect(curFragment);
			} else if (curFragment instanceof BloodOxygenResultFragment) {
				finish();
			}

			if (curFragment instanceof BloodPressureInputFragment
					|| curFragment instanceof PersonnelBeingMeasuredFragment
					|| curFragment instanceof BloodPressureMeasureFragment) {
				comeBackBloodPressureConnect(curFragment);
			} else if (curFragment instanceof BloodPressureResultFragment) {
				finish();
			}

			if (curFragment instanceof EarTemperatureInputFragment
					|| curFragment instanceof PersonnelBeingMeasuredFragment
					|| curFragment instanceof AudioMeasureFragment) {
				comeBackEarTemperatureConnect(curFragment);
			} else if (curFragment instanceof EarTemperatureResultFragment) {
				finish();
			}
		}
	}

	// public
	public void comeBackPersonnelBeingMeasured(Fragment curFragment) {

		if (!isComeFromOtherHomePage) {
			FragmentManager fm = getSupportFragmentManager();
			FragmentTransaction ft = fm.beginTransaction();
			ft.remove(curFragment);
			ft.replace(id.measure_container,
					new PersonnelBeingMeasuredFragment());
			ft.disallowAddToBackStack();
			ft.commitAllowingStateLoss();
		}
	}

	// bloodoxygen
	public void comeBackBloodOxygenConnect(Fragment curFragment) {
		if (isMeasureView && bluetooth_state != BLUETOOTH_STATE_DISCONNTECTION) {
			showPopupWindow(getString(string.alert_title),
					getString(string.alert_content),
					getString(string.action_confirm),
					getString(string.action_cancel), curFragment);
		} else {
			toBloodOxygenConnectFragment(curFragment);
		}
	}

	public void toBloodOxygenConnectFragment(Fragment curFragment) {

		BloodOxygenConnectFragment fragment = new BloodOxygenConnectFragment();
		FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		ft.remove(curFragment);
		ft.replace(id.measure_container, fragment);
		ft.disallowAddToBackStack();
		ft.commitAllowingStateLoss();
	}

	public void comeBackBloodOxygenMeasureOrInput(Fragment curFragment) {
		if (measureOrInput.equals(Constants.MEASURE)) {
			comeBackBloodOxygenMeasure(curFragment);
		} else if (measureOrInput.equals(Constants.INPUT)) {
			comeBackBloodOxygenInput(curFragment);
		}
	}

	public void comeBackBloodOxygenMeasure(Fragment curFragment) {

		FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		ft.remove(curFragment);
		ft.replace(id.measure_container, new BloodOxygenMeasureFragment());
		ft.disallowAddToBackStack();
		ft.commitAllowingStateLoss();
	}

	public void comeBackBloodOxygenInput(Fragment curFragment) {

		FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		ft.remove(curFragment);
		ft.replace(id.measure_container, new BloodOxygenInputFragment());
		ft.disallowAddToBackStack();
		ft.commitAllowingStateLoss();
	}

	// bloodpressure
	public void comeBackBloodPressureConnect(Fragment curFragment) {
		if (isMeasureView && bluetooth_state != BLUETOOTH_STATE_DISCONNTECTION) {
			showPopupWindow(getString(string.alert_title),
					getString(string.alert_content),
					getString(string.action_confirm),
					getString(string.action_cancel), curFragment);
		} else {
			toBloodPressureConnectFragment(curFragment);
		}
	}

	public void toBloodPressureConnectFragment(Fragment curFragment) {

		FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		ft.remove(curFragment);
		ft.replace(id.measure_container, new BloodPressureConnectFragment());
		ft.disallowAddToBackStack();
		ft.commitAllowingStateLoss();
	}

	public void comeBackBloodPressureMeasureOrInput(Fragment curFragment) {
		if (measureOrInput.equals(Constants.MEASURE)) {
			comeBackBloodPressureMeasure(curFragment);
		} else if (measureOrInput.equals(Constants.INPUT)) {
			comeBackBloodPressureInput(curFragment);
		}
	}

	public void comeBackBloodPressureMeasure(Fragment curFragment) {
		FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		ft.remove(curFragment);
		ft.replace(id.measure_container, new BloodPressureMeasureFragment());
		ft.disallowAddToBackStack();
		ft.commitAllowingStateLoss();
	}

	public void comeBackBloodPressureInput(Fragment curFragment) {

		FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		ft.remove(curFragment);
		ft.replace(id.measure_container, new BloodPressureInputFragment());
		ft.disallowAddToBackStack();
		ft.commitAllowingStateLoss();
	}

	// ear temperature
	public void comeBackEarTemperatureConnect(Fragment curFragment) {
		if (isMeasureView) {
			showPopupWindow(getString(string.alert_title),
					getString(string.alert_content),
					getString(string.action_confirm),
					getString(string.action_cancel), curFragment);
		} else {
			toEarTemperatureConnectFragment(curFragment);
		}
	}

	public void toEarTemperatureConnectFragment(Fragment curFragment) {

		FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		ft.remove(curFragment);
		ft.replace(id.measure_container, new AudioConnectFragment());
		ft.disallowAddToBackStack();
		ft.commitAllowingStateLoss();
	}

	public void comeBackEarTemperatureMeasureOrInput(Fragment curFragment) {
		if (measureOrInput.equals(Constants.MEASURE)) {
			comeBackEarTemperatureMeasure(curFragment);
		} else if (measureOrInput.equals(Constants.INPUT)) {
			comeBackEarTemperatureInput(curFragment);
		}
	}

	public void comeBackEarTemperatureMeasure(Fragment curFragment) {

		FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		ft.remove(curFragment);
		ft.replace(id.measure_container, new AudioMeasureFragment());
		ft.disallowAddToBackStack();
		ft.commitAllowingStateLoss();
	}

	public void comeBackEarTemperatureInput(Fragment curFragment) {

		FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		ft.remove(curFragment);
		ft.replace(id.measure_container, new EarTemperatureInputFragment());
		ft.disallowAddToBackStack();
		ft.commitAllowingStateLoss();
	}

	public Dialog dialog;

	private void initPopupWindow(String title, String content, String left,
			String right, final Fragment curFragment) {
		if (dialog == null) {
			ErrorDialogListener listener = new ErrorDialogListener() {
				@Override
				public void restart() {
					dialog.dismiss();
					finish();
				}

				@Override
				public void exit() {
					dialog.dismiss();
				}
			};
			dialog = new ErrorDialog(this, ErrorDialog.TYPE, listener, title,
					content, left, right).dialogFactory();
		}
	}

	private void showPopupWindow(String title, String content, String left,
			String right, Fragment curFragment) {
		if (this.isFinishing())
			return;
		if (dialog == null) {
			initPopupWindow(title, content, left, right, curFragment);
		}
		dialog.show();
	}

	@Override
	public void finishActivity(int requestCode) {
		isComeFromOtherHomePage = false;
		super.finishActivity(requestCode);
	}

}

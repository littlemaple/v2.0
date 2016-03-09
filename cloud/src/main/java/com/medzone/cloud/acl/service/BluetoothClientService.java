package com.medzone.cloud.acl.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

import com.medzone.cloud.Constants;
import com.medzone.cloud.acl.service.test.Performance;
import com.medzone.cloud.acl.thread.bluetooth.BloodOxygenCommunThread;
import com.medzone.cloud.acl.thread.bluetooth.BloodPressureCommunThread;
import com.medzone.cloud.acl.thread.bluetooth.BluetoothBLEHelper;
import com.medzone.cloud.acl.thread.bluetooth.BluetoothConfig;
import com.medzone.cloud.acl.thread.bluetooth.BluetoothConnThread;
import com.medzone.framework.Log;
import com.medzone.framework.data.bean.imp.Device;
import com.medzone.framework.data.bean.imp.Transmit;
import com.medzone.framework.util.BluetoothUtils;

/**
 * 蓝牙模块客户端主控制Service
 * 
 * @author lwm
 * 
 */
public class BluetoothClientService extends Service {
	protected static final String TAG = BluetoothClientService.class.getName();
	private final BluetoothAdapter bluetoothAdapter = BluetoothUtils.adapter;
	private BluetoothSocket socket;
	private String deviceType;
	private Device attachDevice;
	private List<BluetoothDevice> discoveredDevices = new ArrayList<BluetoothDevice>();

	private volatile BluetoothConnThread bluetoothConn;
	private volatile BloodPressureCommunThread bloodPressureComm;
	private volatile BloodOxygenCommunThread bloodOxygenComm;
	private boolean discoveryStarted = false;
	private boolean deviceConnectIn = false;
	private boolean deviceConnected = false;
	private boolean deviceStoped = false;
	private boolean useBLE = true;
	private boolean exited = false;
	private BluetoothBLEHelper BleHelper = null;
	private static long mLastCloseTS = 0;

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (null == intent) {
			stopSelf();
			return START_NOT_STICKY;
		}
		if (bluetoothAdapter == null) {
			Intent foundIntent = new Intent(
					BluetoothUtils.ACTION_NOT_SUPPORT_BLUETOOTH);
			sendBroadcast(foundIntent);
			return START_NOT_STICKY;
		} else {
			Log.v("attachDevice#initSatrtDiscovery$onStartCommand()");
			initStartDiscovery(intent);
		}
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	/**
	 * Service创建时的回调函数
	 */
	@Override
	public void onCreate() {
		super.onCreate();
		// 搜索发现蓝牙广播监听
		IntentFilter discoveryFilter = new IntentFilter();
		discoveryFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
		discoveryFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
		discoveryFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
		discoveryFilter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
		discoveryFilter.addAction(BluetoothDevice.ACTION_FOUND);
		registerReceiver(discoveryReceiver, discoveryFilter);

		// 通讯过程广播监听
		IntentFilter controlFilter = new IntentFilter();
		controlFilter.addAction(BluetoothUtils.ACTION_START_DISCOVERY);
		controlFilter.addAction(BluetoothUtils.ACTION_SELECTED_DEVICE);
		controlFilter.addAction(BluetoothUtils.ACTION_STOP_SERVICE);
		controlFilter.addAction(BluetoothUtils.ACTION_START_COMMUN);
		controlFilter.addAction(BluetoothUtils.ACTION_PAUSE_MEASURE);
		controlFilter.addAction(BluetoothUtils.ACTION_STOP_MEASURE);
		registerReceiver(controlReceiver, controlFilter);
	}

	private void initStartDiscovery(Intent intent) {
		BluetoothConfig.initRules(this);

		Device newDevice = (Device) intent
				.getSerializableExtra(BluetoothUtils.DATA);
		if (newDevice != null && newDevice.getDeviceType() != null) {
			attachDevice = newDevice;
		}

		if (!bluetoothAdapter.isEnabled()) {
			if (BluetoothConfig.useActivity()) //For xiaoMi 2A
			{
		        Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
		        enableIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
		        startActivity(enableIntent);
			}
			else{
				bluetoothAdapter.enable();
			}
			
			Intent searchIntent = new Intent(
					BluetoothUtils.ACTION_START_DISCOVERY);
			sendBroadcast(searchIntent);
		} else {

			if (null != attachDevice) {
				deviceType = attachDevice.getDeviceType();
				useBLE = BluetoothConfig.mbUseBLE;
				if (useBLE) {
					BleHelper = new BluetoothBLEHelper(this, deviceType,
							handler);
					BleHelper.init();
				}

				if (bluetoothAdapter.isEnabled()) {
					startDiscovery();
				}
			}
		}
	}

	private void startDiscovery() {
		Performance.init();
		if (useBLE) {
			BleHelper.scanLeDevice(true);
		} else {
			new Thread() {
				public void run() {
					if (mLastCloseTS > 0) {
						long gap = System.currentTimeMillis() - mLastCloseTS;
						if (gap > 0 && gap < 6000) {
							int total = (int) (6000 - gap);
							while (total > 0 && !exited) {
								try {
									// Log.d(TAG,
									// "gzhang wait till 6000 disconnected");
									Thread.sleep(400);
									total -= 400;
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						}
					}

					if (exited)
						return;

					// Request discover from BluetoothAdapter
					// If we're already discovering, stop it
					if (bluetoothAdapter.isDiscovering()) {
						bluetoothAdapter.cancelDiscovery();
					}
					bluetoothAdapter.startDiscovery();
					Performance.tsScanStart = System.currentTimeMillis();
				}
			}.start();
		}
	}

	// 控制信息广播的接收器
	private BroadcastReceiver controlReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {

			String action = intent.getAction();
			if (BluetoothUtils.ACTION_START_DISCOVERY.equals(action)) {

				// 等待蓝牙被开启，但是如果蓝牙开启失败则会导致这里进行死循环
				if (bluetoothAdapter.isEnabled()) {
					discoveryStarted = false;
					deviceConnectIn = false;
					deviceConnected = false;
					deviceStoped = false;
					useBLE = true;
					exited = false;
					initStartDiscovery(intent);
				} else {

					// TODO 这里会构造一个循环体，应设置信号量，即时终止循环
					// 无法捕捉到用户禁止打开蓝牙的对话框

					Intent enableIntent = new Intent(
							BluetoothUtils.ACTION_START_DISCOVERY);
					sendBroadcast(enableIntent);

				}
				deviceStoped = false;
			}
			if (BluetoothUtils.ACTION_SELECTED_DEVICE.equals(action)) {
				if (deviceStoped == true)
					return;
				// 选择了连接的服务器设备
				BluetoothDevice device = (BluetoothDevice) intent.getExtras()
						.get(BluetoothUtils.DEVICE);
				deviceType = device.getName();
				// 开启设备连接线程
				synchronized (this) {
					if (useBLE) {
						if (BleHelper.getState() < BluetoothBLEHelper.STATE_CONNECTING) {
							BleHelper.connect(device.getAddress());
						}
					} else {
						if (bluetoothConn == null) {
							bluetoothConn = new BluetoothConnThread(handler,
									device);
							bluetoothConn.start();
						}
					}
				}
			} else if (BluetoothUtils.ACTION_START_COMMUN.equals(action)) {
				// 开启通讯线程
				if (Constants.mCloud_P.equals(deviceType)) {
					if (bloodPressureComm != null) {
						bloodPressureComm.measure();
					}
				} else if (Constants.mCloud_O.equals(deviceType)) {
					if (bloodOxygenComm != null) {
						bloodOxygenComm.measure();
					}
				}

			} else if (BluetoothUtils.ACTION_PAUSE_MEASURE.equals(action)) {
				if (Constants.mCloud_O.equals(deviceType)) {
					if (bloodOxygenComm != null) {
						bloodOxygenComm.pauseMeasure();
					}
				} else if (Constants.mCloud_P.equals(deviceType)) {
					if (bloodPressureComm != null) {
						bloodPressureComm.pauseMeasure();
					}
				}

			} else if (BluetoothUtils.ACTION_STOP_MEASURE.equals(action)) {

				closeAll();
				deviceStoped = true;

			} else if (BluetoothUtils.ACTION_STOP_SERVICE.equals(action)) {
				// 停止后台服务
				stopSelf();
			}
		}
	};

	// 蓝牙搜索广播的接收器
	private BroadcastReceiver discoveryReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			// 获取广播的Action
			String action = intent.getAction();
			Log.v(TAG, action);
			if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
				// 开始搜索
				discoveryStarted = true;
			} else if (BluetoothDevice.ACTION_FOUND.equals(action)) {
				// 发现远程蓝牙设备
				// 获取设备
				BluetoothDevice bluetoothDevice = intent
						.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				if (deviceType == null) {
					deviceType = attachDevice.getDeviceType();
				}
				if (deviceType.equals(bluetoothDevice.getName())) {
					discoveredDevices.add(bluetoothDevice);
				} else
					return;

				if (bluetoothConn != null)
					return;

				// 发送发现设备广播
				Intent deviceListIntent = new Intent(
						BluetoothUtils.ACTION_FOUND_DEVICE);
				deviceListIntent.putExtra(BluetoothUtils.DEVICE,
						bluetoothDevice);
				sendBroadcast(deviceListIntent);

				// Toast.makeText(BluetoothClientService.this,
				// bluetoothDevice.getAddress(), Toast.LENGTH_LONG).show();

				Performance.tsScanFound = System.currentTimeMillis();

			} else if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
				BluetoothDevice device = intent
						.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				switch (device.getBondState()) {
				case BluetoothDevice.BOND_BONDING:
					deviceConnectIn = true;
					// Log.i(TAG, "BOND_BONDING come");
					break;
				default:
					break;
				}
			} else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED
					.equals(action)) {
				// 搜索结束
				if (discoveredDevices.isEmpty() && discoveryStarted) {
					// 若未找到设备，则发动未发现设备广播
					Intent foundIntent = new Intent(
							BluetoothUtils.ACTION_NOT_FOUND_DEVICE);
					sendBroadcast(foundIntent);

					Performance.tsScanFailed = System.currentTimeMillis();
					Performance.save();
				}
			} else if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {
				BluetoothDevice bluetoothDevice = intent
						.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				boolean isSame = deviceType != null
						&& deviceType.equals(bluetoothDevice.getName());
				if (!isSame)
					return;
				if (!deviceConnectIn && !deviceConnected)
					return;
				if (useBLE)
					return;

				// Log.i(TAG, "ACTION_ACL_DISCONNECTED" + deviceConnected
				// + ", name = " + bluetoothDevice.getName());
				closeAll();
				// 蓝牙已断开连接
				Intent foundIntent = new Intent(
						BluetoothUtils.ACTION_DISCONNECTION_DEVICE);
				sendBroadcast(foundIntent);
			}
		}
	};

	// 接收其他线程消息的Handler
	Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// 处理消息
			switch (msg.what) {
			case BluetoothUtils.MESSAGE_CONNECT_IN:
				// 连接中
				Intent inIntent = new Intent(BluetoothUtils.ACTION_CONNECT_IN);
				sendBroadcast(inIntent);
				break;
			case BluetoothUtils.MESSAGE_CONNECT_ERROR:
				// 连接错误
				// 发送连接错误广播
				Log.v(TAG, "BluetoothUtils.MESSAGE_CONNECT_ERROR"
						+ deviceConnected);
				closeAll();

				Intent errorIntent = new Intent(
						BluetoothUtils.ACTION_CONNECT_ERROR);
				sendBroadcast(errorIntent);
				break;
			case BluetoothUtils.MESSAGE_CONNECT_SUCCESS:
				// 连接成功
				// 发送连接成功广播
				InputStream input = null;
				OutputStream output = null;
				if (useBLE) {
					input = BleHelper.getInputStream();
					output = BleHelper.getOutputStream();
				} else {
					socket = (BluetoothSocket) msg.obj;
					if (socket == null)
						return;
					try {
						input = socket.getInputStream();
						output = socket.getOutputStream();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}

				if (input == null || output == null)
					return;

				if (Constants.mCloud_P.equals(deviceType)) {
					if (bloodPressureComm == null) {
						bloodPressureComm = new BloodPressureCommunThread(
								handler, input, output);
					}
				} else if (Constants.mCloud_O.equals(deviceType)) {
					if (bloodOxygenComm == null) {
						bloodOxygenComm = new BloodOxygenCommunThread(handler,
								input, output);
					}
				}

				Intent succIntent = new Intent(
						BluetoothUtils.ACTION_CONNECT_SUCCESS);
				sendBroadcast(succIntent);
				deviceConnected = true;
				break;
			case 0:
				// 通讯失败
				Intent dataIntent = new Intent(
						BluetoothUtils.ACTION_DATA_TO_GAME);
				Transmit transmit = new Transmit();
				transmit.setMsg((String) msg.obj);
				transmit.setWhat(msg.what);
				transmit.setArg1(msg.arg1);
				dataIntent.putExtra(BluetoothUtils.DATA, transmit);
				sendBroadcast(dataIntent);
				break;
			case 1:
				// 通讯成功，读取数据
				dataIntent = new Intent(BluetoothUtils.ACTION_DATA_TO_GAME);
				transmit = new Transmit();
				transmit.setMsg((String) msg.obj);
				transmit.setWhat(msg.what);
				dataIntent.putExtra(BluetoothUtils.DATA, transmit);
				sendBroadcast(dataIntent);
				break;
			case 5:
				// 静压
				dataIntent = new Intent(BluetoothUtils.ACTION_DATA_TO_GAME);
				transmit = new Transmit();
				transmit.setMsg((String) msg.obj);
				transmit.setWhat(2);
				dataIntent.putExtra(BluetoothUtils.DATA, transmit);
				sendBroadcast(dataIntent);
				break;
			case 6:
				// 血样波形图数据
				dataIntent = new Intent(BluetoothUtils.ACTION_DATA_TO_GAME);
				transmit = new Transmit();
				transmit.setMsg((String) msg.obj);
				transmit.setWhat(2);
				dataIntent.putExtra(BluetoothUtils.DATA, transmit);
				sendBroadcast(dataIntent);
				break;
			}
			super.handleMessage(msg);
		}

	};

	/**
	 * Service销毁时的回调函数
	 */
	@Override
	public void onDestroy() {
		if (bluetoothAdapter != null)
			bluetoothAdapter.cancelDiscovery();// 停止蓝牙搜索
		closeAll();
		// 解除绑定
		unregisterReceiver(discoveryReceiver);
		unregisterReceiver(controlReceiver);
		super.onDestroy();
		Log.i(TAG, "onDestroy -");
	}

	private void closeAll() {
		synchronized (this) {
			mLastCloseTS = System.currentTimeMillis();
			if (Constants.mCloud_P.equals(deviceType)) {
				if (bloodPressureComm != null) {
					bloodPressureComm.isActive = false;
					bloodPressureComm.stopMeasure();
					bloodPressureComm = null;
				}
			} else if (Constants.mCloud_O.equals(deviceType)) {
				if (bloodOxygenComm != null) {
					bloodOxygenComm.isActive = false;
					bloodOxygenComm.stopMeasure();
					bloodOxygenComm = null;
				}
			}
			if (bluetoothConn != null) {
				bluetoothConn.closeSocket();
				bluetoothConn = null;
			}

			if (useBLE) {
				if (BleHelper != null) {
					BleHelper.disconnect();
					BleHelper = null;
				}
			}

			deviceConnected = false;
			exited = true;
		}
	}

}

package com.medzone.cloud.acl.thread.bluetooth;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.medzone.framework.util.BluetoothUtils;

public class BluetoothBLEHelper {
	public static String ISSC_SPP_SERVICE = "0000fff0-0000-1000-8000-00805f9b34fb";
	public static String ISSC_SPP_SERVICE_RX = "0000fff1-0000-1000-8000-00805f9b34fb";
	public static String ISSC_SPP_SERVICE_TX = "0000fff2-0000-1000-8000-00805f9b34fb";
	private static final long SCAN_PERIOD = 10000;
	private static final String TAG = "BluetoothBLEHelper";
	public static final int STATE_NONE = 0;
	public static final int STATE_FOUND = 1;
	public static final int STATE_CONNECTING = 2;
	public static final int STATE_CONNECTED = 3;

	private Context mContext;
	private Handler mContextHandler;
	private BluetoothGatt mBluetoothGatt;
	private BluetoothAdapter mBluetoothAdapter;
	private String mBluetoothDeviceAddress;
	private String mType;
	private BluetoothGattCharacteristic mReader;
	private BluetoothGattCharacteristic mWriter;
	private BluetoothBLEInputStream mInputStream = null;
	private BluetoothBLEOutputStream mOutputStream = null;
	private boolean mScanning;
	private boolean mFound = false;
	private int mConnectionState = STATE_NONE;

	public BluetoothBLEHelper(Context context, String type, Handler handler) {
		mContext = context;
		mType = type;

		mContextHandler = handler;
	}

	@SuppressLint("InlinedApi")
	public boolean init() {
		if (mContext == null)
			return false;

		if (mType == null)
			return false;

		if (!mContext.getPackageManager().hasSystemFeature(
				PackageManager.FEATURE_BLUETOOTH_LE)) {
			return false;
		}

		// Initializes a Bluetooth adapter. For API level 18 and above, get a
		// reference to
		// BluetoothAdapter through BluetoothManager.
		final BluetoothManager bluetoothManager = (BluetoothManager) mContext
				.getSystemService(Context.BLUETOOTH_SERVICE);
		mBluetoothAdapter = bluetoothManager.getAdapter();

		// Checks if Bluetooth is supported on the device.
		if (mBluetoothAdapter == null) {
			return false;
		}

		// Ensures Bluetooth is enabled on the device. If Bluetooth is not
		// currently enabled,
		// fire an intent to display a dialog asking the user to grant
		// permission to enable it.
		if (!mBluetoothAdapter.isEnabled()) {
			if (!mBluetoothAdapter.isEnabled()) {
				Intent enableBtIntent = new Intent(
						BluetoothAdapter.ACTION_REQUEST_ENABLE);
				mContext.sendBroadcast(enableBtIntent);
				return false;
			}
		}

		return true;
	}

	public void scanLeDevice(final boolean enable) {
		if (enable) {
			mHandler.removeMessages(0);
			mHandler.sendEmptyMessageDelayed(0, SCAN_PERIOD);// Stops
																// scanning
																// after a
																// pre-defined
																// scan
																// period.
			mScanning = true;
			mFound = false;
			mBluetoothAdapter.startLeScan(mLeScanCallback);
		} else {
			mScanning = false;
			mBluetoothAdapter.stopLeScan(mLeScanCallback);
		}
	}

	private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {

		@Override
		public void onLeScan(final BluetoothDevice device, int rssi,
				byte[] scanRecord) {
			if (device == null || device.getName() == null)
				return;

			if (mType.equals(device.getName())) {
				Intent deviceListIntent = new Intent(
						BluetoothUtils.ACTION_FOUND_DEVICE);
				deviceListIntent.putExtra(BluetoothUtils.DEVICE, device);
				mContext.sendBroadcast(deviceListIntent);

				mFound = true;

				scanLeDevice(false);

				mConnectionState = STATE_FOUND;
			}
		}
	};

	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				if (mScanning) {
					mScanning = false;
					mBluetoothAdapter.stopLeScan(mLeScanCallback);

					if (!mFound) {
						Intent foundIntent = new Intent(
								BluetoothUtils.ACTION_NOT_FOUND_DEVICE);
						mContext.sendBroadcast(foundIntent);
					}
				}
				break;
			}
			super.handleMessage(msg);
		}

	};

	public boolean connect(final String address) {
		if (mBluetoothAdapter == null || address == null) {
			Log.w(TAG,
					"BluetoothAdapter not initialized or unspecified address.");
			return false;
		}

		// Previously connected device. Try to reconnect.
		if (mBluetoothDeviceAddress != null
				&& address.equals(mBluetoothDeviceAddress)
				&& mBluetoothGatt != null) {
			Log.d(TAG,
					"Trying to use an existing mBluetoothGatt for connection.");
			if (mBluetoothGatt.connect()) {
				mConnectionState = STATE_CONNECTING;
				return true;
			} else {
				return false;
			}
		}

		final BluetoothDevice device = mBluetoothAdapter
				.getRemoteDevice(address);
		if (device == null) {
			Log.w(TAG, "Device not found.  Unable to connect.");
			return false;
		}
		// We want to directly connect to the device, so we are setting the
		// autoConnect
		// parameter to false.
		mBluetoothGatt = device.connectGatt(mContext, false, mGattCallback);
		Log.d(TAG, "Trying to create a new connection.");
		mBluetoothDeviceAddress = address;
		mConnectionState = STATE_CONNECTING;
		return true;
	}

	public void disconnect() {
		if (mBluetoothGatt != null) {
			mBluetoothGatt.disconnect();
			mBluetoothGatt.close();
			mBluetoothGatt = null;
		}
	}

	public InputStream getInputStream() {
		return mInputStream;
	}

	public OutputStream getOutputStream() {
		return mOutputStream;
	}

	public int getState() {
		return mConnectionState;
	}

	private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
		@Override
		public void onConnectionStateChange(BluetoothGatt gatt, int status,
				int newState) {
			if (newState == BluetoothProfile.STATE_CONNECTED) {
				Log.d(TAG, "BLE connected!");
				mConnectionState = STATE_CONNECTED;
				mBluetoothGatt.discoverServices();
			} else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
				Log.e(TAG, "BLE disconnected!");
				mConnectionState = STATE_NONE;
				Intent succIntent = new Intent(
						BluetoothUtils.ACTION_CONNECT_ERROR);
				mContext.sendBroadcast(succIntent);
			}
		}

		@Override
		public void onServicesDiscovered(BluetoothGatt gatt, int status) {
			if (status == BluetoothGatt.GATT_SUCCESS) {
				initAllData();
				Intent succIntent = new Intent(
						BluetoothUtils.ACTION_CONNECT_SUCCESS);
				mContext.sendBroadcast(succIntent);
				Message msg = mContextHandler
						.obtainMessage(BluetoothUtils.MESSAGE_CONNECT_SUCCESS);
				mContextHandler.sendMessageDelayed(msg, 100);
			} else {
				Log.w(TAG, "onServicesDiscovered received: " + status);
			}
		}

		@Override
		public void onCharacteristicRead(BluetoothGatt gatt,
				BluetoothGattCharacteristic characteristic, int status) {
			if (status == BluetoothGatt.GATT_SUCCESS) {
				// ------------receive data from issc dual-spp module-----------
				if (ISSC_SPP_SERVICE_RX.equals(characteristic.getUuid()
						.toString())) {
					final byte[] data = characteristic.getValue();
					Log.e(TAG, "recv =" + Arrays.toString(data));
					mInputStream.injectData(data);
				}
			}
		}

		@Override
		public void onCharacteristicChanged(BluetoothGatt gatt,
				BluetoothGattCharacteristic characteristic) {
			if (ISSC_SPP_SERVICE_RX.equals(characteristic.getUuid().toString())) {
				final byte[] data = characteristic.getValue();
				Log.e("recv", Arrays.toString(data));
				mInputStream.injectData(data);
			}
		}
	};

	private void initAllData() {
		if (mBluetoothGatt == null)
			return;

		List<BluetoothGattService> services = mBluetoothGatt.getServices();
		for (BluetoothGattService gattService : services) {
			List<BluetoothGattCharacteristic> gattCharacteristics = gattService
					.getCharacteristics();

			for (BluetoothGattCharacteristic characteristic : gattCharacteristics) {
				String uuid = characteristic.getUuid().toString();
				if (uuid.equals(ISSC_SPP_SERVICE_RX)) {
					mReader = characteristic;
					mInputStream = new BluetoothBLEInputStream();
					Log.v(TAG, "ble send to ISSC_SPP_SERVICE_RX");
				} else if (uuid.equals(ISSC_SPP_SERVICE_TX)) {
					mWriter = characteristic;
					mOutputStream = new BluetoothBLEOutputStream(
							mBluetoothGatt, mWriter, mReader);
				}
			}
		}
	}
}

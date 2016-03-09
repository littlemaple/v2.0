/**
 * 
 */
package com.medzone.cloud.ui.fragment;

import android.bluetooth.BluetoothDevice;
import android.content.Intent;

import com.medzone.framework.Log;
import com.medzone.framework.data.bean.imp.Device;
import com.medzone.framework.fragment.BaseFragment;
import com.medzone.framework.util.BluetoothUtils;

/**
 * @author ChenJunQi.
 * 
 */
public class BluetoothFragment extends BaseFragment {

	protected void sendStartDiscover(Device attachDevice) {
		Intent intent = new Intent(BluetoothUtils.ACTION_START_DISCOVERY);
		intent.putExtra(BluetoothUtils.DATA, attachDevice);
		Log.e("attachDevice#sendStartDiscover$" + attachDevice);
		getActivity().sendBroadcast(intent);
	}

	protected void sendSelectedDevice(BluetoothDevice device) {
		Intent selectDeviceIntent = new Intent(
				BluetoothUtils.ACTION_SELECTED_DEVICE);
		selectDeviceIntent.putExtra(BluetoothUtils.DEVICE, device);
		getActivity().sendBroadcast(selectDeviceIntent);
	}

	protected void sendStartMeasureBroadCast(Device attachDevice) {
		Intent intent = new Intent(BluetoothUtils.ACTION_START_COMMUN);
		intent.putExtra(BluetoothUtils.DATA, attachDevice.getDeviceType());
		Log.e("attachDevice#sendStartMeasureBroadCast$"
				+ attachDevice.getDeviceType());
		getActivity().sendBroadcast(intent);
	}

	protected void sendPauseMeasure() {
		Intent intent = new Intent(BluetoothUtils.ACTION_PAUSE_MEASURE);
		getActivity().sendBroadcast(intent);
	}

	protected void sendStopMeasure() {
		Intent intent = new Intent(BluetoothUtils.ACTION_STOP_MEASURE);
		getActivity().sendBroadcast(intent);
	}

}

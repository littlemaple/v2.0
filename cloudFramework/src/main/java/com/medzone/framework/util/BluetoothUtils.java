package com.medzone.framework.util;

import java.util.UUID;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Build;

/**
 * 蓝牙工具类
 * 
 * @author lwm
 * 
 */
@TargetApi(Build.VERSION_CODES.ECLAIR)
public class BluetoothUtils {

	public static final BluetoothAdapter adapter = BluetoothAdapter
			.getDefaultAdapter();

	/**
	 * 本程序所使用的UUID
	 */
	public static final UUID PRIVATE_UUID = UUID
			.fromString("00001101-0000-1000-8000-00805F9B34FB");

	/**
	 * 字符串常量，存放在Intent中的设备对象
	 */
	public static final String DEVICE = "DEVICE";

	/**
	 * 字符串常量，Intent中的数据
	 */
	public static final String DATA = "DATA";

	/**
	 * Action类型标识符，Action类型 为读到数据
	 */
	public static final String ACTION_READ_DATA = "ACTION_READ_DATA";

	/**
	 * Action类型标识符，Action类型为不支持蓝牙设备
	 */
	public static final String ACTION_NOT_SUPPORT_BLUETOOTH = "ACTION_NOT_SUPPORT_BLUETOOTH";

	/**
	 * Action类型标识符，Action类型为 未发现设备
	 */
	public static final String ACTION_NOT_FOUND_DEVICE = "ACTION_NOT_FOUND_DEVICE";

	/**
	 * Action类型标识符，Action类型为 开始搜索设备
	 */
	public static final String ACTION_START_DISCOVERY = "ACTION_START_DISCOVERY";

	/**
	 * Action：设备列表
	 */
	public static final String ACTION_FOUND_DEVICE = "ACTION_FOUND_DEVICE";

	/**
	 * Action：选择的用于连接的设备
	 */
	public static final String ACTION_SELECTED_DEVICE = "ACTION_SELECTED_DEVICE";
	/**
	 * Action类型标识符，Action类型为 断开设备连接
	 */
	public static final String ACTION_DISCONNECTION_DEVICE = "ACTION_DISCONNECTION_DEVICE";

	/**
	 * Action：关闭后台Service
	 */
	public static final String ACTION_STOP_SERVICE = "ACTION_STOP_SERVICE";
	/**
	 * Action：暂停测量
	 */
	public static final String ACTION_PAUSE_MEASURE = "ACTION_PAUSE_MEASURE";

	/**
	 * Action：停止测量
	 */
	public static final String ACTION_STOP_MEASURE = "ACTION_STOP_MEASURE";

	/**
	 * Action：到业务中的数据
	 */
	public static final String ACTION_DATA_TO_GAME = "ACTION_DATA_TO_GAME";
	/**
	 * Action：连接中
	 */
	public static final String ACTION_CONNECT_IN = "ACTION_CONNECT_IN";

	/**
	 * Action：连接成功
	 */
	public static final String ACTION_CONNECT_SUCCESS = "ACTION_CONNECT_SUCCESS";

	/**
	 * Action：连接错误
	 */
	public static final String ACTION_CONNECT_ERROR = "ACTION_CONNECT_ERROR";
	/**
	 * Action：开始通讯
	 */
	public static final String ACTION_START_COMMUN = "ACTION_START_COMMUN";

	/**
	 * Message类型标识符，连接成功
	 */
	public static final int MESSAGE_CONNECT_SUCCESS = 0x00000002;

	/**
	 * Message：连接失败
	 */
	public static final int MESSAGE_CONNECT_ERROR = 0x00000003;

	/**
	 * Message：连接中
	 */
	public static final int MESSAGE_CONNECT_IN = 0x00000004;

	/**
	 * 打开蓝牙功能
	 */
	public static void openBluetooth() {
		adapter.enable();
	}

	/**
	 * 关闭蓝牙功能
	 */
	public static void closeBluetooth() {
		adapter.disable();
	}

	/**
	 * 设置蓝牙发现功能
	 * 
	 * @param duration
	 *            设置蓝牙发现功能打开持续秒数（值为0至300之间的整数）
	 */
	public static Intent openDiscovery(int duration) {
		if (duration <= 0 || duration > 300) {
			duration = 200;
		}
		Intent discoveryIntent = new Intent(
				BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
		discoveryIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION,
				duration);
		discoveryIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		return discoveryIntent;
	}

	/**
	 * 停止蓝牙搜索
	 */
	public static void stopDiscovery() {
		adapter.cancelDiscovery();
	}

	public static final short REMOVE_CALLBACK = 0;
	public static final short QUERY_TERMAINAL = 1;
	public static final short START_MEASURE = 2;
	public static final short PAUSE_MEASURE = 3;
	public static final short RESEND_LAST_DATA = 4;
	public static final short INIT = 5;
	public static final short STATIC_BP = 6;

	public final static int[] MEASURE_CMD_QUERY_TERMINAL = { 0xAA, 0xAA, 0xAA,
			0xAA, 0xAA, 0xAA, 0xAA, 0xAB, 0x01, 0x31, 0xCE, 0x7E };
	public final static int[] MEASURE_CMD_START = { 0xAA, 0xAA, 0xAA, 0xAA,
			0xAA, 0xAA, 0xAA, 0xAB, 0x01, 0x32, 0xCD, 0x7E };
	public final static int[] MEASURE_CMD_STOP = { 0xAA, 0xAA, 0xAA, 0xAA,
			0xAA, 0xAA, 0xAA, 0xAB, 0x01, 0x33, 0xCC, 0x7E };
	public final static int[] MEASURE_CMD_RESEND_LAST_DATA = { 0xAA, 0xAA,
			0xAA, 0xAA, 0xAA, 0xAA, 0xAA, 0xAB, 0x01, 0x35, 0xCA, 0x7E };
	public final static int[] MEASURE_CMD_INIT = { 0xAA, 0xAA, 0xAA, 0xAA,
			0xAA, 0xAA, 0xAA, 0xAB, 0x01, 0x3F, 0xC0, 0x7E };
	public final static int[] MEASURE_CMD_STATIC_BP = { 0xAA, 0xAA, 0xAA, 0xAA,
			0xAA, 0xAA, 0xAA, 0xAB, 0x01, 0x34, 0xCB, 0x7E };

}

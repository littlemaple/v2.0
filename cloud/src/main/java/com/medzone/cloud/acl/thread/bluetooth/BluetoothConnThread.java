package com.medzone.cloud.acl.thread.bluetooth;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Message;

import com.medzone.cloud.acl.service.test.Performance;
import com.medzone.framework.Log;
import com.medzone.framework.util.BluetoothUtils;

/**
 * 蓝牙客户端连接线程
 * 
 * @author lwm
 * 
 */
public class BluetoothConnThread extends Thread {

	private static final String TAG = "BluetoothConnThread*_*";
	private Handler serviceHandler;       // 用于向客户端Service回传消息的handler
	private BluetoothDevice serverDevice; // 服务器设备
	private BluetoothSocket socket;       // 通信Socket
	private String  mAddress = null;
	private boolean exit = false;
	private static final Method _createRfcommSocket = getMethod(
			BluetoothDevice.class, "createInsecureRfcommSocket",
			new Class[] { int.class });


	/**
	 * 构造函数
	 * 
	 * @param handler
	 * @param serverDevice
	 */
	public BluetoothConnThread(Handler handler, BluetoothDevice serverDevice) {
		this.serviceHandler = handler;
		this.mAddress = serverDevice.getAddress();
		Performance.to = serverDevice.getName() + mAddress.substring(mAddress.lastIndexOf(':'));
		// 创建socket
		createSocket();
	}
	
	public BluetoothConnThread(String  address) {
		this.mAddress = address;
		// 创建socket
		createSocket();
	}

	@Override
	public void run() {
		Log.i(TAG, "BluetoothConnThread +");
		try {
//			if (BluetoothConfig.mbNeedSleep)
			{
				Thread.sleep(BluetoothConfig.mWaitingTime);
			}
			
			BluetoothUtils.adapter.cancelDiscovery();
			
			if (socket == null)
				throw new Exception("socket null");

			// 连接socket
			if (serviceHandler != null)
				serviceHandler.obtainMessage(BluetoothUtils.MESSAGE_CONNECT_IN)
						.sendToTarget();
			Performance.tsConnectStart = System.currentTimeMillis();

			socket.connect();

			Performance.tsConnectSucceed = System.currentTimeMillis();

			// 检查 socket
			if (!checkSocket()) {
				Performance.tsQueryFailed = System.currentTimeMillis();
				Performance.save();
				throw new Exception("socket io failed");
			} else {
				Performance.tsQueryReceived = System.currentTimeMillis();
				Performance.save();
			}

			if (serviceHandler != null) {
				Message msg = serviceHandler.obtainMessage();
				msg.what = BluetoothUtils.MESSAGE_CONNECT_SUCCESS;
				msg.obj = socket;
				msg.sendToTarget();
			}
			
		} catch (Exception ex) {
			ex.printStackTrace();
			
			closeSocketEx();
				
			if (Performance.tsQueryFailed == 0){
				Performance.tsConnectedFailed = System.currentTimeMillis();
				Performance.save();
			}

			if (serviceHandler!=null) {
				serviceHandler.obtainMessage(BluetoothUtils.MESSAGE_CONNECT_ERROR)
					.sendToTarget();
			}
		}

		if (exit) {
			closeSocket();
		}

		Log.i(TAG, "BluetoothConnThread -");
	}

	private void createSocket() {
		socket = null;
		serverDevice = BluetoothUtils.adapter.getRemoteDevice(mAddress);
		BluetoothSocket localSocket = null;
		if (BluetoothConfig.mbUnsafeMode && Build.VERSION.SDK_INT > Build.VERSION_CODES.GINGERBREAD) {
			try {
				localSocket = serverDevice
						.createInsecureRfcommSocketToServiceRecord(BluetoothUtils.PRIVATE_UUID);
				socket = localSocket;
			} catch (IOException e2) {
				e2.printStackTrace();
			}
		}
		else if(BluetoothConfig.mbUseFixedChannel)
		{
			try {
				localSocket = createRfcommSocket(6);
				socket = localSocket;
			} catch (Exception e2) {
				e2.printStackTrace();
			}
			
		}
		else {
			try {
				localSocket = serverDevice
						.createRfcommSocketToServiceRecord(BluetoothUtils.PRIVATE_UUID);

				socket = localSocket;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void closeSocket() {
		exit = true;
		closeSocketEx();
	}

	private synchronized void closeSocketEx() {
		Log.i(TAG, "closeSocket+");
		try {
			if (socket != null) {
				socket.close();
				socket = null;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		Log.i(TAG, "closeSocket-");
	}

	private synchronized boolean checkSocket() {
		boolean result = false;
		try {
			OutputStream os = socket.getOutputStream();
			InputStream is = socket.getInputStream();
			os.write(intArrayToByte(BluetoothUtils.MEASURE_CMD_QUERY_TERMINAL));
			int len = 0;
			for (int i = 0; i < 6; i++) {
				Thread.sleep(500);
				len = is.available();
				if (len > 0) {
					result = true;
					break;
				}
			}
			if (len > 0) {
				byte[] bytes = new byte[len];
				is.read(bytes);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		return result;
	}
	
	private static byte[] intArrayToByte(int[] array) {
		int i = 0;
		byte[] bos = new byte[12];
		for (i = 0; i < 12; i++) {
			bos[i] = (byte) array[i];
		}
		return bos;
	}
	
	public static synchronized void initBluetooth(Context context){
		BluetoothUtils.adapter.disable();
		Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
		context.startActivity(enableIntent);
	}

	public static synchronized boolean pair(BluetoothDevice device) {
		boolean result = false;
		try {
			Log.d("mylog", "NOT BOND_BONDED");
			setPin(device, "0000"); // 手机和蓝牙采集器配对
			createBond(device);
			result = true;
		} catch (Exception e) {
			Log.d("mylog", "setPiN failed!");
			e.printStackTrace();
		}

		return result;
	}

	static public boolean setPin(BluetoothDevice btDevice, String str)
			throws Exception {
		try {
			Method removeBondMethod = BluetoothDevice.class.getDeclaredMethod(
					"setPin", new Class[] { byte[].class });
			Boolean returnValue = (Boolean) removeBondMethod.invoke(btDevice,
					new Object[] { str.getBytes() });
			Log.e("returnValue", "" + returnValue);
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}
	
	static public boolean createBond(BluetoothDevice btDevice) throws Exception {
		Method createBondMethod = BluetoothDevice.class.getMethod("createBond");
		Boolean returnValue = (Boolean) createBondMethod.invoke(btDevice);
		return returnValue.booleanValue();
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	static public synchronized boolean removeBond(Class btClass,
			BluetoothDevice btDecive) throws Exception {
		Method removeBondMethod = btClass.getMethod("removeBond");
		Boolean retrunVlaue = (Boolean) removeBondMethod.invoke(btDecive);
		return retrunVlaue.booleanValue();
	}
	
	public BluetoothSocket createRfcommSocket(int channel) throws Exception {
		if (_createRfcommSocket == null)
			throw new NoSuchMethodException("createInsecureRfcommSocket");
		
		try {
			return (BluetoothSocket) _createRfcommSocket.invoke(serverDevice,
					channel);
		} catch (InvocationTargetException tex) {
			tex.printStackTrace();
			
			if (tex.getCause() instanceof Exception)
				throw (Exception) tex.getCause();
			else
				throw tex;
		}
	}
	
	private static Method getMethod(Class<?> cls, String name, Class<?>[] args) {
		try {
			return cls.getMethod(name, args);
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

}

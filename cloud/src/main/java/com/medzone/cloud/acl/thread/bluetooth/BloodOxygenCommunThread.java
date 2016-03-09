package com.medzone.cloud.acl.thread.bluetooth;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.Queue;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.medzone.framework.util.BluetoothUtils;

public class BloodOxygenCommunThread extends BluetoothCommun {
	// private BluetoothSocket socket;
	// 测量完成后 获取的稳定的测量值
	private InputStream mInputStream;
	private OutputStream mOutputStream;
	private AcceptThread acceptThread;
	private int[] dynamicStableResultArray = new int[3];
	private boolean BAT_LOW_FLAG = true;
	private short receivedPackageSerialNumber = 0;
	public boolean isActive = false;// run() 是否正在进行中
	private Queue<Integer> mCommandList = new LinkedList<Integer>();
	private int mCmdSend = -1;
	private int mCmdReceived = -1;
	private static final String SEPARATOR = " ";// 分隔符
	private static final String TAG = "BloodOxygenCommunThread*_*";

	public BloodOxygenCommunThread(Handler handler, InputStream input,
			OutputStream output) {
		super(handler);

		mInputStream = input;
		mOutputStream = output;
		// this.socket = socket;
		acceptThread = new AcceptThread();

		acceptThread.start();
		new Thread(mSendRunnable).start();
	}

	private synchronized void sendMessage(short flag) {
		if (flag == 0)
			return;

		boolean isEmpty = false;
		synchronized (mCommandList) {
			isEmpty = mCommandList.isEmpty();
			mCommandList.add((int) flag);
		}
		if (isEmpty) {
			synchronized (mSendRunnable) {
				mSendRunnable.notify();
			}
		}
	}

	public synchronized void measure() {
		sendMessage(BluetoothUtils.QUERY_TERMAINAL);
	}

	public synchronized void pauseMeasure() {
		sendMessage(BluetoothUtils.PAUSE_MEASURE);
	}

	public synchronized void stopMeasure() {
		Log.i(TAG, "stopMeasure +");
		isActive = false;

		if (acceptThread != null) {
			acceptThread.cancel();
			acceptThread = null;
		}

		synchronized (mSendRunnable) {
			mSendRunnable.notify();
		}

		synchronized (BloodOxygenCommunThread.this) {
			BloodOxygenCommunThread.this.notify();
		}

		Log.i(TAG, "stopMeasure -");
	}

	private synchronized void onMessageReceived(int command) {
		Log.d(TAG, "recv " + command);
		if (mCmdSend == command) {
			mCmdReceived = command;
			synchronized (BloodOxygenCommunThread.this) {
				BloodOxygenCommunThread.this.notify();
			}
		}

	}

	Runnable mSendRunnable = new Runnable() {
		@Override
		public void run() {
			Log.i(TAG, "oxygen send thread +");
			if (acceptThread == null) {
				Log.i(TAG, "send thread -");
				return;
			}

			while (isActive) {
				boolean isEmpty = false;
				synchronized (mCommandList) {
					isEmpty = mCommandList.isEmpty();
				}
				if (isEmpty) {
					synchronized (mSendRunnable) {
						try {
							mSendRunnable.wait();
						} catch (InterruptedException e1) {
							e1.printStackTrace();
						}
					}
				}

				if (!isActive)
					break;

				int command;
				synchronized (mCommandList) {
					command = mCommandList.remove();
				}
				mCmdReceived = -1;
				int resendCmdTimes = 0;
				final int WAIT_TIME = 3;
				mCmdSend = command;
				Log.d(TAG, "send " + command);
				while (resendCmdTimes < WAIT_TIME && mCmdReceived != command) {
					switch (command) {
					case BluetoothUtils.QUERY_TERMAINAL: {
						// acceptThread.sendCMDQueryTerminal();
						acceptThread.sendCMDStartMeasure();
						break;
					}
					case BluetoothUtils.START_MEASURE: {
						acceptThread.sendCMDStartMeasure();
						break;
					}
					case BluetoothUtils.PAUSE_MEASURE: {
						acceptThread.sendCMDStopMeasure();
						break;
					}
					default:
						break;
					}
					resendCmdTimes++;
					if (!isActive)
						break;
					// int nextTime = (command == BluetoothUtils.START_MEASURE)
					// ? 6000
					// : 3000;
					// synchronized (BloodOxygenCommunThread.this) {
					// try {
					// BloodOxygenCommunThread.this.wait(nextTime);
					// } catch (InterruptedException e) {
					// e.printStackTrace();
					// }
					// }
					// if (!isActive)
					// break;
				}

				// if (!isActive)
				// break;
				//
				// if (resendCmdTimes >= WAIT_TIME && mCmdReceived != command) {
				// Log.e(TAG, " " + command + "not received time out");
				// // 手机发送指令超时，请重启测量或退出测量
				// // mSendMessage(0, 0, 0, "手机发送指令超时，请重启测量或退出测量");
				// }
			}

			Log.i(TAG, "oxygen send thread -");
		}
	};

	// 与下位机交换数据的线程
	public class AcceptThread extends Thread {
		private boolean isCmdSendOut = false;// 是否向远程设备传输命令
		private boolean isConnectSuccess = false;
		InputStream is;

		public AcceptThread() {
			isConnectSuccess = true;
			is = mInputStream;
			isActive = true;
		}

		@Override
		public void run() {
			Log.i(TAG, "oxygen receive thread +");
			byte[] buffer = new byte[256];
			short[] decodeBuffer = new short[256];
			int num = 0;
			int i = 0;
			int n = 0;
			while (isActive) {
				try {
					int available = 0;
					do {
						sleep(50);
						if (!isActive) {
							break;
						}
						available = is.available();
					} while (available == 0);
					if (!isActive) {
						break;
					}
					Log.d(TAG, "oxygen is.read +");
					num = is.read(buffer);
					Log.d(TAG, "oxygen is.read -");
					for (i = 0; i < num; i++) {
						decodeBuffer[n] = signedConvertToUnsigned(buffer[i]);
						n++;
					}
					if (buffer[num - 1] == 0X7E)
						n = 0;
					if (n != 0)
						continue;
					if (isCmdSendOut) {
						decodeDataFrame(decodeBuffer);
					}
				} catch (Exception e) {
					Log.i(TAG, "oxygen receive exception" + e);
					e.printStackTrace();
					if (!isActive) {
						break;
					}
				}
			}
			Log.i(TAG, "oxygen receive thread -");
		}

		public void cancel() {
			try {
				setCmdSendOut(false);
				isActive = false;
				is.close();
				mOutputStream.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		public boolean isActive() {
			return isActive;
		}

		public boolean isConnectSuccess() {
			return isConnectSuccess;
		}

		public void sendCMDQueryTerminal() {
			try {
				mOutputStream
						.write(intArrayToByte(BluetoothUtils.MEASURE_CMD_QUERY_TERMINAL));
				isCmdSendOut = true;
			} catch (IOException e) {
				isCmdSendOut = false;
				e.printStackTrace();
			}
		}

		public void sendCMDStartMeasure() {
			try {
				mOutputStream
						.write(intArrayToByte(BluetoothUtils.MEASURE_CMD_START));
				isCmdSendOut = true;
			} catch (IOException e) {
				isCmdSendOut = false;
				e.printStackTrace();
			}
		}

		public void sendCMDStopMeasure() {
			try {
				mOutputStream
						.write(intArrayToByte(BluetoothUtils.MEASURE_CMD_STOP));
				isCmdSendOut = true;
			} catch (IOException e) {
				isCmdSendOut = false;
				e.printStackTrace();
			}
		}

		private void setCmdSendOut(boolean isCmdSendOut) {
			this.isCmdSendOut = isCmdSendOut;
		}
	}

	// 读取数据后要解码，此处即为解码函数
	public void decodeDataFrame(short[] debuffer) {
		/** 桢头的位置 */
		short frameHeadPos = decodeIdentFrameHead(debuffer, 12);
		// 数据长度
		short dataLen;
		// short check_word;
		short function_word;
		short i;
		short j;
		String remote_device_id = "";// 终端设备ID

		// 如果有帧头,跳过帧头
		if (frameHeadPos > 0) {
			// 桢头+1的位 记录了数据的长度；
			dataLen = debuffer[++frameHeadPos];

			// ============================================//
			// 桢头+3的位 ，记录了设备类型；这个数据解析应该已经被废弃掉了；[15]
			switch ((debuffer[frameHeadPos + 2] & 0x0F)) {

			case 0x01: { // 终端类型为血氧仪
				sendMessage(BluetoothUtils.REMOVE_CALLBACK);
				break;
			}
			default:
				break;
			}

			// ============================================//
			if ((debuffer[frameHeadPos + 2] & 0x80) == 0x00) {

				// 桢头+2的位，记录调用方法，已定义的方法有5个；
				function_word = debuffer[++frameHeadPos];

				switch (function_word & 0x0F) {

				// 响应查询终端ID方法
				case 0x01: {
					sendMessage(BluetoothUtils.REMOVE_CALLBACK);
					onMessageReceived(0x01);
					j = 1;
					remote_device_id = "";
					while (j <= 6) {
						remote_device_id += Integer
								.toHexString(
										(debuffer[frameHeadPos + j + 2] & 0x000000FF) | 0xFFFFFF00)
								.substring(6);
						j++;
					}
					sendMessage(BluetoothUtils.START_MEASURE);
					break;
				}

				// 响应开始检测方法
				case 0x02: {
					onMessageReceived(0x02);
					sendMessage(BluetoothUtils.REMOVE_CALLBACK);
					// 响应开始检查
					if (function_word == 0xA2) {

					}

					if (dataLen != 16) {
						break;
					} else {
						// else if (function_word == 0x82) {// 已取得测量结果
						if (receivedPackageSerialNumber == debuffer[frameHeadPos + 2]) {
							// why here?
							break;
						}

						Message dataExchageMsg = mDataExchangeHandler
								.obtainMessage();
						dataExchageMsg.what = 0;
						// 收到的包序列号 桢头 + 4
						receivedPackageSerialNumber = debuffer[frameHeadPos + 2];

						if ((debuffer[frameHeadPos + 1] & 0x30) == 0x30) {
							// 手指未插入,且电池电量低。无测量结果
							dataExchageMsg.what = 3;
							dataExchageMsg.sendToTarget();
							break;
						} else if ((debuffer[frameHeadPos + 1] & 0x30) == 0x20) {
							// 手指未插入,电池电量正常。无测量结果
							dataExchageMsg.what = 2;
							dataExchageMsg.sendToTarget();
							break;
						} else {

							if ((debuffer[frameHeadPos + 1] & 0x30) == 0x10) {
								// 电池电量低。有测量结果
								dataExchageMsg.what = 1;
							}
							// 组装测量数据
							String data;
							String waveData = "";
							int drawWaveFlag = 0;

							if ((debuffer[frameHeadPos + 1] == 0x41)
									|| (debuffer[frameHeadPos + 1] == 0x01)) {
								data = String
										.valueOf(debuffer[frameHeadPos + 3])
										+ "A"
										+ String.valueOf(debuffer[frameHeadPos + 4])
										+ "B"
										+ String.valueOf(debuffer[frameHeadPos + 5])
										+ "C";
								dataExchageMsg.obj = data;
							}
							dataExchageMsg.sendToTarget();

							// 组装波形数据
							for (i = 1; i <= 10; i++) {
								waveData += String
										.valueOf(debuffer[frameHeadPos + 5 + i] & 0x7F)
										+ SEPARATOR;
							}
							drawWaveFlag++;

							// 通知波形刷新
							if (drawWaveFlag == 1) {
								mSendMessage(6, 0, 0, waveData);
							}
						}
					}
					break;
				}

				case 0x03: {
					onMessageReceived(0x03);
					// 响应了停止测量命令
					sendMessage(BluetoothUtils.REMOVE_CALLBACK);
					break;
				}
				}
			}
		}
	}

	@SuppressLint("HandlerLeak")
	Handler mDataExchangeHandler = new Handler() {

		@SuppressLint("HandlerLeak")
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (null != acceptThread) {
				if (!acceptThread.isConnectSuccess) {
					acceptThread.cancel();
				}
			}
			String strInMsg = "";
			switch (msg.what) {
			case 0: {
				if (null != msg.obj) {
					strInMsg = (String) msg.obj;
					/** 取得稳定数值 << **/
					String strSO2 = strInMsg
							.substring(0, strInMsg.indexOf("A"));
					String strPr = strInMsg.substring(
							strInMsg.indexOf("A") + 1, strInMsg.indexOf("B"));
					String strPi = strInMsg.substring(
							strInMsg.indexOf("B") + 1, strInMsg.indexOf("C"));
					dynamicStableResultArray[0] = Integer.valueOf(strSO2);
					dynamicStableResultArray[1] = Integer.valueOf(strPr);
					dynamicStableResultArray[2] = Integer.valueOf(strPi);
					mSendMessage(1, 0, 0, strSO2 + ";" + strPr);
				}
				break;
			}
			case 1: {
				if (BAT_LOW_FLAG) {
					// 电池电量不足，电池电量过低，检测结果可能不准确，若忽略则继续测量，或结束测量
					mSendMessage(0, 1, 0, "电池电量不足，请更换电池");
					mBatLowCautionTimerHandler.postDelayed(mBatLowRunnable,
							3000);
					BAT_LOW_FLAG = false;
				}
				if (null != msg.obj) {
					strInMsg = (String) msg.obj;
					/** 取得稳定数值 << **/
					String strSO2 = strInMsg
							.substring(0, strInMsg.indexOf("A"));
					String strPr = strInMsg.substring(
							strInMsg.indexOf("A") + 1, strInMsg.indexOf("B"));
					String strPi = strInMsg.substring(
							strInMsg.indexOf("B") + 1, strInMsg.indexOf("C"));
					dynamicStableResultArray[0] = Integer.valueOf(strSO2);
					dynamicStableResultArray[1] = Integer.valueOf(strPr);
					dynamicStableResultArray[2] = Integer.valueOf(strPi);
					mSendMessage(1, 0, 0, strSO2 + ";" + strPr);
				}
				break;
			}
			case 2: {
				// 手指滑出血氧仪，手指未插入血氧仪，无法测量，请将手指插入血氧仪继续测量，或结束测量
				mSendMessage(0, 2, 0, "请将手指插入血氧仪");
				break;
			}
			case 3: {
				// 手指滑出且电量低，手指未插入血氧仪，且电池电量过低，请将手指插入血氧仪继续测量，但检测结果可能不准确，或结束测量
				mSendMessage(0, 3, 0, "电池电量不足，请更换电池");
				break;
			}
			default:
				break;
			}

		}
	};

	Handler mBatLowCautionTimerHandler = new Handler();
	Runnable mBatLowRunnable = new Runnable() {
		@Override
		public void run() {
			BAT_LOW_FLAG = true;
		}
	};

}

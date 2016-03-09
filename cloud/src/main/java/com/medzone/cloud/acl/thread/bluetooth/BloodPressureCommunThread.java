package com.medzone.cloud.acl.thread.bluetooth;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.Queue;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;

import com.medzone.framework.Log;
import com.medzone.framework.util.BluetoothUtils;

public class BloodPressureCommunThread extends BluetoothCommun {
	private static final String TAG = "BloodPressureCommunG*_*";
	private InputStream mInputStream;
	private OutputStream mOutputStream;
	// private BluetoothSocket socket;
	public volatile boolean isActive = false;// run() 是否正在进行中
	private int bufferSize;
	private AcceptThread acceptThread;
	private short resendCmdTimes = 0;
	private short ERROR_TYPE_FLAG = 0;
	private int highPressure = 0;
	private int lowPressure = 0;
	private int heartRate = 0;
	private int staticBp = 0;
	private String deviceTypeAndID = "";
	private Queue<Integer> mCommandList = new LinkedList<Integer>();
	private int mCmdReceived = -1;

	public BloodPressureCommunThread(Handler handler, InputStream input,
			OutputStream output) {
		super(handler);
		mInputStream = input;
		mOutputStream = output;
		acceptThread = new AcceptThread();
		acceptThread.start();
		new Thread(cmdRunable).start();
	}

	private void sendMessage(short flag) {
		boolean isEmpty = false;
		synchronized (mCommandList) {
			isEmpty = mCommandList.isEmpty();
			mCommandList.add((int) flag);
		}
		if (isEmpty) {
			synchronized (cmdRunable) {
				cmdRunable.notify();
			}
		}

	}

	private void onMessageReceived(int command) {
		mCmdReceived = command;
		synchronized (BloodPressureCommunThread.this) {
			BloodPressureCommunThread.this.notify();
		}
	}

	public void measure() {
		sendMessage(BluetoothUtils.QUERY_TERMAINAL);
	}

	public void pauseMeasure() {
		sendMessage(BluetoothUtils.PAUSE_MEASURE);
	}

	Runnable cmdRunable = new Runnable() {
		@Override
		public void run() {
			Log.i(TAG, "pressure send thread +");
			if (acceptThread == null)
				return;

			while (isActive) {
				boolean isEmpty = false;
				synchronized (mCommandList) {
					isEmpty = mCommandList.isEmpty();
				}
				if (isEmpty) {
					synchronized (cmdRunable) {
						try {
							cmdRunable.wait();
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
				resendCmdTimes = 0;
				final int WAIT_TIME = 3;
				while (resendCmdTimes < WAIT_TIME && mCmdReceived != command) {
					Log.d(TAG, "send command " + command);
					switch (command) {
					case BluetoothUtils.QUERY_TERMAINAL: {
						acceptThread.sendCMDQueryTerminal();
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
					case BluetoothUtils.STATIC_BP: {
						acceptThread.sendCMDStaticBP();
						break;
					}
					default:
						break;
					}
					resendCmdTimes++;
					int nextTime = (command == BluetoothUtils.START_MEASURE) ? 60000
							: 3000;
					synchronized (BloodPressureCommunThread.this) {
						try {
							BloodPressureCommunThread.this.wait(nextTime);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}

					if (!isActive)
						break;

				}

				if (!isActive)
					break;

				if (resendCmdTimes >= WAIT_TIME) {
					// 手机发送指令超时，请重启测量或退出测量
					mSendMessage(0, 0, 0, "手机发送指令超时，请重启测量或退出测量");
				}
			}

			Log.i(TAG, "pressure send thread -");
		}
	};

	public class AcceptThread extends Thread {
		private boolean isCmdSendOut = false;// 是否向远程设备传输命令
		private boolean isConnectSuccess = false;
		private InputStream is;

		public AcceptThread() {
			try {
				isConnectSuccess = true;
				is = mInputStream;
				isActive = true;
			} catch (Exception e) {
				isActive = false;
				// 蓝牙连接超时
				stopMeasure();
			}
		}

		@Override
		public void run() {
			Log.i(TAG, "pressure receive thread +");
			// data exchange
			byte[] buffer = new byte[256];
			short[] decodeBufferArray = new short[256];
			int n = 0;
			while (true) {
				try {
					int len = is.available();
					boolean bAvailable = len > 0;
					while (!bAvailable) {
						Thread.sleep(20);
						if (!isActive) {
							break;
						}
						len = is.available();
						bAvailable = len > 0;
					}
					if (!isActive) {
						break;
					}
					Log.d(TAG, "isActive =" + isActive + ",bAvailable =" + len);
					bufferSize = is.read(buffer);
					for (int i = 0; i < bufferSize; i++) {
						decodeBufferArray[n++] = signedConvertToUnsigned(buffer[i]);
					}
					if (buffer[bufferSize - 1] == 0X7E)
						n = 0;
					if (n != 0)
						continue;
					if (acceptThread.isCmdSendOut) {
						decodeDataFrame(decodeBufferArray);
					}
					mDataExchangeHandler.sendMessage(mDataExchangeHandler
							.obtainMessage());
				} catch (Exception e) {
					// TODO Things haven't deal with perfect.
					if (!isActive) {
						e.printStackTrace();
						break;
					}
					e.printStackTrace();
				}
			}
			Log.i(TAG, "pressure receive thread -");
		}

		public void cancel() {
			try {
				Log.d(TAG, "cancel +");
				setCmdSendOut(false);
				isActive = false;
				Log.d(TAG, "cancel - 1");
				is.close();
				Log.d(TAG, "cancel - 2");
				mOutputStream.close();
				Log.d(TAG, "cancel - 3");
			} catch (IOException e) {
				e.printStackTrace();
			}
			// acceptThread = null;
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
				sendWriteError(BluetoothUtils.QUERY_TERMAINAL);
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
				sendWriteError(BluetoothUtils.START_MEASURE);
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
				sendWriteError(BluetoothUtils.PAUSE_MEASURE);
			}
		}

		public void sendCMDStaticBP() {
			try {
				mOutputStream
						.write(intArrayToByte(BluetoothUtils.MEASURE_CMD_STATIC_BP));
				isCmdSendOut = true;
			} catch (IOException e) {
				isCmdSendOut = false;
				e.printStackTrace();
				sendWriteError(BluetoothUtils.STATIC_BP);
			}
		}

		public boolean isCmdSendOut() {
			return isCmdSendOut;
		}

		private void setCmdSendOut(boolean isCmdSendOut) {
			this.isCmdSendOut = isCmdSendOut;
		}
	}

	public void stopMeasure() {
		Log.i(TAG, "stop measure+");
		isActive = false;

		if (acceptThread != null) {
			acceptThread.cancel();
			acceptThread = null;
		}

		synchronized (cmdRunable) {
			cmdRunable.notify();
		}

		synchronized (BloodPressureCommunThread.this) {
			BloodPressureCommunThread.this.notify();
		}
		Log.i(TAG, "stop measure-");
	}

	private void sendWriteError(int cmd) {
		Log.e(TAG, "send command error" + cmd);
		// ERR_POPUP_TYPE = Constants.ERR_POPUP_BP_CMDOUTTIME_SHORT;
		// // 手机发送指令超时，请重启测量或退出测量
		// mSendMessage(0, ERR_POPUP_TYPE, cmd, "手机发送指令错误");
	}

	private void sendReadError() {
		Log.e(TAG, "receive command error");
		// ERR_POPUP_TYPE = Constants.ERR_POPUP_BP_CMDOUTTIME_SHORT;
		// // 手机发送指令超时，请重启测量或退出测量
		// mSendMessage(0, ERR_POPUP_TYPE, -1, "手机接收指令错误");
	}

	// 读取数据后要解码，此处即为解码函数
	private void decodeDataFrame(short[] debuffer) {

		short frame_start = decodeIdentFrameHead(debuffer, bufferSize);
		short data_length;
		short check_word;
		short function_word;
		short i;
		short j;

		String remote_device_model = "";// 终端设备型号
		String remote_device_id = "";// 终端设备ID
		if (frame_start > 0) {
			frame_start++;
			data_length = debuffer[frame_start];
			check_word = 0xFF;
			for (i = 1; i <= data_length; i++)
				check_word = (short) (check_word ^ debuffer[frame_start + i]);
			if ((debuffer[frame_start + 1] & 0x80) != 0x80
					|| check_word != debuffer[frame_start + i]
					|| debuffer[frame_start + i + 1] != 0x7E) {
				// warnMessage += "数据帧错误";// 应答帧错误：非应答帧，校验字错误，帧尾错误
			}
			if ((debuffer[frame_start + 1] & 0x40) != 0x00) {
				// warnMessage += "接收数据帧错误";// test ok
			}
			switch ((debuffer[frame_start + 2] & 0x0F)) {
			case 0x03: {
				remote_device_model = "血压终端";
				break;
			}
			default:
				break;
			}
			// warnMessage += "所使用的设备是：" + remote_device_model; // test ok;
			if ((debuffer[frame_start + 2] & 0x80) == 0x00) {
				// warnMessage = "工作正常";// 如果工作正常，就继续解码//test ok
				frame_start++;
				function_word = debuffer[frame_start];
				switch (function_word & 0x0F) {
				// 对查询终端状态的应答
				case 0x01: {
					onMessageReceived(0x01);
					j = 1;
					remote_device_id = "";
					while (j <= 6) {
						remote_device_id += Integer
								.toHexString(
										(debuffer[frame_start + j + 2] & 0x000000FF) | 0xFFFFFF00)
								.substring(6);
						// remote_device_id += " ";
						j++;
					}
					deviceTypeAndID = remote_device_id;
					sendMessage(BluetoothUtils.START_MEASURE);
					// warnMessage += "\n终端设备ID：" + remote_device_id;
					break;
				}
				case 0x02: {
					onMessageReceived(0x02);
					if (function_word == 0xA2) {
						// warnMessage += "\n要开始测量了！wait！ ";
					} else if (function_word == 0x92) {
						if (acceptThread != null) {
							acceptThread.setCmdSendOut(false);
						}
						// warnMessage = "已取得测量结果";
						highPressure = 256 * debuffer[frame_start + 3]
								+ debuffer[frame_start + 4];
						lowPressure = 256 * debuffer[frame_start + 5]
								+ debuffer[frame_start + 6];
						heartRate = 256 * debuffer[frame_start + 7]
								+ debuffer[frame_start + 8];

						if (highPressure == 4352 && lowPressure == 4352
								&& heartRate == 4352) {
							ERROR_TYPE_FLAG = 1;
							// warnMessage += "\n血压检测发生错误";
						} else if (highPressure == 21760
								&& lowPressure == 21760 && heartRate == 21760) {
							ERROR_TYPE_FLAG = 2;
							// warnMessage += "\n电压偏低，请更换电池";
						} else if (highPressure == 65280
								&& lowPressure == 65280 && heartRate == 65280) {
							ERROR_TYPE_FLAG = 3;
							// warnMessage += "\n充气错误:打气速度低或者过压";
						} else {
							if (heartRate > 255) {
								heartRate -= 65280;
							}
							// ## 取得正确测量结果
							completeMeasure();
						}

					} else {
						if (data_length < 8) {
							if (function_word == 0xA4) {
								// 静压检测开始
							} else if (function_word == 0x82) {
								staticBp = 256 * debuffer[frame_start + 3]
										+ debuffer[frame_start + 4];
								mSendMessage(5, 0, 0, staticBp + "");
							}
						} else if (data_length >= 9) {
							highPressure = 256 * debuffer[frame_start + 3]
									+ debuffer[frame_start + 4];
							lowPressure = 256 * debuffer[frame_start + 5]
									+ debuffer[frame_start + 6];
							heartRate = 256 * debuffer[frame_start + 7]
									+ debuffer[frame_start + 8];
							if (highPressure == 21760 && lowPressure == 21760
									&& heartRate == 21760)
								ERROR_TYPE_FLAG = 2;
						}
					}
					break;
				}
				// 对应于结束检测的应答-即没有取到正确的结果便结束测量
				case 0x03: {
					onMessageReceived(0x03);
					break;
				}

				case 0x04: {
					onMessageReceived(0x04);
					break;
				}
				}
			}
		}
	}

	private void completeMeasure() {
		String data = Integer.toString(highPressure) + ";"
				+ Integer.toString(lowPressure) + ";"
				+ Integer.toString(heartRate);
		mSendMessage(1, 0, 0, data);
	}

	/**
	 * 错误提示处理handler
	 * */
	@SuppressLint("HandlerLeak")
	Handler mDataExchangeHandler = new Handler() {
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (null != acceptThread) {
				if (!acceptThread.isConnectSuccess) {
					acceptThread.cancel();
				}
			}
			switch (ERROR_TYPE_FLAG) {
			case 0:
				break;
			case 1: {
				mSendMessage(0, 1, 0, "设备异常，请重新测量");
				// 血压检测错误，血压终端检测发生错误，请重新开始或退出测量
				ERROR_TYPE_FLAG = 0;
				break;
			}
			case 2: {
				mSendMessage(0, 2, 0, "电池电量不足，请更换电池");
				// 电池电量不足，电池电量过低，血压检测不能进行，请更换电池后重新开始
				ERROR_TYPE_FLAG = 0;
				break;
			}
			case 3: {
				mSendMessage(0, 3, 0, "设备异常，请重新测量");
				// 充气错误，打气速度低或者过压，血压检测失败，请重新开始或退出测量
				ERROR_TYPE_FLAG = 0;
				break;
			}
			default:
				break;
			}
		}
	};
}

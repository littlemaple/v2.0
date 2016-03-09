package com.medzone.cloud.acl.audio.decode;

import java.util.ArrayList;
import java.util.List;

import android.os.Handler;
import android.os.Message;

import com.medzone.framework.util.AudioUtils;

public class AudioResultAnalyzer {

	private boolean assertResult;
	private Handler handler;

	public AudioResultAnalyzer(Handler handler) {
		this.handler = handler;
		assertResult = false;
	}

	public synchronized boolean getAssertResult() {
		return assertResult;
	}

	public synchronized void analyze(AudioDecodeResult audioDecodeResult) {
		int type = audioDecodeResult.type;
		List<Short> list = new ArrayList<Short>();
		list.addAll(audioDecodeResult.decimalResultList);
		int start = findStartofFrame(list);
		String str = "";
		assertResult = false;
		if (start != -1) {
			switch (type) {
			case 0: {
				str = "多包:" + convertListToHexStringWithSpace(list);
				sendMessageToUI(AudioUtils.MSG_MESSAGE, 0, 0, str);
				break;
			}
			case 1: {
				if (list.get(start) == 0x09) {
					List<Short> tmp = new ArrayList<Short>();
					tmp.addAll(list.subList(start + 4, list.size() - 2));
					short statusword = (short) (list.get(start + 2) & 0x0F);
					switch (statusword) {
					case 4: {
						str = "耳温:" + convertListToHexStringWithSpace(tmp);
						sendMessageToUI(AudioUtils.MSG_MESSAGE, 2, 0, str);
						break;
					}
					case 5: {
						str = "血糖:" + convertListToHexStringWithSpace(tmp);
						sendMessageToUI(AudioUtils.MSG_MESSAGE, 2, 0, str);
						break;
					}
					default:
						break;
					}
					assertResult = true;
				}
				break;
			}
			case 21: {
				if (list.get(start) == 0x03) {
					short statusword = (short) (list.get(start + 2) & 0x0F);
					switch (statusword) {
					case 4: {
						str = "耳温:工作正常";
						// sendMessageToUI(AudioUtils.MSG_MESSAGE, 0, 0, str);
						break;
					}
					case 5: {
						str = "血糖:工作正常";
						// sendMessageToUI(AudioUtils.MSG_MESSAGE, 0, 0, str);
						break;
					}
					default:
						break;
					}
				}
				break;
			}
			case 22: {
				if (list.get(start) == 0x05) {
					short statusword = (short) (list.get(start + 2) & 0x0F);
					short dataH = list.get(start + 4);
					short dataL = list.get(start + 5);
					switch (statusword) {
					case 4: {
						float data = dataH + (float) (dataL * 0.01);
						if (data >= 35 && data <= 40) {
							str = String.valueOf(data);
							sendMessageToUI(AudioUtils.MSG_MESSAGE, 1, 0, str);
						} else if (data < 35) {
							sendMessageToUI(AudioUtils.MSG_MESSAGE, 0, 0,
									"工作正常，测量结果低于35℃");
						} else if (data > 40) {
							sendMessageToUI(AudioUtils.MSG_MESSAGE, 0, 0,
									"工作正常，测量结果高于40℃");
						}
						break;
					}
					case 5: {
						float data = (float) ((float) (dataH * 256 + dataL) * 0.01);
						// str = "血糖:" + String.valueOf(data) + "mmol/L";
						str = String.valueOf(data);
						sendMessageToUI(AudioUtils.MSG_MESSAGE, 1, 0, str);
						break;
					}
					default:
						break;
					}
				}
				assertResult = true;
				break;
			}
			case -1: {
				str = "错误:" + convertListToHexStringWithSpace(list);
				sendMessageToUI(AudioUtils.MSG_MESSAGE, 0, 0, str);
				break;
			}
			case 20:
				sendMessageToUI(AudioUtils.MSG_MESSAGE, 0, 0,
						"工作正常，测量结果高于42.2℃");
				break;
			case 36:
				sendMessageToUI(AudioUtils.MSG_MESSAGE, 0, 0, "工作正常，测量结果低于32℃");
				break;
			case 52:
				sendMessageToUI(AudioUtils.MSG_MESSAGE, 0, 0, "误差大，环境温度高于40℃");
				break;
			case 68:
				sendMessageToUI(AudioUtils.MSG_MESSAGE, 0, 0, "误差大，环境温度低于10℃");
				break;
			case 84:
				sendMessageToUI(AudioUtils.MSG_MESSAGE, 0, 0, "测量无法完成，电池电量低");
				break;
			case 148:
				sendMessageToUI(AudioUtils.MSG_MESSAGE, 0, 0, "传感器故障");
				break;
			case 164:
				sendMessageToUI(AudioUtils.MSG_MESSAGE, 0, 0, "EEPROM故障");
				break;
			case 255: {
				if (list.get(start) == 0x01) {
					str = "回音:" + convertListToHexStringWithSpace(list);
					// sendMessageToUI(AudioUtils.MSG_MESSAGE, 0, 0, str);
				}
			}
			default:
				break;
			}
		}
	}

	private void sendMessageToUI(int what, int arg1, int arg2, String str) {
		if (handler != null) {
			Message msg = handler.obtainMessage();
			msg.what = what;
			msg.arg1 = arg1;
			msg.arg2 = arg2;
			msg.obj = str;
			handler.sendMessage(msg);
		}
	}

	private synchronized int findStartofFrame(List<Short> list) {
		int start = -1;
		if (list != null) {
			int size = list.size();
			int cntofaa = 0;
			for (int i = 0; i < size; i++) {
				if (list.get(i) == 0xAA) {
					cntofaa++;
					continue;
				} else if (cntofaa >= 7) {
					if (list.get(i) == 0xAB) {
						start = i + 1;
						break;
					} else {
						cntofaa = 0;
					}
				} else {
					cntofaa = 0;
				}
			}
		}
		return start;
	}

	public synchronized String resultListToString(
			AudioDecodeResult audioDecodeResult) {
		int type = audioDecodeResult.type;
		List<Short> list = audioDecodeResult.decimalResultList;
		String str = "Type:" + type + "::"
				+ convertListToHexStringWithSpace(list);
		return str;
	}

	private synchronized String convertListToHexStringWithSpace(List<Short> list) {
		String str = "";
		if (list != null) {
			for (int k = 0; k < list.size(); k++) {
				str += Integer.toHexString(
						(list.get(k) & 0x000000FF) | 0xFFFFFF00).substring(6)
						+ " ";
			}
		}
		return str;
	}

}

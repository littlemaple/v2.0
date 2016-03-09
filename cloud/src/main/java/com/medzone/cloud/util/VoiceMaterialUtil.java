package com.medzone.cloud.util;

import java.util.LinkedList;
import java.util.List;

import com.medzone.mcloud.R;

public class VoiceMaterialUtil {

	public static List<Integer> prepareOxygenVoiceFiles(String oxDataStr,
			String heartRateStr) {

		List<Integer> voiceFileList = new LinkedList<Integer>();

		int oxDataSize = oxDataStr.length();
		int heartRateSize = heartRateStr.length();

		voiceFileList.add(R.raw.yours);
		voiceFileList.add(R.raw.ox);
		voiceFileList.add(R.raw.theresultis);
		voiceFileList.add(R.raw.ox_o);
		voiceFileList.add(R.raw.ch_100s);

		switch (oxDataSize) {
		case 1:
			int data = Integer.valueOf(oxDataStr);
			switch (data) {
			case 0:
				voiceFileList.add(R.raw.n_0);
				break;
			case 1:
				voiceFileList.add(R.raw.n_1);
				break;
			case 2:
				voiceFileList.add(R.raw.n_2);
				break;
			case 3:
				voiceFileList.add(R.raw.n_3);
				break;
			case 4:
				voiceFileList.add(R.raw.n_4);
				break;
			case 5:
				voiceFileList.add(R.raw.n_5);
				break;
			case 6:
				voiceFileList.add(R.raw.n_6);
				break;
			case 7:
				voiceFileList.add(R.raw.n_7);
				break;
			case 8:
				voiceFileList.add(R.raw.n_8);
				break;
			case 9:
				voiceFileList.add(R.raw.n_9);
				break;

			}
			break;
		case 2:

			int data1 = Integer.valueOf(oxDataStr.substring(0, 1));
			int data2 = Integer.valueOf(oxDataStr.substring(1));
			switch (data1) {

			case 1:
				voiceFileList.add(R.raw.n_10);
				break;
			case 2:
				voiceFileList.add(R.raw.n_20);
				break;
			case 3:
				voiceFileList.add(R.raw.n_30);
				break;
			case 4:
				voiceFileList.add(R.raw.n_40);
				break;
			case 5:
				voiceFileList.add(R.raw.n_50);
				break;
			case 6:
				voiceFileList.add(R.raw.n_60);
				break;
			case 7:
				voiceFileList.add(R.raw.n_70);
				break;
			case 8:
				voiceFileList.add(R.raw.n_80);
				break;
			case 9:
				voiceFileList.add(R.raw.n_90);
				break;

			}

			switch (data2) {

			case 1:
				voiceFileList.add(R.raw.n_1);
				break;
			case 2:
				voiceFileList.add(R.raw.n_2);
				break;
			case 3:
				voiceFileList.add(R.raw.n_3);
				break;
			case 4:
				voiceFileList.add(R.raw.n_4);
				break;
			case 5:
				voiceFileList.add(R.raw.n_5);
				break;
			case 6:
				voiceFileList.add(R.raw.n_6);
				break;
			case 7:
				voiceFileList.add(R.raw.n_7);
				break;
			case 8:
				voiceFileList.add(R.raw.n_8);
				break;
			case 9:
				voiceFileList.add(R.raw.n_9);
				break;

			}
			break;
		case 3:
			data1 = Integer.valueOf(oxDataStr.substring(0, 1));
			data2 = Integer.valueOf(oxDataStr.substring(1, 2));
			int data3 = Integer.valueOf(oxDataStr.substring(2));

			switch (data1) {
			case 1:
				voiceFileList.add(R.raw.n_100);
				break;

			case 2:
				voiceFileList.add(R.raw.n_200);
				break;

			}
			switch (data2) {
			case 0:
				if (data3 != 0)
					voiceFileList.add(R.raw.n_0);
				break;

			case 1:
				voiceFileList.add(R.raw.n_10);
				break;
			case 2:
				voiceFileList.add(R.raw.n_20);
				break;
			case 3:
				voiceFileList.add(R.raw.n_30);
				break;
			case 4:
				voiceFileList.add(R.raw.n_40);
				break;
			case 5:
				voiceFileList.add(R.raw.n_50);
				break;
			case 6:
				voiceFileList.add(R.raw.n_60);
				break;
			case 7:
				voiceFileList.add(R.raw.n_70);
				break;
			case 8:
				voiceFileList.add(R.raw.n_80);
				break;
			case 9:
				voiceFileList.add(R.raw.n_90);
				break;

			}

			switch (data3) {

			case 1:
				voiceFileList.add(R.raw.n_1);
				break;
			case 2:
				voiceFileList.add(R.raw.n_2);
				break;
			case 3:
				voiceFileList.add(R.raw.n_3);
				break;
			case 4:
				voiceFileList.add(R.raw.n_4);
				break;
			case 5:
				voiceFileList.add(R.raw.n_5);
				break;
			case 6:
				voiceFileList.add(R.raw.n_6);
				break;
			case 7:
				voiceFileList.add(R.raw.n_7);
				break;
			case 8:
				voiceFileList.add(R.raw.n_8);
				break;
			case 9:
				voiceFileList.add(R.raw.n_9);
				break;

			}
			break;

		}

		voiceFileList.add(R.raw.ox_h);

		switch (heartRateSize) {
		case 1:
			int data = Integer.valueOf(heartRateStr);
			switch (data) {
			case 0:
				voiceFileList.add(R.raw.n_0);
				break;
			case 1:
				voiceFileList.add(R.raw.n_1);
				break;
			case 2:
				voiceFileList.add(R.raw.n_2);
				break;
			case 3:
				voiceFileList.add(R.raw.n_3);
				break;
			case 4:
				voiceFileList.add(R.raw.n_4);
				break;
			case 5:
				voiceFileList.add(R.raw.n_5);
				break;
			case 6:
				voiceFileList.add(R.raw.n_6);
				break;
			case 7:
				voiceFileList.add(R.raw.n_7);
				break;
			case 8:
				voiceFileList.add(R.raw.n_8);
				break;
			case 9:
				voiceFileList.add(R.raw.n_9);
				break;

			}
			break;
		case 2:

			int data1 = Integer.valueOf(heartRateStr.substring(0, 1));
			int data2 = Integer.valueOf(heartRateStr.substring(1));
			switch (data1) {

			case 1:
				voiceFileList.add(R.raw.n_10);
				break;
			case 2:
				voiceFileList.add(R.raw.n_20);
				break;
			case 3:
				voiceFileList.add(R.raw.n_30);
				break;
			case 4:
				voiceFileList.add(R.raw.n_40);
				break;
			case 5:
				voiceFileList.add(R.raw.n_50);
				break;
			case 6:
				voiceFileList.add(R.raw.n_60);
				break;
			case 7:
				voiceFileList.add(R.raw.n_70);
				break;
			case 8:
				voiceFileList.add(R.raw.n_80);
				break;
			case 9:
				voiceFileList.add(R.raw.n_90);
				break;

			}

			switch (data2) {

			case 1:
				voiceFileList.add(R.raw.n_1);
				break;
			case 2:
				voiceFileList.add(R.raw.n_2);
				break;
			case 3:
				voiceFileList.add(R.raw.n_3);
				break;
			case 4:
				voiceFileList.add(R.raw.n_4);
				break;
			case 5:
				voiceFileList.add(R.raw.n_5);
				break;
			case 6:
				voiceFileList.add(R.raw.n_6);
				break;
			case 7:
				voiceFileList.add(R.raw.n_7);
				break;
			case 8:
				voiceFileList.add(R.raw.n_8);
				break;
			case 9:
				voiceFileList.add(R.raw.n_9);
				break;

			}
			break;
		case 3:
			data1 = Integer.valueOf(heartRateStr.substring(0, 1));
			data2 = Integer.valueOf(heartRateStr.substring(1, 2));
			int data3 = Integer.valueOf(heartRateStr.substring(2));

			switch (data1) {
			case 1:
				voiceFileList.add(R.raw.n_100);
				break;

			case 2:
				voiceFileList.add(R.raw.n_200);
				break;

			}
			switch (data2) {
			case 0:
				if (data3 != 0)
					voiceFileList.add(R.raw.n_0);
				break;

			case 1:
				voiceFileList.add(R.raw.n_10);
				break;
			case 2:
				voiceFileList.add(R.raw.n_20);
				break;
			case 3:
				voiceFileList.add(R.raw.n_30);
				break;
			case 4:
				voiceFileList.add(R.raw.n_40);
				break;
			case 5:
				voiceFileList.add(R.raw.n_50);
				break;
			case 6:
				voiceFileList.add(R.raw.n_60);
				break;
			case 7:
				voiceFileList.add(R.raw.n_70);
				break;
			case 8:
				voiceFileList.add(R.raw.n_80);
				break;
			case 9:
				voiceFileList.add(R.raw.n_90);
				break;
			}

			switch (data3) {

			case 1:
				voiceFileList.add(R.raw.n_1);
				break;
			case 2:
				voiceFileList.add(R.raw.n_2);
				break;
			case 3:
				voiceFileList.add(R.raw.n_3);
				break;
			case 4:
				voiceFileList.add(R.raw.n_4);
				break;
			case 5:
				voiceFileList.add(R.raw.n_5);
				break;
			case 6:
				voiceFileList.add(R.raw.n_6);
				break;
			case 7:
				voiceFileList.add(R.raw.n_7);
				break;
			case 8:
				voiceFileList.add(R.raw.n_8);
				break;
			case 9:
				voiceFileList.add(R.raw.n_9);
				break;
			}
			break;
		}
		return voiceFileList;
	}

	public static List<Integer> preparePressureVoiceFiles(
			String systolicPressure, String diastolicPressure, boolean isKpa) {

		List<Integer> voiceFileList = new LinkedList<Integer>();

		int digitSys = 0;
		int digitDia = 0;

		if (isKpa) {
			digitSys = systolicPressure.indexOf(".");
			digitDia = diastolicPressure.indexOf(".");
		} else {
			digitSys = systolicPressure.length();
			digitDia = diastolicPressure.length();
		}

		voiceFileList.add(R.raw.yours);
		voiceFileList.add(R.raw.bp);
		voiceFileList.add(R.raw.theresultis);
		voiceFileList.add(R.raw.bp_h);

		switch (digitSys) {
		case 1:
			int data = Integer.valueOf(systolicPressure.substring(0, digitSys));
			switch (data) {
			case 0:
				voiceFileList.add(R.raw.n_0);
				break;
			case 1:
				voiceFileList.add(R.raw.n_1);
				break;
			case 2:
				voiceFileList.add(R.raw.n_2);
				break;
			case 3:
				voiceFileList.add(R.raw.n_3);
				break;
			case 4:
				voiceFileList.add(R.raw.n_4);
				break;
			case 5:
				voiceFileList.add(R.raw.n_5);
				break;
			case 6:
				voiceFileList.add(R.raw.n_6);
				break;
			case 7:
				voiceFileList.add(R.raw.n_7);
				break;
			case 8:
				voiceFileList.add(R.raw.n_8);
				break;
			case 9:
				voiceFileList.add(R.raw.n_9);
				break;

			}
			break;
		case 2:

			int data1 = Integer.valueOf(systolicPressure.substring(0, 1));
			int data2 = Integer
					.valueOf(systolicPressure.substring(1, digitSys));
			switch (data1) {

			case 1:
				voiceFileList.add(R.raw.n_10);
				break;
			case 2:
				voiceFileList.add(R.raw.n_20);
				break;
			case 3:
				voiceFileList.add(R.raw.n_30);
				break;
			case 4:
				voiceFileList.add(R.raw.n_40);
				break;
			case 5:
				voiceFileList.add(R.raw.n_50);
				break;
			case 6:
				voiceFileList.add(R.raw.n_60);
				break;
			case 7:
				voiceFileList.add(R.raw.n_70);
				break;
			case 8:
				voiceFileList.add(R.raw.n_80);
				break;
			case 9:
				voiceFileList.add(R.raw.n_90);
				break;

			}

			switch (data2) {

			case 1:
				voiceFileList.add(R.raw.n_1);
				break;
			case 2:
				voiceFileList.add(R.raw.n_2);
				break;
			case 3:
				voiceFileList.add(R.raw.n_3);
				break;
			case 4:
				voiceFileList.add(R.raw.n_4);
				break;
			case 5:
				voiceFileList.add(R.raw.n_5);
				break;
			case 6:
				voiceFileList.add(R.raw.n_6);
				break;
			case 7:
				voiceFileList.add(R.raw.n_7);
				break;
			case 8:
				voiceFileList.add(R.raw.n_8);
				break;
			case 9:
				voiceFileList.add(R.raw.n_9);
				break;

			}
			break;
		case 3:
			data1 = Integer.valueOf(systolicPressure.substring(0, 1));
			data2 = Integer.valueOf(systolicPressure.substring(1, 2));
			int data3 = Integer
					.valueOf(systolicPressure.substring(2, digitSys));

			switch (data1) {
			case 1:
				voiceFileList.add(R.raw.n_100);
				break;

			case 2:
				voiceFileList.add(R.raw.n_200);
				break;

			}
			switch (data2) {
			case 0:
				if (data3 != 0)
					voiceFileList.add(R.raw.n_0);
				break;

			case 1:
				voiceFileList.add(R.raw.n_10);
				break;
			case 2:
				voiceFileList.add(R.raw.n_20);
				break;
			case 3:
				voiceFileList.add(R.raw.n_30);
				break;
			case 4:
				voiceFileList.add(R.raw.n_40);
				break;
			case 5:
				voiceFileList.add(R.raw.n_50);
				break;
			case 6:
				voiceFileList.add(R.raw.n_60);
				break;
			case 7:
				voiceFileList.add(R.raw.n_70);
				break;
			case 8:
				voiceFileList.add(R.raw.n_80);
				break;
			case 9:
				voiceFileList.add(R.raw.n_90);
				break;

			}

			switch (data3) {

			case 1:
				voiceFileList.add(R.raw.n_1);
				break;
			case 2:
				voiceFileList.add(R.raw.n_2);
				break;
			case 3:
				voiceFileList.add(R.raw.n_3);
				break;
			case 4:
				voiceFileList.add(R.raw.n_4);
				break;
			case 5:
				voiceFileList.add(R.raw.n_5);
				break;
			case 6:
				voiceFileList.add(R.raw.n_6);
				break;
			case 7:
				voiceFileList.add(R.raw.n_7);
				break;
			case 8:
				voiceFileList.add(R.raw.n_8);
				break;
			case 9:
				voiceFileList.add(R.raw.n_9);
				break;

			}
			break;

		}

		// 添加单位
		if (!isKpa) {
			voiceFileList.add(R.raw.mmhg);
		} else {
			voiceFileList.add(R.raw.point);
			int data = Integer
					.valueOf(systolicPressure.substring(digitSys + 1));
			switch (data) {
			case 0:
				voiceFileList.add(R.raw.n_0);
				break;
			case 1:
				voiceFileList.add(R.raw.n_1);
				break;
			case 2:
				voiceFileList.add(R.raw.n_2);
				break;
			case 3:
				voiceFileList.add(R.raw.n_3);
				break;
			case 4:
				voiceFileList.add(R.raw.n_4);
				break;
			case 5:
				voiceFileList.add(R.raw.n_5);
				break;
			case 6:
				voiceFileList.add(R.raw.n_6);
				break;
			case 7:
				voiceFileList.add(R.raw.n_7);
				break;
			case 8:
				voiceFileList.add(R.raw.n_8);
				break;
			case 9:
				voiceFileList.add(R.raw.n_9);
				break;

			}
			voiceFileList.add(R.raw.kpa);
		}

		// 收缩压
		voiceFileList.add(R.raw.bp_l);

		switch (digitDia) {
		case 1:
			int data = Integer
					.valueOf(diastolicPressure.substring(0, digitDia));
			switch (data) {
			case 0:
				voiceFileList.add(R.raw.n_0);
				break;
			case 1:
				voiceFileList.add(R.raw.n_1);
				break;
			case 2:
				voiceFileList.add(R.raw.n_2);
				break;
			case 3:
				voiceFileList.add(R.raw.n_3);
				break;
			case 4:
				voiceFileList.add(R.raw.n_4);
				break;
			case 5:
				voiceFileList.add(R.raw.n_5);
				break;
			case 6:
				voiceFileList.add(R.raw.n_6);
				break;
			case 7:
				voiceFileList.add(R.raw.n_7);
				break;
			case 8:
				voiceFileList.add(R.raw.n_8);
				break;
			case 9:
				voiceFileList.add(R.raw.n_9);
				break;

			}
			break;
		case 2:

			int data1 = Integer.valueOf(diastolicPressure.substring(0, 1));
			int data2 = Integer.valueOf(diastolicPressure
					.substring(1, digitDia));
			switch (data1) {

			case 1:
				voiceFileList.add(R.raw.n_10);
				break;
			case 2:
				voiceFileList.add(R.raw.n_20);
				break;
			case 3:
				voiceFileList.add(R.raw.n_30);
				break;
			case 4:
				voiceFileList.add(R.raw.n_40);
				break;
			case 5:
				voiceFileList.add(R.raw.n_50);
				break;
			case 6:
				voiceFileList.add(R.raw.n_60);
				break;
			case 7:
				voiceFileList.add(R.raw.n_70);
				break;
			case 8:
				voiceFileList.add(R.raw.n_80);
				break;
			case 9:
				voiceFileList.add(R.raw.n_90);
				break;

			}

			switch (data2) {

			case 1:
				voiceFileList.add(R.raw.n_1);
				break;
			case 2:
				voiceFileList.add(R.raw.n_2);
				break;
			case 3:
				voiceFileList.add(R.raw.n_3);
				break;
			case 4:
				voiceFileList.add(R.raw.n_4);
				break;
			case 5:
				voiceFileList.add(R.raw.n_5);
				break;
			case 6:
				voiceFileList.add(R.raw.n_6);
				break;
			case 7:
				voiceFileList.add(R.raw.n_7);
				break;
			case 8:
				voiceFileList.add(R.raw.n_8);
				break;
			case 9:
				voiceFileList.add(R.raw.n_9);
				break;

			}
			break;
		case 3:
			data1 = Integer.valueOf(diastolicPressure.substring(0, 1));
			data2 = Integer.valueOf(diastolicPressure.substring(1, 2));
			int data3 = Integer.valueOf(diastolicPressure
					.substring(2, digitDia));

			switch (data1) {
			case 1:
				voiceFileList.add(R.raw.n_100);
				break;

			case 2:
				voiceFileList.add(R.raw.n_200);
				break;

			}
			switch (data2) {
			case 0:
				if (data3 != 0)
					voiceFileList.add(R.raw.n_0);
				break;

			case 1:
				voiceFileList.add(R.raw.n_10);
				break;
			case 2:
				voiceFileList.add(R.raw.n_20);
				break;
			case 3:
				voiceFileList.add(R.raw.n_30);
				break;
			case 4:
				voiceFileList.add(R.raw.n_40);
				break;
			case 5:
				voiceFileList.add(R.raw.n_50);
				break;
			case 6:
				voiceFileList.add(R.raw.n_60);
				break;
			case 7:
				voiceFileList.add(R.raw.n_70);
				break;
			case 8:
				voiceFileList.add(R.raw.n_80);
				break;
			case 9:
				voiceFileList.add(R.raw.n_90);
				break;

			}

			switch (data3) {

			case 1:
				voiceFileList.add(R.raw.n_1);
				break;
			case 2:
				voiceFileList.add(R.raw.n_2);
				break;
			case 3:
				voiceFileList.add(R.raw.n_3);
				break;
			case 4:
				voiceFileList.add(R.raw.n_4);
				break;
			case 5:
				voiceFileList.add(R.raw.n_5);
				break;
			case 6:
				voiceFileList.add(R.raw.n_6);
				break;
			case 7:
				voiceFileList.add(R.raw.n_7);
				break;
			case 8:
				voiceFileList.add(R.raw.n_8);
				break;
			case 9:
				voiceFileList.add(R.raw.n_9);
				break;

			}
			break;

		}

		// 添加单位
		if (!isKpa) {
			voiceFileList.add(R.raw.mmhg);
		} else {
			voiceFileList.add(R.raw.point);
			int data = Integer.valueOf(diastolicPressure
					.substring(digitDia + 1));
			switch (data) {
			case 0:
				voiceFileList.add(R.raw.n_0);
				break;
			case 1:
				voiceFileList.add(R.raw.n_1);
				break;
			case 2:
				voiceFileList.add(R.raw.n_2);
				break;
			case 3:
				voiceFileList.add(R.raw.n_3);
				break;
			case 4:
				voiceFileList.add(R.raw.n_4);
				break;
			case 5:
				voiceFileList.add(R.raw.n_5);
				break;
			case 6:
				voiceFileList.add(R.raw.n_6);
				break;
			case 7:
				voiceFileList.add(R.raw.n_7);
				break;
			case 8:
				voiceFileList.add(R.raw.n_8);
				break;
			case 9:
				voiceFileList.add(R.raw.n_9);
				break;
			}
			voiceFileList.add(R.raw.kpa);
		}
		return voiceFileList;
	}

	public static List<Integer> prepareEarTemperatureVoiceFiles(String et) {

		List<Integer> voiceFileList = new LinkedList<Integer>();

		int etSize = et.indexOf(".");
		voiceFileList.add(R.raw.yours);
		voiceFileList.add(R.raw.bbt);
		voiceFileList.add(R.raw.theresultis);

		switch (etSize) {
		case 1:
			int data = Integer.valueOf(et.substring(0, etSize));
			switch (data) {
			case 0:
				voiceFileList.add(R.raw.n_0);
				break;
			case 1:
				voiceFileList.add(R.raw.n_1);
				break;
			case 2:
				voiceFileList.add(R.raw.n_2);
				break;
			case 3:
				voiceFileList.add(R.raw.n_3);
				break;
			case 4:
				voiceFileList.add(R.raw.n_4);
				break;
			case 5:
				voiceFileList.add(R.raw.n_5);
				break;
			case 6:
				voiceFileList.add(R.raw.n_6);
				break;
			case 7:
				voiceFileList.add(R.raw.n_7);
				break;
			case 8:
				voiceFileList.add(R.raw.n_8);
				break;
			case 9:
				voiceFileList.add(R.raw.n_9);
				break;

			}
			break;
		case 2:

			int data1 = Integer.valueOf(et.substring(0, 1));
			int data2 = Integer.valueOf(et.substring(1, etSize));
			switch (data1) {

			case 1:
				voiceFileList.add(R.raw.n_10);
				break;
			case 2:
				voiceFileList.add(R.raw.n_20);
				break;
			case 3:
				voiceFileList.add(R.raw.n_30);
				break;
			case 4:
				voiceFileList.add(R.raw.n_40);
				break;
			case 5:
				voiceFileList.add(R.raw.n_50);
				break;
			case 6:
				voiceFileList.add(R.raw.n_60);
				break;
			case 7:
				voiceFileList.add(R.raw.n_70);
				break;
			case 8:
				voiceFileList.add(R.raw.n_80);
				break;
			case 9:
				voiceFileList.add(R.raw.n_90);
				break;

			}

			switch (data2) {

			case 1:
				voiceFileList.add(R.raw.n_1);
				break;
			case 2:
				voiceFileList.add(R.raw.n_2);
				break;
			case 3:
				voiceFileList.add(R.raw.n_3);
				break;
			case 4:
				voiceFileList.add(R.raw.n_4);
				break;
			case 5:
				voiceFileList.add(R.raw.n_5);
				break;
			case 6:
				voiceFileList.add(R.raw.n_6);
				break;
			case 7:
				voiceFileList.add(R.raw.n_7);
				break;
			case 8:
				voiceFileList.add(R.raw.n_8);
				break;
			case 9:
				voiceFileList.add(R.raw.n_9);
				break;

			}
			break;
		}

		// 添加单位
		voiceFileList.add(R.raw.point);
		int data = Integer.valueOf(et.substring(etSize + 1, etSize + 2));
		switch (data) {
		case 0:
			voiceFileList.add(R.raw.n_0);
			break;
		case 1:
			voiceFileList.add(R.raw.n_1);
			break;
		case 2:
			voiceFileList.add(R.raw.n_2);
			break;
		case 3:
			voiceFileList.add(R.raw.n_3);
			break;
		case 4:
			voiceFileList.add(R.raw.n_4);
			break;
		case 5:
			voiceFileList.add(R.raw.n_5);
			break;
		case 6:
			voiceFileList.add(R.raw.n_6);
			break;
		case 7:
			voiceFileList.add(R.raw.n_7);
			break;
		case 8:
			voiceFileList.add(R.raw.n_8);
			break;
		case 9:
			voiceFileList.add(R.raw.n_9);
			break;
		}
		voiceFileList.add(R.raw.unit_t);

		return voiceFileList;
	}

}

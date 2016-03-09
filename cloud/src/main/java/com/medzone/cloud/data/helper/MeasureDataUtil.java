/**
 * 
 */
package com.medzone.cloud.data.helper;

import android.widget.ImageView;

import com.medzone.framework.data.bean.imp.BloodOxygen;
import com.medzone.framework.data.bean.imp.BloodPressure;
import com.medzone.framework.errorcode.ProxyCode.LocalError;
import com.medzone.mcloud.R.drawable;

/**
 * @author ChenJunQi.
 * 
 */
@Deprecated
public class MeasureDataUtil {

	public static int checkBloodPressureMeasureResult(float hign, float low,
			int rate) {
		if (hign < 0) {
			return LocalError.CODE_SYSTOLIC_PRESSURE_EMPTY;
		}

		if (low < 0) {
			return LocalError.CODE_DIASTOLIC_PRESSURE_EMPTY;
		}

		if (rate < 0) {
			return LocalError.CODE_HEART_RATE_EMPTY;
		}

		if (hign < low) {
			return LocalError.CODE_SYSTOLIC_LESS_THAN_DIASTOLIC;
		}
		return LocalError.CODE_SUCCESS;
	}

	/**
	 * 填充血压的等级状态图
	 * 
	 * @author ChenJunQi. 2014年9月11日
	 * 
	 * @param imageView
	 * @param state
	 */
	public static void BloodPressureFlag(ImageView imageView, int state) {
		switch (state) {
		case BloodPressure.PRESSURE_STATE_LOW:
			imageView.setImageResource(drawable.testresultsview_testresult_low);
			break;
		case BloodPressure.PRESSURE_STATE_IDEAL:
			imageView
					.setImageResource(drawable.testresultsview_testresult_ideal);
			break;
		case BloodPressure.PRESSURE_STATE_NORMAL:
			imageView
					.setImageResource(drawable.testresultsview_testresult_normal);
			break;
		case BloodPressure.PRESSURE_STATE_MILD:
			imageView
					.setImageResource(drawable.testresultsview_testresult_mild);
			break;
		case BloodPressure.PRESSURE_STATE_SERIOUS:
			imageView
					.setImageResource(drawable.testresultsview_testresult_serious);
			break;
		case BloodPressure.PRESSURE_STATE_NORMAL_HIGH:
			imageView
					.setImageResource(drawable.testresultsview_testresult_normalhigh);
			break;
		case BloodPressure.PRESSURE_STATE_MODERATE:
			imageView
					.setImageResource(drawable.testresultsview_testresult_moderate);
			break;
		default:
			imageView.setImageResource(drawable.testresultsview_testresult);
			break;
		}
	}

	/**
	 * 填充血氧的等级状态图
	 * 
	 * @author ChenJunQi. 2014年9月11日
	 * 
	 * @param imageView
	 * @param state
	 */
	public static void BloodOxygenFlag(ImageView imageView, int state) {
		switch (state) {
		case BloodOxygen.OXYGEN_STATE_LOW:
			imageView
					.setImageResource(drawable.group_chat_testresult_hypoxemia);
			break;
		case BloodOxygen.OXYGEN_STATE_IDEAL:
			imageView.setImageResource(drawable.group_chat_testresult_normal);
			break;
		case BloodOxygen.OXYGEN_STATE_MISSING:
			imageView
					.setImageResource(drawable.group_chat_testresult_lossofoxygensaturation);
			break;
		default:
			imageView.setImageResource(drawable.group_chat_testresult);
			break;
		}
	}

	public static String StringConcatenation(int initData) {
		if (initData < 10) {
			return "0" + initData;
		} else {
			return "" + initData;
		}
	}

	public static String StringConcatenation(String initData) {
		if (initData != null) {
			if (initData.length() <= 1) {
				return "0" + initData;

			} else {
				return initData;
			}
		} else {
			return "error";
		}
	}

	public static String StringConcatenationThree(String initData) {
		if (initData != null) {
			int value = Integer.valueOf(initData);
			if (value >= 0 && value <= 9) {
				return "00" + value;
			} else if (value > 9 && value < 100) {
				return "0" + value;
			} else {
				return "" + value;
			}
		} else {
			return "error";
		}
	}

}

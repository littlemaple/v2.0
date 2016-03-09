package com.medzone.framework.util;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.medzone.framework.R.id;
import com.medzone.framework.R.layout;
import com.medzone.framework.R.string;

@SuppressLint("NewApi")
public final class NumberPickerUtil {

	private static Context mContext;
	private static NumberPicker picker;
	private static com.michaelnovakjr.numberpicker.NumberPicker backPortPicker;
	private static TextView rightTv;

	public static void loadActivityContext(Context context) {
		mContext = context;
	}

	static public void showNumberPicker(int curValue, int minValue,
			int maxValue, String title, String rightUnit,
			final onDialogChooseListener listener) {
		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
		LayoutInflater inflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		View rootView = null;

		// 兼容11
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			rootView = inflater.inflate(layout.dialog_number_picker, null);
			picker = (NumberPicker) rootView.findViewById(id.numberpicker);
			picker.setMinValue(minValue);
			rightTv = (TextView) rootView.findViewById(id.tv_right);
			picker.setMaxValue(maxValue);
			picker.setFocusable(true);
			picker.setFocusableInTouchMode(true);
			picker.setValue(curValue);
		} else {
			// BackPort
			rootView = inflater.inflate(layout.dialog_number_picker_backport,
					null);
			backPortPicker = (com.michaelnovakjr.numberpicker.NumberPicker) rootView
					.findViewById(id.backport_numberpicker);
			rightTv = (TextView) rootView.findViewById(id.tv_right);
			backPortPicker.setRange(minValue, maxValue);
			backPortPicker.setFocusable(true);
			backPortPicker.setFocusableInTouchMode(true);
			backPortPicker.setCurrent(curValue);

		}
		rightTv.setText(rightUnit);
		builder.setView(rootView)
				.setPositiveButton(string.action_confirm,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								listener.onConfirm(getPickerValue());
							}
						})
				.setNegativeButton(string.action_cancel,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								listener.onCancel();
							}
						}).setTitle(title);
//		builder.show();
		Dialog dialog = builder.create();
		dialog.setCanceledOnTouchOutside(false);
		dialog.show();
	}

	/**
	 * 主要用于显示kpa
	 * 
	 * @param curValue
	 *            注意，需要传入值为显示的值*multiple的整数
	 * @param minValue
	 * @param maxValue
	 * @param title
	 * @param rightUnit
	 * @param multiple
	 * @param listener
	 */
	static public void showDoubleNumberPicker(int curValue, float minValue,
			float maxValue, String title, String rightUnit,
			final float multiple, final onDialogChooseListener listener) {

		// 将数值预处理成小数字符串

		// final float interval = maxValue - minValue;
		final int interval = (int) (maxValue * 10 - minValue * 10);
		final int mCurValue = curValue;// (int) (curValue * multiple);
		final int mMinValue = (int) (minValue * multiple);
		final int mMaxValue = (int) (maxValue * multiple);

		final String[] displayedValues = new String[interval + 1];
		for (int i = 0; i < displayedValues.length; i++) {
			displayedValues[i] = String.valueOf((float) (mMinValue + i)
					/ multiple);
		}
		// 准备弹窗组件
		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
		LayoutInflater inflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		View rootView = null;

		// 兼容11
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			rootView = inflater.inflate(layout.dialog_number_picker, null);
			rightTv = (TextView) rootView.findViewById(id.tv_right);
			picker = (NumberPicker) rootView.findViewById(id.numberpicker);
			picker.setDisplayedValues(displayedValues);
			picker.setMinValue(mMinValue);
			picker.setMaxValue(mMaxValue);
			picker.setValue(mCurValue);
			picker.setFocusable(true);
			picker.setFocusableInTouchMode(true);
		} else {
			// BackPort
			rootView = inflater.inflate(layout.dialog_number_picker_backport,
					null);
			rightTv = (TextView) rootView.findViewById(id.tv_right);
			backPortPicker = (com.michaelnovakjr.numberpicker.NumberPicker) rootView
					.findViewById(id.backport_numberpicker);
			backPortPicker.setRange(mMinValue, mMaxValue, displayedValues);
			backPortPicker.setCurrent(mCurValue);
			backPortPicker.setFocusable(true);
			backPortPicker.setFocusableInTouchMode(true);
		}
		rightTv.setText(rightUnit);
		builder.setView(rootView)
				.setPositiveButton(string.action_confirm,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								listener.onConfirm(displayedValues[(picker
										.getValue() - mMinValue)]);
							}
						})
				.setNegativeButton(string.action_cancel,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								listener.onCancel();
							}
						}).setTitle(title);
		builder.show();
	}

	static public void showNumberPicker(int curValue, int minValue,
			int maxValue, String title, final onDialogChooseListener listener) {
		showNumberPicker(curValue, minValue, maxValue, title, "", listener);
	}

	/**
	 * 
	 * @param displayedValues
	 * @param curValue
	 * @param minValue
	 *            posIndex ,always it doesn't mean a really value.
	 * @param maxValue
	 *            posIndex ,always it doesn't mean a really value.
	 * @param title
	 * @param onConfirmListener
	 * @param onCancelListener
	 */
	static public void showNumberPicker(final String[] displayedValues,
			int curValue, int minValue, int maxValue, String title,
			String rightUnit, final onDialogChooseListener listener) {

		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
		LayoutInflater inflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		View rootView = null;

		// 兼容11
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			rootView = inflater.inflate(layout.dialog_number_picker, null);

			picker = (NumberPicker) rootView.findViewById(id.numberpicker);
			rightTv = (TextView) rootView.findViewById(id.tv_right);
			picker.setDisplayedValues(displayedValues);
			picker.setMinValue(minValue);
			picker.setMaxValue(maxValue);
			picker.setFocusable(true);
			picker.setFocusableInTouchMode(true);
			picker.setValue(curValue);
		} else {
			// BackPort
			rootView = inflater.inflate(layout.dialog_number_picker_backport,
					null);
			backPortPicker = (com.michaelnovakjr.numberpicker.NumberPicker) rootView
					.findViewById(id.backport_numberpicker);
			rightTv = (TextView) rootView.findViewById(id.tv_right);
			backPortPicker.setRange(minValue, maxValue);
			backPortPicker.setFocusable(true);
			backPortPicker.setFocusableInTouchMode(true);
			backPortPicker.setCurrent(curValue);

		}
		rightTv.setText(rightUnit);
		builder.setView(rootView)
				.setPositiveButton(string.action_confirm,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								listener.onConfirm(getPickerValue());
							}
						})
				.setNegativeButton(string.action_cancel,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								listener.onCancel();
							}
						}).setTitle(title);
		builder.show();

	}

	@Deprecated
	private static Object getPickerValue() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			return picker.getValue();
		} else {
			return backPortPicker.getCurrent();
		}
	}

	public interface onDialogChooseListener {
		void onConfirm(Object value);

		void onCancel();
	}
}

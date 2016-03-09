package com.medzone.framework.task.progress;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.widget.TextView;

import com.medzone.framework.R;
import com.medzone.framework.task.Progress;

/**
 * @author ChenJunQi
 * 
 */
public class CustomDialogProgress implements Progress {

	private Context context = null;
	private CustomProgressDialog customDialog = null;
	private CharSequence message = "";

	// private long startTimeMillis, endTimeMillis;

	public CustomDialogProgress(Context context) {
		this.context = context;
	}

	public CustomDialogProgress(Context context, CharSequence message) {
		this.context = context;
		this.message = message;
	}

	/*
	 * (non-Javadoc)
	 */

	public boolean isAvailable() {
		return customDialog != null;
	}

	/*
	 * (non-Javadoc)
	 */

	public void startProgress() {
		if (customDialog == null) {
			customDialog = CustomProgressDialog.createDialog(context);
			customDialog.setMessage(message);
		}
		customDialog.show();
		// startTimeMillis = System.currentTimeMillis();
	}

	public void finishProgress() {
		if (isAvailable()) {
			// endTimeMillis = System.currentTimeMillis();
			// long interval = endTimeMillis - startTimeMillis;
			// if (interval < 1000) {
			// try {
			// Thread.sleep(1000 - interval);
			// } catch (InterruptedException e) {
			// e.printStackTrace();
			// }
			// }
			customDialog.dismiss();
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ishow.beycare.task.IProgress#updateMessage(java.lang.String)
	 */

	public void updateProgressMessage(CharSequence message) {
		if (isAvailable()) {
			customDialog.setMessage(message);
		}

	}

	public static class CustomProgressDialog extends Dialog {
		@SuppressWarnings("unused")
		private Context context = null;
		private static CustomProgressDialog customProgressDialog = null;

		public CustomProgressDialog(Context context) {
			super(context);
			this.context = context;
		}

		public CustomProgressDialog(Context context, int theme) {
			super(context, theme);
		}

		public static CustomProgressDialog createDialog(Context context) {
			customProgressDialog = new CustomProgressDialog(context,
					R.style.CustomProgressDialog);
			customProgressDialog
					.setContentView(R.layout.progress_dialog_loading);
			customProgressDialog.getWindow().getAttributes().gravity = Gravity.CENTER;

			return customProgressDialog;
		}

		public void onWindowFocusChanged(boolean hasFocus) {

			if (customProgressDialog == null) {
				return;
			}

		}

		public CustomProgressDialog setTitile(String strTitle) {
			return customProgressDialog;
		}

		public void setMessage(CharSequence message) {
			TextView tvMsg = (TextView) customProgressDialog
					.findViewById(R.id.message);

			if (tvMsg != null) {
				tvMsg.setText(message);
			}
		}
	}

	@Override
	public void updateProgress(Integer value) {
		// TODO Auto-generated method stub
	}

	@Override
	public void setIndeterminate(boolean indeterminate) {
		// TODO Auto-generated method stub
	}

}

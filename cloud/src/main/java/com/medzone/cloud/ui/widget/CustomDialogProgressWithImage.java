/**
 * 
 */
package com.medzone.cloud.ui.widget;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.medzone.framework.task.Progress;
import com.medzone.mcloud.R;

/**
 * @author ChenJunQi
 * 
 */
public class CustomDialogProgressWithImage implements Progress {

	private Context context = null;
	private CustomProgressDialog customDialog = null;
	private CharSequence message = "";
	private Drawable leftIcon;

	public CustomDialogProgressWithImage(Context context) {
		this.context = context;
	}

	public CustomDialogProgressWithImage(Context context, CharSequence message) {
		this.context = context;
		this.message = message;
	}

	public CustomDialogProgressWithImage(Context context, Drawable leftIcon) {
		this.context = context;
		this.leftIcon = leftIcon;
	}

	public CustomDialogProgressWithImage(Context context, CharSequence message,
			Drawable leftIcon) {
		this.context = context;
		this.message = message;
		this.leftIcon = leftIcon;
	}

	/*
	 * (non-Javadoc)
	 */

	public boolean isAvailable() {
		return customDialog != null;
	}

	@Override
	public void startProgress() {
		if (customDialog == null) {
			customDialog = CustomProgressDialog.createDialog(context);
			customDialog.setMessage(message);
			customDialog.setImage(leftIcon);
		}
		customDialog.show();
	}

	@Override
	public void finishProgress() {
		if (isAvailable()) {
			if (customDialog != null) {
				customDialog.dismiss();
			}
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
		private Context context = null;
		private static CustomProgressDialog customProgressDialog = null;

		public CustomProgressDialog(Context context) {
			super(context);
			this.context = context;
		}

		public CustomProgressDialog(Context context, int theme) {
			super(context, theme);
			this.context = context;
		}

		public static CustomProgressDialog createDialog(Context context) {
			customProgressDialog = new CustomProgressDialog(context,
					R.style.CustomProgressDialog);
			customProgressDialog.setContentView(R.layout.progress_tip_dialog);
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
					.findViewById(R.id.tip);

			if (tvMsg != null) {
				if (TextUtils.isEmpty(message)) {
					tvMsg.setVisibility(View.GONE);
				} else {
					tvMsg.setText(message);
					tvMsg.setVisibility(View.VISIBLE);
				}
			}
		}

		public void setImage(Drawable icon) {
			ImageView ivIcon = (ImageView) customProgressDialog
					.findViewById(R.id.leftDrawable);

			if (ivIcon != null) {
				ivIcon.setImageDrawable(icon);
				Animation anim = AnimationUtils.loadAnimation(context,
						R.anim.rotate_loading);
				ivIcon.startAnimation(anim);
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

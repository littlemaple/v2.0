/**
 * 
 */
package com.medzone.framework.task.progress;

import android.app.ProgressDialog;
import android.content.Context;

import com.medzone.framework.task.Progress;

/**
 * @author ChenJunQi
 * 
 */
public class SimpleDialogProgress implements Progress {

	private Context context = null;
	private ProgressDialog progressDialog = null;
	private CharSequence title = "";
	private CharSequence message = "";

	public SimpleDialogProgress(Context context) {
		this.context = context;
	}

	public SimpleDialogProgress(Context context, CharSequence title) {
		this.context = context;
		this.title = title;
	}

	public SimpleDialogProgress(Context context, CharSequence title,
			CharSequence message) {
		this.context = context;
		this.title = title;
		this.message = message;
	}

	public boolean isAvailable() {
		return progressDialog != null;
	}

	public void startProgress() {
		progressDialog = ProgressDialog.show(context, title, message, true,
				false);

	}

	public void finishProgress() {
		if (isAvailable()) {
			progressDialog.dismiss();
		}

	}

	public void updateProgressMessage(CharSequence message) {
		if (isAvailable()) {
			progressDialog.setMessage(message);
		}

	}

	public void updateProgress(Integer value) {
		if (isAvailable()) {
			progressDialog.setProgress(value);
		}

	}

	public void setIndeterminate(boolean indeterminate) {
		if (isAvailable()) {
			progressDialog.setIndeterminate(indeterminate);
		}

	}

}

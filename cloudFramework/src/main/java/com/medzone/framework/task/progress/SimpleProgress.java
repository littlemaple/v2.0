/**
 * 
 */
package com.medzone.framework.task.progress;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.medzone.framework.task.Progress;

/**
 * @author ChenJunQi
 * 
 */
public class SimpleProgress implements Progress {

	private ProgressBar progress = null;
	private LinearLayout layout = null;

	public SimpleProgress(LinearLayout layout, ProgressBar progress,
			boolean indeterminate) {
		this.progress = progress;
		this.layout = layout;
		if (isAvailable()) {
			progress.setIndeterminate(indeterminate);
		}
	}

	public boolean isAvailable() {
		return progress != null && layout != null;
	}

	public void startProgress() {
		if (isAvailable()) {
			layout.setVisibility(View.VISIBLE);
		}
	}

	public void finishProgress() {
		if (isAvailable()) {
			layout.setVisibility(View.GONE);
		}

	}

	public void updateProgressMessage(CharSequence message) {
		if (isAvailable()) {
			;
		}

	}

	public void updateProgress(Integer value) {
		if (isAvailable()) {
			progress.setProgress(value);
		}

	}

	public void setIndeterminate(boolean indeterminate) {
		if (isAvailable()) {
			progress.setIndeterminate(indeterminate);
		}

	}

}

/**
 * 
 */
package com.medzone.framework.task.progress;

import com.medzone.framework.task.Progress;
import com.medzone.framework.view.refresh.RefreshableView;

/**
 * @author ChenJunQi.
 * 
 */
public class RefreshableViewProgress implements Progress {
	private RefreshableView refreshableView;

	public RefreshableViewProgress(RefreshableView refreshableView) {
		this.refreshableView = refreshableView;
	}

	@Override
	public boolean isAvailable() {
		return true;
	}

	@Override
	public void startProgress() {
		refreshableView.setRefreshState(true);
	}

	@Override
	public void finishProgress() {
		refreshableView.setRefreshState(false);
	}

	@Override
	public void updateProgressMessage(CharSequence message) {

	}

	@Override
	public void updateProgress(Integer value) {
	}

	@Override
	public void setIndeterminate(boolean indeterminate) {
	}

}
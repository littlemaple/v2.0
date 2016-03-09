/**
 * 
 */
package com.medzone.framework.task;

/**
 * @author ChenJunQi
 * 
 */
public interface Progress {

	public boolean isAvailable();

	public void startProgress();

	public void finishProgress();

	public void updateProgressMessage(CharSequence message);

	public void updateProgress(Integer value);

	public void setIndeterminate(boolean indeterminate);
}

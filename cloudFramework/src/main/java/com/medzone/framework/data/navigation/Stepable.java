package com.medzone.framework.data.navigation;

public interface Stepable<T> extends Comparable<T> {

	/**
	 * Get Newer Page
	 * 
	 * @return pageObject.
	 */
	public abstract T stepUp();

	/**
	 * Get Older Page
	 * 
	 * @return pageObject.
	 */
	public abstract T stepDown();
}

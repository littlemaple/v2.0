package com.medzone.framework.data.navigation;

import java.io.Serializable;

/**
 * Controlls pagination
 * 
 */
public class Paging<T extends Stepable<T>> implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1966439621945140369L;
	// TODO page size must big as 1 :必须大于1，否则将与Divider冲突，导致信息不正确
	protected static final int DEFAULT_PAGE_SIZE = 8;

	private int page = 0;
	private int pageSize = DEFAULT_PAGE_SIZE;
	private T min = null;
	private T max = null;

	public Paging() {
		super();
	}

	public Paging(T min, T max) {
		super();
		this.min = min;
		this.max = max;
	}

	public Paging<T> getDownPaging() {
		T nextMin = null;
		T nextMax = min != null ? min.stepDown() : null;

		return new Paging<T>(page, pageSize, nextMin, nextMax);
	}

	public Paging(int page, int pageSize, T min, T max) {
		super();
		this.page = page;
		this.pageSize = pageSize;
		this.min = min;
		this.max = max;
	}

	public T getMax() {
		return max;
	}

	public T getMin() {
		return min;
	}

	public int getPage() {
		return page;
	}

	public int getPageSize() {
		return pageSize;
	}

	public Paging<T> getUpPaging() {
		T nextMin = max != null ? max.stepUp() : null;
		T nextMax = null;

		return new Paging<T>(page, pageSize, nextMin, nextMax);
	}

	public void setMax(T max) {
		this.max = max;
	}

	public void setMin(T min) {
		this.min = min;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public void reset() {
		page = 0;
		min = null;
		max = null;
		pageSize = DEFAULT_PAGE_SIZE;
	}

}

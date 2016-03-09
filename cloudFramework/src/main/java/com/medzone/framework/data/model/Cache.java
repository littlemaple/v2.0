package com.medzone.framework.data.model;

public interface Cache<T> {
	public abstract void clear();

	public abstract void flush();

	public abstract void asyncFlush();

	public abstract int delete(T item);

	public abstract void asyncDelete(T item);
}

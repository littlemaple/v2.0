package com.medzone.framework.data.model;

import java.util.List;

public interface ListCache<T> extends Cache<T> {

	public abstract void add(int location, T item);

	public abstract void add(T item);

	public abstract void addAll(int location, List<T> itemList);

	public abstract void addAll(List<T> itemList);

	public abstract T get(int location);

	public abstract int indexOf(T item);

	public abstract void remove(int location);

	public abstract void remove(T item);

	public abstract int size();

	public abstract List<T> read();
}

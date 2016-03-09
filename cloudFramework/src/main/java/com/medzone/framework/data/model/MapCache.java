package com.medzone.framework.data.model;

import java.util.List;

public interface MapCache<K, V> extends Cache<V> {

	public abstract boolean containsKey(K key);

	public abstract V get(K key);

	public abstract void put(K key, V value);

	public abstract void remove(K key);

	public abstract List<V> read();

	public abstract boolean read(K key);
}

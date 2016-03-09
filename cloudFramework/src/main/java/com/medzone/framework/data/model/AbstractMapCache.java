package com.medzone.framework.data.model;

import android.os.AsyncTask;
import android.support.v4.util.LruCache;

/**
 * 
 * 
 * Always be used in the nonlinear scene.
 * 
 * @author ChenJunQi.
 * 
 */
public abstract class AbstractMapCache<K, V> implements MapCache<K, V> {

	public static final int DEFAULT_CACHE_SIZE = 8 * 3;
	/**
	 * Maintaining a collection of the results of <b>T</b>
	 */
	protected LruCache<K, V> cache;
	private int cacheSize = DEFAULT_CACHE_SIZE;

	/**
	 * use {@link #DEFAULT_CACHE_SIZE} as cache size.
	 */
	public AbstractMapCache() {
		super();
		cache = new LruCache<K, V>(cacheSize);
	}

	public AbstractMapCache(int cacheSize) {
		super();
		this.cacheSize = cacheSize;
		cache = new LruCache<K, V>(this.cacheSize);
	}

	/**
	 * @return the cacheSize
	 */
	public int getCacheSize() {
		return cacheSize;
	}

	@Override
	public void asyncFlush() {
		AsyncTask<Void, Void, Boolean> flushTask = new AsyncTask<Void, Void, Boolean>() {

			@Override
			protected Boolean doInBackground(Void... params) {
				flush();
				return true;
			}
		};
		flushTask.execute();
	}

	public boolean containsKey(K key) {

		return cache.get(key) != null;
	}

	public V get(K key) {
		return cache.get(key);
	}

	public void put(K key, V value) {
		cache.put(key, value);
	}

	public void remove(K key) {
		cache.remove(key);
	}

	public void clear() {
		cache.evictAll();
	}

}

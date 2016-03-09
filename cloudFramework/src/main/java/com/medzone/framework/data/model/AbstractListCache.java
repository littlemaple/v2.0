package com.medzone.framework.data.model;

import java.util.ArrayList;
import java.util.List;

import android.os.AsyncTask;

/**
 * 
 * 
 * Always be used in the linear scene.
 * 
 * @author ChenJunQi.
 * 
 */
public abstract class AbstractListCache<T> implements ListCache<T> {

	/**
	 * Maintaining a collection of the results of <b>T</b>
	 */
	protected List<T> list;

	public AbstractListCache() {
		super();
		list = new ArrayList<T>();
	}

	public void set(int index, T item) {
		list.set(index, item);
	}

	@Override
	public void add(T item) {
		list.add(item);
	}

	@Override
	public void add(int location, T item) {
		if (location >= 0 && location <= list.size()) {
			list.add(location, item);
		}
	}

	@Override
	public void addAll(List<T> itemList) {
		list.addAll(itemList);
	}

	@Override
	public void addAll(int location, List<T> itemList) {
		if (location >= 0 && location <= list.size()) {
			list.addAll(location, itemList);
		}
	}

	@Override
	public void clear() {
		flush();
		list.clear();
	}

	@Override
	public T get(int location) {
		if (location >= 0 && location < list.size())
			return list.get(location);
		return null;
	}

	@Override
	public int indexOf(T item) {
		return list.indexOf(item);
	}

	@Override
	public void remove(int location) {
		if (location >= 0 && location < list.size())
			list.remove(location);
	}

	@Override
	public void remove(T item) {
		list.remove(item);
	}

	@Override
	public int size() {
		return list.size();
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

}

package com.medzone.cloud.cache;

import java.lang.reflect.ParameterizedType;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import android.os.AsyncTask;

import com.j256.ormlite.dao.Dao;
import com.medzone.cloud.database.DatabaseHelper;
import com.medzone.framework.data.bean.BaseDatabaseObject;
import com.medzone.framework.data.bean.imp.AttachInfo;
import com.medzone.framework.data.model.AbstractListCache;

@SuppressWarnings("unchecked")
public abstract class AbstractDBObjectListCache<T extends BaseDatabaseObject>
		extends AbstractListCache<T> {

	/**
	 * 
	 */
	private Class<T> itemClass;
	protected AttachInfo attachInfo;

	public AbstractDBObjectListCache() {
		super();
		itemClass = (Class<T>) ((ParameterizedType) getClass()
				.getGenericSuperclass()).getActualTypeArguments()[0];
	}

	@Override
	public void flush() {
		try {
			Dao<T, Long> dao = DatabaseHelper.getInstance().getDao(itemClass);
			List<T> cloneList = (List<T>) ((ArrayList<T>) list).clone();
			for (T item : cloneList) {
				if (item.isInvalidate()) {
					dao.createOrUpdate(item);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	public void flush(T item) {
		try {
			Dao<T, Long> dao = DatabaseHelper.getInstance().getDao(itemClass);

			if (item.isInvalidate()) {
				dao.createOrUpdate(item);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void flush(List<T> list) {
		try {
			Dao<T, Long> dao = DatabaseHelper.getInstance().getDao(itemClass);
			List<T> cloneList = (List<T>) ((ArrayList<T>) list).clone();
			for (T item : cloneList) {
				if (item.isInvalidate()) {
					dao.createOrUpdate(item);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void asyncDelete(T item) {
		AsyncTask<T, Void, Boolean> flushTask = new AsyncTask<T, Void, Boolean>() {

			@Override
			protected Boolean doInBackground(T... params) {
				for (T item : params) {
					delete(item);
				}
				return true;
			}
		};
		flushTask.execute(item);
	}

	public void asyncDelete(List<T> list) {
		final List<T> list2 = list;
		AsyncTask<Void, Void, Boolean> flushTask = new AsyncTask<Void, Void, Boolean>() {

			@Override
			protected Boolean doInBackground(Void... params) {
				for (T item : list2) {
					delete(item);
				}
				return true;
			}
		};
		flushTask.execute();
	}

	public void asyncFlush(T item) {
		AsyncTask<T, Void, Boolean> flushTask = new AsyncTask<T, Void, Boolean>() {

			@Override
			protected Boolean doInBackground(T... params) {
				for (T item : params) {
					flush(item);
				}
				return true;
			}
		};
		flushTask.execute(item);
	}

	public void asyncFlush(List<T> list) {
		final List<T> list2 = list;
		AsyncTask<Void, Void, Boolean> flushTask = new AsyncTask<Void, Void, Boolean>() {

			@Override
			protected Boolean doInBackground(Void... params) {
				flush(list2);
				return true;
			}
		};
		flushTask.execute();
	}

	@Override
	public int delete(T item) {
		try {
			Dao<T, Long> dao = DatabaseHelper.getInstance().getDao(itemClass);
			return dao.delete(item);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}

	public int delete(long itemId) {
		try {
			Dao<T, Long> dao = DatabaseHelper.getInstance().getDao(itemClass);
			return dao.deleteById(itemId);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}

	public void addOrUpdate(T item) {
		for (T listItem : list) {
			if (listItem.isSameRecord(item)) {
				listItem.cloneFrom(item);
				return;
			}
		}
		list.add(item);
	}

	public void addOrUpdate(List<T> itemList) {
		for (T item : itemList) {
			addOrUpdate(item);
		}
	}

	/**
	 * @param attachInfo
	 *            the attachInfo to set
	 */
	public void setAttachInfo(AttachInfo attachInfo) {
		this.attachInfo = attachInfo;
	}

	/**
	 * @return the attachInfo
	 */
	public AttachInfo getAttachInfo() {
		return attachInfo;
	}

}

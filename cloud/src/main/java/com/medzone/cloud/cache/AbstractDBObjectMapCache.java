package com.medzone.cloud.cache;

import java.lang.reflect.ParameterizedType;
import java.sql.SQLException;
import java.util.Map;

import android.os.AsyncTask;

import com.j256.ormlite.dao.Dao;
import com.medzone.cloud.database.DatabaseHelper;
import com.medzone.framework.data.bean.BaseDatabaseObject;
import com.medzone.framework.data.bean.imp.AttachInfo;
import com.medzone.framework.data.model.AbstractMapCache;

@SuppressWarnings("unchecked")
public abstract class AbstractDBObjectMapCache<K, V extends BaseDatabaseObject>
		extends AbstractMapCache<K, V> {

	private Class<V> itemClass;
	protected AttachInfo attachInfo;

	public AbstractDBObjectMapCache() {
		super();
		itemClass = (Class<V>) ((ParameterizedType) getClass()
				.getGenericSuperclass()).getActualTypeArguments()[1];
	}

	@Override
	public void flush() {
		if (cache == null || cache.size() == 0) {
			return;
		}
		try {
			Dao<V, Long> dao = DatabaseHelper.getInstance().getDao(itemClass);
			V item;

			for (Map.Entry<K, V> entry : cache.snapshot().entrySet()) {
				item = entry.getValue();
				if (item.isInvalidate()) {
					dao.createOrUpdate(item);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void flush(V item) {
		try {
			Dao<V, Long> dao = DatabaseHelper.getInstance().getDao(itemClass);
			if (item.isInvalidate()) {
				dao.createOrUpdate(item);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void asyncFlush(final V item) {
		AsyncTask<Void, Void, Boolean> flushTask = new AsyncTask<Void, Void, Boolean>() {

			@Override
			protected Boolean doInBackground(Void... params) {
				flush(item);
				return true;
			}
		};
		flushTask.execute();
	}

	@Override
	public int delete(V item) {
		if (item == null) {
			return 0;
		}
		try {
			Dao<V, Long> dao = DatabaseHelper.getInstance().getDao(itemClass);
			if (item.isInvalidate()) {
				return dao.delete(item);

			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}

	@Override
	public void asyncDelete(final V item) {
		AsyncTask<Void, Void, Boolean> flushTask = new AsyncTask<Void, Void, Boolean>() {

			@Override
			protected Boolean doInBackground(Void... params) {
				delete(item);
				return true;
			}
		};
		flushTask.execute();
	}

	public void setAttachInfo(AttachInfo attachInfo) {
		this.attachInfo = attachInfo;
	}

	public AttachInfo getAttachInfo() {
		return attachInfo;
	}
}

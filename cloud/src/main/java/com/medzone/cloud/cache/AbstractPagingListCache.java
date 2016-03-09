package com.medzone.cloud.cache;

import java.util.List;

import com.medzone.framework.data.bean.BaseIdDatabaseObject;
import com.medzone.framework.data.navigation.Paging;
import com.medzone.framework.data.navigation.Stepable;

public abstract class AbstractPagingListCache<T extends BaseIdDatabaseObject, S extends Stepable<S>>
		extends AbstractDBObjectListCache<T> {

	/**
	 * 
	 */

	public AbstractPagingListCache() {
		super();
	}

	abstract public List<T> read(Paging<S> paging);

	@Override
	public List<T> read() {
		return read(new Paging<S>());
	}

}

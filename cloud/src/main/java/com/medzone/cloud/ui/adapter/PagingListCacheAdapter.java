/**
 * 
 */
package com.medzone.cloud.ui.adapter;

import java.util.Observable;
import java.util.Observer;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.medzone.cloud.cache.AbstractPagingListCache;
import com.medzone.cloud.ui.adapter.viewholder.BaseViewHolder;
import com.medzone.framework.Log;
import com.medzone.framework.data.bean.BaseIdDatabaseObject;
import com.medzone.framework.data.navigation.Stepable;

/**
 * @author ChenJunQi.
 * 
 */
public abstract class PagingListCacheAdapter<T extends BaseIdDatabaseObject, S extends Stepable<S>, C extends AbstractPagingListCache<T, S>>
		extends BaseAdapter implements Observer {

	public static final int ITEM_TYPE_DATA = 1;
	protected Context context;
	protected C cache;

	public String getCacheSimpleName() {
		return cache.getClass().getSimpleName();
	}

	public PagingListCacheAdapter(Context context) {
		this.context = context;
	}

	public void setCache(C cache) {
		this.cache = cache;
	}

	@Override
	public int getCount() {
		if (!checkCacheVaild())
			return 0;
		return cache.size();
	}

	@Override
	public T getItem(int position) {
		if (!checkCacheVaild())
			return null;
		return cache.get(position);
	}

	@Override
	public long getItemId(int position) {
		if (!checkCacheVaild())
			return 0;
		return cache.get(position).getId();
	}

	private boolean checkCacheVaild() {
		if (cache == null)
			return false;
		return true;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		int viewType = getItemViewType(position);
		View itemView = null;
		if (convertView != null) {
			itemView = convertView;
		} else {
			itemView = inflateView(viewType, position);
		}
		fillView(itemView, getItem(position));
		return itemView;
	}

	public abstract View inflateView(int viewType, int position);

	@Override
	public int getItemViewType(int position) {
		if (cache != null) {
			T item = cache.get(position);
			if (item != null) {
				return getItemType(item);
			}
		}
		return super.getItemViewType(position);
	}

	public void fillView(View view, Object item) {
		BaseViewHolder viewHolder = (BaseViewHolder) view.getTag();
		viewHolder.fillFromItem(item);
	}

	@Override
	public void update(Observable observable, Object data) {
		Log.v("Paging#PagingListCacheAdapter$refresh");
		notifyDataSetChanged();
	}

	public abstract int getItemType(T item);
}

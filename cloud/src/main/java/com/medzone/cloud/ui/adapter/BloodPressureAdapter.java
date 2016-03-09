/**
 * 
 */
package com.medzone.cloud.ui.adapter;

import java.util.Observable;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.medzone.cloud.cache.BloodPressureCache;
import com.medzone.cloud.ui.adapter.viewholder.BloodPressureViewHolder;
import com.medzone.framework.Log;
import com.medzone.framework.data.bean.imp.BloodPressure;
import com.medzone.framework.data.navigation.LongStepable;
import com.medzone.mcloud.R.layout;

/**
 * @author ChenJunQi.
 * 
 */
public class BloodPressureAdapter extends
		PagingListCacheAdapter<BloodPressure, LongStepable, BloodPressureCache> {

	/**
	 * @param context
	 */
	public BloodPressureAdapter(Context context) {
		super(context);
	}

	@Override
	public View inflateView(int viewType, int position) {

		View itemView = null;
		switch (viewType) {
		case ITEM_TYPE_DATA:
			itemView = LayoutInflater.from(context).inflate(
					layout.list_item_record, null);
			itemView.setTag(new BloodPressureViewHolder(itemView, context));
			break;
		default:
			break;
		}
		return itemView;

	}

	@Override
	public int getItemType(BloodPressure item) {
		if (item == null) {
			return 0;
		}
		return ITEM_TYPE_DATA;
	}

	@Override
	public void update(Observable observable, Object data) {
		super.update(observable, data);
		Log.e("PagingListCacheAdapter#update" + cache.size());

		for (int i = 0; i < cache.size(); i++) {
			try {
				Log.w("Paging#cache" + cache.get(i).getMeasureUID());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}

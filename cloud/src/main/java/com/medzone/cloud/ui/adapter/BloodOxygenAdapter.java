/**
 * 
 */
package com.medzone.cloud.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.medzone.cloud.cache.BloodOxgenCache;
import com.medzone.cloud.ui.adapter.viewholder.BloodOxygenViewHolder;
import com.medzone.framework.data.bean.imp.BloodOxygen;
import com.medzone.framework.data.navigation.LongStepable;
import com.medzone.mcloud.R.layout;

/**
 * @author ChenJunQi.
 * 
 */
public class BloodOxygenAdapter extends
		PagingListCacheAdapter<BloodOxygen, LongStepable, BloodOxgenCache> {

	/**
	 * @param context
	 */
	public BloodOxygenAdapter(Context context) {
		super(context);
	}

	@Override
	public View inflateView(int viewType, int position) {
		View itemView = null;
		switch (viewType) {
		case ITEM_TYPE_DATA:
			itemView = LayoutInflater.from(context).inflate(
					layout.list_item_record, null);
			itemView.setTag(new BloodOxygenViewHolder(itemView, context));
			break;
		default:
			break;
		}
		return itemView;
	}

	@Override
	public int getItemType(BloodOxygen item) {
		if (item == null) {
			return 0;
		}
		return ITEM_TYPE_DATA;
	}

}

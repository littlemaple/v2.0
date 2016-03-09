/**
 * 
 */
package com.medzone.cloud.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.medzone.cloud.cache.EarTemperatureCache;
import com.medzone.cloud.ui.adapter.viewholder.EarTemperatureViewHolder;
import com.medzone.framework.data.bean.imp.EarTemperature;
import com.medzone.framework.data.navigation.LongStepable;
import com.medzone.mcloud.R.layout;

/**
 * @author ChenJunQi.
 * 
 */
public class EarTemperatureAdapter
		extends
		PagingListCacheAdapter<EarTemperature, LongStepable, EarTemperatureCache> {

	/**
	 * @param context
	 */
	public EarTemperatureAdapter(Context context) {
		super(context);
	}

	@Override
	public View inflateView(int viewType, int position) {

		View itemView = null;
		switch (viewType) {
		case ITEM_TYPE_DATA:
			itemView = LayoutInflater.from(context).inflate(
					layout.list_item_record_temperature, null);
			itemView.setTag(new EarTemperatureViewHolder(itemView, context));
			break;
		default:
			break;
		}
		return itemView;
	}

	@Override
	public int getItemType(EarTemperature item) {
		if (item == null) {
			return 0;
		}
		return ITEM_TYPE_DATA;
	}
}

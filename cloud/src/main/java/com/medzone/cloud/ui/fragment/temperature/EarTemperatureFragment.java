/**
 * 
 */
package com.medzone.cloud.ui.fragment.temperature;

import com.medzone.cloud.cache.EarTemperatureCache;
import com.medzone.cloud.ui.adapter.EarTemperatureAdapter;
import com.medzone.cloud.ui.adapter.PagingListCacheAdapter;
import com.medzone.cloud.ui.fragment.BasePagingListModuleFragment;
import com.medzone.framework.data.bean.imp.EarTemperature;
import com.medzone.framework.data.navigation.LongStepable;
import com.medzone.mcloud.R.layout;

/**
 * @author ChenJunQi.
 * 
 */
public class EarTemperatureFragment
		extends
		BasePagingListModuleFragment<EarTemperature, LongStepable, EarTemperatureCache> {

	public EarTemperatureFragment() {
		mainViewResId = layout.fragment_main;
	}

	@Override
	protected PagingListCacheAdapter<EarTemperature, LongStepable, EarTemperatureCache> createAdapter() {
		return new EarTemperatureAdapter(getActivity());
	}

}

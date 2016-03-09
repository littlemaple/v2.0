/**
 * 
 */
package com.medzone.cloud.ui.fragment.bloodpressure;

import com.medzone.cloud.cache.BloodPressureCache;
import com.medzone.cloud.ui.adapter.BloodPressureAdapter;
import com.medzone.cloud.ui.adapter.PagingListCacheAdapter;
import com.medzone.cloud.ui.fragment.BasePagingListModuleFragment;
import com.medzone.framework.data.bean.imp.BloodPressure;
import com.medzone.framework.data.navigation.LongStepable;
import com.medzone.mcloud.R.layout;

/**
 * @author ChenJunQi.
 * 
 */
public class BloodPressureFragment
		extends
		BasePagingListModuleFragment<BloodPressure, LongStepable, BloodPressureCache> {

	public BloodPressureFragment() {
		mainViewResId = layout.fragment_main;
	}

	@Override
	protected PagingListCacheAdapter<BloodPressure, LongStepable, BloodPressureCache> createAdapter() {
		return new BloodPressureAdapter(getActivity());
	}

}

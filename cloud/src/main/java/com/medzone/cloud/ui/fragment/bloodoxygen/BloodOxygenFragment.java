/**
 * 
 */
package com.medzone.cloud.ui.fragment.bloodoxygen;

import android.view.View;
import android.view.View.OnClickListener;

import com.medzone.cloud.cache.BloodOxgenCache;
import com.medzone.cloud.ui.adapter.BloodOxygenAdapter;
import com.medzone.cloud.ui.adapter.PagingListCacheAdapter;
import com.medzone.cloud.ui.fragment.BasePagingListModuleFragment;
import com.medzone.framework.data.bean.imp.BloodOxygen;
import com.medzone.framework.data.navigation.LongStepable;
import com.medzone.mcloud.R.id;
import com.medzone.mcloud.R.layout;

/**
 * @author ChenJunQi.
 * 
 */
public class BloodOxygenFragment
		extends
		BasePagingListModuleFragment<BloodOxygen, LongStepable, BloodOxgenCache>
		implements OnClickListener {

	@Override
	public void onResume() {
		super.onResume();
	}

	public BloodOxygenFragment() {
		mainViewResId = layout.fragment_main;
	}

	@Override
	protected PagingListCacheAdapter<BloodOxygen, LongStepable, BloodOxgenCache> createAdapter() {
		return new BloodOxygenAdapter(getActivity());
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case id.actionbar_left:
 			break;
		default:
			break;
		}

	}

}

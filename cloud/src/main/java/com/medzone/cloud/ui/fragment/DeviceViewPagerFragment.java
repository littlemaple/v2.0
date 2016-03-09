/**
 * 
 */
package com.medzone.cloud.ui.fragment;

import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.GridView;

import com.medzone.cloud.module.CloudMeasureModule;
import com.medzone.cloud.ui.adapter.DeviceGridViewAdapter;
import com.medzone.framework.data.bean.imp.GroupMember;
import com.medzone.mcloud.R;
import com.medzone.mcloud.R.layout;

/**
 * @author ChenJunQi.
 * 
 */
public class DeviceViewPagerFragment extends Fragment {

	private List<CloudMeasureModule<?>> list;
	private GridView gridView;
	private DeviceGridViewAdapter adapter;
	private boolean isNeedUpdate = false;
	private GroupMember groupmember;
	@Deprecated
	private int curPagination = 0;

	private View rootView;

	public static DeviceViewPagerFragment newInstance() {

		return new DeviceViewPagerFragment();
	}

	public void setContent(int curPagination,
			List<CloudMeasureModule<?>> dataList, GroupMember groupmember) {
		this.curPagination = curPagination;
		this.list = dataList;
		this.groupmember = groupmember;
		isNeedUpdate = true;
	}

	public int getCurPagination() {
		return curPagination;
	}

	public boolean isNeedUpdate() {
		return isNeedUpdate;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		rootView = inflater.inflate(layout.fragment_viewpager_device, null);
		gridView = (GridView) rootView.findViewById(R.id.equipment_girdView);
		adapter = new DeviceGridViewAdapter(getActivity(), groupmember);
		adapter.setContent(list);
		gridView.setAdapter(adapter);
		return rootView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		rootView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				getActivity().finish();
			}
		});
	}

	public void invalidate() {
		adapter.notifyDataSetChanged();
	}

}

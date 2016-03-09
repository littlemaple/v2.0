/**
 * 
 */
package com.medzone.cloud.ui.adapter;

import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import com.medzone.cloud.module.CloudMeasureModule;
import com.medzone.cloud.ui.fragment.DeviceViewPagerFragment;
import com.medzone.framework.data.bean.imp.GroupMember;

/**
 * @author ChenJunQi.
 * 
 */
public class CentreDetectionAdapter extends FragmentPagerAdapter {

	private final int pageSize = 4;

	private List<CloudMeasureModule<?>> list;
	private List<DeviceViewPagerFragment> fragmentList = new LinkedList<DeviceViewPagerFragment>();

	protected Context context;
	protected GroupMember groupmember;

	public CentreDetectionAdapter(FragmentManager fm, Context context,
			GroupMember groupmember) {
		super(fm);
		this.context = context;
		this.groupmember = groupmember;
	}

	public void setContent(List<CloudMeasureModule<?>> list) {
		this.list = list;
		notifyDataSetChanged();
	}

	@Override
	public Fragment getItem(int position) {
		DeviceViewPagerFragment ef = DeviceViewPagerFragment.newInstance();
		ef.setContent(position, getGirdViewData(position), groupmember);
		fragmentList.add(position, ef);
		return ef;
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {

		DeviceViewPagerFragment ef = (DeviceViewPagerFragment) super
				.instantiateItem(container, position);
		ef.setContent(position, getGirdViewData(position), groupmember);
		return ef;
	}

	@Override
	public int getItemPosition(Object object) {

		DeviceViewPagerFragment ef = (DeviceViewPagerFragment) object;
		int oldPagination = ef.getCurPagination();
		ef.setContent(oldPagination, getGirdViewData(oldPagination),
				groupmember);
		if (ef.isNeedUpdate()) {
			ef.invalidate();
			return POSITION_NONE;
		}
		return POSITION_UNCHANGED;
	}

	@Override
	public int getCount() {

		if (this.list == null)
			return 0;
		if (this.list.isEmpty())
			return 0;
		int offset = list.size() % pageSize > 0 ? 1 : 0;
		int count = list.size() / pageSize + offset;

		return count;
	}

	private List<CloudMeasureModule<?>> getGirdViewData(int position) {

		List<CloudMeasureModule<?>> list = new LinkedList<CloudMeasureModule<?>>();

		int curPos = position == 0 ? 0 : position * pageSize;
		int endPos = (position + 1) * pageSize > this.list.size() ? this.list
				.size() : (position + 1) * pageSize;

		for (int i = curPos; i < endPos; i++) {
			list.add(this.list.get(i));
		}

		return list;
	}

}
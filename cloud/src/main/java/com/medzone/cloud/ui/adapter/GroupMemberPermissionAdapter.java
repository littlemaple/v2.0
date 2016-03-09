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
import android.view.View;

import com.medzone.cloud.ui.fragment.group.GroupMemberPermissonViewPagerFragment;
import com.medzone.framework.data.bean.imp.GroupMember;
import com.medzone.framework.view.viewpager.IConPagerAdapter;
import com.medzone.mcloud.R.drawable;

/**
 * @author ChenJunQi.
 * 
 */
public class GroupMemberPermissionAdapter extends FragmentPagerAdapter
		implements IConPagerAdapter {

	private final int pageSize = 12;
	private List<GroupMember> memberList;
	private List<GroupMemberPermissonViewPagerFragment> fragmentList = new LinkedList<GroupMemberPermissonViewPagerFragment>();

	protected Context context;

	public GroupMemberPermissionAdapter(FragmentManager fm, Context context) {
		super(fm);
		this.context = context;
	}

	public void setContent(List<GroupMember> list) {
		this.memberList = list;
	}

	@Override
	public Fragment getItem(int position) {
		GroupMemberPermissonViewPagerFragment gvFragment = GroupMemberPermissonViewPagerFragment
				.newInstance();
		gvFragment.setContent(position, getGirdViewData(position));
		fragmentList.add(gvFragment);
		return gvFragment;
	}

	@Override
	public Object instantiateItem(View container, int position) {

		GroupMemberPermissonViewPagerFragment gvFragment = (GroupMemberPermissonViewPagerFragment) super
				.instantiateItem(container, position);
		gvFragment.setContent(position, getGirdViewData(position));

		return gvFragment;
	}

	@Override
	public int getItemPosition(Object object) {

		GroupMemberPermissonViewPagerFragment gvFragment = (GroupMemberPermissonViewPagerFragment) object;
		int oldPagination = gvFragment.getCurPagination();
		gvFragment.setContent(oldPagination, getGirdViewData(oldPagination));
		if (gvFragment.isNeedUpdate()) {
			gvFragment.invalidate();
			return POSITION_NONE;
		}
		return POSITION_UNCHANGED;
	}

	@Override
	public int getCount() {
		if (memberList == null)
			return 0;
		int offset = memberList.size() % pageSize > 0 ? 1 : 0;
		int count = memberList.size() / pageSize + offset;

		return count;
	}

	private List<GroupMember> getGirdViewData(int position) {

		List<GroupMember> list = new LinkedList<GroupMember>();
		int realPageSize = pageSize;

		int curPos = position == 0 ? 0 : position * realPageSize;
		int endPos = (position + 1) * realPageSize > this.memberList.size() ? this.memberList
				.size() : (position + 1) * realPageSize;

		for (int i = curPos; i < endPos; i++) {
			list.add(this.memberList.get(i));
		}

		return list;
	}

	@Override
	public int getIconResId(int index) {
		return drawable.selector_viewpager_indicator;
	}
}

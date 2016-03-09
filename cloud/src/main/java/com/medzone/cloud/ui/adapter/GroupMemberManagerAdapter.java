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

import com.medzone.cloud.Constants;
import com.medzone.cloud.data.CurrentAccountManager;
import com.medzone.cloud.ui.fragment.group.GroupMemberViewPagerFragment;
import com.medzone.framework.data.bean.imp.Group;
import com.medzone.framework.data.bean.imp.GroupMember;
import com.medzone.framework.view.viewpager.IConPagerAdapter;
import com.medzone.mcloud.R.drawable;

/**
 * @author ChenJunQi.
 * 
 */
public class GroupMemberManagerAdapter extends FragmentPagerAdapter implements
		IConPagerAdapter {

	private final int pageSize = 8;
	private List<GroupMember> memberList;
	private List<GroupMemberViewPagerFragment> fragmentList = new LinkedList<GroupMemberViewPagerFragment>();

	private Group group;

	public GroupMemberManagerAdapter(FragmentManager fm, Context context,
			Group group) {
		super(fm);
		this.group = group;
	}

	public void setContent(List<GroupMember> list) {
		this.memberList = list;
		notifyDataSetChanged();
	}

	@Override
	public Fragment getItem(int position) {
		GroupMemberViewPagerFragment gvFragment = GroupMemberViewPagerFragment
				.newInstance();
		gvFragment.setContent(position, getGirdViewData(position));
		fragmentList.add(gvFragment);

		return gvFragment;
	}

	@Override
	public Object instantiateItem(View container, int position) {

		GroupMemberViewPagerFragment ret = (GroupMemberViewPagerFragment) super
				.instantiateItem(container, position);
		ret.setContent(position, getGirdViewData(position));

		return ret;
	}

	@Override
	public int getItemPosition(Object object) {

		GroupMemberViewPagerFragment gvFragment = (GroupMemberViewPagerFragment) object;
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

		int tmpPage = pageSize;
		if (isGroupOwner()) {
			--tmpPage;
		}

		int offset = memberList.size() % tmpPage > 0 ? 1 : 0;
		int count = memberList.size() / tmpPage + offset;

		return count;
	}

	private List<GroupMember> getGirdViewData(int position) {

		List<GroupMember> list = new LinkedList<GroupMember>();
		int realPageSize = pageSize;
		if (isGroupOwner()) {
			realPageSize--;
		}

		int curPos = position == 0 ? 0 : position * realPageSize;
		int endPos = (position + 1) * realPageSize > this.memberList.size() ? this.memberList
				.size() : (position + 1) * realPageSize;

		for (int i = curPos; i < endPos; i++) {
			list.add(this.memberList.get(i));
		}

		if (isGroupOwner()) {
			list.add(getPlaceHolderMember());
		}

		return list;
	}

	private GroupMember getPlaceHolderMember() {
		GroupMember manager = new GroupMember();
		manager.setTag(Constants.OWNER_MANAGER_TAG);
		manager.setGroupID(group.getGroupID());
		return manager;
	}

	private boolean isGroupOwner() {

		return group.equals(CurrentAccountManager.getCurAccount());
	}

	@Override
	public int getIconResId(int index) {
		return drawable.selector_viewpager_indicator;
	}
}

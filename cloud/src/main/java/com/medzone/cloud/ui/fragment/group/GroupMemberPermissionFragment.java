package com.medzone.cloud.ui.fragment.group;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.medzone.cloud.data.PropertyCenter;
import com.medzone.cloud.data.helper.CustomComparator;
import com.medzone.cloud.ui.adapter.GroupMemberPermissionAdapter;
import com.medzone.framework.data.bean.imp.GroupMember;
import com.medzone.framework.fragment.BaseFragment;
import com.medzone.framework.view.viewpager.CirclePageIndicator;
import com.medzone.framework.view.viewpager.IPageIndicator;
import com.medzone.mcloud.R.id;
import com.medzone.mcloud.R.layout;

public class GroupMemberPermissionFragment extends BaseFragment {

	private static GroupMemberPermissionFragment instance;
	public static boolean isLongPressed = false;
	private ViewPager mPager;
	private GroupMemberPermissionAdapter mAdapter;
	private IPageIndicator mIndicator;
	private static boolean isMeasure;

	public static boolean isMeasure() {
		return isMeasure;
	}

	private List<GroupMember> allMemberList, normalMemberList,
			extraCarefulMemberList;

	public GroupMemberPermissionFragment() {
		instance = this;
	}

	public void setContent(List<GroupMember> memberList) {
		this.allMemberList = memberList;
		initGroupMemberData();
	}

	public void isMeasure(boolean isMeasure) {
		GroupMemberPermissionFragment.isMeasure = isMeasure;
	}

	private void sortContentList(List<GroupMember> allMemberList) {

		CustomComparator<GroupMember> cmp = new CustomComparator<GroupMember>();
		Collections.sort(allMemberList, cmp);
	}

	public void initGroupMemberData() {
		normalMemberList = new ArrayList<GroupMember>();
		extraCarefulMemberList = new ArrayList<GroupMember>();
		for (GroupMember member : allMemberList) {
			if (member.isCare()) {
				extraCarefulMemberList.add(member);
			} else {
				normalMemberList.add(member);
			}
		}
		sortContentList(extraCarefulMemberList);
		sortContentList(normalMemberList);
		allMemberList.clear();
		allMemberList.addAll(extraCarefulMemberList);
		allMemberList.addAll(normalMemberList);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(layout.fragment_group_member_manager,
				container, false);
		mPager = (ViewPager) rootView.findViewById(id.pager);
		mIndicator = (CirclePageIndicator) rootView.findViewById(id.indicator);

		mAdapter = new GroupMemberPermissionAdapter(getChildFragmentManager(),
				getActivity());
		mAdapter.setContent(allMemberList);
		mPager.setAdapter(mAdapter);
		mIndicator.setViewPager(mPager);

		return rootView;
	}

	public static void removeMember(GroupMember delMember) {
		instance.allMemberList.remove(delMember);
	}

	public static void toggleLongPressedState() {
		GroupMemberPermissionFragment.isLongPressed = !isLongPressed;
		notifyDataSetChanged();
		PropertyCenter.getInstance().firePropertyChange(
				PropertyCenter.PROPERTY_REFRESH_DELETE_STAGE, null,
				GroupMemberPermissionFragment.isLongPressed);
	}

	public static void setLongPressedState(boolean isLongPressed) {
		if (isLongPressed == GroupMemberPermissionFragment.isLongPressed) {
			return;
		} else {
			GroupMemberPermissionFragment.isLongPressed = isLongPressed;
			notifyDataSetChanged();
		}
		PropertyCenter.getInstance().firePropertyChange(
				PropertyCenter.PROPERTY_REFRESH_DELETE_STAGE, null,
				GroupMemberPermissionFragment.isLongPressed);
	}

	public static void notifyDataSetChanged() {
		instance.invalidate();
	}

	public void invalidate() {
		mAdapter.notifyDataSetChanged();
		mPager.invalidate();
		PropertyCenter.getInstance().firePropertyChange(
				PropertyCenter.PROPERTY_REFRESH_DELETE_STAGE, null,
				GroupMemberPermissionFragment.isLongPressed);
	}
}

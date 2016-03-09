/**
 * 
 */
package com.medzone.cloud.ui.fragment.group;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.medzone.cloud.GlobalVars;
import com.medzone.cloud.data.PropertyCenter;
import com.medzone.cloud.data.TemporaryData;
import com.medzone.cloud.data.helper.CustomComparator;
import com.medzone.cloud.ui.adapter.GroupMemberManagerAdapter;
import com.medzone.framework.Log;
import com.medzone.framework.data.bean.imp.Group;
import com.medzone.framework.data.bean.imp.GroupMember;
import com.medzone.framework.fragment.BaseFragment;
import com.medzone.framework.view.viewpager.CirclePageIndicator;
import com.medzone.framework.view.viewpager.IPageIndicator;
import com.medzone.mcloud.R.id;
import com.medzone.mcloud.R.layout;

/**
 * @author ChenJunQi.
 * 
 */
public class GroupMemberManagerFragment extends BaseFragment implements
		PropertyChangeListener {

	public static boolean isLongPressed = false;
	private static GroupMemberManagerFragment instance;

	public static ViewPager mPager;
	private GroupMemberManagerAdapter mAdapter;
	private IPageIndicator mIndicator;
	private static int CurrentItem = 0;
	public static String mCurrentItem = "current_item";

	private List<GroupMember> allMemberList, normalMemberList;
	private GroupMember tempMember;
	private Group group;

	public GroupMemberManagerFragment() {
		instance = this;
	}

	public void setCurrentItem(int currentItem) {
		GroupMemberManagerFragment.CurrentItem = currentItem;
	}

	public static int getCurrentItem() {
		return CurrentItem;
	}

	public void setContent(List<GroupMember> allMemberList, Group group) {
		this.allMemberList = allMemberList;
		this.group = group;
		initGroupMemberData();
	}

	public static void saveCurrentItem(){
		if(mPager!=null){
			int position = mPager.getCurrentItem();
			TemporaryData.save(GroupMemberManagerFragment.mCurrentItem,
					position);
		}
	}

	private void sortContentList(List<GroupMember> allMemberList) {

		CustomComparator<GroupMember> cmp = new CustomComparator<GroupMember>();
		Collections.sort(allMemberList, cmp);
	}

	public void initGroupMemberData() {
		normalMemberList = new ArrayList<GroupMember>();
		for (GroupMember member : allMemberList) {
			if (member.getAccountID() == null)
				return;
			if (group.getCreatorID() == null)
				return;
			if (member.getAccountID().intValue() == group.getCreatorID()
					.intValue()) {
				tempMember = member;
				continue;
			}
			normalMemberList.add(member);
		}
		sortContentList(normalMemberList);
		allMemberList.clear();
		if (tempMember != null)
			allMemberList.add(tempMember);
		allMemberList.addAll(normalMemberList);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		PropertyCenter.getInstance().addPropertyChangeListener(this);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(layout.fragment_group_member_manager,
				container, false);
		mPager = (ViewPager) rootView.findViewById(id.pager);
		mIndicator = (CirclePageIndicator) rootView.findViewById(id.indicator);

		mAdapter = new GroupMemberManagerAdapter(getChildFragmentManager(),
				getActivity(), group);
		mAdapter.setContent(allMemberList);
		mPager.setAdapter(mAdapter);
		mPager.setCurrentItem(CurrentItem);
		mIndicator.setViewPager(mPager);

		return rootView;
	}

	@Override
	public void onDetach() {
		super.onDetach();
		PropertyCenter.getInstance().removePropertyChangeListener(this);
	}

	public static void removeMember(GroupMember delMember) {
		instance.allMemberList.remove(delMember);
	}

	public static void toggleLongPressedState() {
		GroupMemberManagerFragment.isLongPressed = !isLongPressed;
		notifyDataSetChanged();
		PropertyCenter.getInstance().firePropertyChange(
				PropertyCenter.PROPERTY_REFRESH_DELETE_STAGE, null,
				GroupMemberManagerFragment.isLongPressed);
	}

	public static void setLongPressedState(boolean isLongPressed) {

		if (isLongPressed == GroupMemberManagerFragment.isLongPressed) {
			return;
		} else {
			GroupMemberManagerFragment.isLongPressed = isLongPressed;
			notifyDataSetChanged();
		}
		PropertyCenter.getInstance().firePropertyChange(
				PropertyCenter.PROPERTY_REFRESH_DELETE_STAGE, null,
				GroupMemberManagerFragment.isLongPressed);
	}

	public static void notifyDataSetChanged() {
		instance.invalidate();
	}

	public void invalidate() {
		mAdapter.notifyDataSetChanged();
		mPager.invalidate();
		PropertyCenter.getInstance().firePropertyChange(
				PropertyCenter.PROPERTY_REFRESH_DELETE_STAGE, null,
				GroupMemberManagerFragment.isLongPressed);
	}

	@Override
	public void propertyChange(PropertyChangeEvent event) {
		if (event.getPropertyName().equals(
				PropertyCenter.PROPERTY_REFRESH_AVATAR)) {
			Log.w(">>>群资料中群成员：" + GlobalVars.isOffLineLogined());
			mAdapter.notifyDataSetChanged();
		}
	}
}

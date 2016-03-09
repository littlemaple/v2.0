/**
 * 
 */
package com.medzone.cloud.ui.fragment.group;

import java.util.List;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.medzone.cloud.ui.adapter.GroupMemberGridViewAdapter;
import com.medzone.framework.data.bean.imp.GroupMember;
import com.medzone.framework.fragment.BaseFragment;
import com.medzone.mcloud.R.id;
import com.medzone.mcloud.R.layout;

/**
 * @author ChenJunQi.
 * 
 */
public class GroupMemberViewPagerFragment extends BaseFragment {

	private GridView gridView;
	private GroupMemberGridViewAdapter adapter;
	private List<GroupMember> memberList;

	private int curPagination = 0;
	private boolean isNeedUpdate = false;

	public static GroupMemberViewPagerFragment newInstance() {
		return new GroupMemberViewPagerFragment();
	}

	public void setContent(int curPagination, List<GroupMember> list) {
		this.curPagination = curPagination;
		this.memberList = list;
		this.isNeedUpdate = true;
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
		View rootView = inflater.inflate(
				layout.fragment_viewpager_group_member, container, false);

		gridView = (GridView) rootView.findViewById(id.gv_group_member);
		adapter = new GroupMemberGridViewAdapter(getActivity());
		adapter.setContent(memberList);
		gridView.setAdapter(adapter);

		return rootView;
	}

	public void invalidate() {
		adapter.notifyDataSetChanged();
	}
}

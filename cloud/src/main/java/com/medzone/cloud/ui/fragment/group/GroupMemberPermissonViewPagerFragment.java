/**
 * 
 */
package com.medzone.cloud.ui.fragment.group;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.GridView;

import com.medzone.cloud.data.PropertyCenter;
import com.medzone.cloud.ui.adapter.SettingMemberAdapter;
import com.medzone.framework.data.bean.imp.GroupMember;
import com.medzone.framework.fragment.BaseFragment;
import com.medzone.mcloud.R.id;
import com.medzone.mcloud.R.layout;

/**
 * @author ChenJunQi.
 * 
 */
public class GroupMemberPermissonViewPagerFragment extends BaseFragment
		implements PropertyChangeListener {

	private GridView gridView;
	private SettingMemberAdapter adapter;
	private List<GroupMember> memberList;
	private int curPagination = 0;
	private boolean isNeedUpdate = false;
	// private int hideDrawable, showDrawable;
	private View hideViewBottom;

	public static GroupMemberPermissonViewPagerFragment newInstance() {
		return new GroupMemberPermissonViewPagerFragment();
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
				layout.fragment_viewpager_permission_member, container, false);

		hideViewBottom = rootView.findViewById(id.hide_view_bottom);
		gridView = (GridView) rootView.findViewById(id.gv_group_member);
		adapter = new SettingMemberAdapter(getActivity());
		adapter.setContent(memberList);
		gridView.setAdapter(adapter);
		// hideDrawable = getResources().getColor(R.color.bg_five_alpha_dark);
		// showDrawable = getResources().getColor(android.R.color.white);

		hideViewBottom.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				toogle();
			}
		});
		PropertyCenter.getInstance().addPropertyChangeListener(this);
		return rootView;
	}

	public void show() {

	}

	public void toogle() {
		if (GroupMemberPermissionFragment.isLongPressed) {
			GroupMemberPermissionFragment.setLongPressedState(false);
		}
	}

	public void invalidate() {
		adapter.notifyDataSetChanged();
	}

	@Override
	public void propertyChange(PropertyChangeEvent event) {
		if (TextUtils.equals(event.getPropertyName(),
				PropertyCenter.PROPERTY_REFRESH_DELETE_STAGE)) {
			if (GroupMemberPermissionFragment.isLongPressed) {
				// gridView.setBackgroundColor(getResources().getColor(android.R.color.white));
				hideViewBottom.setVisibility(View.VISIBLE);
			} else {
				hideViewBottom.setVisibility(View.INVISIBLE);
				// gridView.setBackgroundColor(getResources().getColor(R.color.bg_five_alpha_dark));
			}
		}
	}

}

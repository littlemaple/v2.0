package com.medzone.cloud.ui.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.medzone.cloud.ui.adapter.viewholder.GroupMemberDetailViewHolder;
import com.medzone.framework.data.bean.imp.GroupMember;
import com.medzone.mcloud.R.layout;

public class GroupMemberAdapter extends BaseAdapter {

	private List<GroupMember> groupMemberList = new ArrayList<GroupMember>();
	private Context context;

	public GroupMemberAdapter(Context context) {
		super();
		this.context = context;
	}

	public void setContent(List<GroupMember> list) {
		this.groupMemberList = list;
		List<GroupMember> extraCarefulList = new ArrayList<GroupMember>();
		for (GroupMember gm : list) {
			if (gm.isCare()) {
				extraCarefulList.add(gm);
			}
		}

	}

	// 群成员属性改变后在列表中刷新
	public void refreshAfterDel(GroupMember member) {
		groupMemberList.remove(member);
		this.notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return groupMemberList.size();
	}

	@Override
	public Object getItem(int position) {
		return groupMemberList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View itemView = null;
		if (convertView != null) {
			itemView = convertView; 
		} else {
			itemView = inflateView();
		}
		fillView(itemView, getItem(position));

		return itemView;
	}

	private View inflateView() {
		View itemView = null;
		itemView = LayoutInflater.from(context).inflate(layout.list_item_member_detail,
				null);
		itemView.setTag(new GroupMemberDetailViewHolder(itemView, this));
		return itemView;
	}

	private void fillView(View view, final Object item) {
		GroupMemberDetailViewHolder viewHolder = (GroupMemberDetailViewHolder) view.getTag();
		viewHolder.fillFromItem(item);
	}
}
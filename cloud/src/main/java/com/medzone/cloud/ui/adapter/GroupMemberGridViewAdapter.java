/**
 * 
 */
package com.medzone.cloud.ui.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.medzone.cloud.ui.adapter.viewholder.GridViewMemberViewHolder;
import com.medzone.framework.data.bean.imp.GroupMember;
import com.medzone.mcloud.R.layout;

/**
 * @author ChenJunQi.
 * 
 */
public class GroupMemberGridViewAdapter extends BaseAdapter {

	private List<GroupMember> memberList;
	private Context context;

	public GroupMemberGridViewAdapter(Context context) {
		this.context = context;
	}

	public void setContent(List<GroupMember> list) {
		this.memberList = list;
	}

	@Override
	public int getCount() {
		return memberList.size();
	}

	@Override
	public Object getItem(int position) {
		return memberList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return memberList.get(position).getAccountID();
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
		itemView = LayoutInflater.from(context).inflate(
				layout.gridview_item_group_member, null);
		itemView.setTag(new GridViewMemberViewHolder(itemView));
		return itemView;
	}

	private void fillView(View view, final Object item) {
		GridViewMemberViewHolder viewHolder = (GridViewMemberViewHolder) view
				.getTag();
		viewHolder.fillFromItem(item);
	}

}

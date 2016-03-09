/**
 * 
 */
package com.medzone.cloud.ui.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.medzone.cloud.ui.adapter.viewholder.SettingViewHolder;
import com.medzone.framework.data.bean.imp.GroupMember;
import com.medzone.mcloud.R.layout;

/**
 * @author ChenJunQi.
 * 
 */
public class SettingAdapter extends BaseAdapter {

	/**
	 * @param mContext
	 */

	private List<GroupMember> list = new ArrayList<GroupMember>();
	private Context context;
	private Boolean isView;

	public void isView(Boolean isView) {
		this.isView = isView;
	}

	public SettingAdapter(Context context) {
		this.context = context;
	}

	public void setContent(List<GroupMember> list) {
		this.list = list;
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
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
		itemView = LayoutInflater.from(context).inflate(
				layout.list_item_detail, null);
		itemView.setTag(new SettingViewHolder(itemView));
		return itemView;
	}

	private void fillView(View view, final Object item) {
		SettingViewHolder viewHolder = (SettingViewHolder) view.getTag();
		viewHolder.isView(isView);
		viewHolder.fillFromItem(item);
	}

}

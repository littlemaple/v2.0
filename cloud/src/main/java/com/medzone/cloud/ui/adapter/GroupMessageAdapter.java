package com.medzone.cloud.ui.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.medzone.cloud.ui.adapter.viewholder.GroupNotifyViewHolder;
import com.medzone.framework.data.bean.imp.ServiceMessage;
import com.medzone.mcloud.R.layout;

public class GroupMessageAdapter extends BaseAdapter {
	private List<ServiceMessage> data;
	private Context context;

	public GroupMessageAdapter(Context context) {

		this.context = context;
	}

	public void setContent(List<ServiceMessage> data) {
		this.data = data;
		this.notifyDataSetChanged();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View itemView = null;
		ServiceMessage msg = data.get(position);
		// if (convertView != null) {
		// itemView = convertView;
		// } else {
		// itemView = inflateView();
		// }
		// TODO 缓存机制使用方式需要考虑
		itemView = inflateView();
		fillView(itemView, msg);

		return itemView;
	}

	private View inflateView() {
		View itemView = null;
		itemView = LayoutInflater.from(context).inflate(
				layout.list_item_notify, null);
		itemView.setTag(new GroupNotifyViewHolder(itemView));
		return itemView;
	}

	private void fillView(View view, final Object item) {
		GroupNotifyViewHolder viewHolder = (GroupNotifyViewHolder) view
				.getTag();
		viewHolder.fillFromItem(item);
	}

	public void refresh() {
		this.notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return this.data == null ? 0 : data.size();
	}

	@Override
	public Object getItem(int position) {
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
}
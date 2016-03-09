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

import com.medzone.cloud.ui.adapter.viewholder.ServiceSubscribeViewHolder;
import com.medzone.framework.data.bean.imp.Subscribe;
import com.medzone.mcloud.R.layout;

/**
 * @author ChenJunQi.
 * 
 */
public class ServiceSubscribeAdapter extends BaseAdapter {

	private List<Subscribe> list = new ArrayList<Subscribe>();
	private Context context;

	public ServiceSubscribeAdapter(Context context) {
		this.context = context;
	}

	public void setContent(List<Subscribe> list) {
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
		return list.get(position).getId();
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
				layout.gridview_item_service_subscribe, null);
		itemView.setTag(new ServiceSubscribeViewHolder(itemView));
		return itemView;
	}

	private void fillView(View view, final Object item) {
		ServiceSubscribeViewHolder viewHolder = (ServiceSubscribeViewHolder) view
				.getTag();
		viewHolder.fillFromItem(item);
	}

}

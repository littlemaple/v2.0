package com.medzone.cloud.ui.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.medzone.cloud.ui.adapter.viewholder.RecentExceptionMeasureDataViewHolder;
import com.medzone.framework.data.bean.imp.BaseMeasureData;
import com.medzone.mcloud.R.layout;

public class RecentExceptionMeasureDataAdapter extends BaseAdapter {

	private List<BaseMeasureData> list = new ArrayList<BaseMeasureData>();
	private Context context;

	public RecentExceptionMeasureDataAdapter(Context context) {
		this.context = context;
	}

	public void setContent(List<BaseMeasureData> list) {
		this.list = list;
		this.notifyDataSetChanged();
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
				layout.recent_exception_measure_data_fragment_item, null);
		itemView.setTag(new RecentExceptionMeasureDataViewHolder(itemView));
		return itemView;
	}

	private void fillView(View view, final Object item) {
		RecentExceptionMeasureDataViewHolder viewHolder = (RecentExceptionMeasureDataViewHolder) view
				.getTag();
		viewHolder.fillFromItem(item);
	}

}

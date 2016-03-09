package com.medzone.cloud.clock;

import android.content.Context;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.medzone.mcloud.R;

public class AlarmRepeatChooseAdapter extends BaseAdapter {

	private Context context;
	private SparseIntArray data;

	public AlarmRepeatChooseAdapter(Context context, SparseIntArray data) {
		this.data = data;
		this.context = context;
	}

	@Override
	public int getCount() {
		return data.size();
	}

	@Override
	public Object getItem(int position) {
		return data.get(position);
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
		fillView(itemView, getItem(position), position);

		return itemView;
	}

	public View inflateView() {
		View itemView = null;
		itemView = LayoutInflater.from(context).inflate(
				R.layout.activity_alarm_change_repetation, null);
		itemView.setTag(new AlarmRepeatViewHolder(itemView, context));
		return itemView;

	}

	public void fillView(View view, Object item, int position) {
		AlarmRepeatViewHolder holder = (AlarmRepeatViewHolder) view.getTag();
		holder.fillFromItem(item, position);
	}

	public void refresh(SparseIntArray data) {
		this.data = data;
		this.notifyDataSetChanged();
	}
}

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

import com.medzone.cloud.ui.adapter.viewholder.SickInfoViewHolder;
import com.medzone.framework.data.bean.imp.SickInfo;
import com.medzone.mcloud.R.layout;

/**
 * @author ChenJunQi.
 * 
 */
public class IllnessAdapter extends BaseAdapter {

	private List<SickInfo> list;
	private Context context;

	public IllnessAdapter(Context context, List<SickInfo> illnessList) {
		this.list = illnessList;
		this.context = context;
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	public void refresh(List<SickInfo> illnessList) {
		this.list = illnessList;
		this.notifyDataSetChanged();
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
				layout.list_item_illness, null);
		itemView.setTag(new SickInfoViewHolder(itemView));
		return itemView;
	}

	private void fillView(View view, final Object item) {
		SickInfoViewHolder viewHolder = (SickInfoViewHolder) view.getTag();

		viewHolder.fillFromItem(item);
	}

}

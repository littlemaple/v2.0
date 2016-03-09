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

import com.medzone.cloud.module.CloudMeasureModule;
import com.medzone.cloud.ui.adapter.viewholder.MonthlyReportViewHolder;
import com.medzone.mcloud.R.layout;

/**
 * @author ChenJunQi.
 * 
 */
public class MonthlyReportAdapter extends BaseAdapter {

	private List<CloudMeasureModule<?>> sourceList;
	private List<CloudMeasureModule<?>> displayList = new ArrayList<CloudMeasureModule<?>>();
	private Context context;

	/**
	 * @param context
	 * @param resource
	 * @param objects
	 */
	public MonthlyReportAdapter(Context context) {
		this.context = context;
	}

	public void setContent(List<CloudMeasureModule<?>> list) {
		this.sourceList = list;
		filterSourceList(sourceList);
		notifyDataSetChanged();
	}

	private void filterSourceList(List<CloudMeasureModule<?>> sourceList) {
		displayList.clear();
		if (sourceList != null) {
			for (CloudMeasureModule<?> i : sourceList) {
				switch (i.getModuleStatus()) {
				case DISPLAY:
					displayList.add(i);
					break;
				default:
					break;
				}
			}
		}
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
				layout.list_item_monthly_report, null);
		itemView.setTag(new MonthlyReportViewHolder(itemView));
		return itemView;
	}

	private void fillView(View view, final Object item) {
		MonthlyReportViewHolder viewHolder = (MonthlyReportViewHolder) view
				.getTag();
		viewHolder.fillFromItem(item);
	}

	@Override
	public int getCount() {
		return displayList.size();
	}

	@Override
	public Object getItem(int position) {
		return displayList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return Long.valueOf(position);
	}

}
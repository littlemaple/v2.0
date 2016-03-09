package com.medzone.cloud.ui.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;

import com.medzone.cloud.ui.adapter.viewholder.TemperatureListExpandableChildViewHolder;
import com.medzone.cloud.ui.adapter.viewholder.TemperatureListExpandableGroupViewHolder;
import com.medzone.framework.data.bean.imp.EarTemperature;
import com.medzone.framework.data.bean.imp.MeasureStatistical;
import com.medzone.mcloud.R;
import com.medzone.mcloud.R.layout;

public class TemperatureListExpandableAdapter extends BaseExpandableListAdapter {
	private Context context;
	private List<MeasureStatistical> groupData = new ArrayList<MeasureStatistical>();
	private List<List<EarTemperature>> childData = new ArrayList<List<EarTemperature>>();
	private int TYPE_COUNT = 2;
	private int NORMAL_TYPE = 0;
	private int EXCEPTION_TYPE = 1;
	private int currentType;

	public TemperatureListExpandableAdapter(Context context,
			List<MeasureStatistical> groupData,
			List<List<EarTemperature>> childData) {
		super();
		this.context = context;
		this.childData = childData;
		this.groupData = groupData;
	}

	// **************************************
	public Object getChild(int groupPosition, int childPosition) {
		return null;
	}

	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	public int getChildrenCount(int groupPosition) {
		return childData.get(groupPosition).size();
	}

	public int getChildType(int groupPosition, int childPosition) {
		EarTemperature et = childData.get(groupPosition).get(childPosition);
		if (et.getAbnormal() != EarTemperature.TEMPERATURE_STATE_NORMAL)
			return EXCEPTION_TYPE;
		else
			return NORMAL_TYPE;
	}

	public int getChildTypeCount() {
		return TYPE_COUNT;
	}

	public View getChildView(final int groupPosition, final int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		View itemView = null;
		if (convertView != null) {
			itemView = convertView;
		} else {
			itemView = inflafeChildView();
		}
		currentType = getChildType(groupPosition, childPosition);
		itemView.setTag(R.id.temperature_history_list_sum_times, groupPosition);
		itemView.setTag(R.id.temperature_history_list_child_time, childPosition);
		fillChildView(itemView, currentType,
				childData.get(groupPosition).get(childPosition));
		return itemView;
	}

	private View inflafeChildView() {
		View itemView = null;
		itemView = LayoutInflater.from(context).inflate(
				layout.fragment_temperature_history_list_child_item, null);
		itemView.setTag(new TemperatureListExpandableChildViewHolder(itemView));
		return itemView;
	}

	private void fillChildView(View view, int type, final Object item) {
		TemperatureListExpandableChildViewHolder viewHolder = (TemperatureListExpandableChildViewHolder) view
				.getTag();
		viewHolder.fillFromItem(item, type);
	}

	// group
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		View itemView = null;
		if (convertView != null) {
			itemView = convertView;
		} else {
			itemView = inflafeGroupView();
		}
		itemView.setTag(R.id.temperature_history_list_sum_times, groupPosition);
		itemView.setTag(R.id.temperature_history_list_child_time, -1);
		fillGroupView(itemView, isExpanded, groupData.get(groupPosition));
		return itemView;
	}

	private View inflafeGroupView() {
		View itemView = null;
		itemView = LayoutInflater.from(context).inflate(
				layout.fragment_temperature_history_list_item, null);
		itemView.setTag(new TemperatureListExpandableGroupViewHolder(itemView));
		return itemView;
	}

	private void fillGroupView(View view, boolean type, final Object item) {
		TemperatureListExpandableGroupViewHolder viewHolder = (TemperatureListExpandableGroupViewHolder) view
				.getTag();
		viewHolder.fillFromItem(item, type);
	}

	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	public Object getGroup(int groupPosition) {
		return null;
	}

	public int getGroupCount() {
		return groupData.size();

	}

	public boolean hasStableIds() {
		return true;
	}

	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}

	public void hideGroup(int groupPos) {
	}
}

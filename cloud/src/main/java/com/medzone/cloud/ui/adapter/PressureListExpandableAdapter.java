package com.medzone.cloud.ui.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;

import com.medzone.cloud.ui.adapter.viewholder.PressureListExpandableChildViewHolder;
import com.medzone.cloud.ui.adapter.viewholder.PressureListExpandableGroupViewHolder;
import com.medzone.framework.data.bean.imp.BloodPressure;
import com.medzone.framework.data.bean.imp.MeasureStatistical;
import com.medzone.mcloud.R;
import com.medzone.mcloud.R.layout;

public class PressureListExpandableAdapter extends BaseExpandableListAdapter {
	private Context context;
	private List<MeasureStatistical> groupData = new ArrayList<MeasureStatistical>();
	private List<List<BloodPressure>> childData = new ArrayList<List<BloodPressure>>();
	private int TYPE_COUNT = 2;
	private int NORMAL_TYPE = 0;
	private int EXCEPTION_TYPE = 1;
	private int currentType;

	public PressureListExpandableAdapter(Context context,
			List<MeasureStatistical> groupData,
			List<List<BloodPressure>> childData) {
		super();
		this.context = context;
		this.childData = childData;
		this.groupData = groupData;
	}

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
		BloodPressure bp = childData.get(groupPosition).get(childPosition);
		if (bp.isHealthState())
			return NORMAL_TYPE;
		else
			return EXCEPTION_TYPE;
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
		itemView.setTag(R.id.pressure_history_list_sum_times, groupPosition);
		itemView.setTag(R.id.pressure_history_list_child_time, childPosition);
		fillChildView(itemView, currentType,
				childData.get(groupPosition).get(childPosition));
		return itemView;
	}

	private View inflafeChildView() {
		View itemView = null;
		itemView = LayoutInflater.from(context).inflate(
				layout.fragment_pressure_history_list_child_item, null);
		itemView.setTag(new PressureListExpandableChildViewHolder(itemView));
		return itemView;
	}

	private void fillChildView(View view, int type, final Object item) {
		PressureListExpandableChildViewHolder viewHolder = (PressureListExpandableChildViewHolder) view
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
		itemView.setTag(R.id.pressure_history_list_sum_times, groupPosition);
		itemView.setTag(R.id.pressure_history_list_child_time, -1);
		fillGroupView(itemView, isExpanded, groupData.get(groupPosition));
		return itemView;
	}

	private View inflafeGroupView() {
		View itemView = null;
		itemView = LayoutInflater.from(context).inflate(
				layout.fragment_pressure_history_list_item, null);
		itemView.setTag(new PressureListExpandableGroupViewHolder(itemView));
		return itemView;
	}

	private void fillGroupView(View view, boolean type, final Object item) {
		PressureListExpandableGroupViewHolder viewHolder = (PressureListExpandableGroupViewHolder) view
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

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

import com.medzone.cloud.controller.AbstractUseTaskCacheController;
import com.medzone.cloud.data.CurrentAccountManager;
import com.medzone.cloud.module.CloudMeasureModule;
import com.medzone.cloud.ui.adapter.viewholder.DeviceViewHolder;
import com.medzone.framework.data.bean.imp.GroupMember;
import com.medzone.framework.module.ModuleStatus;
import com.medzone.mcloud.R.drawable;
import com.medzone.mcloud.R.layout;
import com.medzone.mcloud.R.string;

/**
 * @author ChenJunQi.
 * 
 */
public class DeviceGridViewAdapter extends BaseAdapter {

	private List<CloudMeasureModule<?>> list;
	private List<CloudMeasureModule<?>> displayList = new ArrayList<CloudMeasureModule<?>>();

	private Context context;
	private GroupMember groupmember;

	public DeviceGridViewAdapter(Context context, GroupMember groupmember) {
		this.context = context;
		this.groupmember = groupmember;
	}

	public void setContent(List<CloudMeasureModule<?>> list) {
		this.list = list;
		initDisplayList();
		notifyDataSetChanged();
	}

	public List<CloudMeasureModule<?>> getDisplayList() {
		return displayList;
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
				layout.gridview_item_equipment, null);
		itemView.setTag(new DeviceViewHolder(itemView, groupmember));
		return itemView;
	}

	private void fillView(View view, final Object item) {
		DeviceViewHolder viewHolder = (DeviceViewHolder) view.getTag();
		viewHolder.fillFromItem(item);
	}

	private void initDisplayList() {
		displayList.clear();
		if (list != null) {
			for (CloudMeasureModule<?> module : list) {
				if (module.getModuleStatus() == ModuleStatus.DISPLAY) {
					displayList.add(module);
				}
			}
		}
		if (groupmember.getAccountID().equals(
				CurrentAccountManager.getCurAccount().getAccountID())) {
			displayList.add(getPlaceholderModule());
		}

		if (displayList.size() % 2 == 1) {
			displayList.add(getPlaceholderModule2());
		}
	}

	public CloudMeasureModule<?> getPlaceholderModule() {

		@SuppressWarnings("rawtypes")
		CloudMeasureModule<?> module = new CloudMeasureModule() {

			@Override
			protected AbstractUseTaskCacheController createCacheController() {
				// TODO Auto-generated method stub
				return null;
			}

		};
		module.init(context, null, null);
		module.setDrawable(drawable.monitor_ic_add);
		module.setName(string.action_add);
		return module;
	}

	public CloudMeasureModule<?> getPlaceholderModule2() {
		@SuppressWarnings("rawtypes")
		CloudMeasureModule<?> module = new CloudMeasureModule() {

			@Override
			protected AbstractUseTaskCacheController createCacheController() {
				// TODO Auto-generated method stub
				return null;
			}

		};
		module.init(context, null, null);
		module.setDrawable(0);
		module.setName(string.place_holder);
		return module;
	}

}
package com.medzone.cloud.ui.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.medzone.cloud.data.CurrentAccountManager;
import com.medzone.cloud.ui.adapter.viewholder.BaseViewHolder;
import com.medzone.cloud.ui.adapter.viewholder.ChooseCarriedViewHolder;
import com.medzone.framework.data.bean.imp.Account;
import com.medzone.framework.data.bean.imp.Group;
import com.medzone.framework.data.bean.imp.GroupMember;
import com.medzone.mcloud.R;
import com.medzone.mcloud.R.drawable;
import com.medzone.mcloud.R.id;

public class ChooseCarriedExpandableAdapter extends BaseExpandableListAdapter {

	private static final int VIEW_TYPE_COUNT = 3;

	public static final int ITEM_TYPE_NORMAL = 0;
	public static final int ITEM_TYPE_SERVICE = 1;
	public static final int ITEM_TYPE_EXTRA_CAREFUL = 2;

	private Context context;
	private List<String> groupList = new ArrayList<String>();
	private List<List<?>> childData = new ArrayList<List<?>>();
	private int[] resGroupIcon = { drawable.group_ic_mygroup,
			drawable.group_ic_servicegroup, drawable.group_ic_special };

	private boolean isDisplayCareList;

	public ChooseCarriedExpandableAdapter(Context context) {
		super();
		this.context = context;

		List<Group> normalList = new ArrayList<Group>();
		List<Group> serviceList = new ArrayList<Group>();
		List<GroupMember> extraCareList = new ArrayList<GroupMember>();

		groupList.add(Group.TYPE_NORMAL, "我的群");
		groupList.add(Group.TYPE_SERVICE, "服务群");
		groupList.add(Group.TYPE_EXTRA_CAREFUL, "特别关心");
		childData.add(Group.TYPE_NORMAL, normalList);
		childData.add(Group.TYPE_SERVICE, serviceList);
		childData.add(Group.TYPE_EXTRA_CAREFUL, extraCareList);

	}

	public void setDisplayExtraCare(boolean isDisplay) {
		this.isDisplayCareList = isDisplay;
		if (!isDisplayCareList) {
			groupList.remove(Group.TYPE_EXTRA_CAREFUL);
			childData.remove(Group.TYPE_EXTRA_CAREFUL);
		} else {
			List<GroupMember> extraCareList = new ArrayList<GroupMember>();
			groupList.add(Group.TYPE_EXTRA_CAREFUL, "特别关心");
			childData.add(Group.TYPE_EXTRA_CAREFUL, extraCareList);
		}
		notifyDataSetChanged();
	}

	public void setGroupContent(List<Group> groupList) {

		childData.get(Group.TYPE_NORMAL).clear();
		childData.get(Group.TYPE_SERVICE).clear();

		List<Group> normalList = new ArrayList<Group>();
		List<Group> serviceList = new ArrayList<Group>();

		for (Group group : groupList) {
			if (group.getType() == Group.TYPE_NORMAL) {
				normalList.add(group);
			} else if (group.getType() == Group.TYPE_SERVICE) {
				if (!isMyServiceGroup(group))
					serviceList.add(group);
			}
		}
		childData.add(Group.TYPE_NORMAL, normalList);
		childData.add(Group.TYPE_SERVICE, serviceList);

		notifyDataSetChanged();

	}

	private boolean isMyServiceGroup(Group group) {
		if (group == null)
			return false;
		if (group.getCreatorID() == null)
			return false;
		Account account = CurrentAccountManager.getCurAccount();
		if (account == null)
			return false;
		if (account.getAccountID() == null)
			return false;
		if (group.getCreatorID().intValue() == account.getAccountID()
				.intValue())
			return true;
		return false;
	}

	public void setExtraCareFulContent(List<GroupMember> extraCarefulList) {
		childData.get(Group.TYPE_EXTRA_CAREFUL).clear();
		childData.add(Group.TYPE_EXTRA_CAREFUL, extraCarefulList);

		notifyDataSetChanged();
	}

	public Object getChild(int groupPosition, int childPosition) {
		return childData.get(groupPosition).get(childPosition);
	}

	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	public int getChildrenCount(int groupPosition) {
		return childData.get(groupPosition).size();
	}

	public View getChildView(final int groupPosition, final int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {

		int viewType = getChildType(groupPosition, childPosition);
		View itemView = null;

		if (convertView != null) {
			itemView = convertView;
		} else {
			itemView = inflateGroupChildrenView(viewType);
		}
		fillView(itemView, getChild(groupPosition, childPosition));
		return itemView;
	}

	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		View itemView = null;

		if (convertView != null) {
			itemView = convertView;
		} else {
			itemView = inflateGroupLayout(groupPosition);
		}
		fillView(itemView, groupPosition, isExpanded);
		return itemView;
	}

	@Deprecated
	private void fillView(View itemView, int groupPosition, boolean isExpanded) {
		ImageView ivGroupIcon = (ImageView) itemView
				.findViewById(id.group_top_imageview);
		ivGroupIcon.setImageResource(resGroupIcon[groupPosition]);
		TextView title = (TextView) itemView
				.findViewById(R.id.group_top_textView);
		title.setText(getGroup(groupPosition).toString());
		ImageView image = (ImageView) itemView
				.findViewById(R.id.group_top_orientation);

		if (isExpanded) {
			image.setBackgroundResource(R.drawable.group_ic_pullup);
		} else {
			image.setBackgroundResource(R.drawable.group_ic_pulldown);
		}

	}

	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	public Object getGroup(int groupPosition) {
		return groupList.get(groupPosition);
	}

	public int getGroupCount() {
		return groupList.size();

	}

	public boolean hasStableIds() {
		return true;
	}

	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}

	private View inflateGroupLayout(int groupPosition) {
		View itemView = null;
		switch (groupPosition) {
		case Group.TYPE_NORMAL:
			itemView = LayoutInflater.from(context).inflate(
					R.layout.list_item_group_myself, null);
			break;
		case Group.TYPE_SERVICE:
			itemView = LayoutInflater.from(context).inflate(
					R.layout.list_item_group_service, null);
			break;
		case Group.TYPE_EXTRA_CAREFUL:
			itemView = LayoutInflater.from(context).inflate(
					R.layout.list_item_careful, null);
			break;
		default:
			break;
		}
		return itemView;
	}

	private View inflateGroupChildrenView(int viewType) {

		View itemView = null;
		switch (viewType) {

		case ITEM_TYPE_NORMAL:
			itemView = LayoutInflater.from(context).inflate(
					R.layout.list_item_group_child_normal, null);
			itemView.setTag(new ChooseCarriedViewHolder(itemView));
			break;
		case ITEM_TYPE_SERVICE:
			itemView = LayoutInflater.from(context).inflate(
					R.layout.list_item_group_child_normal, null);
			itemView.setTag(new ChooseCarriedViewHolder(itemView));
			break;
		// 目前不需要给特别关心的人进行分享
		// case ITEM_TYPE_EXTRA_CAREFUL:
		// itemView = LayoutInflater.from(context).inflate(
		// R.layout.list_item_group_child_extra_careful, null);
		// itemView.setTag(new GroupExtraCarefulViewHolder(itemView));
		// break;

		default:
			break;
		}
		return itemView;
	}

	private void fillView(View view, Object item) {
		BaseViewHolder viewHolder = (BaseViewHolder) view.getTag();
		viewHolder.fillFromItem(item);
	}

	@Override
	public int getChildTypeCount() {
		return VIEW_TYPE_COUNT + super.getChildTypeCount();
	}

	@Override
	public int getChildType(int groupPosition, int childPosition) {

		if (groupPosition == Group.TYPE_NORMAL) {
			return ITEM_TYPE_NORMAL;
		} else if (groupPosition == Group.TYPE_SERVICE) {
			return ITEM_TYPE_SERVICE;
		} else if (groupPosition == Group.TYPE_EXTRA_CAREFUL) {
			return ITEM_TYPE_EXTRA_CAREFUL;
		} else {
			return 0;
		}
	}

}

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

import com.medzone.cloud.data.UnreadMessageCenter;
import com.medzone.cloud.ui.adapter.viewholder.BaseViewHolder;
import com.medzone.cloud.ui.adapter.viewholder.GroupExtraCarefulViewHolder;
import com.medzone.cloud.ui.adapter.viewholder.GroupServiceViewHolder;
import com.medzone.cloud.ui.adapter.viewholder.GroupViewHolder;
import com.medzone.framework.Log;
import com.medzone.framework.data.bean.imp.Group;
import com.medzone.framework.data.bean.imp.GroupMember;
import com.medzone.mcloud.R;
import com.medzone.mcloud.R.drawable;
import com.medzone.mcloud.R.id;

public class GroupExpandableAdapter extends BaseExpandableListAdapter {

	private static final int VIEW_TYPE_COUNT = 6;

	private boolean isNormalGroupEmpty;
	private boolean isServiceGroupEmpty;
	private boolean isExtraCareEmpty;

	// 0-2与position 依赖
	public static final int ITEM_TYPE_NORMAL = 0;
	public static final int ITEM_TYPE_SERVICE = 1;
	public static final int ITEM_TYPE_EXTRA_CAREFUL = 2;
	// 3-5与position 依赖，并依赖于标志位状态
	public static final int ITEM_TYPE_NORMAL_EMPTY = 3;
	public static final int ITEM_TYPE_SERVICE_EMPTY = 4;
	public static final int ITEM_TYPE_EXTRA_CAREFUL_EMPTY = 5;

	public static final String ITEM_TYPE_NORMAL_NAME = "我的群";
	public static final String ITEM_TYPE_SERVICE_NAME = "服务群";
	public static final String ITEM_TYPE_EXTRA_CAREFUL_NAME = "特别关心";

	private Context context;
	private List<String> groupList = new ArrayList<String>();
	private List<List<?>> childData = new ArrayList<List<?>>();
	private int[] resGroupIcon = { drawable.group_ic_mygroup,
			drawable.group_ic_servicegroup, drawable.group_ic_special };

	private boolean isDisplayCareList;

	// 占位符对象
	private Group placeHolderGroup = new Group();
	// 占位符对象
	private GroupMember placeHolderGroupMember = new GroupMember();

	public GroupExpandableAdapter(Context context) {
		super();
		this.context = context;

		List<Group> normalList = new ArrayList<Group>();
		List<Group> serviceList = new ArrayList<Group>();
		List<GroupMember> extraCareList = new ArrayList<GroupMember>();

		groupList.add(Group.TYPE_NORMAL, ITEM_TYPE_NORMAL_NAME);
		groupList.add(Group.TYPE_SERVICE, ITEM_TYPE_SERVICE_NAME);
		groupList.add(Group.TYPE_EXTRA_CAREFUL, ITEM_TYPE_EXTRA_CAREFUL_NAME);

		childData.add(Group.TYPE_NORMAL, normalList);
		childData.add(Group.TYPE_SERVICE, serviceList);
		childData.add(Group.TYPE_EXTRA_CAREFUL, extraCareList);

	}

	/**
	 * 
	 * 设置是否特别关心栏目
	 * 
	 * @param isDisplay
	 */
	public void setDisplayExtraCare(boolean isDisplay) {
		this.isDisplayCareList = isDisplay;
		if (!isDisplayCareList) {
			groupList.remove(Group.TYPE_EXTRA_CAREFUL);
			childData.remove(Group.TYPE_EXTRA_CAREFUL);
		} else {
			List<GroupMember> extraCareList = new ArrayList<GroupMember>();
			groupList.add(Group.TYPE_EXTRA_CAREFUL,
					ITEM_TYPE_EXTRA_CAREFUL_NAME);
			childData.add(Group.TYPE_EXTRA_CAREFUL, extraCareList);
		}
		notifyDataSetChanged();
	}

	/**
	 * 传入群组数据，包括服务群，普通群
	 * 
	 * @param groupList
	 */
	public void setGroupContent(List<Group> groupList) {

		childData.get(Group.TYPE_NORMAL).clear();
		childData.get(Group.TYPE_SERVICE).clear();

		List<Group> normalList = new ArrayList<Group>();
		List<Group> serviceList = new ArrayList<Group>();

		for (Group group : groupList) {
			if (group.getType() == Group.TYPE_NORMAL) {
				normalList.add(group);
			} else if (group.getType() == Group.TYPE_SERVICE) {
				serviceList.add(group);
			}
		}
		if (normalList.isEmpty()) {
			normalList.add(placeHolderGroup);
			isNormalGroupEmpty = true;
		} else {
			isNormalGroupEmpty = false;
		}
		childData.add(Group.TYPE_NORMAL, normalList);

		if (serviceList.isEmpty()) {
			isServiceGroupEmpty = true;
			serviceList.add(placeHolderGroup);
		} else {
			isServiceGroupEmpty = false;
		}
		childData.add(Group.TYPE_SERVICE, serviceList);

		notifyDataSetChanged();

	}

	/**
	 * 传入特别关心列表
	 * 
	 * @param extraCarefulList
	 */
	public void setExtraCareFulContent(List<GroupMember> extraCarefulList) {
		childData.get(Group.TYPE_EXTRA_CAREFUL).clear();
		if (extraCarefulList.isEmpty()) {
			isExtraCareEmpty = true;
			extraCarefulList.add(placeHolderGroupMember);
		} else {
			isExtraCareEmpty = false;
		}
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

		// XXX 当列表项不一致时，无法复用缓存View视图结构
		// if (convertView != null) {
		// itemView = convertView;
		// } else {
		itemView = inflateGroupLayout(groupPosition);
		// }
		fillView(itemView, groupPosition, isExpanded);
		return itemView;
	}

	// TODO 需要增加ViewHolder优化性能
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
		ImageView ivNews = (ImageView) itemView.findViewById(id.iv_new_message);

		refreshNewMessage(groupPosition, ivNews);
	}

	private void refreshNewMessage(int groupPosition, ImageView iv) {

		int size = 0;

		if (groupPosition == ITEM_TYPE_NORMAL) {

			size = UnreadMessageCenter
					.getChatMessageCollectLength(Group.TYPE_NORMAL);
			Log.w("refreshNewMessage#TYPE_NORMAL:" + size);
			hideOrShowNewMessageIcon(size, iv);
		} else if (groupPosition == ITEM_TYPE_SERVICE) {
			size = UnreadMessageCenter
					.getChatMessageCollectLength(Group.TYPE_SERVICE);
			Log.w("refreshNewMessage#ITEM_TYPE_SERVICE:" + size);
			hideOrShowNewMessageIcon(size, iv);
		} else {
			Log.v("#特别关心列表无需刷新");
			// 特别关心不存在消息红点
		}
	}

	private void hideOrShowNewMessageIcon(int size, ImageView iv) {
		if (size == 0) {
			iv.setVisibility(View.GONE);
		} else {
			iv.setVisibility(View.VISIBLE);
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
			itemView.setTag(new GroupViewHolder(itemView));
			break;
		case ITEM_TYPE_SERVICE:
			itemView = LayoutInflater.from(context).inflate(
					R.layout.list_item_group_child_service, null);
			itemView.setTag(new GroupServiceViewHolder(itemView));
			break;

		case ITEM_TYPE_EXTRA_CAREFUL:
			itemView = LayoutInflater.from(context).inflate(
					R.layout.list_item_group_child_extracare, null);
			itemView.setTag(new GroupExtraCarefulViewHolder(itemView));
			break;
		case ITEM_TYPE_NORMAL_EMPTY:
			itemView = LayoutInflater.from(context).inflate(
					R.layout.list_item_group_child_normal_empty, null);
			break;
		case ITEM_TYPE_SERVICE_EMPTY:
			itemView = LayoutInflater.from(context).inflate(
					R.layout.list_item_group_child_service_empty, null);
			break;
		case ITEM_TYPE_EXTRA_CAREFUL_EMPTY:
			itemView = LayoutInflater.from(context).inflate(
					R.layout.list_item_group_child_extracare_empty, null);
			break;
		default:
			break;
		}
		return itemView;
	}

	private void fillView(View view, Object item) {
		BaseViewHolder viewHolder = (BaseViewHolder) view.getTag();
		if (viewHolder != null) {
			viewHolder.fillFromItem(item);
		}
	}

	@Override
	public int getChildTypeCount() {
		return VIEW_TYPE_COUNT + super.getChildTypeCount();
	}

	@Override
	public int getChildType(int groupPosition, int childPosition) {

		switch (groupPosition) {
		case ITEM_TYPE_NORMAL:
			if (isNormalGroupEmpty) {
				return ITEM_TYPE_NORMAL_EMPTY;
			}
			return ITEM_TYPE_NORMAL;
		case ITEM_TYPE_SERVICE:
			if (isServiceGroupEmpty) {
				return ITEM_TYPE_SERVICE_EMPTY;
			}
			return ITEM_TYPE_SERVICE;
		case ITEM_TYPE_EXTRA_CAREFUL:
			if (isExtraCareEmpty) {
				return ITEM_TYPE_EXTRA_CAREFUL_EMPTY;
			}
			return ITEM_TYPE_EXTRA_CAREFUL;
		default:
			return 0;
		}
	}

}

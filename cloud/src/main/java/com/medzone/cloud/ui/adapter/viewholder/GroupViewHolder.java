package com.medzone.cloud.ui.adapter.viewholder;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.medzone.cloud.CloudImageLoader;
import com.medzone.cloud.data.CurrentAccountManager;
import com.medzone.cloud.data.TemporaryData;
import com.medzone.cloud.data.UnreadMessageCenter;
import com.medzone.cloud.ui.GroupChatActivity;
import com.medzone.cloud.ui.GroupDatumActivity;
import com.medzone.framework.data.bean.imp.Group;
import com.medzone.framework.view.RoundedImageView;
import com.medzone.mcloud.R.id;
import com.medzone.mcloud.R.string;

public class GroupViewHolder extends BaseViewHolder {

	private View rootView;
	private TextView tvGroupName, tvRemark;
	private RoundedImageView ivGroupIcon;
	private ImageView ivNews;
	private Context context;

	/**
	 * @param rootView
	 */
	public GroupViewHolder(View rootView) {
		super(rootView);
		context = rootView.getContext();
		this.rootView = rootView;
	}

	@Override
	public void init(View view) {
		tvGroupName = (TextView) view.findViewById(id.tv_group_name);
		tvRemark = (TextView) view.findViewById(id.tv_remark);
		ivGroupIcon = (RoundedImageView) view
				.findViewById(id.image_child_avator);
		ivNews = (ImageView) view.findViewById(id.iv_new_message);
	}

	@Override
	public void fillFromItem(Object item) {
		final Group group = (Group) item;
		// final GroupMember member;

		tvRemark.setVisibility(View.GONE);

		// if (item instanceof Group) {

		if (isGroupOwner(group)) {
			tvRemark.setVisibility(View.VISIBLE);
			tvRemark.setText(string.group_owner);
		}

		rootView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				TemporaryData.save(Group.class.getName(), group);
				intent.setClass(context, GroupChatActivity.class);
				context.startActivity(intent);
			}
		});

		ivGroupIcon.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				TemporaryData.save(Group.class.getName(), group);
				intent.setClass(context, GroupDatumActivity.class);
				context.startActivity(intent);
			}
		});

		tvGroupName.setText(group.getName());
		if (group.getHeadPortRait() != null) {
			CloudImageLoader.getInstance().getImageLoader()
					.displayImage(group.getHeadPortRait(), ivGroupIcon);
		}

		// } else if (item instanceof GroupMember) {
		// member = (GroupMember) item;
		//
		// rootView.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// Toast.makeText(context, "Dev:跳转到他人健康中心", Toast.LENGTH_SHORT)
		// .show();
		// }
		// });
		// ivGroupIcon.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// Toast.makeText(context, "Dev:跳转到个人详细资料", Toast.LENGTH_SHORT)
		// .show();
		// Intent intent = getIntent(member);
		// if (intent != null) {
		// context.startActivity(intent);
		// }
		// }
		// });
		//
		// tvGroupName.setText(CurrentAccountManager.getCurAccount()
		// .getFriendsDisplay(member));
		// CloudImageLoader.getInstance().getImageLoader().displayImage(member.getHeadPortRait(),
		// ivGroupIcon);
		// }
		refreshNews(group);
	}

	// private Intent getIntent(GroupMember member) {
	// TemporaryData.save(GroupMember.class.getName(), member);
	// Intent intent = new Intent();
	// intent.setClass(context, GroupPersonDetailActivity.class);
	// return intent;
	// }

	private boolean isGroupOwner(Group group) {
		return group.equals(CurrentAccountManager.getCurAccount());
	}

	private void refreshNews(Group group) {
		int size = UnreadMessageCenter.getChatMessageCollectLength(
				Group.TYPE_NORMAL, group.getGroupID(), -1);

		if (size == 0) {
			ivNews.setVisibility(View.GONE);
		} else {
			ivNews.setVisibility(View.VISIBLE);
		}
	}

}

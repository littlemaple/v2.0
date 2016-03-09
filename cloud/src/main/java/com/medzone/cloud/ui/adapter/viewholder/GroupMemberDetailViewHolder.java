package com.medzone.cloud.ui.adapter.viewholder;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.medzone.cloud.data.CurrentAccountManager;
import com.medzone.cloud.data.TemporaryData;
import com.medzone.cloud.data.UnreadMessageCenter;
import com.medzone.cloud.data.helper.GroupHelper;
import com.medzone.cloud.ui.GroupChatActivity;
import com.medzone.cloud.ui.GroupMemberActivity;
import com.medzone.cloud.ui.GroupPersonDetailActivity;
import com.medzone.cloud.ui.adapter.GroupMemberAdapter;
import com.medzone.framework.data.bean.imp.Group;
import com.medzone.framework.data.bean.imp.GroupMember;
import com.medzone.framework.network.NetworkClientResult;
import com.medzone.framework.task.BaseResult;
import com.medzone.framework.task.TaskHost;
import com.medzone.mcloud.R.id;
import com.medzone.mcloud.R.string;
import com.nostra13.universalimageloader.core.ImageLoader;

public class GroupMemberDetailViewHolder extends BaseViewHolder {

	private TextView nameTv;
	private ImageView iconIv, iconExtraCareful, ivNews;
	private View rootView;
	private Context context;
	private GroupMemberAdapter adapter;

	/**
	 * @param rootView
	 */
	public GroupMemberDetailViewHolder(View rootView, GroupMemberAdapter adapter) {
		super(rootView);
		this.adapter = adapter;
		this.rootView = rootView;
	}

	@Override
	public void init(View view) {
		rootView = view;
		context = view.getContext();
		nameTv = (TextView) view.findViewById(id.tv_group_name);
		iconIv = (ImageView) view.findViewById(id.iv_group_icon);
		ivNews = (ImageView) view.findViewById(id.iv_new_message);
		iconExtraCareful = (ImageView) view
				.findViewById(id.iv_extra_careful_member);
	}

	@Override
	public void fillFromItem(Object item) {
		final GroupMember member = (GroupMember) item;
		final Group bindGroup = ((GroupMemberActivity) context).getBindGroup();
		member.setBindGroup(bindGroup);
		if (member.isCare()) {
			iconExtraCareful.setVisibility(View.VISIBLE);
		} else {
			iconExtraCareful.setVisibility(View.GONE);
		}
		nameTv.setText(CurrentAccountManager.getCurAccount().getFriendsDisplay(
				member));
		ImageLoader.getInstance()
				.displayImage(member.getHeadPortRait(), iconIv);
		refreshNews(member);
		iconIv.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				TemporaryData.save(GroupMember.class.getName(), member);
				context.startActivity(new Intent(context,
						GroupPersonDetailActivity.class));
			}
		});
		rootView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				TemporaryData.save(GroupMember.class.getName(), member);
				context.startActivity(new Intent(context,
						GroupChatActivity.class));
			}
		});
		rootView.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View arg0) {
				showDelDialog(member);
				return false;
			}
		});
	}

	private void refreshNews(GroupMember member) {
		int size = UnreadMessageCenter.getChatMessageCollectLength(
				Group.TYPE_SERVICE, member.getBindGroup().getGroupID(),
				member.getAccountID());
		if (size == 0) {
			ivNews.setVisibility(View.GONE);
		} else {
			ivNews.setVisibility(View.VISIBLE);
		}
	}

	public void showDelDialog(final GroupMember member) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setMessage("是否确认删除该成员");
		builder.setPositiveButton(
				context.getResources().getString(string.action_confirm),
				new OnClickListener() {

					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						doKickOutMember(member);
					}
				});
		builder.setNegativeButton(
				context.getResources().getString(string.action_cancel), null);
		builder.create().show();
	}

	private void doKickOutMember(final GroupMember member) {
		GroupHelper.groupMemberDel(context, CurrentAccountManager
				.getCurAccount().getAccessToken(), member.getGroupID(), member
				.getAccountID(), new TaskHost() {
			@Override
			public void onPostExecute(int requestCode, BaseResult result) {
				super.onPostExecute(requestCode, result);
				if (result.isSuccess()) {
					if (result.isServerDisposeSuccess()) {
						adapter.refreshAfterDel(member);
					} else {

						Toast.makeText(
								context,
								((NetworkClientResult) result)
										.getResponseResult().toString(),
								Toast.LENGTH_SHORT).show();
					}
				} else {
					Toast.makeText(
							context,
							context.getResources().getString(
									string.error_net_connect),
							Toast.LENGTH_SHORT).show();

				}

			}
		});
	}

}

package com.medzone.cloud.ui.adapter.viewholder;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.medzone.cloud.CloudImageLoader;
import com.medzone.cloud.Constants;
import com.medzone.cloud.data.CurrentAccountManager;
import com.medzone.cloud.data.TemporaryData;
import com.medzone.cloud.data.helper.GroupHelper;
import com.medzone.cloud.ui.GroupChatActivity;
import com.medzone.cloud.ui.GroupDatumActivity;
import com.medzone.cloud.ui.GroupMemberActivity;
import com.medzone.cloud.ui.GroupPersonDetailActivity;
import com.medzone.cloud.ui.GroupServiceForMemberDetailActivity;
import com.medzone.cloud.ui.GroupServiceForOwnerDetailActivity;
import com.medzone.cloud.ui.HealthCentreWebViewActivity;
import com.medzone.cloud.ui.dialog.error.ErrorDialogUtil;
import com.medzone.framework.data.bean.imp.Account;
import com.medzone.framework.data.bean.imp.Group;
import com.medzone.framework.data.bean.imp.GroupMember;
import com.medzone.framework.errorcode.ProxyCode.LocalError;
import com.medzone.framework.errorcode.ProxyErrorCode;
import com.medzone.framework.network.NetworkClientResult;
import com.medzone.framework.task.BaseResult;
import com.medzone.framework.task.TaskHost;
import com.medzone.framework.view.RoundedImageView;
import com.medzone.mcloud.R.id;
import com.medzone.mcloud.R.string;

public class GroupExtraCarefulViewHolder extends BaseViewHolder {

	private View rootView;
	private TextView nameTv, tvExtraCarefulEvent;
	private RoundedImageView iconIv;
	private Context context;
	private Account curAccount;

	public GroupExtraCarefulViewHolder(View rootView) {
		super(rootView);
		context = rootView.getContext();
		this.rootView = rootView;
		curAccount = CurrentAccountManager.getCurAccount();
	}

	@Override
	public void init(View view) {
		tvExtraCarefulEvent = (TextView) view.findViewById(id.tv_event);
		nameTv = (TextView) view.findViewById(id.tv_member_name);
		iconIv = (RoundedImageView) view.findViewById(id.image_child_avator);

	}

	@Override
	public void fillFromItem(Object item) {
		final Group group;
		final GroupMember member;

		// 通群/服务群
		if (item instanceof Group) {
			group = (Group) item;
			nameTv.setText(group.getName());

			rootView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO 代码待重构
					Intent intent = getIntent(group);
					if (intent != null)
						context.startActivity(intent);
				}
			});

			iconIv.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO 代码待重构
					Intent intent = getGroupIntent(group);
					if (intent != null) {
						context.startActivity(intent);
					}
				}
			});

		} else
		// 特别关心
		if (item instanceof GroupMember) {
			member = (GroupMember) item;
			final GroupMember authzMember = CurrentAccountManager
					.getAuthorizedMember(member);

			// fill View
			nameTv.setText(curAccount.getFriendsDisplay(member));
			CloudImageLoader.getInstance().getImageLoader()
					.displayImage(member.getHeadPortRait(), iconIv);

			if (member.getEventContent() != null) {
				tvExtraCarefulEvent.setText(member.getLastEventContent()
						.toString());
			} else {
				tvExtraCarefulEvent.setText(null);
			}

			rootView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					prepareLoadReserveAuthoForMe(member);
				}
			});
			iconIv.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent intent = getIntent(member);
					if (intent != null) {
						context.startActivity(intent);
					}
				}
			});

		}

	}

	private void prepareLoadReserveAuthoForMe(final GroupMember groupMember) {

		// if (groupMember.isMeasureForMe() == null) {
		String accessToken = CurrentAccountManager.getCurAccount()
				.getAccessToken();
		GroupHelper.doGetReserveAuthorizedListTask(context, accessToken,
				groupMember, new TaskHost() {
					@Override
					public void onPostExecute(int requestCode, BaseResult result) {
						super.onPostExecute(requestCode, result);

						if (result.isSuccess()) {

							NetworkClientResult res = (NetworkClientResult) result;
							if (res.isServerDisposeSuccess()) {
								if (groupMember.isViewHistoryForMe()) {
									jump2HealthCentreActivity(groupMember);
								} else {
									ErrorDialogUtil.showErrorDialog(context,
											ProxyErrorCode.TYPE_GROUP,
											LocalError.CODE_12404, true);
								}
							} else {
								Toast.makeText(context,
										res.getResponseResult().toString(),
										Toast.LENGTH_SHORT).show();
							}
						} else {
							Toast.makeText(context, string.error_net_connect,
									Toast.LENGTH_SHORT).show();
						}

					}
				});
		// } else {
		// postLoadClickEvent(isView);
		// }
	}

	private void jump2HealthCentreActivity(GroupMember member) {
		// CloudMeasureModuleCentreRoot.doGetModuleSpec(memberAccount, context,
		// false);
		// TemporaryData.save(Account.class.getName(), memberAccount);
		// Intent intent = new Intent(context, HealthCentreActivity.class);
		// context.startActivity(intent);

		TemporaryData.save(Constants.TEMPORARYDATA_KEY_VIEW_GROUP_MEMBER,
				member);
		Intent intent = new Intent(context, HealthCentreWebViewActivity.class);
		context.startActivity(intent);
	}

	// public void doGetOtherAccount(GroupMember groupMember) {
	//
	//
	//
	// AccountHelper.doGetOtherAccountTask(groupMember.getAccountID(),
	// new TaskHost() {
	// @Override
	// public void onPostExecute(int requestCode, BaseResult result) {
	// super.onPostExecute(requestCode, result);
	// if (result.isSuccess()) {
	// NetworkClientResult res = (NetworkClientResult) result;
	// if (res.isServerDisposeSuccess()) {
	// Account tempAccount = Account
	// .createAccount((NetworkClientResult) result);
	//
	// } else {
	// Toast.makeText(context,
	// res.getResponseResult().toString(),
	// Toast.LENGTH_SHORT).show();
	// }
	// } else {
	// Toast.makeText(context, R.string.error_net_connect,
	// Toast.LENGTH_SHORT).show();
	// }
	// }
	// });
	// }

	private Intent getIntent(Group group) {

		Intent intent = new Intent();
		TemporaryData.save(Group.class.getName(), group);

		if (group.getType() == Group.TYPE_NORMAL) {
			intent.setClass(context, GroupChatActivity.class);
		} else if (group.getType() == Group.TYPE_SERVICE) {

			if (isGroupOwner(group)) {
				intent.setClass(context, GroupMemberActivity.class);
			} else {
				intent.setClass(context, GroupChatActivity.class);
			}
		}

		return intent;
	}

	private Intent getIntent(GroupMember member) {
		TemporaryData.save(GroupMember.class.getName(), member);
		Intent intent = new Intent();
		intent.setClass(context, GroupPersonDetailActivity.class);

		return intent;
	}

	private boolean isGroupOwner(Group group) {
		return group.equals(CurrentAccountManager.getCurAccount());
	}

	private Intent getGroupIntent(Group group) {
		Intent intent = new Intent();
		TemporaryData.save(Group.class.getName(), group);

		if (group.getType() == Group.TYPE_NORMAL) {
			intent.setClass(context, GroupDatumActivity.class);
		} else if (group.getType() == Group.TYPE_SERVICE) {

			if (isGroupOwner(group)) {
				intent.setClass(context,
						GroupServiceForOwnerDetailActivity.class);
			} else {
				intent.setClass(context,
						GroupServiceForMemberDetailActivity.class);
			}
		}
		return intent;
	}
}

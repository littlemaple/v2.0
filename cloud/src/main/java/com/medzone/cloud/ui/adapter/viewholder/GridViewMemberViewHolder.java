/**
 * 
 */
package com.medzone.cloud.ui.adapter.viewholder;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Vibrator;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.medzone.cloud.CloudImageLoader;
import com.medzone.cloud.Constants;
import com.medzone.cloud.data.CurrentAccountManager;
import com.medzone.cloud.data.TemporaryData;
import com.medzone.cloud.data.helper.GroupHelper;
import com.medzone.cloud.ui.GroupDatumActivity;
import com.medzone.cloud.ui.GroupInviteActivity;
import com.medzone.cloud.ui.GroupPersonDetailActivity;
import com.medzone.cloud.ui.SettingPersonalInfoActivity;
import com.medzone.cloud.ui.dialog.error.ErrorDialogUtil;
import com.medzone.cloud.ui.fragment.group.GroupMemberManagerFragment;
import com.medzone.framework.data.bean.imp.Group;
import com.medzone.framework.data.bean.imp.GroupMember;
import com.medzone.framework.errorcode.ProxyErrorCode;
import com.medzone.framework.network.NetworkClientResult;
import com.medzone.framework.task.BaseResult;
import com.medzone.framework.task.TaskHost;
import com.medzone.framework.view.RoundedImageView;
import com.medzone.mcloud.R.drawable;
import com.medzone.mcloud.R.id;
import com.medzone.mcloud.R.string;

/**
 * @author ChenJunQi.
 * 
 */
public class GridViewMemberViewHolder extends BaseViewHolder {

	private View rootView;
	private Context context;

	private RoundedImageView ivIconMember;
	private ImageView ivDelMember, ivOwnerMember, ivExtraCareful;
	private TextView tvNameMember;
	private RelativeLayout rlContainer;

	public GridViewMemberViewHolder(View rootView) {
		super(rootView);
		this.rootView = rootView;
		this.context = rootView.getContext();
	}

	@Override
	public void init(View view) {
		tvNameMember = (TextView) view.findViewById(id.tv_name_member);
		ivIconMember = (RoundedImageView) view.findViewById(id.iv_icon_member);
		ivDelMember = (ImageView) view.findViewById(id.iv_del_member);
		ivExtraCareful = (ImageView) view
				.findViewById(id.iv_extra_careful_member);
		ivOwnerMember = (ImageView) view.findViewById(id.iv_owner_member);
		rlContainer = (RelativeLayout) view.findViewById(id.container);
	}

	public void fillFromItem(Object item) {
		final GroupMember member = (GroupMember) item;
		final Group group = ((GroupDatumActivity) context).getBondGroup();

		// real member
		if (member.getTag() == null) {
			member.setBindGroup(group);
			tvNameMember.setText(CurrentAccountManager.getCurAccount()
					.getFriendsDisplay(member));
			CloudImageLoader.getInstance().getImageLoader()
					.displayImage(member.getHeadPortRait(), ivIconMember);
			showDelIcon(group, member);
			showGroupOwnerIcon(group, member);
			if (member.isCare()) {
				ivExtraCareful.setVisibility(View.VISIBLE);
			} else {
				ivExtraCareful.setVisibility(View.GONE);
			}
			registerLongClickEvent(group, member);

			// fake member
		} else if (TextUtils.equals(member.getTag(),
				Constants.OWNER_MANAGER_TAG)) {
			ivIconMember.setImageResource(drawable.group_ic_addmember);
			tvNameMember.setText(null);
			rootView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent intent = new Intent();
					intent.putExtra(Group.NAME_FIELD_GROUP_ID,
							member.getGroupID());
					intent.setClass(context, GroupInviteActivity.class);
					context.startActivity(intent);
				}
			});
		}
	};

	private void showGroupOwnerIcon(Group group, GroupMember member) {
		ivOwnerMember.setVisibility(View.GONE);
		if (group.equals(member)) {
			ivOwnerMember.setVisibility(View.VISIBLE);
			ivDelMember.setVisibility(View.GONE);
		}
	}

	private void showDelIcon(final Group group, final GroupMember member) {
		if (GroupMemberManagerFragment.isLongPressed) {

			OnClickListener listener = new OnClickListener() {

				@Override
				public void onClick(View v) {
					showConfirmDialog(group, member);
				}
			};
			ivDelMember.setVisibility(View.VISIBLE);
			ivDelMember.setOnClickListener(listener);
			// 避免删除状态时，勿操作点入个人资料
			rootView.setOnClickListener(null);
		} else {
			ivDelMember.setVisibility(View.GONE);
			ivDelMember.setOnClickListener(null);
			rootView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					if (member.equals(CurrentAccountManager.getCurAccount())) {
						Intent intent = new Intent(context,
								SettingPersonalInfoActivity.class);
						context.startActivity(intent);
					} else {
						TemporaryData.save(GroupMember.class.getName(), member);
						context.startActivity(new Intent(context,
								GroupPersonDetailActivity.class));
					}
				}
			});
		}
	}

	private void delMember(GroupMember delMember) {
		GroupMemberManagerFragment.removeMember(delMember);
		GroupMemberManagerFragment.notifyDataSetChanged();
	}

	private void registerLongClickEvent(Group group, GroupMember member) {
		// TODO group 跟account 比较?
		if (group.equals(CurrentAccountManager.getCurAccount())) {

			rootView.setOnLongClickListener(new OnLongClickListener() {

				@Override
				public boolean onLongClick(View v) {
					toggle();
					return true;
				}
			});
			// 禁止群主点击长按
			if (group.getCreatorID() == null)
				return;
			if (member.getAccountID() == null)
				return;
			if (group.getCreatorID().intValue() == member.getAccountID()
					.intValue()) {
				rlContainer.setOnLongClickListener(new OnLongClickListener() {

					@Override
					public boolean onLongClick(View v) {
						vibrate(500);
						return true;
					}
				});
			}
		}
	}

	private void vibrate(long milliseconds) {
		Vibrator vibrator = (Vibrator) context
				.getSystemService(Context.VIBRATOR_SERVICE);
		vibrator.vibrate(milliseconds);
	}

	private void toggle() {
		GroupMemberManagerFragment.toggleLongPressedState();
	}

	private void showConfirmDialog(final Group group, final GroupMember member) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setCancelable(true);
		builder.setMessage(string.group_del_confirm);
		builder.setPositiveButton(string.action_confirm,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						doKickOutMember(group, member);
					}
				});
		builder.setNegativeButton(string.action_cancel, null);
		builder.create().show();

	}

	private void doKickOutMember(final Group group, final GroupMember member) {
		GroupHelper.groupMemberDel(context, CurrentAccountManager
				.getCurAccount().getAccessToken(), group.getGroupID(), member
				.getAccountID(), new TaskHost() {
			@Override
			public void onPostExecute(int requestCode, BaseResult result) {
				super.onPostExecute(requestCode, result);
				if (result.isSuccess()) {
					NetworkClientResult res = (NetworkClientResult) result;
					if (result.isServerDisposeSuccess()) {
						delMember(member);
					} else {
						ErrorDialogUtil.showErrorDialog(context,
								ProxyErrorCode.TYPE_GROUP, res.getErrorCode(),
								true);
					}
				}
			}
		});
	}
}

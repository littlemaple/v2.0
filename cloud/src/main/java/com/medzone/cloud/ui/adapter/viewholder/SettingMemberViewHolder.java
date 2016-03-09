/**
 * 
 */
package com.medzone.cloud.ui.adapter.viewholder;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.medzone.cloud.CloudImageLoader;
import com.medzone.cloud.data.CurrentAccountManager;
import com.medzone.cloud.task.EditGroupMemberPermissionTask;
import com.medzone.cloud.ui.adapter.SettingMemberAdapter;
import com.medzone.cloud.ui.dialog.error.ErrorDialogUtil;
import com.medzone.cloud.ui.fragment.group.GroupMemberPermissionFragment;
import com.medzone.framework.data.bean.imp.GroupMember;
import com.medzone.framework.errorcode.ProxyErrorCode;
import com.medzone.framework.task.BaseResult;
import com.medzone.framework.task.TaskHost;
import com.medzone.mcloud.R.id;
import com.medzone.mcloud.R.string;

/**
 * @author ChenJunQi.
 * 
 */
public class SettingMemberViewHolder extends BaseViewHolder {

	private View rootView;
	private Context context;

	private ImageView iconImage;
	private TextView nameTv;
	private ImageView ivDelMember, ivExtraCarefulMember;

	public SettingMemberViewHolder(View rootView, SettingMemberAdapter adapter) {
		super(rootView);
		this.rootView = rootView;
		this.context = rootView.getContext();
	}

	@Override
	public void init(View view) {
		nameTv = (TextView) view.findViewById(id.tv_name_member);
		iconImage = (ImageView) view.findViewById(id.iv_icon_member);
		ivDelMember = (ImageView) view.findViewById(id.iv_del_member);
		ivExtraCarefulMember = (ImageView) view
				.findViewById(id.iv_extra_careful_member);
	}

	public void fillFromItem(Object item) {
		final GroupMember member = (GroupMember) item;

		nameTv.setText(!TextUtils.isEmpty(member.getRemark()) ? member
				.getRemark() : member.getNickname());
		CloudImageLoader.getInstance().getImageLoader()
				.displayImage(member.getHeadPortRait(), iconImage);
		if (member.isCare()) {
			ivExtraCarefulMember.setVisibility(View.VISIBLE);
		} else {
			ivExtraCarefulMember.setVisibility(View.GONE);
		}
		showDelIcon(member);

		rootView.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				toggle();
				return true;
			}
		});
	};

	private void showDelIcon(final GroupMember member) {
		if (GroupMemberPermissionFragment.isLongPressed) {

			ivDelMember.setVisibility(View.VISIBLE);
			ivDelMember.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View arg0) {
					showDialog(member);
				}
			});
		} else {
			ivDelMember.setVisibility(View.GONE);
			ivDelMember.setOnClickListener(null);
		}
	}

	public void jumpToTarget(Class<?> clz) {
		context.startActivity(new Intent(context, clz));
	}

	public void showDialog(final GroupMember member) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);

		if (!isMeasure())
			builder.setMessage(string.group_recall_view_permission_confirm);
		if (isMeasure())
			builder.setMessage(string.group_recall_measure_permission_confirm);
		builder.setNegativeButton(string.action_cancel, null);
		builder.setPositiveButton(string.action_confirm, new OnClickListener() {

			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				EditGroupMemberPermissionTask(member);
			}
		});
		builder.create().show();
	}

	public void EditGroupMemberPermissionTask(final GroupMember member) {
		EditGroupMemberPermissionTask task = null;
		if (!isMeasure()) {
			task = new EditGroupMemberPermissionTask(context,
					CurrentAccountManager.getCurAccount().getAccessToken(),
					member.getAccountID(), null, false, null, null, null);
		}
		if (isMeasure()) {
			task = new EditGroupMemberPermissionTask(context,
					CurrentAccountManager.getCurAccount().getAccessToken(),
					member.getAccountID(), null, null, null, false, null);
		}
		task.setTaskHost(new TaskHost() {
			@Override
			public void onPostExecute(int requestCode, BaseResult result) {
				super.onPostExecute(requestCode, result);
				if (result.isSuccess()) {
					if (result.isServerDisposeSuccess()) {
						delMember(member);
					} else {
						ErrorDialogUtil.showErrorDialog(context,
								ProxyErrorCode.TYPE_GROUP,
								result.getErrorCode(), true);
					}
				}
			}
		});
		task.execute();
	}

	private void delMember(GroupMember delMember) {
		GroupMemberPermissionFragment.removeMember(delMember);
		GroupMemberPermissionFragment.notifyDataSetChanged();
	}

	private void toggle() {
		GroupMemberPermissionFragment.toggleLongPressedState();
	}

	private boolean isMeasure() {
		if (GroupMemberPermissionFragment.isMeasure())
			return true;
		return false;
	}

}

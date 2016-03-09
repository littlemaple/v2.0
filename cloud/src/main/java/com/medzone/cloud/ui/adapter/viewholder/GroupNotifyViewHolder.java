/**
 * 
 */
package com.medzone.cloud.ui.adapter.viewholder;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.medzone.cloud.Constants;
import com.medzone.cloud.network.NetworkClientHelper;
import com.medzone.cloud.ui.GroupNotifyProcessActivity;
import com.medzone.framework.Log;
import com.medzone.framework.data.bean.imp.Account;
import com.medzone.framework.data.bean.imp.ServiceMessage;
import com.medzone.framework.view.RoundedImageView;
import com.medzone.mcloud.R;
import com.medzone.mcloud.R.drawable;
import com.medzone.mcloud.R.id;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * @author ChenJunQi.
 * 
 */
public class GroupNotifyViewHolder extends BaseViewHolder {

	private RoundedImageView ivMemberIcon;
	private TextView groupName;
	private TextView description;
	private TextView messageStatus;
	private Button btnDisposed;
	private TextView tvDisposed;

	private Context context;
	private DisplayImageOptions options = new DisplayImageOptions.Builder()
			.showImageOnFail(drawable.putongqun).build();

	/**
	 * @param rootView
	 */
	public GroupNotifyViewHolder(View rootView) {
		super(rootView);
		this.context = rootView.getContext();
	}

	@Override
	public void init(View view) {
		ivMemberIcon = (RoundedImageView) view
				.findViewById(id.image_group_avator);
		groupName = (TextView) view.findViewById(id.tv_group_name);
		description = (TextView) view.findViewById(id.tv_group_descripton);
		messageStatus = (TextView) view.findViewById(id.tv__action_status);
		btnDisposed = (Button) view.findViewById(id.button_action);
		tvDisposed = (TextView) view.findViewById(id.tv_action);
	}

	// TODO 待重构的代码
	@Override
	public void fillFromItem(Object item) {
		super.fillFromItem(item);
		final ServiceMessage serviceMsg = (ServiceMessage) item;
		final Account sendAccount = serviceMsg.getSendAccount();
		final String nickname;
		if (sendAccount != null) {
			nickname = sendAccount.getNickname();
		} else {
			nickname = "";
		}

		// FIXME 为什么会出现服务器的图片找不到的情况？
		// java.io.FileNotFoundException:
		// http://d11.sunbo.com/~hightman/mhealth/img/avatar/none.png
		// CloudImageLoader.getInstance().getImageLoader().displayImage(serviceMsg.getGroupAvatar(),
		// ivMemberIcon);

		// TODO
		// 1.邀请通知：群名称下方显示“TODO”，应该显示“**邀请您加入群”，包括接受/拒绝邀请通知也一样显示，只是后面的处理按钮变为已同意/已拒绝
		// 2.退群、解散、踢出群通知：不需要显示群头像和单独列出群名称。应该是头像处显示！图标，后面显示正确文案“XXX群已解散”，“您已被踢出XXX群”，“xx已退出您的XX群”即可。可以参考ios修改。

		switch (serviceMsg.getMessageType()) {
		case Constants.TYPE_INVITE_GROUP:
			invited(serviceMsg, nickname);
			break;
		case Constants.TYPE_ACCEPT_GROUP:
			accepted(serviceMsg, nickname);
			break;
		case Constants.TYPE_REFUSE_GROUP:
			refused(serviceMsg, nickname);
			break;
		case Constants.TYPE_KICK_GROUP:
			kicked(serviceMsg);
			break;
		case Constants.TYPE_QUIT_GROUP:
			quited(serviceMsg, nickname);
			break;
		case Constants.TYPE_DISMISS_GROUP:
			dissmissed(serviceMsg);
			break;
		default:
			break;
		}

	}

	private String getText(int id) {
		return context.getResources().getString(id);
	}

	private int getColor(int id) {
		return context.getResources().getColor(id);
	}

	private void invited(final ServiceMessage serviceMsg, String nickname) {

		String avatarUrl = NetworkClientHelper.getAvatarUrl(serviceMsg
				.getGroupId().intValue());
		ImageLoader.getInstance()
				.displayImage(avatarUrl, ivMemberIcon, options);
		groupName.setText(serviceMsg.getGroupName());
		messageStatus.setVisibility(View.GONE);
		description.setText(nickname
				+ getText(R.string.group_notify_invite_into));
		// 判断isread及response状况，在界面上以不同方式呈现
		if (serviceMsg.isAcceptInvite() != null) {
			tvDisposed.setVisibility(View.VISIBLE);
			btnDisposed.setVisibility(View.GONE);
			btnDisposed.setOnClickListener(null);
			if (serviceMsg.isAcceptInvite()) {
				tvDisposed.setText(getText(R.string.group_notify_agree));
			} else {
				tvDisposed.setText(getText(R.string.group_notify_refuse));
			}
		} else {
			// 未阅读状态
			tvDisposed.setVisibility(View.GONE);
			btnDisposed.setVisibility(View.VISIBLE);
			btnDisposed.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {

					Intent intent = new Intent(context,
							GroupNotifyProcessActivity.class);
					intent.putExtra(ServiceMessage.NAME_FIELD_MESSAGE_ID,
							serviceMsg.getMessageId());
					intent.putExtra(ServiceMessage.NAME_FIELD_GROUP_ID,
							serviceMsg.getGroupId());

					((Activity) context).startActivityForResult(intent, 0);
				}
			});
		}
	}

	// 群主收到的成员同意入群的消息
	private void accepted(final ServiceMessage serviceMsg, String nickname) {
		messageStatus.setVisibility(View.VISIBLE);
		messageStatus.setText("");
		ivMemberIcon.setImageResource(drawable.group_ic_exitgroup);
		description.setVisibility(View.GONE);
		String template = getText(R.string.group_notify_success_join);
		groupName.setText(String.format(template, nickname,serviceMsg.getGroupName()));
		groupName.setTextColor(getColor(R.color.shadow_dark));
		btnDisposed.setVisibility(View.GONE);

		// String avatarUrl = NetworkClientHelper.getAvatarUrl(serviceMsg
		// .getGroupId().intValue());
		// ImageLoader.getInstance()
		// .displayImage(avatarUrl, ivMemberIcon, options);
		// groupName.setText(serviceMsg.getGroupName());
		// groupName.setTextColor(getColor(android.R.color.black));
		// // messageStatus.setVisibility(View.VISIBLE);
		// // messageStatus.setText(getText(R.string.group_notify_agree));
		// String template = getText(R.string.group_notify_success_join);
		// description.setText(String.format(template, nickname));
		// btnDisposed.setVisibility(View.GONE);
	}

	// 群主收到成员拒绝入群的消息
	private void refused(final ServiceMessage serviceMsg, String nickname) {
		messageStatus.setVisibility(View.VISIBLE);
		messageStatus.setText("");
		ivMemberIcon.setImageResource(drawable.group_ic_exitgroup);
		description.setVisibility(View.GONE);
		String template = getText(R.string.group_notify_refuse_join);
		groupName.setText(String.format(template, nickname,serviceMsg.getGroupName()));
		groupName.setTextColor(getColor(R.color.shadow_dark));
		btnDisposed.setVisibility(View.GONE);
		// String avatarUrl = NetworkClientHelper.getAvatarUrl(serviceMsg
		// .getGroupId().intValue());
		// ImageLoader.getInstance()
		// .displayImage(avatarUrl, ivMemberIcon, options);
		// groupName.setText(serviceMsg.getGroupName());
		// groupName.setTextColor(getColor(android.R.color.black));
		// // messageStatus.setVisibility(View.VISIBLE);
		// // messageStatus.setText(getText(R.string.group_notify_refuse));
		// String template = getText(R.string.group_notify_refuse_join);
		// description.setText(String.format(template, nickname));
		// btnDisposed.setVisibility(View.GONE);
	}

	// 成员收到的被踢出群的消息
	private void kicked(ServiceMessage serviceMsg) {
		messageStatus.setVisibility(View.VISIBLE);
		messageStatus.setText("");
		ivMemberIcon.setImageResource(drawable.group_ic_exitgroup);
		description.setVisibility(View.GONE);
		String template = getText(R.string.group_notify_kick);
		groupName.setText(String.format(template, serviceMsg.getGroupName()));
		groupName.setTextColor(getColor(R.color.shadow_dark));
		btnDisposed.setVisibility(View.GONE);
	}

	// 成员退群的消息
	private void quited(ServiceMessage serviceMsg, String nickname) {
		messageStatus.setVisibility(View.VISIBLE);
		messageStatus.setText("");
		ivMemberIcon.setImageResource(drawable.group_ic_exitgroup);
		description.setVisibility(View.GONE);
		String template = getText(R.string.group_notify_quite);
		groupName.setText(String.format(template, nickname,
				serviceMsg.getGroupName()));
		groupName.setTextColor(getColor(R.color.shadow_dark));
		btnDisposed.setVisibility(View.GONE);
	}

	// 群组解散群的消息
	private void dissmissed(ServiceMessage serviceMsg) {
		messageStatus.setVisibility(View.VISIBLE);
		messageStatus.setText("");
		ivMemberIcon.setImageResource(drawable.group_ic_exitgroup);
		description.setVisibility(View.GONE);
		String template = getText(R.string.group_notify_dismiss);
		groupName.setText(String.format(template, serviceMsg.getGroupName()));
		groupName.setTextColor(getColor(R.color.shadow_dark));
		btnDisposed.setVisibility(View.GONE);
	}
}

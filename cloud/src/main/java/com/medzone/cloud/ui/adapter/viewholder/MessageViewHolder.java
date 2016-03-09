/**
 * 
 */
package com.medzone.cloud.ui.adapter.viewholder;

import java.util.Date;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.medzone.cloud.CloudImageLoader;
import com.medzone.cloud.data.CurrentAccountManager;
import com.medzone.cloud.data.TemporaryData;
import com.medzone.cloud.ui.GroupChatActivity;
import com.medzone.cloud.ui.GroupPersonDetailActivity;
import com.medzone.cloud.ui.GroupServiceForMemberDetailActivity;
import com.medzone.cloud.ui.SettingPersonalInfoActivity;
import com.medzone.framework.Log;
import com.medzone.framework.data.bean.imp.Group;
import com.medzone.framework.data.bean.imp.GroupMember;
import com.medzone.framework.data.bean.imp.Message;
import com.medzone.framework.util.TimeUtils;
import com.medzone.framework.view.RoundedImageView;
import com.medzone.mcloud.R;

/**
 * @author ChenJunQi.
 * 
 */
public class MessageViewHolder extends BaseViewHolder {

	private TextView tvItemMessage;
	private TextView tvItemDate;
	private TextView tvItemName;
	private RoundedImageView imageItemAvator;
	private LinearLayout llChatContent;

	private Context context;

	public MessageViewHolder(View itemView) {
		super(itemView);
	}

	public void init(View view) {
		context = view.getContext();
		tvItemMessage = (TextView) view.findViewById(R.id.chatting_content_itv);
		tvItemDate = (TextView) view.findViewById(R.id.chatting_time_tv);
		tvItemName = (TextView) view.findViewById(R.id.tv_name);
		llChatContent = (LinearLayout) view
				.findViewById(R.id.ll_chatting_content);
		imageItemAvator = (RoundedImageView) view
				.findViewById(R.id.chatting_avatar_iv);
	}

	@Override
	public void fillFromItem(Object item) {
		final Message message = (Message) item;
		final Group bindGroup = GroupChatActivity.getCurGroup();
		tvItemMessage.setText(message.getMessage());
		tvItemName.setText(message.getAccountDisplayName());
		tvItemDate.setText(getDateDescription(message.getPostTime()));
		CloudImageLoader.getInstance().getImageLoader()
				.displayImage(message.getIconUrl(), imageItemAvator);

		imageItemAvator.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				final int memberID = message.getAccountID();

				if (memberID == CurrentAccountManager.getCurAccount()
						.getAccountID()) {
					Intent intent = new Intent(context,
							SettingPersonalInfoActivity.class);
					context.startActivity(intent);
				} else {

					GroupMember member = GroupChatActivity
							.searchGroupMember(memberID);

					// TODO
					// 这段代码在MessageLinkViewHolder、MessageRecordViewHolder中均出现的重复！
					if (bindGroup.getType() == Group.TYPE_SERVICE
							&& member.equals(bindGroup)) {

						TemporaryData.save(Group.class.getName(), bindGroup);
						Intent intent = new Intent(context,
								GroupServiceForMemberDetailActivity.class);
						context.startActivity(intent);

					} else {

						if (member != null) {
							member.setBindGroup(bindGroup);
							TemporaryData.save(GroupMember.class.getName(),
									member);
							Intent intent = new Intent(context,
									GroupPersonDetailActivity.class);
							context.startActivity(intent);
						}
					}
				}
			}
		});

		llChatContent.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Log.v(">>>点击了llChatContent！");
			}
		});
	}

	private String getDateDescription(Date date) {
		return TimeUtils.getChatTime(date.getTime());
	}
}

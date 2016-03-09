/**
 * 
 */
package com.medzone.cloud.ui.adapter.viewholder;

import java.util.Date;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.medzone.cloud.CloudImageLoader;
import com.medzone.cloud.Constants;
import com.medzone.cloud.data.CurrentAccountManager;
import com.medzone.cloud.data.TemporaryData;
import com.medzone.cloud.ui.GroupChatActivity;
import com.medzone.cloud.ui.GroupPersonDetailActivity;
import com.medzone.cloud.ui.GroupServiceForMemberDetailActivity;
import com.medzone.cloud.ui.HealthCentreWebViewActivity;
import com.medzone.cloud.ui.SettingPersonalInfoActivity;
import com.medzone.cloud.ui.dialog.DialogPage;
import com.medzone.framework.Log;
import com.medzone.framework.data.bean.imp.BloodOxygen;
import com.medzone.framework.data.bean.imp.BloodPressure;
import com.medzone.framework.data.bean.imp.Group;
import com.medzone.framework.data.bean.imp.GroupMember;
import com.medzone.framework.data.bean.imp.Message;
import com.medzone.framework.util.TimeUtils;
import com.medzone.framework.view.RoundedImageView;
import com.medzone.mcloud.R;
import com.medzone.mcloud.R.drawable;

/**
 * @author ChenJunQi.
 * 
 */
public class MessageLinkViewHolder extends BaseViewHolder {

	private ImageView imageItemAvator;
	private TextView tvTitle, tvSubtitle, tvSubValue;
	private TextView tvPostTime, tvItemName, tvTime;
	private RoundedImageView ivAvatar;
	private RelativeLayout llChatContent;

	private Context context;

	public MessageLinkViewHolder(View itemView) {
		super(itemView);
	}

	public void init(View view) {
		context = view.getContext();
		tvPostTime = (TextView) view.findViewById(R.id.chatting_time_tv);
		tvTime = (TextView) view.findViewById(R.id.tv_measure_time);
		ivAvatar = (RoundedImageView) view
				.findViewById(R.id.chatting_avatar_iv);
		tvItemName = (TextView) view.findViewById(R.id.tv_name);

		imageItemAvator = (ImageView) view.findViewById(R.id.iv_result_icon);
		tvTitle = (TextView) view.findViewById(R.id.tv_title);
		tvSubtitle = (TextView) view.findViewById(R.id.tv_unit_1);
		tvSubValue = (TextView) view.findViewById(R.id.tv_value_1);
		llChatContent = (RelativeLayout) view
				.findViewById(R.id.ll_chatting_content);
	}

	@Override
	public void fillFromItem(Object item) {
		final Message message = (Message) item;
		final Group bindGroup = GroupChatActivity.getCurGroup();

		CloudImageLoader.getInstance().getImageLoader()
				.displayImage(message.getIconUrl(), ivAvatar);
		tvPostTime.setText(getDateDescription(message.getPostTime()));
		tvItemName.setText(message.getAccountDisplayName());

		tvTitle.setText(message.getLinkTitle());

		tvSubValue.setText(message.getLinkDescription());

		try {
			switch (message.getReportType().intValue()) {
			case DialogPage.TYPE_RECENT:
				tvSubtitle.setText("趋势统计：");
				imageItemAvator
						.setImageResource(drawable.group_chat_graph_tendency);
				String timeFormat = TimeUtils.getTime(
						message.getLinkTime() * 1000, TimeUtils.YY_MM_DD);
				tvTime.setText("" + timeFormat);
				break;
			case DialogPage.TYPE_MONTH:
				tvSubtitle.setText("本月统计：");
				imageItemAvator
						.setImageResource(drawable.group_chat_graph_monthly);
				String yyMMcn = TimeUtils.getShareMonthTime(message
						.getLinkTime());
				tvTime.setText("" + yyMMcn);
				break;
			default:
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		llChatContent.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				jump2URL(message);
			}
		});

		ivAvatar.setOnClickListener(new OnClickListener() {

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

	}

	private void jump2URL(Message message) {
		// 临时屏蔽跳转URL功能
		String url = message.getLinkURL();
		Log.i("message link viewholder url:" + url);
		if (!TextUtils.isEmpty(url)) {
			TemporaryData.save(Constants.TEMPORARYDATA_KEY_URL, url);
			TemporaryData.save(Constants.TEMPORARYDATA_KEY_TITLE,
					convertTitle(message));
			Intent intent = new Intent(context,
					HealthCentreWebViewActivity.class);
			context.startActivity(intent);
		}
	}

	private String convertTitle(Message msg) {
		if (TextUtils.isEmpty(msg.getLinkTitle())) {
			if (TextUtils.equals(msg.getRecordType(),
					BloodOxygen.BLOODOXYGEN_TAG))
				return "血氧单次详情";
			else if (TextUtils.equals(msg.getRecordType(),
					BloodPressure.BLOODPRESSURE_TAG))
				return "血压单次详情";
			else
				return "查看分享";
		} else {
			return msg.getLinkTitle();
		}
	}

	private String getDateDescription(Date date) {
		return TimeUtils.getChatTime(date.getTime());
	}
}

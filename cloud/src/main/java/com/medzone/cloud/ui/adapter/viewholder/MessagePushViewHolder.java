/**
 * 
 */
package com.medzone.cloud.ui.adapter.viewholder;

import java.util.Date;

import android.view.View;
import android.widget.TextView;

import com.medzone.cloud.CloudImageLoader;
import com.medzone.framework.data.bean.imp.Message;
import com.medzone.framework.util.TimeUtils;
import com.medzone.framework.view.RoundedImageView;
import com.medzone.mcloud.R;

/**
 * @author ChenJunQi.
 * 
 */
public class MessagePushViewHolder extends BaseViewHolder {

	private TextView tvItemMessage;
	private TextView tvItemDate;
	private TextView tvItemName;
	private RoundedImageView imageItemAvator;

	public MessagePushViewHolder(View itemView) {
		super(itemView);
	}

	public void init(View view) {
		tvItemMessage = (TextView) view.findViewById(R.id.chatting_content_itv);
		tvItemDate = (TextView) view.findViewById(R.id.chatting_time_tv);
		tvItemName = (TextView) view.findViewById(R.id.tv_name);
		imageItemAvator = (RoundedImageView) view
				.findViewById(R.id.chatting_avatar_iv);
	}

	@Override
	public void fillFromItem(Object item) {
		Message message = (Message) item;
		tvItemMessage.setText(message.getMessage());
		tvItemDate.setText(getDateDescription(message.getPostTime()));
		tvItemName.setText(message.getAccountDisplayName());
		CloudImageLoader.getInstance().getImageLoader()
				.displayImage(message.getIconUrl(), imageItemAvator);
	}

	private String getDateDescription(Date date) {
		return TimeUtils.getChatTime(date.getTime());
	}
}

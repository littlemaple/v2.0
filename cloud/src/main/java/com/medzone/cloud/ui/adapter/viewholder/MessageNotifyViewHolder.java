/**
 * 
 */
package com.medzone.cloud.ui.adapter.viewholder;

import android.view.View;
import android.widget.TextView;

import com.medzone.framework.data.bean.imp.Message;
import com.medzone.mcloud.R;

/**
 * @author ChenJunQi.
 * 
 */
public class MessageNotifyViewHolder extends BaseViewHolder {

	private TextView tvItemNotify;

	public MessageNotifyViewHolder(View itemView) {
		super(itemView);
	}

	public void init(View view) {
		tvItemNotify = (TextView) view.findViewById(R.id.tv_notify);
	}

	@Override
	public void fillFromItem(Object item) {
		Message message = (Message) item;

		String nickname = message.getNotifiedName();

		if (Message.NOTIFY_JOIN.equals(message.getNotifiedType())) {
			nickname += "加入群";
		} else if (Message.NOTIFY_QUIT.equals(message.getNotifiedType())) {
			nickname += "退出群";
		} else if (Message.NOTIFY_KICKED.equals(message.getNotifiedType())) {
			nickname += "被管理员移出";
		}
		tvItemNotify.setText(nickname);
	}
}

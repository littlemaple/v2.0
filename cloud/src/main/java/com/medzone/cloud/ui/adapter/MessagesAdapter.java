/**
 * 
 */
package com.medzone.cloud.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.medzone.cloud.cache.MessageCache;
import com.medzone.cloud.data.CurrentAccountManager;
import com.medzone.cloud.ui.GroupChatActivity;
import com.medzone.cloud.ui.adapter.viewholder.MessageLinkViewHolder;
import com.medzone.cloud.ui.adapter.viewholder.MessageNotifyViewHolder;
import com.medzone.cloud.ui.adapter.viewholder.MessageRecordViewHolder;
import com.medzone.cloud.ui.adapter.viewholder.MessageViewHolder;
import com.medzone.framework.data.bean.imp.GroupMember;
import com.medzone.framework.data.bean.imp.Message;
import com.medzone.framework.data.navigation.LongStepable;
import com.medzone.mcloud.R;

/**
 * @author ChenJunQi.
 * 
 */
public class MessagesAdapter extends
		PagingListCacheAdapter<Message, LongStepable, MessageCache> {

	private static final int VIEW_TYPE_COUNT = 8;

	public static final int ITEM_TYPE_RECEIVED_NORMAL = 1;
	public static final int ITEM_TYPE_RECEIVED_RECORD = 2;
	public static final int ITEM_TYPE_RECEIVED_LINK = 3;
	public static final int ITEM_TYPE_SENT_NORMAL = 4;
	public static final int ITEM_TYPE_SENT_RECORD = 5;
	public static final int ITEM_TYPE_SENT_LINK = 6;
	// public static final int ITEM_TYPE_PUSH = 7;
	public static final int ITEM_TYPE_NOTIFY = 7;

	private boolean invilidate = false;

	public MessagesAdapter(Context context) {
		super(context);
	}

	@Override
	public long getItemId(int position) {
		Message item = getItem(position);
		if (item != null) {
			return item.getId();
		}
		return 0;
	}

	@Override
	public int getItemType(Message item) {
		if (item == null) {
			return 0;
		} else if (item.equals(CurrentAccountManager.getCurAccount())) {

			switch (item.getType()) {
			case Message.TYPE_NORMAL:
				return ITEM_TYPE_SENT_NORMAL;
			case Message.TYPE_RECORD:
				return ITEM_TYPE_SENT_RECORD;
			case Message.TYPE_LINK:
				return ITEM_TYPE_SENT_LINK;
			case Message.TYPE_NOTIFY:
				return ITEM_TYPE_NOTIFY;
			default:
				break;
			}
		} else {
			switch (item.getType()) {
			case Message.TYPE_NORMAL:
				return ITEM_TYPE_RECEIVED_NORMAL;
			case Message.TYPE_RECORD:
				return ITEM_TYPE_RECEIVED_RECORD;
			case Message.TYPE_LINK:
				return ITEM_TYPE_RECEIVED_LINK;
			case Message.TYPE_NOTIFY:
				return ITEM_TYPE_NOTIFY;
			default:
				break;
			}
		}
		return 0;
	}

	@Override
	public void fillView(View view, Object item) {
		Message msg = (Message) item;
		if (invilidate) {
			updateMessageItem(msg);
		}
		super.fillView(view, msg);
	}

	private void updateMessageItem(Message msg) {
		if (msg.getAccountID() == null)
			return;
		int memberID = msg.getAccountID().intValue();
		GroupMember bindMember = GroupChatActivity.searchGroupMember(memberID);
		if (bindMember != null)
			if (bindMember.getHeadPortRait() != null)
				msg.setIconUrl(bindMember.getHeadPortRait());
		msg.setBindMember(bindMember);
	}

	@Override
	public View inflateView(int viewType, int position) {
		View itemView = null;
		switch (viewType) {
		case ITEM_TYPE_SENT_NORMAL:
			itemView = LayoutInflater.from(context).inflate(
					R.layout.list_item_message_sent, null);
			itemView.setTag(new MessageViewHolder(itemView));
			break;
		case ITEM_TYPE_SENT_LINK:
			itemView = LayoutInflater.from(context).inflate(
					R.layout.list_item_message_sent_link, null);
			itemView.setTag(new MessageLinkViewHolder(itemView));
			break;
		case ITEM_TYPE_SENT_RECORD:
			itemView = LayoutInflater.from(context).inflate(
					R.layout.list_item_message_sent_record, null);
			itemView.setTag(new MessageRecordViewHolder(itemView));
			break;
		case ITEM_TYPE_RECEIVED_NORMAL:
			itemView = LayoutInflater.from(context).inflate(
					R.layout.list_item_message_received, null);
			itemView.setTag(new MessageViewHolder(itemView));
			break;
		case ITEM_TYPE_RECEIVED_LINK:
			itemView = LayoutInflater.from(context).inflate(
					R.layout.list_item_message_received_link, null);
			itemView.setTag(new MessageLinkViewHolder(itemView));
			break;
		case ITEM_TYPE_RECEIVED_RECORD:
			itemView = LayoutInflater.from(context).inflate(
					R.layout.list_item_message_received_record, null);
			itemView.setTag(new MessageRecordViewHolder(itemView));
			break;

		case ITEM_TYPE_NOTIFY:
			itemView = LayoutInflater.from(context).inflate(
					R.layout.list_item_message_notify, null);
			itemView.setTag(new MessageNotifyViewHolder(itemView));
			break;
		// case ITEM_TYPE_PUSH:
		// itemView = LayoutInflater.from(context).inflate(
		// R.layout.list_item_message_push, null);
		// itemView.setTag(new MessagePushViewHolder(itemView));
		// break;
		default:
			break;
		}
		if (getCount() == position) {
			invilidate = false;
		}
		return itemView;
	}

	@Override
	public int getViewTypeCount() {
		return VIEW_TYPE_COUNT + super.getViewTypeCount();
	}

	public void invilidate() {
		this.invilidate = true;
	}
}

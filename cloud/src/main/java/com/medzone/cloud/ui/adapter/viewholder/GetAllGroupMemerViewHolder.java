package com.medzone.cloud.ui.adapter.viewholder;

import android.view.View;
import android.widget.TextView;

import com.medzone.cloud.CloudImageLoader;
import com.medzone.cloud.data.CurrentAccountManager;
import com.medzone.framework.data.bean.imp.GroupMember;
import com.medzone.framework.view.RoundedImageView;
import com.medzone.mcloud.R.id;

public class GetAllGroupMemerViewHolder extends BaseViewHolder {

	private RoundedImageView headPortRait;
	private TextView nickname;

	/**
	 * @param rootView
	 */
	public GetAllGroupMemerViewHolder(View rootView) {
		super(rootView);
	}

	@Override
	public void init(View view) {
		headPortRait = (RoundedImageView) view
				.findViewById(id.pressure_select_item_image);
		nickname = (TextView) view.findViewById(id.pressure_select_item_text);
	}

	@Override
	public void fillFromItem(Object item) {
		super.fillFromItem(item);
		GroupMember groupmember = (GroupMember) item;
		CloudImageLoader.getInstance().getImageLoader()
				.displayImage(groupmember.getHeadPortRait(), headPortRait);
		if (groupmember.getAccountID().equals(
				CurrentAccountManager.getCurAccount().getAccountID())) {
			nickname.setText("我");
		} else {
			nickname.setText(CurrentAccountManager.getCurAccount()
					.getFriendsDisplay(groupmember));
		}
	}

}

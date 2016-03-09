/**
 * 
 */
package com.medzone.cloud.ui.adapter.viewholder;

import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.medzone.cloud.CloudImageLoader;
import com.medzone.cloud.data.CurrentAccountManager;
import com.medzone.framework.data.bean.imp.GroupMember;
import com.medzone.mcloud.R.id;

/**
 * @author ChenJunQi.
 * 
 */
public class SettingViewHolder extends BaseViewHolder {

	private ImageView icon;
	private TextView name;
	private CheckBox cbStatus;
	private Boolean isView;

	/**
	 * @param rootView
	 */
	public SettingViewHolder(View rootView) {
		super(rootView);
	}

	public void isView(Boolean isView) {
		this.isView = isView;
	}

	@Override
	public void init(View view) {
		icon = (ImageView) view.findViewById(id.icon);
		name = (TextView) view.findViewById(id.name);
		cbStatus = (CheckBox) view.findViewById(id.cbStatus);

	}

	@Override
	public void fillFromItem(Object item) {
		super.fillFromItem(item);

		GroupMember member = (GroupMember) item;
		CloudImageLoader.getInstance().getImageLoader()
				.displayImage(member.getHeadPortRait(), icon);
		name.setText(CurrentAccountManager.getCurAccount().getFriendsDisplay(
				member));
		if (isView)
			cbStatus.setChecked(member.isViewHistory());
		else
			cbStatus.setChecked(member.isMeasure());
	}

}

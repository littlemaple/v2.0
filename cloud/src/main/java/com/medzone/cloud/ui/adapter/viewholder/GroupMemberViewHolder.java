/**
 * 
 */
package com.medzone.cloud.ui.adapter.viewholder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.medzone.cloud.CloudImageLoader;
import com.medzone.framework.data.bean.imp.Group;
import com.medzone.mcloud.R.id;

/**
 * @author ChenJunQi.
 * 
 */
public class GroupMemberViewHolder extends BaseViewHolder {

	private ImageView childAvator;
	private TextView childDescription;

	/**
	 * @param rootView
	 */
	public GroupMemberViewHolder(View rootView) {
		super(rootView);
	}

	@Override
	public void init(View view) {
		childAvator = (ImageView) view.findViewById(id.iv_group_icon);
		childDescription = (TextView) view.findViewById(id.tv_group_name);
	}

	@Override
	public void fillFromItem(Object item) {
		super.fillFromItem(item);
		Group group = (Group) item;
		CloudImageLoader.getInstance().getImageLoader()
				.displayImage(group.getHeadPortRait(), childAvator);
		childDescription.setText(group.getName());
	}

}

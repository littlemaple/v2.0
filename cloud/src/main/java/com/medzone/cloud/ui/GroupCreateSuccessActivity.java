/**
 * 
 */
package com.medzone.cloud.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.LayoutParams;
import com.medzone.cloud.data.PropertyCenter;
import com.medzone.cloud.data.TemporaryData;
import com.medzone.framework.data.bean.imp.Group;
import com.medzone.mcloud.R;
import com.medzone.mcloud.R.id;
import com.medzone.mcloud.R.layout;
import com.medzone.mcloud.R.string;

/**
 * @author ChenJunQi.
 * 
 */
public class GroupCreateSuccessActivity extends BaseActivity {

	private TextView groupNameTv;
	private Group group;

	@Override
	protected void preLoadData(Bundle savedInstanceState) {
		super.preLoadData(savedInstanceState);
		group = (Group) TemporaryData.get(Group.class.getName());
	}

	private void initActionBar() {
		ActionBar actionBar = getSupportActionBar();
		ActionBar.LayoutParams params = new ActionBar.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT,
				Gravity.CENTER);
		View view = LayoutInflater.from(this).inflate(
				R.layout.custom_actionbar_with_text, null);
		TextView title = (TextView) view.findViewById(R.id.actionbar_title);
		title.setText(string.actionbar_title_group_createsuccess);
		actionBar.setCustomView(view, params);
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		actionBar.setDisplayShowCustomEnabled(true);
	}

	@Override
	protected void preInitUI() {
		initActionBar();
	}

	@Override
	protected void initUI() {
		setContentView(layout.activity_group_create_success);
		groupNameTv = (TextView) findViewById(id.tv_group_name);
	}

	@Override
	protected void postInitUI() {
		groupNameTv.setText(group.getName());
	}

	public void onInviteGroupMember(View view) {
		Intent intent = new Intent(this, GroupInviteActivity.class);
		intent.putExtra(Group.NAME_FIELD_GROUP_ID, group.getGroupID());
		startActivity(intent);
	}

	public void onComplete(View view) {
		finish();
	}

	@Override
	public void finish() {
		super.finish();
		PropertyCenter.getInstance().firePropertyChange(
				PropertyCenter.PROPERTY_REFRESH_GROUPLIST_EXPAND, null, null);
	}
}

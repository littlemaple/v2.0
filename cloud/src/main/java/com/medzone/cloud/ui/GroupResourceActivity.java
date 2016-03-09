/**
 * 
 */
package com.medzone.cloud.ui;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.ImageButton;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.LayoutParams;
import com.medzone.cloud.data.CurrentAccountManager;
import com.medzone.cloud.data.PropertyCenter;
import com.medzone.cloud.data.TemporaryData;
import com.medzone.cloud.data.helper.GroupHelper;
import com.medzone.cloud.task.EditGroupTask;
import com.medzone.cloud.ui.dialog.error.ErrorDialogUtil;
import com.medzone.cloud.ui.widget.CleanableEditText;
import com.medzone.framework.data.bean.imp.Group;
import com.medzone.framework.errorcode.ProxyCode.LocalError;
import com.medzone.framework.errorcode.ProxyErrorCode;
import com.medzone.framework.task.BaseResult;
import com.medzone.framework.task.TaskHost;
import com.medzone.mcloud.R;
import com.medzone.mcloud.R.drawable;
import com.medzone.mcloud.R.id;
import com.medzone.mcloud.R.string;

/**
 * @author ChenJunQi.
 * 
 */
public class GroupResourceActivity extends BaseActivity implements
		OnClickListener {

	private CleanableEditText etGroupName;
	private CleanableEditText etGroupDescription;

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
				R.layout.custom_actionbar_with_image, null);
		TextView title = (TextView) view.findViewById(R.id.actionbar_title);
		ImageButton leftButton = (ImageButton) view
				.findViewById(id.actionbar_left);
		leftButton.setImageResource(drawable.personalinformationview_ic_cancel);
		leftButton.setOnClickListener(this);
		ImageButton rightButton = (ImageButton) view
				.findViewById(id.actionbar_right);
		rightButton.setImageResource(drawable.personalinformationview_ic_ok);
		rightButton.setOnClickListener(this);
		title.setText(string.group_resource);
		actionBar.setCustomView(view, params);
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		actionBar.setDisplayShowCustomEnabled(true);
	}

	@Override
	protected void initUI() {
		initActionBar();
		setContentView(R.layout.activity_group_resource);
		etGroupName = (CleanableEditText) findViewById(id.et_group_name);
		etGroupDescription = (CleanableEditText) findViewById(id.et_group_description);
	}

	@Override
	protected void postInitUI() {
		super.postInitUI();
		String groupName = group.getName();
		String groupDescription = group.getDescription();

		if (!TextUtils.isEmpty(groupDescription)) {
			etGroupDescription.setText(groupDescription);
			etGroupDescription.setSelection(groupDescription.length());
		}

		etGroupName.setText(groupName);
		etGroupName.setSelection(groupName.length());
		etGroupDescription.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_ENTER
						&& event.getAction() == KeyEvent.ACTION_DOWN) {
					doUpdateGroupInfoTask();
					return true;
				}
				return false;
			}
		});
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case id.actionbar_left:
			finish();
			break;
		case id.actionbar_right:
			doUpdateGroupInfoTask();
			break;
		default:
			break;
		}
	}

	private void doUpdateGroupInfoTask() {

		final String groupName = etGroupName.getText().toString();
		final String description = etGroupDescription.getText().toString();
		final int errorCode = GroupHelper.checkGroupNameStyle(groupName);

		if (errorCode != LocalError.CODE_SUCCESS) {
			ErrorDialogUtil.showErrorDialog(GroupResourceActivity.this,
					ProxyErrorCode.TYPE_GROUP, errorCode, true);
			return;
		}

		final int errorCode2 = GroupHelper.checkGroupDescription(description);
		if (errorCode2 != LocalError.CODE_SUCCESS) {
			ErrorDialogUtil.showErrorDialog(GroupResourceActivity.this,
					ProxyErrorCode.TYPE_GROUP, errorCode2, true);
			return;
		}

		EditGroupTask task = new EditGroupTask(this, CurrentAccountManager
				.getCurAccount().getAccessToken(), group.getGroupID(),
				etGroupName.getText().toString(), etGroupDescription.getText()
						.toString(), null);
		task.setTaskHost(new TaskHost() {
			@Override
			public void onPostExecute(int requestCode, BaseResult result) {
				super.onPostExecute(requestCode, result);
				if (result.isSuccess()) {
					if (result.isServerDisposeSuccess()) {
						group.setName(groupName);
						group.setDescription(description);
						PropertyCenter.getInstance().firePropertyChange(
								PropertyCenter.PROPERTY_REFRESH_GROUP_MEMBER,
								null, group);
						PropertyCenter.getInstance().firePropertyChange(
								PropertyCenter.PROPERTY_REFRESH_GROUPLIST,
								null, group);
						finish();
					} else {
						ErrorDialogUtil.showErrorDialog(
								GroupResourceActivity.this,
								ProxyErrorCode.TYPE_GROUP,
								result.getErrorCode(), true);
					}
				}
			}
		});
		task.execute();
	}
}

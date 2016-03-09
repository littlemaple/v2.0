/**
 * 
 */
package com.medzone.cloud.ui;

import android.content.Intent;
import android.os.AsyncTask.Status;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.LayoutParams;
import com.medzone.cloud.data.CurrentAccountManager;
import com.medzone.cloud.data.PropertyCenter;
import com.medzone.cloud.data.TemporaryData;
import com.medzone.cloud.data.helper.GroupHelper;
import com.medzone.cloud.task.AddGroupTask;
import com.medzone.cloud.ui.dialog.error.ErrorDialogUtil;
import com.medzone.cloud.ui.widget.CleanableEditText;
import com.medzone.framework.data.bean.imp.Group;
import com.medzone.framework.errorcode.ProxyCode.LocalError;
import com.medzone.framework.errorcode.ProxyErrorCode;
import com.medzone.framework.network.NetworkClientResult;
import com.medzone.framework.task.BaseResult;
import com.medzone.framework.task.TaskHost;
import com.medzone.mcloud.R;
import com.medzone.mcloud.R.drawable;
import com.medzone.mcloud.R.id;
import com.medzone.mcloud.R.layout;
import com.medzone.mcloud.R.string;

/**
 * @author ChenJunQi.
 * 
 */
public class GroupCreateActivity extends BaseActivity implements
		OnClickListener {

	private CleanableEditText editGroupName;
	private AddGroupTask addGroupTask;

	@Override
	protected void preInitUI() {
		initActionBar();
	}

	@Override
	protected void initUI() {
		setContentView(layout.activity_group_create);
		editGroupName = (CleanableEditText) findViewById(id.ce_group_name);
	}
	@Override
	protected void postInitUI() {
		// TODO Auto-generated method stub
		super.postInitUI();
		editGroupName.setOnEditorActionListener(new OnEditorActionListener() {
			
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if(actionId == EditorInfo.IME_ACTION_GO){
					doGroupCreateTask();
				}
				return false;
			}
		});
	}

	private void initActionBar() {
		ActionBar actionBar = getSupportActionBar();
		ActionBar.LayoutParams params = new ActionBar.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT,
				Gravity.CENTER);
		View view = LayoutInflater.from(this).inflate(
				R.layout.custom_actionbar_with_image, null);
		TextView title = (TextView) view.findViewById(R.id.actionbar_title);
		title.setText(string.actionbar_title_group_create);
		ImageButton leftButton = (ImageButton) view
				.findViewById(id.actionbar_left);
		leftButton.setImageResource(drawable.personalinformationview_ic_cancel);
		leftButton.setOnClickListener(this);

		ImageButton rightButton = (ImageButton) view
				.findViewById(id.actionbar_right);
		rightButton.setImageResource(drawable.personalinformationview_ic_ok);
		rightButton.setOnClickListener(this);
		actionBar.setCustomView(view, params);
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		actionBar.setDisplayShowCustomEnabled(true);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case id.actionbar_left:
			finish();
			break;
		case id.actionbar_right:
			doGroupCreateTask();
			break;
		default:
			break;
		}
	}

	private void doGroupCreateTask() {

		final String groupName = editGroupName.getText().toString().trim();
		final int errorCode = GroupHelper.checkGroupNameStyle(groupName);

		if (errorCode != LocalError.CODE_SUCCESS) {
			ErrorDialogUtil.showErrorDialog(GroupCreateActivity.this,
					ProxyErrorCode.TYPE_GROUP, errorCode, true);
		} else {
			String accessToken = CurrentAccountManager.getCurAccount()
					.getAccessToken();

			if (addGroupTask != null
					&& addGroupTask.getStatus() == Status.RUNNING)
				return;
			addGroupTask = new AddGroupTask(this, accessToken, groupName, null);
			addGroupTask.setTaskHost(new TaskHost() {
				@Override
				public void onPostExecute(int requestCode, BaseResult result) {
					super.onPostExecute(requestCode, result);
					if (result.isSuccess()) {
						NetworkClientResult res = (NetworkClientResult) result;
						if (res.isServerDisposeSuccess()) {

							Group newGroup = Group.createGroup(res
									.getResponseResult(), CurrentAccountManager
									.getCurAccount().getAccountID());
							TemporaryData.save(Group.class.getName(), newGroup);
							PropertyCenter.getInstance().firePropertyChange(
									PropertyCenter.PROPERTY_REFRESH_GROUPLIST,
									null, null);
							jumpToCreateSuccessActivity();

						} else {
							ErrorDialogUtil.showErrorDialog(
									GroupCreateActivity.this,
									ProxyErrorCode.TYPE_GROUP,
									result.getErrorCode(), true);
						}
					}
				}
			});
			addGroupTask.execute();
		}
	}

	private void jumpToCreateSuccessActivity() {
		Intent intent = new Intent(GroupCreateActivity.this,
				GroupCreateSuccessActivity.class);
		startActivity(intent);
		finish();
	}
}

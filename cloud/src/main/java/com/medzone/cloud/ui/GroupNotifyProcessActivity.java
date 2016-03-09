/**
 * 
 */
package com.medzone.cloud.ui;

import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.LayoutParams;
import com.medzone.cloud.CloudImageLoader;
import com.medzone.cloud.Constants;
import com.medzone.cloud.data.CurrentAccountManager;
import com.medzone.cloud.data.PropertyCenter;
import com.medzone.cloud.data.helper.AccountHelper;
import com.medzone.cloud.data.helper.GroupHelper;
import com.medzone.cloud.task.MarkMessageResponseTask;
import com.medzone.cloud.ui.dialog.error.ErrorDialogUtil;
import com.medzone.framework.data.bean.imp.Group;
import com.medzone.framework.data.bean.imp.ServiceMessage;
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
public class GroupNotifyProcessActivity extends BaseActivity implements
		OnClickListener {

	private int messageid;
	private int groupid;

	private Group group;

	private ImageView imageGroupAvator;
	private TextView tvGroupName;
	private TextView tvGroupDescription;
	private CheckBox cbGroupUpload;
	private CheckBox cbGroupAlert;

	private Button btnRefused;
	private Button btnAccepted;

	@Override
	protected void preLoadData(Bundle savedInstanceState) {
		super.preLoadData(savedInstanceState);

		initActionBar();
		Intent intent = getIntent();
		groupid = intent.getIntExtra(ServiceMessage.NAME_FIELD_GROUP_ID, 0);
		messageid = intent.getIntExtra(ServiceMessage.NAME_FIELD_MESSAGE_ID, 0);
		doViewGroupTask(groupid);

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
		leftButton.setImageResource(drawable.public_ic_back);
		leftButton.setOnClickListener(this);
		title.setText(string.actionbar_title_group_notify_process);
		actionBar.setCustomView(view, params);
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		actionBar.setDisplayShowCustomEnabled(true);
	}

	@Override
	protected void initUI() {
		setContentView(layout.activity_group_notify_process);
		imageGroupAvator = (ImageView) findViewById(R.id.iv_group_icon);
		tvGroupName = (TextView) findViewById(R.id.tv_group_name);
		tvGroupDescription = (TextView) findViewById(R.id.tv_group_description);
		cbGroupUpload = (CheckBox) findViewById(R.id.iv_upload_message);
		cbGroupAlert = (CheckBox) findViewById(R.id.iv_new_notice);
		btnRefused = (Button) findViewById(R.id.button_refused);
		btnAccepted = (Button) findViewById(R.id.button_accepted);

	}

	@Override
	protected void postInitUI() {
		btnRefused.setOnClickListener(this);
		btnAccepted.setOnClickListener(this);
	}

	private void fillview(Group group) {

		if (!TextUtils.isEmpty(group.getHeadPortRait())) {
			CloudImageLoader.getInstance().getImageLoader()
					.displayImage(group.getHeadPortRait(), imageGroupAvator);
		}
		tvGroupName.setText(group.getName());
		tvGroupDescription.setText(group.getDescription());

	}

	// TODO 需要被重构的部分代码
	private void handleMessageTask(final int messageid, final String response) {
		final String accessToken = CurrentAccountManager.getCurAccount()
				.getAccessToken();

		JSONObject joPermission = new JSONObject();
		try {
			joPermission.put("alert",
					cbGroupAlert.isChecked() ? Constants.ALERT_POPUP
							: Constants.ALERT_QUIET);
			joPermission.put("isupload", cbGroupUpload.isChecked() ? "Y" : "N");
		} catch (Exception e) {
			e.printStackTrace();
		}
		MarkMessageResponseTask task = new MarkMessageResponseTask(this,
				accessToken, messageid, response, joPermission.toString());
		task.setTaskHost(new TaskHost() {
			@Override
			public void onPostExecute(int requestCode, BaseResult result) {
				super.onPostExecute(requestCode, result);
				if (result.isSuccess()) {
					if (result.isServerDisposeSuccess()) {
						if (response.equals("Y")) {
							PropertyCenter.getInstance().firePropertyChange(
									PropertyCenter.PROPERTY_REFRESH_GROUPLIST,
									null, null);
						}
						finish();
					} else {
						ErrorDialogUtil.showErrorDialog(
								GroupNotifyProcessActivity.this,
								ProxyErrorCode.TYPE_GROUP,
								result.getErrorCode(), true);
					}
				}
			}
		});
		task.execute();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case id.actionbar_left:
			finish();
			break;
		case R.id.button_refused:
			handleMessageTask(messageid, "N");
			break;
		case R.id.button_accepted:
			handleMessageTask(messageid, "Y");
			break;
		default:
			break;
		}
	}

	private void doViewGroupTask(int groupID) {

		GroupHelper.doGetGroupTask(this, CurrentAccountManager.getCurAccount()
				.getAccessToken(), groupID, new TaskHost() {
			@Override
			public void onPostExecute(int requestCode, BaseResult result) {
				super.onPostExecute(requestCode, result);
				if (result.isSuccess()) {
					NetworkClientResult res = (NetworkClientResult) result;
					if (result.isServerDisposeSuccess()) {
						group = Group.createGroup(res.getResponseResult(),
								CurrentAccountManager.getCurAccount()
										.getAccountID());
						fillview(group);
					} else {
						// ErrorDialogUtil.showErrorDialog(
						// GroupNotifyProcessActivity.this,
						// ProxyErrorCode.TYPE_GROUP,
						// result.getErrorCode(), true);
						showErrorDialog();
					}
				}
			}
		});
	}

	private void showErrorDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(R.string.group_been_dissolve);
		builder.setPositiveButton(R.string.public_submit,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						AccountHelper.doMarkNotificationProcessTask(
								GroupNotifyProcessActivity.this,
								CurrentAccountManager.getCurAccount()
										.getAccessToken(), messageid, "D",
								new TaskHost() {
									@Override
									public void onPostExecute(int requestCode,
											BaseResult result) {
										super.onPostExecute(requestCode, result);
										if (result.isSuccess()) {
											if (result.isServerDisposeSuccess()) {
												finish();
											} else {
												ErrorDialogUtil
														.showErrorDialog(
																GroupNotifyProcessActivity.this,
																ProxyErrorCode.TYPE_GROUP,
																result.getErrorCode(),
																true);
											}
										}
									}
								});
					}
				});
		builder.setCancelable(false);
		builder.create().show();

	}
}

/**
 * 
 */
package com.medzone.cloud.ui;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

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
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.LayoutParams;
import com.medzone.cloud.CloudImageLoader;
import com.medzone.cloud.Constants;
import com.medzone.cloud.data.CurrentAccountManager;
import com.medzone.cloud.data.PropertyCenter;
import com.medzone.cloud.data.TemporaryData;
import com.medzone.cloud.data.helper.GroupHelper;
import com.medzone.cloud.defender.CloudPush;
import com.medzone.cloud.task.DelGroupMemberTask;
import com.medzone.cloud.task.EditGroupMemberPermissionTask;
import com.medzone.cloud.task.EditGroupSettingTask;
import com.medzone.cloud.ui.dialog.error.ErrorDialogUtil;
import com.medzone.framework.data.bean.imp.Account;
import com.medzone.framework.data.bean.imp.Group;
import com.medzone.framework.data.bean.imp.GroupMember;
import com.medzone.framework.errorcode.ProxyCode.LocalError;
import com.medzone.framework.errorcode.ProxyErrorCode;
import com.medzone.framework.network.NetworkClientResult;
import com.medzone.framework.task.BaseResult;
import com.medzone.framework.task.TaskHost;
import com.medzone.framework.util.TaskUtil;
import com.medzone.framework.util.ToastUtils;
import com.medzone.mcloud.R;
import com.medzone.mcloud.R.drawable;
import com.medzone.mcloud.R.id;
import com.medzone.mcloud.R.string;

/**
 * @author ChenJunQi.
 * 
 */
public class GroupServiceForMemberDetailActivity extends BaseActivity implements
		OnClickListener, PropertyChangeListener {

	private CheckBox cbReceive, cbUpload, cbIsView, cbIsTest;
	private TextView tvGroupName, tvGroupDescription, tvGroupVerify;
	private ImageView imGroupIcon;
	private GroupMember groupMember;
	private Group group;
	private Account account;
	private Button btnExitGroup;
	private boolean isChanged = false;

	public void setChanged(boolean isChanged) {
		this.isChanged = isChanged;
	}

	public boolean isChanged() {
		return isChanged;
	}

	// FIXME 冗余的群设置默认值[1]
	private int getAlertValue(Boolean isPopupAlert) {
		if (isPopupAlert == null)
			return Constants.ALERT_POPUP;
		return isPopupAlert ? Constants.ALERT_POPUP : Constants.ALERT_QUIET;
	}

	// FIXME 冗余的群设置默认值[2]
	private boolean getAlertCheck(Integer alertValue) {
		if (alertValue == null) {
			return true;
		}
		return (alertValue == Constants.ALERT_POPUP) ? true : false;
	}

	@Override
	protected void onStart() {
		super.onStart();
		PropertyCenter.getInstance().addPropertyChangeListener(this);
	}

	@Override
	protected void preLoadData(Bundle savedInstanceState) {
		super.preLoadData(savedInstanceState);
		account = CurrentAccountManager.getCurAccount();
		group = (Group) TemporaryData.get(Group.class.getName());

		if (group.getCreatorID() == null) {
			ToastUtils.show(this, "该群出现异常，请确认改该群是否存在");
			finish();
		} else {
			groupMember = new GroupMember();
			groupMember.setGroupID(group.getGroupID());

			GroupHelper.doGetMemberPermissionTask(this, CurrentAccountManager
					.getCurAccount().getAccessToken(), group.getGroupID(),
					group.getCreatorID(), new TaskHost() {
						@Override
						public void onPostExecute(int requestCode,
								BaseResult result) {
							super.onPostExecute(requestCode, result);
							if (result.isSuccess()) {
								NetworkClientResult res = (NetworkClientResult) result;
								if (result.isServerDisposeSuccess()) {

									groupMember = GroupMember.updateGroup(
											res.getResponseResult(),
											groupMember);

									loadViewData();
								} else {
									ErrorDialogUtil
											.showErrorDialog(
													GroupServiceForMemberDetailActivity.this,
													ProxyErrorCode.TYPE_GROUP,
													result.getErrorCode(), true);
								}
							}
						}
					});
		}
	}

	@Override
	protected void preInitUI() {
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
		title.setText(string.actionbar_title_groupmember_service_datum);
		actionBar.setCustomView(view, params);
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		actionBar.setDisplayShowCustomEnabled(true);
	}

	@Override
	protected void initUI() {
		setContentView(R.layout.activity_group_member_service_detail);
		cbIsTest = (CheckBox) this.findViewById(R.id.cb_allow_measure);
		cbIsView = (CheckBox) this.findViewById(R.id.cb_allow_view);
		cbReceive = (CheckBox) this.findViewById(R.id.cb_receive_message);
		cbUpload = (CheckBox) this.findViewById(R.id.cb_upload_message);

		imGroupIcon = (ImageView) this.findViewById(R.id.iv_group_icon);

		btnExitGroup = (Button) this.findViewById(R.id.button_dissolve);

		tvGroupDescription = (TextView) this
				.findViewById(R.id.tv_group_description);
		tvGroupName = (TextView) this.findViewById(R.id.tv_group_name);
		tvGroupVerify = (TextView) this.findViewById(R.id.tv_group_verify);

	}

	@Override
	protected void postInitUI() {

		cbIsTest.setOnCheckedChangeListener(new CustomCheckedChangeListener());
		cbIsView.setOnCheckedChangeListener(new CustomCheckedChangeListener());
		cbReceive.setOnCheckedChangeListener(new CustomCheckedChangeListener());
		cbUpload.setOnCheckedChangeListener(new CustomCheckedChangeListener());
		btnExitGroup.setOnClickListener(this);

	}

	private void fillAvatarView() {
		if (!TextUtils.isEmpty(group.getHeadPortRait())) {
			CloudImageLoader.getInstance().getImageLoader()
					.displayImage(group.getHeadPortRait(), imGroupIcon);
		}
	}

	public void loadViewData() {
		tvGroupDescription.setText(group.getDescription());
		tvGroupName.setText(group.getName());
		tvGroupVerify.setText(group.getVerify());
		fillAvatarView();
		cbUpload.setChecked(group.getSettingUpload() == null ? false : group
				.getSettingUpload());
		cbReceive.setChecked(getAlertCheck(group.getSettingAlert()));
		cbIsTest.setChecked(groupMember.isMeasure() == null ? false
				: groupMember.isMeasure());
		cbIsView.setChecked(groupMember.isViewHistory() == null ? false
				: groupMember.isViewHistory());
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		PropertyCenter.getInstance().removePropertyChangeListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.actionbar_left:
			finish();
			break;
		case R.id.button_dissolve:
			exitGroup();
			break;
		default:
			break;
		}
	}

	public void exitGroup() {
		AlertDialog.Builder builder = new AlertDialog.Builder(
				GroupServiceForMemberDetailActivity.this);
		builder.setMessage(string.group_server_exit_description);
		builder.setCancelable(true);
		builder.setPositiveButton(string.action_confirm,
				new android.content.DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						exitGroupTask();
					}
				});
		builder.setNegativeButton(string.action_cancel, null);
		builder.show();

	}

	public void exitGroupTask() {
		DelGroupMemberTask task = new DelGroupMemberTask(this,
				account.getAccessToken(), group.getGroupID(), null);
		task.setTaskHost(new TaskHost() {
			@Override
			public void onPostExecute(int requestCode, BaseResult result) {
				super.onPostExecute(requestCode, result);
				if (result.isSuccess()) {
					if (result.isServerDisposeSuccess()) {
						jumpUpActivity();
					} else {
						loadViewData();
						ErrorDialogUtil.showErrorDialog(
								GroupServiceForMemberDetailActivity.this,
								ProxyErrorCode.TYPE_GROUP,
								result.getErrorCode(), true);
					}
				} else {
					loadViewData();
				}
			}
		});
		task.execute();
	}

	private void updateGroupSettingTask(final Boolean isPopupAlert,
			final Boolean settingUpload) {

		EditGroupSettingTask task = new EditGroupSettingTask(this,
				account.getAccessToken(), group.getGroupID(), settingUpload,
				getAlertValue(isPopupAlert));
		task.setTaskHost(new TaskHost() {
			@Override
			public void onPostExecute(int requestCode, BaseResult result) {
				super.onPostExecute(requestCode, result);
				if (result.isSuccess()) {
					if (result.isServerDisposeSuccess()) {

						group.setSettingAlert(getAlertValue(isPopupAlert));
						group.setSettingUpload(settingUpload);
						setChanged(true);
					} else {
						loadViewData();
						ErrorDialogUtil.showErrorDialog(
								GroupServiceForMemberDetailActivity.this,
								ProxyErrorCode.TYPE_GROUP,
								result.getErrorCode(), true);
					}
				} else {
					loadViewData();
				}
			}
		});
		task.execute();
	}

	public void updateGroupMemberPermissionTask(final Boolean isTest,
			final Boolean isView) {
		EditGroupMemberPermissionTask task = new EditGroupMemberPermissionTask(
				this, account.getAccessToken(), group.getCreatorID(),
				group.getGroupID(), isView, null, isTest, null);
		task.setTaskHost(new TaskHost() {
			@Override
			public void onPostExecute(int requestCode, BaseResult result) {
				super.onPostExecute(requestCode, result);
				if (result.isSuccess()) {
					if (result.isServerDisposeSuccess()) {
						groupMember.setMeasure(isTest);
						groupMember.setViewHistory(isView);
						setChanged(true);
					} else {
						loadViewData();
						ErrorDialogUtil.showErrorDialog(
								GroupServiceForMemberDetailActivity.this,
								ProxyErrorCode.TYPE_GROUP,
								result.getErrorCode(), true);
					}
				} else {
					loadViewData();
				}
			}
		});
		task.execute();
	}

	public class CustomCheckedChangeListener implements OnCheckedChangeListener {

		@Override
		public void onCheckedChanged(CompoundButton buttonView,
				boolean isChecked) {
			switch (buttonView.getId()) {
			case R.id.cb_allow_measure:
				updateGroupMemberPermissionTask(isChecked, null);
				break;
			case R.id.cb_allow_view:
				updateGroupMemberPermissionTask(null, isChecked);
				break;
			case R.id.cb_upload_message:
				updateGroupSettingTask(getAlertCheck(group.getSettingAlert()),
						isChecked);
				break;
			case R.id.cb_receive_message:
				updateGroupSettingTask(isChecked, group.getSettingUpload());
				break;
			}
		}

	}

	public void jumpUpActivity() {
		startActivity(new Intent(this, MainTabsActivity.class));
		finish();
	}

	@Override
	public void propertyChange(PropertyChangeEvent event) {
		if (event.getPropertyName().equals(
				PropertyCenter.PROPERTY_REFRESH_AVATAR)) {
			fillAvatarView();

		} else if (event.getPropertyName().equals(
				PropertyCenter.PROPERTY_NOTIFY_GROUP_KICKED)) {

			CloudPush push = (CloudPush) event.getNewValue();
			final int groupID = push.getExtraGroupID();

			// 服务群只有可能是群成员被踢出
			if (groupID == group.getGroupID().intValue()) {
				if (isActive && TaskUtil.isTopActivity(this))
					ErrorDialogUtil.showKickedErrorDialog(this,
							ProxyErrorCode.TYPE_GROUP, LocalError.CODE_12205,
							group.getName());
			}
		}

	}

	@Override
	public void finish() {
		if (isChanged()) {
			PropertyCenter.getInstance().firePropertyChange(
					PropertyCenter.PROPERTY_REFRESH_GROUPLIST, null, null);
		}
		super.finish();
	}

}

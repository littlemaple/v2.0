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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.LayoutParams;
import com.medzone.cloud.CloudImageLoader;
import com.medzone.cloud.Constants;
import com.medzone.cloud.data.CurrentAccountManager;
import com.medzone.cloud.data.PropertyCenter;
import com.medzone.cloud.data.TemporaryData;
import com.medzone.cloud.task.DelGroupMemberTask;
import com.medzone.cloud.task.EditGroupSettingTask;
import com.medzone.cloud.ui.dialog.error.ErrorDialogUtil;
import com.medzone.framework.data.bean.imp.Account;
import com.medzone.framework.data.bean.imp.Group;
import com.medzone.framework.errorcode.ProxyErrorCode;
import com.medzone.framework.network.NetworkClientResult;
import com.medzone.framework.task.BaseResult;
import com.medzone.framework.task.TaskHost;
import com.medzone.framework.util.ToastUtils;
import com.medzone.mcloud.R;
import com.medzone.mcloud.R.drawable;
import com.medzone.mcloud.R.id;
import com.medzone.mcloud.R.string;

/**
 * @author ChenJunQi.
 * 
 */
public class GroupServiceForOwnerDetailActivity extends BaseActivity implements
		OnClickListener, PropertyChangeListener {

	private LinearLayout llayoutGroupMember/* , llGroup */;
	private TextView tvGroupName, tvGroupDescription, tvGroupVerify;
	private ImageView imGroupIcon;
	private CheckBox cbReceive;
	private Account account;
	private Group group;
	private Button btnExitGroup;

	// FIXME 冗余的群设置默认值[3]
	private int getAlertValue(Boolean isPopupAlert) {
		if (isPopupAlert == null) {
			return Constants.ALERT_POPUP;
		}
		return isPopupAlert ? Constants.ALERT_POPUP : Constants.ALERT_QUIET;
	}

	// FIXME 冗余的群设置默认值[4]
	private boolean getAlertCheck(Integer alertValue) {
		if (alertValue == null) {
			return true;
		}
		return (alertValue == Constants.ALERT_POPUP) ? true : false;
	}

	@Override
	protected void preLoadData(Bundle savedInstanceState) {
		super.preLoadData(savedInstanceState);
		group = (Group) TemporaryData.get(Group.class.getName());
		account = CurrentAccountManager.getCurAccount();
		if (group.getCreatorID() == null) {
			ToastUtils.show(this, "该群出现异常，请确认改该群是否存在");
			finish();
		}
		PropertyCenter.getInstance().addPropertyChangeListener(this);

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
		setContentView(R.layout.activity_group_owner_service_detail);
		llayoutGroupMember = (LinearLayout) findViewById(R.id.ll_group_member);
		// llGroup = (LinearLayout) findViewById(R.id.ll_group);
		cbReceive = (CheckBox) findViewById(R.id.cb_receive_message);
		imGroupIcon = (ImageView) findViewById(R.id.iv_group_icon);
		tvGroupDescription = (TextView) findViewById(R.id.tv_group_description);
		tvGroupName = (TextView) findViewById(R.id.tv_group_name);
		tvGroupVerify = (TextView) findViewById(R.id.tv_group_verify);
		btnExitGroup = (Button) findViewById(R.id.button_dissolve);

	}

	private void fillAvatarView() {
		if (!TextUtils.isEmpty(group.getHeadPortRait())) {
			CloudImageLoader.getInstance().getImageLoader()
					.displayImage(group.getHeadPortRait(), imGroupIcon);
		}
	}

	public void fillView() {
		fillAvatarView();
		tvGroupDescription.setText(group.getDescription());
		tvGroupName.setText(group.getName());
		tvGroupVerify.setText(group.getVerify());
		cbReceive.setChecked(getAlertCheck(group.getSettingAlert()));
	}

	@Override
	protected void postInitUI() {
		fillView();
		regisiterEvent();
	}

	private void regisiterEvent() {
		btnExitGroup.setOnClickListener(this);
		// llGroup.setOnClickListener(this);
		llayoutGroupMember.setOnClickListener(this);
		cbReceive.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				updateGroupSettingTask(isChecked);
			}
		});
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
			this.finish();
			break;
		case R.id.ll_group_member:
			TemporaryData.save(Group.class.getName(), group);
			startActivity(new Intent(GroupServiceForOwnerDetailActivity.this,
					GroupMemberActivity.class));
			break;
		case R.id.button_dissolve:
			exitGroup();
			break;
		// 服务群群主暂时不允许编辑群资料
		// case R.id.ll_group:
		// TemporaryData.save(Group.class.getName(), group);
		// startActivity(new Intent(this, GroupResourceActivity.class));
		// break;
		default:
			break;
		}
	}

	public void exitGroup() {

		AlertDialog.Builder builder = new AlertDialog.Builder(
				GroupServiceForOwnerDetailActivity.this);
		String format = getResources().getString(
				string.group_server_exit_description);
		String exitMessage = String.format(format, group.getName());

		builder.setMessage(exitMessage);
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

	public void updateGroupSettingTask(final Boolean isReceive) {
		EditGroupSettingTask task = new EditGroupSettingTask(this,
				account.getAccessToken(), group.getGroupID(), null,
				getAlertValue(isReceive));
		task.setTaskHost(new TaskHost() {
			@Override
			public void onPostExecute(int requestCode, BaseResult result) {
				super.onPostExecute(requestCode, result);
				if (result.isSuccess()) {
					NetworkClientResult res = (NetworkClientResult) result;
					if (res.isServerDisposeSuccess()) {
						group.setSettingAlert(getAlertValue(isReceive));
					} else {
						ErrorDialogUtil.showErrorDialog(
								GroupServiceForOwnerDetailActivity.this,
								ProxyErrorCode.TYPE_GROUP, res.getErrorCode(),
								true);
						fillView();
					}
				} else {
					fillView();
				}
			}
		});
		task.execute();
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
						GroupServiceForOwnerDetailActivity.this.finish();
					} else {
						ErrorDialogUtil.showErrorDialog(
								GroupServiceForOwnerDetailActivity.this,
								ProxyErrorCode.TYPE_GROUP,
								result.getErrorCode(), true);
					}
				}
			}
		});
		task.execute();
	}

	@Override
	public void propertyChange(PropertyChangeEvent event) {
		if (event.getPropertyName().equals(
				PropertyCenter.PROPERTY_REFRESH_GROUPLIST)) {
			fillView();
		} else if (event.getPropertyName().equals(
				PropertyCenter.PROPERTY_REFRESH_AVATAR)) {
			fillAvatarView();
		}
	}
}

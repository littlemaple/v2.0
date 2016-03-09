/**
 * 
 */
package com.medzone.cloud.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.LayoutParams;
import com.medzone.cloud.CloudImageLoader;
import com.medzone.cloud.data.CurrentAccountManager;
import com.medzone.cloud.data.TemporaryData;
import com.medzone.cloud.data.helper.AccountHelper;
import com.medzone.cloud.data.helper.GroupHelper;
import com.medzone.cloud.task.AddGroupMemberTask;
import com.medzone.cloud.ui.dialog.error.ErrorDialogUtil;
import com.medzone.cloud.ui.widget.CleanableEditText;
import com.medzone.cloud.ui.widget.CustomDialogProgressWithImage;
import com.medzone.framework.data.bean.imp.Account;
import com.medzone.framework.data.bean.imp.Account.Gender;
import com.medzone.framework.data.bean.imp.Group;
import com.medzone.framework.errorcode.ProxyCode.LocalError;
import com.medzone.framework.errorcode.ProxyErrorCode;
import com.medzone.framework.network.NetworkClientResult;
import com.medzone.framework.task.BaseResult;
import com.medzone.framework.task.TaskHost;
import com.medzone.framework.util.TimeUtils;
import com.medzone.framework.view.RoundedImageView;
import com.medzone.mcloud.R;
import com.medzone.mcloud.R.drawable;
import com.medzone.mcloud.R.id;
import com.medzone.mcloud.R.string;

/**
 * @author ChenJunQi.
 * 
 */
public class GroupInviteActivity extends BaseActivity implements
		OnClickListener {

	private ImageView ivInvite;
	private RelativeLayout rlSms, rlEmail, rlWechat;
	private CleanableEditText targetEdit;
	private int groupID;

	private Account invitedAccount;

	@Override
	protected void preLoadData(Bundle savedInstanceState) {
		super.preLoadData(savedInstanceState);
		groupID = getIntent().getIntExtra(Group.NAME_FIELD_GROUP_ID, 0);
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
		title.setText(string.actionbar_title_group_invite_member);
		actionBar.setCustomView(view, params);
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		actionBar.setDisplayShowCustomEnabled(true);
	}

	@Override
	protected void initUI() {
		setContentView(R.layout.activity_invite_member);
		targetEdit = (CleanableEditText) this
				.findViewById(R.id.ce_search_member);
		ivInvite = (ImageView) this.findViewById(R.id.btnInvite);
		rlSms = (RelativeLayout) this.findViewById(R.id.relay_sms);
		rlEmail = (RelativeLayout) this.findViewById(R.id.relay_email);
		rlWechat = (RelativeLayout) this.findViewById(R.id.relay_wechat);

	}

	@Override
	protected void postInitUI() {
		ivInvite.setOnClickListener(this);
		rlSms.setOnClickListener(this);
		rlEmail.setOnClickListener(this);
		rlWechat.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnInvite:
			String target = targetEdit.getText().toString();
			final int errorCode = GroupHelper.checkInviteAccountName(target);
			if (errorCode == LocalError.CODE_SUCCESS) {
				doQueryAccountTask(target);
			} else {
				ErrorDialogUtil.showErrorDialog(GroupInviteActivity.this,
						ProxyErrorCode.TYPE_GROUP, errorCode, true);
			}
			break;
		case R.id.actionbar_left:
			this.finish();
			break;
		case R.id.relay_sms:
			// showToast("Dev:缺失SMS");
			break;
		case R.id.relay_email:
			// showToast("Dev:缺失Email");
			break;
		case R.id.relay_wechat:
			// showToast("Dev:缺失WeChat");
			break;
		default:
			break;
		}
	}

	private void doQueryAccountTask(String target) {
		CustomDialogProgressWithImage progress = new CustomDialogProgressWithImage(
				this, getResources().getString(R.string.searching_contact),
				getResources().getDrawable(drawable.set_ic_load));
		TaskHost taskHost = new TaskHost() {
			@Override
			public void onPostExecute(int requestCode, BaseResult result) {
				super.onPostExecute(requestCode, result);

				if (result.isSuccess()) {
					NetworkClientResult res = (NetworkClientResult) result;
					if (res.isServerDisposeSuccess()) {
						invitedAccount = Account.createAccount(res);
						TemporaryData.save(Account.class.getName(),
								invitedAccount);
						showInviteDetailDialog();
					} else {
						ErrorDialogUtil.showErrorDialog(
								GroupInviteActivity.this,
								ProxyErrorCode.TYPE_GROUP,
								result.getErrorCode(), true);
					}
				}
			}
		};
		AccountHelper
				.doGetAccountDetail(this, target, null, progress, taskHost);
	}

	private View getDialogView(Account invitedAccount) {
		View contentView = LayoutInflater.from(this).inflate(
				R.layout.activity_invite_member_detail, null);
		RoundedImageView rivMember = (RoundedImageView) contentView
				.findViewById(id.riv_member);
		TextView tvMemberName = (TextView) contentView
				.findViewById(id.tv_member_name);
		TextView tvMemberSex = (TextView) contentView
				.findViewById(id.tv_member_sex);
		TextView tvMemberAge = (TextView) contentView
				.findViewById(id.tv_member_age);

		CloudImageLoader.getInstance().getImageLoader()
				.displayImage(invitedAccount.getHeadPortRait(), rivMember);
		tvMemberName.setText(invitedAccount.getNickname() + "");
		tvMemberSex.setText(getResources().getString(R.string.gender_)
				+ (invitedAccount.isMale() ? Gender.MALE : Gender.FEMALE));
		tvMemberAge.setText(getResources().getString(R.string.age_)
				+ (int) TimeUtils.getGapYearByBirthday(invitedAccount
						.getBirthday()));
		return contentView;
	}

	private void showInviteDetailDialog() {

		if (isActive) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setCancelable(true);
			builder.setPositiveButton(string.action_invite,
					new android.content.DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							doInvitedTask(invitedAccount);
						}
					});
			builder.setNegativeButton(string.action_cancel, null);
			builder.setView(getDialogView(invitedAccount));
			builder.show();
		}

	}

	public void doInvitedTask(final Account paramAccount) {

		AddGroupMemberTask task = new AddGroupMemberTask(this,
				CurrentAccountManager.getCurAccount().getAccessToken(),
				groupID, paramAccount.getAccountID());
		task.setProgress(new CustomDialogProgressWithImage(this, getResources()
				.getString(R.string.group_inviting_member), getResources()
				.getDrawable(R.drawable.set_ic_load)));
		task.setTaskHost(new TaskHost() {
			@Override
			public void onPostExecute(int requestCode, BaseResult result) {
				super.onPostExecute(requestCode, result);
				if (result.isSuccess()) {
					NetworkClientResult res = (NetworkClientResult) result;
					if (res.isServerDisposeSuccess()) {
						ErrorDialogUtil.showErrorDialog(
								GroupInviteActivity.this,
								ProxyErrorCode.TYPE_GROUP,
								LocalError.CODE_12203, R.drawable.set_ic_ok);
						invitedAccount = null;
					} else {
						ErrorDialogUtil.showErrorDialog(
								GroupInviteActivity.this,
								ProxyErrorCode.TYPE_GROUP, res.getErrorCode(),
								true);
					}
				}
			}
		});
		task.execute();
	}
}

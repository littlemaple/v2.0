/**
 * 
 */
package com.medzone.cloud.ui;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.LayoutParams;
import com.medzone.cloud.CloudImageLoader;
import com.medzone.cloud.Constants;
import com.medzone.cloud.data.CurrentAccountManager;
import com.medzone.cloud.data.PropertyCenter;
import com.medzone.cloud.data.TemporaryData;
import com.medzone.cloud.data.helper.AccountHelper;
import com.medzone.cloud.data.helper.GroupHelper;
import com.medzone.cloud.task.EditGroupMemberPermissionTask;
import com.medzone.cloud.ui.dialog.error.ErrorDialogUtil;
import com.medzone.cloud.ui.fragment.group.GroupMemberManagerFragment;
import com.medzone.cloud.ui.widget.CleanableEditText;
import com.medzone.cloud.ui.widget.CleanableEditText.TextWatcherImplCompat;
import com.medzone.framework.data.bean.imp.Account;
import com.medzone.framework.data.bean.imp.Group;
import com.medzone.framework.data.bean.imp.GroupMember;
import com.medzone.framework.errorcode.ProxyCode;
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
 *         群内的成员资料卡，并非是个人资料卡页面
 */
public class GroupPersonDetailActivity extends BaseActivity implements
		OnClickListener, OnCheckedChangeListener, PropertyChangeListener {

	private static final int LEAVE_TYPE_MEASURE = 0;
	private static final int LEAVE_TYPE_HEALTH_CENTRE = 1;

	private LinearLayout llHelpMeasure, llViewHistory;
	private View line1, line2;
	private CheckBox cbExtraCareful, cbViewHistory, cbHelpMeasure;
	private CleanableEditText etGroupMemberMark;
	private TextView tvGroupMemberNickName, tvGroupMemberAge;
	private RoundedImageView imGroupMemberIcon;
	private Button btnTest, btnHealthCentre;

	private GroupMember groupMember;
	private Account otherAccount;

	private boolean isServiceGroup = false;
	private boolean isChanged = false;

	public void setChanged(boolean isChanged) {
		this.isChanged = isChanged;
	}

	public boolean isChanged() {
		return isChanged;
	}

	@Override
	protected void onStart() {
		super.onStart();
		PropertyCenter.getInstance().addPropertyChangeListener(this);
	}

	@Override
	protected void preLoadData(Bundle savedInstanceState) {
		super.preLoadData(savedInstanceState);

		if (TemporaryData.containsKey(GroupMember.class.getName())) {
			groupMember = (GroupMember) TemporaryData.get(GroupMember.class
					.getName());
			if (groupMember.getBindGroup() != null) {
				Group group = groupMember.getBindGroup();
				if (group.getType() == Group.TYPE_SERVICE) {
					isServiceGroup = true;
				}
			}
		} else {
			finish();
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
		title.setText(string.actionbar_title_groupmember_datum);
		actionBar.setCustomView(view, params);
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		actionBar.setDisplayShowCustomEnabled(true);
	}

	@Override
	protected void initUI() {
		setContentView(R.layout.activity_group_person_detail);
		cbHelpMeasure = (CheckBox) this.findViewById(R.id.tBtnIsTest);
		cbViewHistory = (CheckBox) this.findViewById(R.id.tBtnIsView);
		cbExtraCareful = (CheckBox) this.findViewById(R.id.tBtnExtraCare);
		llHelpMeasure = (LinearLayout) this.findViewById(R.id.ll_help_measure);
		llViewHistory = (LinearLayout) this.findViewById(R.id.ll_view_history);
		line1 = (View) this.findViewById(R.id.view_line_1);
		line2 = (View) this.findViewById(R.id.view_line_2);
		btnTest = (Button) this.findViewById(R.id.btn_test);
		btnHealthCentre = (Button) this.findViewById(R.id.btn_health_centre);

		imGroupMemberIcon = (RoundedImageView) this
				.findViewById(R.id.tv_group_member_icon);

		etGroupMemberMark = (CleanableEditText) this
				.findViewById(R.id.tv_group_member_mark);
		tvGroupMemberNickName = (TextView) this
				.findViewById(R.id.tv_group_member_nick);
		tvGroupMemberAge = (TextView) this
				.findViewById(R.id.tv_group_member_age);

		AccountHelper.doGetAccountDetail(this, groupMember.getAccountID()
				.toString(), null, null, new TaskHost() {
			@Override
			public void onPostExecute(int requestCode, BaseResult result) {
				super.onPostExecute(requestCode, result);
				if (result.isSuccess()) {
					NetworkClientResult res = (NetworkClientResult) result;
					if (res.isServerDisposeSuccess()) {
						otherAccount = Account
								.createAccount((NetworkClientResult) result);
						fillView();
					} else {
						ErrorDialogUtil.showErrorDialog(
								GroupPersonDetailActivity.this,
								ProxyErrorCode.TYPE_GROUP,
								result.getErrorCode(), true);
					}
				}
			}
		});
		fillView();
	}

	private void fillAvatarView() {
		if (!TextUtils.isEmpty(groupMember.getHeadPortRait())) {
			CloudImageLoader
					.getInstance()
					.getImageLoader()
					.displayImage(groupMember.getHeadPortRait(),
							imGroupMemberIcon);
		}
	}

	public void fillView() {

		if (isServiceGroup) {
			llHelpMeasure.setVisibility(View.GONE);
			llViewHistory.setVisibility(View.GONE);
			line1.setVisibility(View.GONE);
			line2.setVisibility(View.GONE);
		} else {
			llHelpMeasure.setVisibility(View.VISIBLE);
			llViewHistory.setVisibility(View.VISIBLE);
			line1.setVisibility(View.VISIBLE);
			line2.setVisibility(View.VISIBLE);
		}
		fillAvatarView();
		if (groupMember.getRemark() != null) {
			etGroupMemberMark.setText(groupMember.getRemark());
		}
		if (groupMember.getNickname() != null) {
			tvGroupMemberNickName.setText(groupMember.getNickname());
		}
		if (otherAccount != null && otherAccount.getBirthday() != null) {
			Float age = TimeUtils.getGapYearByBirthday(otherAccount
					.getBirthday());
			tvGroupMemberAge.setText(String.valueOf(age.intValue()));
		}
		cbExtraCareful.setChecked(groupMember.isCare() == null ? false
				: groupMember.isCare());
		cbHelpMeasure.setChecked(groupMember.isMeasure() == null ? false
				: groupMember.isMeasure());
		cbViewHistory.setChecked(groupMember.isViewHistory() == null ? false
				: groupMember.isViewHistory());

	}

	@Override
	protected void postInitUI() {

		btnTest.setOnClickListener(this);
		btnHealthCentre.setOnClickListener(this);
		cbHelpMeasure.setOnCheckedChangeListener(this);
		cbViewHistory.setOnCheckedChangeListener(this);
		cbExtraCareful.setOnCheckedChangeListener(this);
		etGroupMemberMark
				.setOnEditorActionListener(new OnEditorActionListener() {

					@Override
					public boolean onEditorAction(TextView v, int actionId,
							KeyEvent event) {
						if (actionId == EditorInfo.IME_ACTION_GO) {
							doRemarkPerformClick();
						}
						return false;
					}
				});

		etGroupMemberMark.setTextWatcherImplCompat(new TextWatcherImplCompat() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onClickRightDrawable() {
				// TODO Auto-generated method stub
				doRemarkPerformClick();
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub

			}
		});
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		PropertyCenter.getInstance().removePropertyChangeListener(this);
	}

	public void doChangeMarkInfo(final String remark) {

		EditGroupMemberPermissionTask task = new EditGroupMemberPermissionTask(
				this, CurrentAccountManager.getCurAccount().getAccessToken(),
				otherAccount.getAccountID(), groupMember.getGroupID(), null,
				null, null, remark);
		task.setTaskHost(new TaskHost() {
			@Override
			public void onPostExecute(int requestCode, BaseResult result) {
				super.onPostExecute(requestCode, result);
				if (result.isSuccess()) {
					if (result.isServerDisposeSuccess()) {
						groupMember.setRemark(remark);
						// 如果是特别关心的人，则要通知群列表更新
						PropertyCenter.getInstance().firePropertyChange(
								PropertyCenter.PROPERTY_REFRESH_GROUPLIST,
								null, null);
						// 刷新聊天页面群成员的列表
						PropertyCenter.getInstance().firePropertyChange(
								PropertyCenter.PROPERTY_REFRESH_CHAT_MEMBER,
								null, null);
						// 通知所有群列表处进行更新,包括聊天室列表
						// 获取viewpager当前的页码
						saveCurrentItem();
						PropertyCenter.getInstance().firePropertyChange(
								PropertyCenter.PROPERTY_REFRESH_GROUP_MEMBER,
								null, null);
						setChanged(true);
					} else {
						ErrorDialogUtil.showErrorDialog(
								GroupPersonDetailActivity.this,
								ProxyErrorCode.TYPE_GROUP,
								result.getErrorCode(), true);
						fillView();
					}
				} else {
					fillView();
				}
			}
		});
		task.execute();
	}

	private void saveCurrentItem() {
		// 获取viewpager当前的页码
		if (GroupMemberManagerFragment.mPager != null) {
			int position = GroupMemberManagerFragment.mPager.getCurrentItem();
			TemporaryData.save(GroupMemberManagerFragment.mCurrentItem,
					position);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.actionbar_left:
			this.finish();
			break;
		case R.id.btn_test:
			prepareLoadReserveAuthoForMe(false);
			break;
		case R.id.btn_health_centre:
			prepareLoadReserveAuthoForMe(true);
			break;
		default:
			break;
		}
	}

	private void doRemarkPerformClick() {
		String remark = etGroupMemberMark.getText().toString().trim();

		int errorCode = GroupHelper.checkRemarkStyle(remark);
		if (errorCode == LocalError.CODE_SUCCESS) {
			doChangeMarkInfo(remark);
		} else {
			ErrorDialogUtil.showErrorDialog(GroupPersonDetailActivity.this,
					ProxyErrorCode.TYPE_GROUP, errorCode, true);
		}
		etGroupMemberMark.clearFocus();
		KeyBoardCancle();
	}

	private void prepareLoadReserveAuthoForMe(final boolean isView) {

		String accessToken = CurrentAccountManager.getCurAccount()
				.getAccessToken();
		GroupHelper.doGetReserveAuthorizedListTask(this, accessToken,
				groupMember, new TaskHost() {
					@Override
					public void onPostExecute(int requestCode, BaseResult result) {
						super.onPostExecute(requestCode, result);

						if (result.isSuccess()) {

							NetworkClientResult res = (NetworkClientResult) result;
							if (res.isServerDisposeSuccess()) {
								postLoadClickEvent(isView);
							} else {
								ErrorDialogUtil.showErrorDialog(
										GroupPersonDetailActivity.this,
										ProxyErrorCode.TYPE_GROUP,
										result.getErrorCode(), true);
							}
						}
					}
				});
	}

	private void postLoadClickEvent(final boolean isView) {
		if (!isView) {
			performMeasureButtonClick();
		} else {
			performViewButtonClick();
		}
	}

	private void performMeasureButtonClick() {
		if (groupMember.isMeasureForMe().booleanValue()) {
			doActionClick(LEAVE_TYPE_MEASURE);
		} else {
			ErrorDialogUtil.showErrorDialog(GroupPersonDetailActivity.this,
					ProxyErrorCode.TYPE_GROUP, ProxyCode.LocalError.CODE_12403,
					true);
		}
	}

	private void performViewButtonClick() {
		if (groupMember.isViewHistoryForMe().booleanValue()) {
			doActionClick(LEAVE_TYPE_HEALTH_CENTRE);
		} else {
			ErrorDialogUtil.showErrorDialog(GroupPersonDetailActivity.this,
					ProxyErrorCode.TYPE_GROUP, ProxyCode.LocalError.CODE_12404,
					true);
		}
	}

	public void editMemberPermissionTask(final Boolean isView,
			final Boolean isCare, final Boolean isTest) {
		// 防止空指针异常
		if (otherAccount == null)
			return;
		EditGroupMemberPermissionTask task = new EditGroupMemberPermissionTask(
				this, CurrentAccountManager.getCurAccount().getAccessToken(),
				otherAccount.getAccountID(), groupMember.getGroupID(), isView,
				isCare, isTest, null);
		task.setTaskHost(new TaskHost() {
			@Override
			public void onPostExecute(int requestCode, BaseResult result) {
				super.onPostExecute(requestCode, result);
				if (result.isSuccess()) {
					if (result.isServerDisposeSuccess()) {
						if (isView != null)
							groupMember.setViewHistory(isView);
						if (isCare != null)
							groupMember.setCare(isCare);
						if (isTest != null)
							groupMember.setMeasure(isTest);
						// TODO 目前采用TemporaryData 传item值，后期考虑其他方式
						saveCurrentItem();
						setChanged(true);
						PropertyCenter.getInstance().firePropertyChange(
								PropertyCenter.PROPERTY_REFRESH_CHAT_MEMBER,
								null, null);
						PropertyCenter.getInstance().firePropertyChange(
								PropertyCenter.PROPERTY_REFRESH_GROUP_MEMBER,
								null, null);

					} else {
						ErrorDialogUtil.showErrorDialog(
								GroupPersonDetailActivity.this,
								ProxyErrorCode.TYPE_GROUP,
								result.getErrorCode(), true);
						fillView();
					}
				} else {
					fillView();
				}
			}
		});
		task.execute();
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		switch (buttonView.getId()) {
		case R.id.tBtnExtraCare:
			editMemberPermissionTask(null, cbExtraCareful.isChecked(), null);
			break;
		case R.id.tBtnIsTest:
			editMemberPermissionTask(null, null, cbHelpMeasure.isChecked());
			break;
		case R.id.tBtnIsView:
			editMemberPermissionTask(cbViewHistory.isChecked(), null, null);
			break;
		default:
			break;
		}
	}

	private void doActionClick(int type) {
		if (otherAccount == null) {
			ErrorDialogUtil.showErrorDialog(GroupPersonDetailActivity.this,
					ProxyErrorCode.TYPE_GROUP, ProxyCode.LocalError.CODE_12416,
					true);
		} else {
			/*
			 * CloudMeasureModuleCentreRoot.doGetModuleSpec(otherAccount, this,
			 * false);
			 */

			switch (type) {
			case LEAVE_TYPE_MEASURE:
				jump2CentreMeasureActivity();
				break;
			case LEAVE_TYPE_HEALTH_CENTRE:
				jump2HealthCentreActivity();
				break;
			default:
				break;
			}

		}
	}

	private void jump2CentreMeasureActivity() {

		TemporaryData.save(Constants.TEMPORARYDATA_KEY_TEST_ACCOUNT,
				groupMember);
		Intent intent = new Intent(this, CentreDetectionActivity.class);
		startActivity(intent);
	}

	private void jump2HealthCentreActivity() {

		TemporaryData.save(Constants.TEMPORARYDATA_KEY_VIEW_ACCOUNT,
				otherAccount);
		Intent intent = new Intent(this, HealthCentreWebViewActivity.class);
		startActivity(intent);

	}

	@Override
	public void propertyChange(PropertyChangeEvent event) {
		// if (TextUtils.equals(event.getPropertyName(),
		// CloudMeasureModule.class.getSimpleName())) {
		// switch (leaveType) {
		// case LEAVE_TYPE_MEASURE:
		// jump2CentreMeasureActivity();
		// break;
		// case LEAVE_TYPE_HEALTH_CENTRE:
		// jump2HealthCentreActivity();
		// break;
		// }
		// }
		if (event.getPropertyName().equals(
				PropertyCenter.PROPERTY_REFRESH_AVATAR)) {
			fillAvatarView();
		}
	}

	@Override
	public void finish() {
		if (isChanged()) {
			// 如果是特别关心的人，则要通知群列表更新
			PropertyCenter.getInstance().firePropertyChange(
					PropertyCenter.PROPERTY_REFRESH_GROUPLIST, null, null);
			PropertyCenter.getInstance().firePropertyChange(
					PropertyCenter.PROPERTY_REFRESH_GROUP_MEMBER, null, null);
		}
		super.finish();
	}
}

/**
 * 
 */
package com.medzone.cloud.ui;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask.Status;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Base64;
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
import com.medzone.cloud.data.helper.GroupHelper;
import com.medzone.cloud.defender.CloudPush;
import com.medzone.cloud.task.DelGroupMemberTask;
import com.medzone.cloud.task.DelGroupTask;
import com.medzone.cloud.task.EditGroupSettingTask;
import com.medzone.cloud.task.EditGroupTask;
import com.medzone.cloud.ui.dialog.error.ErrorDialogUtil;
import com.medzone.cloud.ui.fragment.group.GroupMemberManagerFragment;
import com.medzone.framework.Config;
import com.medzone.framework.Log;
import com.medzone.framework.data.bean.imp.Group;
import com.medzone.framework.data.bean.imp.GroupMember;
import com.medzone.framework.errorcode.ProxyCode;
import com.medzone.framework.errorcode.ProxyCode.LocalError;
import com.medzone.framework.errorcode.ProxyCode.NetError;
import com.medzone.framework.errorcode.ProxyErrorCode;
import com.medzone.framework.network.NetworkClientResult;
import com.medzone.framework.task.BaseResult;
import com.medzone.framework.task.TaskHost;
import com.medzone.framework.util.TaskUtil;
import com.medzone.framework.util.ToastUtils;
import com.medzone.framework.util.Tools;
import com.medzone.framework.view.RoundedImageView;
import com.medzone.mcloud.R;
import com.medzone.mcloud.R.array;
import com.medzone.mcloud.R.drawable;
import com.medzone.mcloud.R.id;
import com.medzone.mcloud.R.layout;
import com.medzone.mcloud.R.string;

/**
 * @author ChenJunQi.
 * 
 */
public class GroupDatumActivity extends BaseActivity implements
		OnClickListener, PropertyChangeListener {

	private static final int IMAGE_REQUEST_CODE = 0;
	private static final int CAMERA_REQUEST_CODE = 1;
	private static final int RESULT_REQUEST_CODE = 2;
	// private static final int MODIFIED_PREGNANT_CODE = 3;
	// private static final int MODIFIED_BODYSTATE_CODE = 4;

	private Group curGroup;

	private RoundedImageView imageGroupAvator;
	private TextView tvGroupName;
	private TextView tvGroupDescription;
	private ImageView ivRightIcon;
	private CheckBox cbGroupAlert, cbGroupUpload;

	private Button btnExited, btnDismissed;
	private LinearLayout llGroup;

	private DelGroupMemberTask delGroupMemberTask;
	private EditGroupSettingTask editGroupSettingTask;
	private EditGroupTask editGroupTask;
	private DelGroupTask delGroupTask;
	private View hideViewCenter, hideViewTop, hideViewBottom;

	// 目前针对的是推送到群成员发生变更时 返回到该界面刷新
	private boolean isRefreshGroupMember = false;

	private String[] items;

	private boolean isChanged = false;

	public void setChanged(boolean isChanged) {
		this.isChanged = isChanged;
	}

	public boolean isChanged() {
		return isChanged;
	}

	@Override
	protected void preLoadData(Bundle savedInstanceState) {
		super.preLoadData(savedInstanceState);
		items = getResources().getStringArray(array.avatar_setting_values);
		curGroup = (Group) TemporaryData.get(Group.class.getName());
		PropertyCenter.getInstance().addPropertyChangeListener(this);
		initActionBar();
	}

	private void initActionBar() {
		ActionBar actionBar = getSupportActionBar();
		ActionBar.LayoutParams params = new ActionBar.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT,
				Gravity.CENTER);
		View view = LayoutInflater.from(this).inflate(
				R.layout.custom_actionbar_with_image, null);
		hideViewTop = view.findViewById(R.id.hide_view_top);
		TextView title = (TextView) view.findViewById(R.id.actionbar_title);
		ImageButton leftButton = (ImageButton) view
				.findViewById(id.actionbar_left);
		leftButton.setImageResource(drawable.public_ic_back);
		leftButton.setOnClickListener(this);
		title.setText(string.actionbar_title_group_datum);
		if (Config.isDeveloperMode) {
			title.append("" + curGroup.getGroupID());
		}
		actionBar.setCustomView(view, params);
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		actionBar.setDisplayShowCustomEnabled(true);
	}

	@Override
	protected void initUI() {
		setContentView(layout.activity_group_datum);
		llGroup = (LinearLayout) findViewById(id.ll_group);
		imageGroupAvator = (RoundedImageView) findViewById(R.id.iv_group_icon);
		tvGroupName = (TextView) findViewById(R.id.tv_group_name);
		tvGroupDescription = (TextView) findViewById(R.id.tv_group_description);
		cbGroupAlert = (CheckBox) findViewById(R.id.iv_new_notice);
		cbGroupUpload = (CheckBox) findViewById(R.id.iv_upload_message);
		btnExited = (Button) findViewById(R.id.button_exit);
		btnDismissed = (Button) findViewById(R.id.button_dissolve);

		ivRightIcon = (ImageView) findViewById(R.id.iv_right_icon);

		hideViewCenter = findViewById(R.id.hide_view_center);
		hideViewBottom = findViewById(R.id.hide_view_bottom);

	}

	@Override
	protected void postInitUI() {

		fillView();
		hideViewBottom.setOnClickListener(this);
		hideViewCenter.setOnClickListener(this);
		hideViewTop.setOnClickListener(this);

		if (curGroup.equals(CurrentAccountManager.getCurAccount())) {
			llGroup.setOnClickListener(this);
			imageGroupAvator.setOnClickListener(this);
			ivRightIcon.setVisibility(View.VISIBLE);
		}
		btnExited.setOnClickListener(this);
		btnDismissed.setOnClickListener(this);

		cbGroupAlert.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				doEditGroupSettingTask(null, isChecked ? Constants.ALERT_POPUP
						: Constants.ALERT_QUIET);
			}
		});
		cbGroupUpload.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				doEditGroupSettingTask(isChecked, null);
			}
		});

		doGetAllGroupMemberTask(curGroup.getGroupID(), 0);

	}

	private void fillAvatarView() {
		if (!TextUtils.isEmpty(curGroup.getHeadPortRait())) {
			CloudImageLoader.getInstance().getImageLoader()
					.displayImage(curGroup.getHeadPortRait(), imageGroupAvator);
		}
	}

	private void fillView() {

		fillAvatarView();
		tvGroupName.setText(curGroup.getName());
		tvGroupDescription.setText(curGroup.getDescription());
		cbGroupAlert
				.setChecked(curGroup.getSettingAlert() == Constants.ALERT_POPUP ? true
						: false);
		cbGroupUpload.setChecked(curGroup.getSettingUpload());

		if (CurrentAccountManager.getCurAccount().equals(curGroup)) {
			btnExited.setVisibility(View.GONE);
			btnDismissed.setVisibility(View.VISIBLE);
		} else {
			btnExited.setVisibility(View.VISIBLE);
			btnDismissed.setVisibility(View.GONE);
		}

	}

	@Override
	protected void onDestroy() {
		PropertyCenter.getInstance().removePropertyChangeListener(this);
		super.onDestroy();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if (isRefreshGroupMember) {
			doGetAllGroupMemberTask(curGroup.getGroupID(), 0);
			isRefreshGroupMember = false;
		}
	}

	/**
	 * 
	 * @param groupID
	 * @param currentItem
	 *            viewpager 当前页码
	 */
	private void doGetAllGroupMemberTask(long groupID, final int currentItem) {

		GroupHelper.doGetMembersList(this, CurrentAccountManager
				.getCurAccount().getAccessToken(), (int) groupID,
				new TaskHost() {
					@Override
					public void onPostExecute(int requestCode, BaseResult result) {
						super.onPostExecute(requestCode, result);
						if (result.isSuccess()) {
							NetworkClientResult res = (NetworkClientResult) result;
							if (res.isServerDisposeSuccess()) {

								List<GroupMember> memberList = GroupMember
										.createGroupMemberList(res,
												CurrentAccountManager
														.getCurAccount()
														.getAccountID());

								replaceFragment(memberList, currentItem);

							} else {
								if (result.getErrorCode() == NetError.CODE_43300) {
									// ……
								} else {
									ErrorDialogUtil.showErrorDialog(
											GroupDatumActivity.this,
											ProxyErrorCode.TYPE_GROUP,
											result.getErrorCode(), true);
								}
							}
						}
					}
				});

	}

	private void replaceFragment(List<GroupMember> memberList, int currentItem) {

		if (isActive) {
			FragmentManager fm = getSupportFragmentManager();
			FragmentTransaction ft = fm.beginTransaction();
			GroupMemberManagerFragment gvFragment = new GroupMemberManagerFragment();
			gvFragment.setContent(memberList, curGroup);
			gvFragment.setCurrentItem(currentItem);
			ft.replace(id.fragment_container, gvFragment);
			ft.disallowAddToBackStack();
			ft.commitAllowingStateLoss();
		} else {
			Log.e("activity has been destory,can not replace fragment");
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.button_exit:
			showExitGroupDialog();
			break;
		case R.id.button_dissolve:
			showDismissGroupDialog();
			break;
		case R.id.ll_group:
			TemporaryData.save(Group.class.getName(), curGroup);
			startActivity(new Intent(this, GroupResourceActivity.class));
			break;
		case R.id.actionbar_left:
			this.finish();
			break;
		case R.id.iv_group_icon:
			showAvatarDialog();
			break;
		case R.id.hide_view_bottom:
			toogle();
			break;
		case R.id.hide_view_center:
			toogle();
			break;
		case R.id.hide_view_top:
			toogle();
			break;
		default:
			break;
		}
	}

	private void doExitGroupTask() {
		if (delGroupMemberTask != null
				&& delGroupMemberTask.getStatus() == Status.RUNNING)
			return;
		delGroupMemberTask = new DelGroupMemberTask(this, CurrentAccountManager
				.getCurAccount().getAccessToken(), curGroup.getGroupID(), null);
		delGroupMemberTask.setTaskHost(new TaskHost() {
			@Override
			public void onPostExecute(int requestCode, BaseResult result) {
				super.onPostExecute(requestCode, result);
				if (result.isSuccess()) {
					if (result.isServerDisposeSuccess()) {
						PropertyCenter.getInstance().firePropertyChange(
								PropertyCenter.PROPERTY_REFRESH_GROUPLIST,
								null, null);
						setResult(RESULT_OK);
						finish();
					} else {
						ErrorDialogUtil.showErrorDialog(
								GroupDatumActivity.this,
								ProxyErrorCode.TYPE_GROUP,
								result.getErrorCode(), true);
					}
				}
			}
		});
		delGroupMemberTask.execute();
	}

	private void doDismissGroupTask() {
		if (delGroupTask != null && delGroupTask.getStatus() == Status.RUNNING)
			return;
		delGroupTask = new DelGroupTask(this, CurrentAccountManager
				.getCurAccount().getAccessToken(), curGroup.getGroupID());
		delGroupTask.setTaskHost(new TaskHost() {
			@Override
			public void onPostExecute(int requestCode, BaseResult result) {
				super.onPostExecute(requestCode, result);
				if (result.isSuccess()) {
					if (result.isServerDisposeSuccess()) {
						PropertyCenter.getInstance().firePropertyChange(
								PropertyCenter.PROPERTY_REFRESH_GROUPLIST,
								null, null);
						setResult(RESULT_OK);
						finish();
					} else {
						ErrorDialogUtil.showErrorDialog(
								GroupDatumActivity.this,
								ProxyErrorCode.TYPE_GROUP,
								result.getErrorCode(), true);
					}
				}
			}
		});
		delGroupTask.execute();
	}

	private void doEditGroupSettingTask(final Boolean isupload,
			final Integer alert) {

		if (editGroupSettingTask != null
				&& editGroupSettingTask.getStatus() == Status.RUNNING)
			return;
		editGroupSettingTask = new EditGroupSettingTask(this,
				CurrentAccountManager.getCurAccount().getAccessToken(),
				curGroup.getGroupID(), isupload, alert);
		editGroupSettingTask.setTaskHost(new TaskHost() {
			@Override
			public void onPostExecute(int requestCode, BaseResult result) {
				super.onPostExecute(requestCode, result);
				if (result.isSuccess()) {
					if (result.isServerDisposeSuccess()) {
						if (isupload != null) {
							curGroup.setSettingUpload(isupload);
						}
						if (alert != null) {
							curGroup.setSettingAlert(alert);
						}
						setChanged(true);
						fillView();
					} else {
						ErrorDialogUtil.showErrorDialog(
								GroupDatumActivity.this,
								ProxyErrorCode.TYPE_GROUP,
								result.getErrorCode(), true);
						fillView();
					}
				} else {
					fillView();
				}
			}
		});
		editGroupSettingTask.execute();
	}

	public void showExitGroupDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		// builder.setTitle(R.string.alert_title);
		builder.setMessage(R.string.group_is_quite);
		builder.setCancelable(true);
		builder.setPositiveButton(string.action_confirm,
				new android.content.DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						doExitGroupTask();
					}
				});
		builder.setNegativeButton(string.action_cancel, null);
		builder.show();

	}

	public void showDismissGroupDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		// builder.setTitle(R.string.alert_title);
		builder.setMessage(R.string.group_is_dissolved);
		builder.setCancelable(true);
		builder.setPositiveButton(string.action_confirm,
				new android.content.DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						doDismissGroupTask();
					}
				});
		builder.setNegativeButton(string.action_cancel, null);
		builder.show();

	}

	public void toogle() {
		if (GroupMemberManagerFragment.isLongPressed) {
			GroupMemberManagerFragment.setLongPressedState(false);
		} else {
			GroupMemberManagerFragment.setLongPressedState(true);
		}
	}

	@Override
	public void finish() {
		if (GroupMemberManagerFragment.isLongPressed) {
			GroupMemberManagerFragment.setLongPressedState(false);
		} else {
			if (isChanged()) {
				PropertyCenter.getInstance().firePropertyChange(
						PropertyCenter.PROPERTY_REFRESH_GROUPLIST, null, null);
			}
			super.finish();
		}
	}

	public Group getBondGroup() {
		return curGroup;
	}

	private static final String IMAGE_FILE_NAME = "_group_icon.jpg";

	// modified group avatar
	private void showAvatarDialog() {

		new AlertDialog.Builder(this).setItems(items,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						switch (which) {
						case 0:
							Intent intentFromCapture = new Intent(
									MediaStore.ACTION_IMAGE_CAPTURE);
							// 判断存储卡是否可以用，可用进行存储
							if (Tools.hasSdcard()) {

								intentFromCapture.putExtra(
										MediaStore.EXTRA_OUTPUT,
										Uri.fromFile(new File(Environment
												.getExternalStorageDirectory(),
												curGroup.getGroupID()
														+ IMAGE_FILE_NAME)));
							}

							startActivityForResult(intentFromCapture,
									CAMERA_REQUEST_CODE);
							break;

						case 1:
							Intent intentFromGallery = new Intent();
							intentFromGallery.setType("image/*"); // 设置文件类型
							intentFromGallery
									.setAction(Intent.ACTION_GET_CONTENT);
							startActivityForResult(intentFromGallery,
									IMAGE_REQUEST_CODE);
							break;
						}
					}
				}).show();

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// 结果码不等于取消时候
		if (resultCode != RESULT_CANCELED) {

			switch (requestCode) {
			case IMAGE_REQUEST_CODE:
				startPhotoZoom(data.getData());
				break;
			case CAMERA_REQUEST_CODE:
				if (Tools.hasSdcard()) {
					File tempFile = new File(
							Environment.getExternalStorageDirectory() + "/"
									+ curGroup.getGroupID() + IMAGE_FILE_NAME);
					startPhotoZoom(Uri.fromFile(tempFile));
				} else {
					ErrorDialogUtil.showErrorDialog(GroupDatumActivity.this,
							ProxyErrorCode.TYPE_GROUP,
							ProxyCode.LocalError.CODE_10004, true);
				}

				break;
			case RESULT_REQUEST_CODE:
				if (data != null) {
					getImageToView(data);
				}
				break;
			}
		}
		super.onActivityResult(requestCode, resultCode, data);

	}

	/**
	 * 裁剪图片方法实现
	 * 
	 * @param uri
	 */
	public void startPhotoZoom(Uri uri) {

		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		// 设置裁剪
		intent.putExtra("crop", "true");
		// aspectX aspectY 是宽高的比例
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		// outputX outputY 是裁剪图片宽高
		intent.putExtra("outputX", 320);
		intent.putExtra("outputY", 320);
		intent.putExtra("return-data", true);
		startActivityForResult(intent, 2);
	}

	/**
	 * 保存裁剪之后的图片数据
	 * 
	 * @param picdata
	 */
	private void getImageToView(Intent data) {
		Bundle extras = data.getExtras();
		Bitmap photo = null;
		if (extras != null) {
			photo = extras.getParcelable("data");
			Drawable drawable = new BitmapDrawable(getResources(), photo);
			imageGroupAvator.setImageDrawable(drawable);
		}
		if (photo != null) {
			ByteArrayOutputStream temp = new ByteArrayOutputStream();
			photo.compress(Bitmap.CompressFormat.PNG, 100, temp);
			byte[] byteArr = temp.toByteArray();
			String headPortRait = Base64
					.encodeToString(byteArr, Base64.DEFAULT);

			try {
				Group t = (Group) curGroup.clone();
				t.setHeadPortRait(headPortRait);
				doUpdateGroupInfoTask(t);
			} catch (CloneNotSupportedException e) {
				e.printStackTrace();
			}

		}

	}

	private void doUpdateGroupInfoTask(final Group tmp) {
		if (editGroupTask != null
				&& editGroupTask.getStatus() == Status.RUNNING)
			return;
		editGroupTask = new EditGroupTask(this, CurrentAccountManager
				.getCurAccount().getAccessToken(), tmp.getGroupID(),
				tmp.getName(), tmp.getDescription(), tmp.getHeadPortRait());
		editGroupTask.setTaskHost(new TaskHost() {
			@Override
			public void onPostExecute(int requestCode, BaseResult result) {
				super.onPostExecute(requestCode, result);
				if (result.isSuccess()) {
					NetworkClientResult res = (NetworkClientResult) result;
					if (res.isServerDisposeSuccess()) {
						Group t = Group.createGroup(res.getResponseResult(),
								CurrentAccountManager.getCurAccount()
										.getAccountID());
						curGroup.setHeadPortRait(t.getHeadPortRait());
						setChanged(true);
					} else {
						ErrorDialogUtil.showErrorDialog(
								GroupDatumActivity.this,
								ProxyErrorCode.TYPE_GROUP,
								result.getErrorCode(), true);
					}
				}
			}
		});
		editGroupTask.execute();
	}

	@Override
	public void propertyChange(PropertyChangeEvent event) {
		if (event.getPropertyName().equals(
				PropertyCenter.PROPERTY_REFRESH_GROUP_MEMBER)) {
			fillView();
			Integer position = (Integer) TemporaryData
					.get(GroupMemberManagerFragment.mCurrentItem);
			int currentItem = position == null ? 0 : position;
			doGetAllGroupMemberTask(curGroup.getGroupID(), currentItem);
		} else if (event.getPropertyName().equals(
				PropertyCenter.PROPERTY_REFRESH_AVATAR)) {
			fillAvatarView();
		} else
		/** 处理Push的观察者事件 */
		if (event.getPropertyName().equals(
				PropertyCenter.PROPERTY_NOTIFY_GROUP_QUITED)) {
			CloudPush push = (CloudPush) event.getNewValue();
			final int groupID = push.getExtraGroupID();
			if (groupID == curGroup.getGroupID().intValue()) {
				if (Config.isDeveloperMode) {
					ToastUtils
							.show(this,
									getText(R.string.refresh_page_caused_by_kick_group));
				}
				doGetAllGroupMemberTask(curGroup.getGroupID(), 0);
			}
		} else if (event.getPropertyName().equals(
				PropertyCenter.PROPERTY_NOTIFY_GROUP_KICKED)) {

			CloudPush push = (CloudPush) event.getNewValue();
			final int groupID = push.getExtraGroupID();

			// 服务群只有可能是群成员被踢出
			if (groupID == curGroup.getGroupID().intValue()) {
				if (isActive && TaskUtil.isTopActivity(this)) {
					ErrorDialogUtil.showKickedErrorDialog(
							GroupDatumActivity.this, ProxyErrorCode.TYPE_GROUP,
							LocalError.CODE_12205, curGroup.getName());
				}
			}
		} else if (event.getPropertyName().equals(
				PropertyCenter.PROPERTY_REFRESH_GROUPNOTIFY)) {
			// 接收到群内人员变更等消息，刷新群列表
			CloudPush push = (CloudPush) event.getNewValue();
			if (isActive && TaskUtil.isTopActivity(this)) {
				doGetAllGroupMemberTask(curGroup.getGroupID(), 0);
			} else {
				if (push.getExtraType().equals(Constants.TYPE_ACCEPT_GROUP))
					isRefreshGroupMember = true;
			}
		} else if (event.getPropertyName().equals(
				PropertyCenter.PROPERTY_NOTIFY_GROUP_DISSMISS)) {
			CloudPush push = (CloudPush) event.getNewValue();
			final int groupID = push.getExtraGroupID();
			if (groupID == curGroup.getGroupID().intValue()) {
				if (isActive && TaskUtil.isTopActivity(this))
					ErrorDialogUtil.showKickedErrorDialog(
							GroupDatumActivity.this, ProxyErrorCode.TYPE_GROUP,
							LocalError.CODE_12206, curGroup.getName());
			}
		} else
		/**
		 * 长按删除，阴影部分出现
		 */
		if (TextUtils.equals(event.getPropertyName(),
				PropertyCenter.PROPERTY_REFRESH_DELETE_STAGE)) {
			boolean stage = (Boolean) event.getNewValue();
			if (stage) {
				hideViewCenter.setVisibility(View.VISIBLE);
				hideViewTop.setVisibility(View.VISIBLE);
				hideViewBottom.setVisibility(View.VISIBLE);
			} else {
				hideViewCenter.setVisibility(View.GONE);
				hideViewTop.setVisibility(View.GONE);
				hideViewBottom.setVisibility(View.GONE);
			}
		}
	}

}

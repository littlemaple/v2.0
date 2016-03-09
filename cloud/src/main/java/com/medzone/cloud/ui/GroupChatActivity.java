/**
 * 
 */
package com.medzone.cloud.ui;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import android.content.Intent;
import android.os.Handler;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.LayoutParams;
import com.medzone.cloud.controller.MessagesCacheController;
import com.medzone.cloud.data.CurrentAccountManager;
import com.medzone.cloud.data.PropertyCenter;
import com.medzone.cloud.data.TemporaryData;
import com.medzone.cloud.data.helper.GroupHelper;
import com.medzone.cloud.defender.CloudPush;
import com.medzone.cloud.ui.adapter.MessagesAdapter;
import com.medzone.cloud.ui.adapter.viewholder.ListViewRecyclerListener;
import com.medzone.cloud.ui.dialog.error.ErrorDialogUtil;
import com.medzone.framework.Config;
import com.medzone.framework.Log;
import com.medzone.framework.data.bean.imp.Account;
import com.medzone.framework.data.bean.imp.Group;
import com.medzone.framework.data.bean.imp.GroupMember;
import com.medzone.framework.data.bean.imp.Message;
import com.medzone.framework.data.bean.imp.MessageSession;
import com.medzone.framework.errorcode.ProxyCode.LocalError;
import com.medzone.framework.errorcode.ProxyErrorCode;
import com.medzone.framework.network.NetworkClientResult;
import com.medzone.framework.task.BaseResult;
import com.medzone.framework.task.TaskHost;
import com.medzone.framework.task.progress.RefreshableViewProgress;
import com.medzone.framework.util.NetUtil;
import com.medzone.framework.util.TaskUtil;
import com.medzone.framework.util.ToastUtils;
import com.medzone.framework.view.refresh.RefreshableView;
import com.medzone.framework.view.refresh.RefreshableView.RefreshListener;
import com.medzone.mcloud.R;
import com.medzone.mcloud.R.drawable;
import com.medzone.mcloud.R.id;
import com.medzone.mcloud.R.layout;

/**
 * @author ChenJunQi.
 * 
 * 
 * 
 */
public class GroupChatActivity extends BaseActivity implements OnClickListener,
		PropertyChangeListener {

	// 刷新间隔周期
	private static final int REFRESH_INTERVAL_SECOND = 5000;

	private RefreshableView refreshableView;
	private ListView messageListView;
	private MessageSession messageSession;
	private MessagesAdapter messageAdapter;
	private MessagesCacheController controller;
	private RefreshableViewProgress refreshableViewProgress;

	private static Group curGroup;
	private static List<GroupMember> curMemberList;

	private GroupMember member;
	private TextView tvSend;
	private EditText inputEditText;
	private TextView actionBarTitle;

	private int interlocutorID;

	public static Group getCurGroup() {
		return curGroup;
	}

	public static GroupMember searchGroupMember(int memberID) {
		if (curMemberList != null && curMemberList.size() > 0) {
			for (GroupMember members : curMemberList) {
				if (members.getAccountID().intValue() == memberID) {
					// TODO 临时处理，将自己的信息展现在聊天中
					Account currentAccount = CurrentAccountManager
							.getCurAccount();
					if (currentAccount != null
							&& currentAccount.getAccountID() != null
							&& memberID == currentAccount.getAccountID()
									.intValue()) {
						members.setRemark(currentAccount.getNickname());
						members.setHeadPortRait(currentAccount
								.getHeadPortRait());
					}
					return members;
				}
			}
		}
		return null;
	}

	@Override
	protected void onStart() {
		super.onStart();
		PropertyCenter.getInstance().addPropertyChangeListener(this);
	}

	@Override
	protected void preInitUI() {

		if (TemporaryData.containsKey(Group.class.getName())) {
			curGroup = (Group) TemporaryData.get(Group.class.getName());

			if (curGroup == null) {
				ToastUtils.show(this, "群主已经离开群");
				jumpToMainTab();
			}
			if (curGroup.getCreatorID() == null) {
				ToastUtils.show(this, "群主已经离开群");
				jumpToMainTab();
			}

		}
		if (TemporaryData.containsKey(GroupMember.class.getName())) {
			member = (GroupMember) TemporaryData.get(GroupMember.class
					.getName());
			curGroup = member.getBindGroup();
		}

		createSession(curGroup);

		initActionBar();
	}

	public void jumpToMainTab() {
		startActivity(new Intent(this, MainTabsActivity.class));
		finish();
	}

	private void createSession(Group group) {

		MessageSession ms = new MessageSession();
		ms.setUnRead(false);
		ms.setSessionTitleString(group.getName());
		ms.setInterlocutorName(group.getName());
		ms.setInterlocutorIconUrlString(group.getHeadPortRait());
		ms.setInterlocutorDispalyName(group.getName());

		if (group.getType() == Group.TYPE_NORMAL) {
			interlocutorID = -1;
		} else if (group.getType() == Group.TYPE_SERVICE) {
			if (group.equals(CurrentAccountManager.getCurAccount())) {
				if (member.getAccountID() != null)
					interlocutorID = member.getAccountID();// 群成员id
			} else {
				if (group.getCreatorID() != null)
					interlocutorID = group.getCreatorID();
			}
		}
		ms.setInterlocutorId(interlocutorID);
		GroupHelper
				.setCurrentInterlocutorID(interlocutorID, group.getGroupID());
		messageSession = ms;

	}

	private void initActionBar() {
		ActionBar actionBar = getSupportActionBar();
		ActionBar.LayoutParams params = new ActionBar.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT,
				Gravity.CENTER);
		View view = LayoutInflater.from(this).inflate(
				R.layout.custom_actionbar_with_image, null);
		actionBarTitle = (TextView) view.findViewById(R.id.actionbar_title);
		actionBarTitle.setText(messageSession.getInterlocutorName());
		actionBarTitle.setOnClickListener(this);

		ImageButton leftButton = (ImageButton) view
				.findViewById(id.actionbar_left);
		leftButton.setImageResource(drawable.public_ic_back);
		leftButton.setOnClickListener(this);
		ImageButton rightButton = (ImageButton) view
				.findViewById(id.actionbar_right);
		rightButton.setImageResource(drawable.group_chat_ic_addpeople);
		rightButton.setOnClickListener(this);

		actionBar.setCustomView(view, params);
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		actionBar.setDisplayShowCustomEnabled(true);

	}

	@Override
	protected void initUI() {
		setContentView(layout.activity_group_chat);
		messageListView = (ListView) findViewById(R.id.freelook_listview);
		refreshableView = (RefreshableView) findViewById(R.id.message_refereshableview);
		tvSend = (TextView) findViewById(id.tv_send);
		inputEditText = (EditText) findViewById(id.edit_message);
		inputEditText.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_ENTER
						&& event.getAction() == KeyEvent.ACTION_DOWN) {
					doSendMessage();
					return true;
				}
				return false;
			}
		});
	}

	@Override
	protected void postInitUI() {
		super.postInitUI();
		controller = new MessagesCacheController();
		initListView();
		controller.setInterlocutorID(messageSession.getInterlocutorId(),
				curGroup.getGroupID(), curGroup.getType());
		controller.setNewDataLoadedObserver(new Observer() {

			@Override
			public void update(Observable observable, Object data) {
				messageListView.setSelection(messageAdapter.getCount() - 1);
			}
		});
		tvSend.setOnClickListener(this);
		getDownPage();
		getMemberList();
		autoUpdate();
	}

	private void getMemberList() {
		GroupHelper.doGetMembersList(this, CurrentAccountManager
				.getCurAccount().getAccessToken(), curGroup.getGroupID(),
				new TaskHost() {
					@Override
					public void onPostExecute(int requestCode, BaseResult result) {
						super.onPostExecute(requestCode, result);
						if (result.isSuccess()) {
							NetworkClientResult res = (NetworkClientResult) result;
							if (res.isServerDisposeSuccess()) {
								curMemberList = GroupMember
										.createGroupMemberList(res,
												CurrentAccountManager
														.getCurAccount()
														.getAccountID());
							}
						}
					}
				});
	}

	private void initListView() {
		refreshableView.setRefreshListener(new RefreshListener() {

			@Override
			public void onRefresh(RefreshableView view) {
				getDownPage();
			}
		});
		refreshableViewProgress = new RefreshableViewProgress(refreshableView);
		messageAdapter = new MessagesAdapter(this);
		messageAdapter.setCache(controller.getCache());
		controller.addObserver(messageAdapter);
		messageListView.setAdapter(messageAdapter);
		messageListView.setRecyclerListener(new ListViewRecyclerListener());
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case id.actionbar_left:
			finish();
			break;
		case id.actionbar_right:

			if (curGroup != null) {
				TemporaryData.save(Group.class.getName(), curGroup);
				Intent intent = new Intent();
				if (curGroup.getType().equals(Group.TYPE_NORMAL)) {
					intent.setClass(this, GroupDatumActivity.class);
				} else if (curGroup.getType().equals(Group.TYPE_SERVICE)) {
					if (curGroup.equals(CurrentAccountManager.getCurAccount())) {
						intent.setClass(this,
								GroupServiceForOwnerDetailActivity.class);
					} else {
						intent.setClass(this,
								GroupServiceForMemberDetailActivity.class);
					}
				}
				startActivityForResult(intent, 0);
			} else {
				// TODO
			}

			break;
		case R.id.actionbar_title:
			getUpPage();
			break;
		case id.tv_send:
			doSendMessage();
			break;
		default:
			break;
		}
	}

	private void doSendMessage() {
		final String content = inputEditText.getText().toString();
		final int errorCode = GroupHelper.checkChatContent(content);

		if (errorCode == LocalError.CODE_SUCCESS) {

			if (curGroup.getType() == Group.TYPE_NORMAL) {
				sendMessageTask(content);
			} else if (curGroup.getType() == Group.TYPE_SERVICE) {
				sendP2PMessageTask((int) messageSession.getInterlocutorId(),
						content);
			}
		} else {
			if (errorCode == LocalError.CODE_12302) {
				return;
			} else if (errorCode == LocalError.CODE_12303) {
				ErrorDialogUtil.showErrorDialog(GroupChatActivity.this,
						ProxyErrorCode.TYPE_GROUP, errorCode, true);
			} else if (errorCode == LocalError.CODE_12304) {
				ToastUtils.show(this, getText(R.string.send_too_frequently));
			}
		}
	}

	// TODO 移动到GroupHelper类中
	private void sendMessageTask(String content) {

		int groupid = (int) curGroup.getGroupID();
		int type = Message.TYPE_NORMAL;

		GroupHelper.sendMessageTask(this,
				CurrentAccountManager.getCurAccount(), groupid, content, type,
				new TaskHost() {
					@Override
					public void onPostExecute(int requestCode, BaseResult result) {
						super.onPostExecute(requestCode, result);
						if (result.isSuccess()) {
							NetworkClientResult res = (NetworkClientResult) result;
							if (res.isServerDisposeSuccess()) {
								inputEditText.setText(null);
								getUpPage();
							} else {
								ErrorDialogUtil.showErrorDialog(
										GroupChatActivity.this,
										ProxyErrorCode.TYPE_GROUP,
										result.getErrorCode(), true);
							}
						}
					}
				});

	}

	/**
	 * 
	 * 单人对话id
	 * 
	 * @param syncid
	 */
	// TODO 移动到GroupHelper类中
	@Deprecated
	private void sendP2PMessageTask(Integer syncid, String content) {
		int groupid = (int) curGroup.getGroupID();
		int type = Message.TYPE_NORMAL;

		GroupHelper.sendP2PMessageTask(this,
				CurrentAccountManager.getCurAccount(), groupid, syncid,
				content, type, new TaskHost() {
					@Override
					public void onPostExecute(int requestCode, BaseResult result) {
						super.onPostExecute(requestCode, result);
						if (result.isSuccess()) {
							NetworkClientResult res = (NetworkClientResult) result;
							if (res.isServerDisposeSuccess()) {
								inputEditText.setText(null);
								getUpPage();
							} else {
								ErrorDialogUtil.showErrorDialog(
										GroupChatActivity.this,
										ProxyErrorCode.TYPE_GROUP,
										result.getErrorCode(), true);
							}
						}
					}
				});

	}

	private void getUpPage() {
		controller.getNewItemsFromServer(null, null);
	}

	private void getDownPage() {
		controller.getDownPageItemsFromServer(refreshableViewProgress, null);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == 0) {
			if (resultCode == RESULT_OK) {
				finish();
			}
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		PropertyCenter.getInstance().removePropertyChangeListener(this);
		GroupHelper.setCurrentInterlocutorID(Account.INVALID_ID,
				Group.INVALID_ID);
	}

	private boolean isInCurrentChat(int memberId) {
		if (member.getAccountID().intValue() == memberId)
			return true;
		return false;
	}

	@Override
	public void propertyChange(PropertyChangeEvent event) {
		if (event.getPropertyName().equals(
				PropertyCenter.PROPERTY_REFRESH_CHATROOM)) {
			if (TaskUtil.isTopActivity(this)) {
				getUpPage();
			} else {
				Log.v("propertyChange#聊天室刷新请求");
			}
		} else if (event.getPropertyName().equals(
				PropertyCenter.PROPERTY_REFRESH_AVATAR)) {
			messageAdapter.notifyDataSetChanged();
		} else if (event.getPropertyName().equals(
				PropertyCenter.PROPERTY_REFRESH_CHAT_MEMBER)) {
			getMemberList();
		} else if (event.getPropertyName().equals(
				PropertyCenter.PROPERTY_REFRESH_GROUP_MEMBER)) {
			if (event.getNewValue() instanceof Group) {
				Group group = (Group) event.getNewValue();
				// TODO 刷新群聊天的标题
				if (group != null) {
					actionBarTitle.setText(group.getName());
				}
			}
			messageAdapter.invilidate();
			messageAdapter.notifyDataSetChanged();
		} else if (event.getPropertyName().equals(
				PropertyCenter.PROPERTY_REFRESH_CHAT_AVATAR)) {
			messageAdapter.invilidate();
			messageAdapter.notifyDataSetChanged();
		} else
		/** 处理Push的观察者事件 */
		if (event.getPropertyName().equals(
				PropertyCenter.PROPERTY_NOTIFY_GROUP_QUITED)) {
			if (event.getNewValue() instanceof CloudPush) {
				// 普通群群主收到退群信息时，不会退出此次对话
				if (curGroup.getType() != Group.TYPE_NORMAL) {
					CloudPush push = (CloudPush) event.getNewValue();
					final int groupID = push.getExtraGroupID();
					final int syncID = push.getExtraMessageSendID();
					// 退群的人不在聊天页面的不会对群主提示
					if (groupID == curGroup.getGroupID().intValue()
							&& syncID == push.getExtraMessageSendID()) {
						if (Config.isDeveloperMode) {
							ToastUtils
									.show(this,
											getText(R.string.close_dialog_caused_by_quit_group));

							ToastUtils.show(
									this,
									"退群用户是否在群聊天内"
											+ isInCurrentChat(push
													.getExtraNotifyMemberID()));
						}
						if (isInCurrentChat(push.getExtraNotifyMemberID())) {
							PropertyCenter
									.getInstance()
									.firePropertyChange(
											PropertyCenter.PROPERTY_REFRESH_GROUP_MEMBER,
											null, null);
							finish();
						}
					}
				}
			}
		} else if (event.getPropertyName().equals(
				PropertyCenter.PROPERTY_NOTIFY_GROUP_KICKED)) {
			if (event.getNewValue() instanceof CloudPush) {
				CloudPush push = (CloudPush) event.getNewValue();
				final int groupID = push.getExtraGroupID();
				// 服务群只有可能是群成员被踢出
				if (groupID == curGroup.getGroupID().intValue()) {
					if (isActive && TaskUtil.isTopActivity(this)) {
						ErrorDialogUtil.showKickedErrorDialog(this,
								ProxyErrorCode.TYPE_GROUP,
								LocalError.CODE_12205, curGroup.getName());
					}
				}
			}
		} else if (event.getPropertyName().equals(
				PropertyCenter.PROPERTY_NOTIFY_GROUP_DISSMISS)) {
			if (event.getNewValue() instanceof CloudPush) {
				CloudPush push = (CloudPush) event.getNewValue();
				final int groupID = push.getExtraGroupID();
				if (groupID == curGroup.getGroupID().intValue()) {
					if (isActive && TaskUtil.isTopActivity(this))
						ErrorDialogUtil.showKickedErrorDialog(this,
								ProxyErrorCode.TYPE_GROUP,
								LocalError.CODE_12206, curGroup.getName());
				}
			}
		}
	}

	private void autoUpdate() {
		final Handler handler = new Handler();
		Runnable r = new Runnable() {

			@Override
			public void run() {
				if (!isFinishing()) {
					Log.i("Auto update command is started.");
					handler.postDelayed(this, REFRESH_INTERVAL_SECOND);
					if (NetUtil.isConnect(GroupChatActivity.this)) {
						controller.autoUpdates(null);
						Log.i("Auto update operation is running.");
					}
				} else {
					Log.i("Auto update command is stoped.");
				}
			}
		};
		handler.postDelayed(r, REFRESH_INTERVAL_SECOND);
	}
}

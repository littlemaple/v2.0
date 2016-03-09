/**
 * 
 */
package com.medzone.cloud.ui.fragment.group;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collections;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar.LayoutParams;
import com.medzone.cloud.CloudApplication;
import com.medzone.cloud.GlobalVars;
import com.medzone.cloud.data.CurrentAccountManager;
import com.medzone.cloud.data.PropertyCenter;
import com.medzone.cloud.data.UnreadMessageCenter;
import com.medzone.cloud.data.helper.CustomComparator;
import com.medzone.cloud.data.helper.GroupHelper;
import com.medzone.cloud.ui.GroupCreateActivity;
import com.medzone.cloud.ui.GroupNotifyActivity;
import com.medzone.cloud.ui.adapter.GroupExpandableAdapter;
import com.medzone.cloud.ui.dialog.error.ErrorDialogUtil;
import com.medzone.framework.Log;
import com.medzone.framework.data.bean.imp.Group;
import com.medzone.framework.data.bean.imp.GroupMember;
import com.medzone.framework.errorcode.ProxyErrorCode;
import com.medzone.framework.fragment.BaseFragment;
import com.medzone.framework.network.NetworkClientResult;
import com.medzone.framework.task.BaseResult;
import com.medzone.framework.task.TaskHost;
import com.medzone.mcloud.R;
import com.medzone.mcloud.R.drawable;
import com.medzone.mcloud.R.id;
import com.medzone.mcloud.R.layout;
import com.medzone.mcloud.R.string;

public class GroupFragment extends BaseFragment implements OnClickListener,
		PropertyChangeListener {

	private View containerView;
	private GroupExpandableAdapter groupExpandAdapter;
	private ExpandableListView groupExpandListView;
	private TextView tvErrotNetReachable;
	private View vErrotNetReachableLine;
	private ImageView ivNewNotification;
	private CustomComparator<GroupMember> cmp = new CustomComparator<GroupMember>();

	private View actionBarView;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		PropertyCenter.addPropertyChangeListener(this);
		// 预初始化消息通知
		UnreadMessageCenter.requestUnReadNotification();
	}

	@Override
	protected void initActionBar() {
		int heightPx = CloudApplication.actionBarHeight;
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, heightPx, Gravity.CENTER);
		actionBarView = LayoutInflater.from(getActivity()).inflate(
				R.layout.custom_actionbar_with_image, null);
		actionBarView.setLayoutParams(params);
		ivNewNotification = (ImageView) actionBarView
				.findViewById(id.iv_new_notification);

		TextView title = (TextView) actionBarView
				.findViewById(R.id.actionbar_title);
		title.setText(string.indicator_group);
		title.setOnClickListener(this);
		ImageButton leftButton = (ImageButton) actionBarView
				.findViewById(id.actionbar_left);
		leftButton.setBackgroundResource(drawable.group_ic_inform);
		leftButton.setOnClickListener(this);
		ImageButton rightButton = (ImageButton) actionBarView
				.findViewById(id.actionbar_right);
		rightButton.setBackgroundResource(drawable.serviceview_ic_add);
		rightButton.setOnClickListener(this);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		initActionBar();
		containerView = inflater.inflate(layout.fragment_group, container,
				false);
		LinearLayout llActionBar = (LinearLayout) containerView
				.findViewById(R.id.actionbar);
		llActionBar.addView(actionBarView);
		tvErrotNetReachable = (TextView) containerView
				.findViewById(R.id.tv_error_net_reachable);
		vErrotNetReachableLine = (View) containerView
				.findViewById(R.id.tv_error_net_reachable_line);
		groupExpandListView = (ExpandableListView) containerView
				.findViewById(R.id.expandableListView_list);
		groupExpandAdapter = new GroupExpandableAdapter(getActivity());
		groupExpandListView.setAdapter(groupExpandAdapter);
		groupExpandListView.setGroupIndicator(null);
		groupExpandListView.setOnGroupClickListener(new OnGroupClickListener() {

			@Override
			public boolean onGroupClick(ExpandableListView parent, View v,
					int groupPosition, long id) {
				if (groupPosition == GroupExpandableAdapter.ITEM_TYPE_EXTRA_CAREFUL)
					return true;
				return false;
			}
		});
		boolean isNetConnected = !GlobalVars.isOffLineLogined();
		fillNetConnectStateView(isNetConnected);
		return containerView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		if (GroupHelper.getList() != null) {
			groupExpandAdapter.setGroupContent(GroupHelper.getList());
		}
		doGetGroupListTask();
		// 预初始化授权列表，以便后部调用
		CurrentAccountManager.getAuthorizedList(true, false);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		PropertyCenter.removePropertyChangeListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case id.actionbar_left:
			startActivity(new Intent(getActivity(), GroupNotifyActivity.class));
			break;
		case id.actionbar_right:
			startActivity(new Intent(getActivity(), GroupCreateActivity.class));
			break;
		case R.id.actionbar_title:
			UnreadMessageCenter.print("点击Actionbar");
			break;
		default:
			break;
		}
	}

	/**
	 * TODO 一些时候仅仅需要去刷新群列表，特别关心的刷新可以被独立开
	 */
	private void doGetGroupListTask() {

		// TODO 出现过控制针异常，需要查看
		GroupHelper.doGetAllGroupTask(getActivity(), CurrentAccountManager
				.getCurAccount().getAccessToken(), new TaskHost() {
			@Override
			public void onPostExecute(int requestCode, BaseResult result) {
				super.onPostExecute(requestCode, result);
				if (result.isSuccess()) {

					NetworkClientResult res = (NetworkClientResult) result;

					if (res.isServerDisposeSuccess()) {
						List<Group> list = Group.createGroupList(res,
								CurrentAccountManager.getCurAccount()
										.getAccountID());
						groupExpandAdapter.setGroupContent(list);
						GroupHelper.setList(list);
						UnreadMessageCenter
								.requestUnReadChatMessageByCollect(list);
						doGetGroupCareListTask();
					} else {
						ErrorDialogUtil.showErrorDialog(getActivity(),
								ProxyErrorCode.TYPE_GROUP, res.getErrorCode(),
								true);
					}
				}
			}
		});

	}

	private void doGetGroupCareListTask() {

		GroupHelper.doGetGroupCareListTask(getActivity(), CurrentAccountManager
				.getCurAccount().getAccessToken(), new TaskHost() {
			@Override
			public void onPostExecute(int requestCode, BaseResult result) {
				super.onPostExecute(requestCode, result);
				if (result.isSuccess()) {
					NetworkClientResult res = (NetworkClientResult) result;
					if (res.isServerDisposeSuccess()) {

						List<GroupMember> extraCarefulList = GroupMember
								.createGroupMemberList(res,
										CurrentAccountManager.getCurAccount()
												.getAccountID());
						Collections.sort(extraCarefulList, cmp);
						groupExpandAdapter
								.setExtraCareFulContent(extraCarefulList);
						if (extraCarefulList != null
								&& !extraCarefulList.isEmpty()) {
							groupExpandListView
									.expandGroup(GroupExpandableAdapter.ITEM_TYPE_EXTRA_CAREFUL);
						}
					} else {
						ErrorDialogUtil.showErrorDialog(getActivity(),
								ProxyErrorCode.TYPE_GROUP, res.getErrorCode(),
								true);
					}
				}
			}
		});
	}

	private void fillNetConnectStateView(boolean isConnected) {
		if (!isConnected) {
			tvErrotNetReachable.setVisibility(View.VISIBLE);
			vErrotNetReachableLine.setVisibility(View.VISIBLE);
		} else {
			tvErrotNetReachable.setVisibility(View.GONE);
			vErrotNetReachableLine.setVisibility(View.GONE);
		}
	}

	@Override
	public void propertyChange(PropertyChangeEvent event) {

		if (event.getPropertyName().equals(
				PropertyCenter.PROPERTY_REFRESH_GROUPLIST)) {
			doGetGroupListTask();
		} else if (event.getPropertyName().equals(
				PropertyCenter.PROPERTY_CONNECT_STATE)) {
			boolean connectState = (Boolean) event.getNewValue();
			fillNetConnectStateView(connectState);
		} else if (event.getPropertyName().equals(
				PropertyCenter.PROPERTY_REFRESH_GROUP_MEMBER)) {
			doGetGroupListTask();
		} else if (event.getPropertyName().equals(
				PropertyCenter.PROPERTY_REFRESH_NOTIFICATIONMESSAGE)) {
			refreshNewNotification();
		} else if (event.getPropertyName().equals(
				PropertyCenter.PROPERTY_NOTIFY_GROUP_CHATMESSAGE)) {
			groupExpandAdapter.notifyDataSetChanged();
		} else if (event.getPropertyName().equals(
				PropertyCenter.PROPERTY_REFRESH_AVATAR)) {
			groupExpandAdapter.notifyDataSetChanged();
		} else if (event.getPropertyName().equals(
				PropertyCenter.PROPERTY_REFRESH_GROUPLIST_EXPAND)) {
			groupExpandListView
					.expandGroup(GroupExpandableAdapter.ITEM_TYPE_NORMAL);
		} else if (event.getPropertyName().equals(
				PropertyCenter.PROPERTY_NOTIFY_GROUP_KICKED)) {
			doGetGroupListTask();
		} else if (event.getPropertyName().equals(
				PropertyCenter.PROPERTY_NOTIFY_GROUP_DISSMISS)) {
			doGetGroupListTask();
		}
	}

	private void refreshNewNotification() {
		int size = UnreadMessageCenter.getNewNotiicationCollectLength();
		if (ivNewNotification == null) {
			Log.d("获取到刷新通知列表，但是页面还未被初始化好，该条刷新请求作废。");
			return;
		}
		if (size == 0) {
			ivNewNotification.setVisibility(View.GONE);
		} else {
			ivNewNotification.setVisibility(View.VISIBLE);
		}
	}

}

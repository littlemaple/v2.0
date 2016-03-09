/**
 * 
 */
package com.medzone.cloud.ui;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.LayoutParams;
import com.medzone.cloud.Constants;
import com.medzone.cloud.data.CurrentAccountManager;
import com.medzone.cloud.data.PropertyCenter;
import com.medzone.cloud.data.TemporaryData;
import com.medzone.cloud.data.UnreadMessageCenter;
import com.medzone.cloud.data.helper.CustomComparator;
import com.medzone.cloud.data.helper.GroupHelper;
import com.medzone.cloud.defender.CloudPush;
import com.medzone.cloud.ui.adapter.GroupMemberAdapter;
import com.medzone.cloud.ui.dialog.error.ErrorDialogUtil;
import com.medzone.cloud.ui.widget.CleanableEditText;
import com.medzone.framework.Log;
import com.medzone.framework.data.bean.imp.Group;
import com.medzone.framework.data.bean.imp.GroupMember;
import com.medzone.framework.errorcode.ProxyCode.LocalError;
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
public class GroupMemberActivity extends BaseActivity implements
		OnClickListener, PropertyChangeListener {

	private ListView groupMemberListView;
	private GroupMemberAdapter groupMemberAdapter;
	private Group group;

	private CleanableEditText etSearch;
	private TextView tvSearchHint;

	private List<GroupMember> allMemberList, normalMemberList,
			extraCarefulMemberList, filterMemberList;

	@Override
	protected void preLoadData(Bundle savedInstanceState) {
		super.preLoadData(savedInstanceState);
		group = (Group) TemporaryData.get(Group.class.getName());

		if (group == null) {
			ToastUtils.show(this, "群主已经离开群");
			jumpToMainTab();
		}
		if (group.getCreatorID() == null) {
			ToastUtils.show(this, "群主已经离开群");
			jumpToMainTab();
		}

		UnreadMessageCenter.requestUnReadChatMessage(group.getGroupID()
				.intValue());
	}

	public void jumpToMainTab() {
		startActivity(new Intent(this, MainTabsActivity.class));
		finish();
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
		ImageButton rightButton = (ImageButton) view
				.findViewById(id.actionbar_right);
		rightButton.setImageResource(drawable.serviceview_ic_add);
		rightButton.setOnClickListener(this);
		title.setText(string.group_member);

		actionBar.setCustomView(view, params);
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		actionBar.setDisplayShowCustomEnabled(true);
	}

	@Override
	protected void initUI() {
		setContentView(R.layout.activity_group_member);
		PropertyCenter.addPropertyChangeListener(this);
		// tvTag = (TextView) this.findViewById(R.id.tv_letter_tag);
		// letterSeekBar = (LetterSeekBar) this
		// .findViewById(R.id.letter_sort_right);
		etSearch = (CleanableEditText) this.findViewById(R.id.ce_search_member);
		tvSearchHint = (TextView) this.findViewById(R.id.tv_search_hint);
		groupMemberListView = (ListView) this.findViewById(R.id.lv_groupmember);
		groupMemberAdapter = new GroupMemberAdapter(this);
		groupMemberListView.setAdapter(groupMemberAdapter);
		groupMemberListView.setDivider(new ColorDrawable(Color.TRANSPARENT));
		groupMemberListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
			}
		});
	}

	@Override
	protected void postInitUI() {

		// letterSeekBar.setOnclikViewRight(new CustomOnClickRightListener());
		dogetAllGroupMemberTask(group.getGroupID());
		etSearch.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				String filter = s.toString().trim().toLowerCase();
				if (TextUtils.isEmpty(filter)) {
					tvSearchHint.setVisibility(View.VISIBLE);
				} else {
					tvSearchHint.setVisibility(View.GONE);
				}
				filterSource(filter);
			}
		});
	}

	public void initGroupMemberData() {
		normalMemberList = new ArrayList<GroupMember>();
		extraCarefulMemberList = new ArrayList<GroupMember>();
		for (GroupMember member : allMemberList) {
			if (member.isCare()) {
				extraCarefulMemberList.add(member);
			} else {
				normalMemberList.add(member);
			}
		}
		sortContentList(extraCarefulMemberList);
		sortContentList(normalMemberList);
		allMemberList.clear();
		allMemberList.addAll(extraCarefulMemberList);
		allMemberList.addAll(normalMemberList);
	}

	private void filterSource(String str) {
		filterMemberList = new ArrayList<GroupMember>();
		if (allMemberList == null)
			return;
		if (TextUtils.isEmpty(str)) {
			filterMemberList.addAll(allMemberList);
		} else {
			for (GroupMember member : allMemberList) {

				String displayName = CurrentAccountManager.getCurAccount()
						.getFriendsDisplay(member).toLowerCase();

				if (isContain(displayName, str)) {
					filterMemberList.add(member);
				}
			}
		}
		groupMemberAdapter.setContent(filterMemberList);
		groupMemberAdapter.notifyDataSetChanged();

	}

	/**
	 * content中是否包含str中的字符
	 * 
	 * @param content
	 * @param str
	 * @return
	 */
	private boolean isContain(String content, String str) {
		// 对content处理，避免 名称中可能存在脏数据"-"等,影响正则判断，一般情况下不会有这种情况
		try {
			content.replaceAll(
					"[-!\\*\\.\\?\\+\\$\\^\\[\\]\\(\\)\\{\\}\\|\\/\\/]", "");
			if (str.matches("^[" + content + "]+")) {
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return false;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.actionbar_left:
			this.finish();
			break;
		case id.actionbar_right:
			Intent intent = new Intent(this, GroupInviteActivity.class);
			intent.putExtra(Group.NAME_FIELD_GROUP_ID, group.getGroupID());
			startActivity(intent);
			break;
		default:
			break;
		}
	}

	private void sortContentList(List<GroupMember> allMemberList) {

		CustomComparator<GroupMember> cmp = new CustomComparator<GroupMember>();
		Collections.sort(allMemberList, cmp);
	}

	private void dogetAllGroupMemberTask(long groupID) {

		GroupHelper.doGetMembersList(this, CurrentAccountManager
				.getCurAccount().getAccessToken(), (int) groupID,
				new TaskHost() {
					@Override
					public void onPostExecute(int requestCode, BaseResult result) {
						super.onPostExecute(requestCode, result);
						if (result.isSuccess()) {
							NetworkClientResult res = (NetworkClientResult) result;
							if (res.isServerDisposeSuccess()) {

								allMemberList = GroupMember
										.createGroupMemberListExceptOwner(res,
												group.getGroupID(),
												CurrentAccountManager
														.getCurAccount()
														.getAccountID());
								initGroupMemberData();
								filterSource(null);
								// initCharTitle();
							} else {
								ErrorDialogUtil.showErrorDialog(
										GroupMemberActivity.this,
										ProxyErrorCode.TYPE_GROUP,
										res.getErrorCode(), true);
							}
						} else {
							ErrorDialogUtil.showErrorDialog(
									GroupMemberActivity.this,
									ProxyErrorCode.TYPE_GROUP,
									LocalError.CODE_10002, true);
						}
					}
				});

	}

	public Group getBindGroup() {
		return group;
	}

	@Override
	public void propertyChange(PropertyChangeEvent event) {
		if (event.getPropertyName().equals(
				PropertyCenter.PROPERTY_REFRESH_GROUP_MEMBER)) {
			dogetAllGroupMemberTask(group.getGroupID());
			groupMemberAdapter.notifyDataSetChanged();
		} else if (event.getPropertyName().equals(
				PropertyCenter.PROPERTY_NOTIFY_GROUP_CHATMESSAGE)) {
			groupMemberAdapter.notifyDataSetChanged();
		} else if (event.getPropertyName().equals(
				PropertyCenter.PROPERTY_REFRESH_AVATAR)) {
			groupMemberAdapter.notifyDataSetChanged();
		} else if (event.getPropertyName().equals(
				PropertyCenter.PROPERTY_NOTIFY_GROUP_KICKED)) {
			dogetAllGroupMemberTask(group.getGroupID());
		} else if (event.getPropertyName().equals(
				PropertyCenter.PROPERTY_REFRESH_GROUPNOTIFY)) {
			CloudPush push = (CloudPush) event.getNewValue();
			if (push.getExtraType().equals(Constants.TYPE_ACCEPT_GROUP))
				Log.e("服务群成员发生变更，刷新列表");
			dogetAllGroupMemberTask(group.getGroupID());
		} else if (event.getPropertyName().equals(
				PropertyCenter.PROPERTY_NOTIFY_GROUP_QUITED)) {
			dogetAllGroupMemberTask(group.getGroupID());
		}

	}
}

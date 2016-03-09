/**
 * 
 */
package com.medzone.cloud.ui;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.LayoutParams;
import com.medzone.cloud.data.CurrentAccountManager;
import com.medzone.cloud.data.PropertyCenter;
import com.medzone.cloud.data.UnreadMessageCenter;
import com.medzone.cloud.data.helper.AccountHelper;
import com.medzone.cloud.task.GetMessageListTask;
import com.medzone.cloud.ui.adapter.GroupMessageAdapter;
import com.medzone.cloud.ui.dialog.error.ErrorDialogUtil;
import com.medzone.framework.Log;
import com.medzone.framework.data.bean.imp.Account;
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
public class GroupNotifyActivity extends BaseActivity implements
		OnClickListener, PropertyChangeListener {

	private ListView notifyListView;
	private Account account;
	private GroupMessageAdapter gmAdapter;
	private List<ServiceMessage> messageList = new ArrayList<ServiceMessage>();

	private void initActionBar() {
		ActionBar actionBar = getSupportActionBar();
		ActionBar.LayoutParams params = new ActionBar.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT,
				Gravity.CENTER);
		View view = LayoutInflater.from(this).inflate(
				R.layout.custom_actionbar_with_image, null);
		TextView title = (TextView) view.findViewById(R.id.actionbar_title);
		title.setOnClickListener(this);
		ImageButton leftButton = (ImageButton) view
				.findViewById(id.actionbar_left);
		leftButton.setImageResource(drawable.public_ic_back);
		leftButton.setOnClickListener(this);
		title.setText(string.actionbar_title_group_notify);
		actionBar.setCustomView(view, params);
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		actionBar.setDisplayShowCustomEnabled(true);
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
	}

	@Override
	protected void preInitUI() {
		initActionBar();
	}

	@Override
	protected void initUI() {
		setContentView(layout.activity_group_notify);
		notifyListView = (ListView) findViewById(id.list_group_notify);
	}

	@Override
	protected void postInitUI() {

		notifyListView
				.setOnItemLongClickListener(new OnItemLongClickListener() {

					@Override
					public boolean onItemLongClick(AdapterView<?> parent,
							View view, int position, long id) {
						ServiceMessage msg = messageList.get(position);
						// final int msgID = msg.getMessageId();
						// final String accessToken = CurrentAccountManager
						// .getCurAccount().getAccessToken();
						// AccountHelper.doMarkNotificationProcessTask(
						// accessToken, msgID, true, null);
						delMessageTip(msg);
						return false;
					}
				});
	}

	public void delMessageTip(final ServiceMessage msg) {
		final int msgID = msg.getMessageId();
		AlertDialog.Builder builder = new AlertDialog.Builder(
				GroupNotifyActivity.this);
		builder.setMessage(R.string.history_list_delete_content);
		builder.setCancelable(true);
		builder.setPositiveButton(string.action_confirm,
				new android.content.DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {

						AccountHelper.doMarkNotificationProcessTask(
								GroupNotifyActivity.this,
								account.getAccessToken(), msgID, "D",
								new TaskHost() {
									@Override
									public void onPostExecute(int requestCode,
											BaseResult result) {
										super.onPostExecute(requestCode, result);
										if (result.isSuccess()) {
											if (result.isServerDisposeSuccess()) {
												messageList.remove(msg);
												gmAdapter
														.notifyDataSetChanged();
											} else {
												ErrorDialogUtil
														.showErrorDialog(
																GroupNotifyActivity.this,
																ProxyErrorCode.TYPE_GROUP,
																result.getErrorCode(),
																true);
											}
										}
									}
								});
					}

				});
		builder.setNegativeButton(string.action_cancel, null);
		builder.show();

	}

	@Override
	protected void onResume() {
		super.onResume();
		getMessageTask();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		PropertyCenter.getInstance().removePropertyChangeListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case id.actionbar_left:
			finish();
			break;
		case R.id.actionbar_title:
			// getMessageTask();
			// UnreadMessageCenter.print();
			break;
		default:
			break;
		}
	}

	public void getMessageTask() {

		if (isActive) {
			GetMessageListTask task = new GetMessageListTask(this,
					account.getAccessToken());
			task.setTaskHost(new TaskHost() {
				@Override
				public void onPostExecute(int requestCode, BaseResult result) {
					super.onPostExecute(requestCode, result);
					if (result.isSuccess()) {
						if (result.isServerDisposeSuccess()) {
							NetworkClientResult res = (NetworkClientResult) result;
							messageList.clear();
							messageList = ServiceMessage
									.getMessageListByResult(res);
							loadData(messageList);
							markAllReaded();
						} else {
							ErrorDialogUtil.showErrorDialog(
									GroupNotifyActivity.this,
									ProxyErrorCode.TYPE_GROUP,
									result.getErrorCode(), true);
						}
					}
				}
			});
			task.execute();
		} else {
			Log.e("key#isActive:false");
		}
	}

	private void markAllReaded() {

		Log.e("key#isActive:true#removeAllNotification");
		AccountHelper.doMarkNotificationProcessTask(this, CurrentAccountManager
				.getCurAccount().getAccessToken(), null, true, new TaskHost() {
			public void onPostExecute(int requestCode, BaseResult result) {
				if (result.isSuccess() && result.isServerDisposeSuccess()) {
					UnreadMessageCenter.removeAllNotification();
				}
			};
		});
	}

	public void loadData(List<ServiceMessage> mList) {

		if (gmAdapter == null) {
			gmAdapter = new GroupMessageAdapter(this);
			notifyListView.setAdapter(gmAdapter);
		}
		gmAdapter.setContent(mList);
	}

	@Override
	public void propertyChange(PropertyChangeEvent event) {

		if (event.getPropertyName().equals(
				PropertyCenter.PROPERTY_REFRESH_GROUPNOTIFY)) {
			getMessageTask();
		}
	}

}

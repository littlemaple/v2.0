package com.medzone.cloud.ui;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.ImageButton;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.LayoutParams;
import com.medzone.cloud.data.CurrentAccountManager;
import com.medzone.cloud.ui.widget.CleanableEditText;
import com.medzone.framework.Log;
import com.medzone.framework.data.bean.imp.Account;
import com.medzone.mcloud.R;
import com.medzone.mcloud.R.drawable;
import com.medzone.mcloud.R.id;
import com.umeng.fb.FeedbackAgent;
import com.umeng.fb.model.Conversation;
import com.umeng.fb.model.DevReply;
import com.umeng.fb.model.Reply;
import com.umeng.fb.model.UserInfo;

public class SettingFeedBackActivity extends BaseActivity implements
		com.umeng.fb.model.Conversation.SyncListener, OnClickListener {

	private CleanableEditText etContent;
	private Account currentAccount;

	private FeedbackAgent agent;
	private Conversation defaultConversation;

	private final int TYPE_FAIL_FEED_BACK = 0;
	private final int TYPE_SUCCESS_FEED_BACK = 1;

	@Override
	protected void preLoadData(Bundle savedInstanceState) {
		super.preLoadData(savedInstanceState);
		currentAccount = CurrentAccountManager.getCurAccount();
		agent = new FeedbackAgent(this);
	}

	@Override
	protected void preInitUI() {
		super.preInitUI();
		initActionBar();
	}

	@Override
	protected void initUI() {
		super.initUI();
		setContentView(R.layout.activity_setting_feed_back);
		etContent = (CleanableEditText) this.findViewById(R.id.ce_feed_back);
	}

	@Override
	protected void postInitUI() {
		super.postInitUI();
		etContent.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_ENTER
						&& event.getAction() == KeyEvent.ACTION_DOWN) {
					uploadFeedBackInfo();
					return true;
				}
				return false;
			}
		});
	}

	private void initActionBar() {
		ActionBar actionBar = getSupportActionBar();
		ActionBar.LayoutParams params = new ActionBar.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT,
				Gravity.CENTER);
		View view = LayoutInflater.from(this).inflate(
				R.layout.custom_actionbar_with_image, null);
		TextView title = (TextView) view.findViewById(R.id.actionbar_title);
		title.setText(R.string.actionbar_title_setting_suggestion_feedback);
		ImageButton leftButton = (ImageButton) view
				.findViewById(id.actionbar_left);
		leftButton.setImageResource(drawable.personalinformationview_ic_cancel);
		leftButton.setOnClickListener(this);

		ImageButton rightButton = (ImageButton) view
				.findViewById(id.actionbar_right);
		rightButton.setImageResource(drawable.personalinformationview_ic_ok);
		rightButton.setOnClickListener(this);
		actionBar.setCustomView(view, params);
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		actionBar.setDisplayShowCustomEnabled(true);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.actionbar_left:
			this.finish();
			break;
		case R.id.actionbar_right:
			uploadFeedBackInfo();
			break;
		}

	}

	private void uploadFeedBackInfo() {
		String content = etContent.getText().toString();
		if (TextUtils.isEmpty(content)) {
			showDialog(content,
					getResources().getString(R.string.please_input_suggestion),
					TYPE_FAIL_FEED_BACK);
		} else {
			showDialog(content,
					getResources().getString(R.string.feed_back_hint),
					TYPE_SUCCESS_FEED_BACK);
		}

	}

	@Override
	public void onReceiveDevReply(List<DevReply> list) {
		Log.e("feed back --- dev reply");
	}

	@Override
	public void onSendUserReply(List<Reply> list) {
		Log.e("feed back ---send reply ");
	}

	public void sendInfoToUmeng(String content) {
		String contact = currentAccount.getPhone() == null ? currentAccount
				.getEmail() : currentAccount.getPhone();
		UserInfo info = new UserInfo();
		Map<String, String> map = info.getContact();
		if (map == null)
			map = new HashMap<String, String>();
		map.put("plain", contact);
		info.setContact(map);
		agent.setUserInfo(info);
		defaultConversation = agent.getDefaultConversation();
		defaultConversation.addUserReply(content);
		defaultConversation.sync(this);
	}

	public void showDialog(final String content, String title, final int type) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this)
				.setMessage(title);
		builder.setPositiveButton(R.string.public_submit,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						if (type == TYPE_SUCCESS_FEED_BACK) {
							sendInfoToUmeng(content);
							SettingFeedBackActivity.this.finish();
							Log.e("feed back --- ");
						}
					}

				});
		AlertDialog dialog = builder.create();
		dialog.setCancelable(false);
		dialog.setCanceledOnTouchOutside(false);
		dialog.show();
	}
}

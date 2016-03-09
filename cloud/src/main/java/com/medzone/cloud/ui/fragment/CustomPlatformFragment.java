package com.medzone.cloud.ui.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.medzone.cloud.ui.ShareActivity;
import com.medzone.mcloud.R;
import com.medzone.mcloud.R.id;
import com.medzone.mcloud.R.string;

/**
 * 
 * @author junqi
 * 
 *         用于显示分享各个平台的入口，可以在这里添加不同的平台，并进行呈现
 */
public class CustomPlatformFragment extends DialogFragment implements
		View.OnClickListener {

	private TextView tvCloud;
	private TextView tvWechatFriends;
	private TextView tvWechat;
	private TextView tvSMS;
	private TextView tvEmail;

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {

		return initDialog();
	}

	private Dialog initDialog() {
		View contentView = LayoutInflater.from(getActivity()).inflate(
				R.layout.dialog_share, null);
		initView(contentView);
		// TODO findViews and register event.
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setCancelable(true);
		builder.setNegativeButton(string.action_cancel, null);
		builder.setView(contentView);
		AlertDialog dialog = builder.create();
		return dialog;
	}

	private void initView(View contentView) {
		tvCloud = (TextView) contentView.findViewById(id.tv_cloud);
		tvWechatFriends = (TextView) contentView
				.findViewById(id.tv_wechat_friends);
		tvWechat = (TextView) contentView.findViewById(id.tv_wechat);
		tvSMS = (TextView) contentView.findViewById(id.tv_sms);
		tvEmail = (TextView) contentView.findViewById(id.tv_email);
		tvCloud.setOnClickListener(this);
		tvWechatFriends.setOnClickListener(this);
		tvWechat.setOnClickListener(this);
		tvSMS.setOnClickListener(this);
		tvEmail.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case id.tv_cloud:
			Intent intent = new Intent(getActivity(), ShareActivity.class);
			startActivityForResult(intent, 0);
			dismiss();
			break;
		case id.tv_wechat_friends:

			break;
		case id.tv_wechat:

			break;
		case id.tv_sms:

			break;
		case id.tv_email:

			break;

		default:
			break;
		}
	}

}

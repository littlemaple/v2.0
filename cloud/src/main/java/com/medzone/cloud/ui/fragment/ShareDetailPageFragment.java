package com.medzone.cloud.ui.fragment;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnDismissListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.medzone.cloud.Constants;
import com.medzone.cloud.data.CurrentAccountManager;
import com.medzone.cloud.data.TemporaryData;
import com.medzone.cloud.data.helper.GroupHelper;
import com.medzone.cloud.ui.ShareActivity;
import com.medzone.cloud.ui.dialog.ProxyFactory;
import com.medzone.cloud.ui.dialog.share.CloudShareDialogPage;
import com.medzone.framework.Log;
import com.medzone.framework.data.bean.imp.Group;
import com.medzone.framework.data.bean.imp.Message;
import com.medzone.framework.network.NetworkClientResult;
import com.medzone.framework.task.BaseResult;
import com.medzone.framework.task.TaskHost;
import com.medzone.mcloud.R;
import com.medzone.mcloud.R.id;
import com.medzone.mcloud.R.string;

/**
 * 
 * @author junqi
 * 
 *         预览分享详情
 */
public class ShareDetailPageFragment extends DialogFragment {

	// 分享载体
	private Group carriedGroup;
	// 分享类别 单条/月报/近期
	private int shareType;
	// 测量类别 血压/血氧/耳温
	private String measureType;

	private EditText etLeaveMessage;
	private RelativeLayout rlLayout;

	private CloudShareDialogPage page;
	private TextView dialog_cancle, dialog_send;

	private Dialog loadingDialog;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if (TemporaryData
				.containsKey(Constants.TEMPORARYDATA_KEY_SHARE_CARRIED)) {
			carriedGroup = (Group) TemporaryData
					.get(Constants.TEMPORARYDATA_KEY_SHARE_CARRIED);
		}
		if (TemporaryData.containsKey(Constants.TEMPORARYDATA_KEY_SHARE_TYPE)) {
			shareType = (Integer) TemporaryData
					.get(Constants.TEMPORARYDATA_KEY_SHARE_TYPE);
		}
		if (TemporaryData.containsKey(Constants.TEMPORARYDATA_KEY_MEASURE_TYPE)) {
			measureType = (String) TemporaryData
					.get(Constants.TEMPORARYDATA_KEY_MEASURE_TYPE);
		}

	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {

		return initDialog();
	}

	private Dialog initDialog() {
		View contentView = LayoutInflater.from(getActivity()).inflate(
				R.layout.dialog_share_detail, null);
		initView(contentView);

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setCancelable(false);

		builder.setPositiveButton(string.action_send, new OnClickListener() {

			@Override
			public void onClick(DialogInterface arg0, int arg1) {

				doShareTask();
			}
		});
		builder.setNegativeButton(string.action_cancel, new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				finish();
			}
		}).setInverseBackgroundForced(getUserVisibleHint());
		builder.setView(contentView);
		AlertDialog dialog = builder.create();
		return dialog;
	}

	@Override
	public void onCancel(DialogInterface dialog) {
		super.onCancel(dialog);
		finish();
	}

	private void initView(View contentView) {
		etLeaveMessage = (EditText) contentView
				.findViewById(id.et_leave_message);
		rlLayout = (RelativeLayout) contentView.findViewById(id.rl_detail);

		page = (CloudShareDialogPage) ProxyFactory.getFactory(
				ProxyFactory.TYPE_CLOUD_SHARE).createDetailPage(getActivity(),
				shareType, measureType);
		if (page != null) {
			View child = page.getView();
			rlLayout.addView(child);
		} else {
			// TextView tvError = new TextView(getActivity());
			// tvError.setText("未能找到指定的DetailPage");
			// rlLayout.addView(tvError);
			finish();
		}

	}

	// TODO 分享 获取URL地址，并且调用发送群消息接口
	private void doShareTask() {

		if (page != null) {
			displayLoadingDialog();
			page.doGetShareURL(new TaskHost() {
				@Override
				public void onPostExecute(int requestCode, BaseResult result) {
					super.onPostExecute(requestCode, result);
					if (result.isSuccess()) {
						NetworkClientResult res = (NetworkClientResult) result;
						if (res.isServerDisposeSuccess()) {

							String shareUrl = null;
							JSONObject jo = res.getResponseResult();
							if (jo.has("url") && !jo.isNull("url")) {
								try {
									shareUrl = jo.getString("url");
									Log.i("share webview url:" + shareUrl);
								} catch (JSONException e) {
									e.printStackTrace();
								}
							}
							doSendMessageTask(shareUrl);
						} else {
							// Toast.makeText(GlobalVars.applicationContext,
							// res.getResponseResult().toString(),
							// Toast.LENGTH_SHORT).show();
							hideLoadingDialog();
						}
					} else {
						hideLoadingDialog();
						// Toast.makeText(GlobalVars.applicationContext,
						// string.error_net_connect, Toast.LENGTH_SHORT)
						// .show();
					}
				}
			});
		}

	}

	private void doSendMessageTask(String shareUrl) {

		final int groupid = carriedGroup.getGroupID().intValue();

		if (page != null) {
			TaskHost taskHost = new TaskHost() {
				@Override
				public void onPostExecute(int requestCode, BaseResult result) {
					super.onPostExecute(requestCode, result);
					if (result.isSuccess()) {
						NetworkClientResult res = (NetworkClientResult) result;
						if (res.isServerDisposeSuccess()) {
							String leaveMessage = etLeaveMessage.getText()
									.toString();
							if (!TextUtils.isEmpty(leaveMessage)) {
								doSendNormalMessage(groupid, leaveMessage);
							} else {
								displayConfirmDialog();
							}
						} else {
							hideLoadingDialog();
							// Toast.makeText(GlobalVars.applicationContext,
							// res.getResponseResult().toString(),
							// Toast.LENGTH_SHORT).show();
							finish();
						}
					} else {
						hideLoadingDialog();
						// Toast.makeText(GlobalVars.applicationContext,
						// string.error_net_connect, Toast.LENGTH_SHORT)
						// .show();
						finish();
					}
				}
			};
			page.doSendShareMessage(taskHost, groupid, shareUrl);
		}

	}

	private void doSendNormalMessage(int groupid, String leaveMessage) {

		GroupHelper.sendMessageTask(getActivity(),
				CurrentAccountManager.getCurAccount(), groupid, leaveMessage,
				Message.TYPE_NORMAL, new TaskHost() {
					@Override
					public void onPostExecute(int requestCode, BaseResult result) {
						super.onPostExecute(requestCode, result);
						if (result.isSuccess()) {
							NetworkClientResult res = (NetworkClientResult) result;
							if (res.isServerDisposeSuccess()) {
								displayConfirmDialog();
							} else {
								hideLoadingDialog();
								// Toast.makeText(GlobalVars.applicationContext,
								// res.getResponseResult().toString(),
								// Toast.LENGTH_SHORT).show();
								finish();
							}
						} else {
							hideLoadingDialog();
							// Toast.makeText(GlobalVars.applicationContext,
							// string.error_net_connect,
							// Toast.LENGTH_SHORT).show();
							finish();
						}
					}
				});
	}

	private void displayConfirmDialog() {

		hideLoadingDialog();
		ShareActivity activity = ShareActivity.instance;

		if (activity == null || !activity.isActive) {
			return;
		}

		View contentView = LayoutInflater.from(activity).inflate(
				R.layout.dialog_share_success, null);
		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		builder.setCancelable(true);
		builder.setPositiveButton(string.action_confirm, null);
		builder.setView(contentView);

		final AlertDialog alert = builder.create();
		alert.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss(DialogInterface arg0) {
				finish();
			}
		});
		alert.setOnCancelListener(new OnCancelListener() {

			@Override
			public void onCancel(DialogInterface arg0) {
				finish();
			}
		});
		alert.show();
	}

	private void displayLoadingDialog() {

		ShareActivity activity = ShareActivity.instance;
		if (activity == null || !activity.isActive) {
			return;
		}
		LayoutInflater inflater = LayoutInflater.from(activity);
		View v = inflater.inflate(R.layout.layout_dialog_progress_image, null);
		ImageView spaceshipImage = (ImageView) v.findViewById(R.id.img);
		Animation hyperspaceJumpAnimation = AnimationUtils.loadAnimation(
				activity, R.anim.rotate_loading);
		spaceshipImage.startAnimation(hyperspaceJumpAnimation);

		loadingDialog = new Dialog(activity, R.style.loading_dialog);

		loadingDialog.setCancelable(false);
		loadingDialog.setContentView(v, new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT,
				LinearLayout.LayoutParams.WRAP_CONTENT));
		loadingDialog.show();
	}

	private void hideLoadingDialog() {
		if (loadingDialog != null && loadingDialog.isShowing())
			loadingDialog.cancel();
	}

	private void finish() {
		if (ShareActivity.instance != null) {
			hideLoadingDialog();
			ShareActivity.instance.finish();
			ShareActivity.instance = null;
		}
	}

	@Override
	public void dismiss() {
		super.dismiss();
		finish();
	}
}

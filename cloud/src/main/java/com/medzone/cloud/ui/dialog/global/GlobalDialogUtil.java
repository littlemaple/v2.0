package com.medzone.cloud.ui.dialog.global;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;

import com.medzone.cloud.data.TemporaryData;
import com.medzone.cloud.ui.GlobalDialogActivity;
import com.medzone.cloud.ui.dialog.CloudGlobalDialogFactory;
import com.medzone.cloud.ui.dialog.ProxyFactory;
import com.medzone.cloud.ui.dialog.global.CloudGlobalDialogPage.Builder;
import com.medzone.mcloud.R;

public final class GlobalDialogUtil {

//	private static Dialog dialog;
	public static final String DIALOG_TITLE = "dialog_title";
	public static final String DIALOG_CONTENT = "dialog_content";
	public static final String DIALOG_POSITIVE_CLICK = "dialog_positive_click";

	/**
	 * 对话框展示在当前app的最顶层
	 * 
	 * @param context
	 * @param title
	 * @param content
	 * @param mOnGlobalClickListener
	 */
	public static void showGlobalAppDialog(Context context, String title,
			String content, final onGlobalClickListener mOnGlobalClickListener) {

		CloudGlobalDialogPage page = (CloudGlobalDialogPage) ProxyFactory
				.getFactory(ProxyFactory.TYPE_CLOUD_GLOBAL).createDetailPage(
						context, CloudGlobalDialogFactory.NORMAL_GLOBAL_TYPE);

		CloudGlobalDialogPage.Builder builder = new Builder(page);
		builder.setContent(content);
		builder.setTitle(title);

		Intent intent = new Intent(context, GlobalDialogActivity.class);
		TemporaryData.save(DIALOG_CONTENT, content);
		TemporaryData.save(DIALOG_TITLE, title);
		TemporaryData.save(DIALOG_POSITIVE_CLICK, mOnGlobalClickListener);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent);
		// overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
	}

	/**
	 * @param context
	 * @param title
	 * @param content
	 * @param mOnGlobalClickListener
	 */
	public static void showGlobalDialog(Context context, String title,
			String content, final onGlobalClickListener mOnGlobalClickListener) {
		CloudGlobalDialogPage page = (CloudGlobalDialogPage) ProxyFactory
				.getFactory(ProxyFactory.TYPE_CLOUD_GLOBAL).createDetailPage(
						context, CloudGlobalDialogFactory.NORMAL_GLOBAL_TYPE);

		CloudGlobalDialogPage.Builder builder = new Builder(page);
		builder.setContent(content);
		builder.setTitle(title);

		final Dialog dialog = new Dialog(context, R.style.DialogStyle);
		dialog.setCancelable(true);
		dialog.setCanceledOnTouchOutside(false);
		dialog.setContentView(page.getView());
		dialog.getWindow()
				.setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);

		builder.setOnPositiveButton(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (dialog != null) {
					dialog.dismiss();
				}
			}
		});
		dialog.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss(DialogInterface dialog) {
				if (mOnGlobalClickListener != null)
					mOnGlobalClickListener.onClick();
			}
		});
		dialog.show();
	}

	public interface onGlobalClickListener {
		public void onClick();
	}
}

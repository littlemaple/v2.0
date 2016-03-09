package com.medzone.cloud.ui.dialog.error;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.Toast;

import com.medzone.cloud.CloudApplication;
import com.medzone.cloud.Constants;
import com.medzone.cloud.data.PropertyCenter;
import com.medzone.cloud.defender.BroadCastUtil;
import com.medzone.cloud.ui.MainTabsActivity;
import com.medzone.cloud.ui.dialog.CloudErrorDialogFactory;
import com.medzone.cloud.ui.dialog.IDialogFactory;
import com.medzone.cloud.ui.dialog.ProxyFactory;
import com.medzone.framework.errorcode.ProxyCode.NetError;
import com.medzone.framework.errorcode.ProxyErrorCode;
import com.medzone.mcloud.R;
import com.medzone.mcloud.R.string;

/**
 * 
 * 作用类似于组装者，将获取的错误信息，错误View等组件进行组装
 * 
 * @author junqi
 * 
 */
@SuppressLint("NewApi")
public final class ErrorDialogUtil {

	private static final int TITLE_SUFFIX = 0;
	private static final int CONTENT_SUFFIX = 1;
	private static final int POSITIVE_SUFFIX = 2;
	private static final int NEGATIVE_SUFFIX = 3;
	private static final boolean FLAG_DIALOG_PART = false;

	private static WindowManager windowManager;
	private static View lastContentView;

	private static Dialog dialog;

	public static View getContentView() {
		return lastContentView;
	}

	private static void removeLastViewIfExist() {
		if (lastContentView != null) {
			try {
				getWindowManager().removeViewImmediate(lastContentView);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private static WindowManager getWindowManager() {
		if (windowManager == null) {
			windowManager = (WindowManager) CloudApplication.getInstance()
					.getApplicationContext()
					.getSystemService(Context.WINDOW_SERVICE);
		}
		return windowManager;
	}

	private static OnTouchListener onTouchListener = new OnTouchListener() {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			switch (event.getAction()) {
			case MotionEvent.ACTION_UP:
				removeView();
				break;

			default:
				break;
			}
			return false;
		}
	};

	private static String formatContent(String message, String target) {
		// if (AccountHelper.isEmailCorrect(target)) {
		// message = String.format(message, GlobalVars.applicationContext
		// .getResources().getString(string.email), target);
		// } else {
		// message = String.format(message,
		// GlobalVars.applicationContext.getString(string.phone),
		// target);
		// }
		message = String.format(message, target);
		return message;
	}

	private static boolean isForceKickedOff(Context context, int code) {
		if (code == NetError.CODE_40002) {
			Intent intent = new Intent(BroadCastUtil.ACTION_PUSH_LOGIN_INVALID);
			context.sendBroadcast(intent);
			return true;
		}
		return false;
	}

	public static void showErrorDialog(Context context, int type, int code,
			boolean isAutoCancel) throws NullPointerException {

		removeLastViewIfExist();

		if (isForceKickedOff(context, code)) {
			return;
		}

		// 获取指定错误类别的信息
		String message = ProxyErrorCode.getInstance().getErrorMessage(type,
				code);
		// 经理类根据工厂类别，获取指定的工厂对象
		IDialogFactory<?> factory = ProxyFactory
				.getFactory(ProxyFactory.TYPE_CLOUD_ERROR);
		CloudErrorDialogPage page = (CloudErrorDialogPage) factory
				.createDetailPage(context,
						CloudErrorDialogFactory.ERROR_TYPE_NORMAL);

		CloudErrorDialogPage.Builder builder = new CloudErrorDialogPage.Builder(
				page);
		page = builder.addContent(message).addLoadingIcon(null).build();
		lastContentView = page.getView();
		lastContentView.setOnTouchListener(onTouchListener);

		if (FLAG_DIALOG_PART) {
			useDialog(context, lastContentView);
			handler.removeMessages(1);
			handler.sendEmptyMessageDelayed(1, Constants.DIALOG_DISMISS_TIME);
		} else {

			getWindowManager().addView(lastContentView, getLayoutParams());
			handler.removeMessages(1);
			if (isAutoCancel) {
				handler.sendEmptyMessageDelayed(1,
						Constants.DIALOG_DISMISS_TIME);
			}
		}
	}

	/**
	 * 
	 * @param context
	 * @param type
	 * @param code
	 * @param target
	 *            将具体的target(邮箱/手机)替换到错误码
	 * @throws NullPointerException
	 */
	public static void showErrorDialog(Context context, int type, int code,
			String target) throws NullPointerException {

		removeLastViewIfExist();

		if (isForceKickedOff(context, code)) {
			return;
		}

		// 获取指定错误类别的信息
		String message = ProxyErrorCode.getInstance().getErrorMessage(type,
				code);
		message = formatContent(message, target);

		// 经理类根据工厂类别，获取指定的工厂对象
		IDialogFactory<?> factory = ProxyFactory
				.getFactory(ProxyFactory.TYPE_CLOUD_ERROR);
		CloudErrorDialogPage page = (CloudErrorDialogPage) factory
				.createDetailPage(context,
						CloudErrorDialogFactory.ERROR_TYPE_NORMAL);

		CloudErrorDialogPage.Builder builder = new CloudErrorDialogPage.Builder(
				page);
		page = builder.addContent(message).addLoadingIcon(null).build();
		lastContentView = page.getView();
		lastContentView.setOnTouchListener(onTouchListener);

		if (FLAG_DIALOG_PART) {
			useDialog(context, lastContentView);
			handler.removeMessages(1);
			handler.sendEmptyMessageDelayed(1, Constants.DIALOG_DISMISS_TIME);
		} else {

			getWindowManager().addView(lastContentView, getLayoutParams());
			handler.removeMessages(1);
			handler.sendEmptyMessageDelayed(1, Constants.DIALOG_DISMISS_TIME);
		}
	}

	public static void showErrorDialog(Context context, int type, int code,
			int leftDrawable) throws NullPointerException {

		removeLastViewIfExist();

		if (isForceKickedOff(context, code)) {
			return;
		}

		// 获取指定错误类别的信息
		String message = ProxyErrorCode.getInstance().getErrorMessage(type,
				code);

		// 经理类根据工厂类别，获取指定的工厂对象
		IDialogFactory<?> factory = ProxyFactory
				.getFactory(ProxyFactory.TYPE_CLOUD_ERROR);
		CloudErrorDialogPage page = (CloudErrorDialogPage) factory
				.createDetailPage(context,
						CloudErrorDialogFactory.ERROR_TYPE_NORMAL);

		CloudErrorDialogPage.Builder builder = new CloudErrorDialogPage.Builder(
				page);
		Drawable drawable = context.getResources().getDrawable(leftDrawable);
		page = builder.addContent(message).addLoadingIcon(drawable).build();
		lastContentView = page.getView();
		lastContentView.setOnTouchListener(onTouchListener);

		if (FLAG_DIALOG_PART) {
			useDialog(context, lastContentView);
			handler.removeMessages(1);
			handler.sendEmptyMessageDelayed(1, Constants.DIALOG_DISMISS_TIME);
		} else {

			getWindowManager().addView(lastContentView, getLayoutParams());
			handler.removeMessages(1);
			handler.sendEmptyMessageDelayed(1, Constants.DIALOG_DISMISS_TIME);
		}
	}

	/**
	 * 
	 * @param context
	 * @param type
	 * @param code
	 * @param leftDrawable
	 *            左侧
	 * @param isAnim
	 *            左侧图标是否旋转
	 * 
	 * @param isTouchCancel
	 *            目前场景中 只要设置成 false
	 * @throws NullPointerException
	 */
	public static void showErrorDialog(Context context, int type, int code,
			int leftDrawable, boolean isAnim, boolean isAutoCancel,
			boolean isTouchCancel) throws NullPointerException {

		removeLastViewIfExist();

		if (isForceKickedOff(context, code)) {
			return;
		}

		// 获取指定错误类别的信息
		String message = ProxyErrorCode.getInstance().getErrorMessage(type,
				code);

		// 经理类根据工厂类别，获取指定的工厂对象
		IDialogFactory<?> factory = ProxyFactory
				.getFactory(ProxyFactory.TYPE_CLOUD_ERROR);
		CloudErrorDialogPage page = (CloudErrorDialogPage) factory
				.createDetailPage(context,
						CloudErrorDialogFactory.ERROR_TYPE_NORMAL);

		CloudErrorDialogPage.Builder builder = new CloudErrorDialogPage.Builder(
				page);
		Drawable drawable = context.getResources().getDrawable(leftDrawable);
		page = builder.addContent(message).addLoadingIcon(drawable)
				.isDrawableAnim(isAnim).build();
		lastContentView = page.getView();
		if (isTouchCancel) {
			lastContentView.setOnTouchListener(onTouchListener);
		}

		getWindowManager().addView(lastContentView, getLayoutParams());
		handler.removeMessages(1);
		if (isAutoCancel)
			handler.sendEmptyMessageDelayed(1, Constants.DIALOG_DISMISS_TIME);
	}

	/**
	 * 极少数需要客户自己去定义如何显示错误，如何呈现错误，以及事件响应
	 * 
	 * @param context
	 * @param type
	 *            错误模块类别
	 * @param code
	 *            错误码
	 * @param loadingDrawable
	 *            加载时的drawable
	 * @param negativeListener
	 *            取消事件Listener
	 * @param positiveListener
	 *            确定事件Listener
	 * @param strings
	 *            文案集合，参照：标题{@link#TITLE_SUFFIX},内容{@link#CONTENT_SUFFIX},
	 *            确认按钮名称{@link#POSITIVE_SUFFIX},取消按钮名称{@link#NEGATIVE_SUFFIX}
	 */
	public static void showErrorDialog(Context context, int type, int code,
			Drawable loadingDrawable, OnClickListener negativeListener,
			OnClickListener positiveListener, String... strings)
			throws NullPointerException {

		removeLastViewIfExist();

		if (isForceKickedOff(context, code)) {
			return;
		}

		IDialogFactory<?> factory = ProxyFactory
				.getFactory(ProxyFactory.TYPE_CLOUD_ERROR);
		CloudErrorDialogPage page = (CloudErrorDialogPage) factory
				.createDetailPage(context,
						CloudErrorDialogFactory.ERROR_TYPE_NORMAL);

		CloudErrorDialogPage.Builder builder = new CloudErrorDialogPage.Builder(
				page);
		page = builder.addTitle(strings[TITLE_SUFFIX])
				.addContent(strings[CONTENT_SUFFIX])
				.addLoadingIcon(loadingDrawable).build();
		View detailView = page.getView();

		AlertDialog.Builder aBuilder = new AlertDialog.Builder(context);
		aBuilder.setView(detailView);
		aBuilder.setPositiveButton(strings[POSITIVE_SUFFIX], positiveListener);
		aBuilder.setNegativeButton(strings[NEGATIVE_SUFFIX], negativeListener);
		aBuilder.show();
	}

	/**
	 * 
	 * 呈现部分需要替换文字内容的错误提示类型
	 * 
	 * @author ChenJunQi. 2014年9月12日
	 * 
	 * @param context
	 * @param type
	 * @param code
	 * @param loadingDrawable
	 * @param positiveListener
	 * @throws NullPointerException
	 */
	public static void showKickedErrorDialog(final Context context, int type,
			int code, String value) throws NullPointerException {

		if (isForceKickedOff(context, code)) {
			return;
		}

		String format = ProxyErrorCode.getInstance()
				.getErrorMessage(type, code);
		String errorMessage = String.format(format, value);
		AlertDialog.Builder aBuilder = new AlertDialog.Builder(context);
		aBuilder.setMessage(errorMessage);
		aBuilder.setCancelable(false);
		// aBuilder.setView(detailView);
		aBuilder.setPositiveButton(string.action_confirm,
				new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						Intent intent = new Intent(context,
								MainTabsActivity.class);
						context.startActivity(intent);
						PropertyCenter.getInstance().firePropertyChange(
								PropertyCenter.PROPERTY_REFRESH_GROUPLIST,
								null, null);
					}
				});
		Dialog dialog = aBuilder.create();
		// dialog.getWindow()
		// .setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
		dialog.show();
	}

	public static void showErrorToast(Context context, int type, int code) {

		if (isForceKickedOff(context, code)) {
			return;
		}
		// 获取指定错误类别的信息
		String message = ProxyErrorCode.getInstance().getErrorMessage(type,
				code);
		Toast.makeText(context, message, Toast.LENGTH_SHORT).show();

	}

	public static void removeView() {

		if (FLAG_DIALOG_PART) {
			if (dialog != null && dialog.isShowing()) {
				dialog.dismiss();
			}
		} else {
			if (lastContentView != null && lastContentView.getParent() != null) {
				getWindowManager().removeViewImmediate(lastContentView);
				handler.removeMessages(1);
			}
		}
	}

	public static void useDialog(Context context, View view) {
		dialog = new Dialog(context, R.style.DialogStyle);
		dialog.setCancelable(true);
		dialog.setCanceledOnTouchOutside(true);
		LayoutParams params = dialog.getWindow().getAttributes();
		params.width = LayoutParams.WRAP_CONTENT;
		params.height = LayoutParams.WRAP_CONTENT;
		dialog.getWindow().setAttributes(params);
		// FIXME 在布局中改了颜色，使用全局需要改回来
		// LayoutParams params = dialog.getWindow().getAttributes();
		// params.gravity = Gravity.CENTER;
		// params.height = LayoutParams.WRAP_CONTENT;
		// params.width = LayoutParams.WRAP_CONTENT;
		// params.format = PixelFormat.RGBX_8888;
		// params.alpha = 80;
		// dialog.getWindow().setAttributes(params);
		dialog.setContentView(view);
		dialog.show();
	}

	private static LayoutParams getLayoutParams() {
		LayoutParams params = new LayoutParams();
		params.gravity = Gravity.CENTER;
		params.height = LayoutParams.WRAP_CONTENT;
		params.width = LayoutParams.WRAP_CONTENT;
		params.type = LayoutParams.TYPE_PHONE;
		params.format = PixelFormat.TRANSLUCENT;
		params.alpha = 0.8f;
		return params;
	}

	/**
	 * 定时关闭dialog
	 */
	private static Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1:
				removeView();
				break;
			}
		};
	};
}

package com.medzone.cloud.ui.widget;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;

public class ErrorDialog {

	private Context context;
	private ErrorDialogListener errorDialogListener;
	private String title;
	private String content;
	private String leftText;
	private String rightText;
	private int type;
	public static final int TYPE = 1;
	public static final int OTHER_TYPE = 0;

	public ErrorDialog(Context context, int type,
			ErrorDialogListener errorDialogListener, String title,
			String content, String leftText, String rightText) {
		this.context = context;
		this.type = type;
		this.errorDialogListener = errorDialogListener;
		this.title = title;
		this.content = content;
		this.leftText = leftText;
		this.rightText = rightText;
	}

	public Dialog dialogFactory() {
		Dialog dialog = null;
		switch (type) {
		case TYPE:
			dialog = getErrorDialog();
			break;
		case OTHER_TYPE:
			dialog = getErrorOtherDialog();
			break;
		}
		return dialog;
	}

	private Dialog getErrorDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setMessage(content);
		builder.setTitle(title);
		builder.setCancelable(true);
		builder.setPositiveButton(leftText,
				new android.content.DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						errorDialogListener.restart();
					}
				});
		builder.setNegativeButton(rightText,
				new android.content.DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						errorDialogListener.exit();
					}

				});
		return builder.create();
	}

	private Dialog getErrorOtherDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setMessage(content);
		builder.setTitle(title);
		builder.setCancelable(true);
		builder.setPositiveButton(leftText,
				new android.content.DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						errorDialogListener.restart();
					}
				});
		return builder.create();
	}

	public interface ErrorDialogListener {
		void restart();

		void exit();
	}

}
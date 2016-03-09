package com.medzone.cloud.ui.dialog;

import android.content.Context;

public abstract class DialogPage implements IDialogView {

	public static final int TYPE_RECENT = 0;
	public static final int TYPE_MONTH = 1;
	public static final int TYPE_RECORD = 2;

	protected Context mContext;

	public DialogPage(Context context) {
		this.mContext = context;
		prepareData();
	}

	public abstract void prepareData();
}

package com.medzone.cloud.ui.dialog;

import android.content.Context;

public interface IDialogFactory<T> {

	public T createDetailPage(Context context, Object... objects);
}

package com.medzone.cloud.ui.dialog.global;

import android.view.View.OnClickListener;

public interface ICloudGlobal {

	public void setContent(String content);

	public void setTitle(String title);

	public void setOnPositiveButton(OnClickListener listener);

}

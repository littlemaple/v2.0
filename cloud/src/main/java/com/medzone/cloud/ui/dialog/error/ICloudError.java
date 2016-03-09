package com.medzone.cloud.ui.dialog.error;

import android.graphics.drawable.Drawable;

public interface ICloudError {

	public void setTitle(String title);

	public void setContent(String content);

	public void setLoadingIcon(Drawable icon);
	
	public void isDrawableAnim(boolean isAnim);
}

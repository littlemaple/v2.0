package com.medzone.cloud.ui.dialog.error;

import android.content.Context;
import android.graphics.drawable.Drawable;

import com.medzone.cloud.ui.dialog.DialogPage;

/**
 * 
 * TODO 错误对话框，需要思考，错误对话框需要满足的通用型的规范，并给他制定接口？？
 * 
 * @author junqi
 * 
 */
public abstract class CloudErrorDialogPage extends DialogPage implements
		ICloudError {

	public static Builder builder;

	public CloudErrorDialogPage(Context context) {
		super(context);

	}

	public static class Builder {

		private CloudErrorDialogPage page;

		public Builder(CloudErrorDialogPage page) {
			this.page = page;
		}

		public Builder addTitle(String title) {
			page.setTitle(title);
			return this;
		}

		public Builder addContent(String content) {
			page.setContent(content);
			return this;
		}

		public Builder addLoadingIcon(Drawable icon) {
			page.setLoadingIcon(icon);
			return this;
		}

		public CloudErrorDialogPage build() {
			return page;
		}

		public Builder isDrawableAnim(boolean isAnim) {
			page.isDrawableAnim(isAnim);
			return this;
		}

	}

}

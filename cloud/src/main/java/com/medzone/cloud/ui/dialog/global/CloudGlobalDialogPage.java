package com.medzone.cloud.ui.dialog.global;

import android.content.Context;
import android.view.View.OnClickListener;

import com.medzone.cloud.ui.dialog.DialogPage;

public abstract class CloudGlobalDialogPage extends DialogPage implements
		ICloudGlobal {

	public CloudGlobalDialogPage(Context context) {
		super(context);
	}

	public static class Builder {

		private CloudGlobalDialogPage page;

		public Builder(CloudGlobalDialogPage page) {
			this.page = page;
		}

		public Builder setContent(String content) {
			page.setContent(content);
			return this;
		}

		public Builder setTitle(String title) {
			page.setTitle(title);
			return this;
		}

		public Builder setOnPositiveButton(OnClickListener listener) {
			page.setOnPositiveButton(listener);
			return this;
		}
	}

}

package com.medzone.cloud.ui;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;

import com.medzone.cloud.data.TemporaryData;
import com.medzone.cloud.data.helper.AccountHelper;
import com.medzone.cloud.ui.dialog.CloudGlobalDialogFactory;
import com.medzone.cloud.ui.dialog.ProxyFactory;
import com.medzone.cloud.ui.dialog.global.CloudGlobalDialogPage;
import com.medzone.cloud.ui.dialog.global.CloudGlobalDialogPage.Builder;
import com.medzone.cloud.ui.dialog.global.GlobalDialogUtil;
import com.medzone.cloud.ui.dialog.global.GlobalDialogUtil.onGlobalClickListener;
import com.medzone.framework.Log;

public class GlobalDialogActivity extends BaseActivity {

	private String title;
	private String content;
	private onGlobalClickListener mOnGlobalClickListener;

	@Override
	protected void preLoadData(Bundle savedInstanceState) {
		super.preLoadData(savedInstanceState);

		title = (String) TemporaryData.get(GlobalDialogUtil.DIALOG_TITLE);
		content = (String) TemporaryData.get(GlobalDialogUtil.DIALOG_CONTENT);
		mOnGlobalClickListener = (onGlobalClickListener) TemporaryData
				.get(GlobalDialogUtil.DIALOG_POSITIVE_CLICK);
	}

	@Override
	protected void initUI() {
		super.initUI();
		Log.e("global dialog>>>>>show");
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		LayoutParams params = getWindow().getAttributes();
		params.width = LayoutParams.WRAP_CONTENT;
		params.height = LayoutParams.WRAP_CONTENT;
		params.dimAmount = 0.5f;

		CloudGlobalDialogPage page = (CloudGlobalDialogPage) ProxyFactory
				.getFactory(ProxyFactory.TYPE_CLOUD_GLOBAL).createDetailPage(
						this, CloudGlobalDialogFactory.NORMAL_GLOBAL_TYPE);

		CloudGlobalDialogPage.Builder builder = new Builder(page);

		if (!TextUtils.isEmpty(title))
			builder.setTitle(title);
		if (!TextUtils.isEmpty(content))
			builder.setContent(content);
		builder.setOnPositiveButton(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mOnGlobalClickListener != null) {
					mOnGlobalClickListener.onClick();
				}
				finish();
			}
		});
		setContentView(page.getView());
	}

	@Override
	public void finish() {
		super.finish();
		Log.e("global dialog>>>>>finish");
		AccountHelper.setKickOffEventExist(false);
		AccountHelper.logout(this, true);
	}

}

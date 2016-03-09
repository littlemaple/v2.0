package com.medzone.cloud.ui.dialog;

import android.content.Context;

import com.medzone.cloud.ui.dialog.error.CloudErrorDialogPage;
import com.medzone.cloud.ui.dialog.error.NormalErrorDialogPage;

/**
 * TODO 需要思考创建错误对话框需要的参数
 * 
 * @author junqi
 * 
 */
public class CloudErrorDialogFactory implements
		IDialogFactory<CloudErrorDialogPage> {

	public static final int ERROR_TYPE_NORMAL = 0;

	public static final int GLOBAL_TYPE_NORMAL = 1;

	protected CloudErrorDialogFactory() {
	}

	@Override
	public CloudErrorDialogPage createDetailPage(Context context,
			Object... objects) {

		// 错误呈现View类别
		int viewType = (Integer) objects[0];

		switch (viewType) {
		case ERROR_TYPE_NORMAL:
			return new NormalErrorDialogPage(context);
		case GLOBAL_TYPE_NORMAL:
			return new NormalErrorDialogPage(context);
		}
		return null;
	}
}

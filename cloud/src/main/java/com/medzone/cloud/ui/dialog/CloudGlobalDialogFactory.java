package com.medzone.cloud.ui.dialog;

import com.medzone.cloud.ui.dialog.global.CloudGlobalDialogPage;
import com.medzone.cloud.ui.dialog.global.NormalGlobalDialogPage;

import android.content.Context;

public class CloudGlobalDialogFactory implements
		IDialogFactory<CloudGlobalDialogPage> {

	public static int NORMAL_GLOBAL_TYPE = 1;

	protected CloudGlobalDialogFactory() {

	}

	@Override
	public CloudGlobalDialogPage createDetailPage(Context context,
			Object... objects) {
		Integer type = (Integer) objects[0];
		if (type.intValue() == NORMAL_GLOBAL_TYPE)
			return new NormalGlobalDialogPage(context);
		return null;
	}

}

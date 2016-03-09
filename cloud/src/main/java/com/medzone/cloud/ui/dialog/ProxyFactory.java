package com.medzone.cloud.ui.dialog;

import android.util.SparseArray;

public final class ProxyFactory {

	public static final int TYPE_CLOUD_SHARE = 11;
	public static final int TYPE_CLOUD_ERROR = 19;
	public static final int TYPE_CLOUD_GLOBAL = 24;

	private static SparseArray<IDialogFactory<?>> factorys = new SparseArray<IDialogFactory<?>>();

	public static IDialogFactory<?> getFactory(int key) {

		if (!isCreatedInstance(key)) {
			IDialogFactory<?> t = createInstance(key);
			if (t == null) {
				throw new IllegalArgumentException("指定工厂类别不存在！");
			}
			factorys.put(key, t);
		}

		return factorys.get(key);
	}

	private static boolean isCreatedInstance(int key) {

		return factorys.indexOfKey(key) < 0 ? false : true;
	}

	private static IDialogFactory<?> createInstance(int key) {
		IDialogFactory<?> ret;
		switch (key) {
		case TYPE_CLOUD_SHARE:
			ret = new CloudShareDialogFactory();
			break;
		case TYPE_CLOUD_ERROR:
			ret = new CloudErrorDialogFactory();
			break;
		case TYPE_CLOUD_GLOBAL:
			ret = new CloudGlobalDialogFactory();
			break;
		default:
			ret = null;
			break;
		}
		return ret;
	}
}

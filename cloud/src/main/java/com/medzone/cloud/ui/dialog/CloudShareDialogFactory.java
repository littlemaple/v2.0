package com.medzone.cloud.ui.dialog;

import android.content.Context;

import com.medzone.cloud.module.modules.BloodOxygenModule;
import com.medzone.cloud.module.modules.BloodPressureModule;
import com.medzone.cloud.module.modules.EarTemperatureModule;
import com.medzone.cloud.ui.dialog.share.BloodOxygenMonthlyPage;
import com.medzone.cloud.ui.dialog.share.BloodOxygenRecentlyPage;
import com.medzone.cloud.ui.dialog.share.BloodOxygenSinglePage;
import com.medzone.cloud.ui.dialog.share.BloodPressureMonthlyPage;
import com.medzone.cloud.ui.dialog.share.BloodPressureRecentlyPage;
import com.medzone.cloud.ui.dialog.share.BloodPressureSinglePage;
import com.medzone.cloud.ui.dialog.share.CloudShareDialogPage;
import com.medzone.cloud.ui.dialog.share.EarTemperatureMonthlyPage;
import com.medzone.cloud.ui.dialog.share.EarTemperatureRecentlyPage;
import com.medzone.cloud.ui.dialog.share.EarTemperatureSinglePage;

public class CloudShareDialogFactory implements
		IDialogFactory<CloudShareDialogPage> {

	public static final int SHARE_TYPE_SINGLE = 0;
	public static final int SHARE_TYPE_MONTHLY = 1;
	public static final int SHARE_TYPE_RECENTLY = 2;

	protected CloudShareDialogFactory() {
	}

	/**
	 * 
	 * @param shareType
	 *            分享类别 单条数据/月报/近期
	 * @param measureType
	 *            测量类别 血压/血氧/耳温
	 * @return DetailPage
	 */
	@Override
	public CloudShareDialogPage createDetailPage(Context context,
			Object... objects) {
		int shareType = (Integer) objects[0];
		String measureType = (String) objects[1];
		switch (shareType) {
		case SHARE_TYPE_SINGLE:
			return createSingleDetailPage(context, measureType);
		case SHARE_TYPE_MONTHLY:
			return createMonthlyDetailPage(context, measureType);
		case SHARE_TYPE_RECENTLY:
			return createRecentlyDetailPage(context, measureType);
		default:
			return null;
		}

	}

	private CloudShareDialogPage createSingleDetailPage(Context context,
			String measureType) {
		if (BloodPressureModule.TYPE.equals(measureType)) {
			return new BloodPressureSinglePage(context);
		} else if (BloodOxygenModule.TYPE.equals(measureType)) {
			return new BloodOxygenSinglePage(context);
		} else if (EarTemperatureModule.TYPE.equals(measureType)) {
			return new EarTemperatureSinglePage(context);
		} else {
			return null;
		}

	}

	private CloudShareDialogPage createMonthlyDetailPage(Context context,
			String measureType) {
		if (BloodPressureModule.TYPE.equals(measureType)) {
			return new BloodPressureMonthlyPage(context);
		} else if (BloodOxygenModule.TYPE.equals(measureType)) {
			return new BloodOxygenMonthlyPage(context);
		} else if (EarTemperatureModule.TYPE.equals(measureType)) {
			return new EarTemperatureMonthlyPage(context);
		} else {
			return null;
		}
	}

	private CloudShareDialogPage createRecentlyDetailPage(Context context,
			String measureType) {
		if (BloodPressureModule.TYPE.equals(measureType)) {
			return new BloodPressureRecentlyPage(context);
		} else if (BloodOxygenModule.TYPE.equals(measureType)) {
			return new BloodOxygenRecentlyPage(context);
		} else if (EarTemperatureModule.TYPE.equals(measureType)) {
			return new EarTemperatureRecentlyPage(context);
		} else {
			return null;
		}
	}

}

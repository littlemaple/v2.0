package com.medzone.cloud.ui;

import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentManager.OnBackStackChangedListener;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.KeyEvent;

import com.medzone.cloud.Constants;
import com.medzone.cloud.data.CurrentAccountManager;
import com.medzone.cloud.module.CloudMeasureModule;
import com.medzone.cloud.module.CloudMeasureModuleCentreRoot;
import com.medzone.cloud.module.modules.BloodOxygenModule;
import com.medzone.cloud.module.modules.BloodPressureModule;
import com.medzone.cloud.module.modules.EarTemperatureModule;
import com.medzone.cloud.ui.fragment.bloodoxygen.BloodOxygenHistoryListFragment;
import com.medzone.cloud.ui.fragment.bloodoxygen.BloodOxygenHistoryTrendFragment;
import com.medzone.cloud.ui.fragment.bloodoxygen.BloodOxygenResultDetailsFragment;
import com.medzone.cloud.ui.fragment.bloodpressure.BloodPressureHistoryListFragment;
import com.medzone.cloud.ui.fragment.bloodpressure.BloodPressureHistoryTrendFragment;
import com.medzone.cloud.ui.fragment.bloodpressure.BloodPressureResultDetailsFragment;
import com.medzone.cloud.ui.fragment.temperature.EarTemperatureHistoryListFragment;
import com.medzone.cloud.ui.fragment.temperature.EarTemperatureHistoryTrendFragment;
import com.medzone.cloud.ui.fragment.temperature.EarTemperatureResultDetailsFragment;
import com.medzone.framework.Log;
import com.medzone.framework.data.bean.imp.BloodOxygen;
import com.medzone.framework.data.bean.imp.BloodPressure;
import com.medzone.framework.data.bean.imp.EarTemperature;
import com.medzone.framework.fragment.BaseFragment;
import com.medzone.mcloud.R;
import com.medzone.mcloud.R.id;
import com.medzone.mcloud.R.layout;

/**
 * 
 * @author liwm 测量数据容器
 * 
 * @author ChenJunQi at 2014/09/09 18:58
 * 
 */
public class MeasureDataActivity extends BaseActivity {

	public BloodPressure bpFromHome, bpFromHistory;
	public BloodOxygen boFromHome, boFromHistory;
	public EarTemperature etFromHome, etFromHistory;

	private int trendCategory;
	private boolean isSourceHistory = false;

	private CloudMeasureModule<?> attachModule;

	public void setTrendCategory(int state) {
		trendCategory = state;
	}

	public int getTrendCategory() {
		return trendCategory;
	}

	public void setSourceHistory(boolean isSourceHistory) {
		this.isSourceHistory = isSourceHistory;
	}

	public boolean isSourceHistory() {
		return isSourceHistory;
	}

	private void initBloodPressureModule() {
		attachModule = (BloodPressureModule) CloudMeasureModuleCentreRoot
				.applyModule(CurrentAccountManager.getCurAccount(),
						BloodPressureModule.class.getCanonicalName());
	}

	private void initBloodOygenModule() {
		attachModule = (BloodOxygenModule) CloudMeasureModuleCentreRoot
				.applyModule(CurrentAccountManager.getCurAccount(),
						BloodOxygenModule.class.getCanonicalName());
	}

	private void initEarTemperatureModule() {
		attachModule = (EarTemperatureModule) CloudMeasureModuleCentreRoot
				.applyModule(CurrentAccountManager.getCurAccount(),
						EarTemperatureModule.class.getCanonicalName());
	}

	public CloudMeasureModule<?> getAttachModule() {
		return attachModule;
	}

	@Override
	protected void preLoadData(Bundle savedInstanceState) {
		super.preLoadData(savedInstanceState);
		// TODO 此处的字符串需要定义在共同的一处，考虑重构
		Intent intent = getIntent();
		String type = intent.getStringExtra("type");
		// String trendType = intent.getStringExtra("category");

		if (!TextUtils.isEmpty(type) && type.equals("et")) {
			initEarTemperatureModule();
			String measureUid = intent.getStringExtra("measureUid");
			// TODO 这里存在安全隐患，有可能独取不到数据
			if (!TextUtils.isEmpty(measureUid)) {
				etFromHome = ((EarTemperatureModule) getAttachModule())
						.getCacheController().getMeasureEarTemperature(
								measureUid);
			}
			toEarTemperatureResultDetailsFragment(etFromHome);
		} else if (!TextUtils.isEmpty(type) && type.equals("bo")) {
			initBloodOygenModule();
			String measureUid = intent.getStringExtra("measureUid");
			if (!TextUtils.isEmpty(measureUid)) {
				boFromHome = ((BloodOxygenModule) getAttachModule())
						.getCacheController().getMeasureBloodOxygen(measureUid);
			}
			toBloodOxygenResultDetailsFragment(boFromHome);
		} else if (!TextUtils.isEmpty(type) && type.equals("bp")) {
			initBloodPressureModule();
			String measureUid = intent.getStringExtra("measureUid");
			if (!TextUtils.isEmpty(measureUid)) {
				bpFromHome = ((BloodPressureModule) getAttachModule())
						.getCacheController().getMeasureBloodPressure(
								measureUid);
			}
			toBloodPressureResultDetailsFragment(bpFromHome);

			// 点击月报
		} else if (!TextUtils.isEmpty(type) && type.equals("home_bp")) {
			// TODO 在结果详情页跳转到近期趋势的时候传的值
			// if (trendType != null && trendType.equals("trendType")) {
			// setTrendCategory(Constants.VALUE_RECENT);
			// toBloodPressureHistoryTrendFragment();
			// } else {
			// setTrendCategory(Constants.VALUE_MONTH);
			// toBloodPressureHistoryTrendFragment();
			// }
			setTrendCategory(Constants.VALUE_MONTH);
			toBloodPressureHistoryTrendFragment();
		} else if (!TextUtils.isEmpty(type) && type.equals("home_bo")) {
			// TODO 在结果详情页跳转到近期趋势的时候传的值
			// if (trendType != null && trendType.equals("trendType")) {
			// setTrendCategory(Constants.VALUE_RECENT);
			// toBloodOxygenHistoryTrendFragment();
			// } else {
			// setTrendCategory(Constants.VALUE_MONTH);
			// toBloodOxygenHistoryTrendFragment();
			// }
			setTrendCategory(Constants.VALUE_MONTH);
			toBloodOxygenHistoryTrendFragment();
		} else if (!TextUtils.isEmpty(type) && type.equals("home_et")) {
			setTrendCategory(Constants.VALUE_MONTH);
			toEarTemperatureHistoryTrendFragment();
		}
	}

	@Override
	protected void initUI() {
		setContentView(layout.activity_measure_data);
	}

	protected void postInitUI() {
		onListenBackStackEvent();
	};

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			popBackStack();
		}
		return false;
	}

	// =====================================
	// FRAGMENT INTENT
	// =====================================

	public void toEarTemperatureResultDetailsFragment(EarTemperature et) {

		etFromHistory = et;
		FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		ft.add(id.measure_data_container,
				new EarTemperatureResultDetailsFragment());
		ft.addToBackStack(null);
		ft.commitAllowingStateLoss();
	}

	public void toEarTemperatureHistoryListFragment(Fragment curFragment) {
		FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		ft.add(id.measure_data_container,
				new EarTemperatureHistoryListFragment());
		ft.addToBackStack(null);
		ft.commitAllowingStateLoss();
	}

	public void toEarTemperatureHistoryTrendFragment() {

		initEarTemperatureModule();

		FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		ft.add(id.measure_data_container,
				new EarTemperatureHistoryTrendFragment());
		ft.addToBackStack(null);
		ft.commitAllowingStateLoss();
	}

	public void toBloodOxygenResultDetailsFragment(BloodOxygen bo) {

		boFromHistory = bo;
		FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		ft.add(id.measure_data_container,
				new BloodOxygenResultDetailsFragment());
		ft.addToBackStack(null);
		ft.commitAllowingStateLoss();
	}

	public void toBloodOxygenHistoryTrendFragment() {

		initBloodOygenModule();

		FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		ft.add(id.measure_data_container, new BloodOxygenHistoryTrendFragment());
		ft.addToBackStack(null);
		ft.commitAllowingStateLoss();
	}

	public void toBloodOxygenHistoryListFragment(Fragment curFragment) {

		FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		ft.add(R.id.measure_data_container,
				new BloodOxygenHistoryListFragment());
		ft.addToBackStack(null);
		ft.commitAllowingStateLoss();
	}

	// 进入血压单次详情
	public void toBloodPressureResultDetailsFragment(BloodPressure bp) {
		bpFromHistory = bp;
		FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		ft.add(id.measure_data_container,
				new BloodPressureResultDetailsFragment());
		ft.addToBackStack(null);
		ft.commitAllowingStateLoss();
	}

	// 从首页点击月报进入趋势页
	public void toBloodPressureHistoryTrendFragment() {
		initBloodPressureModule();

		FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		ft.add(id.measure_data_container,
				new BloodPressureHistoryTrendFragment());
		ft.addToBackStack(null);
		ft.commitAllowingStateLoss();
	}

	// 进入血压历史列表
	public void toBloodPressureHistoryListFragment(Fragment curFragment) {
		FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		ft.add(id.measure_data_container,
				new BloodPressureHistoryListFragment());
		ft.addToBackStack(null);
		ft.commitAllowingStateLoss();
	}

	// =====================================
	// BACK STACK MANAGE
	// =====================================

	public void popBackStack() {
		FragmentManager fm = getSupportFragmentManager();
		int counts = fm.getBackStackEntryCount();
		if (counts > 0) {

			fm.popBackStack();
			if (counts == 1) {
				finish();
			}
		}
	}

	public void printBackStack() {

		FragmentManager fm = getSupportFragmentManager();
		Log.w("printBackStack#counts:" + fm.getBackStackEntryCount());
		List<Fragment> fms = fm.getFragments();

		try {
			for (int i = 0; i < fms.size(); i++) {
				// Fragment fragment = fms.get(i);
				// Log.w("print fragment#class:"
				// + fragment.getClass().getSimpleName());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	protected void onListenBackStackEvent() {
		final FragmentManager fm = getSupportFragmentManager();
		fm.addOnBackStackChangedListener(new OnBackStackChangedListener() {

			@Override
			public void onBackStackChanged() {
				List<Fragment> fragments = fm.getFragments();

				Log.e("#active fragment size:" + fragments.size()
						+ "#backstack entry size:"
						+ fm.getBackStackEntryCount());

				printBackStack();

				// Enter
				BaseFragment lastFragment = null;
				int lastIndex = fm.getBackStackEntryCount() - 1;
				if (lastIndex >= 0) {
					lastFragment = (BaseFragment) fragments.get(lastIndex);
				}
				if (lastFragment != null) {
					lastFragment.onBackStackEvent();
					Log.e("Enter#lastFragment#"
							+ lastFragment.getClass().getSimpleName());
				} else {
					// Exit
					int restoreIndex = fm.getBackStackEntryCount() - 2;
					BaseFragment restoreFragment = null;
					if (restoreIndex >= 0) {
						restoreFragment = (BaseFragment) fragments
								.get(restoreIndex);
					}
					if (restoreFragment != null) {
						Log.e("Exit#restoreFragment#"
								+ restoreFragment.getClass().getSimpleName());
						restoreFragment.onBackStackEvent();
					}
				}
			}
		});
	}
}

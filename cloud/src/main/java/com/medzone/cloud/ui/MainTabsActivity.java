/**
 * 
 */
package com.medzone.cloud.ui;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTabHost;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TabWidget;
import android.widget.Toast;

import com.medzone.cloud.CloudApplication;
import com.medzone.cloud.CloudApplicationPreference;
import com.medzone.cloud.Constants;
import com.medzone.cloud.GlobalVars;
import com.medzone.cloud.data.CurrentAccountManager;
import com.medzone.cloud.data.PropertyCenter;
import com.medzone.cloud.data.helper.AccountModuleHelper;
import com.medzone.cloud.module.CloudMeasureModuleCentreRoot;
import com.medzone.cloud.module.modules.AccountModule;
import com.medzone.cloud.network.NetworkClientHelper;
import com.medzone.cloud.ui.fragment.HomeFragment;
import com.medzone.cloud.ui.fragment.ServiceFragment;
import com.medzone.cloud.ui.fragment.group.GroupFragment;
import com.medzone.cloud.ui.fragment.setting.SettingFragment;
import com.medzone.cloud.util.GeneralUtil;
import com.medzone.framework.Log;
import com.medzone.framework.data.bean.imp.Account;
import com.medzone.framework.data.controller.CentreControllerRoot;
import com.medzone.mcloud.R;
import com.medzone.mcloud.R.layout;
import com.umeng.update.UmengDialogButtonListener;

public class MainTabsActivity extends BaseActivity implements
		PropertyChangeListener {

	private Account curAccount;
	private FragmentTabHost mTabHost;
	private TabWidget mTabWidget;

	private int defaultMainPosition = 0;
	private long firstBackEventTimeStamp;

	private void measureActionBarHeight() {

		if (CloudApplication.isNewAPI(Build.VERSION_CODES.HONEYCOMB)) {
			// Calculate ActionBar height
			TypedValue tv = new TypedValue();
			if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv,
					true)) {
				CloudApplication.actionBarHeight = TypedValue
						.complexToDimensionPixelSize(tv.data, getResources()
								.getDisplayMetrics());
			}
		} else {
			TypedValue tv = new TypedValue();
			if (getTheme().resolveAttribute(R.attr.actionBarSize, tv, true)) {
				CloudApplication.actionBarHeight = TypedValue
						.complexToDimensionPixelSize(tv.data, getResources()
								.getDisplayMetrics());
			}
		}
	}

	@Override
	protected void preLoadData(Bundle savedInstanceState) {
		super.preLoadData(savedInstanceState);

		curAccount = (Account) CurrentAccountManager.getCurAccount();
		defaultMainPosition = CloudApplicationPreference
				.getDefTabPosition(defaultMainPosition);
		CloudMeasureModuleCentreRoot.doGetModuleSpec(curAccount, this, false);
		CloudApplication.defenderServiceManager.startJPush();
		measureActionBarHeight();
	}

	// ========================================
	// 处理推送跳转
	// ========================================

	@Override
	protected void initUI() {
		View contentView = getLayoutInflater().inflate(
				R.layout.activity_maintabs, null);
		setContentView(contentView);
		inflateMainTabs(contentView);
		doAppUpdate();
		PropertyCenter.addPropertyChangeListener(this);
	}

	private void inflateMainTabs(View view) {
		mTabHost = (FragmentTabHost) view.findViewById(android.R.id.tabhost);
		mTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);

		if (CloudApplication.isNewAPI(Build.VERSION_CODES.HONEYCOMB)) {
			mTabWidget = mTabHost.getTabWidget();
			mTabWidget.setDividerDrawable(android.R.color.transparent);
		} else {
			// 如果在v11一下设置divider透明，将导致Tab无法正常显示
		}

		mTabHost.addTab(
				createTabSpec(mTabHost, Constants.INDICATOR_HOME,
						layout.indicator_home), HomeFragment.class, null);
		mTabHost.addTab(
				createTabSpec(mTabHost, Constants.INDICATOR_GROUP,
						layout.indicator_group), GroupFragment.class, null);
		mTabHost.addTab(
				createTabSpec(mTabHost, Constants.INDICATOR_MEASURE,
						layout.indicator_measure), Fragment.class, null);
		mTabHost.addTab(
				createTabSpec(mTabHost, Constants.INDICATOR_SERVICE,
						layout.indicator_service), ServiceFragment.class, null);
		mTabHost.addTab(
				createTabSpec(mTabHost, Constants.INDICATOR_SETTING,
						layout.indicator_setting), SettingFragment.class, null);
		mTabHost.setCurrentTab(defaultMainPosition);
	}

	private TabSpec createTabSpec(TabHost host, String tag, int viewResId) {

		View indicatorView = getLayoutInflater().inflate(viewResId, null);
		TabSpec tabSpec = host.newTabSpec(tag);
		tabSpec.setIndicator(indicatorView);
		return tabSpec;
	}

	public void onCenterTabClick(View view) {
		startActivity(new Intent(this, CentreDetectionActivity.class));
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			long secondtime = System.currentTimeMillis();
			if (secondtime - firstBackEventTimeStamp > 3000) {
				Toast.makeText(this, "再按一次返回键退出", Toast.LENGTH_SHORT).show();
				firstBackEventTimeStamp = System.currentTimeMillis();
				return true;
			} else {

				// TODO 重复的代码块[注销]
				Account account = CurrentAccountManager.getCurAccount();
				AccountModule module = AccountModuleHelper.get(account);
				AccountModuleHelper.remove(module);

				CloudMeasureModuleCentreRoot.removeAll(account);
				CentreControllerRoot.getInstance().removeAllController();
				CurrentAccountManager.setCurAccount(null);

				finish();
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	private void doAppUpdate() {

		if (!GlobalVars.isRepeatShowUpdateDialog) {
			GeneralUtil.doAppForceUpdate(this, new UmengDialogButtonListener() {

				@Override
				public void onClick(int cmd) {
					if (cmd == 5) {
						// showToast("Downloading ……");
					}
					if (NetworkClientHelper.isForcedUpdate()) {
						finish();
					}
				}
			});
			GlobalVars.isRepeatShowUpdateDialog = true;
		}
	}

	@Override
	public void propertyChange(PropertyChangeEvent event) {
		if (event.getPropertyName().equals(PropertyCenter.PROPERTY_FINISH_HOME)) {
			Log.e(">>>>>收到关闭的信息，关闭首页");
			finish();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		PropertyCenter.getInstance().removePropertyChangeListener(this);
	}

}

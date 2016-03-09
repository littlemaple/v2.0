/**
 * 
 */
package com.medzone.cloud.ui;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;

import com.medzone.cloud.Constants;
import com.medzone.cloud.data.CurrentAccountManager;
import com.medzone.cloud.data.TemporaryData;
import com.medzone.cloud.module.CloudMeasureModuleCentreRoot;
import com.medzone.cloud.ui.adapter.CentreDetectionAdapter;
import com.medzone.framework.data.bean.imp.Account;
import com.medzone.framework.data.bean.imp.GroupMember;
import com.medzone.framework.view.viewpager.CirclePageIndicator;
import com.medzone.framework.view.viewpager.IPageIndicator;
import com.medzone.mcloud.R.id;
import com.medzone.mcloud.R.layout;

/**
 * 如果是待测帐号进入的,则在退出这个Activity时,把待测帐号创建的controller销毁释放掉.
 * 
 */
public class CentreDetectionActivity extends FragmentActivity {

	private ViewPager mPager;
	private IPageIndicator mIndicator;
	private CentreDetectionAdapter mAdapter;
	private GroupMember groupmember;// 待检测的人的帐号

	@Override
	protected void onResume() {
		super.onResume();
		mAdapter.notifyDataSetChanged();
	}

	private void preLoad() {
		if (TemporaryData.containsKey(Constants.TEMPORARYDATA_KEY_TEST_ACCOUNT)) {
			groupmember = (GroupMember) TemporaryData
					.get(Constants.TEMPORARYDATA_KEY_TEST_ACCOUNT);
		} else {
			Account account = CurrentAccountManager.getCurAccount();
			groupmember = new GroupMember();
			groupmember.setAccountID(account.getAccountID());
			groupmember.setNickname(account.getNickname());
			groupmember.setRealName(account.getRealName());
			groupmember.setRemark(null);
			groupmember.setHeadPortRait(account.getHeadPortRait());
		}
	}

	private void controlPosition() {
		WindowManager.LayoutParams lp = getWindow().getAttributes();
		lp.width = LayoutParams.MATCH_PARENT;
		lp.gravity = Gravity.BOTTOM;
		getWindow().setAttributes(lp);
	}

	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		preLoad();

		setContentView(layout.dialog_measure_device);
		mPager = (ViewPager) findViewById(id.pager);
		mIndicator = (CirclePageIndicator) findViewById(id.indicator);

		controlPosition();
		postInitUI();
	}

	protected void postInitUI() {

		mAdapter = new CentreDetectionAdapter(getSupportFragmentManager(),
				this, groupmember);
		mPager.setAdapter(mAdapter);
		// 不使用被测人的模块，而使用登陆者本身的模块
		// mAdapter.setContent(CloudMeasureModuleCentreRoot
		// .getModules(testAccount));
		mAdapter.setContent(CloudMeasureModuleCentreRoot
				.getModules(CurrentAccountManager.getCurAccount()));

		mIndicator.setViewPager(mPager);
	}

}

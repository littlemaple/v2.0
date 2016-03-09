/**
 * 
 */
package com.medzone.cloud.ui.fragment;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.medzone.cloud.CloudImageLoader;
import com.medzone.cloud.data.CurrentAccountManager;
import com.medzone.cloud.data.PropertyCenter;
import com.medzone.cloud.data.helper.AccountHelper;
import com.medzone.cloud.module.CloudMeasureModule;
import com.medzone.cloud.module.CloudMeasureModuleCentreRoot;
import com.medzone.cloud.module.modules.BloodOxygenModule;
import com.medzone.cloud.module.modules.BloodPressureModule;
import com.medzone.cloud.module.modules.EarTemperatureModule;
import com.medzone.cloud.ui.MeasureDataActivity;
import com.medzone.cloud.ui.SettingManagerDeviceActivity;
import com.medzone.cloud.ui.SettingPersonalInfoActivity;
import com.medzone.cloud.ui.adapter.MeasureHistoryAdapter;
import com.medzone.cloud.ui.adapter.MonthlyReportAdapter;
import com.medzone.framework.Log;
import com.medzone.framework.data.bean.imp.Account;
import com.medzone.framework.fragment.BaseFragment;
import com.medzone.framework.view.HorizontalListView;
import com.medzone.framework.view.RoundedImageView;
import com.medzone.mcloud.R;
import com.medzone.mcloud.R.id;

/**
 * @author ChenJunQi.
 * 
 */
public class HomeFragment extends BaseFragment implements
		PropertyChangeListener {

	private View rootView;
	private RoundedImageView rivAvatar;// 个人头像
	private TextView tvNickname;// 个人昵称
	private HorizontalListView hlMonthlyReport;// 月度报告列表
	private TextView tvLink;
	private ViewPager viewPager;
	private PagerTabStrip pagerTab;
	private RelativeLayout rlPlaceHolder;
	private LinearLayout llMonthlyReport;

	private MonthlyReportAdapter mrAdapter;
	private MeasureHistoryAdapter measureHistoryAdapter;
	private Account attachAccount;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		PropertyCenter.getInstance().addPropertyChangeListener(this);
		attachAccount = CurrentAccountManager.getCurAccount();
		if (attachAccount == null) {
			AccountHelper.doAutoLoginTask(getActivity());
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		if (rootView == null) {

			rootView = inflater.inflate(R.layout.fragment_home, container,
					false);
			llMonthlyReport = (LinearLayout) rootView
					.findViewById(id.ll_monthly_report);
			rlPlaceHolder = (RelativeLayout) rootView
					.findViewById(id.rl_place_holder);
			rivAvatar = (RoundedImageView) rootView
					.findViewById(R.id.im_personal_icon);
			tvNickname = (TextView) rootView
					.findViewById(R.id.tv_personal_name);
			hlMonthlyReport = (HorizontalListView) rootView
					.findViewById(R.id.hl_monthly_report);
			tvLink = (TextView) rootView.findViewById(R.id.tv_ad_2);
			viewPager = (ViewPager) rootView.findViewById(R.id.viewpager);
			pagerTab = (PagerTabStrip) rootView.findViewById(R.id.pagertitle);
			pagerTab.setTextColor(getResources()
					.getColor(android.R.color.white));

			tvLink.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					addDevice();
				}
			});

		} else {
			ViewGroup parent = (ViewGroup) rootView.getParent();
			if (parent != null) {
				parent.removeView(rootView);
			}
		}
		// 预初始化授权列表，以便后部调用
		CurrentAccountManager.getAuthorizedList(true, false);
		return rootView;
	}

	public void addDevice() {
		startActivity(new Intent(getActivity(),
				SettingManagerDeviceActivity.class));
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		showPersonView();
		List<CloudMeasureModule<?>> mds = null;
		if (attachAccount != null) {
			mds = CloudMeasureModuleCentreRoot.getModules(attachAccount);
		}
		if (mds != null && mds.size() > 0) {
			showHistoryViewPager();
			showMonthlyReportView();

		} else {
			Log.w("模块还未初始化，等待异常初始化模块完成……回调该方法");
		}

	}

	@Override
	public void onDetach() {
		super.onDetach();
		PropertyCenter.getInstance().removePropertyChangeListener(this);
	}

	private void showHistoryViewPager() {
		if (measureHistoryAdapter == null) {
			measureHistoryAdapter = new MeasureHistoryAdapter(getActivity(),
					getChildFragmentManager());
			pagerTab.setTabIndicatorColor(getResources().getColor(
					android.R.color.white));
			viewPager.setAdapter(measureHistoryAdapter);
		}
		measureHistoryAdapter.setContent(CloudMeasureModuleCentreRoot
				.getModules(attachAccount));
		if (measureHistoryAdapter.getDisplayModuleCount() > 0) {
			rlPlaceHolder.setVisibility(View.GONE);
			llMonthlyReport.setVisibility(View.VISIBLE);
			viewPager.setVisibility(View.VISIBLE);
		} else {
			rlPlaceHolder.setVisibility(View.VISIBLE);
			viewPager.setVisibility(View.GONE);
			llMonthlyReport.setVisibility(View.GONE);
		}
	}

	private void fillAvatarView() {
		if (!TextUtils.isEmpty(attachAccount.getHeadPortRait())) {
			CloudImageLoader.getInstance().getImageLoader()
					.displayImage(attachAccount.getHeadPortRait(), rivAvatar);
		}
	}

	private void showPersonView() {
		// FIXME 使用clone对象对帐号类信息进行展示,因为多线程setCurrentAccount时,导致读取到脏数据
		try {
			fillAvatarView();
			if (!TextUtils.isEmpty(attachAccount.getNickname())) {
				tvNickname.setText(attachAccount.getNickname());
			} else {
				tvNickname
						.setText(attachAccount.getPhone() == null ? attachAccount
								.getEmail() : attachAccount.getPhone());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		// 头像跳转
		rivAvatar.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(),
						SettingPersonalInfoActivity.class);
				startActivity(intent);
			}
		});

	}

	private void showMonthlyReportView() {

		final List<CloudMeasureModule<?>> associatedModuleList = CloudMeasureModuleCentreRoot
				.getModules(attachAccount);

		if (mrAdapter == null) {
			mrAdapter = new MonthlyReportAdapter(getActivity());
			hlMonthlyReport.setAdapter(mrAdapter);
			hlMonthlyReport.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {

					CloudMeasureModule<?> item = (CloudMeasureModule<?>) mrAdapter
							.getItem(position);

					if (item instanceof BloodOxygenModule) {
						Intent intent = new Intent(getActivity(),
								MeasureDataActivity.class);
						intent.putExtra("type", "home_bo");
						getActivity().startActivity(intent);
					} else if (item instanceof BloodPressureModule) {
						Intent intent = new Intent(getActivity(),
								MeasureDataActivity.class);
						intent.putExtra("type", "home_bp");
						getActivity().startActivity(intent);
					} else if (item instanceof EarTemperatureModule) {
						Intent intent = new Intent(getActivity(),
								MeasureDataActivity.class);
						intent.putExtra("type", "home_et");
						getActivity().startActivity(intent);
					}
				}
			});
		}
		mrAdapter.setContent(associatedModuleList);

	}

	@Override
	public void propertyChange(PropertyChangeEvent event) {
		if (event.getPropertyName().equals(
				PropertyCenter.PROPERTY_REFRESH_ACCOUNT)) {
			showPersonView();

		} else if (event.getPropertyName().equals(
				PropertyCenter.PROPERTY_REFRESH_MY_MODULES)) {
			// 模块加载好，可通知月统计栏目显示，但此时月度统计的数据可能为空
			showHistoryViewPager();
			showMonthlyReportView();
		} else if (event.getPropertyName().equals(
				PropertyCenter.PROPERTY_REFRESH_HISTORY_DATA)) {
			// 数据加载好，需要同时刷新历史列表以及月统计
			showMonthlyReportView();
			showHistoryViewPager();
		} else if (event.getPropertyName().equals(
				PropertyCenter.PROPERTY_REFRESH_AVATAR)) {
			fillAvatarView();
		}
	}

}

///**
// * 
// */
//package com.medzone.cloud.ui;
//
//import java.beans.PropertyChangeEvent;
//import java.beans.PropertyChangeListener;
//import java.util.List;
//
//import android.support.v4.view.PagerTabStrip;
//import android.support.v4.view.ViewPager;
//import android.text.TextUtils;
//import android.view.Gravity;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.widget.AdapterView;
//import android.widget.AdapterView.OnItemClickListener;
//import android.widget.ImageButton;
//import android.widget.RelativeLayout;
//import android.widget.TextView;
//
//import com.actionbarsherlock.app.ActionBar;
//import com.actionbarsherlock.app.ActionBar.LayoutParams;
//import com.medzone.cloud.data.PropertyCenter;
//import com.medzone.cloud.data.TemporaryData;
//import com.medzone.cloud.module.CloudBaseModule;
//import com.medzone.cloud.module.CloudMeasureModuleCentreRoot;
//import com.medzone.cloud.module.modules.BloodOxygenModule;
//import com.medzone.cloud.module.modules.BloodPressureModule;
//import com.medzone.cloud.module.modules.CloudMeasureModule;
//import com.medzone.cloud.module.modules.EarTemperatureModule;
//import com.medzone.cloud.ui.adapter.MeasureHistoryAdapter;
//import com.medzone.cloud.ui.adapter.MonthlyReportAdapter;
//import com.medzone.framework.data.bean.imp.Account;
//import com.medzone.framework.view.HorizontalListView;
//import com.medzone.framework.view.RoundedImageView;
//import com.medzone.mcloud.R;
//import com.medzone.mcloud.R.drawable;
//import com.medzone.mcloud.R.id;
//import com.nostra13.universalimageloader.core.ImageLoader;
//
///**
// * @author ChenJunQi.
// * 
// */
//public class HealthCentreActivity extends BaseActivity implements
//		OnClickListener, PropertyChangeListener {
//
//	private RoundedImageView rivAvatar;
//	private TextView tvNickname;
//	private HorizontalListView hlMonthlyReport;
//	private RelativeLayout rlPlaceHolder;
//
//	private MonthlyReportAdapter mrAdapter;
//
//	private ViewPager viewPager;
//	private PagerTabStrip pagerTab;
//	private MeasureHistoryAdapter measureHistoryAdapter;
//
//	private Account viewAccount;
//
//	@Override
//	protected void onStart() {
//		super.onStart();
//		PropertyCenter.propertySupport.addPropertyChangeListener(this);
//	}
//
//	@Override
//	public void onResume() {
//		super.onResume();
//
//		initActionBar();
//	}
//
//	@Override
//	protected void preLoadData() {
//
//		if (TemporaryData.containsKey(Account.class.getName())) {
//			viewAccount = (Account) TemporaryData.get(Account.class.getName());
//		}
//	}
//
//	private void initActionBar() {
//
//		ActionBar actionBar = getSupportActionBar();
//		ActionBar.LayoutParams params = new ActionBar.LayoutParams(
//				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT,
//				Gravity.CENTER);
//		View view = LayoutInflater.from(this).inflate(
//				R.layout.custom_actionbar_with_image, null);
//		TextView title = (TextView) view.findViewById(R.id.actionbar_title);
//		title.setText("健康中心");
//		ImageButton leftButton = (ImageButton) view
//				.findViewById(id.actionbar_left);
//		leftButton.setImageResource(drawable.public_ic_back);
//		leftButton.setOnClickListener(this);
//		actionBar.setCustomView(view, params);
//		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
//		actionBar.setDisplayShowCustomEnabled(true);
//	}
//
//	@Override
//	protected void initUI() {
//		setContentView(R.layout.fragment_home);
//		rivAvatar = (RoundedImageView) findViewById(R.id.im_personal_icon);
//		tvNickname = (TextView) findViewById(R.id.tv_personal_name);
//		hlMonthlyReport = (HorizontalListView) findViewById(R.id.hl_monthly_report);
//		viewPager = (ViewPager) findViewById(R.id.viewpager);
//		rlPlaceHolder = (RelativeLayout) findViewById(R.id.rl_place_holder);
//		pagerTab = (PagerTabStrip) findViewById(R.id.pagertitle);
//		pagerTab.setTextColor(getResources().getColor(android.R.color.white));
//	}
//
//	@Override
//	protected void postInitUI() {
//		super.postInitUI();
//
//		showPersonView();
//		showMonthlyReportView();
//		showHistoryViewPager();
//	}
//
//	@Override
//	protected void onDestroy() {
//		super.onDestroy();
//		PropertyCenter.propertySupport.removePropertyChangeListener(this);
//	}
//
//	private void showHistoryViewPager() {
//		if (measureHistoryAdapter == null) {
//			measureHistoryAdapter = new MeasureHistoryAdapter(this,
//					getSupportFragmentManager());
//			pagerTab.setTabIndicatorColor(getResources()
//					.getColor(R.color.white));
//			viewPager.setAdapter(measureHistoryAdapter);
//		}
//		measureHistoryAdapter.setContent(CloudMeasureModuleCentreRoot
//				.getModules(viewAccount));
//		if (measureHistoryAdapter.getDisplayModuleCount() > 0) {
//			rlPlaceHolder.setVisibility(View.GONE);
//			viewPager.setVisibility(View.VISIBLE);
//		} else {
//			rlPlaceHolder.setVisibility(View.VISIBLE);
//			viewPager.setVisibility(View.GONE);
//		}
//	}
//
//	private void showPersonView() {
//
//		if (!TextUtils.isEmpty(viewAccount.getHeadPortRait())) {
//			CloudImageLoader.getInstance().getImageLoader().displayImage(
//					viewAccount.getHeadPortRait(), rivAvatar);
//		}
//		if (!TextUtils.isEmpty(viewAccount.getNickname())) {
//			tvNickname.setText(viewAccount.getNickname());
//		} else {
//			tvNickname.setText(viewAccount.getPhone() == null ? viewAccount
//					.getEmail() : viewAccount.getPhone());
//		}
//
//	}
//
//	private void showMonthlyReportView() {
//
//		if (mrAdapter == null) {
//			mrAdapter = new MonthlyReportAdapter(this);
//		}
//		final List<CloudBaseModule> associatedModuleList = CloudMeasureModuleCentreRoot
//				.getModules(viewAccount);
//		mrAdapter.setContent(associatedModuleList);
//		hlMonthlyReport.setAdapter(mrAdapter);
//		hlMonthlyReport.setOnItemClickListener(new OnItemClickListener() {
//
//			@Override
//			public void onItemClick(AdapterView<?> parent, View view,
//					int position, long id) {
//				if (associatedModuleList.get(position) instanceof BloodOxygenModule) {
//					showToast("点击进入血氧月报");
//				} else if (associatedModuleList.get(position) instanceof BloodPressureModule) {
//					showToast("点击进入血压月报");
//				} else if (associatedModuleList.get(position) instanceof EarTemperatureModule) {
//					showToast("点击进入耳温月报");
//				}
//			}
//		});
//
//	}
//
//	@Override
//	public void propertyChange(PropertyChangeEvent event) {
//		if (event.getPropertyName().equals(
//				CloudMeasureModule.class.getSimpleName())) {
//
//			showMonthlyReportView();
//			showHistoryViewPager();
//		}
//	}
//
//	@Override
//	public void onClick(View v) {
//		switch (v.getId()) {
//		case id.actionbar_left:
//			finish();
//			break;
//		default:
//			break;
//		}
//	}
//
//}

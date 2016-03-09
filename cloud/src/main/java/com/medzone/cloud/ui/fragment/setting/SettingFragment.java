/**
 * 
 */
package com.medzone.cloud.ui.fragment.setting;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.medzone.cloud.CloudApplication;
import com.medzone.cloud.CloudImageLoader;
import com.medzone.cloud.data.CurrentAccountManager;
import com.medzone.cloud.data.PropertyCenter;
import com.medzone.cloud.ui.SettingAboutActivity;
import com.medzone.cloud.ui.SettingManagerDeviceActivity;
import com.medzone.cloud.ui.SettingMeasureActivity;
import com.medzone.cloud.ui.SettingPersonalInfoActivity;
import com.medzone.cloud.ui.SettingPrivacyActivity;
import com.medzone.cloud.ui.fragment.CustomPlatformFragment;
import com.medzone.framework.data.bean.imp.Account;
import com.medzone.framework.fragment.BaseFragment;
import com.medzone.framework.view.RoundedImageView;
import com.medzone.mcloud.R;
import com.medzone.mcloud.R.id;
import com.medzone.mcloud.R.layout;
import com.medzone.mcloud.R.string;

/**
 * @author ChenJunQi.
 * 
 */
public class SettingFragment extends BaseFragment implements
		PropertyChangeListener {

	private LinearLayout llPersonalInfo;
	private TextView tvPrivacy, tvManagerDevice, tvMeasureSetting, tvUseHelper,
			tvShared, tvAbout, tvPersonName;
	private RoundedImageView ivHeadPortrait;
	private Account account;

	private View actionBarView;

	@Override
	protected void initActionBar() {
		int heightPx = CloudApplication.actionBarHeight;
		actionBarView = LayoutInflater.from(getActivity()).inflate(
				R.layout.custom_actionbar_with_text, null);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, heightPx, Gravity.CENTER);
		actionBarView.setLayoutParams(params);
		TextView title = (TextView) actionBarView
				.findViewById(R.id.actionbar_title);
		title.setText(string.actionbar_title_setting);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		account = CurrentAccountManager.getCurAccount();
		PropertyCenter.addPropertyChangeListener(this);
	}

	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		initActionBar();
		View view = inflater.inflate(layout.fragment_setting, container, false);
		LinearLayout llActionBar = (LinearLayout) view
				.findViewById(R.id.actionbar);
		llActionBar.addView(actionBarView);
		llPersonalInfo = (LinearLayout) view.findViewById(R.id.person_info);
		tvAbout = (TextView) view.findViewById(R.id.tv_about);
		tvManagerDevice = (TextView) view.findViewById(R.id.tv_manager_device);
		tvMeasureSetting = (TextView) view
				.findViewById(R.id.tv_measure_setting);
		tvPrivacy = (TextView) view.findViewById(R.id.tv_privacy);
		tvUseHelper = (TextView) view.findViewById(R.id.tv_user_helper);
		tvShared = (TextView) view.findViewById(R.id.tv_shared);
		ivHeadPortrait = (RoundedImageView) view
				.findViewById(id.im_personal_icon);
		tvPersonName = (TextView) view.findViewById(id.tv_personal_name);
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		initUI();
		fillView();
		// 预初始化授权列表，以便后部调用
		CurrentAccountManager.getAuthorizedList(true, false);
	}

	@Override
	public void onDestroy() {
		PropertyCenter.removePropertyChangeListener(this);
		super.onDestroy();
	}

	public void initUI() {
		FragmemntClickListener listener = new FragmemntClickListener();

		llPersonalInfo.setOnClickListener(listener);
		tvAbout.setOnClickListener(listener);
		tvManagerDevice.setOnClickListener(listener);
		tvMeasureSetting.setOnClickListener(listener);
		tvPrivacy.setOnClickListener(listener);
		tvUseHelper.setOnClickListener(listener);
		tvShared.setOnClickListener(listener);
	}

	private void fillAvatarView() {
		if (!TextUtils.isEmpty(account.getHeadPortRait())) {
			CloudImageLoader.getInstance().getImageLoader()
					.displayImage(account.getHeadPortRait(), ivHeadPortrait);
		}
	}

	private void fillView() {
		if (!TextUtils.isEmpty(account.getHeadPortRait())) {
			CloudImageLoader.getInstance().getImageLoader()
					.displayImage(account.getHeadPortRait(), ivHeadPortrait);
		}
		tvPersonName.setText(account.getNickname());
	}

	public class FragmemntClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.person_info:
				jumpToTarget(SettingPersonalInfoActivity.class);
				break;
			case R.id.tv_about:
				jumpToTarget(SettingAboutActivity.class);
				break;
			case R.id.tv_manager_device:
				jumpToTarget(SettingManagerDeviceActivity.class);
				break;
			case R.id.tv_measure_setting:
				jumpToTarget(SettingMeasureActivity.class);
				break;
			case R.id.tv_privacy:
				jumpToTarget(SettingPrivacyActivity.class);
				break;
			case R.id.tv_user_helper:
				// jumpToTarget(setting.class);
				break;
			case R.id.tv_shared:
				// shareText(getActivity(), "心云分享", "我是心云");
				showShareDialog();
				break;
			default:
				break;
			}
		}
	}

	/**
	 * 
	 * @author ChenJunQi.
	 */
	private void showShareDialog() {

		FragmentTransaction ft = getFragmentManager().beginTransaction();
		CustomPlatformFragment newFragment = new CustomPlatformFragment();
		newFragment.show(ft, CustomPlatformFragment.class.getName());
	}

	public void jumpToTarget(Class<?> clz) {
		Intent intent = new Intent(getActivity(), clz);
		startActivity(intent);
	}

	@Override
	public void propertyChange(PropertyChangeEvent event) {

		if (event.getPropertyName().equals(
				PropertyCenter.PROPERTY_REFRESH_ACCOUNT)) {
			fillView();
		} else if (event.getPropertyName().equals(
				PropertyCenter.PROPERTY_REFRESH_AVATAR)) {
			fillAvatarView();
		}
	}

}

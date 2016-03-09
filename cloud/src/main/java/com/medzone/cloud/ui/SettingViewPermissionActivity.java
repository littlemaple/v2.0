package com.medzone.cloud.ui;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.LayoutParams;
import com.medzone.cloud.data.CurrentAccountManager;
import com.medzone.cloud.data.PropertyCenter;
import com.medzone.cloud.ui.fragment.group.GroupMemberPermissionFragment;
import com.medzone.framework.data.bean.imp.GroupMember;
import com.medzone.mcloud.R;
import com.medzone.mcloud.R.drawable;
import com.medzone.mcloud.R.id;

public class SettingViewPermissionActivity extends BaseActivity implements
		OnClickListener, PropertyChangeListener {
	private TextView hintTV;
	private List<GroupMember> viewAuthzList;
	private View hideViewCenter, hideViewTop;

	@Override
	protected void preLoadData(Bundle savedInstanceState) {
		super.preLoadData(savedInstanceState);
		viewAuthzList = CurrentAccountManager.getViewList();
	}

	@Override
	protected void preInitUI() {
		initActionBar();
		PropertyCenter.getInstance().addPropertyChangeListener(this);
	}

	@Override
	protected void initUI() {
		setContentView(R.layout.activity_setting_permission);
		hintTV = (TextView) findViewById(id.setting_permission_hintTV);
		hideViewCenter = findViewById(R.id.hide_view_center);

		hideViewCenter.setOnClickListener(this);
		hideViewTop.setOnClickListener(this);
	}

	private void initActionBar() {
		ActionBar actionBar = getSupportActionBar();
		ActionBar.LayoutParams params = new ActionBar.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT,
				Gravity.CENTER);
		View view = LayoutInflater.from(this).inflate(
				R.layout.custom_actionbar_with_image, null);
		hideViewTop = view.findViewById(R.id.hide_view_top);
		TextView title = (TextView) view.findViewById(R.id.actionbar_title);
		title.setText(R.string.actionbar_title_setting_isview);
		title.setOnClickListener(this);

		ImageButton leftButton = (ImageButton) view
				.findViewById(id.actionbar_left);
		leftButton.setImageResource(drawable.public_ic_back);
		leftButton.setOnClickListener(this);

		ImageButton rightButton = (ImageButton) view
				.findViewById(R.id.actionbar_right);
		rightButton.setImageResource(R.drawable.securityprivacyview_ic_edit2x);
		rightButton.setOnClickListener(this);

		actionBar.setCustomView(view, params);
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		actionBar.setDisplayShowCustomEnabled(true);
	}

	@Override
	protected void postInitUI() {
		hintTV.setText(R.string.allow_view_tip);
		replaceFragment(viewAuthzList);
	}

	private void replaceFragment(List<GroupMember> memberList) {
		FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		GroupMemberPermissionFragment gvFragment = new GroupMemberPermissionFragment();
		gvFragment.setContent(memberList);
		gvFragment.isMeasure(false);
		ft.replace(id.fragment_container, gvFragment);
		ft.disallowAddToBackStack();
		ft.commitAllowingStateLoss();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.actionbar_left:
			finish();
			break;
		case R.id.actionbar_right:
			// jumpToTarget(SettingPermissionAddActivity.class, true);
			toogle();
			break;
		case R.id.hide_view_center:
			toogle();
			break;
		case R.id.hide_view_top:
			toogle();
			break;
		default:
			break;
		}
	}

	public void toogle() {
		if (GroupMemberPermissionFragment.isLongPressed) {
			GroupMemberPermissionFragment.setLongPressedState(false);
		} else {
			GroupMemberPermissionFragment.setLongPressedState(true);
		}
	}

	@Override
	public void finish() {
		if (GroupMemberPermissionFragment.isLongPressed) {
			GroupMemberPermissionFragment.setLongPressedState(false);
		} else {
			super.finish();
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		getCurrentFocus();
		return super.onTouchEvent(event);
	}

	@Override
	public void propertyChange(PropertyChangeEvent event) {
		if (TextUtils.equals(event.getPropertyName(),
				PropertyCenter.PROPERTY_REFRESH_DELETE_STAGE)) {
			boolean stage = (Boolean) event.getNewValue();
			if (stage) {
				hideViewCenter.setVisibility(View.VISIBLE);
				hideViewTop.setVisibility(View.VISIBLE);
			} else {
				hideViewCenter.setVisibility(View.GONE);
				hideViewTop.setVisibility(View.GONE);
			}
		}
	}

}

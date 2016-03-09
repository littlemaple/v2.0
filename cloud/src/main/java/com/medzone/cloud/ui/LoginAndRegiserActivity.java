package com.medzone.cloud.ui;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.medzone.cloud.ui.fragment.login.LoginFragment;
import com.medzone.mcloud.R;

public class LoginAndRegiserActivity extends BaseActivity {

	@Override
	protected void initUI() {
		super.initUI();
		setContentView(R.layout.activity_login_register_main);

	}

	@Override
	protected void postInitUI() {
		super.postInitUI();
		FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		ft.add(R.id.login_container, new LoginFragment());
		ft.commitAllowingStateLoss();
	}
}

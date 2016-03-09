package com.medzone.cloud.ui;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import com.medzone.cloud.ui.dialog.error.ErrorDialogUtil;
import com.medzone.cloud.ui.fragment.ChooseCarriedFragment;
import com.medzone.framework.errorcode.ProxyCode.LocalError;
import com.medzone.framework.errorcode.ProxyErrorCode;
import com.medzone.framework.util.NetUtil;
import com.medzone.mcloud.R;
import com.medzone.mcloud.R.layout;

public class ShareActivity extends BaseActivity {

	public ChooseCarriedFragment mChooseCarriedFragment;

	public static ShareActivity instance;

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		instance = this;
		setContentView(layout.activity_main);

		if (!NetUtil.isConnect(this)) {
			ErrorDialogUtil.showErrorDialog(this, ProxyErrorCode.TYPE_MEASURE,
					LocalError.CODE_18100, true);
			finish();
		} else {
			if (savedInstanceState == null) {
				commitChooseCarriedFragment();
			}
		}
	}

	private void commitChooseCarriedFragment() {

		if (mChooseCarriedFragment == null) {
			mChooseCarriedFragment = new ChooseCarriedFragment();
		}
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		ft.add(R.id.container, mChooseCarriedFragment);
		ft.disallowAddToBackStack();
		ft.commitAllowingStateLoss();

	}

}

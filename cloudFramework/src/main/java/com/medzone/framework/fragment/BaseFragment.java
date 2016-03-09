/**
 * 
 */
package com.medzone.framework.fragment;

import android.content.res.Configuration;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;
import com.medzone.framework.Log;

/**
 * @author ChenJunQi.
 * 
 */
public class BaseFragment extends SherlockFragment implements
		OnBackStackListener {

	public BaseFragment() {
		super();
	}

	protected void showToast(int textResId) {
		if (!isDetached() && getActivity() != null) {
			Toast.makeText(getActivity(), getString(textResId),
					Toast.LENGTH_SHORT).show();
		}
	}

	protected void showToast(String text) {
		if (!isDetached() && getActivity() != null) {
			Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void onBackStackEvent() {
		initActionBar();
	}

	protected void initActionBar() {
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		Log.i("BaseFragment$onConfigurationChanged");
	}

}

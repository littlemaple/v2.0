package com.medzone.cloud.ui.fragment.setting;
///**
// * 
// */
//package com.medzone.cloud.ui.fragment;
//
//import android.os.Bundle;
//import android.view.Gravity;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.TextView;
//
//import com.actionbarsherlock.app.ActionBar;
//import com.actionbarsherlock.app.ActionBar.LayoutParams;
//import com.medzone.framework.fragment.BaseFragment;
//import com.medzone.mcloud.R;
//import com.medzone.mcloud.R.layout;
//
///**
// * @author ChenJunQi.
// * 
// */
//public class SettingModifiedFragment extends BaseFragment {
//
//	@Override
//	public void onResume() {
//		super.onResume();
//		initActionBar();
//	}
//
//	private void initActionBar() {
//		ActionBar actionBar = getSherlockActivity().getSupportActionBar();
//		ActionBar.LayoutParams params = new ActionBar.LayoutParams(
//				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT,
//				Gravity.CENTER);
//		View view = LayoutInflater.from(getSherlockActivity()).inflate(
//				R.layout.custom_actionbar_with_text, null);
//		TextView title = (TextView) view.findViewById(R.id.actionbar_title);
//		title.setText("XXXX");
//		actionBar.setCustomView(view, params);
//		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
//		actionBar.setDisplayShowCustomEnabled(true);
//	}
//
//	@Override
//	public View onCreateView(LayoutInflater inflater, ViewGroup container,
//			Bundle savedInstanceState) {
//
//		View view = inflater.inflate(layout.fragment_setting_modified,
//				container, false);
//		return view;
//	}
//
//}

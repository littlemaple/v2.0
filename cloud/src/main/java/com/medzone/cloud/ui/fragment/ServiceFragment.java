/**
 * 
 */
package com.medzone.cloud.ui.fragment;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar.LayoutParams;
import com.medzone.cloud.CloudApplication;
import com.medzone.cloud.ui.adapter.ServiceSubscribeAdapter;
import com.medzone.framework.data.bean.imp.Subscribe;
import com.medzone.framework.fragment.BaseFragment;
import com.medzone.mcloud.R;
import com.medzone.mcloud.R.layout;

/**
 * @author ChenJunQi.
 * 
 */
public class ServiceFragment extends BaseFragment {

	private List<Subscribe> subscribeList;
	private GridView gridService;

	private View actionBarView;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	}

	@Override
	protected void initActionBar() {
		int heightPx = CloudApplication.actionBarHeight;
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, heightPx, Gravity.CENTER);
		actionBarView = LayoutInflater.from(getActivity()).inflate(
				R.layout.custom_actionbar_with_image, null);
		actionBarView.setLayoutParams(params);
		TextView title = (TextView) actionBarView
				.findViewById(R.id.actionbar_title);
		title.setText(R.string.actionbar_title_service);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		initActionBar();
		View view = inflater.inflate(layout.fragment_service, container, false);
		LinearLayout llActionBar = (LinearLayout) view
				.findViewById(R.id.actionbar);
		llActionBar.addView(actionBarView);
		gridService = (GridView) view.findViewById(R.id.gride_subscribe);

		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		subscribeList = getData();
		ServiceSubscribeAdapter adapter = new ServiceSubscribeAdapter(
				getActivity());
		adapter.setContent(subscribeList);
		gridService.setAdapter(adapter);
	}

	public List<Subscribe> getData() {
		List<Subscribe> list = new ArrayList<Subscribe>();
		Subscribe subs = new Subscribe();
		subs.setName(getResources().getString(R.string.clock_remind));
		subs.setdIcon(getResources().getDrawable(
				R.drawable.serviceview_ic_alarms));
		subs.setType(Subscribe.CLOCK_TYPE);
		list.add(subs);
		// subs = new Subscribe();
		// subs.setName(getResources().getString(R.string.cloud_health));
		// subs.setdIcon(getResources().getDrawable(
		// R.drawable.serviceview_ic_xinyunhealth));
		// subs.setType(Subscribe.HEALTH_TYPE);
		// list.add(subs);
		// subs = new Subscribe();
		// subs.setName(getResources().getString(R.string.cloud_store));
		// subs.setdIcon(getResources().getDrawable(
		// R.drawable.serviceview_ic_xinyunmall));
		// subs.setType(Subscribe.STORE_TYPE);
		// list.add(subs);
		return list;
	}

}

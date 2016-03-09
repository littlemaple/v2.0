package com.medzone.cloud.ui.fragment.temperature;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.LayoutParams;
import com.medzone.cloud.CloudApplication;
import com.medzone.cloud.Constants;
import com.medzone.cloud.ui.MeasureActivity;
import com.medzone.cloud.ui.widget.ErrorDialog;
import com.medzone.cloud.ui.widget.ErrorDialog.ErrorDialogListener;
import com.medzone.cloud.util.WakeLockUtil;
import com.medzone.framework.data.bean.imp.GroupMember;
import com.medzone.framework.data.bean.imp.Transmit;
import com.medzone.framework.fragment.BaseFragment;
import com.medzone.framework.util.AudioUtils;
import com.medzone.mcloud.R;
import com.medzone.mcloud.R.drawable;
import com.medzone.mcloud.R.id;
import com.medzone.mcloud.R.string;

public class AudioMeasureFragment extends BaseFragment implements
		OnClickListener {

	private View view;
	private MeasureActivity mActivity;
	private String temperature;
	private int measureTime = 0;
	private boolean isError = false;
	private boolean disConnection = false;
	private boolean isPause = false;
	private ImageView upwardIV;
	private Dialog dialog;

	@Override
	public void onPause() {
		isPause = true;
		super.onPause();
	}

	@Override
	public void onResume() {
		isPause = false;
		super.onResume();
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mActivity = (MeasureActivity) activity;
		mActivity.setMeasureView(true);
	}

	// 广播接收器
	private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			Transmit bean = new Transmit();
			bean = (Transmit) intent.getSerializableExtra(AudioUtils.DATA);
			String action = intent.getAction();
			if (AudioUtils.ACTION_GET_DATA.equals(action)) {
				temperature = bean.getMsg();
			} else if (AudioUtils.ACTION_COMMON_ERROR.equals(action)) {
				if (!isError) {
					showPopupWindow(getString(string.measure_abnormal),
							bean.getMsg());
				}
			} else if (AudioUtils.HEADEST_NOT_INSERT.equals(action)) {
				disConnection = true;
				if (!isError) {
					showPopupWindow(getString(string.audio_connection_error),
							getString(string.audio_not_insert));
				}
			}
		}
	};

	public void onStart() {
		WakeLockUtil.acquireWakeLock();
		if (!TextUtils.isEmpty(temperature) && isPause) {
			toResultFragment();
		} else if (!isPause) {
			// 注册BoradcasrReceiver
			IntentFilter intentFilter = new IntentFilter();
			intentFilter.addAction(AudioUtils.HEADEST_NOT_INSERT);
			intentFilter.addAction(AudioUtils.ACTION_GET_DATA);
			intentFilter.addAction(AudioUtils.ACTION_COMMON_ERROR);
			mActivity.registerReceiver(broadcastReceiver, intentFilter);
			sendAudioMeasure();
		}
		super.onStart();
	}

	private void sendAudioMeasure() {
		Intent intent = new Intent(AudioUtils.ACTION_AUDIO_CONNECT_DETECTION);
		mActivity.sendBroadcast(intent);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_temperature_measure,
				container, false);
		initActionBar();
		upwardIV = (ImageView) view.findViewById(id.audio_measure_upward_iv);
		Scale(upwardIV);
		handler.postDelayed(runnable, 5000);// 每3秒执行一次runnable.
		return view;
	}

	private void Scale(View view) {
		Animation myAnimation_Scale = AnimationUtils.loadAnimation(mActivity,
				R.anim.temperature_scale_action);
		view.startAnimation(myAnimation_Scale);
	}

	@Override
	protected void initActionBar() {
		ActionBar actionBar = getSherlockActivity().getSupportActionBar();
		ActionBar.LayoutParams params = new ActionBar.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT,
				Gravity.CENTER);
		View view = LayoutInflater.from(getSherlockActivity()).inflate(
				R.layout.custom_actionbar_with_image, null);
		TextView title = (TextView) view.findViewById(R.id.actionbar_title);
		GroupMember groupmember = mActivity.getGroupmember();
		if (groupmember.getRemark() != null) {
			title.setText(groupmember.getRemark());
		} else {
			title.setText(groupmember.getNickname());
		}
		ImageButton leftButton = (ImageButton) view
				.findViewById(id.actionbar_left);
		leftButton.setImageResource(drawable.personalinformationview_ic_cancel);
		leftButton.setOnClickListener(this);
		actionBar.setCustomView(view, params);
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		actionBar.setDisplayShowCustomEnabled(true);
	}

	@Override
	public void onDestroyView() {
		WakeLockUtil.releaseWakeLock();
		mActivity.unregisterReceiver(broadcastReceiver);
		handler.removeCallbacks(runnable);
		if (dialog != null) {
			dialog.dismiss();
		}
		super.onDestroyView();
	}

	Handler handler = new Handler();
	Runnable runnable = new Runnable() {
		@Override
		public void run() {
			if (measureTime < 1) {
				measureTime++;
				if (TextUtils.isEmpty(temperature)) {
					if (!isError) {
						showPopupWindow(
								CloudApplication.getInstance()
										.getApplicationContext()
										.getString(string.measure_abnormal),
								CloudApplication.getInstance()
										.getApplicationContext()
										.getString(string.measure_time_out));
					}
				} else {
					toResultFragment();
				}
			}
		}
	};

	private void toResultFragment() {
		EarTemperatureResultFragment fragment = new EarTemperatureResultFragment();
		Bundle bundle = new Bundle();
		bundle.putString(Constants.TEMPERATURE, temperature);
		fragment.setArguments(bundle);
		FragmentManager fm = getFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		ft.remove(this);
		ft.add(id.measure_container, fragment);
		ft.disallowAddToBackStack();
		ft.commitAllowingStateLoss();
	}

	// 初始化弹出信息
	private void initPopupWindow(String title, String content) {
		if (dialog == null) {
			ErrorDialogListener listener = new ErrorDialogListener() {
				@Override
				public void restart() {
					if (disConnection) {
						mActivity
								.comeBackEarTemperatureConnect(AudioMeasureFragment.this);
					} else {
						disConnection = false;
						dialog.dismiss();
						measureTime = 0;
						isError = false;
						Scale(upwardIV);
						handler.postDelayed(runnable, 5000);// 每3秒执行一次runnable.
						sendAudioMeasure();
					}
				}

				@Override
				public void exit() {
					dialog.dismiss();
					mActivity.finish();
				}
			};
			dialog = new ErrorDialog(mActivity, ErrorDialog.TYPE, listener,
					title, content, getString(string.action_restart),
					getString(string.action_exitmeasure)).dialogFactory();
		}
	}

	private void showPopupWindow(String title, String content) {
		isError = true;
		handler.removeCallbacks(runnable);
		if (mActivity.isFinishing())
			return;

		if (dialog == null) {
			initPopupWindow(title, content);
		}
		dialog.show();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case id.actionbar_left:
			mActivity.comeBackEarTemperatureConnect(this);
			break;
		default:
			break;
		}
	}

}

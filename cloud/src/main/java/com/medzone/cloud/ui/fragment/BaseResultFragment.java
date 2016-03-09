package com.medzone.cloud.ui.fragment;

import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.medzone.common.media.bean.Media;
import com.medzone.common.media.broad.Controller;
import com.medzone.common.media.inf.IOnServiceConnectComplete;
import com.medzone.common.media.player.PlayMode;
import com.medzone.common.media.player.PlayState;
import com.medzone.common.media.service.ServiceManager;

public abstract class BaseResultFragment extends BluetoothFragment implements
		IOnServiceConnectComplete {
	protected ServiceManager serviceManager;
	private List<Media> voiceList;
	private boolean isNeedPlay = false;
	private boolean isPlayed = false;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		initConnectService();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		return super.onCreateView(inflater, container, savedInstanceState);
	}

	protected void initConnectService() {
		serviceManager = new ServiceManager(getActivity());
		Controller.service = serviceManager;
		serviceManager.setOnServiceConnectComplete(this);
		serviceManager.connectService();
	}

	@Override
	public void onDestroy() {
		if (serviceManager != null) {
			serviceManager.stop();
			serviceManager.exit();
			serviceManager.disconnectService();
			serviceManager = null;
		}
		super.onDestroy();

	}

	private boolean hasFile() {
		if (serviceManager == null)
			return false;
		if (serviceManager.getPlayState() == PlayState.NOFILE) {
			return false;
		}
		return true;
	}

	protected synchronized void playVoice() {
		isNeedPlay = true;
		if (hasFile()) {
			serviceManager.start();
			isPlayed = true;
		}
	}

	@Override
	public void OnServiceConnectComplete() {
		serviceManager.setPlayMode(PlayMode.ORDER_PLAY);
		if (isNeedPlay && !isPlayed) {
			playVoice();
		}
	}

	protected void setVoiceMaterial(List<Media> voiceList) {
		this.voiceList = voiceList;
		// FIXME 偶见这里Crash空指针
		try {
			if (serviceManager == null) {
				initConnectService();
			}
			serviceManager.setList(voiceList);
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
	}

	protected List<Media> getVoiceMaterial() {
		return voiceList;
	}

	protected abstract List<Media> initVoiceMaterial();

}

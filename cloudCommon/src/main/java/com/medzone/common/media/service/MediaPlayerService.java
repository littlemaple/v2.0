package com.medzone.common.media.service;

import java.util.List;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;

import com.medzone.common.media.aidl.ServiceConnect;
import com.medzone.common.media.aidl.ServiceConnect.Stub;
import com.medzone.common.media.bean.Media;
import com.medzone.common.media.player.MusicPlayer;

public class MediaPlayerService extends Service {
	private MusicPlayer player;

	@Override
	public IBinder onBind(Intent intent) {
		return iBinder;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		player = new MusicPlayer(this);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return 0;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	private ServiceConnect.Stub iBinder = new Stub() {

		@Override
		public boolean stop() throws RemoteException {
			return player.stop();
		}

		@Override
		public boolean start() throws RemoteException {
			return player.start();
		}

		@Override
		public void setPlayMode(int mode) throws RemoteException {
			player.setPlayMode(mode);
		}

		@Override
		public void setList(List<Media> list) throws RemoteException {
			player.setList(list);
		}

		@Override
		public boolean seekTo(int position) throws RemoteException {
			return player.seekTo(position);
		}

		@Override
		public boolean prepare(int index) throws RemoteException {
			return player.prepare(index);
		}

		@Override
		public boolean playPre() throws RemoteException {
			return player.playPre();
		}

		@Override
		public boolean playNext() throws RemoteException {
			return player.playNext();
		}

		@Override
		public boolean play(int index) throws RemoteException {
			return player.play(index);
		}

		@Override
		public boolean pause() throws RemoteException {
			return player.pause();
		}

		@Override
		public int getcurPosition() throws RemoteException {
			return player.getcurPosition();
		}

		@Override
		public int getPlayState() throws RemoteException {
			return player.getPlayState();
		}

		@Override
		public int getPlayMode() throws RemoteException {
			return player.getPlayMode();
		}

		@Override
		public List<Media> getList() throws RemoteException {
			return player.getList();
		}

		@Override
		public int getDuration() throws RemoteException {
			return player.getDuration();
		}

		@Override
		public void exit() throws RemoteException {
			player.exit();
		}

		@Override
		public int getCurPlayIndex() throws RemoteException {
			return player.getCurPlayIndex();
		}
	};
}

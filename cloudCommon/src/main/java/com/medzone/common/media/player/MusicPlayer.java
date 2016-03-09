package com.medzone.common.media.player;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;

import com.medzone.common.media.bean.Media;

public class MusicPlayer implements OnCompletionListener, OnErrorListener {

	private MediaPlayer mediaPlayer;
	private List<Media> mediaList;
	private final String BROAD_NAME = "android.intent.action.AUDIO_SERVICE_BROADCAST";
	private int curPlayIndex;
	private int playState;
	private int playMode;
	private Context context;
	private Random random;

	public MusicPlayer(Context context) {
		this.context = context;
		mediaPlayer = new MediaPlayer();
		mediaPlayer.setOnCompletionListener(this);
		mediaPlayer.setOnErrorListener(this);
		mediaList = new ArrayList<Media>();
		curPlayIndex = -1;
		playState = PlayState.NOFILE;
		playMode = PlayMode.LIST_LOOP_PLAY;
		random = new Random();
		random.setSeed(System.currentTimeMillis());
	}

	public void exit() {
		mediaPlayer.reset();
		mediaList.clear();
		curPlayIndex = -1;
		playState = PlayState.NOFILE;
	}

	public void setList(List<Media> list) {

		if (list == null) {
			playState = PlayState.NOFILE;
			mediaList.clear();
			curPlayIndex = -1;
			return;
		}
		mediaList = list;
		if (mediaList.size() == 0) {
			playState = PlayState.NOFILE;
			mediaList.clear();
			curPlayIndex = -1;
			return;
		}
		switch (playState) {
		case PlayState.NOFILE:
			prepare(0);
			break;
		case PlayState.INVALID:
			prepare(0);
			break;
		case PlayState.PREPARE:
			prepare(0);
			break;
		case PlayState.PLAYING:
			break;
		case PlayState.PAUSE:
			break;
		default:
			break;
		}
	}

	public void setPlayMode(int mode) {
		switch (mode) {
		case PlayMode.SINGLE_LOOP_PLAY:
		case PlayMode.ORDER_PLAY:
		case PlayMode.LIST_LOOP_PLAY:
		case PlayMode.RANDOM_PLAY:
			playMode = mode;
			break;
		}
	}

	public boolean prepare(int index) {
		curPlayIndex = index;
		mediaPlayer.reset();
		int resid = mediaList.get(index).getRawId();
		try {
			AssetFileDescriptor afd = context.getResources().openRawResourceFd(
					resid);
			if (afd == null)
				return false;
			mediaPlayer.setDataSource(afd.getFileDescriptor(),
					afd.getStartOffset(), afd.getLength());
			mediaPlayer.prepare();
			playState = PlayState.PREPARE;
		} catch (IOException e) {
			e.printStackTrace();
			playState = PlayState.INVALID;
			return false;
		}
		return true;
	}

	public boolean start() {
		if (playState == PlayState.NOFILE || playState == PlayState.INVALID) {
			return false;
		}
		mediaPlayer.start();
		playState = PlayState.PLAYING;
		return true;
	}

	public boolean play(int index) {
		if (playState == PlayState.NOFILE) {
			return false;
		}
		if (curPlayIndex == index) {
			if (mediaPlayer.isPlaying() == false) {
				mediaPlayer.start();
				playState = PlayState.PLAYING;
			}
			return true;
		}
		curPlayIndex = index;
		if (prepare(curPlayIndex)) {
			return start();
		}
		return false;
	}

	public boolean seekTo(int position) {
		if (playState == PlayState.NOFILE || playState == PlayState.INVALID) {
			return false;
		}
		int r = resetSeekValue(position);
		int time = mediaPlayer.getDuration();
		int curTime = (int) ((float) r / 100 * time);
		mediaPlayer.seekTo(curTime);
		playState = PlayState.PLAYING;
		return true;
	}

	public boolean pause() {
		if (playState != PlayState.PLAYING) {
			return false;
		}
		mediaPlayer.pause();
		return true;
	}

	public boolean playPre() {
		if (playState == PlayState.NOFILE) {
			return false;
		}
		curPlayIndex--;
		curPlayIndex = resetIndex(curPlayIndex);
		if (prepare(curPlayIndex)) {
			return start();
		}
		return false;
	}

	public boolean playNext() {
		if (playState == PlayState.NOFILE) {
			return false;
		}
		curPlayIndex++;
		curPlayIndex = resetIndex(curPlayIndex);
		if (prepare(curPlayIndex)) {
			return start();
		}
		return false;
	}

	public boolean stop() {
		if (playState == PlayState.PLAYING || playState == PlayState.PAUSE) {
			return prepare(curPlayIndex);
		}
		return false;
	}

	public int getcurPosition() {
		if (playState == PlayState.PLAYING || playState == PlayState.PAUSE) {
			return mediaPlayer.getCurrentPosition();
		}
		return 0;
	}

	public int getDuration() {
		if (playState == PlayState.NOFILE || playState == PlayState.INVALID) {
			return 0;
		}
		return mediaPlayer.getDuration();
	}

	public List<Media> getList() {
		return mediaList;
	}

	public int getCurPlayIndex() {
		return curPlayIndex;
	}

	public int getPlayState() {
		return playState;
	}

	public int getPlayMode() {
		return playMode;
	}

	@Override
	public boolean onError(MediaPlayer mp, int what, int extra) {
		return true;
	}

	@Override
	public void onCompletion(MediaPlayer mp) {
		switch (playMode) {
		case PlayMode.SINGLE_LOOP_PLAY:
			play(curPlayIndex);
			break;
		case PlayMode.ORDER_PLAY:
			if (curPlayIndex != mediaList.size() - 1) {
				playNext();
			} else {
				prepare(0);
			}
			break;
		case PlayMode.LIST_LOOP_PLAY:
			playNext();
			break;
		case PlayMode.RANDOM_PLAY:
			int index = getRandomIndex();
			curPlayIndex = resetIndex(index);
			if (prepare(curPlayIndex)) {
				start();
			}
			break;
		default:
			prepare(curPlayIndex);
			break;
		}
		sendMessageToBroad();
	}

	private int resetIndex(int index) {
		if (index < 0) {
			index = mediaList.size() - 1;
		}
		if (index >= mediaList.size()) {
			index = 0;
		}
		return index;
	}

	private int resetSeekValue(int value) {
		if (value < 0) {
			value = 0;
		}

		if (value > 100) {
			value = 100;
		}

		return value;
	}

	private int getRandomIndex() {
		int size = mediaList.size();
		if (size == 0) {
			return -1;
		}
		return Math.abs(random.nextInt() % size);
	}

	public void sendMessageToBroad() {
		Intent intent = new Intent(BROAD_NAME);
		context.sendBroadcast(intent);
	}
}

package com.medzone.cloud.guide;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class GuidePage {

	private final int DEFAULT_VERSION = 0;
	private final int DEFAULT_SHOW_COUNTS = 1;
	private final String VERSION = "version";
	private final String SHOW_COUNTS = "show_counts";
	private final String PAGE_NAME;

	private int version = 1;
	private int localVersion = DEFAULT_VERSION;
	private int showCounts = DEFAULT_SHOW_COUNTS;
	private List<Integer> list = new ArrayList<Integer>();
	private SharedPreferences preferences;

	public GuidePage(String pageName) {
		this.PAGE_NAME = pageName;
	}

	public GuidePage(String pageName, int version) {
		this.PAGE_NAME = pageName;
		this.version = version;
	}

	public void init(Context context) {
		preferences = getSharedPreferences(context);
		localVersion = preferences.getInt(VERSION, DEFAULT_VERSION);
		showCounts = preferences.getInt(SHOW_COUNTS, DEFAULT_SHOW_COUNTS);

		if (version > localVersion) {
			showCounts = DEFAULT_SHOW_COUNTS;
		}
	}

	public boolean canShow() {
		return version > localVersion || showCounts >= 1;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public int getShowCounts() {
		return showCounts;
	}

	public void setShowCounts(int showCounts) {
		this.showCounts = showCounts;
	}

	public void setOnShow(Context context) {
		showCounts = --showCounts > 0 ? showCounts : 0;
		localVersion = version;
		savePageInfoPreferences(context);
	}

	public List<Integer> getList() {
		return list;
	}

	public void setList(List<Integer> list) {
		this.list = list;
	}

	public void addDrawableRes(int res) {
		list.add(res);
	}

	public int size() {
		return list.size();
	}

	public int getDrawableRes(int location) {
		if (location >= size()) {
			return 0;
		}
		return list.get(location);
	}

	public void reset() {
		list.clear();
	}

	public String getName() {
		return PAGE_NAME;
	}

	public SharedPreferences getSharedPreferences(Context context) {
		return context.getSharedPreferences(PAGE_NAME, Context.MODE_PRIVATE);
	}

	private void savePageInfoPreferences(Context context) {
		Editor editor = getSharedPreferences(context).edit();
		editor.putInt(VERSION, localVersion);
		editor.putInt(SHOW_COUNTS, showCounts);
		editor.commit();
	}

}

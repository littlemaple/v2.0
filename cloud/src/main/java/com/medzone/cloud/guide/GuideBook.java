package com.medzone.cloud.guide;

import java.util.HashMap;
import java.util.Iterator;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.medzone.cloud.CloudApplicationPreference;
import com.medzone.cloud.ui.WelcomeActivity;
import com.medzone.mcloud.R;

public class GuideBook implements OnClickListener {

	public static final String PREFACE_PAGE = "preface_page";
	public static final int PREFACE_VERSION = 1;

	public static final int BPM_RESULT_PAGE_VERSION = 1;

	public static final String BPM_RESULT_PAGE = "bpm_result_page";

	private static GuideBook instance = new GuideBook();
	private HashMap<String, GuidePage> hashMap;
	private GuidePage currentPage;
	private ViewGroup contentView;
	private View pageView;
	private ImageView imageView;
	private int showPageCount = 0;
	private boolean isShowPreface = false;
	private boolean isPrefaceShowing = false;

	private static boolean isInit = false;

	public GuideBook() {
		hashMap = new HashMap<String, GuidePage>();

		GuidePage bpmResultPage = new GuidePage(BPM_RESULT_PAGE,
				BPM_RESULT_PAGE_VERSION);
		bpmResultPage.addDrawableRes(R.drawable.ic_launcher);

		hashMap.put(BPM_RESULT_PAGE, bpmResultPage);
	}

	public static GuideBook getInstance() {
		return instance;
	}

	public void setContentView(View view) {
		if (showPageCount == 0) {
			return;
		}
		if (view instanceof ViewGroup) {
			inflaterPageLayout(view);
		}
	}

	private void inflaterPageLayout(View view) {
		contentView = (ViewGroup) view;
		pageView = LayoutInflater.from(contentView.getContext()).inflate(
				R.layout.guide_book, null);
		imageView = (ImageView) pageView.findViewById(R.id.page_image);
		imageView.setOnClickListener(this);
		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		contentView.addView(pageView, lp);
	}

	public void showPreface(Activity activity) {
		if (isShowPreface) {
			Intent intent = new Intent(activity, WelcomeActivity.class);
			activity.startActivity(intent);
			activity.overridePendingTransition(0, 0); // no animation .
		}
	}

	public void showPage(String pageName) {
		if (TextUtils.isEmpty(pageName)) {
			return;
		}
		if (showPageCount == 0) {
			return;
		}
		if (contentView == null) {
			return;
		}
		if (isPrefaceShowing()) {
			return;
		}
		GuidePage page = getPage(pageName);
		if (page != null && page.canShow()) {
			showPage(page);
		}
	}

	public static void init(Context context) {
		if (!isInit) {
			instance.setShowPreface(CloudApplicationPreference.isShowPreface());
			instance.initPage(context);
			isInit = true;
		}
	}

	private void initPage(Context context) {
		Iterator<String> it = hashMap.keySet().iterator();
		while (it.hasNext()) {
			GuidePage page = hashMap.get(it.next());
			page.init(context);
			if (page.canShow()) {
				showPageCount++;
			}
		}
	}

	// The sequential coupling,must first be initialized
	public boolean isGuidePagesCanShow() {
		if (showPageCount == 0)
			return false;
		return true;
	}

	public GuidePage getPage(String key) {
		return hashMap.get(key);
	}

	private void showPage(GuidePage page) {
		initPageView(page);
		pageView.setVisibility(View.VISIBLE);
	}

	private void initPageView(GuidePage page) {
		currentPage = page;
		imageView.setImageResource(page.getDrawableRes(0));
	}

	private void setShowPreface(boolean isShowPreface) {
		this.isShowPreface = isShowPreface;
	}

	public boolean isShowPreface() {
		return isShowPreface;
	}

	public boolean isPrefaceShowing() {
		return isPrefaceShowing;
	}

	public void setPrefaceShowing(boolean isShowing) {
		this.isPrefaceShowing = isShowing;
		if (!isShowing) {
			setShowPreface(false);
			CloudApplicationPreference
					.saveGuideViewVersionCode(GuideBook.PREFACE_VERSION);
		}
	}

	@Override
	public void onClick(View v) {
		showPageCount--;
		if (showPageCount == 0) {
			if (contentView != null) {
				contentView.removeView(pageView);
			}
		} else {
			if (pageView != null) {
				pageView.setVisibility(View.GONE);
			}
		}
		if (currentPage != null) {
			currentPage.setOnShow(v.getContext());
		}
	}

}

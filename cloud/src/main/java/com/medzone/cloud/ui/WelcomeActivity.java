package com.medzone.cloud.ui;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.medzone.cloud.data.PropertyCenter;
import com.medzone.cloud.guide.GuideBook;
import com.medzone.cloud.ui.adapter.ViewPagerAdapter;
import com.medzone.mcloud.R;

public class WelcomeActivity extends BaseActivity {
	private final String SEARCH_TAG = "intro_intro";
	private final String DEF_TYPE = "drawable";

	private RelativeLayout guideView;
	private ViewPager viewPager;
	private ViewPagerAdapter adapter;

	// private LinearLayout imageViewContainer;
	// private List<ImageView> imageViewList;

	@Override
	protected void preInitUI() {
		guideView = new RelativeLayout(this);
		guideView.setLayoutParams(getFillParentLayoutParams());
	}

	@Override
	protected void initUI() {
		setContentView(guideView);
		addViewPagerView();
		// addImageViewContainerView();
	}

	@Override
	protected void postInitUI() {
		GuideBook.getInstance().setPrefaceShowing(true);
	}

	private void addViewPagerView() {
		viewPager = new ViewPager(this);
		viewPager.setLayoutParams(getFillParentLayoutParams());
		adapter = new ViewPagerAdapter(getPagerViewList());
		viewPager.setAdapter(adapter);
		viewPager.setCurrentItem(0);
		// viewPager.setOnPageChangeListener(new OnPageChangeListener() {
		//
		// @Override
		// public void onPageSelected(int position) {
		// setDotImageViewBackground(imageViewList, position);
		// }
		//
		// @Override
		// public void onPageScrolled(int arg0, float arg1, int arg2) {
		//
		// }
		//
		// @Override
		// public void onPageScrollStateChanged(int arg0) {
		//
		// }
		// });
		guideView.addView(viewPager);
	}

	// private void addImageViewContainerView() {
	// imageViewList = initImageViewList(adapter.getCount());
	//
	// imageViewContainer = new LinearLayout(this);
	// imageViewContainer.setOrientation(LinearLayout.HORIZONTAL);
	// for (int i = 0; i < adapter.getCount(); i++) {
	// imageViewContainer.addView(imageViewList.get(i),
	// getImageViewLayoutParams());
	// }
	// guideView.addView(imageViewContainer,
	// getImageViewContainerLayoutParams());
	//
	// }

	private LayoutParams getFillParentLayoutParams() {
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.MATCH_PARENT);
		return params;
	}

	// private LayoutParams getImageViewContainerLayoutParams() {
	// RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
	// ViewGroup.LayoutParams.WRAP_CONTENT,
	// ViewGroup.LayoutParams.WRAP_CONTENT);
	// params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
	// params.addRule(RelativeLayout.CENTER_HORIZONTAL);
	// params.setMargins(0, 0, 0, 10);
	// return params;
	// }

	// private LayoutParams getImageViewLayoutParams() {
	// LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
	// ViewGroup.LayoutParams.WRAP_CONTENT,
	// ViewGroup.LayoutParams.WRAP_CONTENT);
	// params.setMargins(10, 0, 10, 0);
	// return params;
	// }

	private List<Integer> getResourceIdList() {
		List<Integer> resourceArray = new ArrayList<Integer>();
		Field[] fields = R.drawable.class.getDeclaredFields();
		for (Field field : fields) {
			String name = field.getName();
			if (isVaildDrawableRes(name)) {
				int id = this.getResources().getIdentifier(name, DEF_TYPE,
						getPackageName());
				resourceArray.add(id);
			}
		}
		return resourceArray;
	}

	// check whether is guidebook picture resource.
	private boolean isVaildDrawableRes(String name) {
		int minLength = SEARCH_TAG.length();
		if (name.length() >= minLength
				&& name.substring(0, minLength).equalsIgnoreCase(SEARCH_TAG)) {
			return true;
		}
		return false;
	}

	private List<View> getPagerViewList() {
		List<View> viewList = new ArrayList<View>();
		List<Integer> resIdList = getResourceIdList();

		for (int i = 0; i < resIdList.size(); i++) {
			RelativeLayout view = new RelativeLayout(this);
			view.setLayoutParams(getFillParentLayoutParams());
			ImageView imageView = new ImageView(this);
			imageView.setLayoutParams(getFillParentLayoutParams());
			imageView.setBackgroundResource(resIdList.get(i));

			view.addView(imageView);

			// The last page increase began to experience button.
			if (i == resIdList.size() - 1) {
				view.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						GuideBook.getInstance().setPrefaceShowing(false);
						PropertyCenter.getInstance().firePropertyChange(
								PropertyCenter.PROPERTY_POP_UPDATE, null, null);
						finish();
					}
				});
			}
			viewList.add(view);
		}
		return viewList;
	}

	// private List<ImageView> initImageViewList(int count) {
	// List<ImageView> imageViewList = new ArrayList<ImageView>();
	// for (int i = 0; i < count; i++) {
	// ImageView imageView = new ImageView(this);
	// if (i == 0) {
	// imageView
	// .setBackgroundResource(R.drawable.emotion_page_indicator_focused);
	// } else {
	// imageView
	// .setBackgroundResource(R.drawable.emotion_page_indicator);
	// }
	// imageViewList.add(imageView);
	// }
	// return imageViewList;
	// }

	// private void setDotImageViewBackground(List<ImageView> imageViewList,
	// int position) {
	// for (int i = 0; i < imageViewList.size(); i++) {
	// if (i == position) {
	// imageViewList.get(i).setBackgroundResource(
	// R.drawable.emotion_page_indicator_focused);
	// } else {
	// imageViewList.get(i).setBackgroundResource(
	// R.drawable.emotion_page_indicator);
	// }
	// }
	// }

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		System.gc();
	}
}

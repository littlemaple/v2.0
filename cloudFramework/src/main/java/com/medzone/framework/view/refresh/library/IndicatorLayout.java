package com.medzone.framework.view.refresh.library;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.medzone.framework.R;
import com.medzone.framework.view.refresh.PullToRefreshBase;

@SuppressLint("ViewConstructor")
public class IndicatorLayout extends FrameLayout implements AnimationListener {

	static final int DEFAULT_ROTATION_ANIMATION_DURATION = 200;

	private Animation mInAnim, mOutAnim;
	private ImageView mArrowImageView;
	private TextView mAlertTextView;
	private String pullToRefresh;
	private String releaseToRefresh;

	private final Animation mRotateAnimation, mResetRotateAnimation;

	public IndicatorLayout(Context context, PullToRefreshBase.Mode mode) {
		super(context);
		pullToRefresh = context.getString(R.string.refresh_down_text);
		releaseToRefresh = context.getString(R.string.refresh_release_text);
		mArrowImageView = new ImageView(context);
		mAlertTextView = new TextView(context);
		mAlertTextView.setTextColor(Color.GRAY);
		mAlertTextView.setTextSize(16.0f);
		mAlertTextView.setText(pullToRefresh);
		LinearLayout ll = new LinearLayout(context);
		LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);
		ll.setLayoutParams(lp1);
		ll.setOrientation(LinearLayout.HORIZONTAL);
		ll.setGravity(Gravity.CENTER);
		lp1.leftMargin = 5;
		lp1.rightMargin = 5;
		ll.addView(mArrowImageView, lp1);
		ll.addView(mAlertTextView, lp1);
		FrameLayout.LayoutParams lp2 = new FrameLayout.LayoutParams(
				FrameLayout.LayoutParams.MATCH_PARENT,
				FrameLayout.LayoutParams.WRAP_CONTENT, Gravity.CENTER);
		addView(ll, lp2);

		int inAnimResId, outAnimResId;
		switch (mode) {
		case PULL_UP_TO_REFRESH:
			inAnimResId = R.anim.slide_in_from_bottom;
			outAnimResId = R.anim.slide_out_to_bottom;
			mArrowImageView.setImageResource(R.drawable.arrow_down);
			mArrowImageView.setScaleType(ScaleType.MATRIX);
			Drawable d = context.getResources().getDrawable(
					R.drawable.arrow_down);
			int px = d.getIntrinsicWidth();
			int py = d.getIntrinsicHeight();
			Matrix m = new Matrix();
			m.setRotate(180, px / 2f, py / 2f);
			mArrowImageView.setImageMatrix(m);
			break;
		default:
		case PULL_DOWN_TO_REFRESH:
			inAnimResId = R.anim.slide_in_from_top;
			outAnimResId = R.anim.slide_out_to_top;
			mArrowImageView.setImageResource(R.drawable.arrow_down);
			break;
		}

		mInAnim = AnimationUtils.loadAnimation(context, inAnimResId);
		mInAnim.setAnimationListener(this);

		mOutAnim = AnimationUtils.loadAnimation(context, outAnimResId);
		mOutAnim.setAnimationListener(this);

		final Interpolator interpolator = new LinearInterpolator();
		mRotateAnimation = new RotateAnimation(0, -180,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		mRotateAnimation.setInterpolator(interpolator);
		mRotateAnimation.setDuration(DEFAULT_ROTATION_ANIMATION_DURATION);
		mRotateAnimation.setFillAfter(true);

		mResetRotateAnimation = new RotateAnimation(-180, 0,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		mResetRotateAnimation.setInterpolator(interpolator);
		mResetRotateAnimation.setDuration(DEFAULT_ROTATION_ANIMATION_DURATION);
		mResetRotateAnimation.setFillAfter(true);

	}

	public final boolean isVisible() {
		Animation currentAnim = getAnimation();
		if (null != currentAnim) {
			return mInAnim == currentAnim;
		}

		return getVisibility() == View.VISIBLE;
	}

	public void hide() {
		startAnimation(mOutAnim);
	}

	public void show() {
		startAnimation(mInAnim);
	}

	@Override
	public void onAnimationEnd(Animation animation) {
		clearAnimation();
		setVisibility(View.VISIBLE);
	}

	@Override
	public void onAnimationRepeat(Animation animation) {

	}

	@Override
	public void onAnimationStart(Animation animation) {

	}

	public void releaseToRefresh() {
		mArrowImageView.startAnimation(mRotateAnimation);
		mAlertTextView.setText(releaseToRefresh);
	}

	public void pullToRefresh() {
		mArrowImageView.startAnimation(mResetRotateAnimation);
		mAlertTextView.setText(pullToRefresh);
	}

	public void refreshing() {
		mArrowImageView.clearAnimation();
		setVisibility(View.INVISIBLE);
	}

	public void showIndicator() {
		setVisibility(View.VISIBLE);
		mAlertTextView.setText(pullToRefresh);
	}

	public void reset() {
		setVisibility(View.INVISIBLE);
		mArrowImageView.clearAnimation();
	}

}

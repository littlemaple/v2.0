/*******************************************************************************
 * Copyright 2011, 2012 Chris Banes.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.medzone.framework.view.refresh.library;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;

import com.medzone.framework.R;
import com.medzone.framework.view.refresh.PullToRefreshBase.Mode;

@SuppressLint("ViewConstructor")
public class LoadingLayout extends FrameLayout {

	static final int DEFAULT_ROTATION_ANIMATION_DURATION = 600;

	private final ImageView mHeaderImage;
	private final Matrix mHeaderImageMatrix;

	private IndicatorLayout indicatorLayout;

	private float mRotationPivotX, mRotationPivotY;

	private final Animation mRotateAnimation;

	private TextView mHeaderText;
	private TextView mSubHeaderText;

	private String mRefreshingLabel;
	private String mLastUpdateTimeLable;

	public LoadingLayout(Context context, final Mode mode, TypedArray attrs) {
		super(context);
		ViewGroup header = (ViewGroup) LayoutInflater.from(context).inflate(
				R.layout.pull_to_refresh_header, this);

		mHeaderImage = (ImageView) header
				.findViewById(R.id.pull_to_refresh_image);

		mHeaderText = (TextView) header.findViewById(R.id.pull_to_refresh_text);
		mRefreshingLabel = context.getString(R.string.refresh_refreshing_text);
		mHeaderText.setText(mRefreshingLabel);

		mSubHeaderText = (TextView) header
				.findViewById(R.id.sub_pull_to_refresh_text);
		mSubHeaderText.setText(mLastUpdateTimeLable);

		indicatorLayout = new IndicatorLayout(context, mode);
		FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
				FrameLayout.LayoutParams.MATCH_PARENT,
				FrameLayout.LayoutParams.WRAP_CONTENT, Gravity.CENTER);
		header.addView(indicatorLayout, lp);

		mHeaderImage.setScaleType(ScaleType.MATRIX);
		mHeaderImageMatrix = new Matrix();
		mHeaderImage.setImageMatrix(mHeaderImageMatrix);

		final Interpolator interpolator = new LinearInterpolator();
		mRotateAnimation = new RotateAnimation(0, 360,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		mRotateAnimation.setInterpolator(interpolator);
		mRotateAnimation.setDuration(DEFAULT_ROTATION_ANIMATION_DURATION);
		mRotateAnimation.setRepeatCount(Animation.INFINITE);
		mRotateAnimation.setRepeatMode(Animation.RESTART);

		if (attrs.hasValue(R.styleable.PullToRefresh_ptrHeaderBackground)) {
			Drawable background = attrs
					.getDrawable(R.styleable.PullToRefresh_ptrHeaderBackground);
			if (null != background) {
				setBackgroundDrawable(background);
			}
		}

		// Try and get defined drawable from Attrs
		Drawable imageDrawable = null;
		if (attrs.hasValue(R.styleable.PullToRefresh_ptrDrawable)) {
			imageDrawable = attrs
					.getDrawable(R.styleable.PullToRefresh_ptrDrawable);
		}

		// If we don't have a user defined drawable, load the default
		if (null == imageDrawable) {
			imageDrawable = context.getResources().getDrawable(
					R.drawable.refresh_loading);
		}

		// Set Drawable, and save width/height
		setLoadingDrawable(imageDrawable);

		reset();
	}

	public void reset() {
		setVisibility(View.VISIBLE);
		mHeaderImage.setVisibility(View.GONE);
		mHeaderText.setVisibility(View.GONE);
		mSubHeaderText.setVisibility(View.GONE);
		mHeaderImage.clearAnimation();
		indicatorLayout.reset();
		resetImageRotation();
	}

	public void releaseToRefresh() {
		indicatorLayout.releaseToRefresh();
	}

	public void setPullLabel(String pullLabel) {

	}

	public void refreshing() {
		indicatorLayout.refreshing();
		mHeaderImage.setVisibility(View.VISIBLE);
		mHeaderText.setVisibility(View.VISIBLE);
		mSubHeaderText.setVisibility(View.VISIBLE);
		if (mLastUpdateTimeLable == null || mLastUpdateTimeLable.length() == 0) {
			mSubHeaderText.setVisibility(View.GONE);
		} else {
			mSubHeaderText.setVisibility(View.VISIBLE);
		}
		mSubHeaderText.setText(mLastUpdateTimeLable);
		mHeaderImage.startAnimation(mRotateAnimation);
	}

	public void setSubHeaderText(CharSequence label) {
		mLastUpdateTimeLable = String.valueOf(label);
	}

	public void setRefreshingLabel(String refreshingLabel) {
		mRefreshingLabel = refreshingLabel;
	}

	public void setReleaseLabel(String releaseLabel) {

	}

	public void pullToRefresh() {
		indicatorLayout.pullToRefresh();
	}

	public void setTextColor(ColorStateList color) {
		mHeaderText.setTextColor(color);
		mSubHeaderText.setTextColor(color);
	}

	public void setSubTextColor(ColorStateList color) {
		mSubHeaderText.setTextColor(color);
	}

	public void setTextColor(int color) {
		setTextColor(ColorStateList.valueOf(color));
	}

	public void setLoadingDrawable(Drawable imageDrawable) {
		// Set Drawable, and save width/height
		mHeaderImage.setImageDrawable(imageDrawable);
		mRotationPivotX = imageDrawable.getIntrinsicWidth() / 2f;
		mRotationPivotY = imageDrawable.getIntrinsicHeight() / 2f;
	}

	public void setSubTextColor(int color) {
		setSubTextColor(ColorStateList.valueOf(color));
	}

	public void onPullY(float scaleOfHeight) {
		mHeaderImageMatrix.setRotate(scaleOfHeight * 90, mRotationPivotX,
				mRotationPivotY);
		mHeaderImage.setImageMatrix(mHeaderImageMatrix);
	}

	private void resetImageRotation() {
		mHeaderImageMatrix.reset();
		mHeaderImage.setImageMatrix(mHeaderImageMatrix);
	}

	public void showIndicator() {
		if (!indicatorLayout.isVisible()) {
			indicatorLayout.showIndicator();
		}
	}

	public IndicatorLayout getIndicatorLayout() {
		return indicatorLayout;
	}
}

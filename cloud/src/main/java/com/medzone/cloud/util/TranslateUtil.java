package com.medzone.cloud.util;

import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;

public class TranslateUtil {
	public TranslateUtil(ImageView imageView, float endX, float endY) {
		AnimationSet animationSet = new AnimationSet(true);
		TranslateAnimation translateAnimation = new TranslateAnimation(
				Animation.RELATIVE_TO_SELF, 0f, // X轴的开始位置
				Animation.RELATIVE_TO_SELF, endX, // X轴的结束位置
				Animation.RELATIVE_TO_SELF, 0f, // Y轴的开始位置
				Animation.RELATIVE_TO_SELF, endY); // Y轴的结束位置
		translateAnimation.setDuration(1600);
		animationSet.addAnimation(translateAnimation);
		/*
		 * 第一行的设置如果为true，则动画执行完之后效果定格在执行完之后的状态;
		 * 第二行的设置如果为false，则动画执行完之后效果定格在执行完之后的状态 ;
		 * 第三行设置的是一个long类型的值，是指动画延迟多少毫秒之后执行.
		 */
		animationSet.setFillAfter(true);
		animationSet.setFillBefore(false);
		animationSet.setStartOffset(0);
		imageView.startAnimation(animationSet);
	}
}

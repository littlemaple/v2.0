package com.medzone.cloud.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

public class CustomLinearLayout extends LinearLayout {

	
	private OnResizeListener rListener;
	public void addOnCustomChangeListener(OnResizeListener rListener) {
		this.rListener = rListener;
	}

	public interface OnResizeListener{
		
		void OnResize(int w,int h,int oldw,int oldh);
	}
	
	public CustomLinearLayout(Context context) {
		super(context);
	}

	public CustomLinearLayout(Context context,AttributeSet attr) {
		super(context,attr);
	}
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		if(rListener!=null){
			rListener.OnResize(w, h, oldw, oldh);
		}
	}

}

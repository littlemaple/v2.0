/**
 * 
 */
package com.medzone.cloud.ui.adapter.viewholder;

import android.view.View;

/**
 * @author ChenJunQi.
 * 
 */
public abstract class BaseViewHolder {

	public BaseViewHolder(View rootView) {
		init(rootView);
	}

	public void recycle() {

	}

	public void fillFromItem(Object item) {

	}

	// 这里主要是对同一个ListView或ExpandableListView中有不同的布局
	public void fillFromItem(Object item, Object type) {

	}

	public abstract void init(View view);
}
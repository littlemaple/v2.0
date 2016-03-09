/**
 * 
 */
package com.medzone.cloud.ui.adapter.viewholder;

import android.view.View;
import android.widget.AbsListView.RecyclerListener;

/**
 * @author ChenJunQi.
 * 
 */
public class ListViewRecyclerListener implements RecyclerListener {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.widget.AbsListView.RecyclerListener#onMovedToScrapHeap(android
	 * .view.View)
	 */
	public void onMovedToScrapHeap(View view) {
		if (view != null) {
			BaseViewHolder viewHolder = (BaseViewHolder) view.getTag();
			if (viewHolder != null) {
				viewHolder.recycle();
			}
		}

	}

}
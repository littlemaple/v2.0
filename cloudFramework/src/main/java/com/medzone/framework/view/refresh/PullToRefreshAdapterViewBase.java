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
package com.medzone.framework.view.refresh;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.medzone.framework.view.refresh.library.EmptyViewMethodAccessor;

public abstract class PullToRefreshAdapterViewBase<T extends AbsListView>
		extends PullToRefreshBase<T> implements OnScrollListener {

	static final boolean DEFAULT_SHOW_INDICATOR = true;
	private OnScrollListener mOnScrollListener;
	private OnLastItemVisibleListener mOnLastItemVisibleListener;
	private View mEmptyView;
	private FrameLayout mRefreshableViewHolder;
	private int lastTotalItemCount = 0;

	public PullToRefreshAdapterViewBase(Context context) {
		super(context);
		mRefreshableView.setOnScrollListener(this);
	}

	public PullToRefreshAdapterViewBase(Context context, AttributeSet attrs) {
		super(context, attrs);
		mRefreshableView.setOnScrollListener(this);
	}

	public PullToRefreshAdapterViewBase(Context context, Mode mode) {
		super(context, mode);
		mRefreshableView.setOnScrollListener(this);
	}

	abstract public ContextMenuInfo getContextMenuInfo();

	public final void onScroll(final AbsListView view,
			final int firstVisibleItem, final int visibleItemCount,
			final int totalItemCount) {

		if (DEBUG) {
			Log.d(LOG_TAG, "First Visible: " + firstVisibleItem
					+ ". Visible Count: " + visibleItemCount
					+ ". Total Items: " + totalItemCount);
		}

		// If we have a OnItemVisibleListener, do check...
		if (null != mOnLastItemVisibleListener) {
			if (visibleItemCount > 0 && totalItemCount > visibleItemCount) {
				if (isLastItemVisible()) {
					mOnLastItemVisibleListener.onLastItemVisible();
				}
			}
		}

		// Open the bottom of the display to deal with!
		if (visibleItemCount > 0 && totalItemCount > visibleItemCount) {
			if (isLastItemVisible() && !isRefreshing()) {
				onScrollToBottomRefresh();
			}
		}
		if (mMode.canPullUp() && mCurrentMode == Mode.PULL_UP_TO_REFRESH
				&& totalItemCount == visibleItemCount) {
			// 如果获取不到，说明没有数据了，不再去�?现在没有排除获取失败的情�?
			if (lastTotalItemCount < totalItemCount) {
				lastTotalItemCount = totalItemCount;
				if (mOnRefreshListener2 != null && !isRefreshing()) {
					mOnRefreshListener2.onPullUpToRefresh();
				}
			}

		}

		// Finally call OnScrollListener if we have one
		if (null != mOnScrollListener) {
			mOnScrollListener.onScroll(view, firstVisibleItem,
					visibleItemCount, totalItemCount);
		}
	}

	public final void onScrollStateChanged(final AbsListView view,
			final int scrollState) {
		if (null != mOnScrollListener) {
			mOnScrollListener.onScrollStateChanged(view, scrollState);
		}
	}

	/**
	 * Sets the Empty View to be used by the Adapter View.
	 * 
	 * We need it handle it ourselves so that we can Pull-to-Refresh when the
	 * Empty View is shown.
	 * 
	 * Please note, you do <strong>not</strong> usually need to call this method
	 * yourself. Calling setEmptyView on the AdapterView will automatically call
	 * this method and set everything up. This includes when the Android
	 * Framework automatically sets the Empty View based on it's ID.
	 * 
	 * @param newEmptyView
	 *            - Empty View to be used
	 */
	public final void setEmptyView(View newEmptyView) {
		// If we already have an Empty View, remove it
		if (null != mEmptyView) {
			mRefreshableViewHolder.removeView(mEmptyView);
		}

		if (null != newEmptyView) {
			// New view needs to be clickable so that Android recognizes it as a
			// target for Touch Events
			newEmptyView.setClickable(true);

			ViewParent newEmptyViewParent = newEmptyView.getParent();
			if (null != newEmptyViewParent
					&& newEmptyViewParent instanceof ViewGroup) {
				((ViewGroup) newEmptyViewParent).removeView(newEmptyView);
			}

			mRefreshableViewHolder.addView(newEmptyView,
					ViewGroup.LayoutParams.MATCH_PARENT,
					ViewGroup.LayoutParams.MATCH_PARENT);

			if (mRefreshableView instanceof EmptyViewMethodAccessor) {
				((EmptyViewMethodAccessor) mRefreshableView)
						.setEmptyViewInternal(newEmptyView);
			} else {
				mRefreshableView.setEmptyView(newEmptyView);
			}
		}
	}

	public final void setOnLastItemVisibleListener(
			OnLastItemVisibleListener listener) {
		mOnLastItemVisibleListener = listener;
	}

	public final void setOnScrollListener(OnScrollListener listener) {
		mOnScrollListener = listener;
	};

	protected void addRefreshableView(Context context, T refreshableView) {
		mRefreshableViewHolder = new FrameLayout(context);
		mRefreshableViewHolder.addView(refreshableView,
				ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.MATCH_PARENT);
		addView(mRefreshableViewHolder, new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, 0, 1.0f));
	}

	/**
	 * Returns the number of Adapter View Footer Views. This will always return
	 * 0 for non-ListView views.
	 * 
	 * @return 0 for non-ListView views, possibly 1 for ListView
	 */
	protected int getNumberInternalFooterViews() {
		return 0;
	}

	/**
	 * Returns the number of Adapter View Header Views. This will always return
	 * 0 for non-ListView views.
	 * 
	 * @return 0 for non-ListView views, possibly 1 for ListView
	 */
	protected int getNumberInternalHeaderViews() {
		return 0;
	}

	protected int getNumberInternalViews() {
		return getNumberInternalHeaderViews() + getNumberInternalFooterViews();
	}

	protected boolean isReadyForPullDown() {
		return isFirstItemVisible();
	}

	protected boolean isReadyForPullUp() {
		return isLastItemVisible();
	}

	private boolean isFirstItemVisible() {
		if (mRefreshableView.getCount() <= getNumberInternalViews()) {
			return true;
		} else if (mRefreshableView.getFirstVisiblePosition() == 0) {

			final View firstVisibleChild = mRefreshableView.getChildAt(0);

			if (firstVisibleChild != null) {
				return firstVisibleChild.getTop() >= mRefreshableView.getTop();
			}
		}

		return false;
	}

	private boolean isLastItemVisible() {
		final int count = mRefreshableView.getCount();
		final int lastVisiblePosition = mRefreshableView
				.getLastVisiblePosition();

		if (DEBUG) {
			Log.d(LOG_TAG, "isLastItemVisible. Count: " + count
					+ " Last Visible Pos: " + lastVisiblePosition);
		}

		if (count <= getNumberInternalViews()) {
			return true;
		} else if (lastVisiblePosition == count - 1) {

			final int childIndex = lastVisiblePosition
					- mRefreshableView.getFirstVisiblePosition();
			final View lastVisibleChild = mRefreshableView
					.getChildAt(childIndex);

			if (lastVisibleChild != null) {
				return lastVisibleChild.getBottom() <= mRefreshableView
						.getBottom();
			}
		}

		return false;
	}
}

/**
 * 
 */
package com.medzone.framework.view.refresh;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Scroller;
import android.widget.TextView;

import com.medzone.framework.R;

/**
 * @author ChenJunQi.
 * 
 */
public class RefreshableView extends LinearLayout {

	private static final String TAG = RefreshableView.class.getSimpleName();
	private Scroller scroller;
	private View refreshView;
	private ImageView refreshIndicatorView;
	private int refreshTargetTop = -60;
	private ProgressBar bar;
	private TextView downTextView;
	private TextView timeTextView;
	private LinearLayout refereshLinearLayout;

	private RefreshListener refreshListener;
	private String downTextString;
	private String releaseTextString;

	static final int PULL_TO_REFRESH = 0x0;
	static final int RELEASE_TO_REFRESH = 0x1;
	static final int REFRESHING = 0x2;
	private int mState = PULL_TO_REFRESH;
	private Animation mRotateAnimation, mResetRotateAnimation;
	private final int DEFAULT_ROTATION_ANIMATION_DURATION = 250;

	/**
	 * The last access to the network time
	 */
	private Date lastTime;

	private Long refreshTime = null;
	private int lastX;
	private int lastY;
	// 拉动标记
	private boolean isDragging = false;
	// 是否可刷新标记
	private boolean isRefreshEnabled = true;
	// 在刷新中标记
	private boolean isRefreshing = false;

	private Context mContext;

	public RefreshableView(Context context) {
		super(context);
		mContext = context;
		init();
		if (isInEditMode()) {
			return;
		}
	}

	public RefreshableView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		init();
		if (isInEditMode()) {
			return;
		}
	}

	private void init() {

		// 滑动对象，
		scroller = new Scroller(mContext);

		// 刷新视图顶端的的view
		refreshView = LayoutInflater.from(mContext).inflate(
				R.layout.refresh_top_item, null);

		/**
		 * 拉动时候的视图
		 */
		// 指示器view
		refreshIndicatorView = (ImageView) refreshView
				.findViewById(R.id.indicator);
		refreshIndicatorView.setImageResource(R.drawable.arrow_down);
		// 下拉显示text
		downTextView = (TextView) refreshView.findViewById(R.id.refresh_hint);

		downTextView.setText(R.string.refresh_down_text);

		/**
		 * 刷新时候的视图
		 */
		// 根LinearLayout
		refereshLinearLayout = (LinearLayout) refreshView
				.findViewById(R.id.referesh_linearlayout);
		// 刷新bar
		bar = (ProgressBar) refreshView.findViewById(R.id.progress);
		// 下来显示时间
		timeTextView = (TextView) refreshView.findViewById(R.id.refresh_time);

		LayoutParams lp = new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, -refreshTargetTop);
		lp.topMargin = refreshTargetTop;
		lp.gravity = Gravity.CENTER;
		addView(refreshView, lp);
		downTextString = mContext.getResources().getString(
				R.string.refresh_down_text);
		releaseTextString = mContext.getResources().getString(
				R.string.refresh_release_text);

		final Interpolator interpolator = new LinearInterpolator();
		mRotateAnimation = new RotateAnimation(0, 180,
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

	/**
	 * 刷新
	 * 
	 * @param time
	 */
	public void setRefreshDate(Date date) {
		lastTime = date;
		SimpleDateFormat format = new SimpleDateFormat("MM-dd HH:mm",
				Locale.getDefault());
		timeTextView.setText(String.format(
				mContext.getResources().getString(R.string.last_update_time),
				format.format(date)));
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (isRefreshing) {
			return false;
		}

		int y = (int) event.getRawY();

		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			// 记录下y坐标
			lastY = y;
			break;

		case MotionEvent.ACTION_MOVE:
			Log.i(TAG, "ACTION_MOVE");
			// y移动坐标
			int m = y - lastY;
			if (((m < 6) && (m > -1)) || (!isDragging)) {
				doMovement(m);
			}
			// 记录下此刻y坐标
			this.lastY = y;
			break;

		case MotionEvent.ACTION_UP:
			Log.i(TAG, "ACTION_UP");

			fling();

			break;
		}
		return true;
	}

	/**
	 * up事件处理
	 */
	private void fling() {
		// TODO Auto-generated method stub
		if (isRefreshing) {
			return;
		}
		LinearLayout.LayoutParams lp = (LayoutParams) refreshView
				.getLayoutParams();
		Log.i(TAG, "fling()" + lp.topMargin);
		if (lp.topMargin > 0) {// 拉到了触发可刷新事件
			startrefresh();
		} else {
			returnInitState();
		}
	}

	synchronized private void returnInitState() {
		// TODO Auto-generated method stub
		LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) this.refreshView
				.getLayoutParams();
		int i = lp.topMargin;
		scroller.startScroll(0, i, 0, refreshTargetTop - Math.abs(i));
		invalidate();
		resetHeader();
	}

	private void resetHeader() {
		mState = PULL_TO_REFRESH;
		refreshIndicatorView.clearAnimation();
	}

	synchronized private void startrefresh() {
		if (refreshListener != null) {
			refreshListener.onRefresh(this);
		} else {
			returnInitState();
		}
	}

	private void moveByStateChanged(boolean isRefresh) {
		if (isRefresh) {
			moveByStartRefresh();
		} else {
			returnInitState();
			setRefreshDate(Calendar.getInstance().getTime());
		}
	}

	// 刷新开始时的移动
	private void moveByStartRefresh() {
		LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) this.refreshView
				.getLayoutParams();
		int i = lp.topMargin;
		refereshLinearLayout.setVisibility(View.VISIBLE);
		refreshIndicatorView.setVisibility(View.GONE);
		downTextView.setVisibility(View.GONE);
		scroller.startScroll(0, i, 0, 0 - i);
		invalidate();
	}

	/** 
     *  
     */
	@Override
	public void computeScroll() {
		// TODO Auto-generated method stub
		if (scroller.computeScrollOffset()) {
			int i = this.scroller.getCurrY();
			LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) this.refreshView
					.getLayoutParams();
			int k = Math.max(i, refreshTargetTop);
			lp.topMargin = k;
			this.refreshView.setLayoutParams(lp);
			this.refreshView.invalidate();
			invalidate();
		}
	}

	/**
	 * 下拉move事件处理
	 * 
	 * @param moveY
	 */
	private void doMovement(int moveY) {
		// TODO Auto-generated method stub
		LinearLayout.LayoutParams lp = (LayoutParams) refreshView
				.getLayoutParams();
		// if(moveY > 0){
		// 获取view的上边距
		float f1 = lp.topMargin;
		float f2 = moveY * 0.3F;
		int i = (int) (f1 + f2);
		// 修改上边距
		lp.topMargin = i;
		// 修改后刷新
		refreshView.setLayoutParams(lp);
		refreshView.invalidate();
		invalidate();
		// }
		if (refreshTime != null) {
			setRefreshTime(refreshTime);
		}
		downTextView.setVisibility(View.VISIBLE);
		refreshIndicatorView.setVisibility(View.VISIBLE);

		refereshLinearLayout.setVisibility(View.GONE);
		// timeTextView.setVisibility(View.GONE);
		// bar.setVisibility(View.GONE);
		if (lp.topMargin > 0) {
			if (mState == PULL_TO_REFRESH) {
				downTextView.setText(R.string.refresh_release_text);
				// refreshIndicatorView.setImageResource(R.drawable.refresh_arrow_up);
				refreshIndicatorView.startAnimation(mRotateAnimation);
				mState = RELEASE_TO_REFRESH;
			}
		} else {
			if (mState == RELEASE_TO_REFRESH) {
				downTextView.setText(R.string.refresh_down_text);
				// refreshIndicatorView
				// .setImageResource(R.drawable.refresh_arrow_down);
				refreshIndicatorView.startAnimation(mResetRotateAnimation);
				mState = PULL_TO_REFRESH;
			}
		}

	}

	public void setRefreshEnabled(boolean b) {
		this.isRefreshEnabled = b;
	}

	public void setRefreshListener(RefreshListener listener) {
		this.refreshListener = listener;
	}

	/**
	 * 刷新时间
	 * 
	 * @param refreshTime2
	 */
	private void setRefreshTime(Long time) {
		// TODO Auto-generated method stub

	}

	/*
	 * 该方法一般和ontouchEvent 一起用 (non-Javadoc)
	 * 
	 * @see
	 * Android.view.ViewGroup#onInterceptTouchEvent(android.view.MotionEvent)
	 */
	@Override
	public boolean onInterceptTouchEvent(MotionEvent e) {
		// TODO Auto-generated method stub
		int action = e.getAction();
		int y = (int) e.getRawY();
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			lastY = y;
			break;

		case MotionEvent.ACTION_MOVE:
			// y移动坐标
			int m = y - lastY;

			// 记录下此刻y坐标
			this.lastY = y;
			if (m > 6 && canScroll()) {
				return true;
			}
			break;
		case MotionEvent.ACTION_UP:

			break;

		case MotionEvent.ACTION_CANCEL:

			break;
		}
		return false;
	}

	private boolean canScroll() {
		// TODO Auto-generated method stub
		View childView;
		if (getChildCount() > 1) {
			childView = this.getChildAt(1);
			if (childView instanceof GridView) {
				GridView view = (GridView) childView;
				if (view.getChildAt(0) == null) {
					return true;
				}
				int top = view.getChildAt(0).getTop();
				int pad = view.getListPaddingTop();
				if ((Math.abs(top - pad)) < 3
						&& view.getFirstVisiblePosition() == 0) {
					return true;
				} else {
					return false;
				}
			} else if (childView instanceof ListView) {
				ListView view = (ListView) childView;
				if (view.getChildAt(0) == null) {
					return true;
				}
				int top = view.getChildAt(0).getTop();
				int pad = view.getListPaddingTop();
				if ((Math.abs(top - pad)) < 3
						&& view.getFirstVisiblePosition() == 0) {
					return true;
				} else {
					return false;
				}
			} else if (childView instanceof ScrollView) {
				if (((ScrollView) childView).getScrollY() == 0) {
					return true;
				} else {
					return false;
				}
			}

		}
		return false;
	}

	public boolean getRefereshState() {
		return isRefreshing;
	}

	public void setRefreshState(Boolean isRefreshing) {
		if (this.isRefreshing != isRefreshing) {
			this.isRefreshing = isRefreshing;
		}
		moveByStateChanged(isRefreshing);
	}

	/**
	 * 刷新监听接口
	 * 
	 * @author Nono
	 * 
	 */
	public interface RefreshListener {
		public void onRefresh(RefreshableView view);
	}
}

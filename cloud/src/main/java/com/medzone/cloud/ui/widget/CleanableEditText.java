package com.medzone.cloud.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.CycleInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.EditText;

import com.medzone.cloud.data.helper.AccountHelper;
import com.medzone.framework.Log;
import com.medzone.mcloud.R;

/**
 * 
 * 在焦点变化时和输入内容发生变化时均要判断是否显示右边clean图标
 * 
 */
public class CleanableEditText extends EditText {
	private Drawable mRightDrawable;
	private boolean isHasFocus;
	private int maxBytes = Integer.MAX_VALUE;
	private int maxLinesContent = 0;
	private boolean clickRight = false;
	private TextWatcherImplCompat mTextWatcherImplCompat;

	public CleanableEditText(Context context) {
		super(context);
		// TODO Auto-generated method stub
	}

	public CleanableEditText(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated method stub
	}

	public CleanableEditText(Context context, AttributeSet attrs) {
		super(context, attrs);

		// this(context, attrs, com.android.internal.R.attr.editTextStyle);

		TypedArray typeArr = context.obtainStyledAttributes(attrs,
				R.styleable.CleanAbleAttr);
		maxBytes = typeArr.getInt(R.styleable.CleanAbleAttr_maxbytes,
				Integer.MAX_VALUE);
		maxLinesContent = typeArr.getInt(
				R.styleable.CleanAbleAttr_maxlinescontent, 0);
		clickRight = typeArr.getBoolean(R.styleable.CleanAbleAttr_clickright,
				false);
		typeArr.recycle();

		Drawable[] drawables = this.getCompoundDrawables();
		mRightDrawable = drawables[2];
		this.setOnFocusChangeListener(new FocusChangeListenerImpl());
		this.addTextChangedListener(new TextWatcherImpl());
		setClearDrawableVisible(false);

	}

	public void setMaxBytes(int maxBytes) {
		this.maxBytes = maxBytes;
	}

	public void setTextWatcherImplCompat(
			TextWatcherImplCompat mTextWatcherImplCompat) {
		this.mTextWatcherImplCompat = mTextWatcherImplCompat;
	}

	/**
	 * 当手指抬起的位置在clean的图标的区域 我们将此视为进行清除操作 getWidth():得到控件的宽度
	 * event.getX():抬起时的坐标(改坐标是相对于控件本身而言的)
	 * getTotalPaddingRight():clean的图标左边缘至控件右边缘的距离
	 * getPaddingRight():clean的图标右边缘至控件右边缘的距离 于是: getWidth() -
	 * getTotalPaddingRight()表示: 控件左边到clean的图标左边缘的区域 getWidth() -
	 * getPaddingRight()表示: 控件左边到clean的图标右边缘的区域 所以这两者之间的区域刚好是clean的图标的区域
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_UP:

			boolean isClean = (event.getX() > (getWidth() - getTotalPaddingRight()))
					&& (event.getX() < (getWidth() - getPaddingRight()));
			if (isClean) {
				if (clickRight) {
					if (mTextWatcherImplCompat != null) {
						mTextWatcherImplCompat.onClickRightDrawable();
					}
				} else {
					setText("");
				}
			}
			break;

		default:
			break;
		}
		return super.onTouchEvent(event);
	}

	private class FocusChangeListenerImpl implements OnFocusChangeListener {
		@Override
		public void onFocusChange(View v, boolean hasFocus) {
			isHasFocus = hasFocus;
			if (isHasFocus) {
				boolean isVisible = getText().toString().length() >= 1;
				setClearDrawableVisible(isVisible);
			} else {
				setClearDrawableVisible(false);
			}
		}
	}

	public interface TextWatcherImplCompat {

		public void onTextChanged(CharSequence s, int start, int before,
				int count);

		public void beforeTextChanged(CharSequence s, int start, int count,
				int after);

		public void afterTextChanged(Editable s);

		/**
		 * 点击右边的icon后触发的点击事件,配合clickright属性一起使用
		 */
		public void onClickRightDrawable();

	}

	private class TextWatcherImpl implements TextWatcher {
		@Override
		public void afterTextChanged(Editable s) {
			boolean isVisible = getText().toString().length() >= 1;
			setClearDrawableVisible(isVisible);
			if (mTextWatcherImplCompat != null) {
				mTextWatcherImplCompat.afterTextChanged(s);
			}
			// 输入内容限制，多余定义的行数就不再输入
			if (maxLinesContent != 0) {
				int lines = CleanableEditText.this.getLineCount();
				if (lines > maxLinesContent) {
					String str = s.toString();
					int cursorStart = CleanableEditText.this
							.getSelectionStart();
					int cursorEnd = CleanableEditText.this.getSelectionEnd();
					if (cursorStart == cursorEnd && cursorStart < str.length()
							&& cursorStart >= 1) {
						str = str.substring(0, cursorStart - 1)
								+ str.substring(cursorStart);
					} else {
						str = str.substring(0, s.length() - 1);
					}
					// setText会触发afterTextChanged的递归
					CleanableEditText.this.setText(str);
					// setSelection用的索引不能使用str.length()否则会越界
					CleanableEditText.this.setSelection(CleanableEditText.this
							.getText().length());
				}
			}
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			if (mTextWatcherImplCompat != null) {
				mTextWatcherImplCompat
						.beforeTextChanged(s, start, count, after);
			}
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {

			String content = getText().toString();
			int curContentLen = AccountHelper.getContentBytesLength(content);
			if (curContentLen > maxBytes) {

				int selEndIndex = Selection.getSelectionEnd(getEditableText());

				String newContent = AccountHelper.subStr(content, maxBytes);

				setText(newContent);

				int newLen = getEditableText().length();
				if (selEndIndex > newLen) {
					selEndIndex = getEditableText().length();
				}
				Selection.setSelection(getEditableText(), selEndIndex);
			}
			if (mTextWatcherImplCompat != null) {
				mTextWatcherImplCompat.onTextChanged(s, start, before, count);
			}
		}
	}

	// 隐藏或者显示右边clean的图标
	protected void setClearDrawableVisible(boolean isVisible) {
		if (!clickRight) {
			Drawable rightDrawable;
			if (isVisible) {
				rightDrawable = mRightDrawable;
			} else {
				rightDrawable = null;
			}
			// 使用代码设置该控件left, top, right, and bottom处的图标
			setCompoundDrawables(getCompoundDrawables()[0],
					getCompoundDrawables()[1], rightDrawable,
					getCompoundDrawables()[3]);
		}
	}

	// 显示一个动画,以提示用户输入
	public void setShakeAnimation() {
		this.setAnimation(shakeAnimation(5));
	}

	// CycleTimes动画重复的次数
	public Animation shakeAnimation(int CycleTimes) {
		Animation translateAnimation = new TranslateAnimation(0, 10, 0, 10);
		translateAnimation.setInterpolator(new CycleInterpolator(CycleTimes));
		translateAnimation.setDuration(1000);
		return translateAnimation;
	}

}
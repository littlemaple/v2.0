package com.medzone.framework.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class LetterSeekBar extends View {

	/** 索引字母列表 */
	private String[] text = new String[26];

	/** 画笔样式 */
	private Paint paint_1;

	/** 字体间距 */
	private int TextRange;

	/** 监听接口 */
	private OnclikViewRightListener onclikViewRight;

	public LetterSeekBar(Context context, AttributeSet attrs) {
		super(context, attrs);

		Init();
	}

	public LetterSeekBar(Context context) {
		super(context);
		Init();
	}

	public void Init() {

		for (int i = 0; i < text.length; i++) {
			text[i] = ((char) ((int) 'A' + i)) + "";
		}

		paint_1 = new Paint();
		paint_1.setColor(getResources().getColor(android.R.color.black));
		paint_1.setTextSize(26);
		paint_1.setAntiAlias(true);

	}

	@Override
	public void draw(Canvas canvas) {
		super.draw(canvas);

		int screen_w = getWidth();
		int screen_h = getHeight();
		TextRange = screen_h / 26;

		for (int i = 0; i < text.length; i++) {
			int X = screen_w - getTextWidth(text[i]) >> 1;
			canvas.drawText(text[i], X, (i + 1) * TextRange, paint_1);
		}

	}

	/**
	 * 触屏监听
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {

		switch (event.getAction()) {

		case MotionEvent.ACTION_UP:

			setBackgroundColor(0xffffff);

			if (onclikViewRight != null) {
				onclikViewRight.seteventUP();
			}

			break;

		case MotionEvent.ACTION_DOWN:
		case MotionEvent.ACTION_MOVE:

			setBackgroundColor(0x55ffffff);

			float Y = event.getY(); // 得到当前触点

			int YY = (int) Y / TextRange; // 计算当前位置的字母

			if (YY >= text.length) {
				YY = text.length - 1;
			}
			if (YY <= 0)
				YY = 0;
			String str = text[YY];

			if (onclikViewRight != null) {
				onclikViewRight.seteventDownAndMove(str);
			}

			break;

		}

		return true;
	}

	/**
	 * 返回字体宽度
	 * 
	 * @param str
	 * @return
	 */
	public int getTextWidth(String str) {

		Rect rect = new Rect();
		paint_1.getTextBounds(str, 0, str.length(), rect);
		return rect.width();

	}

	/** 设置监听 */
	public void setOnclikViewRight(OnclikViewRightListener onclikViewRight) {
		this.onclikViewRight = onclikViewRight;
	}

	public interface OnclikViewRightListener {

		/** 松开触屏 */
		public void seteventUP();

		/** 按下或滑动触屏传回当前索引的字母 */
		public void seteventDownAndMove(String str);

	}

}

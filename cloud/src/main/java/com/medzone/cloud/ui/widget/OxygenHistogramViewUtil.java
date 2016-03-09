package com.medzone.cloud.ui.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff.Mode;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class OxygenHistogramViewUtil extends SurfaceView implements
		SurfaceHolder.Callback, Runnable {
	private final static int RESULT_NORMAL_COLOR = 0xffb5ddff;
	public final static int REPORT_SHARE_MESSAHE = 0xffd9eeff;
	public static Handler oxHistogramHandler; // 柱状图Handler

	private double width = 240;
	private double height = 432;

	private boolean is_running = false;
	Thread histogramThread = null;
	private SurfaceHolder theHolder = null;
	private int every_draw_data_cnt = 0;

	private float maxY;
	private double ratioX;
	private double ratioY;

	private float y_histogram = (float) 0;
	private float startX = (float) 0;
	private float startY = (float) 0;
	private float minY = (float) 0;

	private Paint paint = new Paint();

	public OxygenHistogramViewUtil(Context context, AttributeSet attrs,
			int twidth, int theight) {
		super(context, attrs);
		width = twidth;
		height = theight;

		ratioX = (double) (width / 720);
		ratioY = (double) (height / 1280);

		minY = (float) (10 * ratioY + 1);
		maxY = (float) (592 * ratioY);// default:592
		startY = (float) ((maxY - 70 * ratioY) / 4 * 4 + minY);

		paint.setAntiAlias(true);
		paint.setColor(RESULT_NORMAL_COLOR);
		paint.setStyle(Paint.Style.FILL);
		paint.setStrokeWidth((float) (70 * ratioX) + 1);

		theHolder = this.getHolder();
		theHolder.addCallback(this);
		theHolder.setFormat(PixelFormat.TRANSLUCENT);
		setZOrderOnTop(true);
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {

	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		is_running = true;
		histogramThread = new Thread(this);
		histogramThread.start();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		is_running = false;
	}

	@Override
	public void run() {
		Looper.prepare();
		oxHistogramHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				String str = (String) msg.obj;
				super.removeMessages(msg.what, msg.obj);
				int length = str.length();
				String[] wave_str = new String[length];
				int[] wave_int = new int[length];
				int index1 = 0;
				int index2 = 0;
				int i_wave_data = 0;
				int iii = 0;
				while (index1 < length) {
					index2 = str.indexOf(" ", index1);
					wave_str[i_wave_data] = str.substring(index1, index2);
					wave_int[i_wave_data] = Integer
							.valueOf(wave_str[i_wave_data]);
					index1 = index2 + 1;
					i_wave_data++;
				}

				while (is_running && (iii < i_wave_data)) {
					// 在这里加上线程安全锁
					if (every_draw_data_cnt == 2) {
						every_draw_data_cnt = 0;
						synchronized (theHolder) {
							Refresh();
						}
					}

					if (iii < i_wave_data) {
						y_histogram = ((float) 132.5 + (float) wave_int[iii]) / 66 + 35;
						iii++;
						every_draw_data_cnt++;
					}
				}
			}

		};
		Looper.loop();

	}

	private int canvasLeft;
	private int canvasTop;
	private int canvasRight;
	private int canvasBottom;
	private Rect backRect;

	private Paint paint2;

	private void Refresh() {

		// TODO 这里的矩阵参数确定
		if (backRect == null) {
			canvasLeft = 0;
			canvasTop = 0;
			canvasRight = (int) (40 * ratioX);
			canvasBottom = (int) (592 * ratioY);

			backRect = new Rect(canvasLeft, canvasTop, canvasRight,
					canvasBottom);
		}
		Canvas canvas = theHolder.lockCanvas(backRect);

		if (null == canvas) {
			return;
		}

		canvas.drawColor(REPORT_SHARE_MESSAHE, Mode.CLEAR);
		draw(canvas);
		theHolder.unlockCanvasAndPost(canvas);
	}

	@Override
	public void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		if (paint2 == null) {
			paint2 = new Paint();
			paint2.setAntiAlias(true);
			paint2.setColor(REPORT_SHARE_MESSAHE);
			paint2.setStyle(Paint.Style.FILL);
			paint2.setStrokeWidth((float) (70 * ratioX) + 1);
		}

		float stopX = startX;
		float stopY = (float) (minY - (y_histogram - 39) * (startY - minY) / 2);

		canvas.drawLine(startX, startY, stopX, 0, paint2);
		canvas.drawLine(startX, startY, stopX, stopY, paint);
	}

}

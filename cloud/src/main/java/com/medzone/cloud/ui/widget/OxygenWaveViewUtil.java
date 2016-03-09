package com.medzone.cloud.ui.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff.Mode;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/** 测量波形图 */
public class OxygenWaveViewUtil extends SurfaceView implements
		SurfaceHolder.Callback, Runnable {
	private final static int RESULT_NORMAL_COLOR = 0xffb5ddff;
	public final static int REPORT_SHARE_MESSAHE = 0xffd9eeff;
	public static Handler oxWaveHandler; // 波形测量Handler
	private static final String SEPARATOR = " ";// 分隔符
	// default size
	private double width = 240;
	private double height = 432;

	private boolean isRunning = false;
	private Thread waveThread;
	private SurfaceHolder mHolder;

	private float[] y1 = new float[] { 38 };
	private float[] y2 = new float[] { 38 };

	private int brushX = 0;

	private double ratioX;
	private double ratioY;
	private float maxY;
	private float y0Line1;
	private float y0Line4;

	private float x0Line7 = (float) 0.0;
	private float widthRatio;
	private int drawAreaWidth;

	private float heightRatio;
	private int packetsNumber = 0;
	private Paint paint = new Paint();

	public OxygenWaveViewUtil(Context context, AttributeSet attrs, int twidth,
			int theight) {
		super(context, attrs);

		width = twidth;
		height = theight;

		widthRatio = (float) (width / 400);
		drawAreaWidth = (int) (31 * width / 36 - 4 * widthRatio);
		heightRatio = (float) (height / 1280);

		initHolder();
		setZOrderOnTop(true);

	}

	private void initHolder() {
		mHolder = getHolder();
		mHolder.addCallback(this);
		mHolder.setFormat(PixelFormat.TRANSLUCENT);
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {

	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		isRunning = true;
		waveThread = new Thread(this);
		waveThread.start();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		isRunning = false;
	}

	@Override
	public void run() {
		Looper.prepare();
		oxWaveHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				String waveDataStr = (String) msg.obj;
				String[] measureDataStr = new String[waveDataStr.length()];
				int[] measureWaveData = new int[waveDataStr.length()];
				int startPosition = 0;
				int subDataPosition = 0;
				int curFlag = 0; // 当前下标
				int cursor = 0; // 数组下标

				// 拆分测量数据包
				while (startPosition < waveDataStr.length()) {
					subDataPosition = waveDataStr.indexOf(SEPARATOR,
							startPosition);
					measureDataStr[cursor] = waveDataStr.substring(
							startPosition, subDataPosition);
					measureWaveData[cursor] = Integer
							.valueOf(measureDataStr[cursor]);
					startPosition = subDataPosition + 1;
					cursor++;
				}

				while (isRunning && (curFlag < cursor)) {

					// 每2包画一组数据
					if (packetsNumber == 2) {
						packetsNumber = 0;
						synchronized (mHolder) {
							Refresh();
						}
					}

					y1 = new float[brushX + 1];
					y1[brushX] = ((float) 132.5 + (float) measureWaveData[curFlag]) / 66 + 35;

					if (brushX != 0) {
						System.arraycopy(y2, 0, y1, 0, brushX);
					}
					y2 = new float[brushX + 1];
					System.arraycopy(y1, 0, y2, 0, brushX + 1);

					curFlag++;
					brushX++;
					packetsNumber++;

					// 如果画图的横坐标 超过画布的宽度,则重新从下标0处开始画
					if ((brushX * widthRatio) >= drawAreaWidth) {
						brushX = 0;
					}

				}
			}
		};
		Looper.loop();
	}

	// 刷新视图
	public void Refresh() {
		Canvas canvas = mHolder.lockCanvas(new Rect(0, 0,
				(int) ((brushX + 4) * widthRatio), (int) (600 * heightRatio)));
		if (null == canvas) {
			return;
		}
		canvas.drawColor(REPORT_SHARE_MESSAHE, Mode.CLEAR);
		draw(canvas);
		mHolder.unlockCanvasAndPost(canvas);
	}

	@SuppressLint("DrawAllocation")
	@Override
	public void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		ratioX = width / 720;
		ratioY = height / 1280;

		maxY = (float) (592 * ratioY);
		y0Line1 = (float) (10 * ratioY + 1);
		y0Line4 = (float) ((maxY - 70 * ratioY) / 4 * 4 + y0Line1);

		paint.setAntiAlias(true);
		paint.setColor(RESULT_NORMAL_COLOR);
		paint.setStyle(Paint.Style.FILL);
		paint.setStrokeCap(Cap.ROUND);
		paint.setStrokeWidth((float) (10 * ratioX) + 1);
		paint.setDither(true);
		Path path = new Path();
		path.moveTo(x0Line7, y0Line4);
		path.lineTo((x0Line7), (float) (y0Line1 - (y1[0] - 39)
				* (y0Line4 - y0Line1) / 2));
		int ii = 1;
		for (; ii < y1.length; ii++) {
			path.lineTo((x0Line7 + ii * widthRatio),
					(float) (y0Line1 - (y1[ii] - 39) * (y0Line4 - y0Line1) / 2));
		}
		// 单独画最后一帧
		ii--;
		path.lineTo((x0Line7 + ii * widthRatio), y0Line4);
		path.lineTo((x0Line7), y0Line4);

		canvas.drawPath(path, paint);
	}

}

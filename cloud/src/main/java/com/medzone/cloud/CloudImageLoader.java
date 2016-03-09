/**
 * 
 */
package com.medzone.cloud;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;

import com.medzone.mcloud.R.drawable;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.process.BitmapProcessor;

/**
 * @author ChenJunQi. 2014年9月26日
 * 
 */
public final class CloudImageLoader {

	private static CloudImageLoader instance;

	// ============================
	// ImageLoader处理方法
	// ============================

	public static DisplayImageOptions normalDisplayImageOptions;
	public static DisplayImageOptions avatarDisplayImageOptions;
	private static BitmapProcessor avatarBitmapProcessor;

	public synchronized static CloudImageLoader getInstance() {
		if (instance == null) {
			instance = new CloudImageLoader();
			Context context = CloudApplication.getInstance()
					.getApplicationContext();
			instance.initImageLoader(context);
		}
		return instance;
	}

	public ImageLoader getImageLoader() {
		if (instance == null) {
			instance = getInstance();
		}
		return ImageLoader.getInstance();
	}

	private void initImageLoaderDisplayOptions() {
		initImageLoaderBitmapProcessor();
		avatarDisplayImageOptions = new DisplayImageOptions.Builder()
				.imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
				.cacheOnDisc(true).cacheInMemory(true)
				.postProcessor(avatarBitmapProcessor)
				.showImageForEmptyUri(drawable.settingview_avatar_small)
				.showImageOnFail(drawable.settingview_avatar_small)
				.showImageOnLoading(drawable.settingview_avatar_small).build();
		normalDisplayImageOptions = new DisplayImageOptions.Builder()
				.imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
				.cacheOnDisc(true).cacheInMemory(true)
				.showImageForEmptyUri(drawable.settingview_avatar_small)
				.showImageOnFail(drawable.settingview_avatar_small)
				.showImageOnLoading(drawable.settingview_avatar_small).build();
	}

	public void initImageLoader(Context context) {
		initImageLoaderDisplayOptions();
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				context).threadPriority(Thread.NORM_PRIORITY)
				.defaultDisplayImageOptions(avatarDisplayImageOptions)
				.denyCacheImageMultipleSizesInMemory()
				.discCacheFileNameGenerator(new Md5FileNameGenerator())
				.tasksProcessingOrder(QueueProcessingType.LIFO)
				.writeDebugLogs() // Remove for release app
				.build();
		ImageLoader.getInstance().init(config);
	}

	public Bitmap toGrayscale(Bitmap bmpOriginal) {
		int width, height;
		height = bmpOriginal.getHeight();
		width = bmpOriginal.getWidth();
		Bitmap bmpGrayscale = Bitmap.createBitmap(width, height,
				Bitmap.Config.RGB_565);
		Canvas c = new Canvas(bmpGrayscale);
		Paint paint = new Paint();
		ColorMatrix cm = new ColorMatrix();
		// cm.set(new float[] { -0.41f, 0.539f, 0.873f, 0, 0, 0.452f, 0.666f,
		// -0.11f, 0, 0, -0.3f, 1.71f, -0.4f, 0, 0, 0, 0, 0, 1, 0 });
		cm.setSaturation(0);
		ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
		paint.setColorFilter(f);
		c.drawBitmap(bmpOriginal, 0, 0, paint);
		return bmpGrayscale;
	}

	private void initImageLoaderBitmapProcessor() {
		avatarBitmapProcessor = new BitmapProcessor() {

			@Override
			public Bitmap process(Bitmap bitmap) {

				Bitmap result = bitmap;
				if (GlobalVars.isOffLineLogined()) {
					return toGrayscale(result);
				}
				return result;
			}
		};
	}

	public void unInitImageLoader() {
		ImageLoader.getInstance().denyNetworkDownloads(true);
		ImageLoader.getInstance().clearMemoryCache();
		ImageLoader.getInstance().destroy();
	}

}

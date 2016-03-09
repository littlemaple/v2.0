/**
 * 
 */
package com.medzone.cloud.ui.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.medzone.cloud.GlobalVars;

/**
 * @author ChenJunQi. 2014年9月23日
 * 
 */
public class CloudWebView extends WebView {

	public CloudWebView(Context context) {
		super(context);
		init();
	}

	public CloudWebView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public CloudWebView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	@SuppressLint("SetJavaScriptEnabled")
	protected void init() {

		getSettings().setJavaScriptEnabled(true);

		// getSettings().setUseWideViewPort(true);
		// getSettings().setLoadWithOverviewMode(true);
		// getSettings().setSupportMultipleWindows(true);
		// getSettings().setSupportZoom(true);
		// getSettings().setLayoutAlgorithm(LayoutAlgorithm.NARROW_COLUMNS);
		// getSettings().setLoadsImagesAutomatically(true);
		// getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
		// getSettings().setAppCacheEnabled(true);
		// getSettings().setDomStorageEnabled(true);
		// setWebChromeClient(new WebChromeClient());
		setWebViewClient(new WebViewClient() {
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(GlobalVars.formatWebSite(url));
				return true;
			}
		});

	}

	private void aaa() {
		int screenDensity = getContext().getResources().getDisplayMetrics().densityDpi;
		WebSettings.ZoomDensity zoomDensity = WebSettings.ZoomDensity.MEDIUM;
		switch (screenDensity) {
		case DisplayMetrics.DENSITY_LOW:
			zoomDensity = WebSettings.ZoomDensity.CLOSE;
			break;
		case DisplayMetrics.DENSITY_MEDIUM:
			zoomDensity = WebSettings.ZoomDensity.MEDIUM;
			break;
		case DisplayMetrics.DENSITY_HIGH:
			zoomDensity = WebSettings.ZoomDensity.FAR;
			break;
		}
		setInitialScale(0);
		getSettings().setDefaultZoom(zoomDensity);
	}
}

package com.medzone.cloud.ui.dialog.error;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.medzone.mcloud.R;

public class NormalErrorDialogPage extends CloudErrorDialogPage {

	private View contentView;
	private Context context;

	private ImageView imageView;

	public NormalErrorDialogPage(Context context) {
		super(context);
		this.context = context;
		contentView = View.inflate(context, R.layout.error_tip_dialog, null);
	}

	@Override
	public View getView() {
		return contentView;
	}

	@Override
	public void prepareData() {
		// TODO Auto-generated method stub

	}

	@Override
	public void setTitle(String title) {
		// TODO Auto-generated method stub
	}

	@Override
	public void setContent(String content) {
		TextView tv = (TextView) contentView.findViewById(R.id.tip);
		tv.setText(content);
	}

	@Override
	public void setLoadingIcon(Drawable icon) {
		imageView = (ImageView) contentView.findViewById(R.id.leftDrawable);
		imageView.setImageDrawable(icon);
	}

	@Override
	public void isDrawableAnim(boolean isAnim) {
		if (isAnim) {
			Animation anim = AnimationUtils.loadAnimation(context,
					R.anim.rotate_loading);
			imageView.startAnimation(anim);
		}
	}

}

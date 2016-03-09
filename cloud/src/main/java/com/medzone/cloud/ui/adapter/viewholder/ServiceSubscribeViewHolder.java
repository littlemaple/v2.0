/**
 * 
 */
package com.medzone.cloud.ui.adapter.viewholder;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.medzone.cloud.Constants;
import com.medzone.cloud.clock.AlarmListActivity;
import com.medzone.framework.data.bean.imp.Subscribe;
import com.medzone.mcloud.R.drawable;
import com.medzone.mcloud.R.id;

/**
 * @author ChenJunQi.
 * 
 */
public class ServiceSubscribeViewHolder extends BaseViewHolder {

	private View rootView;
	private Context context;

	private ImageView iconImage;
	private TextView nameTv;

	public ServiceSubscribeViewHolder(View rootView) {
		super(rootView);
		this.rootView = rootView;
		this.context = rootView.getContext();
	}

	@Override
	public void init(View view) {
		nameTv = (TextView) view.findViewById(id.tv_name_member);
		iconImage = (ImageView) view.findViewById(id.iv_icon_member);
	}

	public void fillFromItem(Object item) {
		final Subscribe subs = (Subscribe) item;
		if (subs.getTag() == null) {

			nameTv.setText(subs.getName());

			iconImage.setImageResource(drawable.icon);
			iconImage.setImageDrawable(subs.getdIcon());
			rootView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (subs.getType() == Subscribe.CLOCK_TYPE) {
						jumpToTarget(AlarmListActivity.class);
					} else if (subs.getType() == Subscribe.HEALTH_TYPE) {
						jumpToWebSite(Constants.OFFICAL_WEBSITE);
					} else if (subs.getType() == Subscribe.STORE_TYPE) {
						jumpToWebSite(Constants.OFFICAL_WEBSITE);
					}
				}
			});
		}
	};

	public void jumpToTarget(Class<?> clz) {
		context.startActivity(new Intent(context, clz));
	}

	public void jumpToWebSite(String url) {
		Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
		context.startActivity(intent);
	}
}

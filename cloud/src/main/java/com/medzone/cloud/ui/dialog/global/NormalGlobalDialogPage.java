package com.medzone.cloud.ui.dialog.global;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.medzone.mcloud.R;

public class NormalGlobalDialogPage extends CloudGlobalDialogPage {

	private View contentView;

	public NormalGlobalDialogPage(Context context) {
		super(context);
		contentView = LayoutInflater.from(context).inflate(
				R.layout.custom_global_dialog, null);
	}

	@Override
	public void setContent(String content) {
		TextView tv = (TextView) contentView.findViewById(R.id.content);
		tv.setText(content);
	}

	@Override
	public void setTitle(String title) {
		TextView tv = (TextView) contentView.findViewById(R.id.title);
		tv.setText(title);
	}

	@Override
	public View getView() {
		return contentView;
	}

	@Override
	public void prepareData() {

	}

	@Override
	public void setOnPositiveButton(OnClickListener listener) {
		TextView btn = (TextView) contentView.findViewById(R.id.btn_close_alarm);
		btn.setOnClickListener(listener);
	}

}

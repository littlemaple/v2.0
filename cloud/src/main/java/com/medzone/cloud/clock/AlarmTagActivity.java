package com.medzone.cloud.clock;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.LayoutParams;
import com.medzone.cloud.data.helper.ClockHelper;
import com.medzone.cloud.ui.BaseActivity;
import com.medzone.cloud.ui.dialog.error.ErrorDialogUtil;
import com.medzone.cloud.ui.widget.CleanableEditText;
import com.medzone.framework.errorcode.ProxyCode.LocalError;
import com.medzone.framework.errorcode.ProxyErrorCode;
import com.medzone.mcloud.R;

public class AlarmTagActivity extends BaseActivity implements OnClickListener {

	public static final String TAG_LABEL = "label";

	// private LinearLayout llTag;
	private CleanableEditText mCleanableEditText;
	private String label;

	@Override
	protected void preLoadData(Bundle savedInstanceState) {
		super.preLoadData(savedInstanceState);
		Intent i = getIntent();
		if (i.hasExtra(TAG_LABEL)) {
			label = i.getStringExtra(TAG_LABEL);
		}
	}

	private void initActionBar() {
		ActionBar actionBar = getSupportActionBar();
		ActionBar.LayoutParams params = new ActionBar.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT,
				Gravity.CENTER);
		View view = LayoutInflater.from(this).inflate(
				R.layout.custom_actionbar_with_image, null);
		TextView title = (TextView) view.findViewById(R.id.actionbar_title);
		title.setText(R.string.clock_note);
		ImageButton leftButton = (ImageButton) view
				.findViewById(R.id.actionbar_left);
		leftButton
				.setImageResource(R.drawable.personalinformationview_ic_cancel);
		leftButton.setOnClickListener(this);
		ImageButton rightButton = (ImageButton) view
				.findViewById(R.id.actionbar_right);
		rightButton.setImageResource(R.drawable.personalinformationview_ic_ok);
		rightButton.setOnClickListener(this);
		actionBar.setCustomView(view, params);
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		actionBar.setDisplayShowCustomEnabled(true);
	}

	@Override
	protected void preInitUI() {
		super.preInitUI();
		initActionBar();
	}

	@Override
	protected void initUI() {

		setContentView(R.layout.activity_alarm_change_tag);
		mCleanableEditText = (CleanableEditText) findViewById(R.id.ce_alarm_change_tag);
	}

	@Override
	protected void postInitUI() {
		super.postInitUI();
		if (!TextUtils.isEmpty(label)) {
			mCleanableEditText.setText(label);
			mCleanableEditText.setSelection(label.length());
		}
		mCleanableEditText
				.setOnEditorActionListener(new OnEditorActionListener() {

					@Override
					public boolean onEditorAction(TextView v, int actionId,
							KeyEvent event) {
						if (actionId == EditorInfo.IME_ACTION_GO) {
							changeTagOperator();
						}
						return false;
					}
				});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.actionbar_left:
			AlarmTagActivity.this.finish();
			break;
		case R.id.actionbar_right:
			changeTagOperator();
			break;
		default:
			break;
		}

	}

	public void changeTagOperator() {
		String tag = mCleanableEditText.getText().toString();

		final int errorCode = ClockHelper.checkTagStyle(tag);

		if (errorCode == LocalError.CODE_SUCCESS) {
			Intent i = new Intent();
			i.putExtra(TAG_LABEL, tag);
			AlarmTagActivity.this.setResult(RESULT_OK, i);
			AlarmTagActivity.this.finish();
		} else {
			ErrorDialogUtil.showErrorDialog(AlarmTagActivity.this,
					ProxyErrorCode.TYPE_SERVICE, errorCode, true);
		}
	}
}

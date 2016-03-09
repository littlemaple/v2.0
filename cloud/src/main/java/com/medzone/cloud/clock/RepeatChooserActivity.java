package com.medzone.cloud.clock;

import android.content.Intent;
import android.os.Bundle;
import android.util.SparseIntArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.LayoutParams;
import com.medzone.cloud.data.helper.ClockHelper;
import com.medzone.cloud.ui.BaseActivity;
import com.medzone.mcloud.R;

public class RepeatChooserActivity extends BaseActivity implements
		OnClickListener {

	private ListView listView;
	private AlarmRepeatChooseAdapter adapter;
	private SparseIntArray data;
	public static final String TAG_REPEATED = "repeatition";
	private Integer repeatition; // BLOB OBJECT

	@Override
	protected void preLoadData(Bundle savedInstanceState) {
		super.preLoadData(savedInstanceState);
		data = new SparseIntArray();
		Intent intent = getIntent();
		if (intent != null) {
			repeatition = intent.getIntExtra(TAG_REPEATED, 0);
			data = ClockHelper.getWeekRepeatition(repeatition);
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
		title.setText(R.string.clock_remind);
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
	protected void initUI() {
		super.initUI();
		initActionBar();
		setContentView(R.layout.activity_alarm_repeatition);
		listView = (ListView) this.findViewById(R.id.lv_alarm_repetition);

		adapter = new AlarmRepeatChooseAdapter(this, data);
		listView.setAdapter(adapter);

	}

	@Override
	protected void postInitUI() {
		super.postInitUI();
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				AlarmRepeatViewHolder holder = (AlarmRepeatViewHolder) view
						.getTag();
				holder.checkBox.toggle();
				data.put(position, holder.checkBox.isChecked() ? 1 : 0);
				adapter.refresh(data);
			}
		});
	}

	public void refresh(SparseIntArray data) {
		this.data = data;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.actionbar_left:
			RepeatChooserActivity.this.finish();
			break;
		case R.id.actionbar_right:
			Intent i = new Intent();
			i.putExtra(TAG_REPEATED, ClockHelper.getRepeatition(data));
			RepeatChooserActivity.this.setResult(RESULT_OK, i);
			RepeatChooserActivity.this.finish();
			break;
		default:
			break;
		}
	}
}

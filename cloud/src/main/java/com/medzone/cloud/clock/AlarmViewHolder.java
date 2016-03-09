package com.medzone.cloud.clock;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.medzone.cloud.clock.manager.Alarm;
import com.medzone.cloud.clock.manager.AlarmManager;
import com.medzone.cloud.data.TemporaryData;
import com.medzone.cloud.data.helper.ClockHelper;
import com.medzone.cloud.ui.adapter.viewholder.BaseViewHolder;
import com.medzone.mcloud.R;
import com.medzone.mcloud.R.id;
import com.medzone.mcloud.R.string;

public class AlarmViewHolder extends BaseViewHolder {

	private TextView tvClockTime, tvClockRemark, tvClockRepetition;
	private LinearLayout llAlarmItem;
	public CheckBox cbAlarmSwitch;
	private Context context;
	protected Button btnDelete;

	public AlarmViewHolder(View rootView, Context context) {
		super(rootView);
		this.context = context;
	}

	@Override
	public void init(View view) {
		tvClockTime = (TextView) view.findViewById(id.tv_clock_time);
		tvClockRemark = (TextView) view.findViewById(id.tv_clock_remark);
		tvClockRepetition = (TextView) view.findViewById(id.tv_clock_repeation);
		cbAlarmSwitch = (CheckBox) view.findViewById(id.cb_new_notice);
		llAlarmItem = (LinearLayout) view.findViewById(id.ll_clock_item);
		btnDelete = (Button)view.findViewById(R.id.btn_delete_item);
	}

	public void fillFromItem(Object item) {
		final Alarm alarm = (Alarm) item;
		tvClockTime.setText(alarm.getClockTime());
		// if (TextUtils.isEmpty(clock.getLabel())) {
		// tvClockRemark.setVisibility(View.GONE);
		// } else {
		// tvClockRemark.setVisibility(View.VISIBLE);
		// tvClockRemark.setText(clock.getLabel());
		// }
		String repMessage = ClockHelper.getRepText(context,
				alarm.getRepetition());
		// if (TextUtils.isEmpty(repMessage)) {
		// tvClockRepetition.setVisibility(View.GONE);
		// } else {
		// tvClockRepetition.setVisibility(View.VISIBLE);
		// tvClockRepetition.setText(repMessage);
		// }
		tvClockRepetition.setText(repMessage);
		tvClockRemark.setText(alarm.getLabel());
		
//		btnDelete.setOnClickListener(new View.OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				Log.e("click"+alarm.getClockID());
//				btnDelete.setVisibility(View.GONE);
//			}
//		});
		
		llAlarmItem.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				TemporaryData.save(Alarm.class.getName(), alarm);
				Intent intent = new Intent(context, AlarmEditActivity.class);
				context.startActivity(intent);
			}
		});
		llAlarmItem.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				showDialog(alarm);
				return false;
			}
		});

		cbAlarmSwitch.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {

				if (alarm.isSwitchState() && isChecked) {
					return;
				}
				alarm.setSwitchState(isChecked);
				alarm.setIsNext(true);
//				ClockCache.getInstance().addOrUpdate(clock);
				AlarmManager.updateAlarm(alarm);
				Intent i = new Intent(context, AlarmService.class);
				if (isChecked) {
					i.putExtra(ClockHelper.CURRNET_TIME, alarm.getClockTime());
					i.putExtra(ClockHelper.REPEATITION, alarm.getRepetition());
				}
				// FIXME switch切换后service会常驻，考虑是否在退出闹钟的时候destory service
				i.setAction(AlarmService.ACTION_SET_SHOW_NOTIF);
				context.startService(i);
			}
		});
		cbAlarmSwitch.setChecked(alarm.isSwitchState());
	};

	public void showDialog(final Alarm alarm) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setCancelable(true);
		builder.setMessage(R.string.is_delete_alarm);
		builder.setPositiveButton(string.action_delete, new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {

//				ClockCache.getInstance().delete(alarm);
				AlarmManager.deleteAlarm(alarm);
				Intent intent = new Intent();
				intent.setAction(AlarmService.ACTION_REFRESH);
				context.sendBroadcast(intent);

				intent.setAction(AlarmService.ACTION_DELETE_ALARM);
				intent.setClass(context, AlarmService.class);
				context.startService(intent);
			}
		});
		builder.setNegativeButton(string.action_cancel, null);
		builder.create().show();

	}

}

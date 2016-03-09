package com.medzone.cloud.clock;

import java.util.Collections;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.medzone.cloud.clock.manager.Alarm;
import com.medzone.cloud.data.helper.CustomComparator;
import com.medzone.mcloud.R.layout;

public class AlarmListAdapter extends BaseAdapter {

	private List<Alarm> list;
	private Context context;

	private float downX, upX;

	public AlarmListAdapter(Context context, List<Alarm> list) {
		this.context = context;
		this.list = list;
		sortAlarm();
	}

	@Override
	public int getCount() {
		if (list != null)
			return list.size();
		return 0;
	}

	public void setContent(List<Alarm> list) {
		this.list = list;
		sortAlarm();
		this.notifyDataSetChanged();
	}

	public void sortAlarm() {
		CustomComparator<Alarm> cmp = new CustomComparator<Alarm>();
		Collections.sort(list, cmp);
	}

	@Override
	public Object getItem(int position) {
		if (list != null)
			return list.get(position);
		return null;
	}

	@Override
	public long getItemId(int position) {
		if (list != null) {
			Alarm i = list.get(position);
			return i.getClockID();
		}
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View itemView = null;
		if (convertView != null) {
			itemView = convertView;
		} else {
			itemView = inflateView();
		}
		fillView(itemView, getItem(position));

		// itemView.setOnTouchListener(new OnTouchListener() {
		//
		// @Override
		// public boolean onTouch(View v, MotionEvent event) {
		// switch (event.getAction()) {
		// case MotionEvent.ACTION_DOWN: //手指按下
		// downX = event.getX(); //获取手指x坐标
		// if (viewHolder.btnDelete != null) {
		// viewHolder.btnDelete.setVisibility(View.GONE); //影藏显示出来的button
		// }
		// break;
		// case MotionEvent.ACTION_UP: //手指离开
		// upX = event.getX(); //获取x坐标值
		// break;
		// }
		//
		// if (viewHolder.btnDelete != null) {
		// if (Math.abs(downX - upX) > 35) { //2次坐标的绝对值如果大于35，就认为是左右滑动
		// viewHolder.btnDelete.setVisibility(View.VISIBLE); //显示删除button
		// // viewHolder = holder.button; //赋值给全局button，一会儿用
		// // view=v; //得到itemview，在上面加动画
		// return true; //终止事件
		// }
		// return false; //释放事件，使onitemClick可以执行
		// }
		//
		// return false;
		// }
		// });
		return itemView;
	}

	private View inflateView() {
		View itemView = null;
		itemView = LayoutInflater.from(context).inflate(layout.list_item_clock,
				null);
		itemView.setTag(new AlarmViewHolder(itemView, context));
		return itemView;
	}

	private AlarmViewHolder fillView(View view, final Object item) {
		AlarmViewHolder viewHolder = (AlarmViewHolder) view.getTag();
		viewHolder.fillFromItem(item);
		return viewHolder;
	}
}
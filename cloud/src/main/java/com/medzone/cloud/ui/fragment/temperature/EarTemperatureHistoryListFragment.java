package com.medzone.cloud.ui.fragment.temperature;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupCollapseListener;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.LayoutParams;
import com.medzone.cloud.Constants;
import com.medzone.cloud.data.CurrentAccountManager;
import com.medzone.cloud.module.modules.EarTemperatureModule;
import com.medzone.cloud.ui.MeasureDataActivity;
import com.medzone.cloud.ui.adapter.TemperatureListExpandableAdapter;
import com.medzone.cloud.ui.dialog.error.ErrorDialogUtil;
import com.medzone.cloud.ui.widget.ErrorDialog;
import com.medzone.cloud.ui.widget.ErrorDialog.ErrorDialogListener;
import com.medzone.framework.data.bean.imp.EarTemperature;
import com.medzone.framework.data.bean.imp.MeasureStatistical;
import com.medzone.framework.errorcode.ProxyCode;
import com.medzone.framework.errorcode.ProxyErrorCode;
import com.medzone.framework.fragment.BaseFragment;
import com.medzone.framework.task.BaseResult;
import com.medzone.framework.task.TaskHost;
import com.medzone.framework.util.TimeUtils;
import com.medzone.mcloud.R;
import com.medzone.mcloud.R.drawable;
import com.medzone.mcloud.R.id;
import com.medzone.mcloud.R.layout;
import com.medzone.mcloud.R.string;

public class EarTemperatureHistoryListFragment extends BaseFragment implements
		View.OnClickListener, OnScrollListener {
	private List<MeasureStatistical> groupData;
	private List<List<EarTemperature>> childData;
	private List<HashMap<String, String>> totalSum;
	private TemperatureListExpandableAdapter expandAdapter;
	private ExpandableListView expandableList;
	private int indicatorGroupHeight;
	private int theGroupExpandPosition = -1;
	private int countExpand = 0;
	private Map<Integer, Integer> ids = new HashMap<Integer, Integer>();
	private LinearLayout viewFlotage = null;
	private TextView topMonthTV;
	private TextView monthStartTV;
	private TextView monthEndTV;
	private TextView sumTimesTV;
	private TextView errorTimesTV;
	private TextView yearTV;
	private TextView listIsEmptyTV;
	private ImageView toLeftIV, toRightIV;
	private MeasureDataActivity mdActivity;
	private int currentYear;
	private int systemYear;
	private int currentMonth;
	private int systemMonth;
	private int fristYear;
	private int fristMonth;
	private int groupIndex;
	private int childIndex;
	private EarTemperature deleteEarTemperature;
	private EarTemperatureModule etModule;
	private View view;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mdActivity = (MeasureDataActivity) mdActivity;
		etModule = (EarTemperatureModule) mdActivity.getAttachModule();
		systemYear = TimeUtils.getCurrentYear();
		systemMonth = TimeUtils.getCurrentMonth() + 1;
		currentYear = systemYear;
	}

	@Override
	public void onResume() {
		initActionBar();
		super.onResume();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		view = inflater.inflate(layout.fragment_temperature_history_list,
				container, false);
		yearTV = (TextView) view
				.findViewById(id.temperature_history_list_top_year);
		toLeftIV = (ImageView) view
				.findViewById(id.temperature_history_list_top_left);
		toRightIV = (ImageView) view
				.findViewById(id.temperature_history_list_top_right);
		expandableList = (ExpandableListView) view
				.findViewById(R.id.expandableListView_list);
		listIsEmptyTV = (TextView) view
				.findViewById(id.temperature_history_list_text);
		topMonthTV = (TextView) view
				.findViewById(R.id.temperature_history_list_top_month);
		monthStartTV = (TextView) view
				.findViewById(R.id.temperature_history_list_top_month_start);
		monthEndTV = (TextView) view
				.findViewById(R.id.temperature_history_list_top_month_end);
		sumTimesTV = (TextView) view
				.findViewById(R.id.temperature_history_list_top_sum_times);
		errorTimesTV = (TextView) view
				.findViewById(R.id.temperature_history_list_top_error_times);
		viewFlotage = (LinearLayout) view
				.findViewById(R.id.temperature_history_list_top_group);
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		fillView(view);
		getFristMeasureTime();
		dataRefresh();
	}

	private void dataRefresh() {
		initData();
		expandAdapter = new TemperatureListExpandableAdapter(mdActivity,
				groupData, childData);
		View v = new View(mdActivity);
		expandableList.addHeaderView(v);
		expandableList.setAdapter(expandAdapter);
		expandableList.setGroupIndicator(null);
	}

	@Override
	protected void initActionBar() {
		ActionBar actionBar = getSherlockActivity().getSupportActionBar();
		ActionBar.LayoutParams params = new ActionBar.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT,
				Gravity.CENTER);
		View view = LayoutInflater.from(getSherlockActivity()).inflate(
				R.layout.custom_actionbar_with_image, null);
		TextView title = (TextView) view.findViewById(R.id.actionbar_title);
		title.setText(getString(string.history_list_temperature_title));
		ImageButton leftButton = (ImageButton) view
				.findViewById(id.actionbar_left);
		leftButton.setImageResource(drawable.public_ic_back);
		leftButton.setOnClickListener(this);

		actionBar.setCustomView(view, params);
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		actionBar.setDisplayShowCustomEnabled(true);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case id.actionbar_left:
			mdActivity.popBackStack();
			break;
		case id.temperature_history_list_top_left:
			if (fristYear > 0 && fristYear < currentYear) {
				currentYear--;
				yearTV.setText(currentYear + "");
				dataRefresh();
			} else {
				ErrorDialogUtil.showErrorToast(mdActivity,
						ProxyErrorCode.TYPE_MEASURE,
						ProxyErrorCode.LocalError.CODE_11401);
			}
			break;
		case id.temperature_history_list_top_right:
			if (currentYear < systemYear) {
				currentYear++;
				yearTV.setText(currentYear + "");
				dataRefresh();
			}
			break;
		case id.temperature_history_list_top_group:
			viewFlotage.setVisibility(View.GONE);
			expandableList.collapseGroup(theGroupExpandPosition);
			expandableList.setSelectedGroup(theGroupExpandPosition);
			break;
		default:
			break;
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	private void getFristMeasureTime() {
		String time = etModule.getFristMeasureTime(CurrentAccountManager
				.getCurAccount());
		if (time != null && !time.equals("")) {
			fristYear = Integer.valueOf(time.substring(0, 4));
			fristMonth = Integer.valueOf(time.substring(4, 6));
		} else {
			listIsEmptyTV.setVisibility(View.VISIBLE);
		}
	}

	private void initData() {
		groupData = new ArrayList<MeasureStatistical>();
		childData = new ArrayList<List<EarTemperature>>();
		if (fristYear > 0) {
			if (currentYear == systemYear && currentYear == fristYear) {
				currentMonth = systemMonth;
			} else {
				currentMonth = 12;
				fristMonth = 1;
			}
			totalSum = new ArrayList<HashMap<String, String>>();
			totalSum = etModule.getEarTemperatureRecord(currentYear);
			if (totalSum != null && totalSum.size() > 0) {
				for (int i = currentMonth; i >= fristMonth; i--) {
					getGroupData(i);
					getChildData(i);
				}
			}
		}
	}

	private void getGroupData(int index) {
		MeasureStatistical group = new MeasureStatistical();
		String month = "";
		if (index < 10) {
			month = "0" + index;
		} else {
			month = "" + index;
		}
		group.setMeasureMonth(month);
		if (currentYear == systemYear && index == systemMonth) {
			group.setMeasureMonthStart(TimeUtils.getFirstDay());
			group.setMeasureMonthEnd(TimeUtils.getLastDay());
		} else {
			group.setMeasureMonthStart(TimeUtils.getFirstdayMonth(currentYear
					+ "-" + month));
			group.setMeasureMonthEnd(TimeUtils.getLastdayMonth(currentYear
					+ "-" + month));
		}

		for (int count = 0; count < totalSum.size(); count++) {
			if (Integer.valueOf(totalSum.get(totalSum.size() - 1 - count).get(
					Constants.KEY_MONTH)) == index) {
				group.setMeasureSumTimes(totalSum.get(
						totalSum.size() - 1 - count).get(
						Constants.KEY_ALL_COUNT));
				group.setMeasureExceptionTimes(totalSum.get(
						totalSum.size() - 1 - count).get(
						Constants.KEY_EXCEPTION_COUNT));
			}
		}
		groupData.add(group);
	}

	private void getChildData(int index) {
		List<EarTemperature> temperatureList = new ArrayList<EarTemperature>();
		temperatureList = etModule.getMonthlyAllData(currentYear, index);
		childData.add(temperatureList);
	}

	public void fillView(View view) {
		yearTV.setText(currentYear + "");
		toLeftIV.setOnClickListener(this);
		toRightIV.setOnClickListener(this);
		viewFlotage.setOnClickListener(this);

		// 设置滚动事件
		expandableList.setOnScrollListener(this);

		// 点击子列表事件监听
		expandableList.setOnChildClickListener(new myOnChildClickListener());

		// 长按子列表事件监听
		expandableList
				.setOnItemLongClickListener(new myOnItemLongClickListener());

		// 监听父节点打开的事件
		expandableList.setOnGroupExpandListener(new myOnGroupExpandListener());

		// 监听父节点关闭的事件
		expandableList
				.setOnGroupCollapseListener(new myOnGroupCollapseListener());
	}

	class myOnChildClickListener implements OnChildClickListener {
		@Override
		public boolean onChildClick(ExpandableListView parent, View v,
				int groupPosition, int childPosition, long id) {
			mdActivity.setSourceHistory(true);
			mdActivity.toEarTemperatureResultDetailsFragment(childData.get(
					groupPosition).get(childPosition));
			return true;
		}
	}

	class myOnItemLongClickListener implements OnItemLongClickListener {
		@Override
		public boolean onItemLongClick(AdapterView<?> parent, View view,
				int position, long id) {
			childIndex = (Integer) view
					.getTag(R.id.temperature_history_list_child_time);
			groupIndex = (Integer) view
					.getTag(R.id.temperature_history_list_sum_times);
			if (childIndex == -1) {
			} else {
				showPopupWindow(getString(string.history_list_delete_title),
						getString(string.history_list_delete_content),
						getString(string.action_delete),
						getString(string.action_cancel));
			}
			return true;
		}
	}

	class myOnGroupExpandListener implements OnGroupExpandListener {
		@Override
		public void onGroupExpand(int groupPosition) {
			theGroupExpandPosition = groupPosition;
			ids.put(groupPosition, groupPosition);
			countExpand = ids.size();
		}
	}

	class myOnGroupCollapseListener implements OnGroupCollapseListener {
		@Override
		public void onGroupCollapse(int groupPosition) {
			ids.remove(groupPosition);
			// expandableList.setSelectedGroup(groupPosition);//设置被选中的制顶端
			countExpand = ids.size();
		}
	}

	private void deleteCloudEarTemperature(int groupPosition, int childPosition) {
		deleteEarTemperature = childData.get(groupPosition).get(childPosition);
		if (deleteEarTemperature.getRecordID() != null) {
			etModule.deleteCloudEarTemperature(
					deleteEarTemperature.getRecordID(),
					CurrentAccountManager.getCurAccount(), myTaskHost);
		} else {
			deleteLocalEarTemperature();
		}
	}

	private void deleteLocalEarTemperature() {
		etModule.deleteLocalEarTemperature(deleteEarTemperature);
		updateExpandableListView(groupIndex, childIndex);
		ErrorDialogUtil.showErrorToast(mdActivity, ProxyErrorCode.TYPE_MEASURE,
				ProxyCode.LocalError.CODE_11403);
	}

	private void updateExpandableListView(int groupPosition, int childPosition) {
		String allCount = groupData.get(groupPosition).getMeasureSumTimes();
		int allSum = Integer.valueOf(allCount) - 1;
		if (allSum >= 0) {
			allCount = String.valueOf(allSum);
			groupData.get(groupPosition).setMeasureSumTimes(allCount);
		}
		int abnormal = deleteEarTemperature.getAbnormal();
		if (abnormal != EarTemperature.TEMPERATURE_STATE_NORMAL) {
			String exceptionCount = groupData.get(groupPosition)
					.getMeasureExceptionTimes();
			int exceptionSum = Integer.valueOf(exceptionCount) - 1;
			if (exceptionSum >= 0) {
				exceptionCount = String.valueOf(exceptionSum);
				groupData.get(groupPosition).setMeasureExceptionTimes(
						exceptionCount);
			}
		}
		childData.get(groupPosition).remove(childPosition);
		expandAdapter.notifyDataSetChanged();
	}

	private TaskHost myTaskHost = new TaskHost() {
		@Override
		public void onPostExecute(int requestCode, BaseResult result) {
			super.onPostExecute(requestCode, result);
			if (result.isSuccess()) {
				if (result.isServerDisposeSuccess()) {
					deleteLocalEarTemperature();
				} else {
					ErrorDialogUtil.showErrorToast(mdActivity,
							ProxyErrorCode.TYPE_MEASURE,
							ProxyErrorCode.LocalError.CODE_11404);
				}
			} else {
				ErrorDialogUtil.showErrorToast(mdActivity,
						ProxyErrorCode.TYPE_MEASURE,
						ProxyErrorCode.LocalError.CODE_10002);
			}
		}
	};

	private Dialog dialog;

	// 初始化弹出信息
	private void initPopupWindow(String title, String content, String leftBtn,
			String rightBtn) {
		if (dialog == null) {
			ErrorDialogListener listener = new ErrorDialogListener() {
				@Override
				public void restart() {
					dialog.dismiss();
					deleteCloudEarTemperature(groupIndex, childIndex);
				}

				@Override
				public void exit() {
					dialog.dismiss();
				}
			};
			dialog = new ErrorDialog(mdActivity, ErrorDialog.TYPE, listener,
					title, content, leftBtn, rightBtn).dialogFactory();
		}
	}

	private void showPopupWindow(String title, String content, String leftBtn,
			String rightBtn) {
		if (mdActivity.isFinishing())
			return;
		if (dialog == null) {
			initPopupWindow(title, content, leftBtn, rightBtn);
		}
		dialog.show();
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		// 防止三星,魅族等手机第一个条目可以一直往下拉,父条目和悬浮同时出现的问题
		if (firstVisibleItem == 0) {
			viewFlotage.setVisibility(View.GONE);
		}
		// 控制滑动时TextView的显示与隐藏
		int npos = view.pointToPosition(0, 0);
		if (npos != AdapterView.INVALID_POSITION) {
			long pos = expandableList.getExpandableListPosition(npos);
			int childPos = ExpandableListView.getPackedPositionChild(pos);
			final int groupPos = ExpandableListView.getPackedPositionGroup(pos);
			if (childPos == AdapterView.INVALID_POSITION) {
				View groupView = expandableList.getChildAt(npos
						- expandableList.getFirstVisiblePosition());
				indicatorGroupHeight = groupView.getHeight();
			}

			if (indicatorGroupHeight == 0) {
				return;
			}

			if (countExpand > 0) {
				theGroupExpandPosition = groupPos;
				fillTopView(groupPos);
			}
			if (countExpand == 0) {
				viewFlotage.setVisibility(View.GONE);
			}
		}

		if (theGroupExpandPosition == -1) {
			return;
		}

		int showHeight = getHeight();
		MarginLayoutParams layoutParams = (MarginLayoutParams) viewFlotage
				.getLayoutParams();
		// 得到悬浮的条滑出屏幕多少
		layoutParams.topMargin = -(indicatorGroupHeight - showHeight);
		viewFlotage.setLayoutParams(layoutParams);
	}

	private void fillTopView(int groupPos) {
		topMonthTV.setText(groupData.get(theGroupExpandPosition)
				.getMeasureMonth());
		monthStartTV.setText(groupData.get(theGroupExpandPosition)
				.getMeasureMonthStart());
		monthEndTV.setText(groupData.get(theGroupExpandPosition)
				.getMeasureMonthEnd());
		String measureTimes = groupData.get(theGroupExpandPosition)
				.getMeasureSumTimes();
		if (measureTimes != null && measureTimes.length() == 1) {
			sumTimesTV.setText("0" + measureTimes);
		} else {
			sumTimesTV.setText(measureTimes);
		}
		String errorTimes = groupData.get(theGroupExpandPosition)
				.getMeasureExceptionTimes();
		if (errorTimes != null && errorTimes.length() == 1) {
			errorTimesTV.setText("0" + errorTimes);
		} else {
			errorTimesTV.setText(errorTimes);
		}
		if (theGroupExpandPosition != groupPos
				|| !expandableList.isGroupExpanded(groupPos)) {
			viewFlotage.setVisibility(View.GONE);
		} else {
			viewFlotage.setVisibility(View.VISIBLE);
		}
	}

	public boolean dispatchTouchEvent(MotionEvent ev) {
		return mdActivity.dispatchTouchEvent(ev);
	}

	private int getHeight() {
		int showHeight = indicatorGroupHeight;
		int nEndPos = expandableList.pointToPosition(0, indicatorGroupHeight);
		if (nEndPos != AdapterView.INVALID_POSITION) {
			long pos = expandableList.getExpandableListPosition(nEndPos);
			int groupPos = ExpandableListView.getPackedPositionGroup(pos);
			if (groupPos != theGroupExpandPosition) {
				View viewNext = expandableList.getChildAt(nEndPos
						- expandableList.getFirstVisiblePosition());
				showHeight = viewNext.getTop();
			}
		}
		return showHeight;
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
	}
}

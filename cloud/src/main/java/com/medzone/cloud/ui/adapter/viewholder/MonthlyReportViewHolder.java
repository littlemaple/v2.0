/**
 * 
 */
package com.medzone.cloud.ui.adapter.viewholder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.medzone.cloud.data.helper.MeasureDataUtil;
import com.medzone.cloud.module.modules.IMonthlyStatInterface;
import com.medzone.framework.util.TimeUtils;
import com.medzone.mcloud.R.id;

/**
 * @author ChenJunQi.
 * 
 */
public class MonthlyReportViewHolder extends BaseViewHolder {

	private ImageView ivMonthlyReport;
	private TextView tvExceptionCounts;;
	private TextView tvTotalCounts;

	public MonthlyReportViewHolder(View rootView) {
		super(rootView);
	}

	@Override
	public void init(View view) {
		ivMonthlyReport = (ImageView) view.findViewById(id.iv_measure_module);
		tvExceptionCounts = (TextView) view.findViewById(id.tv_exception_time);
		tvTotalCounts = (TextView) view.findViewById(id.tv_total_time);
	}

	@Override
	public void fillFromItem(Object item) {
		super.fillFromItem(item);
		final IMonthlyStatInterface i = (IMonthlyStatInterface) item;

		int curYear = TimeUtils.getCurrentYear();
		int curMonth = TimeUtils.getCurrentMonth() + 1;
		int totalCounts = i.getMonthlyAllCounts(curYear, curMonth);
		int exceptionCounts = i.getMonthlyExceptionCounts(curYear, curMonth);

		ivMonthlyReport.setImageResource(i.getMonthlyIndicator());
		tvExceptionCounts.setText(exceptionCounts + "次异常");
		tvTotalCounts.setText("共" + totalCounts + "次");

	}

}

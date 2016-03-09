package com.medzone.cloud.data.helper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.content.Context;
import android.text.TextUtils;
import android.util.SparseIntArray;

import com.medzone.cloud.CloudApplication;
import com.medzone.cloud.CloudApplicationPreference;
import com.medzone.cloud.clock.manager.Alarm;
import com.medzone.cloud.clock.manager.AlarmManager;
import com.medzone.framework.data.bean.imp.Account;
import com.medzone.framework.errorcode.ProxyCode.LocalError;
import com.medzone.mcloud.R;

/**
 * 测量提醒
 * 
 */
public class ClockHelper {
	public static final int WEEK_DAY_TOTAL = 7;
	public static final int FLAG_SWITCH_ALARM = 0;
	public static final String CURRNET_TIME = "current_time";
	public static final String REPEATITION = "repeatition";
	public static final int FLAG_EDIT_ALARM = 1;

	// 六，五，四，三，二，一，日
	// 二进制中星期六是最高位，在map中存储的方式：星期天对应得key=0

	/**
	 * 111 1111
	 */
	private static final int REPEATITION_ALL_DAY = 127;
	/**
	 * 011 1110
	 */
	private static final int REPEATITION_WORK_DAY = 62;
	/**
	 * 100 0001
	 */
	private static final int REPEATITION_REST_DAY = 65;

	// =====管理当前alarm对象======//
	public static void setCurrentAlarm(Alarm alarm) {
		Alarm temp = new Alarm();
		try {
			temp = (Alarm) alarm.clone();
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		AlarmManager.addCurrentAlarm(temp);
	}

	public static Alarm getCurrentAlarm() {
		Account account = CloudApplicationPreference.getLoginAccount();
		if (account != null)
			return AlarmManager.getCurrentAlarm(account.getAccountID());
		return null;
	}

	public static void clearCurrentAlarm() {
		Account account = CloudApplicationPreference.getLoginAccount();
		if (account != null)
			AlarmManager.clearCurrentAlarm(account.getAccountID());
	}

	// ==========================
	// 错误码逻辑判断
	// ==========================

	/**
	 * 判断标签的格式
	 * 
	 * @param tag
	 * @return
	 */
	public static int checkTagStyle(final String tag) {

		if (TextUtils.isEmpty(tag)) {
			return LocalError.CODE_14103;
		}

		final int len = AccountHelper.getContentBytesLength(tag);

		if (len <= CloudApplication.getInstance().getApplicationContext()
				.getResources().getInteger(R.integer.limit_clock_tag)) {
			return AccountHelper.isCnEnNumCorrect(tag) ? LocalError.CODE_SUCCESS
					: LocalError.CODE_14101;
		} else {
			return LocalError.CODE_14104;
		}
	}

	/**
	 * 是否是登陆状态
	 * 
	 * @return
	 */
	public static boolean isLoginState() {
		if (CloudApplicationPreference.getLoginAccount() != null)
			return true;
		return false;

	}

	/**
	 * 获取具体的某天重复
	 * 
	 * @param repeatition
	 * @return
	 */
	public static SparseIntArray getWeekRepeatition(Integer repeatition) {
		SparseIntArray map = new SparseIntArray();
		if (repeatition == null)
			return null;
		for (int i = 0; i < WEEK_DAY_TOTAL; i++) {
			if ((repeatition & (1 << i)) > 0) {
				map.put(i, 1);
			} else {
				map.put(i, 0);
			}
		}
		return map;
	}

	/**
	 * 通过map组装成整型
	 * 
	 * @param map
	 * @return
	 */
	public static Integer getRepeatition(SparseIntArray map) {
		int result = 0;
		if (map.size() < 1)
			return null;
		for (int i = 0; i < map.size(); i++) {
			result += map.get(i) << i;
		}
		return result;

	}

	/**
	 * 根据整型repeatition 获得文本信息
	 * 
	 * @param repetition
	 * @return
	 */
	public static String getRepText(Context context, Integer repeatition) {
		boolean first = true;
		if (repeatition == null)
			return null;
		// 每天
		if (repeatition.intValue() == REPEATITION_ALL_DAY)
			return context.getResources().getString(R.string.reminder_time_set);
		// 工作日
		if (repeatition.intValue() == REPEATITION_WORK_DAY)
			return context.getResources().getString(
					R.string.reminder_work_day_set);
		// 休息日
		if (repeatition.intValue() == REPEATITION_REST_DAY)
			return context.getResources().getString(
					R.string.reminder_rest_day_set);

		// 其他
		SparseIntArray array = getWeekRepeatition(repeatition);
		String dat[] = context.getResources().getStringArray(R.array.week_list);
		String result = "";
		for (int i = 0; i < array.size(); i++) {
			if (array.get(i) == 1) {
				String template = getText(R.string.week_space);
				if (first) {
					result += String.format(template, dat[i]);
					first = false;
				} else
					result += String.format(template, dat[i]);
			}
		}
		// 去掉多余的最后一位'、'
		if (result.contains("、")) {
			result = result.substring(0, result.length() - 1);
		}
		return result;
	}

	/**
	 * 将int型转成hh:mm的形式
	 * 
	 * @param hour
	 * @param minute
	 * @return
	 */
	public static String convertToStringTime(int hour, int minute) {

		String result = new String("");
		result += ClockHelper.convertToStringTime(hour);
		result += ":";
		result += ClockHelper.convertToStringTime(minute);

		return result;
	}

	private static String convertToStringTime(int value) {
		String result;
		if (value < 10) {
			result = "0" + value;
		} else {
			result = value + "";
		}
		return result;
	}

	/**
	 * 这里约定格式必须为：xx:xx
	 * 
	 * @param time
	 * @return
	 */
	public static int[] convertToIntegerTime(String time) {
		final String t = time;
		int[] result = new int[2];
		result[0] = Integer.valueOf(t.substring(0, 2));
		result[1] = Integer.valueOf(t.substring(3, 5));
		return result;
	}

	/**
	 * 
	 * @return 当前日期的key
	 */
	public static Integer getTodaySparseKey() {
		Calendar c = Calendar.getInstance();
		int w = c.get(Calendar.DAY_OF_WEEK) - 1;
		return w;
	}

	/**
	 * 将毫秒的时间间隔转成文本显示形式
	 * 
	 * @param interval
	 * @return
	 */
	public static String getIntervalText(long interval) {
		// 毫秒转成秒
		interval = interval / 1000;
		long hour = 0, minute = 0, second = 0, day = 0;
		if (interval >= 86400) {
			day = interval / 86400;
			long mod = interval % 86400;
			if (mod >= 3600) {
				hour = mod / 3600;
				mod = mod % 3600;
				if (mod >= 60) {
					minute = mod / 60;
				}
				second = mod % 60;
			} else {
				if (mod >= 60) {
					minute = mod / 60;
				}
				second = mod % 60;
			}
		} else {
			if (interval >= 3600) {
				hour = interval / 3600;
				long mod = interval % 3600;
				if (mod >= 60) {
					minute = mod / 60;
				}
				second = mod % 60;
			} else {
				if (interval >= 60) {
					minute = interval / 60;
				}
				second = interval % 60;
			}
		}
		String template = null;
		if (day == 0) {
			if (hour == 0) {
				if (minute == 0) {
					template = getText(R.string.clock_view_text_ss);
					return String.format(template, second);
				}
				template = getText(R.string.clock_view_text_mm_ss);
				return String.format(template, minute, second);
			}
			template = getText(R.string.clock_view_text_hh_mm_ss);
			return String.format(template, hour, minute, second);
		}
		template = getText(R.string.clock_view_text_dd_hh_mm_ss);
		return String.format(template, day, hour, minute, second);
	}

	/**
	 * 获取下一次的响铃时刻
	 * 
	 * @param value
	 * @param repeatition
	 * @return
	 */
	public static Long getNextTimemillsecond(Long value, Integer repeatition) {
		SparseIntArray sArray = getWeekRepeatition(repeatition);
		int i = getTodaySparseKey();
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(value);
		if (sArray.get(i) == 1 && value > System.currentTimeMillis()) {
			return value;
		}
		while (i < WEEK_DAY_TOTAL) {
			i++;
			if (i >= WEEK_DAY_TOTAL) {
				i = 0;
			}
			c.add(Calendar.DATE, 1);
			if (sArray.get(i) == 1) {
				return c.getTimeInMillis();
			}
		}
		return c.getTimeInMillis();
	}

	/**
	 * 将HH:mm的字符串转成日期格式，出去小时分钟，其余为当前时间
	 * 
	 * @param time
	 * @return
	 */
	public static Date getAlarmDate(String time) {
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
		Calendar cal = Calendar.getInstance();
		Calendar c = Calendar.getInstance();
		Date date = null;
		try {
			c.setTimeInMillis(System.currentTimeMillis());
			date = sdf.parse(time);
			cal.setTime(sdf.parse(time));
			cal.set(Calendar.YEAR, c.get(Calendar.YEAR));
			cal.set(Calendar.MONTH, c.get(Calendar.MONTH));
			cal.set(Calendar.DAY_OF_MONTH, c.get(Calendar.DAY_OF_MONTH));
			date = cal.getTime();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}

	/**
	 * 根据id获取资源文件
	 * 
	 * @return
	 */
	public static String getText(int id) {
		return CloudApplication.getInstance().getResources().getString(id);
	}
}

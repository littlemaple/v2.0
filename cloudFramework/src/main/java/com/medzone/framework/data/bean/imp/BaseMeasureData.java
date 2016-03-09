package com.medzone.framework.data.bean.imp;

import java.util.List;
import java.util.Random;

import com.j256.ormlite.field.DatabaseField;
import com.medzone.framework.data.bean.BasePagingContent;
import com.medzone.framework.module.IRuleDetails;
import com.medzone.framework.util.TimeUtils;

/**
 * 
 * @author ChenJunQi.
 * 
 */
public abstract class BaseMeasureData extends BasePagingContent implements
		IRuleDetails {

	private static final long serialVersionUID = 1L;
	public static final String NAME_FIELD_MEASURETIME = "measureTime";
	public static final String NAME_FIELD_MEASUREU_ID = "measureUID";
	public static final String NAME_FIELD_STATE_FLAG = "stateFlag";
	public static final String NAME_FIELD_RECORD_ID = "recordID";
	public static final String NAME_FIELD_README = "readme";
	public static final String NAME_FIELD_UPTIME = "uptime";
	public static final String NAME_FIELD_SOURCE = "source";
	public static final String NAME_FIELD_X = "x";
	public static final String NAME_FIELD_Y = "y";
	public static final String NAME_FIELD_ABNORMAL = "abnormal";
	public static final String NAME_FIELD_MEASURETIME_HELP = "measureTimeHelp";

	private Integer type; // measure type

	@DatabaseField
	private Long measureTime;

	@DatabaseField(id = true)
	private String measureUID;

	@DatabaseField
	private String measureTimeHelp;

	@DatabaseField
	private String readme; // The record of the user

	@DatabaseField
	private Integer stateFlag;

	@DatabaseField
	private Integer recordID;

	@DatabaseField
	private Long uptime;

	@DatabaseField
	private String source;

	@DatabaseField
	private Double x;

	@DatabaseField
	private Double y;

	@DatabaseField
	private Integer abnormal;

	protected List<Rule> defRules;

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public Long getMeasureTime() {
		return measureTime;
	}

	public void setMeasureTime(Long measureTime) {
		this.measureTime = measureTime;
	}

	public String getMeasureTimeHelp() {
		return measureTimeHelp;
	}

	public void setMeasureTimeHelp(String measureTimeHelp) {
		this.measureTimeHelp = measureTimeHelp;
	}

	public String getMeasureUID() {
		return measureUID;
	}

	public void setMeasureUID(String measureUID) {
		this.measureUID = measureUID;
	}

	public String getReadme() {
		return readme;
	}

	public void setReadme(String readme) {
		this.readme = readme;
	}

	public Integer getStateFlag() {
		return stateFlag;
	}

	public void setStateFlag(Integer stateFlag) {
		this.stateFlag = stateFlag;
	}

	public Integer getRecordID() {
		return recordID;
	}

	public void setRecordID(Integer recordID) {
		this.recordID = recordID;
	}

	public Long getUptime() {
		return uptime;
	}

	public void setUptime(Long uptime) {
		this.uptime = uptime;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public Double getX() {
		return x;
	}

	public void setX(Double x) {
		this.x = x;
	}

	public Double getY() {
		return y;
	}

	public void setY(Double y) {
		this.y = y;
	}

	public Integer getAbnormal() {
		return abnormal;
	}

	public void setAbnormal(Integer abnormal) {
		this.abnormal = abnormal;
	}

	/**
	 * 根据规则判断测量数据是否为健康状态，一般情况下健康含理想，正常。
	 * 
	 * @author ChenJunQi. 2014年9月11日
	 * 
	 * @return boolean
	 */
	public abstract boolean isHealthState();

	public boolean isSameRecord(Object o) {
		BaseMeasureData item2 = (BaseMeasureData) o;
		if (!this.getMeasureUID().equals(item2.getMeasureUID())) {
			return false;
		}
		return true;
	}

	@Override
	public void cloneFrom(Object o) {
		BaseMeasureData item2 = (BaseMeasureData) o;
		this.setMeasureUID(item2.getMeasureUID());
		super.cloneFrom(o);
	}

	/**
	 * 
	 * @author ChenJunQi. 2014年9月11日
	 * 
	 *         XXX 理论上不允许直接调用，目前暂时开放该权限，待子类Util完善后关闭将权限设置为受保护的方法
	 * 
	 */
	public static class Util {

		public static String createMeasureID() {

			Random random = new Random();
			int rand = random.nextInt(899999);
			rand = rand + 100000;
			long milliSecond = System.currentTimeMillis();
			String datestring = TimeUtils.getTime(milliSecond,
					TimeUtils.YYYYMMDD_HHMMSS);
			return String.format("%s%d", datestring, rand);
		}

		public static Long parseTimeFromMeasureUID(String uid) {
			return TimeUtils.getMillisecondDate(getTimeFromMeasureUID(uid));
		}

		public static String getTimeFromMeasureUID(String uid) {
			return uid.substring(0, 14);
		}

		public static Long createMeasureTime() {
			return System.currentTimeMillis() / 1000;
		}
	}

}

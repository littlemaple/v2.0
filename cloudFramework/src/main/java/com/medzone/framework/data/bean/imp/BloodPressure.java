package com.medzone.framework.data.bean.imp;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.j256.ormlite.field.DatabaseField;

/**
 * 
 * @author ChenJunQi.
 * 
 */
public class BloodPressure extends BaseMeasureData {

	/**
	 * 这个值取决于服务端newrules表对不同模块的type的定义
	 */
	public static final int BLOODPRESSURE_ID = 1;
	public static final String BLOODPRESSURE_TAG = "bp";
	public static final String BLOODPRESSURE_UNIT_KPA = "kpa";
	public static final String BLOODPRESSURE_UNIT_MMHG = "mmHg";
	public static final String BLOODPRESSURE_UNIT_RATE = "bpm";

	private static final long serialVersionUID = 1L;

	public static final String NAME_FIELD_HIGH = "value1";
	public static final String NAME_FIELD_LOW = "value2";
	public static final String NAME_FIELD_RATE = "value3";

	public static final int PRESSURE_STATE_LOW = 1;
	public static final int PRESSURE_STATE_IDEAL = 2;
	public static final int PRESSURE_STATE_NORMAL = 3;
	public static final int PRESSURE_STATE_NORMAL_HIGH = 4;
	public static final int PRESSURE_STATE_MILD = 5;
	public static final int PRESSURE_STATE_MODERATE = 6;
	public static final int PRESSURE_STATE_SERIOUS = 7;

	@DatabaseField
	private Float value1;

	@DatabaseField
	private Float value2;

	@DatabaseField
	private Integer value3;

	public BloodPressure() {
		setType(BLOODPRESSURE_ID);
		setTag(BLOODPRESSURE_TAG);
	}

	public void setHigh(Float high) {
		this.value1 = high;
	}

	public Float getHigh() {
		return value1;
	}

	public float getHighKPA() {
		if (value1 != null) {
			return BloodPressureUtil.convertMMHG2KPA(value1.floatValue());
		}
		return BloodPressure.INVALID_ID;
	}

	public void setLow(Float low) {
		this.value2 = low;
	}

	public Float getLow() {
		return value2;
	}

	public float getLowKPA() {
		if (value2 != null) {
			return BloodPressureUtil.convertMMHG2KPA(value2.floatValue());
		}
		return BloodPressure.INVALID_ID;
	}

	public Integer getRate() {
		return value3;
	}

	public void setRate(Integer rate) {
		this.value3 = rate;
	}

	@Override
	public boolean isHealthState() {
		if (getAbnormal() == BloodPressure.PRESSURE_STATE_NORMAL
				|| getAbnormal() == BloodPressure.PRESSURE_STATE_IDEAL
				|| getAbnormal() == BloodPressure.PRESSURE_STATE_NORMAL_HIGH) {
			return true;
		} else {
			return false;
		}
	}

	public String getPressureUnit(boolean isKpa) {
		if (isKpa) {
			return BLOODPRESSURE_UNIT_KPA;
		} else {
			return BLOODPRESSURE_UNIT_MMHG;
		}
	}

	public String getRateUnit() {
		return BLOODPRESSURE_UNIT_RATE;
	}

	@Override
	public boolean isSameRecord(Object o) {
		BloodPressure tmp = (BloodPressure) o;
		if (this.getMeasureUID().equals(tmp.getMeasureUID())) {
			return true;
		}
		return super.isSameRecord(o);
	}

	@Override
	public List<Rule> getRulesCollects() {
		if (defRules == null) {
			defRules = new ArrayList<Rule>();

			int type = BloodPressure.BLOODPRESSURE_ID;
			float[] min1s = new float[] { 0, 0, 90, 120, 0, 130, 0, 140, 0, 0,
					160, 0, 180 };
			float[] max1s = new float[] { 89, 89, 119, 129, 119, 139, 129, 159,
					139, 159, 179, 0, 0 };

			float[] min2s = new float[] { 0, 60, 0, 0, 80, 0, 85, 0, 90, 100,
					0, 110, 0 };
			float[] max2s = new float[] { 59, 79, 79, 84, 84, 89, 89, 99, 99,
					109, 109, 0, 109 };
			String[] results = new String[] { "偏低", "理想", "理想", "正常", "正常",
					"正常偏高", "正常偏高", "轻度", "轻度", "中度", "中度", "重度", "重度" };
			int[] states = new int[] { 1, 2, 2, 3, 3, 4, 4, 5, 5, 6, 6, 7, 7 };
			for (int i = 0; i < results.length; i++) {
				Rule r = new Rule();
				r.setMin1(min1s[i]);
				r.setMax1(max1s[i]);
				r.setMin2(min2s[i]);
				r.setMax2(max2s[i]);
				r.setType(type);
				r.setResult(results[i]);
				r.setState(states[i]);
				defRules.add(r);
			}
		}
		return defRules;
	}

	public static List<BloodPressure> createBloodPressureList(
			JSONArray jsonArray, Integer accountID) {
		List<BloodPressure> retList = new ArrayList<BloodPressure>();
		try {
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject jsonObj = jsonArray.getJSONObject(i);
				retList.add(createBloodPressure(jsonObj, accountID));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return retList;
	}

	public static BloodPressure createBloodPressure(JSONObject jo,
			Integer accountID) {

		BloodPressure bp = new BloodPressure();
		bp.setAccountID(accountID);

		return parse(jo, bp);
	}

	public static BloodPressure updateBloodPressure(JSONObject jo,
			BloodPressure bp) {

		return parse(jo, bp);
	}

	private static BloodPressure parse(JSONObject jo, BloodPressure bp) {
		try {
			if (jo.has("recordid") && !jo.isNull("recordid")) {
				bp.setRecordID(jo.getInt("recordid"));
			}
			if (jo.has("measureuid") && !jo.isNull("measureuid")) {

				String uid = jo.getString("measureuid");
				bp.setMeasureUID(uid);
				bp.setMeasureTime(Util.parseTimeFromMeasureUID(uid));
				bp.setMeasureTimeHelp(Util.getTimeFromMeasureUID(uid));

			}
			if (jo.has("source") && !jo.isNull("source")) {
				bp.setSource(jo.getString("source"));
			}
			if (jo.has("readme") && !jo.isNull("readme")) {
				bp.setReadme(jo.getString("readme"));
			}
			if (jo.has("x") && !jo.isNull("x")) {
				bp.setX(jo.getDouble("x"));
			}
			if (jo.has("y") && !jo.isNull("y")) {
				bp.setY(jo.getDouble("y"));
			}
			if (jo.has("state") && !jo.isNull("state")) {
				bp.setAbnormal(jo.getInt("state"));
			}
			if (jo.has("uptime") && !jo.isNull("uptime")) {
				bp.setUptime(jo.getLong("uptime"));
			}
			if (jo.has("value1") && !jo.isNull("value1")) {
				bp.setHigh((float) jo.getDouble("value1"));
			}
			if (jo.has("value2") && !jo.isNull("value2")) {
				bp.setLow((float) jo.getDouble("value2"));
			}
			if (jo.has("value3") && !jo.isNull("value3")) {
				bp.setRate(jo.getInt("value3"));
			}
			bp.setStateFlag(FLAG_SYNCHRONIZED);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return bp;
	}

	public static class BloodPressureUtil extends Util {

		public static float convertMMHG2KPA(String str) {
			double ret = Double.valueOf(str);

			BigDecimal mData = new BigDecimal(ret).setScale(1,
					BigDecimal.ROUND_HALF_UP);

			return mData.floatValue();
		}

		public static float convertMMHG2KPA(float mmHg) {

			double ret = mmHg / 0.75 / 10;

			BigDecimal mData = new BigDecimal(ret).setScale(1,
					BigDecimal.ROUND_HALF_UP);

			return mData.floatValue();
		}

		public static float convertKPA2MMHG(float kpa) {
			return (float) (7.5 * kpa);
		}
	}

}

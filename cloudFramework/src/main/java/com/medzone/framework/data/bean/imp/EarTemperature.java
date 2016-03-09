package com.medzone.framework.data.bean.imp;

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
public class EarTemperature extends BaseMeasureData {

	/**
	 * 这个值取决于服务端newrules表对不同模块的type的定义
	 */
	public static final int EARTEMPERATURE_ID = 3;
	public static final String EARTEMPERATURE_TAG = "et";

	/**
	 */
	private static final long serialVersionUID = 197958058795345165L;

	public static final String NAME_FIELD_TEMPERATURE = "value1";

	public static final int TEMPERATURE_STATE_LOW = 1;
	public static final int TEMPERATURE_STATE_NORMAL = 2;
	public static final int TEMPERATURE_STATE_FEVER = 3;
	public static final int TEMPERATURE_STATE_HIGH_FEVER = 4;

	@DatabaseField
	private Float value1;// temperature

	@DatabaseField
	private Integer value2;// empty

	@DatabaseField
	private Integer value3;// empty

	public Float getTemperature() {
		return value1;
	}

	public void setTemperature(Float temperature) {
		this.value1 = temperature;
	}

	public EarTemperature() {
		setType(EARTEMPERATURE_ID);
		setTag(EARTEMPERATURE_TAG);
	}

	public static List<EarTemperature> createEarTemperatureList(
			JSONArray jsonArray, Integer accountID) {
		List<EarTemperature> retList = new ArrayList<EarTemperature>();
		try {
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject jsonObj = jsonArray.getJSONObject(i);
				retList.add(createEarTemperature(jsonObj, accountID));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return retList;

	}

	public static EarTemperature createEarTemperature(JSONObject jo,
			Integer accountID) {

		EarTemperature et = new EarTemperature();
		et.setAccountID(accountID);

		return parse(jo, et);
	}

	public static EarTemperature updateEarTemperature(JSONObject jo,
			EarTemperature et) {

		return parse(jo, et);
	}

	private static EarTemperature parse(JSONObject jo, EarTemperature et) {

		try {
			if (jo.has("recordid") && !jo.isNull("recordid")) {
				et.setRecordID(jo.getInt("recordid"));
			}
			if (jo.has("measureuid") && !jo.isNull("measureuid")) {

				String uid = jo.getString("measureuid");
				et.setMeasureUID(uid);
				et.setMeasureTime(Util.parseTimeFromMeasureUID(uid));
				et.setMeasureTimeHelp(Util.getTimeFromMeasureUID(uid));
			}
			if (jo.has("source") && !jo.isNull("source")) {
				et.setSource(jo.getString("source"));
			}
			if (jo.has("readme") && !jo.isNull("readme")) {
				et.setReadme(jo.getString("readme"));
			}
			if (jo.has("x") && !jo.isNull("x")) {
				et.setX(jo.getDouble("x"));
			}
			if (jo.has("y") && !jo.isNull("y")) {
				et.setY(jo.getDouble("y"));
			}
			if (jo.has("state") && !jo.isNull("state")) {
				et.setAbnormal(jo.getInt("state"));
			}
			if (jo.has("uptime") && !jo.isNull("uptime")) {
				et.setUptime(jo.getLong("uptime"));
			}
			et.setTemperature((float) jo.getDouble("value1"));
			et.setStateFlag(FLAG_SYNCHRONIZED);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return et;
	}

	@Override
	public boolean isSameRecord(Object o) {
		EarTemperature tmp = (EarTemperature) o;
		if (this.getMeasureUID().equals(tmp.getMeasureUID())) {
			return true;
		}
		return super.isSameRecord(o);
	}

	@Override
	public List<Rule> getRulesCollects() {
		if (defRules == null) {
			defRules = new ArrayList<Rule>();

			int type = EarTemperature.EARTEMPERATURE_ID;
			float[] min1s = new float[] { 0f, 36f, 37.3f, 39.1f };
			float[] max1s = new float[] { 35.9f, 37.2f, 39f, 0f };
			String[] results = new String[] { "偏低", "正常", "发热", "高热" };
			int[] states = new int[] { 1, 2, 3, 4 };
			for (int i = 0; i < results.length; i++) {
				Rule r = new Rule();
				r.setMin1(min1s[i]);
				r.setMax1(max1s[i]);
				r.setMin2(0f);
				r.setMax2(0f);
				r.setType(type);
				r.setResult(results[i]);
				r.setState(states[i]);
				defRules.add(r);
			}
		}
		return defRules;
	}

	@Override
	public boolean isHealthState() {
		if (getAbnormal() == EarTemperature.TEMPERATURE_STATE_NORMAL) {
			return true;
		}
		return false;
	}
}

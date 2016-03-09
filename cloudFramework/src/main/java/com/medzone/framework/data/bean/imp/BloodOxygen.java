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
public class BloodOxygen extends BaseMeasureData {

	/**
	 * 这个值取决于服务端newrules表对不同模块的type的定义
	 */
	public static final int BLOODOXYGEN_ID = 2;
	public static final String BLOODOXYGEN_TAG = "oxy";
	/**
	 */
	private static final long serialVersionUID = -8507810150844009865L;

	public static final String NAME_FIELD_OXYGEN = "value1";
	public static final String NAME_FIELD_RATE = "value2";

	public static final int OXYGEN_STATE_IDEAL = 1;
	public static final int OXYGEN_STATE_MISSING = 2;
	public static final int OXYGEN_STATE_LOW = 3;

	@DatabaseField
	private Integer value1;// oxygen

	@DatabaseField
	private Integer value2;// rate

	@DatabaseField
	private Integer value3;// empty

	public Integer getOxygen() {
		return value1;
	}

	public void setOxygen(Integer oxy) {
		this.value1 = oxy;
	}

	public Integer getRate() {
		return value2;
	}

	public void setRate(Integer rate) {
		this.value2 = rate;
	}

	public Integer getValue3() {
		return value3;
	}

	public void setValue3(Integer empty) {
		this.value3 = empty;
	}

	public BloodOxygen() {
		setType(BLOODOXYGEN_ID);
		setTag(BLOODOXYGEN_TAG);
	}

	public static List<BloodOxygen> createBloodOxygenList(JSONArray jsonArray,
			Integer accountID) {
		List<BloodOxygen> retList = new ArrayList<BloodOxygen>();
		try {
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject jsonObj = jsonArray.getJSONObject(i);
				retList.add(createBloodOxygen(jsonObj, accountID));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return retList;

	}

	public static BloodOxygen createBloodOxygen(JSONObject jo, Integer accountID) {

		BloodOxygen bo = new BloodOxygen();
		bo.setAccountID(accountID);

		return parse(jo, bo);
	}

	public static BloodOxygen updateBloodOxygen(JSONObject jo, BloodOxygen bo) {

		return parse(jo, bo);
	}

	private static BloodOxygen parse(JSONObject jo, BloodOxygen bo) {
		try {
			if (jo.has("recordid") && !jo.isNull("recordid"))
				bo.setRecordID(jo.getInt("recordid"));
			if (jo.has("measureuid") && !jo.isNull("measureuid")) {

				String uid = jo.getString("measureuid");
				bo.setMeasureUID(uid);
				bo.setMeasureTime(Util.parseTimeFromMeasureUID(uid));
				bo.setMeasureTimeHelp(Util.getTimeFromMeasureUID(uid));
			}
			if (jo.has("source") && !jo.isNull("source")) {
				bo.setSource(jo.getString("source"));
			}
			if (jo.has("readme") && !jo.isNull("readme")) {
				bo.setReadme(jo.getString("readme"));
			}
			if (jo.has("x") && !jo.isNull("x")) {
				bo.setX(jo.getDouble("x"));
			}
			if (jo.has("y") && !jo.isNull("y")) {
				bo.setY(jo.getDouble("y"));
			}
			if (jo.has("state") && !jo.isNull("state")) {
				bo.setAbnormal(jo.getInt("state"));
			}
			if (jo.has("uptime") && !jo.isNull("uptime")) {
				bo.setUptime(jo.getLong("uptime"));
			}
			if (jo.has("value1") && !jo.isNull("value1")) {
				bo.setOxygen(jo.getInt("value1"));
			}
			if (jo.has("value2") && !jo.isNull("value2")) {
				bo.setRate(jo.getInt("value2"));
			}
			bo.setStateFlag(FLAG_SYNCHRONIZED);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return bo;
	}

	@Override
	public boolean isSameRecord(Object o) {
		BloodOxygen tmp = (BloodOxygen) o;
		if (this.getMeasureUID().equals(tmp.getMeasureUID())) {
			return true;
		}
		return super.isSameRecord(o);
	}

	@Override
	public List<Rule> getRulesCollects() {

		if (defRules == null) {
			defRules = new ArrayList<Rule>();
			int type = BloodOxygen.BLOODOXYGEN_ID;
			float[] min1s = new float[] { 95f, 90f, 0f };
			float[] max1s = new float[] { 100f, 94f, 89f };
			String[] results = new String[] { "血氧正常", "氧失饱和状态", "低氧血症" };
			int[] states = new int[] { 1, 2, 3 };
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
		if (getAbnormal() == BloodOxygen.OXYGEN_STATE_IDEAL) {
			return true;
		}
		return false;
	}
}

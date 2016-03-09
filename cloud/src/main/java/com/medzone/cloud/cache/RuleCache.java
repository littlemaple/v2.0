package com.medzone.cloud.cache;

import java.util.List;

import com.medzone.framework.data.bean.imp.BaseMeasureData;
import com.medzone.framework.data.bean.imp.BloodOxygen;
import com.medzone.framework.data.bean.imp.BloodPressure;
import com.medzone.framework.data.bean.imp.EarTemperature;
import com.medzone.framework.data.bean.imp.Rule;
import com.medzone.framework.data.navigation.LongStepable;
import com.medzone.framework.data.navigation.Paging;
import com.medzone.framework.module.IRuleDetails;

public class RuleCache extends AbstractPagingListCache<Rule, LongStepable> {

	/**
	 * 
	 */

	@Override
	public List<Rule> read() {
		// PlaceHolder
		return null;
	}

	public void firstInitCache() {
		// --血压规则库
		// INSERT INTO `rule`(`type`,`min1`, `max1`, `min2`, `max2`,
		// `result`,`state`) VALUES (1,0,89,0,59,'偏低',1);
		// INSERT INTO `rule`(`type`,`min1`, `max1`, `min2`, `max2`,
		// `result`,`state`) VALUES (1,0,89,60,79,'理想',2);
		// INSERT INTO `rule`(`type`,`min1`, `max1`, `min2`, `max2`,
		// `result`,`state`) VALUES (1,90,119,0,79,'理想',2);
		// INSERT INTO `rule`(`type`,`min1`, `max1`, `min2`, `max2`,
		// `result`,`state`) VALUES (1,120,129,0,84,'正常',3);
		// INSERT INTO `rule`(`type`,`min1`, `max1`, `min2`, `max2`,
		// `result`,`state`) VALUES (1,0,119,80,84,'正常',3);
		// INSERT INTO `rule`(`type`,`min1`, `max1`, `min2`, `max2`,
		// `result`,`state`) VALUES (1,130,139,0,89,'正常偏高',4);
		// INSERT INTO `rule`(`type`,`min1`, `max1`, `min2`, `max2`,
		// `result`,`state`) VALUES (1,0,129,85,89,'正常偏高',4);
		// INSERT INTO `rule`(`type`,`min1`, `max1`, `min2`, `max2`,
		// `result`,`state`) VALUES (1,140,159,0,99,'轻度',5);
		// INSERT INTO `rule`(`type`,`min1`, `max1`, `min2`, `max2`,
		// `result`,`state`) VALUES (1,0,139,90,99,'轻度',5);
		// INSERT INTO `rule`(`type`,`min1`, `max1`, `min2`, `max2`,
		// `result`,`state`) VALUES (1,0,159,100,109,'中度',6);
		// INSERT INTO `rule`(`type`,`min1`, `max1`, `min2`, `max2`,
		// `result`,`state`) VALUES (1,160,179,0,109,'中度',6);
		// INSERT INTO `rule`(`type`,`min1`, `max1`, `min2`, `max2`,
		// `result`,`state`) VALUES (1,0,0,110,0,'重度',7);
		// INSERT INTO `rule`(`type`,`min1`, `max1`, `min2`, `max2`,
		// `result`,`state`) VALUES (1,180,0,0,109,'重度',7);
		// --血氧规则库
		// INSERT INTO `rule`(`type`,`min1`, `max1`,`result`,`state`) VALUES
		// (2,95,100,'血氧正常',1);
		// INSERT INTO `rule`(`type`,`min1`, `max1`,`result`,`state`) VALUES
		// (2,90,94,'氧失饱和状态',2);
		// INSERT INTO `rule`(`type`,`min1`, `max1`,`result`,`state`) VALUES
		// (2,0,89,'低氧血症',3);
		// --耳温规则库
		// INSERT INTO `rule`(`type`,`min1`, `max1`,`result`,`state`) VALUES
		// (3,0,35.9,'偏低',1);
		// INSERT INTO `rule`(`type`,`min1`, `max1`,`result`,`state`) VALUES
		// (3,36,37.2,'正常',2);
		// INSERT INTO `rule`(`type`,`min1`, `max1`,`result`,`state`) VALUES
		// (3,37.3,39,'发热',3);
		// INSERT INTO `rule`(`type`,`min1`, `max1`,`result`,`state`) VALUES
		// (3,39.1,0,'高热',4);
	}

	// private Map<String, Object> initParameters(Account account,
	// BaseMeasureData data) {
	// float mAge = TimeUtils.getGapYearByBirthday(account.getBirthday());
	// int mType = data.getType();
	// int mBmiType = bmiType(account);
	// int mSick = account.getSick() & sickMask(mType);
	// int mAgeType = ageType(mAge, mType, mSick);
	//
	// Map<String, Object> parameters = new HashMap<String, Object>();
	// parameters.put(Rule.NAME_FIELD_TYPE, mType);
	// switch (mType) {
	//
	// case BloodPressure.BLOODPRESSURE_ID:
	// parameters.put(Rule.NAME_FIELD_SICK, mSick);
	// if (mSick > 0) {
	// parameters.put(Rule.NAME_FIELD_AGE, mAgeType == 1 ? mAgeType
	// : 0);
	// } else {
	// parameters.put(Rule.NAME_FIELD_AGE, mAgeType);
	// parameters.put(Rule.NAME_FIELD_BMI, mBmiType);
	// }
	// break;
	// case BloodOxygen.BLOODOXYGEN_ID:
	// parameters.put(Rule.NAME_FIELD_SICK, mSick);
	// if (mSick == 0) {
	// parameters.put(Rule.NAME_FIELD_BMI, mBmiType);
	// }
	// break;
	// case EarTemperature.EARTEMPERATURE_ID:
	// parameters.put(Rule.NAME_FIELD_SICK, mSick);
	// parameters.put(Rule.NAME_FIELD_AGE, mAgeType);
	// break;
	// // TODO 更多如血糖等等
	// default:
	// break;
	// }
	//
	// return parameters;
	// }
	//
	// private List<Rule> roughingRead(Map<String, Object> parameters,
	// BaseMeasureData data) {
	//
	// // Modified it,when new rule's type added.
	// final int minTypeValue = 0;
	// final int maxTypeValue = 3;
	//
	// try {
	// Dao<Rule, Long> dao = DatabaseHelper.getInstance().getDao(
	// Rule.class);
	// QueryBuilder<Rule, Long> queryBuilder = dao.queryBuilder();
	// Where<Rule, Long> where = queryBuilder.where();
	// where.between(Rule.NAME_FIELD_TYPE, minTypeValue, maxTypeValue);
	// for (Map.Entry<String, Object> entry : parameters.entrySet()) {
	// where.and();
	// where.eq(entry.getKey(), entry.getValue());
	// }
	// PreparedQuery<Rule> preparedQuery = queryBuilder.prepare();
	//
	// return dao.query(preparedQuery);
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// return null;
	// }

	public Rule readRuleByData(/* Account account, */BaseMeasureData data) {

		boolean matched = false;
		int mType = data.getType();
		// Map<String, Object> parameters = initParameters(account, data);
		// List<Rule> roughingList = roughingRead(parameters, data);

		IRuleDetails ird = (IRuleDetails) data;

		for (Rule rule : ird.getRulesCollects()) {
			double min1 = rule.getMin1();
			double max1 = rule.getMax1();
			double min2 = rule.getMin2();
			double max2 = rule.getMax2();

			switch (mType) {
			case BloodPressure.BLOODPRESSURE_ID:
				float bpHigh = ((BloodPressure) data).getHigh();
				float bpLow = ((BloodPressure) data).getLow();
				// int bpRate = ((BloodPressureBean) data).getRate();

				if (min1 == 0) {
					matched = bpHigh <= max1;
				} else if (max1 == 0) {
					matched = bpHigh >= min1;
				} else {
					matched = bpHigh >= min1 && bpHigh <= max1;
				}

				if (min2 == 0) {
					matched = matched && bpLow <= max2;
				} else if (max2 == 0) {
					matched = matched || bpLow >= min2;
				} else {
					matched = matched && bpLow >= min2 && bpLow <= max2;
				}
				break;
			case BloodOxygen.BLOODOXYGEN_ID:
				int oxygen = ((BloodOxygen) data).getOxygen().intValue();
				// int oxRate = ((OxygenBean) data).getRate();

				if (min1 == 0) {
					matched = oxygen <= max1;
				} else if (max1 == 0) {
					matched = oxygen >= min1;
				} else {
					matched = oxygen >= min1 && oxygen <= max1;
				}

				break;
			case EarTemperature.EARTEMPERATURE_ID:
				float temperture = ((EarTemperature) data).getTemperature();

				if (min1 == 0) {
					matched = temperture <= max1;
				} else if (max1 == 0) {
					matched = temperture >= min1;
				} else {
					matched = temperture >= min1 && temperture <= max1;
				}
				break;
			default:
				break;
			}

			if (matched)
				return rule;
		}

		return null;
	}

	@Override
	public List<Rule> read(Paging<LongStepable> paging) {
		// TODO Auto-generated method stub
		return null;
	}

	/********************************************/
	// 工具类
	/********************************************/

	// TODO remove to healthUtils.class
	// private int bmiType(Account account) {
	// double tall = account.getTall();
	// double weight = account.getWeight();
	// double bmi = 0;
	//
	// // convert to unit: M
	// if (tall > 10) {
	// tall /= 100;
	// }
	// bmi = weight / Math.pow(max((double) 1, tall), 2);
	// return bmi >= 28 ? 1 : 0;
	// }
	//
	// private double max(double a, double b) {
	// return a > b ? a : b;
	// }
	//
	// private int sickMask(int type) {
	// switch (type) {
	// case 1:
	// return 0x0f; // 1|2|4|8
	// case 2:
	// return 0x3b; // 1|2|8|16|32
	// case 3:
	// return 0x3c0; // 64|128|256|512
	// default:
	// return 0x7fffffff;
	// }
	// }
	//
	// private int ageType(float mAge, int mType, int sick1) {
	// switch (mType) {
	// case 1:
	// if (mAge >= 80) {
	// return 2;
	// } else if (mAge >= 65) {
	// return 1;
	// }
	// break;
	// case 2:
	// break;
	// case 3:
	// // 恶性肿瘤 256，风湿类：512
	// if ((sick1 > 0) && ((sick1 & 768) == 0)) {
	// return mAge >= 17 ? 7 : 6;
	// } else if (sick1 == 0) {
	// if (mAge >= 65) {
	// return 5;
	// } else if (mAge >= 17) {
	// return 4;
	// } else if (mAge >= 6) {
	// return 3;
	// } else if (mAge >= 0.33) {
	// return 2;
	// } else {
	// return 1;
	// }
	// }
	// break;
	// }
	// return 0;
	// }
}

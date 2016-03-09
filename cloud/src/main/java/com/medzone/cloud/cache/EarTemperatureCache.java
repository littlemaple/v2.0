package com.medzone.cloud.cache;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.PreparedDelete;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;
import com.medzone.cloud.Constants;
import com.medzone.cloud.data.CurrentAccountManager;
import com.medzone.cloud.database.DatabaseHelper;
import com.medzone.framework.data.bean.imp.Account;
import com.medzone.framework.data.bean.imp.EarTemperature;
import com.medzone.framework.data.navigation.LongStepable;
import com.medzone.framework.data.navigation.Paging;
import com.medzone.framework.util.TimeUtils;

public class EarTemperatureCache extends
		AbstractPagingListCache<EarTemperature, LongStepable> implements
		IDataSynchronous<EarTemperature> {

	/**
	 * 
	 */

	/**
	 * 
	 * 
	 * 
	 * Why override the method here ? The answer is that ,client don't obtain
	 * data from cache, so we could't remove or do other cache synchronous
	 * things by clone object.
	 * 
	 * @author Local_ChenJunQi
	 * @deprecated
	 * 
	 *             We suggest you to obtain from cache instead of other
	 *             operations.
	 * @param item
	 */
	@Override
	public void remove(EarTemperature item) {
		super.remove(item);
		for (EarTemperature o : list) {
			if (o == null) {
				continue;
			}
			if (o.getMeasureUID().equals(item.getMeasureUID())) {
				remove(o);
				break;
			}
		}
	}

	@Override
	public List<EarTemperature> read() {
		if (getAttachInfo().mAccount == null)
			return null;
		try {
			Dao<EarTemperature, Long> dao = DatabaseHelper.getInstance()
					.getDao(EarTemperature.class);
			QueryBuilder<EarTemperature, Long> queryBuilder = dao
					.queryBuilder();
			queryBuilder.orderBy(EarTemperature.NAME_FIELD_MEASURETIME, false);
			queryBuilder.limit(Long.valueOf(1));
			Where<EarTemperature, Long> where = queryBuilder.where();
			where.eq(EarTemperature.NAME_FIELD_ACCOUNT_ID,
					getAttachInfo().mAccount.getAccountID());
			PreparedQuery<EarTemperature> preparedQuery = queryBuilder
					.prepare();
			list = dao.query(preparedQuery);
			return list;
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return null;
	}

	public EarTemperature read(String measureUID) {
		try {
			Dao<EarTemperature, Long> dao = DatabaseHelper.getInstance()
					.getDao(EarTemperature.class);
			QueryBuilder<EarTemperature, Long> queryBuilder = dao
					.queryBuilder();
			Where<EarTemperature, Long> where = queryBuilder.where();
			where.eq(EarTemperature.NAME_FIELD_MEASUREU_ID, measureUID);
			PreparedQuery<EarTemperature> preparedQuery = queryBuilder
					.prepare();
			List<EarTemperature> list = dao.query(preparedQuery);
			if (list != null && list.size() > 0) {
				return list.get(0);
			}
			return null;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	// 读取一年中所有月份，每个月份的正常与异常次数
	public List<HashMap<String, String>> readStatListByYear(int year) {

		List<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
		GenericRawResults<String[]> rawResults;
		int accountID = attachInfo.mAccount.getAccountID();
		String sql = "SELECT D.MONTH,IFNULL(C.MEASURECOUNT,0) AS MEASURECOUNT,IFNULL(C.ABNORMALCOUNT,0) AS ABNORMALCOUNT FROM (";

		// 拼装查询指定月份语句
		String[] months;
		if (year == TimeUtils.getCurrentYear()) {
			months = new String[TimeUtils.getCurrentMonth() + 1];
		} else {
			months = new String[12];
		}
		String zeroHolder = "0";
		for (int i = 0; i < months.length; i++) {
			if (i < 9) {
				int realMonth = i + 1;
				months[i] = zeroHolder + realMonth;
			} else {
				months[i] = String.valueOf(i + 1);
			}
			if (i == 0) {
				sql += "SELECT '" + months[i] + "' AS MONTH ";
			} else {
				sql += " UNION ALL SELECT '" + months[i] + "'";
			}
		}

		sql += ") AS D LEFT OUTER JOIN (SELECT A.MEASURECOUNT, A.MONTH, IFNULL(B.ABNORMALCOUNT,0) AS ABNORMALCOUNT FROM   (SELECT COUNT(MONTH) AS MEASURECOUNT,   MONTH FROM  (  SELECT substr(measuretime , 5, 2 )  AS MONTH  FROM  EarTemperature  WHERE ACCOUNTID = %d AND  substr(measuretime,1,4) = '%d' )  GROUP BY MONTH )  AS A LEFT OUTER JOIN  (SELECT COUNT(MONTH) AS ABNORMALCOUNT, MONTH FROM (  SELECT substr(measuretime , 5, 2 )  AS MONTH , abnormal  FROM  EarTemperature   WHERE ACCOUNTID = %d AND  substr(measuretime,1,4) = '%d') WHERE abnormal !=%d GROUP BY MONTH)  AS B ON A.MONTH = B.MONTH )AS C ON D.MONTH = C.MONTH ORDER BY D.MONTH DESC";
		sql = String.format(sql, accountID, year, accountID, year,
				EarTemperature.TEMPERATURE_STATE_NORMAL);

		try {
			Dao<EarTemperature, Long> dao = DatabaseHelper.getInstance()
					.getDao(EarTemperature.class);
			rawResults = dao.queryRaw(sql);
			List<String[]> results = rawResults.getResults();
			for (int i = 0; i < results.size(); i++) {
				HashMap<String, String> map = new HashMap<String, String>();
				String[] resultArray = results.get(i);
				map.put(Constants.KEY_MONTH, resultArray[0]);
				map.put(Constants.KEY_ALL_COUNT, resultArray[1]);
				map.put(Constants.KEY_EXCEPTION_COUNT, resultArray[2]);

				list.add(map);
			}
			return list;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public List<EarTemperature> readMonthlyLimitData(Integer year, Integer month) {
		int accountID = attachInfo.mAccount.getAccountID();
		String monthStr = "0";
		if (month < 10) {
			monthStr = monthStr + month;
		} else {
			monthStr = String.valueOf(month);
		}
		List<EarTemperature> list = new ArrayList<EarTemperature>();
		GenericRawResults<String[]> rawResults;
		try {
			Dao<EarTemperature, Long> dao = DatabaseHelper.getInstance()
					.getDao(EarTemperature.class);
			String sql = "select value1,measuretime, abnormal,readme from   ( SELECT  substr(measureTime , 1, 8 ) as day,*  from EarTemperature where substr(measureTime,1,4) = '%d' AND substr(measureTime , 5, 2 ) = '%s' AND accountID=%d   order by measuretime asc) group by day ";
			sql = String.format(sql, year, monthStr, accountID);
			rawResults = dao.queryRaw(sql);
			List<String[]> abnormalResult = rawResults.getResults();
			if (abnormalResult != null) {
				int count = abnormalResult.size();
				for (int i = 0; i < count; i++) {
					String[] resultArray = abnormalResult.get(i);
					if (resultArray[0] != null) {
						EarTemperature et = new EarTemperature();
						et.setTemperature(Float.valueOf(resultArray[0]));
						et.setMeasureTime(Long.valueOf(resultArray[1]));
						et.setAbnormal(Integer.valueOf(resultArray[2]));
						et.setReadme(resultArray[3]);
						list.add(et);
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}

	// 查询指定某个月的数据
	public List<EarTemperature> readMonthlyAllData(Integer year, Integer month) {
		String monthStr = "";
		if (month < 10) {
			monthStr = "0" + month;
		} else {
			monthStr = "" + month;
		}
		int accountID = attachInfo.mAccount.getAccountID();
		List<EarTemperature> list = new ArrayList<EarTemperature>();
		GenericRawResults<String[]> rawResults;
		try {
			Dao<EarTemperature, Long> dao = DatabaseHelper.getInstance()
					.getDao(EarTemperature.class);
			String sql = "SELECT value1,measuretime, abnormal,readme,recordID,measureUID FROM ( SELECT substr(measuretime , 5, 2 ) AS month,substr(measuretime,1,4) AS year ,* FROM eartemperature WHERE accountID='%d') AS total WHERE  year ='%d' AND month  = '%s' ORDER BY measuretime DESC;";
			sql = String.format(sql, accountID, year, monthStr);

			rawResults = dao.queryRaw(sql);
			List<String[]> abnormalResult = rawResults.getResults();
			if (abnormalResult != null) {
				int count = abnormalResult.size();
				for (int i = 0; i < count; i++) {
					String[] resultArray = abnormalResult.get(i);
					if (resultArray[0] != null) {
						EarTemperature et = new EarTemperature();
						et.setTemperature(Float.valueOf(resultArray[0]));
						et.setMeasureTime(Long.valueOf(resultArray[1]));
						et.setAbnormal(Integer.valueOf(resultArray[2]));
						et.setReadme(resultArray[3]);
						if (resultArray[4] != null)
							et.setRecordID(Integer.valueOf(resultArray[4]));
						et.setMeasureUID(resultArray[5]);
						list.add(et);
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}

	public List<HashMap<String, String>> readStatListByMonth(Integer year,
			Integer month) {
		String monthStr;
		if (month < 10) {
			monthStr = "0" + month;
		} else {
			monthStr = "" + month;
		}
		try {
			Dao<EarTemperature, Long> dao = DatabaseHelper.getInstance()
					.getDao(EarTemperature.class);
			int accountId = CurrentAccountManager.getCurAccount()
					.getAccountID();
			String sql = "SELECT abnormal, count(abnormal)  FROM eartemperature WHERE SUBSTR( measuretime,1,4 ) = '%d' AND SUBSTR( measuretime,5,2 ) = '%s' AND accountId=%d  GROUP BY abnormal;";
			sql = String.format(sql, year, monthStr, accountId);

			GenericRawResults<String[]> rawResults = dao.queryRaw(sql);

			List<HashMap<String, String>> result = new ArrayList<HashMap<String, String>>();
			List<String[]> abnormalResult = null;
			try {
				abnormalResult = rawResults.getResults();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			if (abnormalResult != null) {
				int count = abnormalResult.size();
				for (int i = 0; i < count; i++) {
					String[] resultArray = abnormalResult.get(i);
					HashMap<String, String> map = new HashMap<String, String>();
					if (Integer.valueOf(resultArray[1]) < 10) {
						map.put(Constants.KEY_COUNT, "0" + resultArray[1]);
					} else {
						map.put(Constants.KEY_COUNT, resultArray[1]);
					}
					map.put(Constants.KEY_ABNORMAL, resultArray[0]);
					result.add(map);
				}
			}
			return result;

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public EarTemperature readYearMonthMaxTemperature(Integer year,
			Integer month) {
		String monthStr = "";
		if (month < 10) {
			monthStr = "0" + month;
		} else {
			monthStr = "" + month;
		}
		int accountID = attachInfo.mAccount.getAccountID();
		GenericRawResults<String[]> rawResults;
		try {
			Dao<EarTemperature, Long> dao = DatabaseHelper.getInstance()
					.getDao(EarTemperature.class);
			String sql = "SELECT MAX( value1 ) , measuretime  FROM (SELECT SUBSTR(measuretime , 5, 2 ) AS MONTH , SUBSTR(measuretime , 1, 4 ) AS YEAR , measuretime, value1 FROM eartemperature WHERE accountID ='%d' ORDER BY measuretime DESC) AS total WHERE YEAR ='%d' AND MONTH ='%s';";
			sql = String.format(sql, accountID, year, monthStr);

			rawResults = dao.queryRaw(sql);
			List<String[]> abnormalResult = rawResults.getResults();
			if (abnormalResult != null && abnormalResult.size() > 0) {
				String[] resultArray = abnormalResult.get(0);
				EarTemperature et = new EarTemperature();
				if (resultArray[0] != null) {
					et.setTemperature(Float.valueOf(resultArray[0]));
					et.setMeasureTime(Long.valueOf(resultArray[1]));
				}
				return et;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	// 获取指定年月的总测量次数
	public int readMonthMeasureCounts(int year, int month) {

		String monthStr = "";
		if (month < 10) {
			monthStr = "0" + month;
		} else {
			monthStr = String.valueOf(month);
		}

		int accountID = attachInfo.mAccount.getAccountID();

		String sql = "SELECT count(*)  FROM  ( SELECT substr(measuretime , 5, 2 ) AS month,substr(measuretime,1,4) AS year ,*   FROM EarTemperature WHERE accountID = %d ) AS TOTAL   WHERE  year ='%d' AND month  = '%s' ORDER BY measureTime DESC;";
		sql = String.format(sql, accountID, year, monthStr);

		GenericRawResults<String[]> rawResults;
		try {
			Dao<EarTemperature, Long> dao = DatabaseHelper.getInstance()
					.getDao(EarTemperature.class);
			rawResults = dao.queryRaw(sql);
			List<String[]> abnormalResult = rawResults.getResults();
			if (abnormalResult != null && abnormalResult.size() > 0) {
				String[] result = abnormalResult.get(0);
				return Integer.valueOf(result[0]);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}

	// 获取指定年月的异常测量次数 ok
	public int readMonthMeasureExceptionCounts(int year, int month) {

		String monthStr = "";
		if (month < 10) {
			monthStr = "0" + month;
		} else {
			monthStr = String.valueOf(month);
		}

		int accountID = attachInfo.mAccount.getAccountID();

		String sql = "SELECT count(*)  FROM  ( SELECT substr(measuretime , 5, 2 ) AS month,substr(measuretime,1,4) AS year ,*   FROM EarTemperature WHERE accountID = %d) AS TOTAL   WHERE  year ='%d' AND month  = '%s'AND abnormal !=%d  ORDER BY measureTime DESC;";
		sql = String.format(sql, accountID, year, monthStr,
				EarTemperature.TEMPERATURE_STATE_NORMAL);

		GenericRawResults<String[]> rawResults;
		try {
			Dao<EarTemperature, Long> dao = DatabaseHelper.getInstance()
					.getDao(EarTemperature.class);
			rawResults = dao.queryRaw(sql);
			List<String[]> abnormalResult = rawResults.getResults();
			if (abnormalResult != null && abnormalResult.size() > 0) {
				String[] result = abnormalResult.get(0);
				return Integer.valueOf(result[0]);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}

	// 删除指定id的一条数据
	public int deleteEarTemperature(String measureUID) {
		try {
			Dao<EarTemperature, Long> dao = DatabaseHelper.getInstance()
					.getDao(EarTemperature.class);
			DeleteBuilder<EarTemperature, Long> deleteBuilder = dao
					.deleteBuilder();
			Where<EarTemperature, Long> where = deleteBuilder.where();
			where.eq(EarTemperature.NAME_FIELD_MEASUREU_ID, measureUID);
			PreparedDelete<EarTemperature> preparedDelete = deleteBuilder
					.prepare();
			int result = dao.delete(preparedDelete);
			return result;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}

	// 获取第一次测量的数据的测试时间
	public String readFristMeasureTime(Account account) {
		String sql = "SELECT measuretime FROM eartemperature WHERE accountID = %d  ORDER BY measuretime ASC LIMIT 1;";
		sql = String.format(sql, account.getAccountID());
		GenericRawResults<String[]> rawResults;
		try {
			Dao<EarTemperature, Long> dao = DatabaseHelper.getInstance()
					.getDao(EarTemperature.class);
			rawResults = dao.queryRaw(sql);
			List<String[]> abnormalResult = rawResults.getResults();
			if (abnormalResult != null && abnormalResult.size() > 0) {
				String[] result = abnormalResult.get(0);
				return result[0];
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public List<EarTemperature> read(Paging<LongStepable> paging) {
		if ((paging == null) || (paging.getPageSize() < 0))
			return null;
		if (getAttachInfo().mAccount == null)
			return null;
		try {
			Dao<EarTemperature, Long> dao = DatabaseHelper.getInstance()
					.getDao(EarTemperature.class);
			QueryBuilder<EarTemperature, Long> queryBuilder = dao
					.queryBuilder();
			queryBuilder.orderBy(EarTemperature.NAME_FIELD_MEASURETIME, false);
			queryBuilder.limit((long) paging.getPageSize());
			Where<EarTemperature, Long> where = queryBuilder.where();
			where.eq(EarTemperature.NAME_FIELD_ACCOUNT_ID,
					getAttachInfo().mAccount.getAccountID());
			where.and();
			where.between(EarTemperature.NAME_FIELD_MEASURETIME,
					LongStepable.getMinValue(paging),
					LongStepable.getMaxValue(paging));
			PreparedQuery<EarTemperature> preparedQuery = queryBuilder
					.prepare();
			return dao.query(preparedQuery);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public List<EarTemperature> readUploadData() {

		try {
			Dao<EarTemperature, Long> dao = DatabaseHelper.getInstance()
					.getDao(EarTemperature.class);
			QueryBuilder<EarTemperature, Long> queryBuilder = dao
					.queryBuilder();
			queryBuilder.orderBy(EarTemperature.NAME_FIELD_MEASURETIME, false);
			Where<EarTemperature, Long> where = queryBuilder.where();
			where.eq(EarTemperature.NAME_FIELD_STATE_FLAG, 0);
			PreparedQuery<EarTemperature> preparedQuery = queryBuilder
					.prepare();
			List<EarTemperature> list = dao.query(preparedQuery);
			return list;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public String readUploadDataJSON() {

		List<EarTemperature> list = readUploadData();

		return convert2JSON(list);
	}

	public String convert2JSON(List<EarTemperature> list) {
		if (list == null)
			return null;
		if (list.size() == 0)
			return null;
		JSONArray jsonArray = new JSONArray();
		try {
			for (EarTemperature et : list) {
				JSONObject jsonObject = new JSONObject();

				jsonObject.put("measureuid", et.getMeasureUID());
				jsonObject.put("source", et.getSource());
				jsonObject.put("readme", et.getReadme());
				jsonObject.put("x", et.getX());
				jsonObject.put("y", et.getY());
				jsonObject.put("value1", et.getTemperature());

				jsonArray.put(jsonObject);
			}
			return jsonArray.toString();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

}

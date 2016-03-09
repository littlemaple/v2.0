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
import com.medzone.framework.Log;
import com.medzone.framework.data.bean.imp.BloodOxygen;
import com.medzone.framework.data.navigation.LongStepable;
import com.medzone.framework.data.navigation.Paging;
import com.medzone.framework.util.TimeUtils;

public class BloodOxgenCache extends
		AbstractPagingListCache<BloodOxygen, LongStepable> implements
		IDataSynchronous<BloodOxygen> {

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
	public void remove(BloodOxygen item) {
		super.remove(item);
		for (BloodOxygen o : list) {
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
	public int delete(BloodOxygen item) {
		int state = super.delete(item);
		if (state != 0) {
			remove(item);
			Log.e("Paging#remove:" + item.getMeasureUID());
			for (int i = 0; i < size(); i++) {
				try {
					Log.w("Paging#list:" + get(i).getMeasureUID());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return state;
	}

	@Override
	public List<BloodOxygen> read() {
		if (getAttachInfo().mAccount == null)
			return null;
		try {
			Dao<BloodOxygen, Long> dao = DatabaseHelper.getInstance().getDao(
					BloodOxygen.class);
			QueryBuilder<BloodOxygen, Long> queryBuilder = dao.queryBuilder();
			queryBuilder.orderBy(BloodOxygen.NAME_FIELD_MEASURETIME, false);
			queryBuilder.limit(Long.valueOf(1));
			Where<BloodOxygen, Long> where = queryBuilder.where();
			where.eq(BloodOxygen.NAME_FIELD_ACCOUNT_ID,
					getAttachInfo().mAccount.getAccountID());
			PreparedQuery<BloodOxygen> preparedQuery = queryBuilder.prepare();
			list = dao.query(preparedQuery);
			return list;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public BloodOxygen read(String measureUID) {
		try {
			Dao<BloodOxygen, Long> dao = DatabaseHelper.getInstance().getDao(
					BloodOxygen.class);
			QueryBuilder<BloodOxygen, Long> queryBuilder = dao.queryBuilder();
			Where<BloodOxygen, Long> where = queryBuilder.where();
			where.eq(BloodOxygen.NAME_FIELD_MEASUREU_ID, measureUID);
			PreparedQuery<BloodOxygen> preparedQuery = queryBuilder.prepare();
			List<BloodOxygen> list = dao.query(preparedQuery);
			if (list != null && list.size() > 0) {
				return list.get(0);
			}
			return null;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public List<BloodOxygen> read(Paging<LongStepable> paging) {
		if ((paging == null) || (paging.getPageSize() < 0))
			return null;
		if (getAttachInfo().mAccount == null)
			return null;
		try {
			Dao<BloodOxygen, Long> dao = DatabaseHelper.getInstance().getDao(
					BloodOxygen.class);
			QueryBuilder<BloodOxygen, Long> queryBuilder = dao.queryBuilder();
			queryBuilder.orderBy(BloodOxygen.NAME_FIELD_MEASURETIME, false);
			queryBuilder.limit((long) paging.getPageSize());
			Where<BloodOxygen, Long> where = queryBuilder.where();
			where.eq(BloodOxygen.NAME_FIELD_ACCOUNT_ID,
					getAttachInfo().mAccount.getAccountID());
			where.and();
			where.between(BloodOxygen.NAME_FIELD_MEASURETIME,
					LongStepable.getMinValue(paging),
					LongStepable.getMaxValue(paging));
			PreparedQuery<BloodOxygen> preparedQuery = queryBuilder.prepare();

			List<BloodOxygen> list = dao.query(preparedQuery);

			return list;
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

		sql += ") AS D LEFT OUTER JOIN (SELECT A.MEASURECOUNT, A.MONTH, IFNULL(B.ABNORMALCOUNT,0) AS ABNORMALCOUNT FROM   (SELECT COUNT(MONTH) AS MEASURECOUNT,   MONTH FROM  (  SELECT substr(measureTimeHelp , 5, 2 )  AS MONTH  FROM  BLOODOXYGEN  WHERE ACCOUNTID = %d AND  substr(measureTimeHelp,1,4) = '%d' )  GROUP BY MONTH )  AS A LEFT OUTER JOIN  (SELECT COUNT(MONTH) AS ABNORMALCOUNT, MONTH FROM (  SELECT substr(measureTimeHelp , 5, 2 )  AS MONTH , abnormal  FROM  BLOODOXYGEN   WHERE ACCOUNTID = %d AND  substr(measureTimeHelp,1,4) = '%d') WHERE abnormal !=%d GROUP BY MONTH)  AS B ON A.MONTH = B.MONTH )AS C ON D.MONTH = C.MONTH ORDER BY D.MONTH DESC";
		sql = String.format(sql, accountID, year, accountID, year,
				BloodOxygen.OXYGEN_STATE_IDEAL);

		try {
			Dao<BloodOxygen, Long> dao = DatabaseHelper.getInstance().getDao(
					BloodOxygen.class);
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

	// 读取本月中每天最后一条的所有数据
	public List<BloodOxygen> readMonthlyLimitData(Integer year, Integer month) {
		int accountID = attachInfo.mAccount.getAccountID();
		String monthStr = "0";
		if (month < 10) {
			monthStr = monthStr + month;
		} else {
			monthStr = String.valueOf(month);
		}
		List<BloodOxygen> list = new ArrayList<BloodOxygen>();
		GenericRawResults<String[]> rawResults;
		try {
			Dao<BloodOxygen, Long> dao = DatabaseHelper.getInstance().getDao(
					BloodOxygen.class);
			String sql = "select value1, value2,measureTime, abnormal,readme from   ( SELECT  substr(measureTimeHelp , 1, 8 ) as day,*  from bloodoxygen where substr(measureTimeHelp,1,4) = '%d' AND substr(measureTimeHelp , 5, 2 ) = '%s' AND accountID=%d   order by measureTimeHelp asc) group by day;";
			sql = String.format(sql, year, monthStr, accountID);
			rawResults = dao.queryRaw(sql);
			List<String[]> abnormalResult = rawResults.getResults();
			if (abnormalResult != null) {
				int count = abnormalResult.size();
				for (int i = 0; i < count; i++) {
					String[] resultArray = abnormalResult.get(i);
					if (resultArray[0] != null) {
						BloodOxygen bo = new BloodOxygen();
						bo.setOxygen(Integer.valueOf(resultArray[0]));
						bo.setRate(Integer.valueOf(resultArray[1]));
						bo.setMeasureTime(Long.valueOf(resultArray[2]));
						bo.setAbnormal(Integer.valueOf(resultArray[3]));
						bo.setReadme(resultArray[4]);
						list.add(bo);
					}
				}
				return list;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	// 读取指定月份中 所有测量数据
	public List<BloodOxygen> readMonthlyAllData(Integer year, Integer month) {
		int accountID = attachInfo.mAccount.getAccountID();
		String monthStr = "0";
		if (month < 10) {
			monthStr = monthStr + month;
		} else {
			monthStr = String.valueOf(month);
		}
		List<BloodOxygen> list = new ArrayList<BloodOxygen>();
		GenericRawResults<String[]> rawResults;
		try {
			Dao<BloodOxygen, Long> dao = DatabaseHelper.getInstance().getDao(
					BloodOxygen.class);
			String sql = "SELECT value1,value2,measureTime, abnormal,readme,recordID,measureUID  FROM ( SELECT substr(measureTimeHelp , 5, 2 ) AS month,substr(measureTimeHelp,1,4) AS year ,value1,value2,measureTime, abnormal,readme,recordID,measureUID FROM bloodoxygen WHERE accountID='%d') AS total WHERE  year ='%d' AND month  = '%s' ORDER BY measureTime DESC;";
			sql = String.format(sql, accountID, year, monthStr);
			rawResults = dao.queryRaw(sql);
			List<String[]> abnormalResult = rawResults.getResults();
			if (abnormalResult != null) {
				int count = abnormalResult.size();
				for (int i = 0; i < count; i++) {
					String[] resultArray = abnormalResult.get(i);
					if (resultArray[0] != null) {
						BloodOxygen bo = new BloodOxygen();
						bo.setOxygen(Integer.valueOf(resultArray[0]));
						bo.setRate(Integer.valueOf(resultArray[1]));
						bo.setMeasureTime(Long.valueOf(resultArray[2]));
						bo.setAbnormal(Integer.valueOf(resultArray[3]));
						bo.setReadme(resultArray[4]);
						if (resultArray[5] != null)
							bo.setRecordID(Integer.valueOf(resultArray[5]));
						bo.setMeasureUID(resultArray[6]);
						list.add(bo);
					}
				}
				return list;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	/** 根据abnormal值统计每个abnormal等级出现的次数 */
	public List<HashMap<String, String>> readStatListByMonth(Integer year,
			Integer month) {
		String monthStr = "";
		if (month < 10) {
			monthStr = "0" + month;
		} else {
			monthStr = "" + month;
		}
		List<HashMap<String, String>> result = new ArrayList<HashMap<String, String>>();
		GenericRawResults<String[]> rawResults;
		HashMap<String, String> map;
		try {
			Dao<BloodOxygen, Long> dao = DatabaseHelper.getInstance().getDao(
					BloodOxygen.class);
			int accountId = CurrentAccountManager.getCurAccount()
					.getAccountID();
			String sql = "SELECT abnormal, count(abnormal)  FROM bloodoxygen WHERE SUBSTR( measureTimeHelp,1,4 ) = '%d' AND SUBSTR( measureTimeHelp,5,2 ) = '%s' AND accountId=%d  GROUP BY abnormal;";
			sql = String.format(sql, year, monthStr, accountId);

			rawResults = dao.queryRaw(sql);
			List<String[]> abnormalResult = rawResults.getResults();
			if (abnormalResult != null) {
				int count = abnormalResult.size();
				for (int i = 0; i < count; i++) {
					String[] resultArray = abnormalResult.get(i);
					map = new HashMap<String, String>();
					if (Integer.valueOf(resultArray[1]) < 10) {
						map.put(Constants.KEY_COUNT, "0" + resultArray[1]);
					} else {
						map.put(Constants.KEY_COUNT, resultArray[1]);
					}
					map.put(Constants.KEY_ABNORMAL, resultArray[0]);
					result.add(map);
				}
				return result;
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	// 获取指定年月的最小值
	public BloodOxygen readYearMonthMinOxygen(Integer year, Integer month) {
		String monthStr = "";
		if (month < 10) {
			monthStr = "0" + month;
		} else {
			monthStr = "" + month;
		}
		int accountID = attachInfo.mAccount.getAccountID();
		GenericRawResults<String[]> rawResults;
		try {
			Dao<BloodOxygen, Long> dao = DatabaseHelper.getInstance().getDao(
					BloodOxygen.class);
			String sql = "SELECT MIN(value1),value2 , measureTime  FROM (SELECT SUBSTR(measureTimeHelp , 5, 2 ) AS MONTH , SUBSTR(measureTimeHelp , 1, 4 ) AS YEAR , measureTime, value1,value2 FROM bloodoxygen WHERE accountID ='%d' ORDER BY measureTimeHelp DESC) AS total WHERE YEAR ='%d' AND MONTH ='%s';";
			sql = String.format(sql, accountID, year, monthStr);
			rawResults = dao.queryRaw(sql);
			List<String[]> abnormalResult = rawResults.getResults();
			if (abnormalResult != null && abnormalResult.size() > 0) {
				String[] resultArray = abnormalResult.get(0);
				BloodOxygen bo = new BloodOxygen();
				if (resultArray[0] != null) {
					bo.setOxygen(Integer.valueOf(resultArray[0]));
					bo.setRate(Integer.valueOf(resultArray[1]));
					bo.setMeasureTime(Long.valueOf(resultArray[2]));
				}
				return bo;
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

		String sql = "SELECT count(*)  FROM  ( SELECT substr(measureTimeHelp , 5, 2 ) AS month,substr(measureTimeHelp,1,4) AS year ,*   FROM bloodoxygen WHERE accountID = %d ) AS TOTAL   WHERE  year ='%d' AND month  = '%s' ORDER BY measureTimeHelp DESC;";
		sql = String.format(sql, accountID, year, monthStr);
		GenericRawResults<String[]> rawResults;
		try {
			Dao<BloodOxygen, Long> dao = DatabaseHelper.getInstance().getDao(
					BloodOxygen.class);
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

		String sql = "SELECT count(*)  FROM  ( SELECT substr(measureTimeHelp , 5, 2 ) AS month,substr(measureTimeHelp,1,4) AS year ,*   FROM bloodoxygen WHERE accountID = %d ) AS TOTAL   WHERE  year ='%d' AND month  = '%s'AND abnormal !=%d ORDER BY measureTimeHelp DESC;";
		sql = String.format(sql, accountID, year, monthStr,
				BloodOxygen.OXYGEN_STATE_IDEAL);
		GenericRawResults<String[]> rawResults;
		try {
			Dao<BloodOxygen, Long> dao = DatabaseHelper.getInstance().getDao(
					BloodOxygen.class);
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
	public int deleteBloodOxygen(String measureUID) {
		try {
			Dao<BloodOxygen, Long> dao = DatabaseHelper.getInstance().getDao(
					BloodOxygen.class);
			DeleteBuilder<BloodOxygen, Long> deleteBuilder = dao
					.deleteBuilder();
			Where<BloodOxygen, Long> where = deleteBuilder.where();
			where.eq(BloodOxygen.NAME_FIELD_MEASUREU_ID, measureUID);
			PreparedDelete<BloodOxygen> preparedDelete = deleteBuilder
					.prepare();
			int result = dao.delete(preparedDelete);
			return result;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}

	// 获取第一次测量的数据的测试时间
	public Long readFristMeasureTime() {
		String sql = "SELECT measureTime FROM bloodoxygen WHERE accountID = %d ORDER BY measureTimeHelp ASC LIMIT 1;";
		sql = String.format(sql, CurrentAccountManager.getCurAccount()
				.getAccountID());
		GenericRawResults<String[]> rawResults;
		try {
			Dao<BloodOxygen, Long> dao = DatabaseHelper.getInstance().getDao(
					BloodOxygen.class);
			rawResults = dao.queryRaw(sql);
			List<String[]> abnormalResult = rawResults.getResults();
			if (abnormalResult != null && abnormalResult.size() > 0) {
				String[] result = abnormalResult.get(0);
				return Long.valueOf(result[0]);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public List<BloodOxygen> readUploadData() {

		try {
			Dao<BloodOxygen, Long> dao = DatabaseHelper.getInstance().getDao(
					BloodOxygen.class);
			QueryBuilder<BloodOxygen, Long> queryBuilder = dao.queryBuilder();
			queryBuilder.orderBy(BloodOxygen.NAME_FIELD_MEASURETIME, false);
			Where<BloodOxygen, Long> where = queryBuilder.where();
			where.eq(BloodOxygen.NAME_FIELD_STATE_FLAG, 0);
			PreparedQuery<BloodOxygen> preparedQuery = queryBuilder.prepare();
			List<BloodOxygen> list = dao.query(preparedQuery);

			return list;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public String readUploadDataJSON() {

		List<BloodOxygen> list = readUploadData();

		return convert2JSON(list);
	}

	public String convert2JSON(List<BloodOxygen> list) {
		if (list == null)
			return null;
		if (list.size() == 0)
			return null;
		JSONArray jsonArray = new JSONArray();
		try {
			for (BloodOxygen bo : list) {
				JSONObject jsonObject = new JSONObject();

				jsonObject.put("measureuid", bo.getMeasureUID());
				jsonObject.put("source", bo.getSource());
				jsonObject.put("readme", bo.getReadme());
				jsonObject.put("x", bo.getX());
				jsonObject.put("y", bo.getY());
				jsonObject.put("value1", bo.getOxygen());
				jsonObject.put("value2", bo.getRate());

				jsonArray.put(jsonObject);
			}
			return jsonArray.toString();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}
}

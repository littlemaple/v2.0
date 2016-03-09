//package com.medzone.cloud.cache;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import com.j256.ormlite.dao.Dao;
//import com.j256.ormlite.stmt.PreparedQuery;
//import com.j256.ormlite.stmt.QueryBuilder;
//import com.j256.ormlite.stmt.Where;
//import com.medzone.cloud.database.DatabaseHelper;
//import com.medzone.framework.data.bean.imp.Setting;
//
//public class SettingCache extends AbstractDBObjectListCache<Setting> {
//
//	private static final int FALG_MASTER_SETTING = 0;
//
//	private int masterAccountId;
//
//	public SettingCache(int masterAccountId) {
//		this.masterAccountId = masterAccountId;
//	}
//
//	@Override
//	public List<Setting> read() {
//		try {
//			Dao<Setting, Long> dao = DatabaseHelper
//					.getInstance().getDao(Setting.class);
//			QueryBuilder<Setting, Long> queryBuilder = dao
//					.queryBuilder();
//			Where<Setting, Long> where = queryBuilder.where();
//			where.eq(Setting.NAME_FIELD_ACCOUNT_ID, masterAccountId);
//
//			PreparedQuery<Setting> preparedQuery = queryBuilder.prepare();
//			list = dao.query(preparedQuery);
//			return list;
//
//		} catch (java.sql.SQLException e) {
//			e.printStackTrace();
//		}
//		return null;
//	}
//
//	public Setting readMasterSetting() {
//		return list.get(FALG_MASTER_SETTING);
//	}
//
//	public List<Setting> readBoundSetting() {
//		List<Setting> boundSettingList = null;
//		for (int i = 1; i < list.size(); i++) {
//			if (boundSettingList == null) {
//				boundSettingList = new ArrayList<Setting>();
//			}
//			boundSettingList.add(list.get(i));
//		}
//		return boundSettingList;
//	}
//
//}

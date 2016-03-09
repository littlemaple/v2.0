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
//import com.medzone.framework.data.bean.imp.System;
//
//public class SystemCache extends AbstractDBObjectListCache<System> {
//
//	private int appVersion;
//
//	public SystemCache(int appVersion) {
//		this.appVersion = appVersion;
//	}
//
//	@Override
//	public List<System> read() {
//		try {
//			Dao<System, Long> dao = DatabaseHelper.getInstance().getDao(
//					System.class);
//			QueryBuilder<System, Long> queryBuilder = dao.queryBuilder();
//			Where<System, Long> where = queryBuilder.where();
//			where.eq(System.NAME_FIELD_APPVERSION, appVersion);
//
//			PreparedQuery<System> preparedQuery = queryBuilder.prepare();
//			list = dao.query(preparedQuery);
//			return list;
//
//		} catch (java.sql.SQLException e) {
//			e.printStackTrace();
//		}
//		return null;
//	}
//
//	public List<System> readBoundSystem() {
//		List<System> boundSystemList = null;
//		for (int i = 0; i < list.size(); i++) {
//			if (boundSystemList == null) {
//				boundSystemList = new ArrayList<System>();
//			}
//			boundSystemList.add(list.get(i));
//		}
//		return boundSystemList;
//	}
//
//}

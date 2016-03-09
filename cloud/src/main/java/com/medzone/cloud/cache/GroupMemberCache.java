//package com.medzone.cloud.cache;
//
//import java.util.List;
//
//import com.j256.ormlite.dao.Dao;
//import com.j256.ormlite.stmt.PreparedQuery;
//import com.j256.ormlite.stmt.QueryBuilder;
//import com.j256.ormlite.stmt.Where;
//import com.medzone.cloud.database.DatabaseHelper;
//import com.medzone.framework.data.bean.imp.GroupMember;
//
//public class GroupMemberCache extends AbstractDBObjectListCache<GroupMember> {
//
//	// Read with all group has additional account getAttachInfo()rmation
//
//	@Override
//	public List<GroupMember> read() {
//		try {
//			Dao<GroupMember, Long> dao = DatabaseHelper.getInstance().getDao(
//					GroupMember.class);
//			QueryBuilder<GroupMember, Long> queryBuilder = dao.queryBuilder();
//			Where<GroupMember, Long> where = queryBuilder.where();
//			where.eq(GroupMember.NAME_FIELD_ACCOUNT_ID,
//					getAttachInfo().mAccount.getAccountID());
//			// ToDo group id
//			// where.eq(GroupMember.NAME_FIELD_GROUP_ID,1);
//
//			PreparedQuery<GroupMember> preparedQuery = queryBuilder.prepare();
//			list = dao.query(preparedQuery);
//			return list;
//
//		} catch (java.sql.SQLException e) {
//			e.printStackTrace();
//		}
//		return null;
//	}
//
//}

package com.medzone.cloud.cache;

//public class HelpCache extends AbstractDBObjectListCache<Help> {
//
//	// FIXME 没有默认的方法，因为读取所有帮助列表的行为并不合理，需要与帐号进行结合（存在绑定设备的概念）。
//	// 只读取要被将是的帮助列表，可以只是索引。
//	@Override
//	public List<Help> read() {
//		return null;
//	}
//
//	public Help readByType(int type) {
//		try {
//			Dao<Help, Long> dao = DatabaseHelper.getInstance().getDao(
//					Help.class);
//			QueryBuilder<Help, Long> queryBuilder = dao.queryBuilder();
//			Where<Help, Long> where = queryBuilder.where();
//			where.eq(Help.NAME_FIELD_TYPE, type);
//
//			PreparedQuery<Help> preparedQuery = queryBuilder.prepare();
//			List<Help> list = dao.query(preparedQuery);
//			return list.get(0);
//
//		} catch (java.sql.SQLException e) {
//			e.printStackTrace();
//		}
//		return null;
//	}
//
// }

package com.medzone.cloud.cache;

import java.util.List;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;
import com.medzone.cloud.database.DatabaseHelper;
import com.medzone.framework.data.bean.imp.Group;

public class GroupCache extends AbstractDBObjectListCache<Group> {

	// Read with all group has additional account getAttachInfo()rmation

	/**
	 * 
	 */

	@Override
	public List<Group> read() {
		if (getAttachInfo().mAccount == null)
			return null;
		try {
			Dao<Group, Long> dao = DatabaseHelper.getInstance().getDao(
					Group.class);
			QueryBuilder<Group, Long> queryBuilder = dao.queryBuilder();
			Where<Group, Long> where = queryBuilder.where();
			where.eq(Group.NAME_FIELD_ACCOUNT_ID,
					getAttachInfo().mAccount.getAccountID());

			PreparedQuery<Group> preparedQuery = queryBuilder.prepare();
			list = dao.query(preparedQuery);
			return list;

		} catch (java.sql.SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

}

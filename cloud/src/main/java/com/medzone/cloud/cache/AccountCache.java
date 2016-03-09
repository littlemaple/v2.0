package com.medzone.cloud.cache;

import java.sql.SQLException;
import java.util.List;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;
import com.medzone.cloud.database.DatabaseHelper;
import com.medzone.framework.data.bean.imp.Account;

public class AccountCache extends AbstractDBObjectListCache<Account> {

	/**
	 * 
	 */

	@Override
	public List<Account> read() {
		if (getAttachInfo().mAccount == null)
			return null;
		try {
			Dao<Account, Long> dao = DatabaseHelper.getInstance().getDao(
					Account.class);
			QueryBuilder<Account, Long> queryBuilder = dao.queryBuilder();
			queryBuilder.limit(Long.valueOf(1));
			Where<Account, Long> where = queryBuilder.where();

			where.eq(Account.NAME_FIELD_ACCOUNT_ID,
					getAttachInfo().mAccount.getAccountID());

			PreparedQuery<Account> preparedQuery = queryBuilder.prepare();
			list = dao.query(preparedQuery);
			return list;
		} catch (SQLException e) {
			return null;
		}
	}

	private List<Account> getAllAccountBean() {
		try {
			Dao<Account, Long> dao = DatabaseHelper.getInstance().getDao(
					Account.class);
			return dao.queryForAll();
		} catch (SQLException e) {
			return null;
		}
	}

	public boolean clearHistory() {
		try {
			Dao<Account, Long> dao = DatabaseHelper.getInstance().getDao(
					Account.class);
			dao.delete(getAllAccountBean());
			return true;
		} catch (SQLException e) {
			return false;
		}
	}

	// 依据用户名读取本地的帐号记录 。
	public Account readAccountByPhone(String phone) {
		try {
			Dao<Account, Long> dao = DatabaseHelper.getInstance().getDao(
					Account.class);
			QueryBuilder<Account, Long> queryBuilder = dao.queryBuilder();
			// queryBuilder.orderBy(Account.FIELD_NAME_ID, false);
			Where<Account, Long> where = queryBuilder.where();
			where.eq(Account.NAME_FIELD_PHONE, phone);
			PreparedQuery<Account> preparedQuery = queryBuilder.prepare();
			List<Account> list = dao.query(preparedQuery);

			return list.size() > 0 ? list.get(0) : null;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	public Account readAccountByEmail(String email) {
		try {
			Dao<Account, Long> dao = DatabaseHelper.getInstance().getDao(
					Account.class);
			QueryBuilder<Account, Long> queryBuilder = dao.queryBuilder();
			// queryBuilder.orderBy(Account.FIELD_NAME_ID, false);
			Where<Account, Long> where = queryBuilder.where();
			where.eq(Account.NAME_FIELD_EMAIL, email);
			PreparedQuery<Account> preparedQuery = queryBuilder.prepare();
			List<Account> list = dao.query(preparedQuery);

			return list.size() > 0 ? list.get(0) : null;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

}

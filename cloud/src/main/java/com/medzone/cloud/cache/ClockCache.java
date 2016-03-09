package com.medzone.cloud.cache;

import java.sql.SQLException;
import java.util.List;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;
import com.medzone.cloud.data.CurrentAccountManager;
import com.medzone.cloud.database.DatabaseHelper;
import com.medzone.framework.Log;
import com.medzone.framework.data.bean.imp.AttachInfo;
import com.medzone.framework.data.bean.imp.Clock;

public class ClockCache extends AbstractDBObjectListCache<Clock> {

	/**
	 * 
	 */
	private static ClockCache instance;

	public static ClockCache getInstance() {
		if (instance == null)
			instance = new ClockCache();
		return instance;
	}

	public ClockCache() {
		AttachInfo attachInfo = new AttachInfo();
		attachInfo.mAccount = CurrentAccountManager.getCurAccount();
		instance = this;
		instance.setAttachInfo(attachInfo);
	}

	@Override
	public List<Clock> read() {
		try {
			Dao<Clock, Long> dao = DatabaseHelper.getInstance().getDao(
					Clock.class);
			QueryBuilder<Clock, Long> queryBuilder = dao.queryBuilder();
			Where<Clock, Long> where = queryBuilder.where();
			if (CurrentAccountManager.getCurAccount() == null)
				return null;
			where.eq(Clock.NAME_FIELD_ACCOUNT_ID, CurrentAccountManager
					.getCurAccount().getAccountID());

			PreparedQuery<Clock> preparedQuery = queryBuilder.prepare();
			list = dao.query(preparedQuery);
			return list;

		} catch (java.sql.SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void add(Clock item) {
		super.add(item);
		try {
			Dao<Clock, Long> dao = DatabaseHelper.getInstance().getDao(
					Clock.class);
			dao.create(item);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void addOrUpdate(Clock item) {
		try {
			Dao<Clock, Long> dao = DatabaseHelper.getInstance().getDao(
					Clock.class);
			if (item.getClockID() != null) {

				Clock tmp = dao.queryForId(item.getClockID().longValue());
				if (tmp != null) {
					Log.e(">>>>闹钟修改操作"+item.getClockTime()+"--"+item.getLabel());
					tmp.setSwitchState(item.isSwitchState());
					tmp.setClockTime(item.getClockTime());
					tmp.setLabel(item.getLabel());
					tmp.setRepetition(item.getRepetition());
				}
				dao.update(tmp);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}

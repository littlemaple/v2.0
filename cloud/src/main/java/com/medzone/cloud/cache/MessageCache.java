/**
 * 
 */
package com.medzone.cloud.cache;

import java.util.Collections;
import java.util.List;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;
import com.medzone.cloud.database.DatabaseHelper;
import com.medzone.framework.data.bean.imp.Message;
import com.medzone.framework.data.bean.imp.MessageSession;
import com.medzone.framework.data.navigation.LongStepable;
import com.medzone.framework.data.navigation.Paging;

/**
 * @author ChenJunQi.
 * 
 */
public class MessageCache extends
		AbstractPagingListCache<Message, LongStepable> {

	/**
	 * 
	 */
	private long interlocutorID = MessageSession.INVALID_ID - 0x99;
	private long groupID = MessageSession.INVALID_ID;
	private int groupType = (int) MessageSession.INVALID_ID;

	@Override
	public List<Message> read(Paging<LongStepable> paging) {
		if (isVaild(paging)) {
			try {
				Dao<Message, Long> messageDao = DatabaseHelper.getInstance()
						.getDao(Message.class);
				QueryBuilder<Message, Long> queryBuilder = messageDao
						.queryBuilder();
				queryBuilder.orderBy(Message.FIELD_NAME_ID, false);
				queryBuilder.limit((long) paging.getPageSize());
				Where<Message, Long> where = queryBuilder.where();

				if (interlocutorID != groupID) {
					where.eq(Message.NAME_FIELD_ACCOUNT_ID, getInterlocutorID());
					where.and();
				}
				where.eq(Message.FIELD_NAME_MESSAGE_GROUP_ID, getGroupID());
				where.and();
				where.between(Message.FIELD_NAME_ID,
						LongStepable.getMinValue(paging),
						LongStepable.getMaxValue(paging));
				PreparedQuery<Message> preparedQuery = queryBuilder.prepare();
				List<Message> list = messageDao.query(preparedQuery);
				Collections.reverse(list);// 倒置list
				return list;
			} catch (java.sql.SQLException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public long getInterlocutorID() {
		return interlocutorID;
	}

	public void setInterlocutorID(long interlocutorID) {
		this.interlocutorID = interlocutorID;
	}

	public void setGroupID(long groupID) {
		this.groupID = groupID;
	}

	public long getGroupID() {
		return groupID;
	}

	public void setGroupType(int groupType) {
		this.groupType = groupType;
	}

	public int getGroupType() {
		return groupType;
	}

	private boolean isVaild(Paging<LongStepable> paging) {
		// if (interlocutorID == MessageSession.INVALID_ID) {
		// return false;
		// }

		if (paging == null) {
			return false;
		}

		if (paging.getPageSize() < 0) {
			return false;
		}
		return true;
	}

}

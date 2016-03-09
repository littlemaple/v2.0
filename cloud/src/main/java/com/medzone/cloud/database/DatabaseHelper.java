package com.medzone.cloud.database;

import java.sql.SQLException;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.medzone.cloud.CloudApplication;
import com.medzone.framework.data.bean.imp.Account;
import com.medzone.framework.data.bean.imp.BloodOxygen;
import com.medzone.framework.data.bean.imp.BloodPressure;
import com.medzone.framework.data.bean.imp.Clock;
import com.medzone.framework.data.bean.imp.EarTemperature;
import com.medzone.framework.data.bean.imp.Group;
import com.medzone.framework.data.bean.imp.Message;
import com.medzone.framework.data.bean.imp.MessageSession;

public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

	private static final String DATABASE_NAME = "mcloud.db";
	private static final int DATABASE_VERSION = 25;
	private static volatile DatabaseHelper instance;

	private Dao<Account, Long> accountDao;
	private Dao<Group, Long> groupDao;
	// private Dao<Setting, Long> settingDao;
	private Dao<Clock, Long> clockDao;
	// private Dao<Rule, Long> ruleDao;
	// private Dao<Help, Long> helpDao;
	// private Dao<System, Long> systemDao;
	private Dao<BloodPressure, Long> bloodPressureDao;
	private Dao<EarTemperature, Long> temperatureDao;
	private Dao<BloodOxygen, Long> oxygenDao;
	// private Dao<SettingType, Long> settingTypeDao;
	private Dao<Message, Long> messageDao;
	private Dao<MessageSession, Long> messageSessionDao;

	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	public static DatabaseHelper getInstance() {
		if (instance == null) {
			init(CloudApplication.getInstance().getApplicationContext());
		}
		return instance;
	}

	/**
	 * 
	 * @see add synchronized to avoid: This method contains an unsynchronized
	 *      lazy initialization of a static field. After the field is set, the
	 *      object stored into that location is further updated or accessed. The
	 *      setting of the field is visible to other threads as soon as it is
	 *      set. If the futher accesses in the method that set the field serve
	 *      to initialize the object, then you have a very serious
	 *      multithreading bug, unless something else prevents any other thread
	 *      from accessing the stored object until it is fully initialized.
	 */
	public synchronized static void init(Context context) {
		if (instance == null) {
			instance = (DatabaseHelper) OpenHelperManager.getHelper(context,
					DatabaseHelper.class);
			instance.getReadableDatabase();
		}
	}

	public static void unInit() {
		OpenHelperManager.releaseHelper();
		instance = null;
	}

	@Override
	public void onCreate(SQLiteDatabase sqLiteDatabase,
			ConnectionSource connectionSource) {

		try {
			TableUtils.createTable(connectionSource, Account.class);
			// TableUtils.createTable(connectionSource, Setting.class);
			// TableUtils.createTable(connectionSource, SettingType.class);
			// TableUtils.createTable(connectionSource, Help.class);
			// TableUtils.createTable(connectionSource, System.class);
			// TableUtils.createTable(connectionSource, Rule.class);
			TableUtils.createTable(connectionSource, BloodPressure.class);
			TableUtils.createTable(connectionSource, Group.class);
			TableUtils.createTable(connectionSource, BloodOxygen.class);
			TableUtils.createTable(connectionSource, EarTemperature.class);
			TableUtils.createTable(connectionSource, Clock.class);
			TableUtils.createTable(connectionSource, Message.class);
			TableUtils.createTable(connectionSource, MessageSession.class);

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase sqLiteDatabase,
			ConnectionSource connectionSource, int oldVersion, int newVersion) {
		try {
			TableUtils.dropTable(connectionSource, Account.class, true);
			// TableUtils.dropTable(connectionSource, Setting.class, true);
			// TableUtils.dropTable(connectionSource, SettingType.class, true);
			// TableUtils.dropTable(connectionSource, Help.class, true);
			// TableUtils.dropTable(connectionSource, System.class, true);
			// TableUtils.dropTable(connectionSource, Rule.class, true);
			TableUtils.dropTable(connectionSource, Group.class, true);
			TableUtils.dropTable(connectionSource, BloodPressure.class, true);
			TableUtils.dropTable(connectionSource, BloodOxygen.class, true);
			TableUtils.dropTable(connectionSource, EarTemperature.class, true);
			TableUtils.dropTable(connectionSource, Clock.class, true);
			TableUtils.dropTable(connectionSource, Message.class, true);
			TableUtils.dropTable(connectionSource, MessageSession.class, true);

			onCreate(sqLiteDatabase, connectionSource);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	public <D extends Dao<T, ?>, T> D getDao(Class<T> clazz)
			throws SQLException {
		if (clazz.equals(Account.class)) {
			if (accountDao == null) {
				accountDao = (Dao<Account, Long>) super.getDao(clazz);
			}
			return (D) accountDao;
		}
		if (clazz.equals(Clock.class)) {
			if (clockDao == null) {
				clockDao = (Dao<Clock, Long>) super.getDao(clazz);
			}
			return (D) clockDao;
		}
		if (clazz.equals(Group.class)) {
			if (groupDao == null) {
				groupDao = (Dao<Group, Long>) super.getDao(clazz);
			}
			return (D) groupDao;
		}
		// if (clazz.equals(Help.class)) {
		// if (helpDao == null) {
		// helpDao = (Dao<Help, Long>) super.getDao(clazz);
		// }
		// return (D) helpDao;
		// }
		// if (clazz.equals(Rule.class)) {
		// if (ruleDao == null) {
		// ruleDao = (Dao<Rule, Long>) super.getDao(clazz);
		// }
		// return (D) ruleDao;
		// }
		// if (clazz.equals(Setting.class)) {
		// if (settingDao == null) {
		// settingDao = (Dao<Setting, Long>) super.getDao(clazz);
		// }
		// return (D) settingDao;
		// }
		// if (clazz.equals(SettingType.class)) {
		// if (settingTypeDao == null) {
		// settingTypeDao = (Dao<SettingType, Long>) super.getDao(clazz);
		// }
		// return (D) settingTypeDao;
		// }
		// if (clazz.equals(System.class)) {
		// if (systemDao == null) {
		// systemDao = (Dao<System, Long>) super.getDao(clazz);
		// }
		// return (D) systemDao;
		// }
		if (clazz.equals(BloodPressure.class)) {
			if (bloodPressureDao == null) {
				bloodPressureDao = (Dao<BloodPressure, Long>) super
						.getDao(clazz);
			}
			return (D) bloodPressureDao;
		}
		if (clazz.equals(EarTemperature.class)) {
			if (temperatureDao == null) {
				temperatureDao = (Dao<EarTemperature, Long>) super
						.getDao(clazz);
			}
			return (D) temperatureDao;
		}
		if (clazz.equals(BloodOxygen.class)) {
			if (oxygenDao == null) {
				oxygenDao = (Dao<BloodOxygen, Long>) super.getDao(clazz);
			}
			return (D) oxygenDao;
		}
		if (clazz.equals(Message.class)) {
			if (messageDao == null) {
				messageDao = (Dao<Message, Long>) super.getDao(clazz);
			}
			return (D) messageDao;
		}
		if (clazz.equals(MessageSession.class)) {
			if (messageSessionDao == null) {
				messageSessionDao = (Dao<MessageSession, Long>) super
						.getDao(clazz);
			}
			return (D) messageSessionDao;
		}

		return null;
	}

}

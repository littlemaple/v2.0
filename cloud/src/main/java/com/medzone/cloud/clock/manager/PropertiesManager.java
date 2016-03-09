package com.medzone.cloud.clock.manager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;

import com.medzone.framework.Log;

public class PropertiesManager {

	private static PropertiesManager propertiesManager = null;
	private Properties localProperties;
	private static String FILE_NAME;

	private boolean InitStatus = false;

	protected static PropertiesManager getInstance() {
		if (propertiesManager == null) {
			propertiesManager = new PropertiesManager();
		}
		return propertiesManager;
	}

	protected void initLocalFile(File filePath) {
		if (!InitStatus) {
			InitStatus = true;
			try {
				FILE_NAME = filePath + "/alarm.properties";
				File file = new File(FILE_NAME);
				if (!file.exists()) {
					file.createNewFile();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	protected void saveProperties(Properties localProperties) {
		try {
			FileOutputStream out = new FileOutputStream(FILE_NAME);
			ObjectOutputStream os = new ObjectOutputStream(out);
			os.writeObject(localProperties);
			os.flush();
			out.flush();
			os.close();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @return
	 */
	private Properties readLocalProperties() {

		if (localProperties == null) {
			try {
				FileInputStream fis = new FileInputStream(FILE_NAME);
				if (fis.available() == 0) {
					initProperties();
				} else {
					ObjectInputStream ois = new ObjectInputStream(fis);
					Object obj = ois.readObject();
					ois.close();
					if (obj instanceof Properties)
						localProperties = (Properties) obj;
				}
			} catch (Exception e) {
				Log.e("读取本地properties失败");
				e.printStackTrace();
			}
		}
		return localProperties;
	}

	private void initProperties() {
		saveProperties(new Properties());
		readLocalProperties();
	}

	/**
	 */
	@SuppressWarnings("unused")
	private void refreshProperties() {
		try {
			FileInputStream fis = new FileInputStream(FILE_NAME);
			ObjectInputStream ois = new ObjectInputStream(fis);
			Object obj = ois.readObject();
			ois.close();
			if (obj instanceof Properties)
				localProperties = (Properties) obj;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected Properties readPropertiesById(int id) {
		Properties properties = readLocalProperties();
		if (properties.get(id) == null) {
			return new Properties();
		}
		return (Properties) properties.get(id);
	}

	protected Properties readPropertiesById(Object key) {
		Properties properties = readLocalProperties();
		if (properties.get(key) == null) {
			return new Properties();
		}
		return (Properties) properties.get(key);
	}

	protected void print(Object key) {
		List<Alarm> list = new ArrayList<Alarm>();
		Properties properties = readLocalProperties();
		Properties p = (Properties) properties.get(key);
		for (Entry<Object, Object> set : p.entrySet()) {
			if (set.getValue() instanceof Alarm) {
				Alarm alarm = (Alarm) set.getValue();
				list.add(alarm);
			}
		}
		Log.e("size" + list.size());
	}

	/**
	 * @param id
	 * @return
	 */
	protected List<Alarm> readAccountAlarm(int id) {

		List<Alarm> list = new ArrayList<Alarm>();
		Properties properties = readPropertiesById(id);
		if (properties == null)
			return null;
		for (Entry<Object, Object> set : properties.entrySet()) {
			if (set.getValue() instanceof Alarm) {
				Alarm alarm = (Alarm) set.getValue();
				if (alarm.getClockID() != null
						&& alarm.getClockID().intValue() != 0)
					list.add(alarm);
			}
		}
		return list;
	}

	/**
	 * 
	 * @param id
	 *            (account id)
	 * @param key
	 *            (properties key)
	 * @return
	 */
	protected Object readAlarm(int accountId, Object key) {

		Properties properties = readPropertiesById(accountId);
		if (properties == null)
			return null;
		return properties.get(key);
	}

	protected void saveAlarm(Alarm alarm) {
		Properties properties = readPropertiesById(alarm.getAccountID());
		int nextId = nextKey(PropertiesManager.getInstance()
				.readPropertiesById(alarm.getAccountID()));
		alarm.setClockID(nextId);
		properties.put(nextId, alarm);
		localProperties.put(alarm.getAccountID(), properties);
		saveProperties(localProperties);
		// print(alarm.getAccountID());
	}

	protected void saveCurrentAlarm(Alarm alarm) {
		Properties properties = readPropertiesById(alarm.getAccountID());
		properties.put(alarm.getClockID(), alarm);
		localProperties.put(alarm.getAccountID(), properties);
		saveProperties(localProperties);
	}

	protected void print(Properties properties) {
		for (Object obj : properties.keySet()) {
			System.out.println(obj);
		}
	}

	protected void updateAlarm(Alarm alarm) {
		Properties properties = readPropertiesById(alarm.getAccountID());
		if (properties == null)
			return;
		for (Entry<Object, Object> set : properties.entrySet()) {
			Alarm temp = (Alarm) set.getValue();
			if (objEquals(temp, alarm)) {
				properties.put(alarm.getClockID(), alarm);
				localProperties.put(alarm.getAccountID(), properties);
				saveProperties(localProperties);
				break;
			}
		}
		// print(alarm.getAccountID());
	}

	protected void deleteAlarm(Alarm alarm) {
		Properties properties = readPropertiesById(alarm.getAccountID());
		if (properties == null)
			return;
		for (Entry<Object, Object> set : properties.entrySet()) {
			Alarm temp = (Alarm) set.getValue();
			if (objEquals(temp, alarm)) {
				properties.remove(alarm.getClockID());
				localProperties.put(alarm.getAccountID(), properties);
				saveProperties(localProperties);
				break;
			}
		}
		// print(alarm.getAccountID());
	}

	protected void deleteAlarm(int clockId, int accountId) {
		Properties properties = readPropertiesById(accountId);
		if (properties == null)
			return;
		for (Entry<Object, Object> set : properties.entrySet()) {
			Alarm temp = (Alarm) set.getValue();
			if (temp.getClockID().intValue() == clockId) {
				properties.remove(clockId);
				localProperties.put(accountId, properties);
				saveProperties(localProperties);
				break;
			}
		}
	}

	@Override
	protected void finalize() throws Throwable {
		super.finalize();
	}

	private boolean objEquals(Alarm a, Alarm b) {
		if (a.getClockID() == null) {
			System.err.println("clock id null");
			return false;
		}
		if (b.getClockID() == null) {
			System.err.println("clock id null");
			return false;
		}
		if (a.getClockID().intValue() == b.getClockID().intValue())
			return true;
		return false;
	}

	/**
	 * 
	 * @param properties
	 * @return
	 */
	private int nextKey(Properties properties) {
		int res = 1;
		for (Object key : properties.keySet()) {
			if (key instanceof Integer) {
				int temp = (Integer) key;
				if (temp > res)
					res = temp;
			}
		}
		return ++res;
	}
}

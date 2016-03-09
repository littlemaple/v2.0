/**
 * 
 */
package com.medzone.cloud.module;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.AsyncTask.Status;
import android.text.TextUtils;
import android.util.SparseArray;
import android.widget.Toast;

import com.medzone.cloud.CloudApplication;
import com.medzone.cloud.data.CurrentAccountManager;
import com.medzone.cloud.data.PropertyCenter;
import com.medzone.cloud.module.modules.BloodOxygenModule;
import com.medzone.cloud.module.modules.BloodPressureModule;
import com.medzone.cloud.module.modules.EarTemperatureModule;
import com.medzone.cloud.task.GetModuleConfigTask;
import com.medzone.cloud.ui.widget.CustomDialogProgressWithImage;
import com.medzone.framework.Log;
import com.medzone.framework.data.bean.imp.Account;
import com.medzone.framework.data.bean.imp.AttachInfo;
import com.medzone.framework.module.ModuleSpecification;
import com.medzone.framework.module.ModuleStatus;
import com.medzone.framework.network.NetworkClientResult;
import com.medzone.framework.task.BaseResult;
import com.medzone.framework.task.TaskHost;
import com.medzone.mcloud.R.drawable;
import com.medzone.mcloud.R.string;

/**
 * Centralized management module
 * 
 * 模块管理器集中对模块的管理工作,不负责越级处理模块
 */
public final class CloudMeasureModuleCentreRoot {

	private static Context context;
	private static GetModuleConfigTask getModuleTask;
	/**
	 * Key:AccountID
	 */
	private static SparseArray<List<CloudMeasureModule<?>>> modulesMap = new SparseArray<List<CloudMeasureModule<?>>>();

	public static List<CloudMeasureModule<?>> getModules(Account account) {
		if (account.getAccountID() == Account.INVALID_ID) {
			return null;
		}
		// FIXME why the moduleMap will be null?
		if (modulesMap == null) {
			modulesMap = new SparseArray<List<CloudMeasureModule<?>>>();
		}
		if (modulesMap.indexOfKey(account.getAccountID()) < 0) {
			return null;
		}
		return modulesMap.get(account.getAccountID());
	}

	public static boolean containsModules(Account account) {
		if (modulesMap.indexOfKey(account.getAccountID()) < 0) {
			return false;
		}
		return true;
	}

	/**
	 * 为指定帐号初始化一个模块容器
	 * 
	 * @param key
	 */
	private static void initModuleSet(Account key) {

		context = CloudApplication.getInstance().getApplicationContext();

		CloudModuleSpecificationManager.init(context, key);
		modulesMap.put(key.getAccountID(),
				new ArrayList<CloudMeasureModule<?>>());

	}

	/**
	 * 从网络获取用并更新本地以及内存中模块配置信息
	 * 
	 * @param key
	 * @param context
	 * @param isProgressShow
	 */
	public static void doGetModuleSpec(final Account key,
			final Context context, boolean isProgressShow) {

		if (CurrentAccountManager.getCurAccount() == null)
			return;
		if (getModuleTask != null
				&& getModuleTask.getStatus() == Status.RUNNING)
			return;
		getModuleTask = new GetModuleConfigTask(null, CurrentAccountManager
				.getCurAccount().getAccessToken(), key.getAccountID());
		if (isProgressShow) {
			getModuleTask.setProgress(new CustomDialogProgressWithImage(
					context, "正在加载数据……"));
		}
		getModuleTask.setTaskHost(new TaskHost() {
			@Override
			public void onPostExecute(int requestCode, BaseResult result) {
				super.onPostExecute(requestCode, result);
				if (result.isSuccess()) {
					NetworkClientResult res = (NetworkClientResult) result;
					if (res.isServerDisposeSuccess()) {
						List<ModuleSpecification> latestSpecs = ModuleSpecification
								.createModuleSpecificationList(res);
						initAllModuleWithLocalUpdate(key, latestSpecs);
					} else {
						Toast.makeText(context,
								res.getResponseResult().toString(),
								Toast.LENGTH_SHORT).show();
					}
				} else {
					Toast.makeText(context, string.error_net_connect,
							Toast.LENGTH_SHORT).show();
					initAllModuleWithLocalUpdate(key, null);
				}
			}
		});
		getModuleTask.execute();
	}

	public static void saveAllSpecs(Account key,
			List<ModuleSpecification> latestSpecs) {
		if (latestSpecs != null) {
			getManager(key).setUpdateSpecs(latestSpecs);
		}
		getManager(key).readAll();
	}

	public static void applyAllModules(Account key) {

		List<CloudMeasureModule<?>> associatedModuleList = new ArrayList<CloudMeasureModule<?>>();

		BloodPressureModule bpModule = (BloodPressureModule) applyModule(key,
				BloodPressureModule.class.getCanonicalName());
		if (bpModule != null) {
			ModuleSpecification tmpSpec = bpModule.getModuleSpecification();
			if (tmpSpec == null) {
				tmpSpec = BloodPressureModule.getDefaultSpecification(true);
			}
			bpModule.setModuleSpecification(tmpSpec);
			bpModule.setName(string.module_bloodpressure);
			bpModule.setIntroduce(string.module_bloodpressure_description);
			bpModule.setDrawable(drawable.monitor_ic_blood_pressure);
			associatedModuleList.add(bpModule);
		}

		BloodOxygenModule bxModule = (BloodOxygenModule) applyModule(key,
				BloodOxygenModule.class.getCanonicalName());
		if (bxModule != null) {
			ModuleSpecification tmpSpec = bxModule.getModuleSpecification();
			if (tmpSpec == null) {
				tmpSpec = BloodOxygenModule.getDefaultSpecification(true);
			}
			bxModule.setModuleSpecification(tmpSpec);
			bxModule.setName(string.module_bloodoxygen);
			bxModule.setIntroduce(string.module_bloodoxygen_description);
			bxModule.setDrawable(drawable.monitor_ic_hypoxemia);
			associatedModuleList.add(bxModule);
		}
		EarTemperatureModule tModule = (EarTemperatureModule) applyModule(key,
				EarTemperatureModule.class.getCanonicalName());
		if (tModule != null) {
			tModule.setName(string.module_temperture);
			tModule.setIntroduce(string.module_temperture_description);
			tModule.setDrawable(drawable.monitor_ic_gaore);

			ModuleSpecification tmpSpec = tModule.getModuleSpecification();
			if (tmpSpec == null) {
				tmpSpec = EarTemperatureModule.getDefaultSpecification(true);
			}
			// 强制让耳温版本不显示，并且处于不可配置状态
			tmpSpec.setModuleStatus(ModuleStatus.UNINSTALL);
			tModule.setModuleSpecification(tmpSpec);
			associatedModuleList.add(tModule);
		}

		modulesMap.put(key.getAccountID(), associatedModuleList);
	}

	public static void notifyDataSetChanged(Account key) {
		if (key == null || CurrentAccountManager.getCurAccount() == null) {
			return;
		}
		if (key.equals(CurrentAccountManager.getCurAccount())) {
			// 如果是获取他人的模块信息，也需要通知观察者更新，但是需要与自身的更新区分开
			PropertyCenter.getInstance().firePropertyChange(
					PropertyCenter.PROPERTY_REFRESH_MY_MODULES, null, null);
		} else {
			// 目前暂时先将SimpleName定义为通知他人的模块信息变更
			PropertyCenter.getInstance().firePropertyChange(
					PropertyCenter.PROPERTY_REFRESH_OTHER_MODULES, null, null);
		}
	}

	/**
	 * 
	 * @param key
	 * @param latestSpecs
	 *            latestSpecs 为null,表明直接读取本地信息,不使用指定配置更新本地
	 */
	public static void initAllModuleWithLocalUpdate(Account key,
			List<ModuleSpecification> latestSpecs) {

		saveAllSpecs(key, latestSpecs);

		applyAllModules(key);

		notifyDataSetChanged(key);
	}

	public static CloudMeasureModule<?> applyModule(Account key,
			String className) {

		String moduleID = CloudMeasureModule.INVAILD_ID;
		try {
			Class<?> clazz = Class.forName(className);
			Field field = clazz.getField("ID");
			if (field != null) {
				Object o = field.get(null);
				if (o != null)
					moduleID = o.toString();
			}

			List<CloudMeasureModule<?>> modules = modulesMap.get(key
					.getAccountID());
			for (CloudMeasureModule<?> module : modules) {
				if (TextUtils.equals(module.getModuleID(), moduleID)) {
					return module;
				}
			}

			// 如果不存在，则主动构建
			if (checkValidity(key, moduleID)) {
				Constructor<?> constructor = clazz.getDeclaredConstructor();
				constructor.setAccessible(true);
				CloudMeasureModule<?> module = (CloudMeasureModule<?>) constructor
						.newInstance();

				// 获取指定模块的配信置息
				ModuleSpecification spec = getManager(key).findSpecByModuleID(
						moduleID);

				AttachInfo attachInfo = new AttachInfo();
				attachInfo.mAccount = key;
				attachInfo.mModuleId = moduleID;
				module.init(context, attachInfo, spec);

				modules.add(module);
				return module;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void removeAll(Account key) {
		try {
			modulesMap.get(key.getAccountID()).clear();
			modulesMap.clear();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static boolean checkValidity(Account key, String moduleID) {
		if (TextUtils.equals(moduleID, CloudMeasureModule.INVAILD_ID)) {
			return false;
		}
		return true;
	}

	private static CloudModuleSpecificationManager getManager(Account key) {

		if (key == null) {
			key = CurrentAccountManager.getCurAccount();
			Log.w("CloudMeasureModuleCentreRoot#key:account is null.");
		}

		if (!containsModules(key)) {
			initModuleSet(key);
		}
		return CloudModuleSpecificationManager.getInstance(key);
	}

	/**
	 * 从内存中读取模块配的克隆对象
	 * 
	 * @param key
	 * @return 返回所有的模块配置,如果返回Null,则表明模块初始化出现问题
	 */
	public static List<ModuleSpecification> getAllModuleSpcification(Account key) {
		List<ModuleSpecification> cloneResult = null;
		List<CloudMeasureModule<?>> modules = modulesMap
				.get(key.getAccountID());
		if (modules != null && modules.size() != 0) {
			cloneResult = new ArrayList<ModuleSpecification>();
			for (CloudMeasureModule<?> module : modules) {
				cloneResult.add((ModuleSpecification) module
						.getModuleSpecification().clone());
			}
		}
		return cloneResult;
	}

	/**
	 * 更新各个模块所持有的模块配置,这里模块所持有的配置,与XML读取出来的配置不是同一个引用了
	 * 
	 * @param key
	 * @param updateSpecs
	 */
	public static void updateAllModuleSpecification(Account key,
			List<ModuleSpecification> updateSpecs) {
		List<CloudMeasureModule<?>> modules = modulesMap
				.get(key.getAccountID());
		if (modules != null && modules.size() != 0) {
			int position = 0;
			for (CloudMeasureModule<?> module : modules) {
				module.setModuleSpecification(updateSpecs.get(position));
				position++;
			}
		}
	}

	public static final List<ModuleSpecification> initDefaultMeasureModules(
			boolean... isDisplay) {

		List<ModuleSpecification> list = new ArrayList<ModuleSpecification>();
		list.add(BloodOxygenModule.getDefaultSpecification(isDisplay[0]));
		list.add(BloodPressureModule.getDefaultSpecification(isDisplay[1]));
		list.add(EarTemperatureModule.getDefaultSpecification(isDisplay[2]));

		return list;
	}

	private static GetModuleConfigTask task;

	public static void doUpdateAllSpecification(final Account key,
			final List<ModuleSpecification> updateSpecs,
			final IUpdateSpecificationListener listener) {
		if (task != null && task.getStatus() == Status.RUNNING) {
			return;
		}
		task = new GetModuleConfigTask(null, CurrentAccountManager
				.getCurAccount().getAccessToken(), updateSpecs);
		task.setTaskHost(new TaskHost() {
			@Override
			public void onPostExecute(int requestCode, BaseResult result) {
				super.onPostExecute(requestCode, result);
				if (result.isSuccess()) {
					if (result.isServerDisposeSuccess()) {

						// 将已经网络同步的模块配置更新本地
						getManager(key).setUpdateSpecs(updateSpecs);
						getManager(key).readAll();

						updateAllModuleSpecification(key, updateSpecs);

						PropertyCenter.getInstance().firePropertyChange(
								PropertyCenter.PROPERTY_REFRESH_MY_MODULES,
								null, null);

						listener.onComplete(true);
					} else {
						listener.onComplete(false);
					}
				} else {
					listener.onComplete(false);
				}

			}
		});
		task.execute();
	}
}

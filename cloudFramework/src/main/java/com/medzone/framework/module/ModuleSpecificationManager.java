/**
 * 
 */
package com.medzone.framework.module;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.content.Context;
import android.text.TextUtils;

import com.medzone.framework.data.bean.imp.Account;

public abstract class ModuleSpecificationManager/*
												 * implements
												 * IModuleNotificationHost
												 */{

	// The corresponding category measurement module
	protected final static int CATEGOTY_MEASURE_MODUL = 0;
	// The total number of the service side of categories defined
	protected final static int CATEGOTY_COUNT = 1;

	protected Account mAttachAccount;

	protected Context mContext;
	// Each categories of modules, will be assigned to the respective List
	protected List<List<ModuleSpecification>> memoryModulesList;

	// protected IModuleNotificationHost mIModuleNotificationHost;

	protected ModuleSpecificationXMLUtil getCache() {
		return ModuleSpecificationXMLUtil.getInstance(mContext);
	}

	protected ModuleSpecificationManager(Account accountBean, Context context/*
																			 * ,
																			 * IModuleNotificationHost
																			 * mnh
																			 */) {
		this.mAttachAccount = accountBean;
		this.mContext = context;
		// if (mnh != null)
		// this.mIModuleNotificationHost = mnh;
		// else
		// this.mIModuleNotificationHost = this;

		memoryModulesList = applyForContainer();
	}

	/**
	 * 仅仅从本地文件读取模块配置信息
	 * 
	 */
	protected List<List<ModuleSpecification>> readAllFromLocal() {
		return getCache().getModulesFromXML(mContext, mAttachAccount);
	}

	// FIXME 需要考虑的方法,这里是希望能够保证获取到的数据非空,但是如果是网络下的配置,则需要与外部Task相结合.
	// 所以会造成单独调用这里的方法会出现不完整的问题
	public List<List<ModuleSpecification>> readAll() {

		List<List<ModuleSpecification>> result = readAllFromNet();

		if (result == null || result.size() == 0) {

			if (!isSpecsExist()) {
				createDefaultFile();
			}
			result = readAllFromLocal();
		}
		sortModulesByOrder(result);

		// TODO 同步内存中的值，如果配置有变更，也应该通知相关处理进行更新
		memoryModulesList = result;

		return result;
	}

	/**
	 * 这个方法使用遍历去查找指定模块，会造成性能损耗，不要用在性能要求较高的场景。
	 * 
	 * @param moduleID
	 * @return ModuleSpecification
	 */
	public ModuleSpecification findSpecByModuleID(String moduleID) {

		ModuleSpecification spec = null;
		List<ModuleSpecification> list = memoryModulesList
				.get(CATEGOTY_MEASURE_MODUL);
		for (ModuleSpecification ms : list) {
			if (TextUtils.equals(moduleID, ms.getModuleID())) {
				spec = ms;
				break;
			}
		}
		return spec;
	}

	/**
	 * 将指定项配置列表新建或者更新本地
	 * 
	 * @param modules
	 */
	public void createOrUpdate(List<ModuleSpecification> modules) {
		getCache().createXMLFile(modules, mAttachAccount);
	}

	/**
	 * 
	 * @return 检查该配置文件是否存在
	 */
	public boolean isSpecsExist() {
		return getCache().isXMLFileExist(mAttachAccount);
	}

	public boolean deleteAll() {
		return getCache().delXMLModuleSpecification(mAttachAccount);
	}

	public void flush(ModuleSpecification spec) {
		getCache().updateXMLSimpleModuleSpecification(mContext, spec,
				mAttachAccount);
	}

	/**
	 * 为模块配置信息申请容器，模块的种类将影响模块配置容器下子容器的数量
	 * 
	 * @return 空的模块配置容器
	 */
	private List<List<ModuleSpecification>> applyForContainer() {
		List<List<ModuleSpecification>> result = new ArrayList<List<ModuleSpecification>>(
				CATEGOTY_COUNT);
		for (int i = 0; i < CATEGOTY_COUNT; i++) {
			result.add(new ArrayList<ModuleSpecification>());
		}
		return result;
	}

	/**
	 * 依据模块配置下的order为key进行排序，这要求我们的模块配置必须是有序的，不能出现重复的order配置
	 * 
	 * @param modules
	 *            模块配置列表
	 * 
	 */
	private void sortModulesByOrder(List<List<ModuleSpecification>> modules) {
		if (modules != null) {
			for (List<ModuleSpecification> list : modules) {
				Collections.sort(list, new Comparator<ModuleSpecification>() {

					@Override
					public int compare(ModuleSpecification a,
							ModuleSpecification b) {
						return a.getOrder() - b.getOrder();
					}
				});
			}
		}
	}

	// @Override
	// public void pushNotification(ModuleSpecification ms, String content,
	// int count) {
	// // A PLACEHOLDER METHOD，DON'T REALIZE IT HERE.
	// }

	protected abstract void createDefaultFile();

	protected abstract List<List<ModuleSpecification>> readAllFromNet();

	public void reset() {
		mAttachAccount = null;
		mContext = null;
		memoryModulesList = null;
		// mIModuleNotificationHost = null;
	}
}

/**
 * 
 */
package com.medzone.cloud.module;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.SparseArray;

import com.medzone.framework.data.bean.imp.Account;
import com.medzone.framework.module.ModuleSpecification;
import com.medzone.framework.module.ModuleSpecificationManager;

/**
 * 一个帐号对应一个模块说明管理类，用于读取模块的配置信息
 */
final class CloudModuleSpecificationManager extends ModuleSpecificationManager {

	private static SparseArray<CloudModuleSpecificationManager> specManagerMap = new SparseArray<CloudModuleSpecificationManager>();

	protected CloudModuleSpecificationManager(Account accountBean,
			Context context) {
		super(accountBean, context);
	}

	private static boolean containsKey(Account key) {
		if (specManagerMap.indexOfKey(key.getAccountID()) < 0)
			return false;
		return true;
	}

	/**
	 * 确保在使用Manager类的第一个动作是调用该 {@link #init(Context, Account)}方法
	 * 
	 * @param context
	 * @param key
	 * @param mnh
	 */
	public static void init(Context context, Account key) {

		if (!containsKey(key)) {
			specManagerMap.put(key.getAccountID(),
					new CloudModuleSpecificationManager(key, context));
		}
	}

	public static void unInit(Account key) {
		if (containsKey(key)) {
			specManagerMap.get(key.getAccountID()).reset();
			specManagerMap.delete(key.getAccountID());
		}
	}

	public static CloudModuleSpecificationManager getInstance(Account key) {

		if (containsKey(key)) {
			return specManagerMap.get(key.getAccountID());
		}
		throw new IllegalArgumentException("ID:" + key.getAccountID()
				+ "配置管理器，尚未初始化过!");
	}

	// XXX 与方法名不符合，并没有做到读取网络配置的功能
	// 思考，因为多帐号情况下，获取模块配置就有2种情况，分别是获取自己，获取他人。
	// 如果在这边定义获取网络模块配置数据的标准请求，那么将不可控制。
	// 除非外部已经限制好可能发生的情况
	@Override
	protected List<List<ModuleSpecification>> readAllFromNet() {

		final List<List<ModuleSpecification>> lists;
		List<ModuleSpecification> netSpecList = getUpdateSpecs();
		if (netSpecList == null || netSpecList.size() == 0) {
			return null;
		}
		lists = new ArrayList<List<ModuleSpecification>>();
		lists.add(netSpecList);

		// 更新本地XML文件
		createOrUpdate(netSpecList);

		return lists;
	}

	private List<ModuleSpecification> updateSpecs;

	/**
	 * 传递配置列表，调用read方法将对这里的配置信息进行本地存储
	 */
	public void setUpdateSpecs(List<ModuleSpecification> updateSpecs) {
		this.updateSpecs = updateSpecs;
	}

	/**
	 * 获取服务端模块配置的事件，交由外部Task实现。这里只是获取外部Task成功后，返回的配置列表。
	 * 
	 * @return
	 */
	@Deprecated
	private List<ModuleSpecification> getUpdateSpecs() {
		return updateSpecs;
	}

	@Override
	protected void createDefaultFile() {
		List<ModuleSpecification> tmp = CloudMeasureModuleCentreRoot
				.initDefaultMeasureModules(true, true, true);
		createOrUpdate(tmp);
	}

}

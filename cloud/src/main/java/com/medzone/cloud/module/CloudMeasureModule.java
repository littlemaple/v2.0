/**
 * 
 */
package com.medzone.cloud.module;

import android.content.Context;
import android.os.AsyncTask.Status;
import android.text.TextUtils;

import com.medzone.cloud.controller.AbstractUsePagingTaskCacheController;
import com.medzone.cloud.data.CurrentAccountManager;
import com.medzone.cloud.module.modules.CloudUseTaskCacheControllerModule;
import com.medzone.cloud.task.GetModuleConfigTask;
import com.medzone.framework.Log;
import com.medzone.framework.data.bean.imp.AttachInfo;
import com.medzone.framework.module.ModuleSpecification;
import com.medzone.framework.module.ModuleStatus;
import com.medzone.framework.task.BaseResult;
import com.medzone.framework.task.TaskHost;

/**
 * @author ChenJunQi.
 * 
 */
public abstract class CloudMeasureModule<T extends AbstractUsePagingTaskCacheController<?, ?, ?>>
		extends CloudUseTaskCacheControllerModule<T> {

	public static final int FRAGMENT_TYPE_HISTORY = 0x1001;

	public static final char DEFAULT_MODULE_SETTING = 1;
	protected ModuleSpecification mModuleSpecification;

	public void init(Context context, AttachInfo attachInfo,
			ModuleSpecification spec) {
		super.init(context, attachInfo);
		mModuleSpecification = spec;
	}

	protected CloudModuleSpecificationManager getManager() {
		return CloudModuleSpecificationManager
				.getInstance(mAttachInfo.mAccount);
	}

	public ModuleSpecification getDefaultSpecification() {
		return null;
	};

	public boolean isDefaultInstall() {
		String isDefaultInstall = mModuleSpecification
				.getExtraAttributeString(ModuleSpecification.EXTRA_ATTRIBUTES_DEFAULT_INSTALL);
		return Boolean.valueOf(isDefaultInstall);
	}

	public boolean isShowInHomePage() {
		String isShowInHomePage = mModuleSpecification
				.getExtraAttributeString(ModuleSpecification.EXTRA_ATTRIBUTES_DEFAULT_SHOW_IN_HOMEPAGE);
		return Boolean.valueOf(isShowInHomePage);
	}

	public boolean isUnInstallable() {
		String isUnInstallable = mModuleSpecification
				.getExtraAttributeString(ModuleSpecification.EXTRA_ATTRIBUTES_IS_UNINSTALLABLE);
		return Boolean.valueOf(isUnInstallable);
	}

	public String getModuleDownLoadPath() {
		return mModuleSpecification.getDownLoadLink();
	}

	public String getModulePackagePath() {
		return mModuleSpecification.getPackageName();
	}

	public String getModuleActivityPath() {
		return mModuleSpecification.getClassName();
	}

	public int getOrder() {
		return mModuleSpecification.getOrder();
	}

	public String getExtraAttributeString(String key) {
		return mModuleSpecification.getExtraAttributeString(key);
	}

	public Boolean getExtraAttributeBool(String key, boolean defaultValue) {
		return mModuleSpecification.getExtraAttributeBool(key, defaultValue);
	}

	public String getModuleID() {
		return mModuleSpecification.getModuleID();
	}

	public int getCategory() {
		return mModuleSpecification.getCategory();
	}

	public ModuleStatus getModuleStatus() {
		return mModuleSpecification.getModuleStatus();
	}

	public String getModuleSetting() {
		return mModuleSpecification.getSetting();
	}

	public int getDownSerial() {
		if (mModuleSpecification.getDownSerial() == null) {
			mModuleSpecification.setDownSerial(Integer.valueOf(0));
		}
		return mModuleSpecification.getDownSerial().intValue();
	}

	public ModuleSpecification getModuleSpecification() {
		return mModuleSpecification;
	}

	public void setModuleSpecification(ModuleSpecification moduleSpecification) {
		this.mModuleSpecification = moduleSpecification;
	}

	/**
	 * 对现有内存中的配置信息进行本地存储
	 */
	public void flush() {
		getManager().flush(mModuleSpecification);
	}

	public void updateModuleDownSerial(int downSerial) {
		// downSerial没有必要更新到服务端，所以一直是本地保存
		mModuleSpecification.setDownSerial(downSerial);
		flush();
	}

	private GetModuleConfigTask task;

	/**
	 * 更新单个模块的配置，保存到服务器
	 * 
	 * @param spec
	 * @param listener
	 * 
	 * 
	 */
	public void doUpdateSimpleSpecification(final ModuleSpecification spec,
			final IUpdateSpecificationListener listener) {
		if (task != null && task.getStatus() == Status.RUNNING) {
			return;
		}
		task = new GetModuleConfigTask(null, CurrentAccountManager
				.getCurAccount().getAccessToken(), spec);
		task.setTaskHost(new TaskHost() {
			@Override
			public void onPostExecute(int requestCode, BaseResult result) {
				super.onPostExecute(requestCode, result);
				if (result.isSuccess()) {
					if (result.isServerDisposeSuccess()) {
						mModuleSpecification = spec;
						flush();
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

	/**
	 * 修改指定项的设置值
	 * 
	 * @param position
	 *            指定下标对应的设置
	 * @param value
	 *            指定下标对应的设置value,如果设置成功，将被复写
	 * @param listener
	 *            回调该次API调用是否成功
	 */
	public void modifiedSetting(int position, char value,
			IUpdateSpecificationListener listener) {
		String tmp = getModuleSetting();

		if (TextUtils.isEmpty(tmp)) {

			ModuleSpecification defSpecification = getDefaultSpecification();
			if (defSpecification == null) {
				Log.w("该模块没有配置默认配置，无法进行初始化配置操作！");
				return;
			} else {
				tmp = defSpecification.getSetting();
			}
			if (tmp == null) {
				Log.w("模块设置为null,该模块无任何设置可配置！");
				return;
			}
		}
		char[] tmpSetting = tmp.toCharArray();
		if (position < 0 || tmpSetting.length < position) {
			throw new IndexOutOfBoundsException(
					"指定position,不在可设置的长度范围内,请检查传入值！");
		}
		tmpSetting[position] = value;
		String newSettings = new String(tmpSetting);

		ModuleSpecification tmpSpec = (ModuleSpecification) getModuleSpecification()
				.clone();
		tmpSpec.setSetting(newSettings);
		doUpdateSimpleSpecification(tmpSpec, listener);
	}

	/**
	 * 
	 * @param position
	 *            获取指定下标对应的设置value
	 * @return 当设置setting为null时，主动调用时会抛出空指针异常
	 */
	public char getPositionSetting(int position) {
		final String setting = getModuleSetting();
		if (TextUtils.isEmpty(setting)) {
			return DEFAULT_MODULE_SETTING;
		}
		char[] tmpSetting = setting.toCharArray();
		if (position < 0 || tmpSetting.length < position) {
			throw new IndexOutOfBoundsException(
					"指定position,不在可设置的长度范围内,请检查传入值！");
		}
		Log.e("current bloodpressure unit setting>>>>>"+tmpSetting[position]);
		return tmpSetting[position];
	}
}

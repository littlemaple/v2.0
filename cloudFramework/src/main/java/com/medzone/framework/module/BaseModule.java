/**
 * 
 */
package com.medzone.framework.module;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.SparseArray;

import com.medzone.framework.fragment.BaseModuleFragment;

/**
 * 
 * @author Local_ChenJunQi
 * 
 *         TODO 重构该类，使模块本身具有管理多个碎片的功能，以达到模块内以碎片为单位生命周期管理。
 * 
 */
public abstract class BaseModule implements IModule {

	// XXX 这里调整为使用Int标志位，有助于对性能的改善
	public final static String INVAILD_ID = "INVAILD_MODULE_ID";

	protected Context mContext;
	// Resource
	protected int resName;
	protected int resIntroduce;
	protected int resDrawable;
	// Memory fragments.
	protected SparseArray<BaseModuleFragment> fragments;

	protected void init(Context context) {
		this.mContext = context;
	}

	public void setName(int res) {
		this.resName = res;
	}

	@Override
	public String getName() {
		return mContext.getResources().getString(resName);
	}

	public void setIntroduce(int res) {
		this.resIntroduce = res;
	}

	@Override
	public String getIntroduce() {
		return mContext.getResources().getString(resIntroduce);
	}

	public void setDrawable(int res) {
		this.resDrawable = res;
	}

	@Override
	public Drawable getDrawable() {
		if (resDrawable == 0) {
			// XXX 可以再这里设置模块图标默认值
			return null;
		}
		return mContext.getResources().getDrawable(resDrawable);
	}

	protected void createFragment(int key) {

	}

	public BaseModuleFragment getFragment(int key) {
		initFragmentQueue();
		if (!isContains(key)) {
			createFragment(key);
		}
		return fragments.get(key);
	}

	protected void addFragment(int key, BaseModuleFragment value) {
		initFragmentQueue();
		fragments.put(key, value);
	}

	protected boolean isContains(int key) {
		initFragmentQueue();
		if (fragments.indexOfKey(key) < 0) {
			return false;
		}
		return true;
	}

	private void initFragmentQueue() {
		if (fragments == null) {
			fragments = new SparseArray<BaseModuleFragment>();
		}
	}

}

package com.medzone.cloud.ui.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.ViewGroup;

import com.medzone.cloud.module.CloudMeasureModule;

/**
 * 
 * 
 * @author ChenJunQi.
 * 
 * 
 * 
 */
public class MeasureHistoryAdapter extends FragmentPagerAdapter {

	private static final int NOT_FIND_PLUGIN_POSITION = -100;
	private static final String SAVE_STATE_BUNDLE_NAME = "states";
	private static final String SAVE_STATE_KEY_BUNDLE_NAME = "states_key";
	private static final String SAVE_FRAGMENT_KEY_BUNDLE_NAME = "_";

	private List<CloudMeasureModule<?>> sourceList;
	private List<CloudMeasureModule<?>> newList;
	private List<CloudMeasureModule<?>> oldList;

	private HashMap<String, Fragment> moduleFragments = new HashMap<String, Fragment>();
	private HashMap<String, Fragment.SavedState> moduleFragmentSavedState = new HashMap<String, Fragment.SavedState>();

	private FragmentTransaction mCurTransaction = null;
	private Fragment mCurrentPrimaryItem = null;
	private FragmentManager mFragmentManager;

	protected Context mContext;

	public MeasureHistoryAdapter(Context context, FragmentManager fm) {
		super(fm);
		this.mContext = context;
		this.mFragmentManager = fm;
	}

	public void setContent(List<CloudMeasureModule<?>> sourceList) {
		this.sourceList = sourceList;
		this.oldList = this.newList;
		filterSourceList();
		this.notifyDataSetChanged();
		checkRecord();
	}

	private void filterSourceList() {
		newList = new ArrayList<CloudMeasureModule<?>>();
		if (sourceList != null) {
			for (CloudMeasureModule<?> i : sourceList) {
				switch (i.getModuleStatus()) {
				case DISPLAY:
					newList.add(i);
					break;
				default:
					break;
				}
			}
		}
	}

	public int getDisplayModuleCount() {
		return newList == null ? 0 : newList.size();
	}

	@Override
	public Fragment getItem(int position) {
		return this.newList.get(position).getFragment(
				CloudMeasureModule.FRAGMENT_TYPE_HISTORY);
	}

	@Override
	public long getItemId(int position) {

		return this.newList.get(position).getOrder();
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {

		String moduleID = this.newList.get(position).getModuleID();
		if (moduleFragments.containsKey(moduleID)) {
			Fragment f = moduleFragments.get(moduleID);
			if (f != null) {
				return f;
			}
		}

		if (mCurTransaction == null) {
			mCurTransaction = mFragmentManager.beginTransaction();
		}
		Fragment fragment = getItem(position);

		if (moduleFragmentSavedState.containsKey(moduleID)) {
			Fragment.SavedState fss = moduleFragmentSavedState.get(moduleID);
			if (fss != null) {
				fragment.setInitialSavedState(fss);
			}
		}
		fragment.setMenuVisibility(false);
		fragment.setUserVisibleHint(false);
		moduleFragments.put(moduleID, fragment);

		mCurTransaction.add(container.getId(), fragment);
		return fragment;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		Fragment fragment = (Fragment) object;
		if (mCurTransaction == null) {
			mCurTransaction = mFragmentManager.beginTransaction();
		}
		CloudMeasureModule<?> pulgin = getInemNewPositionModuleSpecification(object);
		if (pulgin == null) {
			// if null,thie pulgin removed
		} else {
			// pulgin hide,save the state
			String pluginId = pulgin.getModuleID();
			moduleFragmentSavedState.put(pluginId,
					mFragmentManager.saveFragmentInstanceState(fragment));
			moduleFragments.remove(pluginId);
		}
		mCurTransaction.remove(fragment);
	}

	@Override
	public void setPrimaryItem(ViewGroup container, int position, Object object) {
		Fragment fragment = (Fragment) object;
		if (fragment != mCurrentPrimaryItem) {
			if (mCurrentPrimaryItem != null) {
				mCurrentPrimaryItem.setMenuVisibility(false);
				mCurrentPrimaryItem.setUserVisibleHint(false);
			}
			if (fragment != null) {
				fragment.setMenuVisibility(true);
				fragment.setUserVisibleHint(true);
			}
			mCurrentPrimaryItem = fragment;
		}
	}

	@Override
	public void finishUpdate(ViewGroup container) {
		if (mCurTransaction != null) {
			mCurTransaction.commitAllowingStateLoss();
			mCurTransaction = null;
			mFragmentManager.executePendingTransactions();
		}
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return ((Fragment) object).getView() == view;
	}

	@Override
	public int getItemPosition(Object object) {
		int newPosition = getInemNewPosition(object);
		if (newPosition == NOT_FIND_PLUGIN_POSITION) {
			// 移除
			return POSITION_NONE;
		} else {
			int oldPosition = getInemOldPosition(object);
			if (newPosition == oldPosition) {
				return POSITION_UNCHANGED;
			} else {
				return newPosition;
			}
		}
	}

	@Override
	public int getCount() {
		return this.newList == null ? 0 : newList.size();
	}

	@Override
	public CharSequence getPageTitle(int position) {

		CharSequence cs;
		// FIXME 偶见的异常那抛出，暂时不明白原因
		try {
			cs = this.newList == null ? "" : newList.get(position).getName();
		} catch (Exception e) {
			e.printStackTrace();
			cs = "";
		}

		return cs;
	}

	@Override
	public Parcelable saveState() {
		Bundle state = null;

		if (moduleFragmentSavedState.size() > 0) {
			state = new Bundle();
			Fragment.SavedState[] fss = new Fragment.SavedState[moduleFragmentSavedState
					.size()];
			String[] fsskey = new String[moduleFragmentSavedState.size()];
			moduleFragmentSavedState.values().toArray(fss);
			moduleFragmentSavedState.keySet().toArray(fsskey);
			state.putParcelableArray(SAVE_STATE_BUNDLE_NAME, fss);
			state.putStringArray(SAVE_STATE_KEY_BUNDLE_NAME, fsskey);
		}
		Iterator<String> interator = moduleFragments.keySet().iterator();
		while (interator.hasNext()) {
			String key = interator.next();
			Fragment f = moduleFragments.get(key);
			if (f != null) {
				if (state == null) {
					state = new Bundle();
				}
				mFragmentManager.putFragment(state,
						SAVE_FRAGMENT_KEY_BUNDLE_NAME + key, f);
			}
		}
		return state;
	}

	@Override
	public void restoreState(Parcelable state, ClassLoader loader) {
		if (state != null) {
			Bundle bundle = (Bundle) state;
			bundle.setClassLoader(loader);
			Parcelable[] fss = bundle
					.getParcelableArray(SAVE_STATE_BUNDLE_NAME);
			String[] fsskey = bundle.getStringArray(SAVE_STATE_KEY_BUNDLE_NAME);
			moduleFragmentSavedState.clear();
			moduleFragments.clear();
			if (fsskey != null) {
				for (int i = 0; i < fsskey.length; i++) {
					moduleFragmentSavedState.put(fsskey[i],
							(Fragment.SavedState) fss[i]);
				}
			}
			Iterable<String> keys = bundle.keySet();
			for (String key : keys) {
				if (key.startsWith(SAVE_FRAGMENT_KEY_BUNDLE_NAME)) {
					String pluginId = key.substring(1);
					Fragment f = null;
					try {
						f = mFragmentManager.getFragment(bundle, key);
						if (f != null) {
							f.setMenuVisibility(false);
							moduleFragments.put(pluginId, f);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	private int getInemOldPosition(Object object) {
		if (oldList != null) {
			for (int i = 0; i < oldList.size(); i++) {
				CloudMeasureModule<?> plugin = getInemOldPositionPluginSpecification(object);
				if (plugin != null) {
					return oldList.indexOf(plugin);
				}
			}
		}
		return NOT_FIND_PLUGIN_POSITION;
	}

	private CloudMeasureModule<?> getInemOldPositionPluginSpecification(
			Object object) {
		final Fragment fragment = (Fragment) object;
		if (oldList != null) {
			for (int i = 0; i < oldList.size(); i++) {
				if (isFragmentEqual(
						fragment,
						oldList.get(i).getFragment(
								CloudMeasureModule.FRAGMENT_TYPE_HISTORY)))
					return oldList.get(i);
			}
		}
		return null;
	}

	private int getInemNewPosition(Object object) {
		if (newList != null) {
			for (int i = 0; i < newList.size(); i++) {
				CloudMeasureModule<?> plugin = getInemNewPositionModuleSpecification(object);
				if (plugin != null) {
					return newList.indexOf(plugin);
				}
			}
		}
		return NOT_FIND_PLUGIN_POSITION;
	}

	private CloudMeasureModule<?> getInemNewPositionModuleSpecification(
			Object object) {
		final Fragment fragment = (Fragment) object;
		if (newList != null) {
			for (int i = 0; i < newList.size(); i++) {
				if (isFragmentEqual(
						fragment,
						newList.get(i).getFragment(
								CloudMeasureModule.FRAGMENT_TYPE_HISTORY))) {
					return newList.get(i);
				}
			}
		}
		return null;
	}

	private boolean isFragmentEqual(Fragment object, Fragment fragment) {
		return object.equals(fragment);
	}

	private void checkRecord() {
		if (newList != null) {
			if (oldList != null) {
				for (CloudMeasureModule<?> item : oldList) {
					if (!newList.contains(item)) {
						moduleFragmentSavedState.remove(item.getModuleID());
						moduleFragments.remove(item.getModuleID());
					}
				}
			}
		}
	}

}

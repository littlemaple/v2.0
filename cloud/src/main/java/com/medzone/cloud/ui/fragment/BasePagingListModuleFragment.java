/**
 * 
 */
package com.medzone.cloud.ui.fragment;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView.RecyclerListener;
import android.widget.ListView;
import android.widget.TextView;

import com.medzone.cloud.cache.AbstractPagingListCache;
import com.medzone.cloud.controller.AbstractUsePagingTaskCacheController;
import com.medzone.cloud.module.CloudMeasureModule;
import com.medzone.cloud.module.modules.BloodOxygenModule;
import com.medzone.cloud.module.modules.BloodPressureModule;
import com.medzone.cloud.module.modules.EarTemperatureModule;
import com.medzone.cloud.ui.adapter.PagingListCacheAdapter;
import com.medzone.cloud.ui.adapter.viewholder.BaseViewHolder;
import com.medzone.framework.Log;
import com.medzone.framework.data.bean.BasePagingContent;
import com.medzone.framework.data.navigation.Stepable;
import com.medzone.framework.fragment.BaseModuleFragment;
import com.medzone.framework.task.BaseResult;
import com.medzone.framework.task.TaskHost;
import com.medzone.framework.util.TimeUtils;
import com.medzone.framework.view.refresh.PullToRefreshBase.Mode;
import com.medzone.framework.view.refresh.PullToRefreshBase.OnRefreshListener2;
import com.medzone.framework.view.refresh.PullToRefreshListView;
import com.medzone.mcloud.R.id;
import com.medzone.mcloud.R.layout;
import com.medzone.mcloud.R.string;

/**
 * @author ChenJunQi.
 * 
 */
public abstract class BasePagingListModuleFragment<T extends BasePagingContent, S extends Stepable<S>, C extends AbstractPagingListCache<T, S>>
		extends BaseModuleFragment /* implements PropertyChangeListener */{

	protected int mainViewResId;
	protected PagingListCacheAdapter<T, S, C> adapter;
	protected AbstractUsePagingTaskCacheController<T, S, C> controller;
	protected PullToRefreshListView refreshListView;
	protected ListView listView;

	public BasePagingListModuleFragment() {
	}

	public void setController(
			AbstractUsePagingTaskCacheController<T, S, C> controller) {
		this.controller = controller;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		// PropertyCenter.propertySupport.addPropertyChangeListener(this);
	}

	@Override
	final public View onCreateView(LayoutInflater inflater,
			ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(mainViewResId, container, false);
		preInit();
		initUI(view);
		return view;
	}

	protected void preInit() {
		initAdapter();
	}

	protected void initUI(View view) {

		refreshListView = (PullToRefreshListView) view
				.findViewById(id.refreshListView);
		refreshListView.setPullToRefreshEnabled(true);
		refreshListView.setMode(Mode.BOTH);
		refreshListView.setOnRefreshListener(new OnRefreshListener2() {

			@Override
			public void onPullDownToRefresh() {
				Log.v("onPullDownToRefresh#getNewItems"
						+ adapter.getCacheSimpleName());
				getNewItems(true);
			}

			@Override
			public void onPullUpToRefresh() {
				getOldContents();
				Log.v("onPullUpToRefresh#getOldContents"
						+ adapter.getCacheSimpleName());
			}

		});
		listView = refreshListView.getRefreshableView();
		listView.setAdapter(adapter);
		listView.setDivider(new ColorDrawable(Color.TRANSPARENT));
		// listView.setEmptyView(getItemEmptyView());
		listView.setRecyclerListener(new RecyclerListener() {

			@Override
			public void onMovedToScrapHeap(View view) {
				if (view != null) {
					BaseViewHolder viewHolder = (BaseViewHolder) view.getTag();
					if (viewHolder != null) {
						viewHolder.recycle();
					}
				}
			}

		});
		registerForContextMenu(listView);
		eventRegistration();
		getNewItems(true);
		Log.v("init#getNewItems" + adapter.getCacheSimpleName());
	}

	private View getItemEmptyView() {
		View emptyView = LayoutInflater.from(getActivity()).inflate(
				layout.list_item_history_empty, null);
		TextView tvAd1 = (TextView) emptyView.findViewById(id.tv_ad_1);
		String tvFormat = getResources().getString(string.home_welcomes_ad3);
		String moduleName = "";
		if (controller.getAttachInfo().mModuleId != null
				&& controller.getAttachInfo().mModuleId != CloudMeasureModule.INVAILD_ID) {
			if (controller.getAttachInfo().mModuleId
					.equals(BloodPressureModule.ID)) {
				moduleName = BloodPressureModule.NAME;
			} else if (controller.getAttachInfo().mModuleId
					.equals(BloodOxygenModule.ID)) {
				moduleName = BloodOxygenModule.NAME;
			} else if (controller.getAttachInfo().mModuleId
					.equals(EarTemperatureModule.ID)) {
				moduleName = EarTemperatureModule.NAME;
			}
		}
		tvFormat = String.format(tvFormat, moduleName);
		tvAd1.setText(tvFormat);
		emptyView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				getNewItems(true);
			}
		});
		return emptyView;
	}

	protected void eventRegistration() {

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		// PropertyCenter.propertySupport.removePropertyChangeListener(this);
		controller.deleteObserver(adapter);
	}

	private TaskHost taskhost;

	private TaskHost createEmptyViewTaskHost() {
		if (taskhost == null) {
			taskhost = new TaskHost() {
				@Override
				public void onPostExecute(int requestCode, BaseResult result) {
					super.onPostExecute(requestCode, result);
					if (controller.getCache() == null
							|| controller.getCache().size() <= 0) {
						listView.setEmptyView(getItemEmptyView());
					}
				}
			};
		}
		return taskhost;
	}

	protected void getNewItems(boolean autoPullDown) {

		if (isAdded()) {
			refreshListView.setLastUpdatedLabel(getLastUpdateLabel());
			if (autoPullDown) {
				refreshListView.setPullDownRefreshing();
			}
			controller.getNewItemsFromServer(refreshListView,
					createEmptyViewTaskHost());
		} else {
			controller.getNewItemsFromServer(null, null);
		}

	}

	protected void getOldContents() {
		refreshListView.setLastUpdatedLabel(getLastUpdateLabel());
		controller.getDownPageItemsFromServer(refreshListView,
				createEmptyViewTaskHost());
	}

	private String getLastUpdateLabel() {
		String currentTime = TimeUtils.getTime(System.currentTimeMillis(),
				TimeUtils.MEASURE_RESULT_DETAILES_HISTORY_TIME);
		String format = getResources().getString(
				com.medzone.framework.R.string.last_update_time);
		return String.format(format, currentTime);
	}

	private void initAdapter() {

		if (adapter == null) {
			adapter = createAdapter();
			if (adapter != null) {
				adapter.setCache(controller.getCache());
			}
		}
		controller.addObserver(adapter);
	}

	protected abstract PagingListCacheAdapter<T, S, C> createAdapter();

	// @Override
	// public void propertyChange(PropertyChangeEvent event) {
	// if (event.getPropertyName().equals(
	// PropertyCenter.PROPERTY_REFRESH_HISTORY_DATA)) {
	// String typeTag = (String) event.getNewValue();
	//
	// String typeID = null;
	// if (typeTag == BloodPressure.BLOODPRESSURE_TAG) {
	// typeID = BloodPressureModule.ID;
	// } else if (typeTag == BloodOxygen.BLOODOXYGEN_TAG) {
	// typeID = BloodOxygenModule.ID;
	// } else if (typeTag == EarTemperature.EARTEMPERATURE_TAG) {
	// typeID = EarTemperatureModule.ID;
	// }
	// String curTypeID = controller.getAttachInfo().mModuleId;
	// if (TextUtils.equals(typeID, curTypeID)) {
	// getNewItems(true);
	// Log.v("propertyChange#getNewItems(true)"
	// + adapter.getCacheSimpleName());
	// } else {
	// Log.v("#忽视此次刷新" + adapter.getCacheSimpleName() + "的请求");
	// }
	//
	// }
	// }
}

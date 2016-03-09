package com.medzone.cloud.ui;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.LayoutParams;
import com.medzone.cloud.data.CurrentAccountManager;
import com.medzone.cloud.data.PropertyCenter;
import com.medzone.cloud.data.helper.AccountHelper;
import com.medzone.cloud.data.helper.SickHelper;
import com.medzone.cloud.ui.adapter.IllnessAdapter;
import com.medzone.cloud.ui.adapter.viewholder.SickInfoViewHolder;
import com.medzone.cloud.ui.dialog.error.ErrorDialogUtil;
import com.medzone.framework.data.bean.imp.Account;
import com.medzone.framework.data.bean.imp.SickInfo;
import com.medzone.framework.errorcode.ProxyErrorCode;
import com.medzone.framework.task.BaseResult;
import com.medzone.framework.task.TaskHost;
import com.medzone.mcloud.R;
import com.medzone.mcloud.R.array;
import com.medzone.mcloud.R.drawable;
import com.medzone.mcloud.R.id;
import com.medzone.mcloud.R.string;

public class SettingChangeBodyStateActivity extends BaseActivity implements
		OnClickListener {

	private ListView listBodyState;
	private IllnessAdapter adapter;
	private int sick1 = (int) SickInfo.INVALID_ID;
	private List<SickInfo> sickList;

	private void initActionBar() {
		ActionBar actionBar = getSupportActionBar();
		ActionBar.LayoutParams params = new ActionBar.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT,
				Gravity.CENTER);
		View view = LayoutInflater.from(this).inflate(
				R.layout.custom_actionbar_with_image, null);
		TextView title = (TextView) view.findViewById(R.id.actionbar_title);
		title.setText(string.actionbar_title_setting_body_state);
		ImageButton leftButton = (ImageButton) view
				.findViewById(id.actionbar_left);
		leftButton.setImageResource(drawable.personalinformationview_ic_cancel);
		leftButton.setOnClickListener(this);

		ImageButton rightButton = (ImageButton) view
				.findViewById(id.actionbar_right);
		rightButton.setImageResource(drawable.personalinformationview_ic_ok);
		rightButton.setOnClickListener(this);
		actionBar.setCustomView(view, params);
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		actionBar.setDisplayShowCustomEnabled(true);
	}

	@Override
	protected void preLoadData(Bundle savedInstanceState) {
		super.preLoadData(savedInstanceState);
		Integer sickObj = CurrentAccountManager.getCurAccount().getSick();
		if (sickObj != null) {
			sick1 = sickObj.intValue();
		}
		if (sick1 != SickInfo.INVALID_ID) {
			sickList = SickHelper.parseSick(sick1);
		} else {
			sickList = firstInitSickList();
		}

	}

	@Override
	protected void preInitUI() {
		initActionBar();
	}

	private List<SickInfo> firstInitSickList() {
		List<SickInfo> mSickList = new ArrayList<SickInfo>();

		String[] illages = getResources().getStringArray(
				array.illness_key_setting_values);
		String[] illagesSets = getResources().getStringArray(
				array.illness_value_setting_values);

		for (int i = 0; i < illages.length; i++) {
			SickInfo bs = new SickInfo();
			bs.setDiseaseName(illages[i]);
			bs.setOwner(illagesSets[i]);
			mSickList.add(bs);
		}
		return mSickList;
	}

	@Override
	protected void initUI() {
		setContentView(R.layout.activity_setting_change_bodystate);
		listBodyState = (ListView) findViewById(id.list_body_state);

		adapter = new IllnessAdapter(this, sickList);
		listBodyState.setAdapter(adapter);
		listBodyState.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				SickInfoViewHolder holder = (SickInfoViewHolder) view.getTag();
				holder.cbisDiseaseExist.toggle();
				sickList.get(position).setDiseaseExist(
						holder.cbisDiseaseExist.isChecked());
			}

		});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.actionbar_left:
			finish();
			break;
		case R.id.actionbar_right:
			final Account t;
			try {
				t = (Account) CurrentAccountManager.getCurAccount().clone();
				t.setSick((int) SickHelper.packSick(sickList));
				AccountHelper.doUpdateAccountTask(this, t, new TaskHost() {
					@Override
					public void onPostExecute(int requestCode, BaseResult result) {
						super.onPostExecute(requestCode, result);
						if (result.isSuccess()) {
							if (result.isServerDisposeSuccess()) {
								CurrentAccountManager.getCurAccount().setSick(
										t.getSick());
								PropertyCenter
										.getInstance()
										.firePropertyChange(
												PropertyCenter.PROPERTY_REFRESH_ACCOUNT,
												null,
												CurrentAccountManager
														.getCurAccount());
								finish();
							} else {
								ErrorDialogUtil.showErrorDialog(
										SettingChangeBodyStateActivity.this,
										ProxyErrorCode.TYPE_SETTING,
										result.getErrorCode(), true);
							}
						} else {
							ErrorDialogUtil.showErrorToast(
									SettingChangeBodyStateActivity.this,
									ProxyErrorCode.TYPE_SETTING,
									ProxyErrorCode.LocalError.CODE_10002);

						}
					}
				});
			} catch (CloneNotSupportedException e) {
				e.printStackTrace();
			}
			break;
		default:
			break;
		}
	}
}

/**
 * 
 */
package com.medzone.cloud.ui.fragment;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.LayoutParams;
import com.medzone.cloud.Constants;
import com.medzone.cloud.data.CurrentAccountManager;
import com.medzone.cloud.data.helper.GroupHelper;
import com.medzone.cloud.ui.MeasureActivity;
import com.medzone.cloud.ui.adapter.GetAllGroupMemberTestAdapter;
import com.medzone.cloud.ui.dialog.error.ErrorDialogUtil;
import com.medzone.framework.data.bean.imp.Account;
import com.medzone.framework.data.bean.imp.GroupMember;
import com.medzone.framework.errorcode.ProxyErrorCode;
import com.medzone.framework.fragment.BaseFragment;
import com.medzone.framework.network.NetworkClientResult;
import com.medzone.framework.task.BaseResult;
import com.medzone.framework.task.TaskHost;
import com.medzone.mcloud.R;
import com.medzone.mcloud.R.drawable;
import com.medzone.mcloud.R.id;
import com.medzone.mcloud.R.layout;
import com.medzone.mcloud.R.string;

public class PersonnelBeingMeasuredFragment extends BaseFragment implements
		OnClickListener {

	private ListView listView;
	private TextView listIsEmptyTV;
	private GetAllGroupMemberTestAdapter getAllGroupMemberTestAdapter;
	private List<GroupMember> testAccountList = new ArrayList<GroupMember>();
	private MeasureActivity mActivity;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mActivity = (MeasureActivity) activity;
		doGetAllTestMember();
	}

	@Override
	public void onResume() {
		super.onResume();
		initActionBar();
	}

	@Override
	protected void initActionBar() {
		ActionBar actionBar = getSherlockActivity().getSupportActionBar();
		ActionBar.LayoutParams params = new ActionBar.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT,
				Gravity.CENTER);
		View view = LayoutInflater.from(getSherlockActivity()).inflate(
				R.layout.custom_actionbar_with_image, null);
		TextView title = (TextView) view.findViewById(R.id.actionbar_title);
		title.setText(string.actionbar_title_the_researchers_chose_to_being_measured);
		ImageButton leftButton = (ImageButton) view
				.findViewById(id.actionbar_left);
		leftButton.setImageResource(drawable.public_ic_back);
		leftButton.setOnClickListener(this);

		actionBar.setCustomView(view, params);
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		actionBar.setDisplayShowCustomEnabled(true);
		actionBar.show();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);

		View view = inflater.inflate(layout.personnel_being_measured_fragment,
				container, false);

		listView = (ListView) view
				.findViewById(id.personnel_being_measured_list);
		listIsEmptyTV = (TextView) view
				.findViewById(id.personnel_being_measured_list_empty);

		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		getAllGroupMemberTestAdapter = new GetAllGroupMemberTestAdapter(
				getActivity());
		getAllGroupMemberTestAdapter.setContent(testAccountList);
		listView.setAdapter(getAllGroupMemberTestAdapter);
		listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				GroupMember tmp = testAccountList.get(position);
				mActivity.setGroupmember(tmp);
				comeBack();
			}
		});

	}

	private void notifyViewChanged() {
		if (testAccountList == null || testAccountList.size() <= 0) {
			listIsEmptyTV.setVisibility(View.VISIBLE);
			listIsEmptyTV
					.setText(string.the_researchers_chose_to_being_measured_isEmpty);
		} else {
			listIsEmptyTV.setVisibility(View.GONE);
		}
	}

	private void doGetAllTestMember() {

		GroupHelper.doGetMeasureList(getActivity(), CurrentAccountManager
				.getCurAccount().getAccessToken(), new TaskHost() {
			@Override
			public void onPostExecute(int requestCode, BaseResult result) {
				super.onPostExecute(requestCode, result);
				if (result.isSuccess()) {
					NetworkClientResult res = (NetworkClientResult) result;
					if (res.isServerDisposeSuccess()) {
						Account account = CurrentAccountManager.getCurAccount();
						List<GroupMember> list = GroupMember
								.createGroupMemberList(res,
										account.getAccountID());
						GroupMember groupmember = new GroupMember();
						groupmember.setAccountID(account.getAccountID());
						groupmember.setNickname(account.getNickname());
						groupmember.setRealName(account.getRealName());
						groupmember.setRemark(null);
						groupmember.setHeadPortRait(account.getHeadPortRait());
						testAccountList.add(groupmember);
						testAccountList.addAll(1, list);
						getAllGroupMemberTestAdapter
								.setContent(testAccountList);
						notifyViewChanged();
					} else {
						ErrorDialogUtil.showErrorToast(mActivity,
								ProxyErrorCode.TYPE_MEASURE,
								result.getErrorCode());
					}
				} else {
					comeBack();
				}
			}

		});

	}

	private void comeBack() {

		if (mActivity.type.equals(Constants.mCloud_O)) {
			mActivity.comeBackBloodOxygenConnect(this);
		} else if (mActivity.type.equals(Constants.mCloud_P)) {
			mActivity.comeBackBloodPressureConnect(this);
		} else if (mActivity.type.equals(Constants.mCloud_ET)) {
			mActivity.comeBackEarTemperatureConnect(this);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case id.actionbar_left:
			comeBack();
			break;

		default:
			break;
		}

	}

}

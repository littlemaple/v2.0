package com.medzone.cloud.ui.fragment;

import java.util.List;

import android.os.AsyncTask.Status;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar.LayoutParams;
import com.medzone.cloud.CloudApplication;
import com.medzone.cloud.data.CurrentAccountManager;
import com.medzone.cloud.task.GetAllGroupTask;
import com.medzone.cloud.ui.adapter.ChooseCarriedExpandableAdapter;
import com.medzone.cloud.ui.widget.CustomDialogProgressWithImage;
import com.medzone.framework.Config;
import com.medzone.framework.data.bean.imp.Group;
import com.medzone.framework.fragment.BaseFragment;
import com.medzone.framework.network.NetworkClientResult;
import com.medzone.framework.task.BaseResult;
import com.medzone.framework.task.Progress;
import com.medzone.framework.task.TaskHost;
import com.medzone.mcloud.R;
import com.medzone.mcloud.R.id;
import com.medzone.mcloud.R.layout;
import com.medzone.mcloud.R.string;

/**
 * 
 * @author junqi
 * 
 *         选择分享的载体，这里实际运用的场景是选择群id
 */
public class ChooseCarriedFragment extends BaseFragment implements
		View.OnClickListener {

	private View containerView;
	private ChooseCarriedExpandableAdapter mChooseCarriedExpandableAdapter;
	private ExpandableListView mChooseCarriedExpandableListView;

	private GetAllGroupTask getAllGroupTask;
	private View actionBarView;

	@Override
	protected void initActionBar() {
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, CloudApplication.actionBarHeight,
				Gravity.CENTER);
		actionBarView = LayoutInflater.from(getActivity()).inflate(
				R.layout.custom_actionbar_with_image, null);
		actionBarView.setLayoutParams(params);

		TextView title = (TextView) actionBarView
				.findViewById(R.id.actionbar_title);
		title.setText(R.string.actionbar_title_share_choose_carried);
		title.setOnClickListener(this);
		ImageButton leftButton = (ImageButton) actionBarView
				.findViewById(R.id.actionbar_left);
		leftButton
				.setImageResource(R.drawable.personalinformationview_ic_cancel);
		leftButton.setOnClickListener(this);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		initActionBar();
		containerView = inflater.inflate(layout.fragment_share_choosecarried,
				container, false);
		LinearLayout actionbar = (LinearLayout) containerView
				.findViewById(id.actionbar);
		actionbar.addView(actionBarView);
		mChooseCarriedExpandableListView = (ExpandableListView) containerView
				.findViewById(R.id.expandableListView_list);
		mChooseCarriedExpandableAdapter = new ChooseCarriedExpandableAdapter(
				getActivity());
		mChooseCarriedExpandableAdapter.setDisplayExtraCare(false);
		mChooseCarriedExpandableListView
				.setAdapter(mChooseCarriedExpandableAdapter);
		mChooseCarriedExpandableListView.setGroupIndicator(null);

		return containerView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		doGetGroupListTask();
	}

	private void doGetGroupListTask() {

		if (getAllGroupTask != null
				&& getAllGroupTask.getStatus() == Status.RUNNING)
			return;
		String accessToken = CurrentAccountManager.getCurAccount()
				.getAccessToken();

		// XXX 我认为这里是一个非常操蛋的需求，词不达意，下版本中建议更改文案。
		Progress progress = new CustomDialogProgressWithImage(getActivity(),
				"正在生成分享", getResources().getDrawable(R.drawable.set_ic_load));
		getAllGroupTask = new GetAllGroupTask(getActivity(), accessToken);
		getAllGroupTask.setProgress(progress);
		getAllGroupTask.setTaskHost(new TaskHost() {
			@Override
			public void onPostExecute(int requestCode, BaseResult result) {
				super.onPostExecute(requestCode, result);
				if (result.isSuccess()) {

					NetworkClientResult res = (NetworkClientResult) result;

					if (res.isServerDisposeSuccess()) {
						List<Group> groupList = Group.createGroupList(res,
								CurrentAccountManager.getCurAccount()
										.getAccountID());

						mChooseCarriedExpandableAdapter
								.setGroupContent(groupList);
						if (groupList.size() != 0) {
							mChooseCarriedExpandableListView.expandGroup(0);
							mChooseCarriedExpandableListView.expandGroup(1);
						}

					} else {
						showToast(res.getResponseResult().toString());
					}
				} else {
					showToast(string.error_net_connect);
				}
			}
		});
		getAllGroupTask.execute();

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.actionbar_left:
			getActivity().finish();
			break;
		case R.id.actionbar_title:
			if (Config.isDeveloperMode) {
				doGetGroupListTask();
			}
			break;
		default:
			break;
		}
	}

}

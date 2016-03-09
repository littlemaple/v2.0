package com.medzone.cloud.ui.adapter.viewholder;

import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.medzone.cloud.CloudImageLoader;
import com.medzone.cloud.Constants;
import com.medzone.cloud.data.CurrentAccountManager;
import com.medzone.cloud.data.TemporaryData;
import com.medzone.cloud.ui.ShareActivity;
import com.medzone.cloud.ui.fragment.ShareDetailPageFragment;
import com.medzone.framework.data.bean.imp.Group;
import com.medzone.framework.view.RoundedImageView;
import com.medzone.mcloud.R.id;
import com.medzone.mcloud.R.string;

public class ChooseCarriedViewHolder extends BaseViewHolder {

	private View rootView;
	private TextView tvGroupName, tvRemark;
	private RoundedImageView ivGroupIcon;

	/**
	 * @param rootView
	 */
	public ChooseCarriedViewHolder(View rootView) {
		super(rootView);
		this.rootView = rootView;
	}

	@Override
	public void init(View view) {
		tvGroupName = (TextView) view.findViewById(id.tv_group_name);
		tvRemark = (TextView) view.findViewById(id.tv_remark);
		ivGroupIcon = (RoundedImageView) view
				.findViewById(id.image_child_avator);
	}

	@Override
	public void fillFromItem(Object item) {
		final Group group = (Group) item;

		tvRemark.setVisibility(View.GONE);
		if (isGroupOwner(group)) {
			tvRemark.setVisibility(View.VISIBLE);
			tvRemark.setText(string.group_owner);
		}
		tvGroupName.setText(group.getName());
		if (group.getHeadPortRait() != null) {
			CloudImageLoader.getInstance().getImageLoader().displayImage(
					group.getHeadPortRait(), ivGroupIcon);
		}

		rootView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				// removeCarriedFragment(v.getContext());
				// 预览分享内容
				TemporaryData.save(Constants.TEMPORARYDATA_KEY_SHARE_CARRIED,
						group);
				commitPreviewDetailFragment();

			}
		});
	}

	private boolean isGroupOwner(Group group) {

		return group.equals(CurrentAccountManager.getCurAccount());
	}

	// private void removeCarriedFragment(Context context) {
	// ShareActivity activity = (ShareActivity) context;
	// FragmentTransaction ft = activity.getSupportFragmentManager()
	// .beginTransaction();
	// ft.hide(activity.mChooseCarriedFragment);
	// ft.commitAllowingStateLoss();
	// }

	private void commitPreviewDetailFragment() {

		ShareActivity activity = ShareActivity.instance;

		// 被负载的窗口存在，并且可见时，才允许被附加。
		if (activity != null) {
			FragmentTransaction ft = activity.getSupportFragmentManager()
					.beginTransaction();
			ShareDetailPageFragment newFragment = new ShareDetailPageFragment();
			newFragment.show(ft, ShareDetailPageFragment.class.getName());
		}
	}
}

///**
// * 
// */
//package com.medzone.cloud.ui.adapter.viewholder;
//
//import android.view.View;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import com.medzone.mcloud.R;
//import com.medzone.mcloud.R.id;
//
///**
// * @author ChenJunQi.
// * 
// */
//public class GroupParentViewHolder extends BaseViewHolder {
//
//	private View rootView;
//	private boolean isExpanded;
//	private ImageView ivGroupIcon;
//	private TextView tvGroupName;
//	private ImageView ivArrow;
//
//	/**
//	 * @param rootView
//	 */
//	public GroupParentViewHolder(View rootView) {
//		super(rootView);
//		this.rootView = rootView;
//	}
//
//	/**
//	 * 
//	 */
//	public GroupParentViewHolder(View rootView, boolean isExpanded) {
//		this(rootView);
//		this.isExpanded = isExpanded;
//	}
//
//	@Override
//	public void init(View view) {
//		ivGroupIcon = (ImageView) view.findViewById(id.group_top_imageview);
//		ivArrow = (ImageView) view.findViewById(id.group_top_orientation);
//		tvGroupName = (TextView) view.findViewById(id.group_top_textView);
//	}
//
//	@Override
//	public void fillFromItem(Object item) {
//		super.fillFromItem(item);
//		final int groupPosition = (Integer) item;
//
//		TextView title = (TextView) rootView
//				.findViewById(R.id.group_top_textView);
//		title.setText(">>>" + groupPosition);
//		ImageView image = (ImageView) rootView
//				.findViewById(R.id.group_top_orientation);
//		if (isExpanded) {
//			image.setBackgroundResource(R.drawable.open_group);
//		} else {
//			image.setBackgroundResource(R.drawable.close_group);
//		}
//
//	}
//}

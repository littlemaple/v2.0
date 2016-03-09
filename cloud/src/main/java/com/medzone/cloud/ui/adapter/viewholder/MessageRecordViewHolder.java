/**
 * 
 */
package com.medzone.cloud.ui.adapter.viewholder;

import java.util.Date;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.medzone.cloud.CloudImageLoader;
import com.medzone.cloud.Constants;
import com.medzone.cloud.data.CurrentAccountManager;
import com.medzone.cloud.data.TemporaryData;
import com.medzone.cloud.data.helper.MeasureDataUtil;
import com.medzone.cloud.ui.GroupChatActivity;
import com.medzone.cloud.ui.GroupPersonDetailActivity;
import com.medzone.cloud.ui.GroupServiceForMemberDetailActivity;
import com.medzone.cloud.ui.HealthCentreWebViewActivity;
import com.medzone.cloud.ui.SettingPersonalInfoActivity;
import com.medzone.framework.Log;
import com.medzone.framework.data.bean.imp.BloodOxygen;
import com.medzone.framework.data.bean.imp.BloodPressure;
import com.medzone.framework.data.bean.imp.BloodPressure.BloodPressureUtil;
import com.medzone.framework.data.bean.imp.EarTemperature;
import com.medzone.framework.data.bean.imp.Group;
import com.medzone.framework.data.bean.imp.GroupMember;
import com.medzone.framework.data.bean.imp.Message;
import com.medzone.framework.util.TimeUtils;
import com.medzone.framework.view.RoundedImageView;
import com.medzone.mcloud.R;
import com.medzone.mcloud.R.id;
import com.medzone.mcloud.R.string;

/**
 * @author ChenJunQi.
 * 
 */
public class MessageRecordViewHolder extends BaseViewHolder {

	private TextView tvPostTime;
	private RoundedImageView ivAvatar;

	private TextView tvType, tvMeasureTime;
	private TextView tvItemName;
	private ImageView ivResultImage;
	private LinearLayout llFirst, llSecond;
	private TextView tvUnitFirst, tvValueFirst, tvUnitEnFirst;
	private TextView tvUnitSecond, tvValueSecond, tvUnitEnSecond;
	private LinearLayout llChatContent;

	private Context context;

	public MessageRecordViewHolder(View itemView) {
		super(itemView);
	}

	public void init(View view) {
		context = view.getContext();
		tvPostTime = (TextView) view.findViewById(R.id.chatting_time_tv);
		ivAvatar = (RoundedImageView) view
				.findViewById(R.id.chatting_avatar_iv);
		tvType = (TextView) view.findViewById(id.tv_measure_type);
		tvMeasureTime = (TextView) view.findViewById(id.tv_measure_time);
		ivResultImage = (ImageView) view.findViewById(id.iv_result_icon);
		llFirst = (LinearLayout) view.findViewById(id.ll_1);
		llSecond = (LinearLayout) view.findViewById(id.ll_2);
		tvUnitFirst = (TextView) view.findViewById(id.tv_unit_1);
		tvValueFirst = (TextView) view.findViewById(id.tv_value_1);
		tvUnitEnFirst = (TextView) view.findViewById(id.tv_unit_en_1);
		tvUnitSecond = (TextView) view.findViewById(id.tv_unit_2);
		tvValueSecond = (TextView) view.findViewById(id.tv_value_2);
		tvUnitEnSecond = (TextView) view.findViewById(id.tv_unit_en_2);
		tvItemName = (TextView) view.findViewById(R.id.tv_name);
		llChatContent = (LinearLayout) view
				.findViewById(R.id.ll_chatting_content);

	}

	@Override
	public void fillFromItem(Object item) {
		final Message message = (Message) item;
		final Group bindGroup = GroupChatActivity.getCurGroup();

		CloudImageLoader.getInstance().getImageLoader()
				.displayImage(message.getIconUrl(), ivAvatar);
		tvPostTime.setText(getDateDescription(message.getPostTime()));
		tvItemName.setText(message.getAccountDisplayName());

		String type = message.getRecordType();
		if (TextUtils.equals(type, BloodOxygen.BLOODOXYGEN_TAG)) {
			fillBloodOxygenView(message);
		} else if (TextUtils.equals(type, BloodPressure.BLOODPRESSURE_TAG)) {
			fillBloodPressureView(message);
		} else if (TextUtils.equals(type, EarTemperature.EARTEMPERATURE_TAG)) {
			fillEarTemperatureView(message);
		}

		llChatContent.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				jump2URL(message);
			}
		});

		ivAvatar.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				final int memberID = message.getAccountID();

				if (memberID == CurrentAccountManager.getCurAccount()
						.getAccountID()) {
					Intent intent = new Intent(context,
							SettingPersonalInfoActivity.class);
					context.startActivity(intent);
				} else {

					GroupMember member = GroupChatActivity
							.searchGroupMember(memberID);

					// TODO
					// 这段代码在MessageLinkViewHolder、MessageRecordViewHolder中均出现的重复！
					if (bindGroup.getType() == Group.TYPE_SERVICE
							&& member.equals(bindGroup)) {

						TemporaryData.save(Group.class.getName(), bindGroup);
						Intent intent = new Intent(context,
								GroupServiceForMemberDetailActivity.class);
						context.startActivity(intent);

					} else {

						if (member != null) {
							member.setBindGroup(bindGroup);
							TemporaryData.save(GroupMember.class.getName(),
									member);
							Intent intent = new Intent(context,
									GroupPersonDetailActivity.class);
							context.startActivity(intent);
						}
					}
				}
			}
		});
	}

	private void jump2URL(Message message) {
		// 临时屏蔽跳转URL功能
		String url = message.getRecordURL();
		Log.i("message record viewholder url:" + url);
		if (!TextUtils.isEmpty(url)) {
			TemporaryData.save(Constants.TEMPORARYDATA_KEY_URL, url);
			TemporaryData.save(Constants.TEMPORARYDATA_KEY_TITLE,
					convertTitle(message));
			Intent intent = new Intent(context,
					HealthCentreWebViewActivity.class);
			context.startActivity(intent);
		}

	}

	private String convertTitle(Message msg) {
		if (TextUtils.isEmpty(msg.getLinkTitle())) {
			if (TextUtils.equals(msg.getRecordType(),
					BloodOxygen.BLOODOXYGEN_TAG))
				return "血氧单次详情";
			else if (TextUtils.equals(msg.getRecordType(),
					BloodPressure.BLOODPRESSURE_TAG))
				return "血压单次详情";
			else
				return "查看分享";
		} else {
			return msg.getLinkTitle();
		}
	}

	private void fillBloodOxygenView(Message message) {
		String typeDescription = context.getResources().getString(
				string.blood_oxygen);
		tvType.setText(typeDescription);
		llSecond.setVisibility(View.VISIBLE);

		tvMeasureTime.setText(getMeasureTime(message.getRecordTime()));

		tvUnitFirst.setText("血氧饱和度");
		tvValueFirst.setText(message.getRecordValue1());
		tvUnitEnFirst.setText("%");
		tvUnitSecond.setText("心率");
		tvValueSecond.setText(message.getRecordValue2());
		tvUnitEnSecond.setText("bpm");
		MeasureDataUtil
				.BloodOxygenFlag(ivResultImage, message.getRecordState());

	}

	private void fillBloodPressureView(Message message) {
		String typeDescription = context.getResources().getString(
				string.blood_pressure);
		tvType.setText(typeDescription);
		llSecond.setVisibility(View.VISIBLE);

		tvMeasureTime.setText(getMeasureTime(message.getRecordTime()));
		tvUnitFirst.setText("收缩压/舒张压");

		// FIXME 将这里的处理移至血压控制器中
		Float high = Float.valueOf(message.getRecordValue1());
		Float low = Float.valueOf(message.getRecordValue2());

		if (message.getRecordUnit() == 1) {

			float highValue = BloodPressureUtil.convertMMHG2KPA(high
					.floatValue());
			float lowValue = BloodPressureUtil
					.convertMMHG2KPA(low.floatValue());
			tvValueFirst.setText(highValue + "/" + lowValue);
			tvUnitEnFirst.setText(BloodPressure.BLOODPRESSURE_UNIT_KPA);
		} else {

			tvValueFirst.setText(high.intValue() + "/" + low.intValue());
			tvUnitEnFirst.setText(BloodPressure.BLOODPRESSURE_UNIT_MMHG);
		}

		tvUnitSecond.setText("心率");
		tvValueSecond.setText(message.getRecordValue3());
		tvUnitEnSecond.setText(BloodPressure.BLOODPRESSURE_UNIT_RATE);

		MeasureDataUtil.BloodPressureFlag(ivResultImage,
				message.getRecordState());
	}

	private void fillEarTemperatureView(Message message) {
		String typeDescription = context.getResources().getString(
				string.ear_temperature);
		tvType.setText(typeDescription);
		llSecond.setVisibility(View.GONE);

		tvMeasureTime.setText(getMeasureTime(message.getRecordTime()));

		tvUnitFirst.setText("温度");
		tvValueFirst.setText(message.getRecordValue1());
		tvUnitEnFirst.setText("℃");

		switch (message.getRecordState()) {
		case EarTemperature.TEMPERATURE_STATE_HIGH_FEVER:
			ivResultImage
					.setImageResource(R.drawable.testresultsview_testresult_graph_gaore);
			break;
		case EarTemperature.TEMPERATURE_STATE_LOW:
			ivResultImage
					.setImageResource(R.drawable.testresultsview_testresult_graph_dire);
			break;
		case EarTemperature.TEMPERATURE_STATE_FEVER:
			ivResultImage
					.setImageResource(R.drawable.testresultsview_testresult_graph_fare);
			break;
		case EarTemperature.TEMPERATURE_STATE_NORMAL:
			ivResultImage
					.setImageResource(R.drawable.testresultsview_testresult_graph_normal);
			break;
		default:
			ivResultImage
					.setImageResource(R.drawable.testresultsview_testresult_graph_normal);
		}
	}

	private String getDateDescription(Date date) {
		return TimeUtils.getChatTime(date.getTime());
	}

	private String getMeasureTime(Date date) {
		return TimeUtils.getTime(date.getTime(), TimeUtils.YYYY_MM_DD_HH_MM_SS);
	}
}

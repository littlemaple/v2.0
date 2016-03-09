package com.medzone.cloud.ui;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask.Status;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.LayoutParams;
import com.medzone.cloud.CloudApplication;
import com.medzone.cloud.CloudImageLoader;
import com.medzone.cloud.Constants;
import com.medzone.cloud.data.CurrentAccountManager;
import com.medzone.cloud.data.PropertyCenter;
import com.medzone.cloud.data.TemporaryData;
import com.medzone.cloud.data.helper.AccountHelper;
import com.medzone.cloud.data.helper.AccountModuleHelper;
import com.medzone.cloud.data.helper.SickHelper;
import com.medzone.cloud.module.modules.AccountModule.IGetDetailCallBack;
import com.medzone.cloud.task.LogoutTask;
import com.medzone.cloud.ui.dialog.error.ErrorDialogUtil;
import com.medzone.cloud.ui.fragment.group.GroupMemberManagerFragment;
import com.medzone.cloud.util.ImageCropUtil;
import com.medzone.framework.data.bean.imp.Account;
import com.medzone.framework.data.bean.imp.Account.Gender;
import com.medzone.framework.errorcode.ProxyCode;
import com.medzone.framework.errorcode.ProxyErrorCode;
import com.medzone.framework.network.NetworkClientResult;
import com.medzone.framework.task.BaseResult;
import com.medzone.framework.task.TaskHost;
import com.medzone.framework.task.progress.CustomDialogProgress;
import com.medzone.framework.util.NumberPickerUtil;
import com.medzone.framework.util.NumberPickerUtil.onDialogChooseListener;
import com.medzone.framework.util.TimeUtils;
import com.medzone.framework.util.Tools;
import com.medzone.framework.view.RoundedImageView;
import com.medzone.mcloud.R;
import com.medzone.mcloud.R.array;
import com.medzone.mcloud.R.drawable;
import com.medzone.mcloud.R.id;
import com.medzone.mcloud.R.string;

/***
 * 
 * @author ChenJunQi.
 * 
 * 
 */
public class SettingPersonalInfoActivity extends BaseActivity implements
		OnClickListener, PropertyChangeListener {

	private static final String IMAGE_FILE_NAME = "_icon.jpg";

	private static final int RESULT_CODE_CAMERA_OPEN = 1;
	private static final int RESULT_CODE_IMAGE_TAILORING = 2;
	private static final int RESULT_CODE_PREGNANT = 3;
	private static final int RESULT_CODE_IMAGE_CHOOSE = 4;
	private static final int RESULT_CODE_IMAGE_CHOOSE_KITKAT = 5;
	private String[] genders;
	private int genderCheckedItem;

	private AlertDialog avatorDialog;

	private RoundedImageView ivHeadPortrait;
	private LinearLayout llNickName, llPregnant, llSwitchAvatar, llGender,
			llHeight, llBirthday, llWeight, llBodyState;
	private TextView tvPersonName, tvGender, tvBirthday, tvHeight, tvWeight,
			tvPregnant, tvBodyState;
	private Button btnLogout;

	private LogoutTask task;
	private boolean isChanged = false;// 标记个人资料页，是否有变更？
	private Account curAccount;

	private void initActionBar() {
		ActionBar actionBar = getSupportActionBar();
		ActionBar.LayoutParams params = new ActionBar.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT,
				Gravity.CENTER);
		View view = LayoutInflater.from(this).inflate(
				R.layout.custom_actionbar_with_image, null);
		TextView title = (TextView) view.findViewById(R.id.actionbar_title);
		title.setText(string.actionbar_title_personal);
		ImageButton leftButton = (ImageButton) view
				.findViewById(id.actionbar_left);
		leftButton.setImageResource(drawable.public_ic_back);
		leftButton.setOnClickListener(this);
		actionBar.setCustomView(view, params);
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		actionBar.setDisplayShowCustomEnabled(true);
	}

	@Override
	protected void preLoadData(Bundle savedInstanceState) {
		super.preLoadData(savedInstanceState);

		curAccount = CurrentAccountManager.getCurAccount();
		NumberPickerUtil.loadActivityContext(this);
		PropertyCenter.addPropertyChangeListener(this);
		genders = getResources().getStringArray(array.gender_setting_values);
	}

	@Override
	protected void preInitUI() {
		initActionBar();
	}

	@Override
	protected void initUI() {
		setContentView(R.layout.activity_setting_personal);

		llNickName = (LinearLayout) findViewById(R.id.relay_nickname);
		llPregnant = (LinearLayout) findViewById(R.id.relay_pregnant);
		llSwitchAvatar = (LinearLayout) findViewById(R.id.relay_switch_avatar);
		llGender = (LinearLayout) findViewById(R.id.relay_gender);
		llBirthday = (LinearLayout) findViewById(R.id.relay_birthday);
		llHeight = (LinearLayout) findViewById(R.id.relay_height);
		llWeight = (LinearLayout) findViewById(R.id.relay_weight);
		llBodyState = (LinearLayout) findViewById(R.id.relay_body_state);

		ivHeadPortrait = (RoundedImageView) findViewById(R.id.im_personal_icon);

		tvGender = (TextView) findViewById(R.id.tv_personal_gender);
		tvPersonName = (TextView) findViewById(R.id.tv_personal_nickname);
		tvBirthday = (TextView) findViewById(R.id.tv_personal_birth);
		tvHeight = (TextView) findViewById(R.id.tv_personal_tall);
		tvWeight = (TextView) findViewById(R.id.tv_personal_weight);
		tvBodyState = (TextView) findViewById(R.id.tv_personal_health);
		tvPregnant = (TextView) findViewById(R.id.tv_personal_pregnant);

		btnLogout = (Button) findViewById(R.id.btn_logout);

		fillView(curAccount);
	}

	private void fillAvatarView(final Account account) {
		if (!TextUtils.isEmpty(account.getHeadPortRait())) {
			CloudImageLoader.getInstance().getImageLoader()
					.displayImage(account.getHeadPortRait(), ivHeadPortrait);
		}
	}

	private void fillView(final Account account) {

		fillAvatarView(account);

		if (account.getNickname() != null) {
			tvPersonName.setText(account.getNickname());
		}
		if (account.isMale() != null) {
			tvGender.setText(account.isMale() ? Gender.MALE : Gender.FEMALE);
			genderCheckedItem = account.isMale() ? 0 : 1;
		}
		if (account.getBirthday() != null)

			tvBirthday.setText(TimeUtils.getTime(account.getBirthday()
					.getTime(), TimeUtils.DATE_FORMAT_DATE));

		if (account.getTall() != null) {
			tvHeight.setText(String.valueOf(account.getTall().intValue()));
			tvHeight.append(Constants.UNIT_ch_CM);
		}
		if (account.getWeight() != null) {
			tvWeight.setText(String.valueOf(account.getWeight().intValue()));
			tvWeight.append(Constants.UNIT_ch_KG);
		}
		if (account.getPrebornday() != null) {

			Date preborndayDate = TimeUtils.getDate(account.getPrebornday(),
					TimeUtils.DATE_FORMAT_DATE);

			long intervalDays = TimeUtils.getDaysBetween(new Date(),
					preborndayDate);
			int ret = (int) (Constants.DURING_PREGNANCY - intervalDays);

			String result = null;
			if (ret <= 0) {
				result = 0 + Constants.UNIT_ch_DAY;
			} else if (ret >= Constants.DURING_PREGNANCY) {
				result = getResources().getString(R.string.finished);
			} else {
				result = String.valueOf(ret) + Constants.UNIT_ch_DAY;
			}
			tvPregnant.setText(result);
		}
		if (account.getSick() != null)
			tvBodyState.setText(SickHelper.getSickMessage(account.getSick()));
	}

	private void updateAccount(final Account account) {
		// 因为获取账户资料时，没有返回头像，那么则修改其他字段时不对其进行刷新
		if (account.getHeadPortRait() != null) {
			curAccount.setHeadPortRait(account.getHeadPortRait());
		}
		curAccount.setNickname(account.getNickname());
		curAccount.setBirthday(account.getBirthday());
		curAccount.setTall(account.getTall());
		curAccount.setWeight(account.getWeight());
		curAccount.setPrebornday(account.getPrebornday());
		curAccount.setMale(account.isMale());
		genderCheckedItem = account.isMale() ? 0 : 1;

	}

	@Override
	protected void postInitUI() {
		llNickName.setOnClickListener(this);
		llPregnant.setOnClickListener(this);
		llSwitchAvatar.setOnClickListener(this);
		llGender.setOnClickListener(this);
		llBirthday.setOnClickListener(this);
		llHeight.setOnClickListener(this);
		llWeight.setOnClickListener(this);
		llBodyState.setOnClickListener(this);
		btnLogout.setOnClickListener(this);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		PropertyCenter.removePropertyChangeListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.actionbar_left:
			finish();
			break;
		case R.id.relay_nickname:
			String tmpName = tvPersonName.getText().toString();
			TemporaryData.save(Account.NAME_FIELD_NICKNAME, tmpName);
			startActivity(new Intent(getApplicationContext(),
					SettingChangeNickNameActivity.class));
			break;
		case R.id.relay_switch_avatar:
			showAvatarDialog();
			break;
		case R.id.relay_gender:
			showGenderDialog();
			break;
		case R.id.relay_birthday:
			showBirthDayDialog(tvBirthday);
			break;
		case R.id.relay_height:
			showHeightDialog(tvHeight);
			break;
		case R.id.relay_weight:
			showWeightDialog(tvWeight);
			break;
		case R.id.relay_pregnant:

			TemporaryData.save(Account.NAME_FIELD_PREGNANT,
					curAccount.getPrebornday());
			startActivityForResult(new Intent(getApplicationContext(),
					SettingChangePregnantActivity.class), RESULT_CODE_PREGNANT);

			break;
		case R.id.relay_body_state:
			startActivity(new Intent(getApplicationContext(),
					SettingChangeBodyStateActivity.class));
			break;
		case R.id.btn_logout:
			doLogoutTask();
			break;
		default:
			break;
		}
	}

	public void jumpToTarget(Class<?> clz) {
		startActivity(new Intent(SettingPersonalInfoActivity.this, clz));
	}

	private void showAvatarDialog() {

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setCancelable(true);
		View view = getLayoutInflater().inflate(R.layout.dialog_avator_layout,
				null);
		view.findViewById(R.id.camera).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						Intent intentFromCapture = new Intent(
								MediaStore.ACTION_IMAGE_CAPTURE);
						// 判断存储卡是否可以用，可用进行存储
						if (Tools.hasSdcard()) {

							intentFromCapture.putExtra(MediaStore.EXTRA_OUTPUT,
									Uri.fromFile(new File(Environment
											.getExternalStorageDirectory(),
											curAccount.getAccountID()
													+ IMAGE_FILE_NAME)));
						}

						startActivityForResult(intentFromCapture,
								RESULT_CODE_CAMERA_OPEN);
					}
				});
		view.findViewById(R.id.album).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (CloudApplication
						.isNewAPI(android.os.Build.VERSION_CODES.KITKAT)) {
					Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
					// intent.addCategory(Intent.CATEGORY_OPENABLE);
					intent.setType("image/*");
					startActivityForResult(intent,
							RESULT_CODE_IMAGE_CHOOSE_KITKAT);
				} else {
					Intent intentFromGallery = new Intent();
					intentFromGallery.setType("image/*"); // 设置文件类型
					intentFromGallery.setAction(Intent.ACTION_GET_CONTENT);
					startActivityForResult(intentFromGallery,
							RESULT_CODE_IMAGE_CHOOSE);
				}
			}
		});

		builder.setView(view);
		avatorDialog = builder.create();
		avatorDialog.setCanceledOnTouchOutside(true);
		avatorDialog.show();
	}

	@Override
	protected void onStart() {
		super.onStart();
		if (avatorDialog != null && avatorDialog.isShowing())
			avatorDialog.dismiss();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// 结果码不等于取消时候
		if (resultCode != RESULT_CANCELED) {
			Uri originalUri = null;
			switch (requestCode) {
			case RESULT_CODE_IMAGE_CHOOSE:
				originalUri = data.getData();
				startPhotoZoom(originalUri);
				break;
			case RESULT_CODE_IMAGE_CHOOSE_KITKAT:

				String path = ImageCropUtil.getPath(getApplicationContext(),
						data.getData());
				originalUri = Uri.fromFile(new File(path));
				startPhotoZoom(originalUri);

				break;
			case RESULT_CODE_CAMERA_OPEN:

				if (Tools.hasSdcard()) {
					File tempFile = new File(
							Environment.getExternalStorageDirectory() + "/"
									+ curAccount.getAccountID()
									+ IMAGE_FILE_NAME);
					startPhotoZoom(Uri.fromFile(tempFile));
				} else {
					ErrorDialogUtil.showErrorDialog(
							SettingPersonalInfoActivity.this,
							ProxyErrorCode.TYPE_SETTING,
							ProxyCode.LocalError.CODE_10004, true);
				}
				break;
			case RESULT_CODE_IMAGE_TAILORING:
				setCropingImageFile(data);
				break;
			case RESULT_CODE_PREGNANT:
				String pregnant = (String) TemporaryData
						.get(Account.NAME_FIELD_PREGNANT);
				try {
					Account t = (Account) curAccount.clone();
					t.setPrebornday(pregnant);
					doUpdateAccount(t);
				} catch (CloneNotSupportedException e) {
					e.printStackTrace();
				}
				break;
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	/**
	 * 裁剪图片方法实现
	 * 
	 * @param uri
	 */
	public void startPhotoZoom(Uri uri) {

		if (uri == null)
			return;
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		// 设置裁剪
		intent.putExtra("crop", "true");
		// aspectX aspectY 是宽高的比例
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		// outputX outputY 是裁剪图片宽高
		intent.putExtra("outputX", 320);
		intent.putExtra("outputY", 320);
		intent.putExtra("scale", true);
		intent.putExtra("return-data", true);
		intent.putExtra("noFaceDetection", true); // no face detection
		startActivityForResult(intent, 2);
	}

	/**
	 * 保存裁剪之后的图片数据
	 * 
	 * @param picdata
	 */
	private void setCropingImageFile(Intent data) {

		if (data == null)
			return;
		Bundle extras = data.getExtras();
		Bitmap photo = null;
		if (extras != null) {
			photo = extras.getParcelable("data");
			Drawable drawable = new BitmapDrawable(photo);
			ivHeadPortrait.setImageDrawable(drawable);
		}
		if (photo != null) {
			ByteArrayOutputStream temp = new ByteArrayOutputStream();
			photo.compress(Bitmap.CompressFormat.PNG, 100, temp);
			byte[] byteArr = temp.toByteArray();
			String headPortRait = Base64
					.encodeToString(byteArr, Base64.DEFAULT);

			try {
				Account t = (Account) curAccount.clone();
				t.setHeadPortRait(headPortRait);
				t.setTag(Account.TAG_FORCE_UPDATE);
				doUpdateAccount(t);
			} catch (CloneNotSupportedException e) {
				e.printStackTrace();
			}

		}

	}

	private void showGenderDialog() {
		AlertDialog dialog = null;
		AlertDialog.Builder builder = new AlertDialog.Builder(this)
				.setSingleChoiceItems(genders, genderCheckedItem,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								switch (which) {
								case 0:
									setGender(true);
									if (dialog != null)
										dialog.dismiss();
									break;
								case 1:
									setGender(false);
									if (dialog != null)
										dialog.dismiss();
									break;
								}
							}
						});
		dialog = builder.create();
		dialog.show();
		dialog.setCanceledOnTouchOutside(true);
	}

	private void setGender(boolean isMale) {
		if (isMale) {
			tvGender.setText(Gender.MALE);
		} else {
			tvGender.setText(Gender.FEMALE);
		}
		try {
			Account temp = (Account) curAccount.clone();
			temp.setMale(isMale);
			temp.setTag(Account.TAG_FORCE_UPDATE);
			doUpdateAccount(temp);
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}

	}

	@SuppressLint("NewApi")
	public void showBirthDayDialog(final TextView tv) {
		Calendar cal = Calendar.getInstance();
		if (curAccount.getBirthday() != null) {
			Date birthday = this.curAccount.getBirthday();
			cal.setTime(birthday);
		} else {
			cal.set(1960, 0, 1);
		}
		AlertDialog dialog = null;
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		View content = getLayoutInflater().inflate(R.layout.datepick_content,
				null);
		final DatePicker pick = (DatePicker) content.findViewById(R.id.data);
		if (CloudApplication.isNewAPI(Build.VERSION_CODES.HONEYCOMB)) {
			long max = System.currentTimeMillis();
			long min = new GregorianCalendar(1900, 0, 1).getTimeInMillis();
			pick.init(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH),
					cal.get(Calendar.DAY_OF_MONTH),
					new OnDateChangedListener() {

						@Override
						public void onDateChanged(DatePicker view, int year,
								int monthOfYear, int dayOfMonth) {

						}
					});
			pick.setMinDate(min);
			pick.setMaxDate(max);
		}
		builder.setView(content);
		builder.setTitle(R.string.birthday_setting);
		builder.setPositiveButton(R.string.public_submit,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						String birthdayOnDateset = pick.getYear() + "-"
								+ (pick.getMonth() + 1) + "-"
								+ pick.getDayOfMonth();
						tv.setText(birthdayOnDateset);
						try {

							Account temp = (Account) curAccount.clone();
							if (birthdayOnDateset != null
									&& !TextUtils.isEmpty(birthdayOnDateset)) {
								temp.setBirthday(TimeUtils.getDate(
										birthdayOnDateset,
										TimeUtils.DATE_FORMAT_DATE));
								temp.setTag(Account.TAG_FORCE_UPDATE);
								doUpdateAccount(temp);
							}
						} catch (CloneNotSupportedException e) {
							e.printStackTrace();
						}

					}
				});
		builder.setNegativeButton(R.string.public_cancle,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
					}
				});
//		builder.create().show();
		 Dialog birth = builder.create();
		 birth.setCanceledOnTouchOutside(false);
		 birth.show();

	}

	private void showHeightDialog(final TextView tv) {

		int defValue = curAccount.getTall() != null ? curAccount.getTall()
				.intValue() : Constants.HEIGHT_DEFAULT;
		int minValue = Constants.HEIGHT_MIN;
		int maxValue = Constants.HEIGHT_MAX;

		String title = getString(string.setting_height);
		NumberPickerUtil.showNumberPicker(defValue, minValue, maxValue, title,
				Constants.UNIT_ch_CM, new onDialogChooseListener() {

					@Override
					public void onConfirm(Object value) {
						tv.setText((Integer) value + "");
						tv.append(Constants.UNIT_ch_CM);
						try {
							Account temp = (Account) curAccount.clone();
							temp.setTall(Float.valueOf((Integer) value));
							doUpdateAccount(temp);
						} catch (CloneNotSupportedException e) {
							e.printStackTrace();
						}
					}

					@Override
					public void onCancel() {
					}

				});
	}

	private void showWeightDialog(final TextView tv) {

		int defValue = curAccount.getWeight() != null ? curAccount.getWeight()
				.intValue() : Constants.WEIGHT_DEFAULT;
		int minValue = Constants.WEIGHT_MIN;
		int maxValue = Constants.WEIGHT_MAX;
		String title = getString(string.setting_weight);

		NumberPickerUtil.showNumberPicker(defValue, minValue, maxValue, title,
				Constants.UNIT_ch_KG, new onDialogChooseListener() {

					@Override
					public void onConfirm(Object value) {
						tv.setText((Integer) value + "");
						tv.append(Constants.UNIT_ch_KG);
						try {
							Account temp = (Account) curAccount.clone();
							temp.setWeight(Float.valueOf((Integer) value));
							doUpdateAccount(temp);
						} catch (CloneNotSupportedException e) {
							e.printStackTrace();
						}
					}

					@Override
					public void onCancel() {

					}
				});

	}

	public void doLogoutTask() {
		if (task != null && task.getStatus() == Status.RUNNING)
			return;
		task = new LogoutTask(this, curAccount.getAccessToken());
		task.setProgress(new CustomDialogProgress(this,
				getText(R.string.logouting)));
		task.setTaskHost(new TaskHost() {
			@Override
			public void onPostExecute(int requestCode, BaseResult result) {
				super.onPostExecute(requestCode, result);
				NetworkClientResult res = (NetworkClientResult) result;
				if (res.isSuccess()) {
					if (!res.isServerDisposeSuccess()) {
						// showTipDialog(res);
					}
				}
				AccountHelper.logout(SettingPersonalInfoActivity.this, true);
			}
		});
		task.execute();
	}

	@Override
	public void propertyChange(PropertyChangeEvent event) {
		if (event.getPropertyName().equals(
				PropertyCenter.PROPERTY_REFRESH_ACCOUNT)) {
			fillView((Account) event.getNewValue());
		} else if (event.getPropertyName().equals(
				PropertyCenter.PROPERTY_REFRESH_AVATAR)) {
			fillAvatarView(curAccount);
		}
	}

	/**
	 * 更改账号的资料
	 * 
	 * @param account
	 */
	private void doUpdateAccount(final Account account) {
		AccountHelper.doUpdateAccountTask(this, account, new TaskHost() {
			@Override
			public void onPostExecute(int requestCode, BaseResult result) {
				super.onPostExecute(requestCode, result);
				if (result.isSuccess()) {
					NetworkClientResult res = (NetworkClientResult) result;
					if (res.isServerDisposeSuccess()) {
						isChanged = true;
						if (TextUtils.equals(account.getTag(),
								Account.TAG_FORCE_UPDATE)) {
							doGetAccountDetails();
						} else {
							updateAccount(account);
							fillView(account);
						}
						PropertyCenter.getInstance().firePropertyChange(
								PropertyCenter.PROPERTY_REFRESH_GROUP_MEMBER,
								null, null);
					} else {
						ErrorDialogUtil.showErrorDialog(
								SettingPersonalInfoActivity.this,
								ProxyErrorCode.TYPE_SETTING,
								result.getErrorCode(), true);
						fillView(curAccount);
					}
				} else {
					ErrorDialogUtil.showErrorToast(
							SettingPersonalInfoActivity.this,
							ProxyErrorCode.TYPE_SETTING,
							ProxyErrorCode.LocalError.CODE_10002);
					fillView(curAccount);
				}
			}
		});
	}

	private void doGetAccountDetails() {

		AccountModuleHelper.get(CurrentAccountManager.getCurAccount())
				.updateAccountInfo(new IGetDetailCallBack() {

					@Override
					public void onComplete(int resultCode) {
						switch (resultCode) {
						case BaseResult.DISPOSE_CODE_SUCCESS:
							fillView(CurrentAccountManager.getCurAccount());
							break;
						default:
							fillView(CurrentAccountManager.getCurAccount());
							ErrorDialogUtil.showErrorDialog(
									SettingPersonalInfoActivity.this,
									ProxyErrorCode.TYPE_SETTING, resultCode,
									true);
							break;
						}
					}
				});
	}

	@Override
	public void finish() {
		super.finish();
		if (isChanged) {
			GroupMemberManagerFragment.saveCurrentItem();
			PropertyCenter.getInstance().firePropertyChange(
					PropertyCenter.PROPERTY_REFRESH_GROUP_MEMBER, null, null);
			PropertyCenter.getInstance().firePropertyChange(
					PropertyCenter.PROPERTY_REFRESH_ACCOUNT, null, curAccount);

			if (CurrentAccountManager.getCurAccount() != null) {
				AccountModuleHelper.get(CurrentAccountManager.getCurAccount())
						.flushCurAccountInfo();
			}
		}
	}
}

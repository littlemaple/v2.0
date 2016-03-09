package com.medzone.cloud.ui;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask.Status;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.LayoutParams;
import com.medzone.cloud.CloudApplication;
import com.medzone.cloud.GlobalVars;
import com.medzone.cloud.data.TemporaryData;
import com.medzone.cloud.data.helper.AccountHelper;
import com.medzone.cloud.module.modules.AccountModule.IGetDetailCallBack;
import com.medzone.cloud.task.RegisterByEmailTask;
import com.medzone.cloud.task.RegisterByPhoneTask;
import com.medzone.cloud.ui.dialog.error.ErrorDialogUtil;
import com.medzone.cloud.ui.widget.CleanableEditText;
import com.medzone.framework.data.bean.imp.Account;
import com.medzone.framework.data.bean.imp.Account.Gender;
import com.medzone.framework.errorcode.ProxyCode;
import com.medzone.framework.errorcode.ProxyCode.LocalError;
import com.medzone.framework.errorcode.ProxyErrorCode;
import com.medzone.framework.network.NetworkClientResult;
import com.medzone.framework.task.BaseResult;
import com.medzone.framework.task.TaskHost;
import com.medzone.framework.util.TimeUtils;
import com.medzone.mcloud.R;
import com.medzone.mcloud.R.drawable;
import com.medzone.mcloud.R.id;
import com.medzone.mcloud.R.string;

public class RegisterPersonInfoActivity extends BaseActivity implements
		OnClickListener {

	private Button nextBtn;
	private TextView editBirthday;
	private CleanableEditText editNickname;
	private RadioGroup genderGroup;
	private String genderValue = null; // Def value see as xml.

	private Account registerAccount;
	private boolean isRegistered = false;

	@Override
	protected void preLoadData(Bundle savedInstanceState) {
		super.preLoadData(savedInstanceState);
		if (TemporaryData.containsKey(Account.class.getName())) {
			registerAccount = (Account) TemporaryData.get(Account.class
					.getName());
		} else {
			finish();
		}
	}

	@Override
	protected void preInitUI() {
		ActionBar actionBar = getSupportActionBar();
		ActionBar.LayoutParams params = new ActionBar.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT,
				Gravity.CENTER);
		View view = LayoutInflater.from(this).inflate(
				R.layout.custom_actionbar_with_image, null);
		TextView title = (TextView) view.findViewById(R.id.actionbar_title);
		title.setText(string.action_title_register_personinfo);
		ImageView leftButton = (ImageView) view
				.findViewById(R.id.actionbar_left);
		leftButton.setImageResource(drawable.public_ic_back);
		leftButton.setOnClickListener(this);
		actionBar.setCustomView(view, params);
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		actionBar.setDisplayShowCustomEnabled(true);
	}

	@Override
	protected void initUI() {
		setContentView(R.layout.activity_register_personinfo);

		nextBtn = (Button) findViewById(R.id.btn_next);
		editNickname = (CleanableEditText) findViewById(R.id.ce_edit_nickname);
		editBirthday = (TextView) findViewById(R.id.edit_birthday);
		genderGroup = (RadioGroup) findViewById(R.id.radiogroup_gender);
	}

	@Override
	protected void postInitUI() {
		genderGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch (checkedId) {
				case id.radio_male:
					genderValue = Gender.MALE;
					break;
				case id.radio_female:
					genderValue = Gender.FEMALE;
					break;
				default:
					break;
				}
			}
		});
		nextBtn.setOnClickListener(this);
		editBirthday.setOnClickListener(this);
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public void showBirthDayDialog(final TextView tv) {

		final String oldBirthday = tv.getText().toString();

		Calendar cal = Calendar.getInstance();

		if (TextUtils.isEmpty(oldBirthday)) {
			cal.set(1960, 0, 1);
		} else {
			Date oldDate = TimeUtils.getDate(oldBirthday,
					TimeUtils.DATE_FORMAT_DATE);
			cal.setTime(oldDate);
		}
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
						int month = pick.getMonth() + 1;
						int day = pick.getDayOfMonth();
						String birthdayOnDateset = pick.getYear() + "-"
								+ (month > 9 ? month : "0" + month) + "-"
								+ (day > 9 ? day : "0" + day);
						editBirthday.setText(birthdayOnDateset);
					}
				});
		builder.setNegativeButton(R.string.public_cancle,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
					}
				});
		builder.create().show();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.edit_birthday:
			showBirthDayDialog(editBirthday);
			break;
		case R.id.actionbar_left:
			this.finish();
			break;
		case R.id.btn_next:
			String nickname = editNickname.getText().toString();
			String birthday = editBirthday.getText().toString();
			String gender = genderValue;

			int errorCode = AccountHelper.checkRegisterPersonDetail(nickname,
					birthday, gender);

			if (errorCode == LocalError.CODE_SUCCESS) {
				doNextAction();
			} else {

				ErrorDialogUtil
						.showErrorDialog(this,
								ProxyErrorCode.TYPE_LOGIN_AND_REGISTER,
								errorCode, true);
			}
			break;
		default:
			break;
		}
	}

	private RegisterByEmailTask registerByEmailTask;
	private RegisterByPhoneTask registerByPhoneTask;

	private void prepareParams() {
		// prepare params
		registerAccount.setNickname(editNickname.getText().toString());
		registerAccount
				.setMale(TextUtils.equals(genderValue, Gender.MALE) ? true
						: false);
		registerAccount.setBirthday(TimeUtils.getDate(editBirthday.getText()
				.toString(), TimeUtils.DATE_FORMAT_DATE));
	}

	private void doNextAction() {

		nextBtn.setClickable(false);
		if (!isRegistered) {
			prepareParams();
			if (registerAccount.getEmail() != null) {
				doEmailRegister();
			} else if (registerAccount.getPhone() != null) {
				doPhoneRegister();
			}
		} else {
			doLoginTask(registerAccount);
		}

	}

	private void doEmailRegister() {
		if (registerByEmailTask != null
				&& registerByEmailTask.getStatus() == Status.RUNNING)
			return;
		registerByEmailTask = new RegisterByEmailTask(this, registerAccount);

		registerByEmailTask.setTaskHost(new TaskHost() {
			@Override
			public void onPostExecute(int requestCode, BaseResult result) {
				super.onPostExecute(requestCode, result);
				if (result.isSuccess()) {
					if (result.isServerDisposeSuccess()) {
						isRegistered = true;
						ErrorDialogUtil.showErrorDialog(
								RegisterPersonInfoActivity.this,
								ProxyErrorCode.TYPE_LOGIN_AND_REGISTER,
								LocalError.CODE_10211, true);
						doLoginTask(registerAccount);
					} else {
						ErrorDialogUtil.showErrorDialog(
								RegisterPersonInfoActivity.this,
								ProxyErrorCode.TYPE_LOGIN_AND_REGISTER,
								result.getErrorCode(), true);
					}
				}
				nextBtn.setClickable(true);
			}
		});
		registerByEmailTask.execute();
	}

	private void doPhoneRegister() {
		if (registerByPhoneTask != null
				&& registerByPhoneTask.getStatus() == Status.RUNNING)
			return;
		registerByPhoneTask = new RegisterByPhoneTask(this, registerAccount);

		registerByPhoneTask.setTaskHost(new TaskHost() {
			@Override
			public void onPostExecute(int requestCode, BaseResult result) {
				super.onPostExecute(requestCode, result);
				if (result.isSuccess()) {
					if (result.isServerDisposeSuccess()) {
						isRegistered = true;
						ErrorDialogUtil.showErrorDialog(
								RegisterPersonInfoActivity.this,
								ProxyErrorCode.TYPE_LOGIN_AND_REGISTER,
								LocalError.CODE_10211, true);
						doLoginTask(registerAccount);
					} else {
						ErrorDialogUtil.showErrorDialog(
								RegisterPersonInfoActivity.this,
								ProxyErrorCode.TYPE_LOGIN_AND_REGISTER,
								result.getErrorCode(), true);
					}
				}
				nextBtn.setClickable(true);
			}
		});
		registerByPhoneTask.execute();
	}

	private void doLoginTask(final Account tmpAccount) {

		AccountHelper.doLoginTask(this, tmpAccount, new TaskHost() {
			@Override
			public void onPostExecute(int requestCode, BaseResult result) {
				super.onPostExecute(requestCode, result);

				if (result.isSuccess()) {
					if (result.isServerDisposeSuccess()) {
						GlobalVars.setOffLined(false);
						AccountHelper.updateAfterLoginSuccess(
								(NetworkClientResult) result, tmpAccount,
								new IGetDetailCallBack() {
									@Override
									public void onComplete(int code) {

										switch (code) {
										case BaseResult.DISPOSE_CODE_SUCCESS:
											jumpDevice();
											break;
										default:
											ErrorDialogUtil
													.showErrorDialog(
															RegisterPersonInfoActivity.this,
															ProxyErrorCode.TYPE_LOGIN_AND_REGISTER,
															ProxyCode.LocalError.CODE_10001,
															true);
											break;
										}
									}
								});
					} else {
						ErrorDialogUtil.showErrorDialog(
								RegisterPersonInfoActivity.this,
								ProxyErrorCode.TYPE_LOGIN_AND_REGISTER,
								result.getErrorCode(), true);
					}
				}
			}
		});
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public void jumpDevice() {
		Intent intent = new Intent(this, RegisterDeviceActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
		startActivity(intent);
		overridePendingTransition(R.anim.zoom_in, R.anim.zoom_out);
		finish();
	}

}

package com.medzone.cloud.ui.fragment.login;

import java.util.Calendar;
import java.util.Date;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.DatePickerDialog.OnDateSetListener;
import android.os.AsyncTask.Status;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.LayoutParams;
import com.medzone.cloud.GlobalVars;
import com.medzone.cloud.data.TemporaryData;
import com.medzone.cloud.data.helper.AccountHelper;
import com.medzone.cloud.module.modules.AccountModule.IGetDetailCallBack;
import com.medzone.cloud.task.RegisterByEmailTask;
import com.medzone.cloud.task.RegisterByPhoneTask;
import com.medzone.cloud.ui.dialog.error.ErrorDialogUtil;
import com.medzone.cloud.ui.widget.CleanableEditText;
import com.medzone.cloud.ui.widget.CustomDatepickDialog;
import com.medzone.framework.data.bean.imp.Account;
import com.medzone.framework.data.bean.imp.Account.Gender;
import com.medzone.framework.errorcode.ProxyCode;
import com.medzone.framework.errorcode.ProxyCode.LocalError;
import com.medzone.framework.errorcode.ProxyErrorCode;
import com.medzone.framework.fragment.BaseFragment;
import com.medzone.framework.network.NetworkClientResult;
import com.medzone.framework.task.BaseResult;
import com.medzone.framework.task.TaskHost;
import com.medzone.framework.util.TimeUtils;
import com.medzone.mcloud.R;
import com.medzone.mcloud.R.drawable;
import com.medzone.mcloud.R.id;
import com.medzone.mcloud.R.string;

public class RegisterPersonInfoFragment extends BaseFragment implements
		OnClickListener {

	private Button nextBtn;
	private TextView editBirthday;
	private CleanableEditText editNickname;
	private RadioGroup genderGroup;
	private String genderValue = null; // Def value see as xml.

	private Account registerAccount;
	private boolean isRegistered = false;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if (TemporaryData.containsKey(Account.class.getName())) {
			registerAccount = (Account) TemporaryData.get(Account.class
					.getName());
		} else {
			getFragmentManager().popBackStack();
		}
	}

	@Override
	protected void initActionBar() {
		super.initActionBar();
		ActionBar actionBar = getSherlockActivity().getSupportActionBar();
		actionBar.show();
		ActionBar.LayoutParams params = new ActionBar.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT,
				Gravity.CENTER);
		View view = LayoutInflater.from(getActivity()).inflate(
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
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.activity_register_personinfo,
				null);

		nextBtn = (Button) view.findViewById(R.id.btn_next);
		editNickname = (CleanableEditText) view
				.findViewById(R.id.ce_edit_nickname);
		editBirthday = (TextView) view.findViewById(R.id.edit_birthday);
		genderGroup = (RadioGroup) view.findViewById(R.id.radiogroup_gender);

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
		return view;
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		initActionBar();
	}

	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		initActionBar();
	}

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

		CustomDatepickDialog birthDialog = new CustomDatepickDialog(
				getActivity(), new OnDateSetListener() {

					@Override
					public void onDateSet(DatePicker view, int year,
							int monthOfYear, int dayOfMonth) {
						String month, day;
						monthOfYear++;
						if (monthOfYear < 10) {
							month = "0" + monthOfYear;
						} else {
							month = "" + monthOfYear;
						}
						if (dayOfMonth < 10) {
							day = "0" + dayOfMonth;
						} else {
							day = "" + dayOfMonth;
						}
						String birthdayOnDateset = year + "-" + month + "-"
								+ day;
						editBirthday.setText(birthdayOnDateset);
					}
				}, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH),
				cal.get(Calendar.DAY_OF_MONTH));
		birthDialog.setTitle(R.string.birthday_setting);
		birthDialog.show();

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.edit_birthday:
			showBirthDayDialog(editBirthday);
			break;
		case R.id.actionbar_left:
			getFragmentManager().popBackStack();
			;
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
						.showErrorDialog(getActivity(),
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
		registerByEmailTask = new RegisterByEmailTask(getActivity(),
				registerAccount);

		registerByEmailTask.setTaskHost(new TaskHost() {
			@Override
			public void onPostExecute(int requestCode, BaseResult result) {
				super.onPostExecute(requestCode, result);
				if (result.isSuccess()) {
					if (result.isServerDisposeSuccess()) {
						isRegistered = true;
						ErrorDialogUtil.showErrorDialog(getActivity(),
								ProxyErrorCode.TYPE_LOGIN_AND_REGISTER,
								LocalError.CODE_10211, true);
						doLoginTask(registerAccount);
					} else {
						ErrorDialogUtil.showErrorDialog(getActivity(),
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
		registerByPhoneTask = new RegisterByPhoneTask(getActivity(),
				registerAccount);

		registerByPhoneTask.setTaskHost(new TaskHost() {
			@Override
			public void onPostExecute(int requestCode, BaseResult result) {
				super.onPostExecute(requestCode, result);
				if (result.isSuccess()) {
					if (result.isServerDisposeSuccess()) {
						isRegistered = true;
						ErrorDialogUtil.showErrorDialog(getActivity(),
								ProxyErrorCode.TYPE_LOGIN_AND_REGISTER,
								LocalError.CODE_10211, true);
						doLoginTask(registerAccount);
					} else {
						ErrorDialogUtil.showErrorDialog(getActivity(),
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

		AccountHelper.doLoginTask(getActivity(), tmpAccount, new TaskHost() {
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
															getActivity(),
															ProxyErrorCode.TYPE_LOGIN_AND_REGISTER,
															ProxyCode.LocalError.CODE_10001,
															true);
											break;
										}
									}
								});
					} else {
						ErrorDialogUtil.showErrorDialog(getActivity(),
								ProxyErrorCode.TYPE_LOGIN_AND_REGISTER,
								result.getErrorCode(), true);
					}
				}
			}
		});
	}

	public void jumpDevice() {
		// Intent intent = new Intent(this, RegisterDeviceActivity.class);
		// intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		// intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
		// startActivity(intent);
		// finish();

		FragmentManager fm = getFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		ft.add(R.id.login_container, new RegisterDeviceFragment());
		ft.addToBackStack(getTag());
		ft.commit();

	}
}

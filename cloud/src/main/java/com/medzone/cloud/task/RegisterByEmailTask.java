package com.medzone.cloud.task;

import java.util.Date;

import android.content.Context;

import com.medzone.cloud.network.NetworkClient;
import com.medzone.framework.data.bean.imp.Account;
import com.medzone.framework.task.BaseResult;

public class RegisterByEmailTask extends BaseCloudTask {

	private String email;
	private String checkCode;
	private String password;
	private String nickname;
	private Boolean isMale;
	private Date birthday;
	private String location;

	public RegisterByEmailTask(Context context, Account account) {
		super(context, 0);
		this.email = account.getEmail();
		this.checkCode = account.getTag();
		this.password = account.getPassword();
		this.nickname = account.getNickname();
		this.isMale = account.isMale();
		this.birthday = account.getBirthday();
		this.location = account.getLocation();
	}

	@Override
	protected BaseResult doInBackground(Void... params) {
		return NetworkClient.getInstance().doRegisterByEmail(email, checkCode,
				password, nickname, isMale, birthday, location);
	}

}

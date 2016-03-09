package com.medzone.framework.errorcode;

import com.medzone.framework.errorcode.ProxyCode.LocalError;
import com.medzone.framework.errorcode.ProxyCode.NetError;

public class LoginRegisterErrorCode extends ErrorCode {

	public LoginRegisterErrorCode() {
		super();
	}

	@Override
	protected void initCodeCollect() {
		super.initCodeCollect();
		errorCodeMap.put(NetError.CODE_40100, "请输入正确的手机号或邮箱");
		errorCodeMap.put(NetError.CODE_40101, "请输入正确的手机号或邮箱");
		errorCodeMap.put(NetError.CODE_40102, "该帐号未注册，请重新输入");
		errorCodeMap.put(NetError.CODE_40103, "该帐号已经被注册，请重新输入");
		errorCodeMap.put(NetError.CODE_40104, "发送过于频繁");
		errorCodeMap.put(NetError.CODE_40105, "网络异常，请稍后再试");
		errorCodeMap.put(NetError.CODE_40106, "验证码错误");
		errorCodeMap.put(NetError.CODE_40300, "注册失败，请重新注册");
		
		errorCodeMap.put(NetError.CODE_40301, "密码需为6-16位英文或数字");
		errorCodeMap.put(NetError.CODE_40302, "验证码错误");
		errorCodeMap.put(NetError.CODE_40303, "验证码错误");
		errorCodeMap.put(NetError.CODE_40400, "请输入正确的手机号或邮箱");
		errorCodeMap.put(NetError.CODE_40401, "密码需为6-16位英文或数字");
		errorCodeMap.put(NetError.CODE_40500, "服务器异常，请稍后再试");
		errorCodeMap.put(NetError.CODE_40501, "请输入正确的手机号或邮箱");
		errorCodeMap.put(NetError.CODE_40502, "帐号或密码错误，请重新输入");
		errorCodeMap.put(NetError.CODE_40503, "密码错误次数过多，请 15分钟后重新登陆");

		errorCodeMap.put(LocalError.CODE_ACCOUNT_ILLAGE, "请输入正确的手机号或邮箱");
		errorCodeMap.put(LocalError.CODE_10076, "请输入验证码");
		errorCodeMap.put(LocalError.CODE_10203, "验证码已经发送至\n%1$s");
		errorCodeMap.put(LocalError.CODE_NICKNAME_TOO_LONG, "昵称字数超过限制，请重新输入");
		errorCodeMap.put(LocalError.CODE_NICKNAME_ILLAGE, "昵称只能由汉字、数字及英文字母组成");
		errorCodeMap.put(LocalError.CODE_10206, "验证码错误");
		errorCodeMap.put(LocalError.CODE_10304, "验证码错误"); 

		errorCodeMap.put(LocalError.CODE_10305, "请输入新密码 ");
		errorCodeMap.put(LocalError.CODE_PASSWORD_ERROR, "帐号或密码错误，请重新输入");
		errorCodeMap.put(LocalError.CODE_LOGIN_KICKED_ERROR, "您的帐号在别处登录");
		
		errorCodeMap.put(LocalError.CODE_PASSWORD_ILLAGE, "密码需为6-16位英文或数字");
		errorCodeMap.put(LocalError.CODE_10307, "新密码设置成功");
		errorCodeMap.put(LocalError.CODE_PASSWORD_EMPTY, "请输入密码");
		errorCodeMap.put(LocalError.CODE_ACCOUNT_EMPTY, "请输入帐号 ");
		errorCodeMap.put(LocalError.CODE_EMAIL_EMPTY, "请输入帐号");
		errorCodeMap.put(LocalError.CODE_PHONE_EMPTY, "请输入帐号");
		errorCodeMap.put(LocalError.CODE_10211, "注册成功");
		errorCodeMap.put(LocalError.CODE_NICKNAME_EMPTY, "请输入昵称");
		errorCodeMap.put(LocalError.CODE_10212, "请选择生日");
		errorCodeMap.put(LocalError.CODE_10213, "请选择性别 ");
		errorCodeMap.put(LocalError.CODE_10312, "请输入验证码");
		errorCodeMap.put(LocalError.CODE_10214, "注册异常，请重新注册");
		errorCodeMap.put(LocalError.CODE_PHONE_ILLAGE, "请输入正确的手机号或邮箱");
		errorCodeMap.put(LocalError.CODE_EMAIL_ILLAGE, "请输入正确的手机号或邮箱");
	}

}

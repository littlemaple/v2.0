package com.medzone.framework.errorcode;

import com.medzone.framework.errorcode.ProxyCode.LocalError;
import com.medzone.framework.errorcode.ProxyCode.NetError;

public class SettingErrorCode extends ErrorCode {

	public SettingErrorCode() {
		super();
	}

	@Override
	protected void initCodeCollect() {
		super.initCodeCollect();
		errorCodeMap
				.put(NetError.CODE_40700, "帐号资料不正确");
		errorCodeMap.put(NetError.CODE_40701, "旧手机验证码不正确");
		errorCodeMap.put(NetError.CODE_40702, "新手机验证码不正确");
		errorCodeMap.put(NetError.CODE_40703, "新密码解码失败");
		errorCodeMap.put(NetError.CODE_40704, "头像数据解码不正确");
		errorCodeMap.put(NetError.CODE_40705, "邮箱修改验证码不正确");

		errorCodeMap.put(LocalError.CODE_NICKNAME_EMPTY, "请输入昵称");
		errorCodeMap.put(LocalError.CODE_NICKNAME_ILLAGE, "昵称只能由汉字、数字及英文字母组成");
		errorCodeMap.put(LocalError.CODE_NICKNAME_TOO_LONG, "字数超过限制");

		errorCodeMap.put(LocalError.CODE_REALNAME_EMPTY, "请输入姓名");
		errorCodeMap.put(LocalError.CODE_REALNAME_TOO_LONG, "字数超过限制");
		errorCodeMap.put(LocalError.CODE_REALNAME_ILLAGE, "姓名只能由汉字、数字及英文字母组成");

		errorCodeMap.put(LocalError.CODE_13105, "请输入邮箱");
		errorCodeMap.put(LocalError.CODE_13106, "请输入正确的邮箱号");

		errorCodeMap.put(LocalError.CODE_INFO_BIND_PHONE_NULL, "请输入手机号");
		errorCodeMap.put(LocalError.CODE_13107, "请输入正确的手机号");

		errorCodeMap.put(LocalError.CODE_INFO_BIND_IDCRAD_NULL, "请输入身份证号码");
		errorCodeMap
				.put(LocalError.CODE_INFO_BIND_IDCRAD_ILLAGE, "请输入正确的身份证号码");

		errorCodeMap.put(LocalError.CODE_13109, "请选择省份与城市");
		errorCodeMap.put(LocalError.CODE_13110,
				"您输入的地址格式不正确，请重新输入");
		errorCodeMap.put(LocalError.CODE_13111, "字数超过限制");
		errorCodeMap.put(LocalError.CODE_13112, "请输入详细地址");

		errorCodeMap.put(LocalError.CODE_13201, "请输入原密码");
		errorCodeMap.put(LocalError.CODE_RESET_NEW_PASSWORD_NULL, "请输入新密码");
		errorCodeMap.put(LocalError.CODE_13202, "原密码输入不正确，请重新输入 ");
		errorCodeMap.put(LocalError.CODE_RESET_NEW_PASSWORD_SUCCESS, "新密码设置成功");
		errorCodeMap.put(LocalError.CODE_PASSWORD_ILLAGE,
				"密码需为6-16位英文或数字，请重新输入");

		errorCodeMap.put(LocalError.CODE_13204, "预产日期不可以为空");
		errorCodeMap.put(LocalError.CODE_13205, "无法选择到当前日期之前");
	}
}

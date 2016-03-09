package com.medzone.framework.errorcode;

import com.medzone.framework.errorcode.ProxyCode.LocalError;

public class ServiceErrorCode extends ErrorCode {

	public ServiceErrorCode() {
		super();
	}

	@Override
	protected void initCodeCollect() {
		super.initCodeCollect();
		errorCodeMap.put(LocalError.CODE_14101, "标签只能由汉字、数字及英文字母组成");
		errorCodeMap.put(LocalError.CODE_14102, "闹钟个数已达上限，无法添加新闹钟");
		errorCodeMap.put(LocalError.CODE_14103, "请输入标签");
	}
}

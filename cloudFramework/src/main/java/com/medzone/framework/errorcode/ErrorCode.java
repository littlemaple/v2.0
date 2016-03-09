package com.medzone.framework.errorcode;

import android.util.SparseArray;

import com.medzone.framework.errorcode.ProxyCode.LocalError;
import com.medzone.framework.errorcode.ProxyCode.NetError;

/**
 * 
 * <p>
 * 1) .处理错误码以及错误提示信息的映射关系
 * </p>
 * <p>
 * 2) .需要注意这边同一错误码可能对应多份错误信息。（目前场景是分模块去呈现）
 * </p>
 * <p>
 * 3) .另外错误码应该允许被客户定制，即客户端可以选择不使用我们推荐的错误信息去呈现。（兼容同一模块内，也出现不同的错误提示信息极少数情况）
 * </p>
 * 
 * @author junqi
 * 
 */
abstract class ErrorCode implements IErrorCode {

	protected static int ERRORCODE_UNKNOWN = 0;
	protected static String ERRORCODE_UNKNOWN_MESSAGE = "未知错误";

	protected static SparseArray<String> errorCodeMap = new SparseArray<String>();

	public ErrorCode() {
		if (errorCodeMap == null) {
			errorCodeMap = new SparseArray<String>();
			errorCodeMap.put(ERRORCODE_UNKNOWN, ERRORCODE_UNKNOWN_MESSAGE);
		}

		initCodeCollect();
	}

	protected void initCodeCollect() {
		errorCodeMap.put(LocalError.CODE_10000, "无");
		errorCodeMap.put(LocalError.CODE_10001, "当前网络不可用,请检查网络设置");
		errorCodeMap.put(LocalError.CODE_10002, "网络异常,请稍后再试");
		errorCodeMap.put(LocalError.CODE_10003, "字数超过限制");
		errorCodeMap.put(LocalError.CODE_10004, "未找到存储卡，无法存储照片！");
		errorCodeMap.put(LocalError.CODE_10005, "服务器异常，请稍后重试");
		errorCodeMap.put(NetError.CODE_40000, "服务器异常，请稍后再试");
		errorCodeMap.put(NetError.CODE_40001, "服务器异常，请稍后再试");
		errorCodeMap.put(NetError.CODE_40002, "您的帐号在别处登陆！");
		errorCodeMap.put(NetError.CODE_50001, "服务器异常，请稍后再试");
	}

	/**
	 * @param type
	 *            指定错误集合类别
	 * @param errorCode
	 *            错误码
	 * @return 如果存在指定集合，并且集合内错误码也存在，则返回对应的友好错误信息。如果不存在，则返回
	 *         {@link#ERRORCODE_UNKNOWN}对应的内容.
	 */
	@Override
	public String getErrorMessage(int type, int errorCode) {

		if (isContainsKey(type)) {
			return errorCodeMap.get(errorCode);
		} else {
			return errorCodeMap.get(ERRORCODE_UNKNOWN);
		}
	}

	/**
	 * 
	 * @param type
	 * @return
	 */
	protected boolean isContainsKey(int type) {
		return ProxyErrorCode.getInstance().exceptionArrs.indexOfKey(type) < 0 ? false
				: true;
	}
}

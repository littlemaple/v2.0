package com.medzone.framework.errorcode;

import java.util.ArrayList;
import java.util.List;

import android.text.TextUtils;
import android.util.SparseArray;

import com.medzone.framework.Log;

/**
 * 
 * 是否需要错误日志记录用户错误行为？
 * 
 * @author junqi
 * 
 */
public class ProxyErrorCode extends ProxyCode implements IErrorCode {

	public static final int TYPE_LOGIN_AND_REGISTER = 10;
	public static final int TYPE_HOME = 11;
	public static final int TYPE_GROUP = 12;
	public static final int TYPE_MEASURE = 13;
	public static final int TYPE_SERVICE = 14;
	public static final int TYPE_SETTING = 15;

	protected static int ERRORCODE_NO_HISTORY = -1;
	protected static String ERRORCODE_NO_HISTORY_MESSAGE = "错误日志为空";

	private static final String TYPE_EXCEPTION_NOT_EXIST = "没有指定类别的错误管理器";
	private static ProxyErrorCode instance;
	// 错误类别，错误码,我们可以本地硬存这个列表，记录错误行为发生的规律以及频繁程度。
	private static List<ErrorAction> historyActionList;

	public SparseArray<IErrorCode> exceptionArrs;

	private ProxyErrorCode() {
		exceptionArrs = new SparseArray<IErrorCode>();
	}

	public static ProxyErrorCode getInstance() {
		if (instance == null) {
			instance = new ProxyErrorCode();
			historyActionList = new ArrayList<ErrorAction>();
		}
		return instance;
	}

	// ===================================================//
	// 异常码管理类的方法，异常码本身无法获取该事件 //
	// ===================================================//

	/**
	 * 根据指定错误类别与错误码，获取一条错误信息
	 */
	@Override
	public String getErrorMessage(int type, int errorCode) {

		if (!isExceptionCodeExist(type)) {
			boolean isCreateSuccess = createIfNotExist(type);
			if (!isCreateSuccess) {
				return TYPE_EXCEPTION_NOT_EXIST;
			}
		}

		String ret = exceptionArrs.get(type).getErrorMessage(type, errorCode);

		if (TextUtils.isEmpty(ret)) {
			ret = "错误码：" + errorCode + ",错误内容：未定义。";
		}
		return ret;
	}

	/**
	 * 如果指定错误管理对象不存在，则进行创建
	 */
	private boolean createIfNotExist(int type) {
		switch (type) {
		case TYPE_LOGIN_AND_REGISTER:
			exceptionArrs.put(type, new LoginRegisterErrorCode());
			return true;
		case TYPE_HOME:
			exceptionArrs.put(type, new HomeErrorCode());
			return true;
		case TYPE_GROUP:
			exceptionArrs.put(type, new GroupErrorCode());
			return true;
		case TYPE_MEASURE:
			exceptionArrs.put(type, new MeasureErrorCode());
			return true;
		case TYPE_SERVICE:
			exceptionArrs.put(type, new ServiceErrorCode());
			return true;
		case TYPE_SETTING:
			exceptionArrs.put(type, new SettingErrorCode());
			return true;
		default:
			return false;
		}
	}

	/**
	 * 记录一次错误行为，不作任何前台呈现。
	 * 
	 * @param type
	 * @param code
	 */
	public void addErrorAction(int type, int code) {
		historyActionList.add(new ErrorAction(type, code));
	}

	/**
	 * 
	 * @return 获取距离本次最新一次错误码Code
	 */
	@Deprecated
	public int getLastestErrorCode() {
		int length = historyActionList.size();
		if (length == 0) {
			return ERRORCODE_NO_HISTORY;
		} else {
			return historyActionList.get(length).getCode();
		}
	}

	/**
	 * 
	 * @return 获取距离本次最新一次错误码Message
	 */
	@Deprecated
	public String getLastestErrorMessage() {
		int length = historyActionList.size();
		if (length == 0) {
			return ERRORCODE_NO_HISTORY_MESSAGE;
		} else {
			int errorCode = historyActionList.get(length).getCode();
			int type = historyActionList.get(length).getType();
			return getErrorMessage(type, errorCode);
		}
	}

	private boolean isExceptionCodeExist(int type) {
		return exceptionArrs.indexOfKey(type) < 0 ? false : true;
	}

	/**
	 * 将错误信息写入SD卡
	 */
	public void flushSDCard() {
		// TODO 将错误信息写入SD卡
	}

	public void release(int type) {
		if (isExceptionCodeExist(type)) {
			exceptionArrs.delete(type);
		} else {
			Log.d("This type has been release!");
		}
	}

	public void release() {
		exceptionArrs.clear();
	}

	class ErrorAction {
		private int code;
		private int type;

		public ErrorAction(int type, int code) {
			this.code = code;
			this.type = type;
		}

		public int getCode() {
			return code;
		}

		public void setCode(int code) {
			this.code = code;
		}

		public int getType() {
			return type;
		}

		public void setType(int type) {
			this.type = type;
		}
	}
}

package com.medzone.framework.task;

import com.medzone.framework.errorcode.ProxyCode;

public class BaseResult {

	public static final int DISPOSE_CODE_SUCCESS = 0;

	// http code success(200)
	protected boolean isSuccess = false;
	protected boolean isServerDisposeSuccess = false;
	// server response code(0)
	protected int errorCode;
	protected String errorMessage;

	public BaseResult() {
		super();
	}

	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}

	public int getErrorCode() {
		return errorCode;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	/**
	 * 
	 * 
	 * TODO Use {@link ProxyCode} instead of {@link #isSuccess()}.
	 * 
	 * @author Local_ChenJunQi
	 * 
	 */
	@Deprecated
	public boolean isSuccess() {
		return isSuccess;
	}

	/**
	 * TODO Use {@link ProxyCode} instead of {@link #setSuccess(boolean)}.
	 * 
	 * @author Local_ChenJunQi
	 * 
	 * @param isSuccess
	 */
	@Deprecated
	public void setSuccess(boolean isSuccess) {
		this.isSuccess = isSuccess;
	}

	/**
	 * TODO Use {@link ProxyCode} instead of
	 * {@link #setServerDisposeSuccess(boolean)}.
	 * 
	 * @author Local_ChenJunQi
	 * 
	 */
	@Deprecated
	public void setServerDisposeSuccess(boolean isServerDisposeSuccess) {
		this.isServerDisposeSuccess = isServerDisposeSuccess;
	}

	/**
	 * TODO Use {@link ProxyCode} instead of {@link #isServerDisposeSuccess()}.
	 * 
	 * @author Local_ChenJunQi
	 * 
	 */
	@Deprecated
	public boolean isServerDisposeSuccess() {
		return isServerDisposeSuccess;// errorCode == DISPOSE_CODE_SUCCESS;
	}

}
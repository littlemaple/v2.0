package com.medzone.framework.network.exception;

public class RestException extends Exception {

	private static final long serialVersionUID = 7499675036625522380L;

	public RestException(Exception e) {
		super(e);
	}

	public RestException(String exceptionMessage) {
		super(exceptionMessage);
	}
}

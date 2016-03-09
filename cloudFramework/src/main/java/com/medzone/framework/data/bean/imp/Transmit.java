package com.medzone.framework.data.bean.imp;

import java.io.Serializable;

/**
 * 用于传输的数据类
 * 
 * @author lwm
 * 
 */
public class Transmit implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String msg = "";
	private int what;
	private int arg1;

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public String getMsg() {
		return this.msg;
	}

	public int getWhat() {
		return what;
	}

	public void setWhat(int what) {
		this.what = what;
	}

	public int getArg1() {
		return arg1;
	}

	public void setArg1(int arg1) {
		this.arg1 = arg1;
	}

}

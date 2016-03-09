package com.medzone.cloud.acl.thread.audio;

public interface AcceptInterface {

	public void prepare();

	public void start();

	public void stop();

	public void release();

	public boolean isrunning();
	
}

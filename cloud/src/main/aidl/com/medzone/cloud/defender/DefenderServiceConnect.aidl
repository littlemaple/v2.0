package com.medzone.cloud.defender;
import com.medzone.cloud.defender.JPush;

interface DefenderServiceConnect{

	void initJPush();
	void startJPush();
	void stopJPush();
	String getRegisterID();
	
}
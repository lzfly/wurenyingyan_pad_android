package com.wuren.datacenter.util;

import android.os.Environment;

public class ConstUtils {
	
	//UDP find gateway command
	public final static String S_SEARCH_GATEWAY_COMMAND="GETIP\r\n";
	
	
	public static int SPLASH_DISPLAY_TIME = 1000;	//封面显示时间
	
	public static int LOGIN_WAIT_TIME = 1000;	//登录成功后等待时间
	
	public static int SERVICE_START_WAIT_TIME = 800;	//服务启动等待时间
	
	
	
//	
	public static String G_APK_NAME = "DataCenter";
	
	
	public static final String G_GLOABAL_PATH=  Environment.getExternalStorageDirectory()+"/"+G_APK_NAME;
	
	
	public static final class GatewayType{
		
		public static final int GATEWAY_FEBEE=101;//飞比网关
	}
	


}

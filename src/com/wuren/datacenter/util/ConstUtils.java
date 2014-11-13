package com.wuren.datacenter.util;

import android.os.Environment;

public class ConstUtils {
	
	public static int S_HTTP_REQUEST_RETRY_COUNT = 2;	//服务器请求次数
	public static int S_HTTP_REQUEST_TIMEOUT = 1000 * 20; //服务器请求超时时间	

	public static String S_NET_URL="http://192.168.1.101";
	public static String S_API_URL = S_NET_URL+":"+"8007";
	
	public static String S_INIT_URL = S_API_URL + "/smartcenter/init";				//智能主机初始化接口
	public static String S_LOGIN_URL = S_API_URL + "/smartcenter/login";			//智能主机登录接口
	public static String S_SYNC_DEVICE_URL = S_API_URL + "/device/deviceSync";		//设备同步接口
	public static String S_POST_DATA_URL = S_API_URL + "";		//
	
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

package com.wuren.datacenter.util;

import java.io.File;


import android.os.Environment;

public class ConstUtils {
	
	public static int S_HTTP_REQUEST_RETRY_COUNT = 2;	//服务器请求次数
	public static int S_HTTP_REQUEST_TIMEOUT = 1000 * 20; //服务器请求超时时间	

	public static String S_NET_URL="http://192.168.1.102";
	public static String S_API_URL = S_NET_URL+":"+"8007";
	
	public static String S_INIT_URL = S_API_URL + "/smartcenter/init";				//智能主机初始化接口
	public static String S_LOGIN_URL = S_API_URL + "/smartcenter/login";			//智能主机登录接口
	public static String S_SYNC_DEVICE_URL = S_API_URL + "/device/deviceSync";		//设备同步接口
	public static String S_POST_DATA_URL = S_API_URL + "/notice/new";		//提交设备上报数据
	public static String S_GET_DEVICE_TYPE_URL = S_API_URL + "/deviceType/deviceTypeList";		//获取设备类型接口
	public static String S_GET_BIND_CAMERA_URL = S_API_URL + "/device/getBindCamera";		//获取设备绑定的摄像头
	public static String S_GET_BIND_CAMERA_LIST_URL = S_API_URL + "/device/getBindCameraList";		//获取所有设备绑定的摄像头
	
	public static String S_UPLOAD_PICTURE_URL = S_API_URL + "/camera/uploadCameraScreenshot";//上传拍摄的照片接口	
	
	public static String S_GET_CAMERS_URL = S_API_URL + "/camera/cameraList";		//获取摄像列表
	
	//UDP find gateway command
	public final static String S_SEARCH_GATEWAY_COMMAND="GETIP\r\n";
	
	
	public final static int SPLASH_DISPLAY_TIME = 1000;	//封面显示时间
	
	public final static int LOGIN_WAIT_TIME = 1000;	//登录成功后等待时间
	
	public final static int SERVICE_START_WAIT_TIME = 800;	//服务启动等待时间
	
	public final static int DEVICE_OFFLINE_INTEVAL_TIME=3*60*1000;//设置设备在线超时时间为3分钟  
	
	public final static int DEVICE_EVENT_OCCUR_TIME=10*1000;//在这个时间内设备多次上报 只算做一次
	
//	
	public final static String G_APK_NAME = "DataCenter";
	
	
	public  final static int CAPTURE_PICTURE_NUM=5;
	
	public static final String G_GLOABAL_PATH=  Environment.getExternalStorageDirectory()+"/"+G_APK_NAME;
	
	public  static String G_IMAGE_PATH=ConstUtils.G_GLOABAL_PATH+File.separator+"jietu";
	
	


}

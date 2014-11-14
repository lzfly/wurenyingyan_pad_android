package com.wuren.datacenter.util;

import android.os.Environment;

public class ConstUtils {
	
	public static int S_HTTP_REQUEST_RETRY_COUNT = 2;	//�������������
	public static int S_HTTP_REQUEST_TIMEOUT = 1000 * 20; //����������ʱʱ��	

	public static String S_NET_URL="http://192.168.1.101";
	public static String S_API_URL = S_NET_URL+":"+"8007";
	
	public static String S_INIT_URL = S_API_URL + "/smartcenter/init";				//����������ʼ���ӿ�
	public static String S_LOGIN_URL = S_API_URL + "/smartcenter/login";			//����������¼�ӿ�
	public static String S_SYNC_DEVICE_URL = S_API_URL + "/device/deviceSync";		//�豸ͬ���ӿ�
	public static String S_POST_DATA_URL = S_API_URL + "/notice/new";		//�ύ�豸�ϱ�����
	
	//UDP find gateway command
	public final static String S_SEARCH_GATEWAY_COMMAND="GETIP\r\n";
	
	
	public final static int SPLASH_DISPLAY_TIME = 1000;	//������ʾʱ��
	
	public final static int LOGIN_WAIT_TIME = 1000;	//��¼�ɹ���ȴ�ʱ��
	
	public final static int SERVICE_START_WAIT_TIME = 800;	//���������ȴ�ʱ��
	
	public final static int DEVICE_OFFLINE_INTEVAL_TIME=2*60*1000;//�����豸���߳�ʱʱ��Ϊ3����  
	
	public final static int DEVICE_EVENT_OCCUR_TIME=10*1000;//�����ʱ�����豸����ϱ� ֻ����һ��
	
//	
	public final static String G_APK_NAME = "DataCenter";
	
	
	public static final String G_GLOABAL_PATH=  Environment.getExternalStorageDirectory()+"/"+G_APK_NAME;
	
	
	


}

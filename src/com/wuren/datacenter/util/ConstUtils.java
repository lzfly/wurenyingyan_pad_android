package com.wuren.datacenter.util;

import android.os.Environment;

public class ConstUtils {
	
	//UDP find gateway command
	public final static String S_SEARCH_GATEWAY_COMMAND="GETIP\r\n";
	
	
	public static final class FbeeControlCommand{
		
		
		public static final int SRPC_CMD_ID_POS = 0;
		public static final int SRPC_CMD_LEN_POS = 1;
	        
	        
		public static final byte HEART_BEAT = 1;
		public static final byte RPCS_CLOSE = (byte)0x80;
	    public static final byte RPCS_GET_DEVICES = (byte)0x81;
	    public static final byte RPCS_SET_DEV_STATE = (byte)0x82;
	    public static final byte RPCS_SET_DEV_LEVEL = (byte)0x83;
	    public static final byte RPCS_SET_DEV_COLOR = (byte)0x84;
	    public static final byte RPCS_GET_DEV_STATE = (byte)0x85;
	    public static final byte RPCS_GET_DEV_LEVEL = (byte)0x86;
	    public static final byte RPCS_GET_DEV_HUE = (byte)0x87;
	    public static final byte RPCS_GET_DEV_SAT = (byte)0x88;
	    public static final byte RPCS_BIND_DEVICES = (byte)0x89;
	    public static final byte RPCS_GET_THERM_READING = (byte)0x8a;
	    public static final byte RPCS_GET_POWER_READING = (byte)0x8b;
	    public static final byte RPCS_DISCOVER_DEVICES = (byte)0x8c;
	    public static final byte RPCS_SEND_ZCL = (byte)0x8d;
	    public static final byte RPCS_GET_GROUPS = (byte)0x8e;
	    public static final byte RPCS_ADD_GROUP = (byte)0x8f;
	    public static final byte RPCS_GET_SCENES = (byte)0x90;
	    public static final byte RPCS_STORE_SCENE = (byte)0x91;
	    public static final byte RPCS_RECALL_SCENE = (byte)0x92;
	    public static final byte RPCS_IDENTIFY_DEVICE = (byte)0x93;
	    public static final byte RPCS_CHANGE_DEVICE_NAME = (byte)0x94;
	    
	    
	}
	
	public static String G_APK_NAME = "DataCenter";
	
	
	public static final String G_GLOABAL_PATH=  Environment.getExternalStorageDirectory()+"/"+G_APK_NAME;
	
	
	public static final class GatewayType{
		
		public static final int GATEWAY_FEBEE=101;//·É±ÈÍø¹Ø
	}
	


}

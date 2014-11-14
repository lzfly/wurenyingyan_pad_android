package com.wuren.datacenter.util;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import android.util.Log;

import com.wuren.datacenter.List.DeviceList;
import com.wuren.datacenter.List.GatewayList;
import com.wuren.datacenter.bean.DeviceInfoBean;
import com.wuren.datacenter.bean.GatewayBean;
import com.wuren.datacenter.service.DataTransactionService;

public class DataUtils {
	
	
public static final class FbeeControlCommand{
		
		
		public static final int SRPC_CMD_ID_POS = 0;
		public static final int SRPC_CMD_LEN_POS = 1;
	        
	        
		public static final byte HEART_BEAT = 1;
		
		
		//SRPC CMD ID's	
	    //define the outgoing RPSC command ID's
		public static final byte RPCS_NEW_ZLL_DEVICE = (byte)0x0001;
		public static final byte RPCS_DEV_ANNCE = (byte)0x0002;
		public static final byte RPCS_SIMPLE_DESC = (byte)0x0003;
		public static final byte RPCS_TEMP_READING = (byte)0x0004;
		public static final byte RPCS_POWER_READING = (byte)0x0005;
		public static final byte RPCS_PING = (byte)0x0006;
	    public static final byte RPCS_GET_DEV_STATE_RSP = (byte)0x0007;
	    public static final byte RPCS_GET_DEV_LEVEL_RSP = (byte)0x0008;
	    public static final byte RPCS_GET_DEV_HUE_RSP = (byte)0x0009;
	    public static final byte RPCS_GET_DEV_SAT_RSP = (byte)0x000a;
	    public static final byte RPCS_ADD_GROUP_RSP = (byte)0x000b;
	    public static final byte RPCS_GET_GROUP_RSP = (byte)0x000c;
	    public static final byte RPCS_ADD_SCENE_RSP = (byte)0x000d;
	    public static final byte RPCS_GET_SCENE_RSP = (byte)0x000e;
	    public static final byte RPCS_GET_GATEDETAIL_RSP = (byte)0x15;
	    public static final byte RPCS_GET_DEV_ColorTemperature_RSP = (byte)0x27;
       
		public static final byte RPCS_GET_DEV_SP = (byte)0x70;
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
	    public static final byte RPCS_DELETE_DEVICE= (byte)0x95;
	    
	    public static final byte RPCS_GET_GATEDETAIL = (byte)0x9D;
	    public static final byte RPCS_ALLOW_ADDDEVICES= (byte)0x9F;
	    
	    public static final byte RPCS_OPENORCLOSE_ONLINE_SWITCH= (byte)0xA0;//打开、关闭在线状态查询
	    public static final byte RPCS_FACTORY_GATEWAY= (byte)0xA1;//网关复位 
	    
	    public static final byte RPCS_SET_DEV_ColorTemperature = (byte)0xA8;
	    public static final byte RPCS_GET_DEV_ColorTemperature = (byte)0xA9;
	    
	    
	}


	private static DataUtils mInstance;
	
	public static DataUtils getInstance()
	{
		if(mInstance==null)
			mInstance=new DataUtils();
		return mInstance;
		
	}
	
	private DataUtils()
	{
		
	}
	
	public static String bytes2HexString(byte[] b) {
	  	  String ret = "";
	  	  for (int i = 0; i < b.length; i++) {
	  	   String hex = Integer.toHexString(b[ i ] & 0xFF);
	  	   if (hex.length() == 1) {
	  	    hex = '0' + hex;
	  	   }
	  	   ret += hex.toUpperCase();
	  	  }
	  	  return ret;
	  }
	
	
//	public static List<GatewayBean> mListGateway=new ArrayList();
//	
//	public static List<DeviceInfoBean> mListDevices=new ArrayList();
	
	public static Hashtable mHtGatewayReceive_Socket_Thread=new Hashtable();
	
	
	
//	//通过gateway sn 获得gate对象
//	public GatewayBean getGate(String gateway_sn)
//	{
//		if(gateway_sn==null)
//			return null;
//		for(int i=0;i<mListGateway.size();i++)
//		{
//			GatewayBean temp=mListGateway.get(i);
//			if(temp.getSN().equals(gateway_sn))
//				return temp;
//		}
//		return null;
//	}
	
	public void executeCommand(Socket socket,int mControlType)
	{
		   //生成输出内容
           byte[] msg=null;
           
           switch(mControlType)
           {
            case FbeeControlCommand.RPCS_GET_DEVICES:
        	   
        	    msg=new byte[2];
           		msg[FbeeControlCommand.SRPC_CMD_ID_POS] = FbeeControlCommand.RPCS_GET_DEVICES;
           		msg[FbeeControlCommand.SRPC_CMD_LEN_POS] = 0;
           		break;
           		
           	default:
           		msg=null;
           		break;
           }
         
         	try {  	           	            	            
	            //获取输出流
	            OutputStream os= socket.getOutputStream();
	            
	            byte[] wr=DataUtils.getInstance().getSendSrpc(socket,msg);
	            
	            
	            if(os==null)//可能网关断了，需要从网关列表删除，并停止相应socket
	            {
	            	GatewayBean gate=GatewayList.findByIP(socket.getInetAddress().getHostAddress());
 					if(gate!=null)
 					{
 						DataTransactionService.mHtGateway_Socket_Table.remove(gate.getSN());
 						GatewayList.remove(gate);
 						//停止监听线程
 						
 						try { 							
 							socket.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
 						
 						
 						//将与该网关相关联的设备都置下线状态
 						List<DeviceInfoBean> listDevices=DeviceList.getDeviceList(gate);	 						
 						if(listDevices!=null && listDevices.size()>0)
 						{
 							for(int i=0;i<listDevices.size();i++)
 							{
 								DeviceInfoBean itemDevice=listDevices.get(i);
 								itemDevice.setIsOnline(false);
 								HttpUtils.deviceOffline(itemDevice, null);
 							}
 						}
 						
 					}
 					return;
	            }
	            //写入
	            os.write(wr);
	            
	            os.flush();
	            
	              
	        } catch (UnknownHostException e) {  
	            // TODO Auto-generated catch block  
	            e.printStackTrace();  
	        } catch (IOException e) {  
	            // TODO Auto-generated catch block  
	            e.printStackTrace();  
	        }
	}
	
	public  byte[] getSendSrpc(Socket socket,byte[] msg)
    {
		
		//获得SN array
		String ip=socket.getInetAddress().getHostAddress();
		byte[] sn=null;
		GatewayBean gateway = GatewayList.findByIP(ip);
		if (gateway != null)
		{
			sn = gateway.getSNArray();
		}
//		for(int i=0;i<mListGateway.size();i++)
//		{
//			String temp=mListGateway.get(i).getIP();
//			
//			if(temp.equalsIgnoreCase(ip))
//			{
//				sn=mListGateway.get(i).getSNArray();
//				break;
//			}
//		}
		
		if(sn==null)
			return null;
		
        int j = msg.length;
        byte[] bt1 = new byte[j + 7];
        bt1[0] = (byte)((j + 7) & 0xff);
        bt1[1] = (byte)(((j + 7) & 0xff00) / 0x100);
        
//        byte[] bt1 = new byte[j + 6];
//        bt1[0] = (byte)((j + 6) & 0xff);
//        bt1[1] = (byte)(((j + 6) & 0xff00) / 0x100);
        
//        
       
        
        for (int i = 0; i < 4; i++)
        {
            bt1[i + 2] = sn[i];
            
        }
        bt1[6] = (byte)0xfe;
        
        for (int i = 0; i < j; i++)
        {
            bt1[7 + i] = msg[i];
            
           
        }
        
        
        String command="";
		for(int k=0;k<bt1.length;k++)
		{
			command+=(bt1[k]&0xff);
			command+=" ";
			
		}
		
		 Log.v("jiaojc",socket.getInetAddress().getHostAddress()+" begin send command:"+command);
        
        return bt1;

    }
	

}

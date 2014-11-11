package com.wuren.datacenter.util;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import android.os.SystemClock;
import android.util.Log;

import com.wuren.datacenter.List.DeviceList;
import com.wuren.datacenter.List.GatewayList;
import com.wuren.datacenter.bean.DeviceInfoBean;
import com.wuren.datacenter.bean.GatewayBean;
import com.wuren.datacenter.service.DataTransactionService;
import com.wuren.datacenter.service.ServiceSocketMonitor;
import com.wuren.datacenter.util.DataUtils.FbeeControlCommand;


public class FebeeAPI {
	
private static FebeeAPI mInstance;
	
	private final static String TAG="FebeeAPI";
	public static FebeeAPI getInstance()
	{
		if(mInstance==null)
			mInstance=new FebeeAPI();
		return mInstance;
		
	}
	
	private FebeeAPI()
	{
		
	}
	
	private Socket getServerSocket(String gateway_sn)
	{
		Log.v(TAG,"into getServerSocket ,gateway_sn:"+gateway_sn);
		GatewayBean gate=GatewayList.getGateway(gateway_sn);				
		if(gate==null)
			return null;
		
		Object obj=DataTransactionService.mHtGateway_Socket_Table.get(gateway_sn);
		if(obj==null)
			return null;
		
		Socket receiveSocket=(Socket)obj;
			
		return receiveSocket;
		
	}
	
	private void sendCommand(Socket receiveSocket,byte[]msg)
	{
		try {  	           	            	            
            //获取输出流
            OutputStream os= receiveSocket.getOutputStream();
            
            byte[] wr=DataUtils.getInstance().getSendSrpc(receiveSocket,msg);
            
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
	
	//获得网关详细信息
	public void getGateDetailInfo(String gateway_sn)
	{
		Socket receiveSocket=getServerSocket(gateway_sn);
		
		if(receiveSocket==null)
			return;
		
		//发送get gate detail 指令 生成输出内容
		byte[] msg=new byte[1];
		msg[FbeeControlCommand.SRPC_CMD_ID_POS] = FbeeControlCommand.RPCS_GET_GATEDETAIL;
		  
		sendCommand(receiveSocket,msg);
						
	}
	
	
	//网关复位,isAll为true时复位网关内存储信息，并复位 zigbee 网络信息，完全回复出厂设置
	//isAll为false时只复位网关内存储信息
	public void resetGate(String gateway_sn,boolean isAll,GatewayListener listener)
	{
		//获得gate receive socket
		Socket receiveSocket=getServerSocket(gateway_sn);
		
		if(receiveSocket==null)
			return;
		
		ServiceSocketMonitor s=(ServiceSocketMonitor)DataUtils.mHtGatewayReceive_Socket_Thread.get(gateway_sn);
		if(listener!=null)
			s.setGatewayListener(listener);
		
	    //发送reset gate指令 生成输出内容
        byte[] msg=new byte[3];
		msg[FbeeControlCommand.SRPC_CMD_ID_POS] = FbeeControlCommand.RPCS_FACTORY_GATEWAY;
		msg[FbeeControlCommand.SRPC_CMD_LEN_POS] = 1;
		if(isAll)
			msg[2] = 0x50;
		else
			msg[2] = 0x0A;
		
		sendCommand(receiveSocket,msg);
	}
	
	//0x9F 允许入网.(无参数，发送该指令后允许入网 60 秒)
	public void allowAddDevices(String gateway_sn)
	{
		//获得gate receive socket
		Socket receiveSocket=getServerSocket(gateway_sn);
		
		if(receiveSocket==null)
			return;
				
		byte[] msg=new byte[1];
		msg[FbeeControlCommand.SRPC_CMD_ID_POS] = FbeeControlCommand.RPCS_ALLOW_ADDDEVICES;
		
		
		sendCommand(receiveSocket,msg);
	 		
	}
	
	
	//0x95 ,从网关删除指定设备
	public void deleteDevice(DeviceInfoBean device,DeviceListener listener)
	{
		String gateway_sn=device.getGateway_SN();
				
		if(gateway_sn==null || gateway_sn.length()==0)
			return;
		
		//获得gate receive socket
		Socket receiveSocket=getServerSocket(gateway_sn);
		
		if(receiveSocket==null)
			return;
				
		 byte[] msg=new byte[0x0C+2];
	        
	       
		msg[FbeeControlCommand.SRPC_CMD_ID_POS] = FbeeControlCommand.RPCS_DELETE_DEVICE;
		msg[FbeeControlCommand.SRPC_CMD_LEN_POS] = 0x0C;
		
		int index=2;
		msg[index++]=0x02;//地址模式
		
		//device短地址
		int shortAddr=device.getShortAddr();
		
		//将569E转换为56和9E格式--jiaojc。
		int first_shortAddr_byte= shortAddr>>8;
		int second_shortAddr_byte=shortAddr&255;	
		
		
		msg[index++]=(byte)second_shortAddr_byte;
		msg[index++]=(byte)first_shortAddr_byte;
		
		//IEEE
		byte[] device_ieee=device.getIEEE();
		for(int i=0;i<device_ieee.length;i++)
		{
			msg[index++]=device_ieee[i];
		}
		
		//EndPoint
		msg[index++]=(byte)device.getEndPoint();
        
	
		sendCommand(receiveSocket,msg);
		
		SystemClock.sleep(2000);
		
		DeviceList.remove(device);
		
		
		if(listener!=null)
			listener.onTaskComplete();
		//
		
	}
	
	//0x94 更改设备名
	public void changeDeviceName(DeviceInfoBean device,String name,DeviceListener listener)
	{
		String gateway_sn=device.getGateway_SN();
		
		if( gateway_sn==null || gateway_sn.length()==0 || name==null || name.length()==0 )
			return;
		
		//获得gate receive socket
		Socket receiveSocket=getServerSocket(gateway_sn);
		
		if(receiveSocket==null)
			return;
		
		byte[] name_array=name.getBytes();
		
		int name_length=name_array.length;
		
		
				
		byte[] msg=new byte[name_length+6];
		msg[FbeeControlCommand.SRPC_CMD_ID_POS] = FbeeControlCommand.RPCS_CHANGE_DEVICE_NAME;
		msg[FbeeControlCommand.SRPC_CMD_LEN_POS] = (byte)(name_length+4);
		
		int index=2;
		
		//device短地址
		int shortAddr=device.getShortAddr();
		
		//将569E转换为56和9E格式--jiaojc。
		int first_shortAddr_byte= shortAddr>>8;
		int second_shortAddr_byte=shortAddr&255;
		
		msg[index++]=(byte)second_shortAddr_byte;
		msg[index++]=(byte)first_shortAddr_byte;
		
		//EndPoint
		msg[index++]=(byte)device.getEndPoint();
		
		msg[index++]=(byte)name_length;
		
	
		
		for(int i=0;i<name_array.length;i++)
		{
			msg[index++]=name_array[i];
		}
		
		
		sendCommand(receiveSocket,msg);
		
		
		
		SystemClock.sleep(2000);
		
		device.setName(name);
		
		DeviceList.put(device);
		
		
		if(listener!=null)
			listener.onTaskComplete();
	 		
	}
	
	//

}

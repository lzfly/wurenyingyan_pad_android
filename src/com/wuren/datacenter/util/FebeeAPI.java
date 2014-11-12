package com.wuren.datacenter.util;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import android.util.Log;

import com.wuren.datacenter.List.GatewayList;
import com.wuren.datacenter.bean.GatewayBean;
import com.wuren.datacenter.service.DataTransactionService;
import com.wuren.datacenter.service.ServiceSocketMonitor;
import com.wuren.datacenter.util.DataUtils.FbeeControlCommand;


public class FebeeAPI {
	
private static FebeeAPI mInstance;
	
	public static FebeeAPI getInstance()
	{
		if(mInstance==null)
			mInstance=new FebeeAPI();
		return mInstance;
		
	}
	
	private FebeeAPI()
	{
		
	}
	
	
	//网关复位,isAll为true时复位网关内存储信息，并复位 zigbee 网络信息，完全回复出厂设置
	//isAll为false时只复位网关内存储信息
	public void resetGate(String gateway_sn,boolean isAll,GatewayListener listener)
	{
		Log.v("jiaojc","0000000");
		//获得gate receive socket
		GatewayBean gate = GatewayList.getGateway(gateway_sn);
		if(gate==null)
			return;
		Object obj=DataTransactionService.mHtGateway_Socket_Table.get(gateway_sn);
		
		Log.v("jiaojc","1111111111111");
		if(obj==null)
			return;
		Log.v("jiaojc","222222222222");
		Socket receiveSocket=(Socket)obj;
		
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
        		
		Log.v("jiaojc","3333333333333");
       
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
		
		
		//接收成功与否的数据
	}

}

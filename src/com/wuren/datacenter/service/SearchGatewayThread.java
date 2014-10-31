package com.wuren.datacenter.service;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

import com.wuren.datacenter.bean.GatewayBean;
import com.wuren.datacenter.util.ConstUtils;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;



public class SearchGatewayThread extends Thread{
	
	
	private static final String LOG_TAG = "SearchGatewayThread";
	  
    //private String mDataString;  
    //private DatagramSocket mUdpSocket;  
    public static final int DEFAULT_PORT = 9090;  
    
    private static final int MAX_DATA_PACKET_LENGTH = 256;
    
    //每隔30秒搜索在局域网一次网关
    private static final int SEARCH_INTEVAL_SECOND=30;
   
    private Context mContext;
    public SearchGatewayThread( Context context) {  
    	  
    	this.mContext=context;
    }  
    
    public void run(){  
    	
    	
    	while(true)
    	{
    		search();
    		SystemClock.sleep(SEARCH_INTEVAL_SECOND*1000);
    	}
    	
    	
		
    } 
    
    
    private boolean isExist(GatewayBean gate)
    {
    	if(DataTransactionService.mListGateway==null)
    		return false;
    	
    	boolean b=false;
    	for(int i=0;i<DataTransactionService.mListGateway.size();i++)
    	{    		
    		GatewayBean temp=DataTransactionService.mListGateway.get(i);
    		if(temp.getIP().equalsIgnoreCase(gate.getIP()))
    		{
    			b=true;
    			break;
    		}
    	}
    	return b;
    }
     
    private void searchFebee(int gatewayType)
    {
    	String mDataString=ConstUtils.S_SEARCH_GATEWAY_COMMAND;
    	DatagramPacket dataPacket = null;
    	
    	DatagramSocket mUdpSocket = null;
    	   
		try {  
    		mUdpSocket = new DatagramSocket(DEFAULT_PORT );  
    		byte[] buffer = new byte[MAX_DATA_PACKET_LENGTH];  
            dataPacket = new DatagramPacket(buffer, MAX_DATA_PACKET_LENGTH);   
            
            byte[] data = mDataString.getBytes();  
            dataPacket.setData( data );  
            dataPacket.setLength( data.length );  
            dataPacket.setPort( DEFAULT_PORT );     

            InetAddress broadcastAddr;  

            broadcastAddr = InetAddress.getByName("255.255.255.255");  
            dataPacket.setAddress(broadcastAddr);
            
            Log.v(LOG_TAG,"begin send command:"+mDataString);
            
            mUdpSocket.send(dataPacket);
        } catch (Exception e) {  
            Log.e(LOG_TAG, e.toString());  
        } 
    
        Log.v(LOG_TAG,"begin receive command...");
        byte[] data=new byte[256] ;
        DatagramPacket udpReceivePacket  = new DatagramPacket( data, 256 );
        
        GatewayBean gate=new GatewayBean();
        
        //如果10秒后还没收到网关回应，则认为局域网里没有智能网关，就跳出循环流程。
        int maxWaitCount=10;
        int iWaitFlag=0;
        boolean bFoundGateway=false;
	    while( iWaitFlag < maxWaitCount )
	    {  	      	
	          try {      
	          	mUdpSocket.receive(udpReceivePacket);  
	          } catch (Exception e) {  
	              System.out.println( e.toString());  
	          }  
	
	          String strMsg=new String(udpReceivePacket.getData()).trim();
	          if( strMsg.length() != 0 ){
	          	
	              if(strMsg.contains("IP:"))
		            {	            	
		            	Log.v(LOG_TAG,"Found IP gateway:"+strMsg);
		            	
		            	String strArray[]=strMsg.split("\r\n");
		            	
		            	
		            	String strIPArray[]=strArray[0].split(":");
		            	
		            	if(DataTransactionService.mListGateway==null)
		            		DataTransactionService.mListGateway=new ArrayList();
		            	
		            	gate.setIP(strIPArray[1]);
		            	
		            	String strSNArray[]=strArray[1].split(":");
		            	gate.setSN(strSNArray[1]);
		            	
		            	
		            	String str3=strSNArray[1].substring(0,2);
		            	String str2=strSNArray[1].substring(2,4);
		            	String str1=strSNArray[1].substring(4,6);
		            	String str0=strSNArray[1].substring(6,8);
		            	
		            	byte[]karray=new byte[4];
		            		            	
		            	
		            	karray[0]=(byte)(int)Integer.valueOf(str0,16);	            		            	
		            	karray[1]=(byte)(int)Integer.valueOf(str1,16);
		            	karray[2]=(byte)(int)Integer.valueOf(str2,16);
		            	karray[3]=(byte)(int)Integer.valueOf(str3,16);
		            	
		            	gate.setSNArray(karray);
		            	
		            	
		            	Log.v("jiaojc","IP:"+gate.getIP()+"\tSN:"+gate.getSN());
		            	
		            	if(!isExist(gate))
		            	{
		            		DataTransactionService.mListGateway.add(gate);
		            		//开始启动监听线程 开始监听该gateway数据
		            		beginListenGateway(gate);
		            		
		            		//等待数秒再往下处理
		            		SystemClock.sleep(2000);
		            		
		            		//开始启动心跳监听进程开始监听是否与网关断网
		            		startHeartBeatListen(gate);
		            		
		            		//开始启动定期轮询搜索与该网关相联的设备
		            		startSearchDevice(gate);
		            	}
		            			            	
		            	bFoundGateway=true;
		            	
		            	//将byte转为int的方法 记得保留下来。
		            	//int tt=karray[0]&0xff;
		            
		         	    break;
		            	
		            }
	          
	          }  
	          SystemClock.sleep(1000);
	          iWaitFlag++;
	      }  
	    
	      if(!bFoundGateway)
	      {
	    	  Log.v(LOG_TAG,"Sorry,can not find gateway!");
	      }
	      mUdpSocket.close();
    }
    
    private void startHeartBeatListen(GatewayBean gate)
    {
    	
    	 Intent intent = new Intent( mContext, DataTransactionService.class);
		 intent.setAction(DataTransactionService.SEARCH_HEART_BEAT_ACTION);
		 intent.putExtra("gateway_sn", gate.getSN());//目的是为搜索到侦听的socket		 
		 mContext.startService(intent);
    	
    }
    
    private Socket beginListenGateway( GatewayBean gate)
    {
    	Socket mSocket=null;
    	try {
			mSocket = new Socket(gate.getIP(), 8001);	
			mSocket.setKeepAlive(true);
			mSocket.setSoTimeout(120);  
//			//将socket与网关SN相关联
			DataTransactionService.mHtGateway_Socket_Table.put(gate.getSN(), mSocket);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
    	ServiceSocketMonitor serviceSocketMonitor=null;
						
    	serviceSocketMonitor=new ServiceSocketMonitor(mSocket,mContext);
				
		(new Thread(serviceSocketMonitor)).start();
		
		return mSocket;
		
    	
    }
    
    
    private void startSearchDevice( GatewayBean gate)
    {
		 Intent intent = new Intent( mContext, DataTransactionService.class);
		 intent.setAction(DataTransactionService.SEARCH_DEVICES_ACTION);
		 intent.putExtra("gateway_sn", gate.getSN());//目的是为搜索到侦听的socket		 
		 mContext.startService(intent);
		 
    }
  
    private void search()
    {
    	//开始搜索飞比网关
    	searchFebee(ConstUtils.GatewayType.GATEWAY_FEBEE);
    }
    


}


//public class SearchGatewayThread extends Thread{
//	
//	
//	private static final String LOG_TAG = "SearchGatewayThread";
//	  
//    private String mDataString;  
//    private DatagramSocket mUdpSocket;  
//    public static final int DEFAULT_PORT = 9090;  
//    
//    private static final int MAX_DATA_PACKET_LENGTH = 256;  
//   
//    private Context mContext;
//    public SearchGatewayThread( String dataString ,Context context) {  
//    	this.mDataString = dataString;  
//    	this.mContext=context;
//    }  
//    
//     
//  
//    public void run(){  
//		DatagramPacket dataPacket = null;  
//   
//		try {  
//    		mUdpSocket = new DatagramSocket(DEFAULT_PORT );  
//    		byte[] buffer = new byte[MAX_DATA_PACKET_LENGTH];  
//            dataPacket = new DatagramPacket(buffer, MAX_DATA_PACKET_LENGTH);   
//            
//            byte[] data = mDataString.getBytes();  
//            dataPacket.setData( data );  
//            dataPacket.setLength( data.length );  
//            dataPacket.setPort( DEFAULT_PORT );     
//
//            InetAddress broadcastAddr;  
//
//            broadcastAddr = InetAddress.getByName("255.255.255.255");  
//            dataPacket.setAddress(broadcastAddr);
//            
//            Log.v(LOG_TAG,"begin send command:"+mDataString);
//            
//            mUdpSocket.send(dataPacket);
//        } catch (Exception e) {  
//            Log.e(LOG_TAG, e.toString());  
//        } 
//    
//        Log.v(LOG_TAG,"begin receive command...");
//        byte[] data=new byte[256] ;
//        DatagramPacket udpReceivePacket  = new DatagramPacket( data, 256 );
//        
//        GatewayBean gate=new GatewayBean();
//        while( true )
//        {  
//        	
//            try {      
//            	mUdpSocket.receive(udpReceivePacket);  
//            } catch (Exception e) {  
//                System.out.println( e.toString());  
//            }  
//
//            String strMsg=new String(udpReceivePacket.getData()).trim();
//            if( strMsg.length() != 0 ){
//            	
//                if(strMsg.contains("IP:"))
//	            {	            	
//	            	Log.v(LOG_TAG,strMsg);
//	            	
//	            	String strArray[]=strMsg.split("\r\n");
//	            	
//	            	
//	            	String strIPArray[]=strArray[0].split(":");
//	            	
//	            	if(DataTransactionService.mListGateway==null)
//	            		DataTransactionService.mListGateway=new ArrayList();
//	            	
//	            	gate.setIP(strIPArray[1]);
//	            	
//	            	String strSNArray[]=strArray[1].split(":");
//	            	gate.setSN(strSNArray[1]);
//	            	
//	            	
////	            	Integer sn10=Integer.valueOf(strSNArray[1],16);
////	            	
////	            	Log.v("jiaojc","sn10:"+sn10);
////	            	
////	            	int []karray=new int[4];
////	            	karray[0]=sn10%256;
////	            	karray[1]=(sn10/256)%256;
////	            	karray[2]=(sn10/256/256)%256;
////	            	karray[3]=sn10/256/256/256;
////	            	
////	            	Log.v("jiaojc","karray[0]:"+karray[0]+"\tkarray[1]:"+karray[1]+"\tkarray[2]:"+karray[2]
////	            			+"\tkarray[3]:"+karray[3]);
////	            	DataTransactionService.mGateway.setSNArray(karray);
//	            	
//	            	String str3=strSNArray[1].substring(0,2);
//	            	String str2=strSNArray[1].substring(2,4);
//	            	String str1=strSNArray[1].substring(4,6);
//	            	String str0=strSNArray[1].substring(6,8);
//	            	
//	            	byte[]karray=new byte[4];
//	            		            	
//	            	
//	            	karray[0]=(byte)(int)Integer.valueOf(str0,16);	            		            	
//	            	karray[1]=(byte)(int)Integer.valueOf(str1,16);
//	            	karray[2]=(byte)(int)Integer.valueOf(str2,16);
//	            	karray[3]=(byte)(int)Integer.valueOf(str3,16);
//	            	
//	            	gate.setSNArray(karray);
//	            	
//	            	
//	            	Log.v("jiaojc","IP:"+gate.getIP()+"\tSN:"+gate.getSN());
//	            	
//	            	DataTransactionService.mListGateway.add(gate);
//	            	
//	            	
//	            	
//	            	//将byte转为int的方法 记得保留下来。
//	            	//int tt=karray[0]&0xff;
//	            
//	         	    break;
//	            	
//	            }
//            
//            }  
//            SystemClock.sleep(1000);
//        }  
//    }  
//
//
//}

package com.wuren.datacenter.service;



import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;

import com.wuren.datacenter.List.DeviceList;
import com.wuren.datacenter.List.GatewayList;
import com.wuren.datacenter.bean.DeviceInfoBean;
import com.wuren.datacenter.bean.GatewayBean;
import com.wuren.datacenter.util.ConstUtils;
import com.wuren.datacenter.util.DataUtils;
import com.wuren.datacenter.util.FebeeAPI;
import com.wuren.datacenter.util.HttpUtils;
import com.wuren.datacenter.util.Log;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Base64;

public class DataTransactionService extends Service{

	
	private static final String LOG_TAG = "DataTransactionService";
	
	//Search gateway in local network
	public static final String SEARCH_GATEWAY_ACTION="com.wuren.datacenter.SEARCH_GATEWAY";
	
	//Find device list with the gateway
	public static final String SEARCH_DEVICES_ACTION="com.wuren.datacenter.SEARCH_DEVICES";
	//public static final String SEARCH_GATEWAY_ACTION="com.wuren.datacenter.SEARCH_GATEWAY";
	
	
	public static final String RESPONSE_COMMANDS_ACTION="com.wuren.datacenter.RESPONSE_COMMANDS";
	
	
	//心跳进程启动
	public static final String SEARCH_HEART_BEAT_ACTION="com.wuren.datacenter.HEARTBEAT";
	
	
	public static final String REQUEST_GATEWAYDETAIL_ACTION="com.wuren.datacenter.REQUEST_GATEWAYDETAIL";
	
	
	
	//设备在线状态监听进程
	public static final String Listen_Device_Online_Status_ACTION="com.wuren.datacenter.ListenDeviceOnline";
		
	public static Hashtable mHtGateway_Socket_Table=new Hashtable();
	
	public static Handler mHandler;
	
	
	private static Thread m_receiveThread;
	
	private static ServiceSocketMonitor serviceSocketMonitor;
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	
	private void processSearchGateway()
	{
			//new SearchGatewayThread(ConstUtils.S_SEARCH_GATEWAY_COMMAND,this).start();			
			new SearchGatewayThread(this).start();
		
	}
	
	  ///////////////////////////////////////////////////////////////////////////////////
		 // MD5加密		 
	     private byte[] getMD5ByteArray(String str) {     
	        MessageDigest messageDigest = null;     
	     
	        try {     
	            messageDigest = MessageDigest.getInstance("MD5");     
	     
	            messageDigest.reset();     
	     
	            messageDigest.update(str.getBytes("UTF-8"));     
	        } catch (NoSuchAlgorithmException e) {     
	            System.out.println("NoSuchAlgorithmException caught!");     
	            System.exit(-1);     
	        } catch (UnsupportedEncodingException e) {     
	            e.printStackTrace();     
	        }     
	     
	        byte[] byteArray = messageDigest.digest();
	        
	        Log.v(LOG_TAG,"after md5,byteArray length:"+byteArray );
	        return byteArray;
	     
	    }  
	    
	
	
	private String f(String a,String b,String c)
	{
		
		String x=a+b+c;
		String base64=Base64.encodeToString(getMD5ByteArray(x), Base64.DEFAULT);
		
		String result1=base64.replace('+', 'm');
		String result2=result1.replace('/', 'f');
		
		String finalResult=result2.replaceAll("=", "");
		
		 
		return finalResult;
	}
	
	//////////////////////////////////////////////////////////////////////
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		
		Log.v(LOG_TAG,"DataTransactionService onCreate");
		
		//mHandler=new Handler();
		
		File configPath=new File(ConstUtils.G_GLOABAL_PATH);
		if(!configPath.exists())
		{
			configPath.mkdir();
		}
//		
//		String test=f("A000001A2B3C4F","sprint","foobar");
//		String test2=f("","","foobar");
//		Log.v(LOG_TAG,"test:"+test+"\tlength:"+test.length());
//		Log.v(LOG_TAG,"test2:"+test2+"\t test2 length:"+test2.length());
		
		processSearchGateway();  
		
		
		
		(new Thread(new RepeatLoginService())).start();
				
		startListenDevicesOnlineStatus();
	
		
		
	}
	
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		//return super.onStartCommand(intent, flags, startId);
		
		ProcessSessionRequest processSessionRequest = new ProcessSessionRequest(intent,flags,startId);
		new Thread(processSessionRequest).start();
		
		return START_REDELIVER_INTENT;       
	}
	

	
	
	 private void startListenDevicesOnlineStatus()
	 {		 
		 new Thread(new DeviceOnlineListenRequest()).start();   
	 }
	    
	
	 
	
	
	private class ProcessSessionRequest implements Runnable
	{
		
		private int mStartID = -1;
    	Intent mIntent = null;
    	int mFlags = -1;
    	
    	public ProcessSessionRequest(Intent intent, int flags, int startId)
		{
			Log.v( LOG_TAG, "ProcessSessionRequest(Intent intent, int flags, int startId).");
			this.mIntent = intent;
			this.mFlags = flags;
			this.mStartID = startId;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			Log.v( LOG_TAG, "run().");
			
			String strAction = mIntent.getAction();
			Log.v( LOG_TAG, "onStartCommand :Intent: " + mIntent);
			
			Log.v(LOG_TAG,"Received action:"+strAction);
			
        	if(strAction == null)
        	{
        		Log.i( LOG_TAG, ".... Warning: the ActiveCare service is started with no action");
        		return;
        	}
        	
        	if ( strAction.equals(SEARCH_GATEWAY_ACTION))
	   		{
        		//First should find gateway info
        		processSearchGateway();        		
	   		}
        	
        	if ( strAction.equals(SEARCH_DEVICES_ACTION))
	   		{
        	  String gateway_sn=mIntent.getStringExtra("gateway_sn");
	 		  Object obj=mHtGateway_Socket_Table.get(gateway_sn);
	 		  
	 		  if(obj!=null)
	 		  {
	 			 Socket socket=(Socket)obj;  
	 			 SearchDeviceRequest searchDeviceRequest = new SearchDeviceRequest(socket);
	 			 new Thread(searchDeviceRequest).start();
	 		  }
	   		}
        	
        	//启动心跳监听网关是否断连
        	if ( strAction.equals(SEARCH_HEART_BEAT_ACTION))
        	{
        		String gateway_sn=mIntent.getStringExtra("gateway_sn");
        		Object obj=mHtGateway_Socket_Table.get(gateway_sn);
        		if(obj!=null)
  	 		    {
        			 Socket socket=(Socket)obj; 
        			 
        			 HeartBeatRequest heartBeatRequest = new HeartBeatRequest(socket);
    	 			 new Thread(heartBeatRequest).start();	 
  	 		    }
        	}
        	//Get gate detail info
        	if ( strAction.equals(REQUEST_GATEWAYDETAIL_ACTION))
        	{
        		String gateway_sn=mIntent.getStringExtra("gateway_sn");
        		
        		GatewayBean gate=GatewayList.getGateway(gateway_sn);
        		if(gate!=null)
        		{
        			String username=gate.getUsername();
        			String password=gate.getPassword();
    				
        			if(username==null || username.length()==0)
        			{
        				FebeeAPI.getInstance().getGateDetailInfo(gateway_sn);
        				SystemClock.sleep(3000);
        				
        				username=gate.getUsername();
        				password=gate.getPassword();
        				
        				
        			}
        			    				
    				Log.v(LOG_TAG,"getway user:"+username+"\tpwd:"+password);

        		}
        	}
        	
        	
        	
		}
	}
	
	private class HeartBeatRequest implements Runnable
	{
		
		private Socket mSocket;
		public HeartBeatRequest(Socket socket)
		{
			this.mSocket=socket;
			
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			while(true)
			{
				try{
	 				mSocket.sendUrgentData(0xFF);
 				}
				catch(Exception ex)
				{
	 					//ex.printStackTrace();
	 					Log.v(LOG_TAG,"heart beat send error:"+mSocket.getInetAddress().getHostAddress());
	 					//从table里删除mSocket,并关闭当前socket,Gatewaylist里也要删除相应的网关
	 					removeSocket(mSocket);
	 					
	 					break;	 					
				}
				SystemClock.sleep(5*1000);//5秒监听一次
				
			}
		}
		
	}
	
	
	public static  void removeSocket(Socket socket)
	{
		GatewayBean gate=GatewayList.findByIP(socket.getInetAddress().getHostAddress());
		if(gate!=null)
		{
			mHtGateway_Socket_Table.remove(gate.getSN());
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
	}


	
	
	private class DeviceOnlineListenRequest implements Runnable
	{
		@Override
		public void run() {
			// TODO Auto-generated method stub
			while(true)
			{
				Date currentDate=new Date();
				
				List<DeviceInfoBean> currentDeviceList=DeviceList.getDeviceList();
				for(int i=0;i<currentDeviceList.size();i++)
				{
					DeviceInfoBean bean=currentDeviceList.get(i);
					Date heartTime=bean.getHeartTime();
					if(heartTime==null)
						continue;
					else
					{
						//比较当前时间和heartTime,当超过设定时间就算下线
						//需要上报服务器并置在线状态为false
						
						 long between=(currentDate.getTime()-heartTime.getTime())/1000;		
						 
						 
						 if(between>(ConstUtils.DEVICE_OFFLINE_INTEVAL_TIME/1000))
						 {
							 
							 if(bean.isOnline())
							 {
								 Log.v("jiaojc1","between >DEVICE_OFFLINE_INTEVAL_TIME device is offline");
								 bean.setIsOnline(false);
								 HttpUtils.deviceOffline(bean, null);
							 }
						 }
					}
				}
				SystemClock.sleep(30*1000);//30秒监听一次				
			}
		}
		
	}
	
	private class SearchDeviceRequest implements Runnable
	{
		
		private Socket mSocket;
		public SearchDeviceRequest(Socket socket)
		{
			this.mSocket=socket;
			
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			while(true)
			{
				if(mSocket.isClosed())
					break;
				
				//GatewayBean gate=GatewayList.findByIP(mSocket.getInetAddress().getHostAddress());
				
				DataUtils.getInstance().executeCommand(mSocket,DataUtils.FbeeControlCommand.RPCS_GET_DEVICES);
				SystemClock.sleep(2*60*1000);//120秒搜索一次设备
				
			}
		}
		
	}
	
	
	/**   
     * 追加文件：使用FileWriter   
     *    
     * @param fileName   
     * @param content   
     */    
    public static void FileWriter(String fileName, String content) {
    	
        FileWriter writer = null;  
        try {     
            // 打开一个写文件器，构造函数中的第二个参数true表示以追加形式写文件     
            writer = new FileWriter(fileName, true);     
            writer.write(content);       
        } catch (IOException e) {     
            e.printStackTrace();     
        } finally {     
            try {     
                if(writer != null){  
                    writer.close();     
                }  
            } catch (IOException e) {     
                e.printStackTrace();     
            }     
        }   
    }    
	
    

	   private   boolean isExistInFile(String filePath,String compareTxt){
	        FileInputStream fis = null;
	        byte[] mByte = new byte[1024];
	        
	       boolean bExist=false;
	        try {
	        	fis = new FileInputStream(new File(filePath));
	            //fis = new FileInputStream(File);
	            InputStreamReader isr=new InputStreamReader(fis);
	            
	            BufferedReader br=new BufferedReader(isr);
	            
	            String show=br.readLine();
	            if(show.compareToIgnoreCase(compareTxt)==0)
	            {
	            	return true;
	            	
	            }
	            
	            while(show!=null){
	            //String show=br.readLine();
	            System.out.println(show);            
	            show=br.readLine();
	            
	            if(show!=null && show.compareToIgnoreCase(compareTxt)==0)
	            {
	            	bExist=true;
	            	break;
	            }
	            	
	            
	            }
	          
	        }   
	        catch (FileNotFoundException e) 
	        {
	            e.printStackTrace();
	        } 
	        catch (IOException e) {
	            e.printStackTrace();
	        }
	        finally{
	        try {
	            if(null!=fis)
	            fis.close();
	        } 
	        catch (IOException e) {
	            e.printStackTrace();
	            }
	        }
	        return bExist;
	     }
	
	

	
	
	
	
	
	
	
}

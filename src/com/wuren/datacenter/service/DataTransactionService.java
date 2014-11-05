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

import com.wuren.datacenter.bean.DeviceInfoBean;
import com.wuren.datacenter.bean.GatewayBean;
import com.wuren.datacenter.util.ConstUtils;
import com.wuren.datacenter.util.DataUtils;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Base64;
import android.util.Log;

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
	        
	        Log.v("jiaojc","after md5,byteArray length:"+byteArray );
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
//		Log.v("jiaojc","test:"+test+"\tlength:"+test.length());
//		Log.v("jiaojc","test2:"+test2+"\t test2 length:"+test2.length());
		
		processSearchGateway();     
	
		
		
	}
	
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		//return super.onStartCommand(intent, flags, startId);
		
		ProcessSessionRequest processSessionRequest = new ProcessSessionRequest(intent,flags,startId);
		new Thread(processSessionRequest).start();
		
		return START_REDELIVER_INTENT;       
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
				Log.v("jiaojc","heart beat:"+mSocket.getInetAddress().getHostAddress());
				
				try{
	 				mSocket.sendUrgentData(0xFF);
	 				}
				catch(Exception ex)
				{
	 					ex.printStackTrace();
	 					Log.v("jiaojc","heart beat send error:"+mSocket.getInetAddress().getHostAddress());
	 			//	reconnect();
	 			}
				SystemClock.sleep(3*1000);//3秒监听一次
				
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
				DataUtils.getInstance().executeCommand(mSocket,DataUtils.FbeeControlCommand.RPCS_GET_DEVICES);
				SystemClock.sleep(40*1000);//40秒搜索一次设备
				
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

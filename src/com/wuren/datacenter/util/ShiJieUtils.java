package com.wuren.datacenter.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.util.Base64;
import android.util.Log;

public class ShiJieUtils {
	
	public final static String TAG="ShiJieUtils";
	
	  private static void byte2image(byte[] data,String path){
	      if(data.length<3||path.equals("")) 
	    	  return;
	      try{
	    	 
	    	  FileOutputStream imageOutput = new FileOutputStream(new File(path));
		      imageOutput.write(data, 0, data.length);
		      imageOutput.close();
		      System.out.println("Make Picture success,Please find image in " + path);
	      }
	      catch(Exception ex) 
	      {
	         System.out.println("Exception: " + ex);
	         ex.printStackTrace();
	      }
	    }
	  
	
	public static void Capture( String address,String port,String fileFullPathName)
	{		
		String username="admin";
		String password="admin";
		int channel=1;		
		Capture(address,port,fileFullPathName,username,password,channel);			
	}
	
	public static void Capture( String address,String port,String fileFullPathName,String username,String password,int channel)
	{
		
		
		
		//String url="http://192.168.1.23:10080/jpgimage/1/image.jpg";
		String url=null;
		if(Integer.parseInt(port)!=80)
			url="http://"+address+":"+port+"/jpgimage/"+channel+"/image.jpg";
		else
			url="http://"+address+"/jpgimage/"+channel+"/image.jpg";
		
		Log.v(TAG,"Begin capture image,url:"+url);
		
		HttpGet request=new HttpGet(url);
		byte[] encodedPassword = (username + ":" + password).getBytes();		
		request.addHeader("Authorization","Basic "+Base64.encodeToString(encodedPassword, Base64.DEFAULT)); 
		HttpResponse resp=null;
		
		HttpClient client = new DefaultHttpClient();
		try {
			resp=client.execute(request);
			if (resp.getStatusLine().getStatusCode() == HttpStatus.SC_OK)
			{
				HttpEntity a=resp.getEntity();
				if(a.isStreaming())
				{
					byte[] result = null; 
					Log.v(TAG,"request image OK");
					result = EntityUtils.toByteArray(a);
					//byte2image(result,ConstUtils.G_LAST_IMAGE_PATH);				
					byte2image(result,fileFullPathName);
				}
				else
				{									
					Log.v(TAG,"request image failed,I am a txt.");
				}
				
			}
			else
			{
				Log.v(TAG,"resp.getStatusLine().getStatusCode():"+resp.getStatusLine().getStatusCode());	
			}
			
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}										
	}
	
	
//	private void get()
//	{
//		//String url="192.168.1.23:10080/jpgimage/1/image.jpg";
//		String url="http://192.168.1.23:10080/cgi/sys_get?Group=DeviceInfo";
//		HttpGet request=new HttpGet(url);
//		String username="admin";
//		String password="admin";
//		byte[] encodedPassword = (username + ":" + password).getBytes();
//		
//		request.addHeader("Authorization","Basic "+Base64.encodeToString(encodedPassword, Base64.DEFAULT)); 
//		Log.v("jiaojc",""+Base64.encodeToString(encodedPassword, Base64.DEFAULT));
//		HttpResponse resp=null;
//		
//		HttpClient client = new DefaultHttpClient();
//		try {
//			resp=client.execute(request);
//			if (resp.getStatusLine().getStatusCode() == HttpStatus.SC_OK)
//			{
//				
//				String result= EntityUtils.toString(resp.getEntity());
//				Log.v("jiaojc","request get OK:"+result);
//			}
//			else
//			{
//				Log.v("jiaojc","resp.getStatusLine().getStatusCode():"+resp.getStatusLine().getStatusCode());	
//			}
//			
//		} catch (ClientProtocolException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
//				
//		
//	}
//	
	
	
	

}

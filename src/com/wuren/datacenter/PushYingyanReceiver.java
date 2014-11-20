package com.wuren.datacenter;

import java.io.File;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.igexin.sdk.PushConsts;
import com.wuren.datacenter.List.DeviceBindCameraList;
import com.wuren.datacenter.List.DeviceList;
import com.wuren.datacenter.bean.DeviceInfoBean;
import com.wuren.datacenter.devicehandler.ShiJieCameraReceiver;
import com.wuren.datacenter.util.ConstUtils;
import com.wuren.datacenter.util.DbDeviceState;
import com.wuren.datacenter.util.FebeeAPI;
import com.wuren.datacenter.util.ShiJieUtils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;

public class PushYingyanReceiver extends BroadcastReceiver{

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
			Bundle bundle = intent.getExtras();
		  	Log.d("jiaojc", "onReceive() action=" + bundle.getInt("action"));
		  	
   		    switch (bundle.getInt(PushConsts.CMD_ACTION)) {
			   case PushConsts.GET_MSG_DATA:
				    // payload
				    byte[] payload = bundle.getByteArray("payload");
				    if (payload != null)
				    {
				    
				    	String data = new String(payload);
					     
					     String decode_data=new String(Base64.decode(payload, 0, payload.length, Base64.DEFAULT));
					     Log.d("jiaojc", "Got Payload base64:" + data);
					     Log.d("jiaojc", "Got Payload decode:" + decode_data);
					     
					     JSONObject Obj = JSON.parseObject(decode_data);
					     String actionType= Obj.getString("actionType");
					     
					     Log.d("jiaojc", "actionType:"+actionType );
					     if(actionType.equals("camera_screenshot"))
					     {   
					    	 
					    	 JSONObject camerInfoObj = Obj.getJSONObject("cameraInfo");
					    	 if(camerInfoObj!=null)
					    	 {
					    		 String cameraSN=camerInfoObj.getString("cameraSN");
					    		 String user=camerInfoObj.getString("from");					    		 					    		 
					    		 Intent it=new Intent(ShiJieCameraReceiver.CaptureImageAction);
					    		 it.putExtra("cameraSN", cameraSN);
					    		 it.putExtra("user", user);
								 context.sendBroadcast(it);								 
					    	 }
					     }
					     else if(actionType.equals("bind_camera"))
					     {
					    	 JSONObject camerInfoObj = Obj.getJSONObject("bindInfo");
					    	 if(camerInfoObj!=null)
					    	 {
					    		 String cameraSN=camerInfoObj.getString("cameraSN");
					    		 
					    		 String device_ieee=camerInfoObj.getString("deviceSN");
					    		 
					    		 DeviceBindCameraList.put(device_ieee, cameraSN);								 
					    	 }
					     }
					     else if(actionType.equals("unbind_camera"))
					     {
					    	 JSONObject camerInfoObj = Obj.getJSONObject("bindInfo");
					    	 if(camerInfoObj!=null)
					    	 {
					    		 
					    		 String device_ieee=camerInfoObj.getString("deviceSN");
					    		 
					    		 DeviceBindCameraList.remove(device_ieee);								 
					    	 }
					     }
					     
					     else if(actionType.equals("open_device"))
					     {
					    	 JSONArray items = Obj.getJSONArray("deviceSN");
					    	 
					    	 
					    	 if (items != null )
					    	 {
					    		 for(int i=0;i<items.size();i++)
					    		 {
					    			 
					    			 DbDeviceState.openDevice(items.getString(i));
					    			 
					    		 }

					    	 }
					     }
					     else if(actionType.equals("close_device"))
					     {
					    	 JSONArray items = Obj.getJSONArray("deviceSN");
					    	 if (items != null )
					    	 {
					    		 for(int i=0;i<items.size();i++)
					    		 {
					    			 DbDeviceState.closeDevice(items.getString(i));
					    		 }

					    	 }
	
					     }
					     
							
							
					     
				     //将data进行JSON解析
				     //1 消息类型 2参数值
				     
//				     //ShiJieUtils.Capture("192.168.1.35", "10080", ConstUtils.G_IMAGE_PATH+File.separator+"image.jpg");
//				     Intent it=new Intent(ShiJieCameraReceiver.CaptureImageAction);
//				     context.sendBroadcast(it);
				     // TODO:payload
				    }
				    break;
			   case PushConsts.GET_CLIENTID:
				   
				   break;
			   case PushConsts.GET_SDKONLINESTATE:
				   break;
			   case PushConsts.GET_SDKSERVICEPID:
				   break;
			   default:
			    break;
			  }
		 }
}

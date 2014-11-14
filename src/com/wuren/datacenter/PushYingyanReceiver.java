package com.wuren.datacenter;

import com.igexin.sdk.PushConsts;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
				     Log.d("jiaojc", "Got Payload:" + data);
				     
				     //将data进行JSON解析
				     //1 消息类型 2参数值
				     
				     
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

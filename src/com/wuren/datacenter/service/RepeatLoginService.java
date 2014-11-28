package com.wuren.datacenter.service;

import android.os.SystemClock;
import com.wuren.datacenter.util.Log;

import com.wuren.datacenter.List.CameraList;
import com.wuren.datacenter.List.DeviceBindCameraList;
import com.wuren.datacenter.List.DeviceClassList;
import com.wuren.datacenter.util.GlobalContext;
import com.wuren.datacenter.util.HttpUtils;

public class RepeatLoginService  implements Runnable{

	private static final int REPEAT_LOGIN_TIME=10*60*1000;
	private static final String TAG="RepeatLoginService";
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		
		
		SystemClock.sleep(REPEAT_LOGIN_TIME);
		
		Log.v(TAG,"begin repeat login");
		Thread thread=new Thread(new LoginServerRequest());
		thread.start();
		
		SystemClock.sleep(REPEAT_LOGIN_TIME);
		
		if(thread!=null)
		{
			thread.interrupt();
		}
	}
	
	
	 private class LoginServerRequest implements Runnable
	 {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			HttpUtils.login(GlobalContext.S_SMARTCENTER_SN, GlobalContext.S_PASS, new HttpUtils.HttpResponseListener() {
				
				
				@Override
				public void onStart() {
				}
				
				@Override
				public void onDone(boolean succ, String result) {
					
					
					if(succ)
					{
								
						Log.v(TAG,"repeat login success");
						
						HttpUtils.getDeviceClass(null);
												
						HttpUtils.getCameraList(null);
												
						HttpUtils.getBindCameraList(null);
					}
				}
				
			});
		}
		 
	 }

}



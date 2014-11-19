package com.wuren.datacenter.util;

import com.igexin.sdk.PushManager;

import android.app.Application;
import android.app.ProgressDialog;

public class GlobalContext extends Application {

	private static GlobalContext S_INSTANCE = null;

	private static String S_PASS = "888888";
	
	public static boolean S_LOGINED = false;
	public static String S_LOGIN_SESSION = "";
	
	public static String S_SMARTCENTER_SN;
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		S_INSTANCE = this;
		
		PushManager.getInstance().initialize(this.getApplicationContext());
		
		//final String devId = CommonUtils.getDeviceId(this);
		S_SMARTCENTER_SN = "351792055028994";
		HttpUtils.init(S_SMARTCENTER_SN, new HttpUtils.HttpResponseListener() {
			
			@Override
			public void onStart() {
			}

			@Override
			public void onDone(boolean succ, String result) {
				if (succ)
				{
					
					HttpUtils.login(S_SMARTCENTER_SN, S_PASS, new HttpUtils.HttpResponseListener() {
						
						@Override
						public void onStart() {
						}
						
						@Override
						public void onDone(boolean succ, String result) {
							
							if(succ)
							{
								//start seart Camera thread.
								
								//Initialize DeviceTypeList
								HttpUtils.getDeviceTypes(null);
								
								HttpUtils.getCameraList(null);
								
								HttpUtils.getBindCameraList(null);
							}
						}
						
					});
				}
			}
			
		});
	}
	
	public String getPushClientId()
	{
		return PushManager.getInstance().getClientid(this);		
	}
	
	public static GlobalContext getInstance()
	{
		return S_INSTANCE;
	}
	
	public static boolean isLogined()
	{
		return S_LOGINED;
	}

	private static ProgressDialog m_WaitingDlg;
	private static void showWaitingDialog(String msg)
	{
		if (m_WaitingDlg == null)
		{
			m_WaitingDlg = new ProgressDialog(S_INSTANCE);
		}
		m_WaitingDlg.setMessage(msg);
		m_WaitingDlg.show();
	}
	
	private static void hideWaitingDialog()
	{
		if (m_WaitingDlg != null)
		{
			m_WaitingDlg.dismiss();
		}		
	}
	
}

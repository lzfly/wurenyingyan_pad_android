package com.wuren.datacenter.util;

import android.app.Application;
import android.app.ProgressDialog;

public class GlobalContext extends Application {

	private static GlobalContext S_INSTANCE = null;

	private static String S_PASS = "888888";
	
	public static boolean S_LOGINED = false;
	public static String S_LOGIN_SESSION = "";
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		S_INSTANCE = this;
		
		final String devId = CommonUtils.getDeviceId(this);
		HttpUtils.init(devId, new HttpUtils.HttpResponseListener() {
			
			@Override
			public void onStart() {
			}
			
			@Override
			public void onDone(boolean succ, String result) {
				if (succ)
				{
					HttpUtils.login(devId, S_PASS, new HttpUtils.HttpResponseListener() {
						
						@Override
						public void onStart() {
						}
						
						@Override
						public void onDone(boolean succ, String result) {
						}
						
					});
				}
			}
			
		});
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

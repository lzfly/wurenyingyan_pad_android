package com.wuren.datacenter.util;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.wuren.datacenter.bean.DeviceInfoBean;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

public class HttpUtils {
	
	public interface HttpResponseListener
	{
		void onStart();
		void onDone(boolean succ, String result);
	}
	
	private static int S_SUCC_CODE = 10000;
	
	private static FinalHttp getFinalHttp()
	{
		FinalHttp fh = new FinalHttp();
		fh.configRequestExecutionRetryCount(ConstUtils.S_HTTP_REQUEST_RETRY_COUNT);
		fh.configTimeout(ConstUtils.S_HTTP_REQUEST_TIMEOUT);
		return fh;
	}
	
	public static void init(String sn, final HttpResponseListener callback)
	{
		FinalHttp fh = getFinalHttp();
		
		AjaxParams params = new AjaxParams();
		params.put("sn", sn);
		
		fh.post(ConstUtils.S_INIT_URL, params, new AjaxCallBack<String>() {

			@Override
			public void onStart() {
				super.onStart();
				
				if (callback != null)
				{
					callback.onStart();
				}
			}

			@Override
			public void onFailure(Throwable t, int errorNo, String strMsg) {
				super.onFailure(t, errorNo, strMsg);
				
				if (callback != null)
				{
					callback.onDone(false, null);
				}
			}

			@Override
			public void onSuccess(String t) {
				super.onSuccess(t);
				
				boolean succ = false;
				try
				{
					JSONObject loginObj = JSON.parseObject(t);
					int code = loginObj.getIntValue("code");
					if (code == S_SUCC_CODE)
					{
						succ = true;
					}
				}
				catch (Exception exp)
				{
				}
				
				if (callback != null)
				{
					callback.onDone(succ, null);
				}
			}
			
		});
	}
	
	//登录服务器
	public static void login(String name, String pass, final HttpResponseListener callback)
	{
		FinalHttp fh = getFinalHttp();

		AjaxParams params = new AjaxParams();
		params.put("sn", name);
		params.put("pass", pass);
		
		fh.post(ConstUtils.S_LOGIN_URL, params, new AjaxCallBack<String>() {

			@Override
			public void onStart() {
				super.onStart();
				
				if (callback != null)
				{
					callback.onStart();
				}
			}

			@Override
			public void onFailure(Throwable t, int errorNo, String strMsg) {
				super.onFailure(t, errorNo, strMsg);
				GlobalContext.S_LOGINED = false;
				
				if (callback != null)
				{
					callback.onDone(false, null);
				}
			}

			@Override
			public void onSuccess(String t) {
				super.onSuccess(t);
				
				try
				{
					JSONObject loginObj = JSON.parseObject(t);
					int code = loginObj.getIntValue("code");
					if (code == S_SUCC_CODE)
					{
						JSONObject resultObj = loginObj.getJSONObject("result");
						if (resultObj != null)
						{
							GlobalContext.S_LOGIN_SESSION = resultObj.getString("Session");
							GlobalContext.S_LOGINED = true;
						}
					}
				}
				catch (Exception exp)
				{
					GlobalContext.S_LOGINED = false;
				}
				
				if (callback != null)
				{
					callback.onDone(GlobalContext.S_LOGINED, null);
				}
			}

		});
		
	}

	public static void getDeviceTypes(final HttpResponseListener callback)
	{
		FinalHttp fh = getFinalHttp();

		fh.post("", new AjaxCallBack<String>() {

			@Override
			public void onStart() {
				super.onStart();
				
				if (callback != null)
				{
					callback.onStart();
				}
			}

			@Override
			public void onFailure(Throwable t, int errorNo, String strMsg) {
				super.onFailure(t, errorNo, strMsg);
				
				if (callback != null)
				{
					callback.onDone(false, null);
				}
			}

			@Override
			public void onSuccess(String t) {
				super.onSuccess(t);

				boolean succ = false;
				try
				{
					JSONObject loginObj = JSON.parseObject(t);
					int code = loginObj.getIntValue("code");
					if (code == S_SUCC_CODE)
					{
						//TODO 解析数据
//						S_DEVICE_TYPES.put("", value)

						succ = true;
					}
				}
				catch (Exception exp)
				{
				}
				
				if (callback != null)
				{
					callback.onDone(succ, null);
				}
			}
			
		});
	}
	
	//同步设备信息
	public static void syncDevice(DeviceInfoBean device, boolean isOpen, boolean isOnline, final HttpResponseListener callback)
	{
		String url = ConstUtils.S_SYNC_DEVICE_URL + "?sid=" + GlobalContext.S_LOGIN_SESSION;
		
		FinalHttp fh = getFinalHttp();
		
		String devName = device.getName();
		if (devName == null || TextUtils.isEmpty(devName))
		{
			devName = device.getIEEE_string_format();
		}
		
		AjaxParams params = new AjaxParams();
		params.put("device_sn", device.getIEEE_string_format());
		params.put("type_code", device.getDeviceType());
		params.put("name", devName);
		params.put("is_open", isOpen ? "1" : "0");
		params.put("is_online", isOnline ? "1" : "0");
		
		fh.post(url, params, new AjaxCallBack<String>() {

			@Override
			public void onStart() {
				super.onStart();
				
				if (callback != null)
				{
					callback.onStart();
				}
			}

			@Override
			public void onFailure(Throwable t, int errorNo, String strMsg) {
				super.onFailure(t, errorNo, strMsg);
				
				if (callback != null)
				{
					callback.onDone(false, null);
				}
			}

			@Override
			public void onSuccess(String t) {
				super.onSuccess(t);
				
				boolean succ = false;
				try
				{
					JSONObject loginObj = JSON.parseObject(t);
					int code = loginObj.getIntValue("code");
					if (code == S_SUCC_CODE)
					{
						succ = true;
					}
				}
				catch (Exception exp)
				{
				}
				
				if (callback != null)
				{
					callback.onDone(succ, null);
				}
			}
			
		});
	}
	
	//提交设备实时数据（报警，温/湿度，颜色，亮度等等）
	public static void postDeviceData(final HttpResponseListener callback)
	{
		FinalHttp fh = getFinalHttp();
		
		AjaxParams params = new AjaxParams();
		params.put("device_sn", "");
		//TODO 其他数据		
		
		fh.post(ConstUtils.S_POST_DATA_URL,  params, new AjaxCallBack<String>() {

			@Override
			public void onStart() {
				super.onStart();
				
				if (callback != null)
				{
					callback.onStart();
				}
			}

			@Override
			public void onFailure(Throwable t, int errorNo, String strMsg) {
				super.onFailure(t, errorNo, strMsg);
				
				if (callback != null)
				{
					callback.onDone(false, null);
				}
			}

			@Override
			public void onSuccess(String t) {
				super.onSuccess(t);
				
				boolean succ = false;
				try
				{
					JSONObject loginObj = JSON.parseObject(t);
					int code = loginObj.getIntValue("code");
					if (code == S_SUCC_CODE)
					{
						succ = true;
					}
				}
				catch (Exception exp)
				{
				}
				
				if (callback != null)
				{
					callback.onDone(succ, null);
				}
			}
			
		});
	}
	
}

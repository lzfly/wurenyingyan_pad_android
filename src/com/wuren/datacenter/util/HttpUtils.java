package com.wuren.datacenter.util;

import java.io.File;
import java.io.IOException;
import java.util.Hashtable;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.wuren.datacenter.List.CameraList;
import com.wuren.datacenter.List.DeviceBindCameraList;
import com.wuren.datacenter.List.DeviceClassList;
import com.wuren.datacenter.bean.CameraInfoBean;
import com.wuren.datacenter.bean.DeviceInfoBean;
import com.wuren.datacenter.bean.DeviceClassBean;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

public class HttpUtils {
	
	
	private final static String TAG="HttpUtils";
	
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
		//params.put("push_clientid", PushManager.getInstance().getClientid(GlobalContext.getInstance()));
		params.put("push_clientid", GlobalContext.getInstance().getPushClientId());
		
		Log.v("jiaojc","push_clientid:"+GlobalContext.getInstance().getPushClientId());
		
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
	
	public static void getBindCameraList(final HttpResponseListener callback)
	{
		String url = ConstUtils.S_GET_BIND_CAMERA_LIST_URL + "?sid=" + GlobalContext.S_LOGIN_SESSION;
		
		FinalHttp fh = getFinalHttp();
		fh.post(url, new AjaxCallBack<String>() {

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
					JSONObject respObj = JSON.parseObject(t);
					int code = respObj.getIntValue("code");
					if (code == S_SUCC_CODE)
					{
						JSONObject resultObj = respObj.getJSONObject("result");
						if (resultObj != null)
						{
							JSONArray arrDevBindCamera = resultObj.getJSONArray("deviceBindCamera.list");
							if (arrDevBindCamera != null && arrDevBindCamera.size() > 0)
							{
								for (int i = 0; i < arrDevBindCamera.size(); i++)
								{
									JSONObject devBindCameraObj = arrDevBindCamera.getJSONObject(i);
									if (devBindCameraObj != null)
									{
										DeviceBindCameraList.put(devBindCameraObj.getString("DEVICE_SN"),
												devBindCameraObj.getString("CAMERA_SN"));
									}
								}
								
								succ = true;
							}
						}
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
	
	public static void getBindCamera(final String deviceSN, final HttpResponseListener callback)
	{
		if (DeviceBindCameraList.exists(deviceSN))
		{
			if (callback != null)
			{
				callback.onDone(true, null);
			}
		}
		else
		{
			String url = ConstUtils.S_GET_BIND_CAMERA_URL + "?sid=" + GlobalContext.S_LOGIN_SESSION;
			
			FinalHttp fh = getFinalHttp();
			
			AjaxParams params = new AjaxParams();
			params.put("device_sn", deviceSN);
			
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
						JSONObject respObj = JSON.parseObject(t);
						int code = respObj.getIntValue("code");
						if (code == S_SUCC_CODE)
						{
							JSONObject resultObj = respObj.getJSONObject("result");
							if (resultObj != null)
							{
								JSONObject devBindCameraObj = resultObj.getJSONObject("deviceBindCamera");
								if (devBindCameraObj != null)
								{
									String devSN = devBindCameraObj.getString("DEVICE_SN");
									if (deviceSN.equals(devSN))
									{
										String cameraSN = devBindCameraObj.getString("CAMERA_SN");
										if (CameraList.exists(cameraSN))
										{
											DeviceBindCameraList.put(devSN, cameraSN);
											succ = true;
										}
									}
								}
							}
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
	
	public static void getDeviceClass(final HttpResponseListener callback)
	{
		String url = ConstUtils.S_GET_DEVICE_TYPE_URL + "?sid=" + GlobalContext.getInstance().S_LOGIN_SESSION;
		
		FinalHttp fh = getFinalHttp();
		fh.get(url, new AjaxCallBack<String>() {

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
					JSONObject respObj = JSON.parseObject(t);
					int code = respObj.getIntValue("code");
					if (code == S_SUCC_CODE)
					{
						JSONObject resultObj = respObj.getJSONObject("result");
						if (resultObj != null)
						{
							JSONArray arrDevTypes = resultObj.getJSONArray("deviceType.list");
							if (arrDevTypes != null && arrDevTypes.size() > 0)
							{
								for (int i = 0; i < arrDevTypes.size(); i++)
								{
									JSONObject devTypeObj = arrDevTypes.getJSONObject(i);
									if (devTypeObj != null)
									{
										DeviceClassBean devType = new DeviceClassBean();
										devType.setCode(devTypeObj.getString("CODE"));
										devType.setName(devTypeObj.getString("NAME"));
										devType.setIcon(devTypeObj.getString("ICON"));
										devType.setType(devTypeObj.getString("TYPE"));
										DeviceClassList.put(devType);
									}
								}
							}
							
							succ = true;
						}
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
	
	//获得摄像头列表
	public static void getCameraList(final HttpResponseListener callback)
	{
		String url = ConstUtils.S_GET_CAMERS_URL + "?sid=" + GlobalContext.getInstance().S_LOGIN_SESSION;
		
		FinalHttp fh = getFinalHttp();
		fh.get(url, new AjaxCallBack<String>() {

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
					JSONObject respObj = JSON.parseObject(t);
					int code = respObj.getIntValue("code");
					Log.v(TAG,"getCameraList response code:"+code);
					if (code == S_SUCC_CODE)
					{
						JSONObject resultObj = respObj.getJSONObject("result");
						if (resultObj != null)
						{
							JSONArray arrCameras = resultObj.getJSONArray("camera.list");
							if (arrCameras != null && arrCameras.size() > 0)
							{
								for (int i = 0; i < arrCameras.size(); i++)
								{
									JSONObject cameraObj = arrCameras.getJSONObject(i);
									if (cameraObj != null)
									{
										CameraInfoBean camera = new CameraInfoBean();
										camera.setIP(cameraObj.getString("IP"));
										camera.setModel(cameraObj.getString("MODEL"));
										camera.setSmartcenter_sn(cameraObj.getString("SMARTCENTER_SN"));
										camera.setPort(cameraObj.getString("PORT"));
										camera.setSn(cameraObj.getString("SN"));
										camera.setName(cameraObj.getString("NAME"));
									
										CameraList.put(camera);
									}
								}
							}
							
							succ = true;
						}
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
	
	//设备上线
	public static void deviceOnline(DeviceInfoBean device, final HttpResponseListener callback)
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
		params.put("is_online", "1");
		
				
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
	
	//设备下线
	public static void deviceOffline(DeviceInfoBean device, final HttpResponseListener callback)
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
		Log.v("jiaojc","deviceOffline---device_sn:"+device.getIEEE_string_format());
		params.put("is_online", "0");
		
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
	

	
	public static String uploadPicture(String camera_sn,String user,String image_path,final HttpResponseListener callback)
	{
		String url = ConstUtils.S_UPLOAD_PICTURE_URL + "?sid=" + GlobalContext.S_LOGIN_SESSION ;
		
		String afterurl="&camera_sn="+camera_sn+"&to_user="+user;
		
		url+=afterurl;
		
		
		
		// set client timeout
		HttpParams httpParams = new BasicHttpParams();
		int timeoutConnection = 10000;
		int timeoutSocket = 10000;
		HttpConnectionParams.setConnectionTimeout(httpParams, timeoutConnection);
		HttpConnectionParams.setSoTimeout(httpParams, timeoutSocket);
		// init client
		HttpClient httpClient = new DefaultHttpClient(httpParams);
		
		
		HttpPost httpPost = new HttpPost(url);
		
		
		File fileTemp=new File(image_path);
		FileBody bin = new FileBody(fileTemp);
		MultipartEntity reqEntity = new MultipartEntity();
		reqEntity.addPart("upload", bin);// upload为请求后台的File upload属性
		httpPost .setEntity(reqEntity);
		
		HttpResponse httpResponse;
		try {
			httpResponse = httpClient.execute(httpPost);
			
			Log.w("jiaojc", "upload--result--before:"+httpResponse.getStatusLine().getStatusCode());
			
			if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) 
			{
				
				String httpResult = EntityUtils.toString(httpResponse.getEntity());
				Log.w("jiaojc", "upload--result:"+httpResult);
				return httpResult;
			} else {
				Log.w("jiaojc", "upload--result:"+httpResponse.getStatusLine().getStatusCode());
				return null;
			}
			
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;						
	}
		

	//上传zip file
	public static String uploadZipFile(String zip_path,final HttpResponseListener callback)
	{
		String url = ConstUtils.S_UPLOAD_PICTURE_URL + "?sid=" + GlobalContext.S_LOGIN_SESSION ;
		
		
		
		
		// set client timeout
		HttpParams httpParams = new BasicHttpParams();
		int timeoutConnection = 10000;
		int timeoutSocket = 10000;
		HttpConnectionParams.setConnectionTimeout(httpParams, timeoutConnection);
		HttpConnectionParams.setSoTimeout(httpParams, timeoutSocket);
		// init client
		HttpClient httpClient = new DefaultHttpClient(httpParams);
		
		
		HttpPost httpPost = new HttpPost(url);
		
		
		File fileTemp=new File(zip_path);
		FileBody bin = new FileBody(fileTemp);
		MultipartEntity reqEntity = new MultipartEntity();
		reqEntity.addPart("upload", bin);// upload为请求后台的File upload属性
		httpPost .setEntity(reqEntity);
		
		HttpResponse httpResponse;
		try {
			httpResponse = httpClient.execute(httpPost);
			
			Log.w("jiaojc", "zip upload--result--before:"+httpResponse.getStatusLine().getStatusCode());
			
			if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) 
			{				
				String httpResult = EntityUtils.toString(httpResponse.getEntity());
				Log.w("jiaojc", "zip upload--result:"+httpResult);
				return httpResult;
			} else {
				Log.w("jiaojc", "zip upload--result:"+httpResponse.getStatusLine().getStatusCode());
				return null;
			}
			
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;						
	}
	
	

	
	//提交设备实时数据（报警，温/湿度，颜色，亮度等等）
	public static void postDeviceData(String devSN, String msg, String type,String zip_name, final HttpResponseListener callback)
	{
		Log.v("jiaojc","into method postDeviceData");
		String url = ConstUtils.S_POST_DATA_URL + "?sid=" + GlobalContext.S_LOGIN_SESSION;
		
		FinalHttp fh = getFinalHttp();
		
		AjaxParams params = new AjaxParams();
		params.put("device_sn", devSN);
		params.put("message", msg);
		params.put("type", type);
		params.put("package_file", zip_name+".zip");
		
		fh.post(url,  params, new AjaxCallBack<String>() {

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

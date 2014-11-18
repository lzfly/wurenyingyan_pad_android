package com.wuren.datacenter.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.igexin.sdk.PushManager;
import com.wuren.datacenter.List.CameraList;
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
	
	
	public static void getDeviceTypes(final HttpResponseListener callback)
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
	
//	{
//		Log.v("jiaojc","into appclient upload methods");
//		try {
//			HttpPost httpPost = headerFilter(new HttpPost(this.apiUrl));
//			List<NameValuePair> postParams = new ArrayList<NameValuePair>();
//			// get post parameters
//			Iterator it = urlParams.entrySet().iterator();
//			while (it.hasNext()) {
//				Map.Entry entry = (Map.Entry) it.next();
//				Log.v("jiaojc","key:"+entry.getKey().toString()+"\tvalue:"+entry.getValue().toString());
//				
//				postParams.add(new BasicNameValuePair(entry.getKey().toString(), entry.getValue().toString()));
//			}
//			
//			String image_path=urlParams.get("zip_path").toString();
//	
//			//String ss=Environment.getExternalStorageDirectory()+"/"+"3.jpg";
//			File fileTemp=new File(image_path);
//			FileBody bin = new FileBody(fileTemp);
//			MultipartEntity reqEntity = new MultipartEntity();
//			reqEntity.addPart("upload", bin);// upload为请求后台的File upload属性
//
//			httpPost.setEntity(reqEntity);
//			
//			Log.w("AppClient.post.url", this.apiUrl);
//			HttpResponse httpResponse = httpClient.execute(httpPost);
//			if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
//				String httpResult = resultFilter(httpResponse.getEntity());
//				Log.w("AppClient.post.result", httpResult);
//				return httpResult;
//			} else {
//				return null;
//			}
//		} catch (ConnectTimeoutException e) {
//			throw new Exception(C.err.network);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return null;
//	}
	
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
		
		List<NameValuePair> postParams = new ArrayList<NameValuePair>();
		postParams.add(new BasicNameValuePair("camera_sn",camera_sn));
		postParams.add(new BasicNameValuePair("to_user",user));
		
		
		
//		try {
//			httpPost.setEntity(new UrlEncodedFormEntity(postParams, HTTP.UTF_8));
//		} catch (UnsupportedEncodingException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
		
		
		
		File fileTemp=new File(image_path);
		FileBody bin = new FileBody(fileTemp);
		MultipartEntity reqEntity = new MultipartEntity();
		reqEntity.addPart("upload", bin);// upload为请求后台的File upload属性
	//	reqEntity.addPart(postParams);
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
		
	//上传图片
//	public static void uploadPicture(String camera_sn,String user,String image_path,final HttpResponseListener callback)
//	{
//		String url = ConstUtils.S_UPLOAD_PICTURE_URL + "?sid=" + GlobalContext.S_LOGIN_SESSION;
//		
//		FinalHttp fh = getFinalHttp();
//		
//		
//		AjaxParams params = new AjaxParams();
//		params.put("camera_sn", camera_sn);
//		params.put("to_user", user);
//		
//		File fileTemp=new File(image_path);
//		
////		FileBody bin = new FileBody(fileTemp);		
////		MultipartEntity reqEntity = new MultipartEntity();
////		reqEntity.addPart("upload", bin);// upload为请求后台的File upload属性
////		
//		try {
//			params.put("file", fileTemp);
//		} catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
//		fh.post(url, params, new AjaxCallBack<String>() {
//
//			@Override
//			public void onStart() {
//				super.onStart();
//				
//				Log.v("jiaojc","uploadPicture---onStart");
//				if (callback != null)
//				{
//					
//					callback.onStart();
//				}
//			}
//
//			@Override
//			public void onFailure(Throwable t, int errorNo, String strMsg) {
//				super.onFailure(t, errorNo, strMsg);
//				Log.v("jiaojc","uploadPicture---onFailure");
//				if (callback != null)
//				{
//					callback.onDone(false, null);
//				}
//			}
//
//			@Override
//			public void onSuccess(String t) {
//				super.onSuccess(t);
//				
//				boolean succ = false;
//				try
//				{
//					JSONObject loginObj = JSON.parseObject(t);
//					int code = loginObj.getIntValue("code");
//					if (code == S_SUCC_CODE)
//					{
//						Log.v("jiaojc","uploadPicture---S_SUCC_CODE");
//						succ = true;
//					}
//				}
//				catch (Exception exp)
//				{
//				}
//				
//				if (callback != null)
//				{
//					callback.onDone(succ, null);
//				}
//			}
//			
//		});
//	}

	
	//提交设备实时数据（报警，温/湿度，颜色，亮度等等）
	public static void postDeviceData(String devSN, String msg, String type, final HttpResponseListener callback)
	{
		Log.v("jiaojc","into method postDeviceData");
		String url = ConstUtils.S_POST_DATA_URL + "?sid=" + GlobalContext.S_LOGIN_SESSION;
		
		FinalHttp fh = getFinalHttp();
		
		AjaxParams params = new AjaxParams();
		params.put("device_sn", devSN);
		params.put("message", msg);
		params.put("type", type);
		
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

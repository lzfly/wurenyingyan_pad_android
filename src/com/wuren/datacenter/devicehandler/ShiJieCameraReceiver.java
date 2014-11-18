package com.wuren.datacenter.devicehandler;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;


import com.wuren.datacenter.List.CameraList;
import com.wuren.datacenter.bean.CameraInfoBean;
import com.wuren.datacenter.util.ConstUtils;
import com.wuren.datacenter.util.GlobalContext;
import com.wuren.datacenter.util.HttpUtils;
import com.wuren.datacenter.util.IOUtils;
import com.wuren.datacenter.util.ShiJieUtils;
import com.wuren.datacenter.util.ToastUtils;
import com.wuren.datacenter.util.ZipUtil;



import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;

public class ShiJieCameraReceiver extends BroadcastReceiver {

	public static String CaptureImageAction="com.5ren.qianliyan.SHIJIE_CAPTURE_IMAGE";
	public static String CaptureZIPAction="com.5ren.qianliyan.SHIJIE_CAPTURE_ZIP";
	private final static String TAG="ShiJieCameraReceiver";
	
	private String mZipPicturePath;
	private String mPicturePathTemp;
	
	
	private String mCaptureImagePath;
	public static boolean isCaptureFinish=false;
	@Override
	public void onReceive(Context context, Intent intent) {
		
		Log.v(TAG,"Receive Action:"+intent.getAction());
		if(intent.getAction().equals(CaptureImageAction))
		{
			
			String cameraSN=intent.getStringExtra("cameraSN");
			String user=intent.getStringExtra("user");
			
			Log.v(TAG,"requeset camera SN:"+cameraSN);
			CameraInfoBean camera=CameraList.getCamera(cameraSN);
			if(camera!=null)
			{				
				
				SimpleDateFormat simpleDateFormat= new SimpleDateFormat("yyyyMMddHHmmss");
				String strOcurDateForFile=simpleDateFormat.format(new Date());
				
				String imageName=user+"_"+cameraSN+"_"+strOcurDateForFile+".jpg";
				
				String path=ConstUtils.G_IMAGE_PATH+File.separator+imageName;

				new Thread(new CaptureImageRequest(camera,user,path)).start(); 
				
			}
			else
			{
				Log.v(TAG,"Camera is not exist!");
			}
		}
		if(intent.getAction().equals(CaptureZIPAction))
		{

			//String sensorAddr = intent.getStringExtra("sensor_addr");
			//if (HttpUtils.S_BIND_CAMERA.containsKey(sensorAddr))
//			{
				//String cameraAddr = HttpUtils.S_BIND_CAMERA.get(sensorAddr);
//				String cameraAddr ="192.168.1.35";
				//SensorInfo camera = ActiveSensorList.getSensor(cameraAddr);
//				if (camera != null)
//				{
//					File imagePathRoot=new File(ConstUtils.G_IMAGE_PATH);
//					if(!imagePathRoot.exists())
//					{
//						imagePathRoot.mkdir();
//					}
//					
//					String image_name= intent.getStringExtra("image_name");
//					
//					if(image_name!=null && image_name.length()!=0)
//					{
//						mPicturePathTemp=ConstUtils.G_IMAGE_PATH+File.separator+image_name+"_temp";
//						mZipPicturePath=ConstUtils.G_IMAGE_PATH+File.separator+image_name+".zip";
//						
//						File file=new File(mPicturePathTemp);
//						if(!file.exists())
//						{
//							file.mkdir();
//						}
//						
//						//cature(camera.getIP(), camera.getPort());
//						//cature("192.168.1.35", "10080");
//						ShiJieUtils.Capture("192.168.1.35", "10080", ConstUtils.G_IMAGE_PATH+File.separator+"image.jpg");
//					}
//					else
//						Log.v(TAG,"Failed to get file Name.");
//				}
//				else
//				{
//					ToastUtils.show(GlobalContext.getInstance(), "截图失败！");
//				}
//			}
		}
		
	}
	
	private class CaptureImageRequest implements Runnable
	{
		
		private CameraInfoBean mCamera;
		private String mUser;
		private String mPath;
		public CaptureImageRequest(CameraInfoBean camera,String user,String path)
		{
			
			this.mCamera=camera;
			this.mUser=user;
			this.mPath=path;
		}
		@Override
		public void run() {
			// TODO Auto-generated method stub
			if(ShiJieUtils.Capture(mCamera.getIP(),mCamera.getPort(), this.mPath))
			{
				HttpUtils.uploadPicture(mCamera.getSn(), mUser, mPath, null);				
			}
			else
			{
				Log.v(TAG,"capture image error!");
			}
		}
		
		
	}
	
	
	
	private void cature(CameraInfoBean camera,String user)
	{
		     isCaptureFinish=false;
		     
		     Thread t = new Thread(new Runnable() {

				@Override
				public void run() {
					
					//ShiJieUtils.Capture(camera.getIP(), "10080", ConstUtils.G_IMAGE_PATH+File.separator+"image.jpg");
//					try
//					{
//						for(int i=0;i<ConstUtils.CAPTURE_PICTURE_NUM;i++)
//						{
//							
//							ShiJieUtils.Capture(ip, port, mPicturePathTemp+File.separator+(i+1)+".jpg");
//							if(i<ConstUtils.CAPTURE_PICTURE_NUM-1)
//								SystemClock.sleep(900);
//							
//							
//						}
//						isCaptureFinish=true;
//						
//						//要生成zip压缩包，然后删除原文件，否则让图库里照片太乱。
//						
//						File zipFile=new File(mZipPicturePath);	
//						Collection<File> detailFileList=new ArrayList();
//						detailFileList=ZipUtil.getFileList(mPicturePathTemp,".jpg");
//						
//						try {
//							ZipUtil.zipFiles(detailFileList, zipFile);
//						} catch (IOException e) {
//							// TODO Auto-generated catch block
//							e.printStackTrace();							
//						}
//						
//						//删除临时文件
//						File fileTemp=new File(mPicturePathTemp);
//						if(fileTemp.exists())
//						{
//							IOUtils.delete(fileTemp);
//						}
//						
//					}
//					catch (Exception exp)
//					{
//						exp.printStackTrace();
//					}
//					finally
//					{
//						isCaptureFinish=true;
//					}
				}
		    	 
		     });
		     t.start();
		     

		
	}

}

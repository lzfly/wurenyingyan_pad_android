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
			
			Date nowDate=new Date();
			SimpleDateFormat simpleDateFormat= new SimpleDateFormat("yyyyMMddHHmmss");
			String strOcurDateForFile=simpleDateFormat.format(nowDate);
		
			
			if(camera!=null)
			{				
				
				
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
			
			
   		 
			String cameraSN=intent.getStringExtra("cameraSN");
			String zip_name=intent.getStringExtra("zip_name");
			
			
			Log.v(TAG,"requeset camera SN:"+cameraSN+"\tzip_name:"+zip_name);
			CameraInfoBean camera=CameraList.getCamera(cameraSN);
			
			if(camera!=null)
			{				
				
				
				
				
				String path_temp=ConstUtils.G_IMAGE_PATH+File.separator+zip_name+"_temp";
				String zipFullPath=ConstUtils.G_IMAGE_PATH+File.separator+zip_name+".zip";
				
				File file=new File(path_temp);
				if(!file.exists())
				{
					file.mkdir();
				}
				
				new Thread(new CaptureZipRequest(camera,path_temp,zipFullPath)).start(); 
				
				
			}
			else
			{
				Log.v(TAG,"Camera is not exist!");
			}


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
	
	
	private class CaptureZipRequest implements Runnable
	{
		
		private CameraInfoBean mCamera;
		private String mPathTemp;
		private String mZipFullPath;
		public CaptureZipRequest(CameraInfoBean camera,String path_temp,String zipFullPath)
		{
			
			this.mCamera=camera;
			this.mPathTemp=path_temp;
			this.mZipFullPath=zipFullPath;
		}
		
		@Override
		public void run() {
			
			
			try
			{
				for(int i=0;i<ConstUtils.CAPTURE_PICTURE_NUM;i++)
				{
					
					ShiJieUtils.Capture(mCamera.getIP(), mCamera.getPort(), mPathTemp+File.separator+(i+1)+".jpg");
					if(i<ConstUtils.CAPTURE_PICTURE_NUM-1)
						SystemClock.sleep(900);					
					
				}
				
				
				//要生成zip压缩包，然后删除原文件，否则让图库里照片太乱。
				
				File zipFile=new File(mZipFullPath);	
				Collection<File> detailFileList=new ArrayList();
				detailFileList=ZipUtil.getFileList(mPathTemp,".jpg");
				
				try {
					ZipUtil.zipFiles(detailFileList, zipFile);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();							
				}
				
				//删除临时文件
				File fileTemp=new File(mPathTemp);
				if(fileTemp.exists())
				{
					IOUtils.delete(fileTemp);
				}
				
				Log.v("jiaojc","begin upload zip file to server");
				//上传到服务器上
				HttpUtils.uploadZipFile(mZipFullPath,null);
				
			}
			catch (Exception exp)
			{
				exp.printStackTrace();
			}
			
		}		
		
	}
	
	
	
	

}

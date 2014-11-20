package com.wuren.datacenter.List;

import java.util.Hashtable;

import android.os.SystemClock;

import com.wuren.datacenter.bean.CameraInfoBean;
import com.wuren.datacenter.bean.DeviceClassBean;
import com.wuren.datacenter.util.HttpUtils;

public class CameraList {

	
	
	private static Hashtable<String, CameraInfoBean> S_CAMERAS = new Hashtable<String, CameraInfoBean>();
	
	private static Object S_LOCK = new Object();
	
	public static void clear()
	{
		S_CAMERAS.clear();
	}
	
	public static boolean exists(String sn)
	{
		return S_CAMERAS.containsKey(sn);
	}
	
	//如果存在，增加失败
	public static boolean add(CameraInfoBean camera)
	{
		String sn= camera.getSn();
		if (!exists(sn))
		{
			synchronized (S_LOCK)
			{
				if (!exists(sn))
				{
					S_CAMERAS.put(sn, camera);
				}
			}
		}
		return false;
	}
	
	//如果存在，覆盖已有的
	public static void put(CameraInfoBean camera)
	{
		String sn= camera.getSn();
		synchronized (S_LOCK)
		{
			S_CAMERAS.put(sn, camera);
		}
	}
	
	public static CameraInfoBean getCamera(String sn)
	{
		if(S_CAMERAS.size()==0)
		{
			HttpUtils.getDeviceClass(null);
			SystemClock.sleep(2000);
		}
		
		if (exists(sn))
		{
			return S_CAMERAS.get(sn);
		}
		return null;
	}
	
}

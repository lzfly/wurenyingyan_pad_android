package com.wuren.datacenter.List;

import java.util.Hashtable;

import com.wuren.datacenter.bean.CameraInfoBean;

public class DeviceBindCameraList {

	private static Hashtable<String, String> S_DEV_BIND_CAMERAS = new Hashtable<String, String>();
	
	private static Object S_LOCK = new Object();
	
	public static void clear()
	{
		S_DEV_BIND_CAMERAS.clear();
	}
	
	public static boolean exists(String devIEEE)
	{
		return S_DEV_BIND_CAMERAS.containsKey(devIEEE);
	}
	
	//如果存在，增加失败
	public static boolean add(String devSN, String cameraSN)
	{
		if (!exists(devSN))
		{
			synchronized (S_LOCK)
			{
				if (!exists(devSN))
				{
					S_DEV_BIND_CAMERAS.put(devSN, cameraSN);
				}
			}
		}
		return false;
	}
	
	//如果存在，覆盖已有的
	public static void put(String devSN, String cameraSN)
	{
		synchronized (S_LOCK)
		{
			S_DEV_BIND_CAMERAS.put(devSN, cameraSN);
		}
	}
	
	public static void remove(String devSN)
	{
		synchronized (S_LOCK)
		{
			S_DEV_BIND_CAMERAS.remove(devSN);
		}
	}
	
	public static CameraInfoBean getBindCamera(String devSN)
	{
		if (exists(devSN))
		{
			String cameraSN = S_DEV_BIND_CAMERAS.get(devSN);
			return CameraList.getCamera(cameraSN);
		}
		return null;
	}
	
}

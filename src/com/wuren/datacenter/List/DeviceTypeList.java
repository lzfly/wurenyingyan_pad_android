package com.wuren.datacenter.List;

import java.util.Hashtable;

import com.wuren.datacenter.bean.DeviceTypeInfo;

public class DeviceTypeList {

	private static Hashtable<String, DeviceTypeInfo> S_DEVICE_TYPES = new Hashtable<String, DeviceTypeInfo>();
	
	private static Object S_LOCK = new Object();
	
	public static void clear()
	{
		S_DEVICE_TYPES.clear();
	}
	
	public static boolean exists(String identification)
	{
		return S_DEVICE_TYPES.containsKey(identification);
	}
	
	//如果存在，增加失败
	public static boolean add(DeviceTypeInfo deviceType)
	{
		String identification = deviceType.getIdentification();
		if (!exists(identification))
		{
			synchronized (S_LOCK)
			{
				if (!exists(identification))
				{
					S_DEVICE_TYPES.put(identification, deviceType);
				}
			}
		}
		return false;
	}
	
	//如果存在，覆盖已有的
	public static void put(DeviceTypeInfo deviceType)
	{
		String identification = deviceType.getIdentification();
		synchronized (S_LOCK)
		{
			S_DEVICE_TYPES.put(identification, deviceType);
		}
	}
	
	public static DeviceTypeInfo getDeviceType(String identification)
	{
		if (exists(identification))
		{
			return S_DEVICE_TYPES.get(identification);
		}
		return null;
	}
	
}

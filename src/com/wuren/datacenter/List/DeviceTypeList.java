package com.wuren.datacenter.List;

import java.util.Hashtable;

import com.wuren.datacenter.bean.DeviceTypeInfo;

public class DeviceTypeList {

	private static Hashtable<String, DeviceTypeInfo> S_DEVICE_TYPES = new Hashtable<String, DeviceTypeInfo>();
	
	public static void clear()
	{
		S_DEVICE_TYPES.clear();
	}
	
	public static boolean exists(String identification)
	{
		return S_DEVICE_TYPES.containsKey(identification);
	}
	
	//������ڣ�����ʧ��
	public static boolean add(DeviceTypeInfo deviceType)
	{
		String identification = deviceType.getIdentification();
		if (!exists(identification))
		{
			S_DEVICE_TYPES.put(identification, deviceType);
		}
		return false;
	}
	
	//������ڣ��������е�
	public static void put(DeviceTypeInfo deviceType)
	{
		String identification = deviceType.getIdentification();
		S_DEVICE_TYPES.put(identification, deviceType);
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

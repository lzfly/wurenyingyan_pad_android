package com.wuren.datacenter.List;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import com.wuren.datacenter.bean.DeviceInfoBean;

public class DeviceList {

	private static Hashtable<String, DeviceInfoBean> S_DEVICES = new Hashtable<String, DeviceInfoBean>();
	
	public static void clear()
	{
		S_DEVICES.clear();
	}
	
	public static boolean exists(String devIEEE)
	{
		return S_DEVICES.containsKey(devIEEE);
	}
	
	//如果存在，增加失败
	public static boolean add(DeviceInfoBean device)
	{
		String ieee = device.getIEEE_string_format();
		if (!exists(ieee))
		{
			S_DEVICES.put(ieee, device);
		}
		return false;
	}
	
	//如果存在，覆盖已有的
	public static void put(DeviceInfoBean device)
	{
		String ieee = device.getIEEE_string_format();
		S_DEVICES.put(ieee, device);
	}
	
	public static DeviceInfoBean getDevice(String devIEEE)
	{
		if (exists(devIEEE))
		{
			return S_DEVICES.get(devIEEE);
		}
		return null;
	}

	public static List<DeviceInfoBean> getDeviceList()
	{
		List<DeviceInfoBean> result = new ArrayList<DeviceInfoBean>();
		result.addAll(S_DEVICES.values());
		return result;
	}

}

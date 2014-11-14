package com.wuren.datacenter.List;

import java.util.Hashtable;

import com.wuren.datacenter.bean.DeviceTypeInfo;

public class DeviceTypeList {

	public class Type{
		
		public static final int GENERAL_POWER_SWITCH=0; //普通开关
		public static final int COTROLLED_RELAY_SWITCH=0x0002;//可控继电器(开关)
		public static final int INTELLIGENCE_POWER_SWITCH=0x0009;//智能开关插座 
		public static final int DOOR_LOCK=0x0060;//门磁
		public static final int Dimmable_Light=0x0101;//调光灯
		public static final int Color_Dimmable_Light=0x0102;//彩灯
		public static final int Color_Dimmer_Switch=0x0105;//可调颜色灯，有调光，开关功能
		public static final int Light_Sensor=0x0106;//光照
		public static final int Occupancy_Sensor=0x0107;//人体红外
		public static final int Color_Temperature_1=0x0110;//色温灯
		public static final int Color_Temperature_2=0x0220;//色温灯
		public static final int Occupancy_Sensor_Remote_Control_Switch=0x0161;//红外遥控器
		public static final int Window_Covering_Device=0x202;//窗帘
		public static final int Philips_Color_Dimmable_Light=0x0210;//飞利浦彩灯
		public static final int Thermostat=0x0301;//温湿度控制器
		public static final int Thermostat_Sensor=0x0302;//温湿度控制器
		public static final int Gas_Sensor=0x0308;//可燃气体
		public static final int PM2_5=0x0309;//PM2.5
		public static final int Smoke_Sensor=0x0310;//烟雾
		public static final int Dot_Matrix_Monitor=0x0340;//点阵显示器
		public static final int Sound_Light_Alarm=0x0403;//声光报警器
		
		
		
		
		
	}
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

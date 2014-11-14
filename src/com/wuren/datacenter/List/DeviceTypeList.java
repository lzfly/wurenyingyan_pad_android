package com.wuren.datacenter.List;

import java.util.Hashtable;

import com.wuren.datacenter.bean.DeviceTypeInfo;

public class DeviceTypeList {

	public class Type{
		
		public static final int GENERAL_POWER_SWITCH=0; //��ͨ����
		public static final int COTROLLED_RELAY_SWITCH=0x0002;//�ɿؼ̵���(����)
		public static final int INTELLIGENCE_POWER_SWITCH=0x0009;//���ܿ��ز��� 
		public static final int DOOR_LOCK=0x0060;//�Ŵ�
		public static final int Dimmable_Light=0x0101;//�����
		public static final int Color_Dimmable_Light=0x0102;//�ʵ�
		public static final int Color_Dimmer_Switch=0x0105;//�ɵ���ɫ�ƣ��е��⣬���ع���
		public static final int Light_Sensor=0x0106;//����
		public static final int Occupancy_Sensor=0x0107;//�������
		public static final int Color_Temperature_1=0x0110;//ɫ�µ�
		public static final int Color_Temperature_2=0x0220;//ɫ�µ�
		public static final int Occupancy_Sensor_Remote_Control_Switch=0x0161;//����ң����
		public static final int Window_Covering_Device=0x202;//����
		public static final int Philips_Color_Dimmable_Light=0x0210;//�����ֲʵ�
		public static final int Thermostat=0x0301;//��ʪ�ȿ�����
		public static final int Thermostat_Sensor=0x0302;//��ʪ�ȿ�����
		public static final int Gas_Sensor=0x0308;//��ȼ����
		public static final int PM2_5=0x0309;//PM2.5
		public static final int Smoke_Sensor=0x0310;//����
		public static final int Dot_Matrix_Monitor=0x0340;//������ʾ��
		public static final int Sound_Light_Alarm=0x0403;//���ⱨ����
		
		
		
		
		
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
	
	//������ڣ�����ʧ��
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
	
	//������ڣ��������е�
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

package com.wuren.datacenter.devicehandler;

import com.wuren.datacenter.bean.DeviceInfoBean;
import com.wuren.datacenter.util.HttpUtils;

public class InvadeDeviceHandler {

	public static void alarm(DeviceInfoBean device, byte[] data)
	{
		if (data != null && data.length > 0)
		{
			if (data[data.length - 1] == 1)
			{
				//���� �� ����
//				
//				HttpUtils.postDeviceData(device.getIEEE_string_format(), "���ⱨ����", 
//						"WARN", null);
//				
//				Log.d("wxm", "���ⱨ����");
			}
		}
	}

}

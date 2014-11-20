package com.wuren.datacenter.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import com.wuren.datacenter.List.DeviceList;
import com.wuren.datacenter.bean.DeviceInfoBean;


import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;

public class DbDeviceState {
	
	private static final SQLiteDatabase S_DB = DbHelper.getDB("wurenyingyan.db");
	
	private static final String S_TABLE_DEVICE_STATE="device_state";
	
	public static void checkDeviceStateTable()
	{
		if (!DbHelper.hasTable(S_DB, S_TABLE_DEVICE_STATE))
		{
			
			DbHelper.createTable(S_DB, S_TABLE_DEVICE_STATE,
					"device_ieee VARCHAR(32) PRIMARY KEY",
					"State Integer"	// 0¹Ø	1¿ª  2Í£
					);
						
		}
		
	}
	
	private static boolean updateDeviceState(String device_ieee, int state)
	{
		DeviceInfoBean device=DeviceList.getDevice(device_ieee);
		if(device==null)
			return false;
		if (device_ieee != null && !TextUtils.isEmpty(device_ieee))
		{
			checkDeviceStateTable();
			
			ContentValues cv = new ContentValues();
			cv.put("device_ieee", device_ieee);
			cv.put("State", state);
			
			if (DbHelper.insert(S_DB, S_TABLE_DEVICE_STATE, cv, true))
			{
				S_DEVICE_STATES.put(device_ieee, state);
								
				FebeeAPI.getInstance().setDeviceStatus(device, state);
//				
				return true;
			}
		}
		return false;
	}
	
	private static HashMap<String, Integer> S_DEVICE_STATES = new HashMap<String, Integer>();
	
	public static int getDeviceState(String device_ieee)
	{
		if (S_DEVICE_STATES.containsKey(device_ieee))
		{
			return S_DEVICE_STATES.get(device_ieee);
		}
		else
		{
			String whereText = "device_ieee='" + device_ieee + "'";
			Cursor c = DbHelper.selectOne(S_DB, S_TABLE_DEVICE_STATE, new String[] {"State"}, whereText);
			if (c != null)
			{
				try
				{
					if (c.getCount() > 0)
					{
						c.moveToNext();
						
						int state = c.getInt(0);
						S_DEVICE_STATES.put(device_ieee, state);
						return state;
					}
				}
				catch (Exception exp)
				{
				}
				finally
				{
					c.close();
				}
			}
		}
		
		return 1;
	}
	
	public static boolean openDevice(String device_ieee)
	{
		return updateDeviceState(device_ieee, 1);
	}
	
	public static boolean closeDevice(String device_ieee)
	{
		return updateDeviceState(device_ieee, 0);
	}
	
}

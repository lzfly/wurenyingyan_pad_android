package com.wuren.datacenter.List;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import com.wuren.datacenter.bean.GatewayBean;

public class GatewayList {


	private static Hashtable<String, GatewayBean> S_GATEWAYS = new Hashtable<String, GatewayBean>();
	
	
	private static Object S_LOCK = new Object();
	
	
	public static void clear()
	{
		S_GATEWAYS.clear();
	}
	
	public static boolean exists(String sn)
	{
		return S_GATEWAYS.containsKey(sn);
	}
	
	//如果存在，增加失败
	public static boolean add(GatewayBean gateway)
	{
		String sn = gateway.getSN();
		if (!exists(sn))
		{
			synchronized (S_LOCK){
				S_GATEWAYS.put(sn, gateway);
			}
		}
		return false;
	}
	
	//如果存在，覆盖已有的
	public static void put(GatewayBean gateway)
	{
		String sn = gateway.getSN();
		synchronized (S_LOCK)
		{
			S_GATEWAYS.put(sn, gateway);
		}
	}
	
	public static GatewayBean getGateway(String sn)
	{
		if (exists(sn))
		{
			return S_GATEWAYS.get(sn);
		}
		return null;
	}
	
	public static GatewayBean findByIP(String ip)
	{
		GatewayBean result = null;
		
		if (ip != null)
		{
			Iterator<GatewayBean> i = S_GATEWAYS.values().iterator();
			while (i.hasNext())
			{
				GatewayBean gateway = i.next();
				if (ip.equalsIgnoreCase(gateway.getIP()))
				{
					result = gateway;
					break;
				}
			}
		}
		
		return result;
	}
	
	public static void remove(GatewayBean gate)
	{
		String gateway_sn=gate.getSN();
		
		synchronized (S_LOCK)
		{
			S_GATEWAYS.remove(gateway_sn);
		}
	}
	
	public static List<GatewayBean> getGatewayList()
	{
		List<GatewayBean> result = new ArrayList<GatewayBean>();
		result.addAll(S_GATEWAYS.values());
		return result;
	}

}

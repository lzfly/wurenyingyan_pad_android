package com.wuren.datacenter.bean;

public class DeviceTypeInfo {

	private int m_Id;
	private String m_Identification;
	private String m_Name;
	
	public int getId()
	{
		return m_Id;
	}
	
	public void setId(int id)
	{
		m_Id = id;
	}
	
	public String getIdentification()
	{
		return m_Identification;
	}
	
	public void setIdentification(String identification)
	{
		m_Identification = identification;
	}
	
	public String getName()
	{
		return m_Name;
	}
	
	public void setName(String name)
	{
		m_Name = name;
	}

}

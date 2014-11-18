package com.wuren.datacenter.bean;

public class DeviceClassBean {

	private String m_Code;
	private String m_Name;
	private String m_Icon;
	private String m_Type;

	public String getCode() {
		return m_Code;
	}

	public void setCode(String m_Code) {
		this.m_Code = m_Code;
	}

	public String getName() {
		return m_Name;
	}

	public void setName(String m_Name) {
		this.m_Name = m_Name;
	}
	
	public String getIcon()
	{
		return m_Icon;
	}
	
	public void setIcon(String icon)
	{
		m_Icon = icon;
	}
	
	public String getType()
	{
		return m_Type;
	}
	
	public void setType(String type)
	{
		m_Type = type;
	}

}

package com.wuren.datacenter.bean;

import java.util.Date;

import android.util.Log;

public class DeviceInfoBean {
	
	private String name; //device name
	
	private boolean  onlineStatus;// 0 不在线 其他值为在线
	
	private String SN; //SN号
	
	
	private byte[] IEEE;//IEEE地址
	
	private String strIEEE;//字符串格式的IEEE
	
	private int shortAddr;//短地址
	
	private char endPoint;//end point值
	
	private int profileID;//profile id array
		
	private int clusterID;//cluster id
	
	private String deviceType;
	
	private String gateway_SN;
	
	private int status=0;// 开/关/停 1/0/2,红外没有开关状态值
	
	private int lightness=0;//亮度
	
	
	private int hue=0; //色调
	
	private int saturation=0;//饱和度
	
	private int colorTemperature=0;//色温
	
	private boolean isOnline = false;
	
	private Date heartTime ;
	
	private Date eventTime;//上次事件上报时发生的时间，当间隔小于10s内上报，累计算作一次上报
	
	
	public Date getEventTime() {
		return eventTime;
	}

	public void setEventTime(Date eventTime) {
		this.eventTime = eventTime;
	}

	public boolean isOnline()
	{
		return isOnline;
	}
	
	public void setIsOnline(boolean online)
	{
		isOnline = online;
	}
	
	public Date getHeartTime()
	{
		return heartTime;
	}
	
	public void setHeartTime(Date time)
	{
		heartTime = time;
	}
		
	public int getColorTemperature() {
		return colorTemperature;
	}

	public void setColorTemperature(int colorTemperature) {
		this.colorTemperature = colorTemperature;
	}

	public int getHue() {
		return hue;
	}

	public void setHue(int hue) {
		this.hue = hue;
	}

	public int getSaturation() {
		return saturation;
	}

	public void setSaturation(int saturation) {
		this.saturation = saturation;
	}

	public int getLightness() {
		return lightness;
	}

	public void setLightness(int lightness) {
		this.lightness = lightness;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getGateway_SN() {
		return gateway_SN;
	}

	public void setGateway_SN(String gateway_SN) {
		this.gateway_SN = gateway_SN;
	}

	public String getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(int deviceType) {
		this.deviceType = String.format("%#06x", deviceType);
		
		//反得devicetype int类型值
		//int b = Integer.parseInt(this.deviceType.replaceAll("^0[x|X]", ""), 16);
		
		
	}

	public int getClusterID() {
		return clusterID;
	}

	public void setClusterID(int clusterID) {
		this.clusterID = clusterID;
	}

	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean getOnlineStatus() {
		return onlineStatus;
	}

	public void setOnlineStatus(boolean  isOnline) {
		
		this.onlineStatus = isOnline;
	}

	public String getIEEE_string_format() {
		return strIEEE;
	}

	public void setIEEE_string_format(String str_ieee) {
		strIEEE = str_ieee;
	}
	
	public String getSN() {
		return SN;
	}

	public void setSN(String sN) {
		SN = sN;
	}

	public byte[] getIEEE() {
		return IEEE;
	}

	public void setIEEE(byte[] iEEE) {
		IEEE = iEEE;
	}

	public int getShortAddr() {
		return shortAddr;
	}

	public void setShortAddr(int shortAddr) {
		this.shortAddr = shortAddr;
	}

	public char getEndPoint() {
		return endPoint;
	}

	public void setEndPoint(char endPoint) {
		this.endPoint = endPoint;
	}

	public int getProfileID() {
		return profileID;
	}

	public void setProfileID(int profileID) {
		this.profileID = profileID;
	}

	
	
	

}

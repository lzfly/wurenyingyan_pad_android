package com.wuren.datacenter.bean;

public class DeviceInfoBean {
	
	private String name; //device name
	
	private boolean  onlineStatus;// 0 不在线 其他值为在线
	
	private String SN; //SN号
	
	
	private byte[] IEEE;//IEEE地址
	
	private int shortAddr;//短地址
	
	private char endPoint;//end point值
	
	private int deviceID;//device id array
	
	private int profileID;//profile id array
		
	private int clusterID;//cluster id
	
	
		
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

	public int getDeviceID() {
		return deviceID;
	}

	public void setDeviceID(int deviceID) {
		this.deviceID = deviceID;
	}

	public int getProfileID() {
		return profileID;
	}

	public void setProfileID(int profileID) {
		this.profileID = profileID;
	}

	
	
	

}

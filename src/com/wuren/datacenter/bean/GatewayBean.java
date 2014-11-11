package com.wuren.datacenter.bean;

public class GatewayBean {
	
	private String IP;
	
	private String SN;
	
	private byte[] SNArray;
	
	private String version;
	
	private String username;
	
	private String password;
	
	

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public byte[] getSNArray() {
		return SNArray;
	}

	public void setSNArray(byte[] sNArray) {
		SNArray = sNArray;
	}

	public String getIP() {
		return IP;
	}

	public void setIP(String iP) {
		IP = iP;
	}

	public String getSN() {
		return SN;
	}

	public void setSN(String sN) {
		SN = sN;
	}
	
	

}

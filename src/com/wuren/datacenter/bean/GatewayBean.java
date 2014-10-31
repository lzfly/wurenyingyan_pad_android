package com.wuren.datacenter.bean;

public class GatewayBean {
	
	private String IP;
	
	private String SN;
	
	private byte[] SNArray;

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

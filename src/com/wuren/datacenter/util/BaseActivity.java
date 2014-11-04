package com.wuren.datacenter.util;

import java.util.Random;

import android.app.ActivityGroup;
import android.content.Context;

public class BaseActivity extends ActivityGroup {

	public Context thisObj = this;
	
	private int m_Id;
	
	public BaseActivity() {
		m_Id = new Random().nextInt();
	}
	
	public int getId()
	{
		return m_Id;
	}

}

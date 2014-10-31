package com.wuren.datacenter;

import java.io.BufferedReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import com.wuren.datacenter.R;
import com.wuren.datacenter.service.DataTransactionService;

import android.os.Bundle;
import android.os.SystemClock;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;

public class MainActivity extends Activity {

	private static String LOG_TAG="MainActivity";
	 public static final int DEFAULT_PORT = 9090;  
    private static final int MAX_DATA_PACKET_LENGTH = 256;  
    private byte[] buffer = new byte[MAX_DATA_PACKET_LENGTH];  
    
      
	    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		
		 Intent in = new Intent( this, DataTransactionService.class);
		 in.setAction(DataTransactionService.SEARCH_GATEWAY_ACTION);		
		 this.startService(in);	
		 
		 
		 
//		 in = new Intent( this, DataTransactionService.class);
//		 in.setAction(DataTransactionService.SEARCH_DEVICES_ACTION);		
//		 this.startService(in);	
//		 
		 
		 
//		 
		 
		//new BroadCastUdp("GETIP\r\n").start();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	 
	 
	 
}
	
	
	 
	



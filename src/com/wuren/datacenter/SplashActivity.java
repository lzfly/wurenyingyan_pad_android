package com.wuren.datacenter;

import java.io.File;
import java.util.List;

import com.wuren.datacenter.service.DataTransactionService;
import com.wuren.datacenter.util.ConstUtils;


import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.os.Handler.Callback;
import android.os.Message;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.view.Menu;

public class SplashActivity extends Activity {

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.qly_activity_splash);
		
		
		//jiaojc
		File configPath=new File(ConstUtils.G_GLOABAL_PATH);
		if(!configPath.exists())
		{
			configPath.mkdir();
		}
		//jiaojc
		Intent intent = new Intent(SplashActivity.this, DataTransactionService.class);
		SplashActivity.this.startService(intent);
		
		new Handler().postDelayed(new Runnable()
		{
			@Override
			public void run() {
                Intent guideIntent = new Intent(SplashActivity.this,  
                		GatewayActivity.class);
                SplashActivity.this.startActivity(guideIntent);  
                SplashActivity.this.finish(); 
			}
			
		}, ConstUtils.SPLASH_DISPLAY_TIME);
		
	}

	
	
	@Override
	protected void onStop() {
		super.onStop();
		
		
	}

	@Override
	protected void onResume() {
		super.onResume();
		
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return true;
	}

	
}

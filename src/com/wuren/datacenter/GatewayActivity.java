package com.wuren.datacenter;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.wuren.datacenter.bean.GatewayBean;
import com.wuren.datacenter.service.DataTransactionService;
import com.wuren.datacenter.util.BaseActivity;
import com.wuren.datacenter.widgets.MultiColumnAdapter;
import com.wuren.datacenter.widgets.MultiColumnView;



import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.AnimationDrawable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class GatewayActivity extends BaseActivity {
	
	class DeviceAdapter extends MultiColumnAdapter
	{

		@Override
		public View getView(Context context, int index, boolean isInsert) {
			
			
			View view = LayoutInflater.from(context).inflate(R.layout.qly_view_one_column_device, null);
//			
			Object data = getItem(index);
			if (data != null && data instanceof GatewayBean)
			{
				GatewayBean gate = (GatewayBean)data;
					//LinearLayout gateContainer = (LinearLayout)view.findViewById(R.id.sensor_container);
					//gateContainer.setTag(R.string.tag_sensor_info, gate);
					//gateContainer.setOnClickListener(sensorContainerOnClickListener);
//
					ImageView sensorType = (ImageView)view.findViewById(R.id.sensor_type_image);
					sensorType.setImageResource(R.drawable.qly_icon_camera_sensor);
//					
					
					TextView typeNameView = (TextView)view.findViewById(R.id.device_type_name);					
					
					Resources res=GatewayActivity.this.getResources();
				
					typeNameView.setText(res.getString(R.string.gate_IP_name)+gate.getIP());
//					
					TextView aliasNameView = (TextView)view.findViewById(R.id.device_alias_name);
					aliasNameView.setText(gate.getSN().toUpperCase());
				
					
					TextView gate_sn_view = (TextView)view.findViewById(R.id.gate_sn);
					gate_sn_view.setText(gate.getSN().toUpperCase());
				
					
//				
					m_SensorViews.put(gate.getSN(), view);
			}
//			
			return view;
		}

		@Override
		public View resetView(View view, int index, boolean isInsert) {
		
				return view;
		}
		
	}
	
	public synchronized void addNewGate(GatewayBean gate)
	{
		boolean haved = false;
		
		for (int i = 0; i < m_DeviceAdapter.getCount(); i++)
		{
			Object item = m_DeviceAdapter.getItem(i);
			if (item instanceof GatewayBean)
			{
				if (((GatewayBean)item).getSN().equals(gate.getSN()))
				{
					haved = true;
					break;
				}
			}
		}
		
		if (!haved)
		{
			m_DeviceAdapter.add(gate);
		}
		
		m_ReceivingContainer.setVisibility(View.GONE);
		m_ViewContainer.setVisibility(View.VISIBLE);

	}
	
	
	
	View.OnClickListener viewCameraClickListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {

		}
	};

	
	View.OnClickListener sensorContainerOnClickListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {

		}
		
	};

	View.OnClickListener switchOnOffClickListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {

		}
		
	};
	
	private ScrollView m_ViewContainer;

	private MultiColumnView m_MultiColumnView;
	private DeviceAdapter m_DeviceAdapter;
	
	private LinearLayout m_ReceivingContainer;
	
	
	private HashMap<String, View> m_SensorViews = new HashMap<String, View>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.qly_activity_gateway);
		m_ReceivingContainer = (LinearLayout)findViewById(R.id.device_receiving_container);
		
		m_ViewContainer = (ScrollView)findViewById(R.id.device_column_views_container);
		
		m_MultiColumnView = (MultiColumnView)findViewById(R.id.device_column_views);
		m_MultiColumnView.setScrollView(m_ViewContainer);
//
		m_DeviceAdapter = new DeviceAdapter();
		m_MultiColumnView.setAdapter(m_DeviceAdapter);
		
		initDevice();
		
		handler.postDelayed(runnable, TIME); //ÿ��2sִ��  
	}
	
	private int TIME = 2000; 
	Handler handler = new Handler();  
    Runnable runnable = new Runnable() {  
  
        @Override  
        public void run() {  
            try {  
            	            	
                handler.postDelayed(this, TIME);
                
                List<GatewayBean> new_gates;
                for(int i=0;i<DataTransactionService.mListGateway.size();i++)
                {
                	GatewayBean item=DataTransactionService.mListGateway.get(i);
                	boolean haved=false;
                	for (int j = 0; j < m_DeviceAdapter.getCount(); j++)
            		{
            			Object obj = m_DeviceAdapter.getItem(j);
            			if (item instanceof GatewayBean)
            			{
            				if (((GatewayBean)obj).getSN().equals(item.getSN()))
            				{
            					haved = true;
            					break;
            				}
            			}
            		}
                	
                	if (!haved)
            		{
            			m_DeviceAdapter.add(item);
            		}
                	
                	
                }
                
                if (m_DeviceAdapter.getCount() > 0)
				{
					m_ReceivingContainer.setVisibility(View.GONE);
					m_ViewContainer.setVisibility(View.VISIBLE);
				}
               
                
            } catch (Exception e) {  
                // TODO Auto-generated catch block  
                e.printStackTrace();  
                System.out.println("exception...");  
            }  
        }  
    };  
	
	private void initDevice()
	{
		new Handler().post(new Runnable(){
			
			@Override
			public void run() {
				
				List<GatewayBean> gates=DataTransactionService.mListGateway;
				if (gates!=null && gates.size() > 0)
				{
					m_DeviceAdapter.add(gates.toArray());
				}
				
				if (m_DeviceAdapter.getCount() > 0)
				{
					m_ReceivingContainer.setVisibility(View.GONE);
					m_ViewContainer.setVisibility(View.VISIBLE);
				}
			}

		});
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return true;
	}

	

}
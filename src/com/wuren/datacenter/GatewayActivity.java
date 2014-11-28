package com.wuren.datacenter;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;


import com.wuren.datacenter.List.DeviceList;
import com.wuren.datacenter.List.GatewayList;
import com.wuren.datacenter.bean.DeviceInfoBean;
import com.wuren.datacenter.bean.GatewayBean;
import com.wuren.datacenter.service.DataTransactionService;
import com.wuren.datacenter.util.BaseActivity;
import com.wuren.datacenter.util.DataUtils;
import com.wuren.datacenter.util.DeviceListener;
import com.wuren.datacenter.util.FebeeAPI;
import com.wuren.datacenter.widgets.MultiColumnAdapter;
import com.wuren.datacenter.widgets.MultiColumnView;



import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.os.Handler.Callback;
import android.os.Message;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.AnimationDrawable;
import android.text.TextUtils;
import com.wuren.datacenter.util.Log;
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
			
			
			View view = LayoutInflater.from(context).inflate(R.layout.qly_view_one_column_gate, null);
//			
			Object data = getItem(index);
			if (data != null && data instanceof GatewayBean)
			{
				GatewayBean gate = (GatewayBean)data;
				LinearLayout gateContainer = (LinearLayout)view.findViewById(R.id.sensor_container);
				gateContainer.setTag(R.string.tag_gate_circle, gate);
				gateContainer.setOnClickListener(sensorContainerOnClickListener);
				
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
				
					
					
					Button btDiscoveryDevice = (Button)view.findViewById(R.id.btn_discovery_device);
					btDiscoveryDevice.setTag(R.string.tag_gate_discovery, gate);
					btDiscoveryDevice.setOnClickListener(discorveryClickListener);
					
					
					Button btFactoryDevice = (Button)view.findViewById(R.id.btn_factory_gate);	
					btFactoryDevice.setTag(R.string.tag_gate_factory, gate);
					btFactoryDevice.setOnClickListener(factoryDeviceClickListener);
					
			
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
	
	
	View.OnClickListener sensorContainerOnClickListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			
			Object tagValue = v.getTag(R.string.tag_gate_circle);
			if (tagValue instanceof GatewayBean)
			{
				GatewayBean gate = (GatewayBean)tagValue;
				//启动与该网关相关联的设备列表。
				Intent intent=new Intent(GatewayActivity.this,DeviceActivity.class);
				
				intent.putExtra("gateway_sn", gate.getSN());
				
				GatewayActivity.this.startActivity(intent);
			}

		}
		
	};
	
	
View.OnClickListener discorveryClickListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			
			Log.v("jiaojc","discovery device");
			Object tagValue = v.getTag(R.string.tag_gate_discovery);
			if (tagValue instanceof GatewayBean)
			{
				GatewayBean gate = (GatewayBean)tagValue;
				
				
				
				//获得网关详细信息测试
//				 Intent in = new Intent( GatewayActivity.this, DataTransactionService.class);
//				 in.putExtra("gateway_sn", gate.getSN());
//				 Log.v("jiaojc","will set sn:"+gate.getSN());
//				 in.setAction(DataTransactionService.REQUEST_GATEWAYDETAIL_ACTION);
//				 GatewayActivity.this.startService(in);
				
				
				//允许入网测试
				FebeeAPI.getInstance().allowAddDevices(gate.getSN());
				//
	
			
			}

		}
		
	};

	View.OnClickListener factoryDeviceClickListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			
			Log.v("jiaojc","begin factory ...");
			Object tagValue = v.getTag(R.string.tag_gate_factory);
			if (tagValue instanceof GatewayBean)
			{
				GatewayBean gate = (GatewayBean)tagValue;
				
				//只复位网关内存信息
				//FebeeAPI.getInstance().resetGate(gate.getSN(), false, null);				
				//复位网络，真正恢复出厂
//			FebeeAPI.getInstance().resetGate(gate.getSN(), true, null);
				
///////////////////////////////////////////////////////////////////////////////////////////
				
				//删除指定设备API测试				
//				for(int i=0;i<DataUtils.mListDevices.size();i++)
//				{
//					DeviceInfoBean bean=DataUtils.mListDevices.get(i);
//					if(bean.getGateway_SN().equals("112E0710"))
//					{
//						FebeeAPI.getInstance().deleteDevice(bean,deviceTestListener);
//						break;
//					}
//				}
		
///////////////////////////////////////////////////////////////////////////////////////////
				//更改设备名测试
//				for(int i=0;i<DataUtils.mListDevices.size();i++)
//				{
//					DeviceInfoBean bean=DataUtils.mListDevices.get(i);
//					if(bean.getGateway_SN().equals("112E0710"))
//					{
//						FebeeAPI.getInstance().changeDeviceName(bean,"abc",deviceTestListener);
//						break;
//					}
//				}
				
/////////////////////////////////////////////////////////////////////////////////////////////				
				
//				
//				//String device_ieee="2A273A04004B1200";
//				
//				//指定插座的开关状态测试
//				String device_ieee="7C351105004B1200";
//				
//				DeviceInfoBean device=DeviceList.getDevice(device_ieee);
//				
//				if(device!=null)
//				{
//					FebeeAPI.getInstance().setDeviceStatus(device, 1, null);
//					SystemClock.sleep(2000);
//					FebeeAPI.getInstance().getDeviceStatus(device, null);
//				}
//				
				
///////////////////////////////////////////////////////////////////////////////////////////
				
				
				FebeeAPI.getInstance().openOrCloseOnLineSwitch(gate.getSN(),0);
			}
			

		}
		
	};
	DeviceListener deviceTestListener=new DeviceListener(){

		@Override
		public void onTaskComplete() {
			// TODO Auto-generated method stub
			
			Log.v("jiaojc","device listener onTaskComplete");
			
		}
		
	};
	
	private ScrollView m_ViewContainer;

	private MultiColumnView m_MultiColumnView;
	private DeviceAdapter m_DeviceAdapter;
	
	private LinearLayout m_ReceivingContainer;
	
	
	
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
		
		handler.postDelayed(runnable, TIME); //每隔2s执行  
	}
	
	private int TIME = 2000; 
	Handler handler = new Handler();  
    Runnable runnable = new Runnable() {  
  
        @Override  
        public void run() {  
            try {  
            	            	
                handler.postDelayed(this, TIME);
                
                List<GatewayBean> new_gates = new ArrayList<GatewayBean>();
                List<GatewayBean> currList = GatewayList.getGatewayList();
                for(int i = 0; i < currList.size(); i++)
                {
                	GatewayBean item = currList.get(i);
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
                		new_gates.add(item);
            		}
                }
				if (new_gates.size() > 0)
				{
					m_DeviceAdapter.add(new_gates.toArray());
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
				
				List<GatewayBean> gates = GatewayList.getGatewayList();
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

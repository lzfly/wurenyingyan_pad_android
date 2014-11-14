package com.wuren.datacenter.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;

import com.wuren.datacenter.List.DeviceList;
import com.wuren.datacenter.List.DeviceTypeList;
import com.wuren.datacenter.List.GatewayList;
import com.wuren.datacenter.bean.DeviceInfoBean;
import com.wuren.datacenter.bean.GatewayBean;
import com.wuren.datacenter.devicehandler.InvadeDeviceHandler;
import com.wuren.datacenter.util.CommonUtils;
import com.wuren.datacenter.util.ConstUtils;
import com.wuren.datacenter.util.DataUtils;
import com.wuren.datacenter.util.DeviceListener;
import com.wuren.datacenter.util.GatewayListener;
import com.wuren.datacenter.util.HttpUtils;
import com.wuren.datacenter.util.CommonUtils.DateFormatType;

import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;


public class ServiceSocketMonitor implements Runnable {

	
	private static Object S_SINGLE_RUN_LOCK = new Object();
	private static boolean S_RUNNING = false;
	
	
	private ByteArrayOutputStream m_ReadStream = new ByteArrayOutputStream();
		
	//要操作的socket
	private Socket mSocket;
	
	
	private Context mContext;
	
	GatewayBean mGate;
	
	
	



	
	
	public ServiceSocketMonitor(Context context,Socket socket,GatewayBean gate)
	{
		
		Log.v("jiaojc","socket ip:"+socket.getInetAddress().getHostAddress());
		
		this.mSocket=socket;
		m_ReadStream = new ByteArrayOutputStream();
		this.mContext=context;
		
		this.mGate=gate;
	}
	
	private GatewayListener mGatewayListener;
	public void setGatewayListener(GatewayListener gatewayListener)
	{
		this.mGatewayListener=gatewayListener;
	}
	
	private DeviceListener mDeviceListener;
	public void setDeviceListener(DeviceListener deviceListener)
	{
		this.mDeviceListener=deviceListener;
	}
	

	
	@Override
	public void run() {
		
				while(true)
				{
				
					try
					{
						
						if (mSocket != null )
						{
							
							if(mSocket.isClosed())
							{
								//断线了
								Log.v("jiaojc", mSocket.getInetAddress().getHostAddress()+"  is closed,Thread will be closed");
								break;
							}
							
							if( mSocket.isConnected())
							{
								
								try 
								{
									InputStream stream = mSocket.getInputStream();
									
									if(stream==null)
										break;
										
									m_ReadStream.reset();
									
									int buffSize = 512;
									byte[] buff = new byte[buffSize];
									int readSize = stream.read(buff, 0, buffSize);
	
									while (readSize > 0)
									{
										
										m_ReadStream.write(buff, 0, readSize);
										
										if (readSize < buffSize)									
											break;
																			
										readSize = stream.read(buff, 0, buffSize);
									}
									
									byte[] receivedData = m_ReadStream.toByteArray();

									if (receivedData.length > 2)
									{
										String msgs="";
										for(int k=0;k<receivedData.length;k++)
										{
											msgs+=(receivedData[k]&0xff);
											msgs+=" ";
											
										}
									    Log.v("jiaojc",mSocket.getInetAddress().getHostAddress()+" receive msg:"+msgs+"\n");
									    Log.d("wxm", "received data: " + msgs);

									    int bytesRead = receivedData.length;
					                    int bytesProcessed = 0;
					                    while (bytesRead > bytesProcessed)
					                    {
					                        bytesProcessed += rpcsProcessIncoming(receivedData, bytesProcessed);					                         
					                    }
									}
								} 
								catch (Exception e) 
								{
									e.printStackTrace();
								}
								
							}
							else
							{
								//断线了
								Log.v("jiaojc", mSocket.getInetAddress().getHostAddress()+"  is disconnected");								
								break;
							}
							
	
						}
					
						
						
							
						}				
					catch (Exception exp)
					{
						exp.printStackTrace();
					}
			
		  }
		
		
	}

	public int rpcsProcessIncoming(byte[] msg, int msgPtr)
    {
        int msgLen;
        int msgoldstart;
        msgoldstart = msgPtr;
        Log.v("jiaojc",""+mGate.getIP()+" rpcsProcessIncoming response header--"+msg[msgPtr + DataUtils.FbeeControlCommand.SRPC_CMD_ID_POS]);
        switch (msg[msgPtr + DataUtils.FbeeControlCommand.SRPC_CMD_ID_POS])
        {
         	

            case DataUtils.FbeeControlCommand.RPCS_NEW_ZLL_DEVICE:
                {
                    int profileId = 0, deviceId = 0, nwkAddr = 0;
                    char endPoint;
                    String deviceName = "";
                    String deviceSN = "";
                    byte[] ieee = new byte[8];
                    
                    boolean bOnline=false;

                    msgLen = msg[msgPtr + DataUtils.FbeeControlCommand.SRPC_CMD_LEN_POS] + 2;
                  
                    //index passed len and cmd ID
                    msgPtr += 2;

                    //Get the NwkAddr
                    for (int i = 0; i < 2; i++, msgPtr++)
                    {
                        //java does not support unsigned so use a bigger container to avoid conversion issues
                        int nwkAddrTemp = (msg[msgPtr] & 0xff);
                        nwkAddr += (nwkAddrTemp << (8 * i));
                    }
                    
             //       Log.v("jiaojc","shortAddress:"+nwkAddr+"\tHex:"+Integer.toHexString(nwkAddr));                    
                    
                    //Get the EndPoint
                    byte byte_endpoint=msg[msgPtr++];
                    endPoint = (char)byte_endpoint;
                    
                 //   Log.v("jiaojc","endPoint byteValue:"+byte_endpoint+"\tchar format:"+endPoint);

                    //Get the ProfileId
                    for (int i = 0; i < 2; i++, msgPtr++)
                    {
                        int profileIdTemp = (msg[msgPtr] & 0xff);
                        profileId += (profileIdTemp << (8 * i));
                    }
                    
              //      Log.v("jiaojc","profileId:"+profileId+"\tHex:"+Integer.toHexString(profileId));

                    //Get the DeviceId
                    for (int i = 0; i < 2; i++, msgPtr++)
                    {
                        int deviceIdTemp = (msg[msgPtr] & 0xff);
                        deviceId += (deviceIdTemp << (8 * i));
                    }
                    
                    
            //        Log.v("jiaojc","deviceId:"+deviceId+"\tHex:"+Integer.toHexString(deviceId));


                    //index passed version
                    msgPtr++;

                    //index passed device name
                    int nameSize = msg[msgPtr++];
                    if (nameSize > 0)
                    {
                        char[] device1 = new char[nameSize];
                        for (int i = 0; i < nameSize; i++)
                        {
                            device1[i] = (char)msg[msgPtr++];
                        }
                        deviceName = new String(device1,0,nameSize);
                        
                        Log.v("jiaojc","deviceName:"+deviceName);
                        
                    }
                    //index passed status
                    
                    //获得当前设备状态 0为不在线，其他值为在线
                    if(msg[msgPtr++]!=0)
                    {
                    	bOnline=true;
                    }
                    else
                    {
                    	bOnline=false;
                    }
                    
                    Log.v("jiaojc","device "+Integer.toHexString(nwkAddr));
                    
             
                    //copy IEEE Addr
                    for (int i = 0; i < 8; i++)
                    {
                        ieee[i] = msg[msgPtr++];
                    }
                    String str_ieee_temp=DataUtils.bytes2HexString(ieee);
                    Log.v("jiaojc","ieee:"+str_ieee_temp);
                    
                    if ((msgPtr - msgoldstart + 2) < msgLen)
                    {
                        int snSize = msg[msgPtr++];
                        if ((snSize > 0) && (snSize < 64))
                        {
                            char[] device2 = new char[snSize];
                            for (int i = 0; i < snSize; i++)
                            {
                                //deviceName += (char) msg[msgPtr++];
                                device2[i] = (char)msg[msgPtr++];
                            }

                            
                            deviceSN = new String(device2,0,snSize);
                //            Log.v("jiaojc","deviceSN:"+deviceSN);

                        }
                    }
                    newDevice(profileId, deviceId, nwkAddr, endPoint, ieee,bOnline,deviceName, deviceSN);

                    break;
                }
            case DataUtils.FbeeControlCommand.RPCS_ADD_GROUP_RSP:
                {
                    short groupId = 0;
                    msgLen = msg[msgPtr + DataUtils.FbeeControlCommand.SRPC_CMD_LEN_POS] + 2;

                    //index passed len and cmd ID
                    msgPtr += 2;

                    //Get the GroupId
                    for (int i = 0; i < 2; i++, msgPtr++)
                    {
                        //java does not support unsigned so use a bigger container to avoid conversion issues
                        int groupIdTemp = (msg[msgPtr] & 0xff);
                        groupId +=(short) (groupIdTemp << (8 * i));
                    }
             
                    break;
                }

            case DataUtils.FbeeControlCommand.RPCS_GET_GROUP_RSP:
                {
                    short groupId = 0;
                    msgLen = msg[msgPtr + DataUtils.FbeeControlCommand.SRPC_CMD_LEN_POS] + 2;

                    //index passed len and cmd ID
                    msgPtr += 2;

                    //Get the groupId
                    for (int i = 0; i < 2; i++, msgPtr++)
                    {
                        //java does not support unsigned so use a bigger container to avoid conversion issues
                        int groupIdTemp = (msg[msgPtr] & 0xff);
                        groupId +=(short) (groupIdTemp << (8 * i));
                    }
                    /*
                    String groupNameStr = new String(msg, msgPtr + 1, msg[msgPtr], Charset.defaultCharset());

                    ZigbeeAssistant.newGroup(groupNameStr, groupId, ZigbeeGroup.groupStatusActive);
                    */
                    break;
                }

            case DataUtils.FbeeControlCommand.RPCS_ADD_SCENE_RSP:
                {
                    short groupId = 0;
                    byte sceneId = 0;
                    msgLen = msg[msgPtr + DataUtils.FbeeControlCommand.SRPC_CMD_LEN_POS] + 2;

                    //index passed len and cmd ID
                    msgPtr += 2;

                    //Get the GroupId
                    for (int i = 0; i < 2; i++, msgPtr++)
                    {
                        //java does not support unsigned so use a bigger container to avoid conversion issues
                        int groupIdTemp = (msg[msgPtr] & 0xff);
                        groupId += (short)(groupIdTemp << (8 * i));
                    }

                    //Get the sceneId
                    sceneId = (byte)msg[msgPtr++];
         
                    break;
                }

            case DataUtils.FbeeControlCommand.RPCS_GET_SCENE_RSP:
                {
                    short groupId = 0;
                    byte sceneId = 0;
                    msgLen = msg[msgPtr + DataUtils.FbeeControlCommand.SRPC_CMD_LEN_POS] + 2;

                    //index passed len and cmd ID
                    msgPtr += 2;

                    //Get the groupId
                    for (int i = 0; i < 2; i++, msgPtr++)
                    {
                        //java does not support unsigned so use a bigger container to avoid conversion issues
                        int groupIdTemp = (msg[msgPtr] & 0xff);
                        groupId += (short)(groupIdTemp << (8 * i));
                    }

                    //Get the sceneId
                    sceneId = (byte)msg[msgPtr++];
                    /*
                    String sceneNameStr = new String(msg, msgPtr + 1, msg[msgPtr], Charset.defaultCharset());

                    ZigbeeAssistant.newScene(sceneNameStr, groupId, sceneId, ZigbeeScene.sceneStatusActive);
                    */
                    break;
                }
            case DataUtils.FbeeControlCommand.RPCS_GET_DEV_STATE_RSP:
                {
                    int nwkAddr = 0;
                    byte endPoint = 0;
                    byte state = 0;
                    msgLen = msg[msgPtr + DataUtils.FbeeControlCommand.SRPC_CMD_LEN_POS] + 2;

                    //index passed len and cmd ID
                    msgPtr += 2;

                    //Get the nwkAddr
                    for (int i = 0; i < 2; i++, msgPtr++)
                    {
                        //java does not support unsigned so use a bigger container to avoid conversion issues
                        int nwkAddrTemp = (msg[msgPtr] & 0xff);
                        nwkAddr += (nwkAddrTemp << (8 * i));
                    }

                    //Get the EP
                    endPoint = (byte)msg[msgPtr++];

                    //Get the state
                    state = (byte)msg[msgPtr++];
                    
                    DeviceInfoBean device=DeviceList.getDevice(nwkAddr);
                    if(device!=null)
                    	device.setStatus(state);
                    
                    if(this.mDeviceListener!=null)
                    {
                    	mDeviceListener.onTaskComplete();
                    }
                    
                    Log.v("jiaojc","device status:"+state+"\tdevice online value:"+device.getOnlineStatus());
                    
                    

                  
                    break;
                }
            case DataUtils.FbeeControlCommand.RPCS_GET_DEV_LEVEL_RSP:
                {
                    int nwkAddr = 0;
                    byte endPoint = 0;
                    byte level = 0;
                    msgLen = msg[msgPtr + DataUtils.FbeeControlCommand.SRPC_CMD_LEN_POS] + 2;

                    //index passed len and cmd ID
                    msgPtr += 2;

                    //Get the nwkAddr
                    for (int i = 0; i < 2; i++, msgPtr++)
                    {
                        //java does not support unsigned so use a bigger container to avoid conversion issues
                        int nwkAddrTemp = (msg[msgPtr] & 0xff);
                        nwkAddr += (nwkAddrTemp << (8 * i));
                    }

                    //Get the EP
                    endPoint = (byte)msg[msgPtr++];

                    //Get the state
                    level = (byte)msg[msgPtr++];
                    
                    
                    DeviceInfoBean device=DeviceList.getDevice(nwkAddr);
                    
                    if(device!=null)
                    	device.setLightness(level);
                    
                    if(this.mDeviceListener!=null)
                    {
                    	mDeviceListener.onTaskComplete();
                    }

                
                    break;
                }
            case DataUtils.FbeeControlCommand.RPCS_GET_DEV_HUE_RSP:
                {
                    int nwkAddr = 0;
                    byte endPoint = 0;
                    byte hue = 0;
                    msgLen = msg[msgPtr + DataUtils.FbeeControlCommand.SRPC_CMD_LEN_POS] + 2;

                    //index passed len and cmd ID
                    msgPtr += 2;

                    //Get the nwkAddr
                    for (int i = 0; i < 2; i++, msgPtr++)
                    {
                        //java does not support unsigned so use a bigger container to avoid conversion issues
                        int nwkAddrTemp = (msg[msgPtr] & 0xff);
                        nwkAddr += (nwkAddrTemp << (8 * i));
                    }

                    //Get the EP
                    endPoint = (byte)msg[msgPtr++];

                    //Get the state
                    hue = (byte)msg[msgPtr++];
                    
                    DeviceInfoBean device=DeviceList.getDevice(nwkAddr);
                    
                    if(device!=null)
                    	device.setHue(hue);
                    
                    if(this.mDeviceListener!=null)
                    {
                    	mDeviceListener.onTaskComplete();
                    }

                  
                    break;
                }
            case DataUtils.FbeeControlCommand.RPCS_GET_DEV_SAT_RSP:
                {
                    int nwkAddr = 0;
                    byte endPoint = 0;
                    byte sat = 0;
                    msgLen = msg[msgPtr + DataUtils.FbeeControlCommand.SRPC_CMD_LEN_POS] + 2;

                    //index passed len and cmd ID
                    msgPtr += 2;

                    //Get the nwkAddr
                    for (int i = 0; i < 2; i++, msgPtr++)
                    {
                        //java does not support unsigned so use a bigger container to avoid conversion issues
                        int nwkAddrTemp = (msg[msgPtr] & 0xff);
                        nwkAddr += (nwkAddrTemp << (8 * i));
                    }

                    //Get the EP
                    endPoint = (byte)msg[msgPtr++];

                    //Get the state
                    sat = (byte)msg[msgPtr++];
                    
                    
                    DeviceInfoBean device=DeviceList.getDevice(nwkAddr);
                    
                    if(device!=null)
                    	device.setSaturation(sat);
                    
                    if(this.mDeviceListener!=null)
                    {
                    	mDeviceListener.onTaskComplete();
                    }
            
                    break;
                }
            case DataUtils.FbeeControlCommand.RPCS_GET_DEV_SP:
            {
            	int nwkAddr = 0;
                byte endPoint = 0;
                short cluster_id=0;
                byte report_number=0;
                
                short attribute_id=0;
                
                byte dataType=0;
                
                byte data=0;
                 
            	msgLen = msg[msgPtr + DataUtils.FbeeControlCommand.SRPC_CMD_LEN_POS] + 2;
            	
            	
            	
            	 //index passed len and cmd ID
                msgPtr += 2;

                //Get the nwkAddr
                for (int i = 0; i < 2; i++, msgPtr++)
                {
                    int nwkAddrTemp = (msg[msgPtr] & 0xff);
                    nwkAddr += (nwkAddrTemp << (8 * i));
                }
                
                String strShortAddr=Integer.toHexString(nwkAddr).toUpperCase();
                if(strShortAddr.length()<4)
                	strShortAddr="0"+strShortAddr;
                
              //  Log.v("jiaojc",mSocket.getInetAddress().getHostAddress()+"--"+strShortAddr+" received a device response. value:"+nwkAddr);

                //Get the EP
                endPoint = (byte)msg[msgPtr++];
                
                //Cluster ID
                for (int i = 0; i < 2; i++, msgPtr++)
                {
                    //java does not support unsigned so use a bigger container to avoid conversion issues
                    int clusterIDTemp = (msg[msgPtr] & 0xff);
                    cluster_id += (short)(clusterIDTemp << (8 * i));
                }
                
                //报告个数
                report_number=msg[msgPtr++];
                
                //Attribute id
                for (int i = 0; i < 2; i++, msgPtr++)
                {
                    //java does not support unsigned so use a bigger container to avoid conversion issues
                    int attributeIDTemp = (msg[msgPtr] & 0xff);
                    attribute_id += (short)(attributeIDTemp << (8 * i));
                }
                
                //Data type
                dataType=msg[msgPtr++];
                
                //Data
                data=msg[msgPtr++];
                
                //开始处理
                DeviceInfoBean device=DeviceList.getDevice(nwkAddr);
                
                //先判断当前设备之前的在线状态，当为非在线状态时，需要上报                
                if(!device.isOnline())
                {                	
                	Log.v("jiaojc1",strShortAddr+"--before is offline,will set online status");
                	HttpUtils.deviceOnline(device, null);
                }
                
                device.setHeartTime(new Date());
                device.setIsOnline(true);
                
                
                String msgUpload=getUploadMessage(device,data);
                
                if(msgUpload.length()!=0)
                	HttpUtils.postDeviceData(device.getIEEE_string_format(), msgUpload, device.getDeviceType(), null);
                
            }
            break;
            case DataUtils.FbeeControlCommand.RPCS_GET_DEV_ColorTemperature_RSP:
            {
            	  int nwkAddr = 0;
                  byte endPoint = 0;
                  int colorTemperature = 0;
                  msgLen = msg[msgPtr + DataUtils.FbeeControlCommand.SRPC_CMD_LEN_POS] + 2;

                  //index passed len and cmd ID
                  msgPtr += 2;

                  //Get the nwkAddr
                  for (int i = 0; i < 2; i++, msgPtr++)
                  {
                      //java does not support unsigned so use a bigger container to avoid conversion issues
                      int nwkAddrTemp = (msg[msgPtr] & 0xff);
                      nwkAddr=(nwkAddrTemp << (8 * i));
                  }

                  //Get the EP
                  endPoint = (byte)msg[msgPtr++];

                  //Get the colorTemperature                  
                  for (int i = 0; i < 2; i++, msgPtr++)
                  {
                      int colorTemperatureTemp = (msg[msgPtr] & 0xff);
                      colorTemperature += (short)(colorTemperatureTemp << (8 * i));
                  }
                 
                  
                  
                  DeviceInfoBean device=DeviceList.getDevice(nwkAddr);
                  
                  if(device!=null)
                  	device.setColorTemperature(colorTemperature);
                  
                  if(this.mDeviceListener!=null)
                  {
                  		mDeviceListener.onTaskComplete();
                  }
            }
            break;
            case DataUtils.FbeeControlCommand.RPCS_GET_GATEDETAIL_RSP:
            {
            	msgLen = msg[msgPtr + DataUtils.FbeeControlCommand.SRPC_CMD_LEN_POS] + 2;
            	
            	msgPtr += 2;
            	
            	byte version[] =new byte[5];
            	for (int i = 0; i < version.length; i++)
                {
            		version[i] = msg[msgPtr++];
                }
                String gate_version=new String(version);
                Log.v("jiaojc","gate version:"+gate_version);
                
                byte snid[]=new byte[4];
                for (int i = 0; i < snid.length; i++)
                {
                	snid[3-i] = msg[msgPtr++];
                }
                
                Log.v("jiaojc","snid:"+snid[0]+"\t"+snid[1]+"\tsnid[2]:"+snid[2]+"\tsnid[3]:"+snid[3]);
                
                
                String hexSn=DataUtils.bytes2HexString(snid);
                
                Log.v("jiaojc","hexSN:"+hexSn);
                //username
                byte user[]=new byte[20];
                byte pwd[]=new byte[20];
                
                
               
                int userLenth=0;
                for (int i = 0; i < user.length; i++)
                {
                	user[i] = msg[msgPtr++];
                
                }
                
                for (int i = 0; i < user.length; i++)
                {
                	if(user[i]==0)
                		break;
                	else
                	{
                		userLenth++;
                	}
                }
                
                int pwdLength=0;
                
                for (int i = 0; i < pwd.length; i++)
                {
                	pwd[i] = msg[msgPtr++];                
                }
                for (int i = 0; i < pwd.length; i++)
                {
                	if(pwd[i]==0)
                		break;
                	else
                	{
                		pwdLength++;
                	}
                }

               Log.v("jiaojc","user:"+new String(user,0,userLenth)+"\tpwd:"+new String(pwd,0,pwdLength));
               
               
               
               GatewayBean gate=GatewayList.getGateway(hexSn);
               if(gate!=null)
               {
            		gate.setUsername(new String(user,0,userLenth));
       				gate.setPassword(new String(pwd,0,pwdLength));
       				gate.setVersion(gate_version);
               }
	            
               
              
                
            	
            }
            break;

            default:
                {
                    msgLen = 0;
                    break;
                }
        }

        return msgLen;
    }
	
	
	
	
	
	
	private void newDevice(int profileId,int deviceId, int nwkAddr, char endPoint,byte[] ieee,
			boolean bOnline,String deviceName, String deviceSN)
	{
		String strIEEE=DataUtils.bytes2HexString(ieee);
		 DeviceInfoBean device=new DeviceInfoBean();
		 
		 device.setProfileID(profileId);
		 device.setDeviceType(deviceId);
		 device.setShortAddr(nwkAddr);
		 device.setEndPoint(endPoint);
		 device.setIEEE(ieee);
		 device.setOnlineStatus(bOnline);
		 device.setName(deviceName);
		 device.setIEEE_string_format(strIEEE);
		 device.setSN(deviceSN);
		 device.setGateway_SN(this.mGate.getSN());
		 
		// device.setHeartTime(new Date());
		// device.setIsOnline(true);
		 
		 DeviceList.put(device);

		 HttpUtils.syncDevice(device, true, device.isOnline(), null);
		 
		 Log.d("wxm", device.getShortAddr() + " report time:  " + 
				 CommonUtils.formatDate(new Date(), DateFormatType.All));
	}
	
	
	//将数据组成对应的json格式
	private String getUploadMessage(DeviceInfoBean device ,int value)
	{
		String deviceType=device.getDeviceType();
		int typeValue = Integer.parseInt(deviceType.replaceAll("^0[x|X]", ""), 16);
		
		String result="";
		switch(typeValue)
		{
		case DeviceTypeList.Type.Occupancy_Sensor://红外
			
			if(value!=0)//有人，此时需要组织数据传递到服务器上
			{
				result="{\"Occupancy\":\"TRUE\"}";				
			}
				
			break;
		default:
			break;
		}
		
		return result;
	}
	
	
	
	
	
	
		
}

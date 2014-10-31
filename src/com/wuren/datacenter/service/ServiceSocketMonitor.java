package com.wuren.datacenter.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.wuren.datacenter.bean.DeviceInfoBean;
import com.wuren.datacenter.util.ConstUtils;

import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;


public class ServiceSocketMonitor implements Runnable {

	
	private static Object S_SINGLE_RUN_LOCK = new Object();
	private static boolean S_RUNNING = false;
	
	public static List<DeviceInfoBean> mListDevices=new ArrayList();
	private ByteArrayOutputStream m_ReadStream = new ByteArrayOutputStream();
		
	//要操作的socket
	private Socket mSocket;
	
	
	private Context mContext;
	
	
	
	   private static int SRPC_CMD_ID_POS = 0;
	   private static int SRPC_CMD_LEN_POS = 1;

       //SRPC CMD ID's	
       //define the outgoing RPSC command ID's
	   private static final byte RPCS_NEW_ZLL_DEVICE = (byte)0x0001;
	   private static final byte RPCS_DEV_ANNCE = (byte)0x0002;
       private static final byte RPCS_SIMPLE_DESC = (byte)0x0003;
       private static final byte RPCS_TEMP_READING = (byte)0x0004;
       private static final byte RPCS_POWER_READING = (byte)0x0005;
       private static final byte RPCS_PING = (byte)0x0006;
       private static final byte RPCS_GET_DEV_STATE_RSP = (byte)0x0007;
       private static final byte RPCS_GET_DEV_LEVEL_RSP = (byte)0x0008;
       private static final byte RPCS_GET_DEV_HUE_RSP = (byte)0x0009;
       private static final byte RPCS_GET_DEV_SAT_RSP = (byte)0x000a;
       private static final byte RPCS_ADD_GROUP_RSP = (byte)0x000b;
       private static final byte RPCS_GET_GROUP_RSP = (byte)0x000c;
       private static final byte RPCS_ADD_SCENE_RSP = (byte)0x000d;
       private static final byte RPCS_GET_SCENE_RSP = (byte)0x000e;


       //define incoming RPCS command ID's
       private static final byte RPCS_GET_DEV_SP = (byte)0x70;
       private static final byte RPCS_CLOSE = (byte)0x80;
       private static final byte RPCS_GET_DEVICES = (byte)0x81;
       private static final byte RPCS_SET_DEV_STATE = (byte)0x82;
       private static final byte RPCS_SET_DEV_LEVEL = (byte)0x83;
       private static final byte RPCS_SET_DEV_COLOR = (byte)0x84;
       private static final byte RPCS_GET_DEV_STATE = (byte)0x85;
       private static final byte RPCS_GET_DEV_LEVEL = (byte)0x86;
       private static final byte RPCS_GET_DEV_HUE = (byte)0x87;
       private static final byte RPCS_GET_DEV_SAT = (byte)0x88;
       private static final byte RPCS_BIND_DEVICES = (byte)0x89;
       private static final byte RPCS_GET_THERM_READING = (byte)0x8a;
       private static final byte RPCS_GET_POWER_READING = (byte)0x8b;
       private static final byte RPCS_DISCOVER_DEVICES = (byte)0x8c;
       private static final byte RPCS_SEND_ZCL = (byte)0x8d;
       private static final byte RPCS_GET_GROUPS = (byte)0x8e;
       private static final byte RPCS_ADD_GROUP = (byte)0x8f;
       private static final byte RPCS_GET_SCENES = (byte)0x90;
       private static final byte RPCS_STORE_SCENE = (byte)0x91;
       private static final byte RPCS_RECALL_SCENE = (byte)0x92;
       private static final byte RPCS_IDENTIFY_DEVICE = (byte)0x93;
       private static final byte RPCS_CHANGE_DEVICE_NAME = (byte)0x94;   

       
	
	
	public ServiceSocketMonitor(Socket socket,Context context)
	{
		
		Log.v("jiaojc","socket ip:"+socket.getInetAddress().getHostAddress());
		
		this.mSocket=socket;
		m_ReadStream = new ByteArrayOutputStream();
		this.mContext=context;
		
	}
	

	
	@Override
	public void run() {
		
		//if (!S_RUNNING)
		//{
			
			//	S_RUNNING = true;
				
				while(true)
				{
				
					try
					{
						
						if (mSocket != null )
						{
							
							if( mSocket.isConnected())
							{
								try 
								{
									InputStream stream = mSocket.getInputStream();
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
							}
							
	
						}
					
						
						
							
						}				
					catch (Exception exp)
					{
						exp.printStackTrace();
					}
					
				
					
				
			
		  }
		//}
		
	}
	
	
	
//	@Override
//	public void run() {
//		
//		 byte[] data = new byte[1024];
//		 int buffSize = 1024;
//         int recv;
//         int bytesRead = 0;
//         int bytesProcessed = 0;
//        
//         if(S_RUNNING==false)
//        	 S_RUNNING=true;
//         
//         while (true)
//         {
//             try
//             {
//            	 InputStream stream = mSocket.getInputStream();
//
//            	 recv = stream.read(data, 0, buffSize);
//                 if (recv > 2)
//                 {
//                     bytesRead = recv;
//                     bytesProcessed = 0;
//                     while (bytesRead > bytesProcessed)
//                     {
//                         bytesProcessed += rpcsProcessIncoming(data, bytesProcessed);
//                         
//                     }
//                 }
//             }
//             catch(Exception e)
//             {
//            	 e.printStackTrace();
//             }
//             
//           //  SystemClock.sleep(5000);
//             
//             if(S_RUNNING==false)
//            	 break;
//         }
//         
//	}
	
	public int rpcsProcessIncoming(byte[] msg, int msgPtr)
    {
        int msgLen;
        int msgoldstart;
        msgoldstart = msgPtr;
       // Log.v("jiaojc",""+msg[msgPtr + SRPC_CMD_ID_POS]);
        switch (msg[msgPtr + SRPC_CMD_ID_POS])
        {
         	

            case RPCS_NEW_ZLL_DEVICE:
                {
                    int profileId = 0, deviceId = 0, nwkAddr = 0;
                    char endPoint;
                    String deviceName = "";
                    String deviceSN = "";
                    byte[] ieee = new byte[8];
                    
                    boolean bOnline=false;


                   
                    
                    msgLen = msg[msgPtr + SRPC_CMD_LEN_POS] + 2;
                  
                    //index passed len and cmd ID
                    msgPtr += 2;

                    //Get the NwkAddr
                    for (int i = 0; i < 2; i++, msgPtr++)
                    {
                        //java does not support unsigned so use a bigger container to avoid conversion issues
                        int nwkAddrTemp = (msg[msgPtr] & 0xff);
                        nwkAddr += (nwkAddrTemp << (8 * i));
                    }
                    
                    Log.v("jiaojc","shortAddress:"+nwkAddr+"\tHex:"+Integer.toHexString(nwkAddr));                    
                    
                    //Get the EndPoint
                    endPoint = (char)msg[msgPtr++];

                    //Get the ProfileId
                    for (int i = 0; i < 2; i++, msgPtr++)
                    {
                        int profileIdTemp = (msg[msgPtr] & 0xff);
                        profileId += (profileIdTemp << (8 * i));
                    }
                    
                    Log.v("jiaojc","profileId:"+profileId+"\tHex:"+Integer.toHexString(profileId));

                    //Get the DeviceId
                    for (int i = 0; i < 2; i++, msgPtr++)
                    {
                        int deviceIdTemp = (msg[msgPtr] & 0xff);
                        deviceId += (deviceIdTemp << (8 * i));
                    }
                    
                    
                    Log.v("jiaojc","deviceId:"+profileId+"\tHex:"+Integer.toHexString(deviceId));


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
                    msgPtr += nameSize;

                    //index passed status
                    
                    //获得当前设备状态 0为不在线，其他值为在线
                    if(msg[msgPtr++]!=0)
                    	bOnline=true;
                    
                  //  msgPtr++;

                    //copy IEEE Addr
                    for (int i = 0; i < 8; i++)
                    {
                        ieee[i] = msg[msgPtr++];
                    }
                    
                    
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
                            Log.v("jiaojc","deviceSN:"+deviceSN);

                        }
                    }
                    newDevice(profileId, deviceId, nwkAddr, endPoint, ieee,bOnline,deviceName, deviceSN);

                    break;
                }
            case RPCS_ADD_GROUP_RSP:
                {
                    short groupId = 0;
                    msgLen = msg[msgPtr + SRPC_CMD_LEN_POS] + 2;

                    //index passed len and cmd ID
                    msgPtr += 2;

                    //Get the GroupId
                    for (int i = 0; i < 2; i++, msgPtr++)
                    {
                        //java does not support unsigned so use a bigger container to avoid conversion issues
                        int groupIdTemp = (msg[msgPtr] & 0xff);
                        groupId +=(short) (groupIdTemp << (8 * i));
                    }
                    /*
                    String groupNameStr = new String(msg, msgPtr + 1, msg[msgPtr], Charset.defaultCharset());

                    List<ZigbeeGroup> groupList = ZigbeeAssistant.getGroups();
                    //find the group
                    for (int i = 0; i < groupList.size(); i++)
                    {
                        if (groupNameStr.equals(groupList.get(i).getGroupName()))
                        {
                            groupList.get(i).setGroupId(groupId);
                            groupList.get(i).setStatus(ZigbeeGroup.groupStatusActive);
                            break;
                        }
                    }*/
                    break;
                }

            case RPCS_GET_GROUP_RSP:
                {
                    short groupId = 0;
                    msgLen = msg[msgPtr + SRPC_CMD_LEN_POS] + 2;

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

            case RPCS_ADD_SCENE_RSP:
                {
                    short groupId = 0;
                    byte sceneId = 0;
                    msgLen = msg[msgPtr + SRPC_CMD_LEN_POS] + 2;

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
                    /*
                    String sceneNameStr = new String(msg, msgPtr + 1, msg[msgPtr], Charset.defaultCharset());

                    List<ZigbeeScene> sceneList = ZigbeeAssistant.getScenes();
                    //find the scene
                    for (int i = 0; i < sceneList.size(); i++)
                    {
                        if (sceneNameStr.equals(sceneList.get(i).getSceneName()) && (groupId == sceneList.get(i).getGroupId()))
                        {
                            sceneList.get(i).setSceneId(sceneId);
                            sceneList.get(i).setStatus(ZigbeeScene.sceneStatusActive);
                            break;
                        }
                    }*/
                    break;
                }

            case RPCS_GET_SCENE_RSP:
                {
                    short groupId = 0;
                    byte sceneId = 0;
                    msgLen = msg[msgPtr + SRPC_CMD_LEN_POS] + 2;

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
            case RPCS_GET_DEV_STATE_RSP:
                {
                    short nwkAddr = 0;
                    byte endPoint = 0;
                    byte state = 0;
                    msgLen = msg[msgPtr + SRPC_CMD_LEN_POS] + 2;

                    //index passed len and cmd ID
                    msgPtr += 2;

                    //Get the nwkAddr
                    for (int i = 0; i < 2; i++, msgPtr++)
                    {
                        //java does not support unsigned so use a bigger container to avoid conversion issues
                        int nwkAddrTemp = (msg[msgPtr] & 0xff);
                        nwkAddr += (short)(nwkAddrTemp << (8 * i));
                    }

                    //Get the EP
                    endPoint = (byte)msg[msgPtr++];

                    //Get the state
                    state = (byte)msg[msgPtr++];

                    /*
                    List<ZigbeeDevice> devList = ZigbeeAssistant.getDevices();
                    //find the device
                    for (int i = 0; i < devList.size(); i++)
                    {
                        if ((((short)devList.get(i).NetworkAddr) == nwkAddr) && (devList.get(i).EndPoint == endPoint))
                        {
                            devList.get(i).setCurrentState(state);
                            break;
                        }
                    }*/
                    break;
                }
            case RPCS_GET_DEV_LEVEL_RSP:
                {
                    short nwkAddr = 0;
                    byte endPoint = 0;
                    byte level = 0;
                    msgLen = msg[msgPtr + SRPC_CMD_LEN_POS] + 2;

                    //index passed len and cmd ID
                    msgPtr += 2;

                    //Get the nwkAddr
                    for (int i = 0; i < 2; i++, msgPtr++)
                    {
                        //java does not support unsigned so use a bigger container to avoid conversion issues
                        int nwkAddrTemp = (msg[msgPtr] & 0xff);
                        nwkAddr += (short)(nwkAddrTemp << (8 * i));
                    }

                    //Get the EP
                    endPoint = (byte)msg[msgPtr++];

                    //Get the state
                    level = (byte)msg[msgPtr++];

                    /*
                    List<ZigbeeDevice> devList = ZigbeeAssistant.getDevices();
                    //find the device
                    for (int i = 0; i < devList.size(); i++)
                    {
                        if ((((short)devList.get(i).NetworkAddr) == nwkAddr) && (devList.get(i).EndPoint == endPoint))
                        {
                            devList.get(i).setCurrentLevel(level);
                            break;
                        }
                    }*/
                    break;
                }
            case RPCS_GET_DEV_HUE_RSP:
                {
                    short nwkAddr = 0;
                    byte endPoint = 0;
                    byte hue = 0;
                    msgLen = msg[msgPtr + SRPC_CMD_LEN_POS] + 2;

                    //index passed len and cmd ID
                    msgPtr += 2;

                    //Get the nwkAddr
                    for (int i = 0; i < 2; i++, msgPtr++)
                    {
                        //java does not support unsigned so use a bigger container to avoid conversion issues
                        int nwkAddrTemp = (msg[msgPtr] & 0xff);
                        nwkAddr += (short)(nwkAddrTemp << (8 * i));
                    }

                    //Get the EP
                    endPoint = (byte)msg[msgPtr++];

                    //Get the state
                    hue = (byte)msg[msgPtr++];

                    /*
                    List<ZigbeeDevice> devList = ZigbeeAssistant.getDevices();
                    //find the device
                    for (int i = 0; i < devList.size(); i++)
                    {
                        if ((((short)devList.get(i).NetworkAddr) == nwkAddr) && (devList.get(i).EndPoint == endPoint))
                        {
                            devList.get(i).setCurrentHue(hue);
                            break;
                        }
                    }*/
                    break;
                }
            case RPCS_GET_DEV_SAT_RSP:
                {
                    short nwkAddr = 0;
                    byte endPoint = 0;
                    byte sat = 0;
                    msgLen = msg[msgPtr + SRPC_CMD_LEN_POS] + 2;

                    //index passed len and cmd ID
                    msgPtr += 2;

                    //Get the nwkAddr
                    for (int i = 0; i < 2; i++, msgPtr++)
                    {
                        //java does not support unsigned so use a bigger container to avoid conversion issues
                        int nwkAddrTemp = (msg[msgPtr] & 0xff);
                        nwkAddr += (short)(nwkAddrTemp << (8 * i));
                    }

                    //Get the EP
                    endPoint = (byte)msg[msgPtr++];

                    //Get the state
                    sat = (byte)msg[msgPtr++];
                    /*

                    List<ZigbeeDevice> devList = ZigbeeAssistant.getDevices();
                    //find the device
                    for (int i = 0; i < devList.size(); i++)
                    {
                        if ((((short)devList.get(i).NetworkAddr) == nwkAddr) && (devList.get(i).EndPoint == endPoint))
                        {
                            devList.get(i).setCurrentSat(sat);
                            break;
                        }
                    }*/
                    break;
                }
            case RPCS_GET_DEV_SP:
            {
            	msgLen = msg[msgPtr + SRPC_CMD_LEN_POS] + 2;
            	//Log.v("jiaojc",mSocket.getInetAddress().getHostAddress()+" received a device response.");
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
	
	
	private String bytes2HexString(byte[] b) {
  	  String ret = "";
  	  for (int i = 0; i < b.length; i++) {
  	   String hex = Integer.toHexString(b[ i ] & 0xFF);
  	   if (hex.length() == 1) {
  	    hex = '0' + hex;
  	   }
  	   ret += hex.toUpperCase();
  	  }
  	  return ret;
  }
	
	private void newDevice(int profileId,int deviceId, int nwkAddr, char endPoint,byte[] ieee,
			boolean bOnline,String deviceName, String deviceSN)
	{
		String strIEEE=bytes2HexString(ieee);
		boolean bAdded=false;
		
		for(int i=0;i<mListDevices.size();i++)
		{
			byte[]ieee_item=mListDevices.get(i).getIEEE();
			
			String str_ieee_temp=bytes2HexString(ieee);
			if(str_ieee_temp.equals(strIEEE))
			{
				bAdded=true;
				break;
			}
		}
		
		if(bAdded)
			return;
		
		 DeviceInfoBean device=new DeviceInfoBean();
		 
		 device.setProfileID(profileId);
		 device.setDeviceID(deviceId);
		 device.setShortAddr(nwkAddr);
		 device.setEndPoint(endPoint);
		 device.setIEEE(ieee);
		 device.setOnlineStatus(bOnline);
		 device.setName(deviceName);
		 device.setSN(deviceSN);
		 
		 mListDevices.add(device);
		 
	}
	
	
	
	

//	@Override
//	public void run() {
//		
//		if (!S_RUNNING)
//		{
//			synchronized(S_SINGLE_RUN_LOCK)
//			{
//				S_RUNNING = true;
//				
//				while(S_RUNNING)
//				{
//				
//					try
//					{
//						
//						if (mSocket != null && mSocket.isConnected())
//						{
//							
//								try 
//								{
//									InputStream stream = mSocket.getInputStream();
//									m_ReadStream.reset();
//									
//									int buffSize = 512;
//									byte[] buff = new byte[buffSize];
//									int readSize = stream.read(buff, 0, buffSize);
//	
//									while (readSize > 0)
//									{
//										
//										m_ReadStream.write(buff, 0, readSize);
//										
//										if (readSize < buffSize)									
//											break;
//																			
//										readSize = stream.read(buff, 0, buffSize);
//									}
//									
//									byte[] receivedData = m_ReadStream.toByteArray();
//
//									if (receivedData.length > 0)
//									{
//										String msgs="";
//										for(int k=0;k<receivedData.length;k++)
//										{
//											msgs+=(receivedData[k]&0xff);
//											msgs+="\t";
//											
//										}
//									//	 Log.v("jiaojc","msg:"+msgs+"\n");
//										
//										 Intent in = new Intent( mContext, DataTransactionService.class);
//										 in.setAction(DataTransactionService.RESPONSE_COMMANDS_ACTION);
//										 
//										 in.putExtra("command_type", mOperatorType);
//										 in.putExtra("byte_array", receivedData);
//										 mContext.startService(in);	
//										 
//									}
//									
//								} 
//								catch (Exception e) 
//								{
//									e.printStackTrace();
//								}
//								
//								
//								
//								
//	
//							}					
//							
//						}				
//					catch (Exception exp)
//					{
//						exp.printStackTrace();
//					}				
//				
//			}
//		  }
//		}
//		
//	}
	
}

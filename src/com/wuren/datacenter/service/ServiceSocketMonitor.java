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

import com.wuren.datacenter.bean.DeviceInfoBean;
import com.wuren.datacenter.bean.GatewayBean;
import com.wuren.datacenter.util.ConstUtils;
import com.wuren.datacenter.util.DataUtils;
import com.wuren.datacenter.util.GatewayListener;

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
                            Log.v("jiaojc","deviceSN:"+deviceSN);

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
                    short nwkAddr = 0;
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
            case DataUtils.FbeeControlCommand.RPCS_GET_DEV_LEVEL_RSP:
                {
                    short nwkAddr = 0;
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
            case DataUtils.FbeeControlCommand.RPCS_GET_DEV_HUE_RSP:
                {
                    short nwkAddr = 0;
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
            case DataUtils.FbeeControlCommand.RPCS_GET_DEV_SAT_RSP:
                {
                    short nwkAddr = 0;
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
            case DataUtils.FbeeControlCommand.RPCS_GET_DEV_SP:
            {
            	msgLen = msg[msgPtr + DataUtils.FbeeControlCommand.SRPC_CMD_LEN_POS] + 2;
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
	
	
	
	
	private void newDevice(int profileId,int deviceId, int nwkAddr, char endPoint,byte[] ieee,
			boolean bOnline,String deviceName, String deviceSN)
	{
		String strIEEE=DataUtils.bytes2HexString(ieee);
		boolean bAdded=false;
		
		for(int i=0;i<DataUtils.mListDevices.size();i++)
		{
			byte[]ieee_item=DataUtils.mListDevices.get(i).getIEEE();
			
			String str_ieee_temp=DataUtils.bytes2HexString(ieee_item);
			
			
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
		 device.setIEEE_string_format(strIEEE);
		 device.setSN(deviceSN);
		 device.setGateway_SN(this.mGate.getSN());
		 
		 DataUtils.mListDevices.add(device);
		 
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

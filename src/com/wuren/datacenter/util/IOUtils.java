package com.wuren.datacenter.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;


public class IOUtils {

	// tag for log
	private static String TAG = IOUtils.class.getSimpleName();
	
	// Load image from local
	public static Bitmap getBitmapLocal(String url) {
		try {
			FileInputStream fis = new FileInputStream(url);
			return BitmapFactory.decodeStream(fis);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}

	// Load image from network
//	public static Bitmap getBitmapRemote(Context ctx, String url) {
//		URL myFileUrl = null;
//		Bitmap bitmap = null;
//		try {
//			Log.w(TAG, url);
//			myFileUrl = new URL(url);
//		} catch (MalformedURLException e) {
//			e.printStackTrace();
//		}
//		try {
//			HttpURLConnection conn = null;
//			if (HttpUtil.WAP_INT == HttpUtil.getNetType(ctx)) {
//				Proxy proxy = new Proxy(java.net.Proxy.Type.HTTP, new InetSocketAddress("10.0.0.172", 80)); 
//				conn = (HttpURLConnection) myFileUrl.openConnection(proxy);
//			} else {
//				conn = (HttpURLConnection) myFileUrl.openConnection();
//			}
//			conn.setConnectTimeout(10000);
//			conn.setDoInput(true);
//			conn.connect();
//			InputStream is = conn.getInputStream();
//			bitmap = BitmapFactory.decodeStream(is);
//			is.close();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		return bitmap;
//	}
	
	public static String  readMessage(String txtPath)
	{
		StringBuffer sb= new StringBuffer("");
		 try {
	            // read file content from file
	            
	           
	            FileReader reader = new FileReader(txtPath);
	            BufferedReader br = new BufferedReader(reader);
	           
	            String str = null;
	           
	            while((str = br.readLine()) != null) {
	                  sb.append(str+"\n");	                 
	                  System.out.println(str);
	            }
	           
	            br.close();
	            reader.close();
		 }
	      catch(FileNotFoundException e) {
	                  e.printStackTrace();
	     }
	     catch(IOException e) {
	                  e.printStackTrace();
	     }
		 return sb.toString();
   }
	

	//删除一个文件夹
    public static void delete(File file) {  
        if (file.isFile()) {  
            file.delete();  
            return;  
        }  
  
        if(file.isDirectory()){  
            File[] childFiles = file.listFiles();  
            if (childFiles == null || childFiles.length == 0) {  
                file.delete();  
                return;  
            }  
      
            for (int i = 0; i < childFiles.length; i++) {  
                delete(childFiles[i]);  
            }  
            file.delete();  
        }  
    } 
    
    public static String readFile(String filePath){
        FileInputStream fis = null;
        byte[] mByte = new byte[1024];
        //int length=0;
        String strReturn="";
        try {
            fis = new FileInputStream(new File(filePath));
            InputStreamReader isr=new InputStreamReader(fis);
            
            BufferedReader br=new BufferedReader(isr);
            
            String show=br.readLine();
            strReturn+=show;
            while(show!=null){
            //String show=br.readLine();
            System.out.println(show);            
            show=br.readLine();
            if(show!=null)
                strReturn+=show;
            }
            
            //length=fis.read(mByte);
        }   
        catch (FileNotFoundException e) 
        {
            e.printStackTrace();
        } 
        catch (IOException e) {
            e.printStackTrace();
        }
        finally{
        try {
            if(null!=fis)
            fis.close();
        } 
        catch (IOException e) {
            e.printStackTrace();
            }
        }
        //mByte[length]='\n';
        //return new String(mByte).toString();
        return strReturn;
     }
    
    public static String readFile(File txtFile){
        FileInputStream fis = null;
        byte[] mByte = new byte[1024];
        //int length=0;
        String strReturn="";
        try {
            fis = new FileInputStream(txtFile);
            InputStreamReader isr=new InputStreamReader(fis);
            
            BufferedReader br=new BufferedReader(isr);
            
            String show=br.readLine();
            strReturn+=show;
            while(show!=null){
            //String show=br.readLine();
            System.out.println(show);            
            show=br.readLine();
            if(show!=null)
                strReturn+=show;
            }
          
        }   
        catch (FileNotFoundException e) 
        {
            e.printStackTrace();
        } 
        catch (IOException e) {
            e.printStackTrace();
        }
        finally{
        try {
            if(null!=fis)
            fis.close();
        } 
        catch (IOException e) {
            e.printStackTrace();
            }
        }
        return strReturn;
     }

    //枚举文件夹下所有扩展名为ext的文件
    public static List<String> getFilePathList(String folder,String ext)  {
    	List<String> fileList = new ArrayList<String>();
    	File root=new File(folder);
    	if(!root.exists())
    		return null;
    	else
    	{
    		File[] files = root.listFiles();
    		for(int i=0;i<files.length;i++)
    		{
    			if(files[i].getPath().endsWith(ext))
    			{
    				fileList.add(files[i].getAbsolutePath());
    			}
    		}
    		return fileList;
    	}
    	
    }
}


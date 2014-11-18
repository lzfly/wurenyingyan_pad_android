package com.wuren.datacenter.util;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.util.Log;



public class ImageUtils {


	public static Bitmap getThumbnails(String path) 
	{
		
		BitmapFactory.Options options = new BitmapFactory.Options();  
		options.inJustDecodeBounds=true;
		BitmapFactory.decodeFile(path,options);
		//options.inSampleSize = computeSampleSize(options, -1, 128*128);
		
		int be = options.outHeight/200;  
		Log.v("jiaojc","getThumbnails:"+options.outHeight+"\tbe:"+be);
		
        if (be <= 0) {  
            be = 10;  
        }  
        options.inSampleSize = be; 
		
		options.inJustDecodeBounds = false;  
		Bitmap bitmap=null;
		bitmap = BitmapFactory.decodeFile(path,options);
		
		return bitmap;
		
	}
	
	 static boolean  bmp2file(Bitmap bmp,String full_path){  
         CompressFormat format= Bitmap.CompressFormat.JPEG;  
        int quality = 60;  
        OutputStream stream = null;  
        try {  
                stream = new FileOutputStream(full_path);  
        } catch (FileNotFoundException e) {  
                // TODO Auto-generated catch block  
                e.printStackTrace();  
        }  
        
        if(stream!=null)        
        	return bmp.compress(format, quality, stream);        
        else
        	return false;
        
          
        } 
	 
	 
	
	public static int computeSampleSize(BitmapFactory.Options options, int minSideLength, int maxNumOfPixels) {  
	    int initialSize = computeInitialSampleSize(options, minSideLength, maxNumOfPixels);  
	    int roundedSize;  
	    if (initialSize <= 8) {  
	        roundedSize = 1;  
	        while (roundedSize < initialSize) {  
	            roundedSize <<= 1;  
	        }  
	    } else {  
	    	
	    	
	         
	        roundedSize = (initialSize + 7) / 8 * 8;  
	    }  
	    return roundedSize;  
	}  
	  
	private static int computeInitialSampleSize(BitmapFactory.Options options,int minSideLength, int maxNumOfPixels) {  
	    double w = options.outWidth;  
	    double h = options.outHeight;  
	    int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math.sqrt(w * h / maxNumOfPixels));  
	    int upperBound = (minSideLength == -1) ? 128 :(int) Math.min(Math.floor(w / minSideLength), Math.floor(h / minSideLength));  
	    if (upperBound < lowerBound) {  
	        // return the larger one when there is no overlapping zone.  
	        return lowerBound;  
	    }  
	    if ((maxNumOfPixels == -1) && (minSideLength == -1)) {  
	        return 1;  
	    } else if (minSideLength == -1) {  
	        return lowerBound;  
	    } else {  
	        return upperBound;  
	    }  
	}
}

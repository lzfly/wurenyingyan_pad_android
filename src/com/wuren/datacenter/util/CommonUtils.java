package com.wuren.datacenter.util;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;

import net.tsz.afinal.FinalBitmap;
import android.content.Context;
import android.content.res.Resources;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.TypedValue;
import android.widget.ImageView;

public class CommonUtils {
	
	public enum DateFormatType
	{
		All,
		yyyy_MM_dd_HH_mm,
		yyyy_MM_dd
	}
	
	private static SimpleDateFormat m_SDF_ALL = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private static SimpleDateFormat m_SDF_YYYY_MM_DD_HH_MM = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	private static SimpleDateFormat m_SDF_YYYY_MM_DD = new SimpleDateFormat("yyyy-MM-dd");
	
	public static String formatDate(Date date, DateFormatType type)
	{
		if (type == DateFormatType.yyyy_MM_dd)
		{
			return m_SDF_YYYY_MM_DD.format(date);
		}
		else if (type == DateFormatType.yyyy_MM_dd_HH_mm)
		{
			return m_SDF_YYYY_MM_DD_HH_MM.format(date);
		}
		else
		{
			return m_SDF_ALL.format(date);
		}
	}
	
	public static int dip2px(float dip)
	{
		Resources resources = GlobalContext.getInstance().getResources();
		float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, resources.getDisplayMetrics());
		return Math.round(px);
	}
	
	public static int px2dip(float px)
	{
		Resources resources = GlobalContext.getInstance().getResources();
		final float scale = resources.getDisplayMetrics().density;
		return Math.round(px / scale + 0.5f);
	}
	
	public static int getStatusBarHeight(Context context){
	    Class<?> c = null;
	    
	    Object obj = null;
	    
	    Field field = null;
	    
	    int x = 0, statusBarHeight = 0;
	    try
	    {
	        c = Class.forName("com.android.internal.R$dimen");
	        obj = c.newInstance();
	        field = c.getField("status_bar_height");
	        x = Integer.parseInt(field.get(obj).toString());
	        statusBarHeight = context.getResources().getDimensionPixelSize(x);
	    }
	    catch (Exception e1) 
	    {
	    }
	    return statusBarHeight;
	}
	
	public static String getDeviceId(Context context)
	{
		TelephonyManager tm = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
		return tm.getDeviceId();
	}

	public static String getPhoneNumber(Context context)
	{
		TelephonyManager tm = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
		return tm.getLine1Number();
	}
	
	public static String getUniqueSN(Context context)
	{
		String result = getPhoneNumber(context);
		if (!(result != null && !TextUtils.isEmpty(result)))
		{
			result = getDeviceId(context);
		}
		return result;
	}

	private static FinalBitmap m_FinalBitmap = null;
	public static FinalBitmap getImageDisplayer(Context context)
	{
		if (m_FinalBitmap == null)
		{
			m_FinalBitmap = FinalBitmap.create(context);
			m_FinalBitmap.configBitmapLoadThreadSize(10);
			m_FinalBitmap.configBitmapMaxHeight(200);
			m_FinalBitmap.configBitmapMaxWidth(200);
			m_FinalBitmap.configMemoryCachePercent(0.05f);
		}
		return m_FinalBitmap;
	}
	
	public static void loadImage(Context context, ImageView view, String url)
	{
		FinalBitmap bmp = getImageDisplayer(context);
		bmp.display(view, url);
	}
	
	public static byte[] subBytes(byte[] source, int start, int len)
	{
		byte[] result = new byte[len];
		if (start + len <= source.length)
		{
			for (int i = 0; i < len; i++)
			{
				result[i] = source[start + i];
			}
		}
		return result;
	}
	
}

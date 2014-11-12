package com.wuren.datacenter.util;

import com.wuren.datacenter.R;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.ViewGroup.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

public class ToastUtils {

	public static void show(Context context, String text, int textColor, float textSize, int gravity, int xOffset, int yOffset)
	{
		Toast t = Toast.makeText(context, "", Toast.LENGTH_SHORT);
		t.setGravity(gravity, xOffset, yOffset);
		TextView tv = new TextView(context);
		tv.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		tv.setBackgroundResource(R.drawable.qly_shape_toast_round_rectangle);
		tv.setTextSize(textSize);
		tv.setTextColor(textColor);
		tv.setText(text);
		t.setView(tv);
		t.show();
	}
	
	public static void show(Context context, String text, int textColor, int textSize)
	{
		show(context, text, textColor, textSize, Gravity.CENTER | Gravity.BOTTOM, 0, CommonUtils.dip2px(60));
	}
	
	public static void show(Context context, String text, int textColor)
	{
		show(context, text, textColor, 17);
	}
	
	public static void show(Context context, String text)
	{
		show(context, text, Color.WHITE);
	}
	
	public static void show(Context context, int strId)
	{
		show(context, context.getString(strId), Color.WHITE);
	}
	
}

package com.wuren.datacenter.bean;

import android.graphics.Bitmap;

public class ZipPictureBean {
		
			private Bitmap bitmap; //当前zipPath里的zipName图片
			private String zipPath;//压缩包路径
			private String zipName;//压缩包目录里的文件名
			
			public ZipPictureBean(String zipPath)
			{
				this.zipPath=zipPath;
			}
			public String getZipName() {
				return zipName;
			}
			public void setZipName(String zipName) {
				this.zipName = zipName;
			}
			public Bitmap getBitmap() {
				return bitmap;
			}
			
			public void setBitmap(Bitmap bitmap) {
				
				this.bitmap = bitmap;
			}
			public String getZipPath() {
				return zipPath;
			}
			
			public void setZipPath(String zipPath) {
				this.zipPath = zipPath;
			}
			
			public String toString()
			{
				return zipPath;
			}
		 
	

}

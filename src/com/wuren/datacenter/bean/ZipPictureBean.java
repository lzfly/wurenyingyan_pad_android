package com.wuren.datacenter.bean;

import android.graphics.Bitmap;

public class ZipPictureBean {
		
			private Bitmap bitmap; //��ǰzipPath���zipNameͼƬ
			private String zipPath;//ѹ����·��
			private String zipName;//ѹ����Ŀ¼����ļ���
			
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

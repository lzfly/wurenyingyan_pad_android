package com.wuren.datacenter.util;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import com.wuren.datacenter.bean.ZipPictureBean;



import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * Java utils 实现的Zip工具
 *
 * @author once
 */
public class ZipUtil {
    private static final int BUFF_SIZE = 1024 * 1024; // 1M Byte

    /**
     * 批量压缩文件（夹）
     *
     * @param resFileList 要压缩的文件（夹）列表
     * @param zipFile 生成的压缩文件
     * @throws IOException 当压缩过程出错时抛出
     */
    public static void zipFiles(Collection<File> resFileList, File zipFile) throws IOException {
        ZipOutputStream zipout = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(
                zipFile), BUFF_SIZE));
        for (File resFile : resFileList) {
            zipFile(resFile, zipout, "");
        }
        zipout.close();
    }

    /**
     * 批量压缩文件（夹）
     *
     * @param resFileList 要压缩的文件（夹）列表
     * @param zipFile 生成的压缩文件
     * @param comment 压缩文件的注释
     * @throws IOException 当压缩过程出错时抛出
     */
    public static void zipFiles(Collection<File> resFileList, File zipFile, String comment)
            throws IOException {
        ZipOutputStream zipout = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(
                zipFile), BUFF_SIZE));
        for (File resFile : resFileList) {
            zipFile(resFile, zipout, "");
        }
        zipout.setComment(comment);
        zipout.close();
    }

    /**
     * 解压缩一个文件
     *
     * @param zipFile 压缩文件
     * @param folderPath 解压缩的目标目录
     * @throws IOException 当解压缩过程出错时抛出
     */
    public static void upZipFile(File zipFile, String folderPath) throws ZipException, IOException {
        File desDir = new File(folderPath);
        if (!desDir.exists()) {
            desDir.mkdirs();
        }
        ZipFile zf = new ZipFile(zipFile);
        for (Enumeration<?> entries = zf.entries(); entries.hasMoreElements();) {
            ZipEntry entry = ((ZipEntry)entries.nextElement());
            //后面插入判断是否为文件夹，如果是不处理
            if(entry.isDirectory()) 
            	continue;
//            String name = entry.getName(); 
//            if(name.endsWith(File.separator)) 
//            	continue; 
            InputStream in = zf.getInputStream(entry);
            String str = folderPath + File.separator + entry.getName();
            str = new String(str.getBytes("8859_1"), "GB2312");
            File desFile = new File(str);
            if (!desFile.exists()) {
                File fileParentDir = desFile.getParentFile();
                if (!fileParentDir.exists()) {
                    fileParentDir.mkdirs();
                }
                desFile.createNewFile();
            }
            OutputStream out = new FileOutputStream(desFile);
            byte buffer[] = new byte[BUFF_SIZE];
            int realLength;
            while ((realLength = in.read(buffer)) > 0) {
                out.write(buffer, 0, realLength);
            }
            in.close();
            out.close();
        }
    }

    /**
     * 解压文件名包含传入文字的文件
     *
     * @param zipFile 压缩文件
     * @param folderPath 目标文件夹
     * @param nameContains 传入的文件匹配名
     * @throws ZipException 压缩格式有误时抛出
     * @throws IOException IO错误时抛出
     */
    public static ArrayList<File> upZipSelectedFile(File zipFile, String folderPath,
            String nameContains) throws ZipException, IOException {
        ArrayList<File> fileList = new ArrayList<File>();

        File desDir = new File(folderPath);
        if (!desDir.exists()) {
            desDir.mkdir();
        }

        ZipFile zf = new ZipFile(zipFile);
        for (Enumeration<?> entries = zf.entries(); entries.hasMoreElements();) {
            ZipEntry entry = ((ZipEntry)entries.nextElement());
            if (entry.getName().contains(nameContains)) {
                InputStream in = zf.getInputStream(entry);
                String str = folderPath + File.separator + entry.getName();
                str = new String(str.getBytes("8859_1"), "GB2312");
                // str.getBytes("GB2312"),"8859_1" 输出
                // str.getBytes("8859_1"),"GB2312" 输入
                File desFile = new File(str);
                if (!desFile.exists()) {
                    File fileParentDir = desFile.getParentFile();
                    if (!fileParentDir.exists()) {
                        fileParentDir.mkdirs();
                    }
                    desFile.createNewFile();
                }
                OutputStream out = new FileOutputStream(desFile);
                byte buffer[] = new byte[BUFF_SIZE];
                int realLength;
                while ((realLength = in.read(buffer)) > 0) {
                    out.write(buffer, 0, realLength);
                }
                in.close();
                out.close();
                fileList.add(desFile);
            }
        }
        return fileList;
    }
    
    public static boolean upZipSelectedFile(File zipFile, String folderPath,String outName,
            String nameContains) throws ZipException, IOException {
      
    	boolean bUnZip=false;
        File desDir = new File(folderPath);
        if (!desDir.exists()) {
            desDir.mkdir();
        }

        ZipFile zf = new ZipFile(zipFile);
        for (Enumeration<?> entries = zf.entries(); entries.hasMoreElements();) {
            ZipEntry entry = ((ZipEntry)entries.nextElement());
            if (entry.getName().contains(nameContains)) {
                InputStream in = zf.getInputStream(entry);
                String str = folderPath + File.separator + outName;
                str = new String(str.getBytes("8859_1"), "GB2312");
                // str.getBytes("GB2312"),"8859_1" 输出
                // str.getBytes("8859_1"),"GB2312" 输入
                File desFile = new File(str);
                if (!desFile.exists()) {
                    File fileParentDir = desFile.getParentFile();
                    if (!fileParentDir.exists()) {
                        fileParentDir.mkdirs();
                    }
                    desFile.createNewFile();
                }
                OutputStream out = new FileOutputStream(desFile);
                byte buffer[] = new byte[BUFF_SIZE];
                int realLength;
                while ((realLength = in.read(buffer)) > 0) {
                    out.write(buffer, 0, realLength);
                }
                in.close();
                out.close();
                bUnZip=true;
                break;
            }
        }
        
        return bUnZip;
    }

    /**
     * 获得压缩文件内文件列表
     *
     * @param zipFile 压缩文件
     * @return 压缩文件内文件名称
     * @throws ZipException 压缩文件格式有误时抛出
     * @throws IOException 当解压缩过程出错时抛出
     */
    public static ArrayList<String> getEntriesNames(File zipFile) throws ZipException, IOException {
        ArrayList<String> entryNames = new ArrayList<String>();
        Enumeration<?> entries = getEntriesEnumeration(zipFile);
        while (entries.hasMoreElements()) {
            ZipEntry entry = ((ZipEntry)entries.nextElement());
            entryNames.add(new String(getEntryName(entry).getBytes("GB2312"), "8859_1"));
        }
        return entryNames;
    }

    /**
     * 获得压缩文件内压缩文件对象以取得其属性
     *
     * @param zipFile 压缩文件
     * @return 返回一个压缩文件列表
     * @throws ZipException 压缩文件格式有误时抛出
     * @throws IOException IO操作有误时抛出
     */
    public static Enumeration<?> getEntriesEnumeration(File zipFile) throws ZipException,
            IOException {
        ZipFile zf = new ZipFile(zipFile);
        return zf.entries();

    }

    /**
     * 取得压缩文件对象的注释
     *
     * @param entry 压缩文件对象
     * @return 压缩文件对象的注释
     * @throws UnsupportedEncodingException
     */
    public static String getEntryComment(ZipEntry entry) throws UnsupportedEncodingException {
        return new String(entry.getComment().getBytes("GB2312"), "8859_1");
    }

    /**
     * 取得压缩文件对象的名称
     *
     * @param entry 压缩文件对象
     * @return 压缩文件对象的名称
     * @throws UnsupportedEncodingException
     */
    public static String getEntryName(ZipEntry entry) throws UnsupportedEncodingException {
        return new String(entry.getName().getBytes("GB2312"), "8859_1");
    }

    /**
     * 压缩文件
     *
     * @param resFile 需要压缩的文件（夹）
     * @param zipout 压缩的目的文件
     * @param rootpath 压缩的文件路径
     * @throws FileNotFoundException 找不到文件时抛出
     * @throws IOException 当压缩过程出错时抛出
     */
    private static void zipFile(File resFile, ZipOutputStream zipout, String rootpath)
            throws FileNotFoundException, IOException {
        rootpath = rootpath + (rootpath.trim().length() == 0 ? "" : File.separator)
                + resFile.getName();
        rootpath = new String(rootpath.getBytes("8859_1"), "GB2312");
        if (resFile.isDirectory()) {
            File[] fileList = resFile.listFiles();
            for (File file : fileList) {
                zipFile(file, zipout, rootpath);
            }
        } else {
            byte buffer[] = new byte[BUFF_SIZE];
            BufferedInputStream in = new BufferedInputStream(new FileInputStream(resFile),
                    BUFF_SIZE);
            zipout.putNextEntry(new ZipEntry(rootpath));
            int realLength;
            while ((realLength = in.read(buffer)) != -1) {
                zipout.write(buffer, 0, realLength);
            }
            in.close();
            zipout.flush();
            zipout.closeEntry();
        }
    }
    
    public static List<File> GetFileList(String zipFileString,
    		boolean bContainFolder, boolean bContainFile) throws Exception {

    		List<File> fileList = new ArrayList<File>();
    		ZipInputStream inZip = new ZipInputStream(new FileInputStream(
    		zipFileString));
    		ZipEntry zipEntry;
    		String szName = "";

    		while ((zipEntry = inZip.getNextEntry()) != null) 
    		{
    			szName = zipEntry.getName();
	    		if (zipEntry.isDirectory()) 
	    		{
	
		    		// get the folder name of the widget
		    		szName = szName.substring(0, szName.length() - 1);
		    		File folder = new File(szName);
		    		if (bContainFolder) 
		    		{
		    			fileList.add(folder);
		    		}
	
	    		} 
	    		else 
	    		{
		    		File file = new File(szName);
		    		if (bContainFile) 
		    		{
		    			fileList.add(file);
		    		}
	    		}
    		}// end of while
    		inZip.close();

    		return fileList;
    		}
    
    public static String readTextFromZipFile(String file) throws Exception {
    	ZipFile zipFile = null;
    	ZipInputStream zin = null;
    	InputStream in = null;
    	StringBuffer sbf = null;
    	try {
    		zipFile = new ZipFile(file);
    		in = new BufferedInputStream(new FileInputStream(file));
    		zin = new ZipInputStream(in);
    		ZipEntry ze;
    		sbf = new StringBuffer();
    		while ((ze = zin.getNextEntry()) != null) {
    			if (ze.isDirectory()) {
    			} else {
    				StringBuffer sBuffer = new StringBuffer();
    				// 这里的判断不能用ze.getSize() > 0， 当文件的大小很小时，会返回-1
    				if (ze.getSize() != 0) {
    					if(ze.getName().endsWith(".txt"))
    					{
	    					BufferedReader br = new BufferedReader(
	    							new InputStreamReader(
	    									zipFile.getInputStream(ze)));
	    					String line;
	    					while ((line = br.readLine()) != null) {
	    						sBuffer.append(line + "\r\n");
	    					}
	    					sbf.append(sBuffer);
	    					br.close();
	    					break;
    					}
    				}
    			}
    		}

    	} catch (Exception e) {
    		e.printStackTrace();
    	} finally {
    		try {
    			if(in != null) {
    				in.close();
    			}
    			
    			if(zipFile != null) {
    				zipFile.close();
    			} 
    		} catch (IOException e) {
    			e.printStackTrace();
    		}
    	}
    	return sbf.toString();
    }
    
    
    public static Bitmap getBitmapFromZipFile(String file,BitmapFactory.Options options) throws Exception {
    	ZipFile zipFile = null;
    	ZipInputStream zin = null;
    	InputStream in = null;
    	StringBuffer sbf = null;
    	Bitmap bmp=null;
    	try {
    		zipFile = new ZipFile(file);
    		in = new BufferedInputStream(new FileInputStream(file));
    		zin = new ZipInputStream(in);
    		ZipEntry ze;
    		sbf = new StringBuffer();
    		while ((ze = zin.getNextEntry()) != null) {
    			if (ze.isDirectory()) {
    			} else {
    				StringBuffer sBuffer = new StringBuffer();
    				// 这里的判断不能用ze.getSize() > 0， 当文件的大小很小时，会返回-1
    				if (ze.getSize() != 0) {
    					if(ze.getName().toLowerCase().endsWith(".jpg")
    							||ze.getName().toLowerCase().endsWith(".png")
    							||ze.getName().toLowerCase().endsWith(".bmp")
    							||ze.getName().toLowerCase().endsWith(".gif"))
    					{    	
    						if(options==null)
    							bmp=Bitmap.createBitmap(BitmapFactory.decodeStream(zipFile.getInputStream(ze)));
    						else
    							bmp=Bitmap.createBitmap(BitmapFactory.decodeStream(zipFile.getInputStream(ze),null,options));
	    					break;
    					}
    				}
    			}
    		}

    	} catch (Exception e) {
    		e.printStackTrace();
    	} finally {
    		try {
    			if(in != null) {
    				in.close();
    			}
    			
    			if(zipFile != null) {
    				zipFile.close();
    			} 
    		} catch (IOException e) {
    			e.printStackTrace();
    		}
    	}
    	return bmp;
    }
    
    
    public static List<ZipPictureBean> getPicturesFromZip(String file,BitmapFactory.Options options) throws Exception {
    	ZipFile zipFile = null;
    	ZipInputStream zin = null;
    	InputStream in = null;
    	StringBuffer sbf = null;
    	Bitmap bmp=null;
    	
    	List<ZipPictureBean> listPicInfo=new ArrayList();
    	
    	try {
    		zipFile = new ZipFile(file);
    		in = new BufferedInputStream(new FileInputStream(file));
    		zin = new ZipInputStream(in);
    		ZipEntry ze;
    		sbf = new StringBuffer();
    		while ((ze = zin.getNextEntry()) != null) {
    			if (ze.isDirectory()) {
    			} else {
    				StringBuffer sBuffer = new StringBuffer();
    				// 这里的判断不能用ze.getSize() > 0， 当文件的大小很小时，会返回-1
    				if (ze.getSize() != 0) {
    					if(ze.getName().toLowerCase().endsWith(".jpg")
    							||ze.getName().toLowerCase().endsWith(".png")
    							||ze.getName().toLowerCase().endsWith(".bmp")
    							||ze.getName().toLowerCase().endsWith(".gif"))
    					{    	
    						if(options==null)
    							bmp=Bitmap.createBitmap(BitmapFactory.decodeStream(zipFile.getInputStream(ze)));
    						else
    							bmp=Bitmap.createBitmap(BitmapFactory.decodeStream(zipFile.getInputStream(ze),null,options));
    						
    						ZipPictureBean bean=new ZipPictureBean(file);
    						bean.setBitmap(bmp);
    						bean.setZipName(ze.getName());
    						listPicInfo.add(bean);
	    					
    					}
    				}
    			}
    		}

    	} catch (Exception e) {
    		e.printStackTrace();
    	} finally {
    		try {
    			if(in != null) {
    				in.close();
    			}
    			
    			if(zipFile != null) {
    				zipFile.close();
    			} 
    		} catch (IOException e) {
    			e.printStackTrace();
    		}
    	}
    	return listPicInfo;
    }
    
    
    public static Bitmap getPictureItemFromZip(String file,BitmapFactory.Options options,String name) throws Exception {
    	ZipFile zipFile = null;
    	ZipInputStream zin = null;
    	InputStream in = null;
    	StringBuffer sbf = null;
    	Bitmap bmp=null;
    	
    		
    	try {
    		zipFile = new ZipFile(file);
    		in = new BufferedInputStream(new FileInputStream(file));
    		zin = new ZipInputStream(in);
    		ZipEntry ze;
    		sbf = new StringBuffer();
    		while ((ze = zin.getNextEntry()) != null) {
    			if (ze.isDirectory()) {
    			} else 
    			{
    				StringBuffer sBuffer = new StringBuffer();
    				// 这里的判断不能用ze.getSize() > 0， 当文件的大小很小时，会返回-1
    				if (ze.getSize() != 0) {
    					if(ze.getName().equalsIgnoreCase(name))
    					{
	    					if(ze.getName().toLowerCase().endsWith(".jpg")
	    							||ze.getName().toLowerCase().endsWith(".png")
	    							||ze.getName().toLowerCase().endsWith(".bmp")
	    							||ze.getName().toLowerCase().endsWith(".gif"))
	    					{    	
	    						if(options==null)
	    							bmp=Bitmap.createBitmap(BitmapFactory.decodeStream(zipFile.getInputStream(ze)));
	    						else
	    							bmp=Bitmap.createBitmap(BitmapFactory.decodeStream(zipFile.getInputStream(ze),null,options));
	    						
	    						
	    					    break;
		    					
	    					}
    					}
	    			}
    			}
    		}

    	} catch (Exception e) {
    		e.printStackTrace();
    	} finally {
    		try {
    			if(in != null) {
    				in.close();
    			}
    			
    			if(zipFile != null) {
    				zipFile.close();
    			} 
    		} catch (IOException e) {
    			e.printStackTrace();
    		}
    	}
    	return bmp;
    }
    
    
    
    //获得一个目录里所有扩展名为ext的列表。
    public static Collection<File> getFileList(String folder,String ext)  {
    	
    	Collection<File> fileList = new ArrayList();
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
    				fileList.add(files[i]);
    			}
    		}
    		return fileList;
    	}
    	
    }
    
   
    
}


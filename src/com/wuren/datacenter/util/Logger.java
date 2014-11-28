package com.wuren.datacenter.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import android.content.Context;

public class Logger {

	  private static String LOG_TAG = "Logger";
	  private static Logger loggerInstance = null;
	  private static final String SPACE = " ";
	  private static final String NEWLINE = "\r\n";
	  private static int referenceCount = 0;
	  public static final int ERROR = 4;
	  public static final int WARNING = 3;
	  public static final int INFO = 2;
	  public static final int DEBUG = 1;
	  public static final int VERBOSE = 0;
	  private static int MAX_LOG_SIZE = 32;
	  private static int MIN_LOG_SIZE = 4;
	  private static int MAX_LOG_RECORD_SIZE = 4096;
	  private static boolean TIME_STAMP = true;
	  private int logLevel = 0;
	  private int maxLogSize = MIN_LOG_SIZE;
	  private boolean ftimeStamp = TIME_STAMP;
	  private static String logFileDirectory = null;
	  private static String logFile = "app_log.txt";
	  private static String logFileBackup = "app_log.bak";
	  private static File logFileConnection = null;
	  private static FileInputStream logFileIS = null;
	  private static FileOutputStream logFileOS = null;
	  private static Context ctxt = null;
	  
	  
	  public static synchronized Logger get(Context paramContext)
	  {
		    if (paramContext == null)
		      return null;
		    if (loggerInstance == null)
		    {
		      loggerInstance = new Logger();
		      ctxt = paramContext;
		      File localFile = ctxt.getFilesDir();
		      logFileDirectory = localFile.getAbsolutePath();
		      android.util.Log.v(LOG_TAG, "Log directory path : " + logFileDirectory);
		      android.util.Log.v(LOG_TAG, "Log file path : " + logFileDirectory + "/" + logFile);
		      android.util.Log.v(LOG_TAG, "Backup Log file path : " + logFileDirectory + "/" + logFileBackup);
		      localFile = null;
		    }
		    else if (paramContext != ctxt)
		    {
		      Log.w(LOG_TAG, "App context does not match.");
		      return null;
		    }
		    if (referenceCount == 0)
		      initLogFile();
		    referenceCount += 1;
		    return loggerInstance;
	  }
	  
	  

	  public synchronized void close()
	  {
	    referenceCount -= 1;
	    if (referenceCount < 0)
	    {
	      referenceCount = 0;
	      return;
	    }
	    if (referenceCount != 0)
	      return;
	    mergeLogFiles();
	    closeLogFile();
	  }

	  public void log(int paramInt, String paramString)
	  {
	    if (referenceCount == 0)
	      return;
	    if (logFileConnection == null)
	    {
	      initLogFile();
	      if (logFileConnection == null)
	        return;
	    }
	    paramInt = validateLogLevel(paramInt);
	    
	    if (paramInt < this.logLevel)
	      return;
	    
	    
	    
	    
	    switch (paramInt)
	    {
	    case 4:
	      writeLogMessage("[ERROR]", paramString);
	      break;
	    case 3:
	      writeLogMessage("[WARNING]", paramString);
	      break;
	    case 2:
	      writeLogMessage("[INFO]", paramString);
	      break;
	    case 1:
	      writeLogMessage("[DEBUG]", paramString);
	      break;
	    case 0:
	      writeLogMessage("[VERBOSE]", paramString);
	    }
	  }

	  public void debug(String paramString1, String paramString2)
	  {
	    log(1, paramString2);
	  }

	  public void error(String paramString1, String paramString2)
	  {
	    log(4, paramString2);
	  }

	  public void warning(String paramString1, String paramString2)
	  {
	    log(3, paramString2);
	  }

	  public void info(String paramString1, String paramString2)
	  {
	    log(2, paramString2);
	  }

	  public void verbose(String paramString1, String paramString2)
	  {
	    log(0, paramString1 + " : " + paramString2);
	  }

	  private synchronized void writeLogMessage(String paramString1, String paramString2)
	  {
	    try
	    {
	      if (logFileOS != null)
	      {
	        StringBuffer localStringBuffer = new StringBuffer();
	        if (this.ftimeStamp)
	          localStringBuffer.append(getTimeInUTC()).append(" ");
	        localStringBuffer.append(paramString1).append(" ");
	        if (paramString2.length() <= MAX_LOG_RECORD_SIZE)
	          localStringBuffer.append(paramString2).append("\r\n");
	        else
	          localStringBuffer.append(paramString2.substring(0, MAX_LOG_RECORD_SIZE)).append("\r\n");
	        if (logFileConnection.length() > this.maxLogSize)
	          fixReducedFileSize();
	        if (logFileConnection.length() + localStringBuffer.length() > this.maxLogSize)
	          try
	          {
	            File localFile = new File(logFileBackup);
	            if (localFile.exists())
	              localFile.delete();
	            logFileConnection.renameTo(localFile);
	            localFile = null;
	            logFileOS.close();
	            logFileOS = null;
	            logFileConnection = null;
	            initLogFile();
	          }
	          catch (Exception localException2)
	          {
	          }
	        logFileOS.write(localStringBuffer.toString().getBytes());
	        logFileOS.flush();
	      }
	    }
	    catch (Exception localException1)
	    {
	      try
	      {
	        if (logFileConnection != null)
	          logFileConnection = null;
	      }
	      catch (Exception localException3)
	      {
	      }
	      finally
	      {
	        initLogFile();
	      }
	    }
	  }

	  private int validateMaxSize(int paramInt)
	  {
	    if (MIN_LOG_SIZE > paramInt)
	      return MIN_LOG_SIZE;
	    if (MAX_LOG_SIZE < paramInt)
	      return MAX_LOG_SIZE;
	    return paramInt;
	  }

	  private int validateLogLevel(int paramInt)
	  {
	    if (paramInt < 0)
	      return 0;
	    if (paramInt > 4)
	      return 4;
	    return paramInt;
	  }

	  private void mergeLogFiles()
	  {
	    File localFile = null;
	    FileInputStream localFileInputStream = null;
	    try
	    {
	      localFile = new File(logFileBackup);
	      if (localFile.exists())
	      {
	        localFileInputStream = new FileInputStream(localFile);
	        int i = (int)localFile.length();
	        if (0 >= i)
	        {
	          localFile.delete();
	          return;
	        }
	        byte[] arrayOfByte1 = new byte[i];
	        if (-1 == localFileInputStream.read(arrayOfByte1, 0, i))
	          throw new IOException();
	        localFile.delete();
	        logFileIS = new FileInputStream(logFileConnection);
	        int j = (int)logFileConnection.length();
	        byte[] arrayOfByte2 = new byte[j];
	        if (-1 == logFileIS.read(arrayOfByte2, 0, j))
	          throw new IOException();
	        logFileOS.close();
	        logFileConnection.delete();
	        logFileConnection.createNewFile();
	        logFileOS = new FileOutputStream(logFileConnection, true);
	        int k = this.maxLogSize - j;
	        int l = 0;
	        if (k <= i)
	        {
	          l = i - k;
	          int i1 = getNewLinePos(arrayOfByte1, l);
	          if (i1 != -1)
	          {
	            l = i1 + "\r\n".length();
	            logFileOS.write(arrayOfByte1, l, i - l);
	            logFileOS.flush();
	          }
	        }
	        logFileOS.write(arrayOfByte2, 0, j);
	        logFileOS.flush();
	      }
	    }
	    catch (Exception localException3)
	    {
	    }
	    finally
	    {
	      try
	      {
	        if (logFileIS != null)
	          logFileIS.close();
	        if (localFile != null)
	          localFile = null;
	        if (localFileInputStream != null)
	          localFileInputStream.close();
	      }
	      catch (Exception localException5)
	      {
	      }
	    }
	  }

	  private void fixReducedFileSize()
	  {
	    File localFile = null;
	    FileInputStream localFileInputStream = null;
	    FileOutputStream localFileOutputStream = null;
	    try
	    {
	      localFile = new File(logFileBackup);
	      if (localFile.exists())
	        localFile.delete();
	      logFileConnection.renameTo(localFile);
	      logFileOS.close();
	      logFileConnection = null;
	      initLogFile();
	      localFileInputStream = new FileInputStream(localFile);
	      int i = (int)localFile.length();
	      byte[] arrayOfByte = new byte[i];
	      localFileInputStream.read(arrayOfByte, 0, i);
	      logFileConnection.delete();
	      logFileConnection.createNewFile();
	      int j = i - this.maxLogSize;
	      int k = getNewLinePos(arrayOfByte, j);
	      if (k != -1)
	      {
	        k += "\r\n".length();
	        localFileOutputStream = new FileOutputStream(localFile);
	        localFileOutputStream.write(arrayOfByte, k, i - k);
	        localFileOutputStream.flush();
	      }
	    }
	    catch (Exception localException3)
	    {
	    }
	    finally
	    {
	      try
	      {
	        if (localFileInputStream != null)
	          localFileInputStream.close();
	        if (localFileOutputStream != null)
	          localFileOutputStream.close();
	        if (localFile != null)
	          localFile = null;
	      }
	      catch (Exception localException4)
	      {
	      }
	    }
	  }

	  private int getNewLinePos(byte[] paramArrayOfByte, int paramInt)
	  {
	    int i = 0;
	    while (true)
	    {
	      if (('\r' == (char)(0xFF & paramArrayOfByte[paramInt])) && ('\n' == (char)(0xFF & paramArrayOfByte[(paramInt + 1)])))
	      {
	        i = 1;
	        break;
	      }
	      ++paramInt;
	    }
	    if (i != 0)
	      return paramInt;
	    return -1;
	  }

	  private static void initLogFile()
	  {
	    try
	    {
	      logFileConnection = new File(logFile);
	      logFileOS = ctxt.openFileOutput(logFile, 32769);
	    }
	    catch (FileNotFoundException localFileNotFoundException)
	    {
	      Log.v(LOG_TAG, "initLogFile exception : " + localFileNotFoundException.toString());
	    }
	  }

	  private void closeLogFile()
	  {
	    try
	    {
	      if (logFileOS != null)
	      {
	        logFileOS.close();
	        logFileOS = null;
	      }
	      if (logFileIS != null)
	      {
	        logFileIS.close();
	        logFileIS = null;
	      }
	      if (logFileConnection != null)
	        logFileConnection = null;
	    }
	    catch (Exception localException)
	    {
	      Log.v(LOG_TAG, "closeLogFile exception : " + localException.toString());
	    }
	  }

	  private String getTimeInUTC()
	  {
	    StringBuffer localStringBuffer = new StringBuffer();
	    Calendar localCalendar = Calendar.getInstance();
	    localCalendar.setTime(new Date());
	    localStringBuffer.append(localCalendar.get(1)).append("-");
	    localStringBuffer.append(printTwoDigits(localCalendar.get(2) + 1)).append("-");
	    localStringBuffer.append(printTwoDigits(localCalendar.get(5))).append(" ");
	    localStringBuffer.append(printTwoDigits(localCalendar.get(11))).append(":");
	    localStringBuffer.append(printTwoDigits(localCalendar.get(12))).append(":");
	    localStringBuffer.append(printTwoDigits(localCalendar.get(13)));
	    return localStringBuffer.toString();
	  }

	  private String printTwoDigits(int paramInt)
	  {
	    if (paramInt > 9)
	      return String.valueOf(paramInt);
	    return "0" + paramInt;
	  }

	  public synchronized void setLevel(int paramInt)
	  {
	    paramInt = validateLogLevel(paramInt);
	    this.logLevel = paramInt;
	  }

	  public synchronized void setMaxSize(int paramInt)
	  {
	    paramInt = validateMaxSize(paramInt);
	    this.maxLogSize = (paramInt * 1024);
	  }

	  public synchronized void enableTimeStamp(boolean paramBoolean)
	  {
	    this.ftimeStamp = paramBoolean;
	  }

	  public int getLevel()
	  {
	    return this.logLevel;
	  }

	  public int getMaxSize()
	  {
	    return this.maxLogSize;
	  }

	  public boolean isTimeStamp()
	  {
	    return this.ftimeStamp;
	  }

	  public synchronized void setConfigurations(int level, int maxSize, boolean isTimeStamp)
	  {
		  maxSize = validateMaxSize(maxSize);
		  this.logLevel = validateLogLevel(level);
		  this.maxLogSize = (maxSize * 1024);
		  this.ftimeStamp = isTimeStamp;
	  }
}

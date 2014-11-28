package com.wuren.datacenter.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


import android.content.Context;

public class Log {
	
 private static Context mAppContext = null;
  private static Logger mLogger = null;
  private static ExecutorService mExecutorService = null;
  
  public static final int LOG_PANIC = 0;
  public static final int LOG_CRITICAL = 1;
  public static final int LOG_WARNING = 2;
  public static final int LOG_INFO = 3;
  public static final int LOG_VERBOSE = 4;
  public static int level = 3;

	
	
	
  public static synchronized void initLog(Context context, int level_set)
  {
    mAppContext = context;
    mExecutorService = Executors.newSingleThreadExecutor();
    level = level_set;
  }
  
  public static boolean isDebugLogRequired()
  {
    return level > 3;    
  }

  public static synchronized void close()
  {
    if (mExecutorService != null)
    {
      try
      {
        mExecutorService.shutdown();
        if (!mExecutorService.awaitTermination(3L, TimeUnit.SECONDS))
        {
          mExecutorService.shutdownNow();
          mExecutorService.awaitTermination(1L, TimeUnit.SECONDS);
        }
      }
      catch (Exception localException)
      {
      }
      mExecutorService = null;
    }
    if (mLogger != null)
    {
      mLogger.close();
      mLogger = null;
    }
    mAppContext = null;
  }
  
  private static synchronized void submit(Runnable paramRunnable)
  {
    if (mExecutorService == null)
      return;
    mExecutorService.submit(paramRunnable);
  }
  
  private static Logger getLogger()
  {
    if ((mExecutorService != null) && (mLogger == null))
      mLogger = Logger.get(mAppContext);
    return mLogger;
  }
  
  public static void e(String tag, String message)
  {
    if ((tag != null) && (message != null))
    	android.util.Log.e(tag, message);
    
    WriteLogService writer=new WriteLogService(Logger.ERROR,tag,message);
    
    submit(writer);
        
  }
  
  
  public static void w(String tag, String message)
  {
	  if ((tag != null) && (message != null))
      android.util.Log.w(tag, message);
    WriteLogService writer=new WriteLogService(Logger.WARNING,tag,message);    
    submit(writer);
  }

  public static void i(String tag, String message)
  {
    if ((tag != null) && (message != null))
      android.util.Log.i(tag, message);
    
    
    WriteLogService writer=new WriteLogService(Logger.INFO,tag,message);    
    submit(writer);
    
  }
  
  
  

  public static void d(String tag, String message)
  {
    if (!isDebugLogRequired())
      return;
    
    if ((tag != null) && (message != null))
      android.util.Log.d(tag, message);
    
    WriteLogService writer=new WriteLogService(Logger.DEBUG,tag,message);    
    submit(writer);
  }

  public static void v(String tag, String message)
  {
    if (!isDebugLogRequired())
      return;
    
    
    if ((tag != null) && (message != null))
      android.util.Log.v(tag, message);
    
    
    WriteLogService writer=new WriteLogService(Logger.VERBOSE,tag,message);    
    submit(writer);
    
    
    
   
  }
  
  
  private static class WriteLogService  implements Runnable
  {
  	  
  	  private String mTag;
  	  private String mMessage;
  	  private int mLevel;
  	  public WriteLogService(int level,String tag,String message)
  	  {
  		  this.mTag=tag;
  		  this.mMessage=message;
  	  }
  	@Override
  	public void run() {
  		// TODO Auto-generated method stub
  		
  		Logger localLogger = Log.getLogger();
        if (localLogger == null)
          return;
        switch(this.mLevel)
        {
        case Logger.INFO:
        	localLogger.info(mTag, mMessage);	
        	break;
        case Logger.DEBUG:
        	localLogger.debug(mTag, mMessage);
        	break;
        case Logger.VERBOSE:
        	localLogger.verbose(mTag, mMessage);
        	break;
        case Logger.WARNING:
        	localLogger.warning(mTag, mMessage);
        	break;
        case Logger.ERROR:
        	localLogger.error(mTag, mMessage);
        	break;
        default:
        	break;
        		
        	
        }              
  		
  	}	  
  	  
  }
  
  
  
 

}




	



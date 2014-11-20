package com.wuren.datacenter.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DbHelper {

	private static Context getContent()
	{
		return GlobalContext.getInstance();
	}
	
	public static SQLiteDatabase getDB(String dbName)
	{
		return getContent().openOrCreateDatabase(dbName, Context.MODE_PRIVATE, null);
	}
	
	public static void dropDatabase(String dbName)
	{
		getContent().deleteDatabase(dbName);
	}
	
	public static boolean hasTable(SQLiteDatabase db, String tableName)
	{
		String sql = "SELECT COUNT(*) AS C From SQLite_Master Where Type ='table' AND Name ='" + tableName + "';";
        Cursor cursor = db.rawQuery(sql, null);
        if(cursor != null && cursor.moveToNext())
        {
        	return cursor.getInt(0) > 0;
        }
		return false;
	}
	
	public static boolean dropTable(SQLiteDatabase db, String tableName)
	{
		if (db != null && db.isOpen() && !db.isReadOnly())
		{
			if (hasTable(db, tableName))
			{
				String sqlDropCmd = "DROP TABLE " + tableName;
				try
				{
					db.execSQL(sqlDropCmd);
					return true;
				}
				catch (Exception exp)
				{
					;
				}
			}
		}
		return false;
	}
	
	public static boolean createTable(SQLiteDatabase db, String tableName, String...columns)
	{
		if (db != null && db.isOpen() && !db.isReadOnly() && columns.length > 0)
		{
			if (!hasTable(db, tableName))
			{
				StringBuilder sqlCreateCmd = new StringBuilder();
				sqlCreateCmd.append("CREATE TABLE ");
				sqlCreateCmd.append(tableName);
				sqlCreateCmd.append("(");
				
				for (int i = 0; i < columns.length; i++)
				{
					if (i > 0)
					{
						sqlCreateCmd.append(",");
					}
					sqlCreateCmd.append(columns[i]);
				}
				
				sqlCreateCmd.append(");");
				
				try
				{
					db.execSQL(sqlCreateCmd.toString());
					return true;
				}
				catch (Exception exp)
				{
					;
				}
			}
		}
		return false;
	}

	public static boolean insert(SQLiteDatabase db, String tableName, ContentValues values, boolean replace)
	{
		if (db != null && db.isOpen() && !db.isReadOnly())
		{
			if (values != null && values.size() > 0)
			{
				try
				{
					if (!replace)
					{
						return db.insert(tableName, null, values) != -1;
					}
					else
					{
						return db.insertWithOnConflict(tableName, null, values, SQLiteDatabase.CONFLICT_REPLACE) != -1;
					}
				}
				catch (Exception exp)
				{
					;
				}
			}
		}
		return false;
	}
	
	public static boolean delete(SQLiteDatabase db, String tableName, String whereText)
	{
		if (db != null && db.isOpen() && !db.isReadOnly())
		{
			String delSQL = "DELETE FROM " + tableName + " WHERE " + whereText;
			try
			{
				db.execSQL(delSQL);
				return true;
			}
			catch (Exception exp)
			{
				return false;
			}
		}		
		return false;
	}
	
	public static boolean update(SQLiteDatabase db, String tableName, ContentValues values, String whereText)
	{
		if (db != null && db.isOpen() && !db.isReadOnly())
		{
			if (values != null && values.size() > 0)
			{
				try
				{
					db.update(tableName, values, whereText, null);
					return true;
				}
				catch (Exception exp)
				{
					;
				}
			}
		}
		return false;
	}
	
	public static Cursor selectOne(SQLiteDatabase db, String tableName, String[] columns, String whereText)
	{
		if (db != null && db.isOpen() && !db.isReadOnly())
		{
			try
			{
				return db.query(tableName, columns, whereText, null, null, null, null, "0, 1");
			}
			catch (Exception exp)
			{
				;
			}
		}
		return null;
	}
	
	public static Cursor selectOneByOrder(SQLiteDatabase db, String tableName, String[] columns, String whereText, String orderBy)
	{
		if (db != null && db.isOpen() && !db.isReadOnly())
		{
			try
			{
				return db.query(tableName, columns, whereText, null, null, null, orderBy, "0, 1");
			}
			catch (Exception exp)
			{
				;
			}
		}
		return null;
	}
	
	public static Cursor selectAll(SQLiteDatabase db, String tableName, String[] columns, String whereText)
	{
		if (db != null && db.isOpen() && !db.isReadOnly())
		{
			try
			{
				return db.query(tableName, columns, whereText, null, null, null, null);
			}
			catch (Exception exp)
			{
				;
			}
		}
		return null;
	}

	public static Cursor selectAllByOrder(SQLiteDatabase db, String tableName, String[] columns, String whereText, String orderBy)
	{
		if (db != null && db.isOpen() && !db.isReadOnly())
		{
			try
			{
				return db.query(tableName, columns, whereText, null, null, null, orderBy);
			}
			catch (Exception exp)
			{
				;
			}
		}
		return null;
	}
	
	public static Cursor selectPage(SQLiteDatabase db, String tableName, String[] columns, String whereText, int offset, int pageSize)
	{
		if (db != null && db.isOpen() && !db.isReadOnly())
		{
			try
			{
				return db.query(tableName, columns, whereText, null, null, null, null, offset + "," + pageSize);
			}
			catch (Exception exp)
			{
				;
			}
		}
		return null;
	}
	
	public static Cursor selectPageByOrder(SQLiteDatabase db, String tableName, String[] columns, String whereText, String orderBy, int offset, int pageSize)
	{
		if (db != null && db.isOpen() && !db.isReadOnly())
		{
			try
			{
				return db.query(tableName, columns, whereText, null, null, null, orderBy, offset + "," + pageSize);
			}
			catch (Exception exp)
			{
				;
			}
		}
		return null;
	}
	
	public static boolean hasRecord(SQLiteDatabase db, String tableName, String whereText)
	{
		Cursor c = selectOne(db, tableName, null, whereText);
		try
		{
			if (c != null && c.getCount() > 0)
			{
				return true;
			}
			return false;
		}
		catch (Exception exp)
		{
			return false;
		}
		finally
		{
			if (c != null)
			{
				c.close();
			}
		}
	}

	public static void clearTable(SQLiteDatabase db, String tableName, boolean resetSequence)
	{
		String delSQL = "DELETE FROM " + tableName;
		db.execSQL(delSQL);
		
		if (resetSequence)
		{
			String resetSQL = "UPDATE Sqlite_Sequence SET Seq=0 WHERE Name='" + tableName + "'";
			db.execSQL(resetSQL);
		}
	}
	
	public static String getTopString(SQLiteDatabase db, String tableName, String[] columns, String whereText, String orderBy)
	{
		if (orderBy != null && orderBy.length() > 0)
		{
			Cursor c = selectOneByOrder(db, tableName, columns, whereText, orderBy);
			try
			{
				if (c != null && c.moveToFirst())
				{
					return c.getString(0);
				}
			}
			catch (Exception exp)
			{
				;
			}
			finally
			{
				if (c != null)
				{
					c.close();
				}
			}
		}
		else
		{
			Cursor c = selectOne(db, tableName, columns, whereText);
			try
			{
				if (c != null && c.moveToFirst())
				{
					return c.getString(0);
				}
			}
			catch (Exception exp)
			{
				;
			}
			finally
			{
				if (c != null)
				{
					c.close();
				}
			}
		}
		return null;
	}
	
	public static int getTopInt(SQLiteDatabase db, String tableName, String[] columns, String whereText, String orderBy)
	{
		if (orderBy != null && orderBy.length() > 0)
		{
			Cursor c = selectOneByOrder(db, tableName, columns, whereText, orderBy);
			try
			{
				if (c != null && c.moveToFirst())
				{
					return c.getInt(0);
				}
			}
			catch (Exception exp)
			{
				;
			}
			finally
			{
				if (c != null)
				{
					c.close();
				}
			}
		}
		else
		{
			Cursor c = selectOne(db, tableName, columns, whereText);
			try
			{
				if (c != null && c.moveToFirst())
				{
					return c.getInt(0);
				}
			}
			catch (Exception exp)
			{
				;
			}
			finally
			{
				if (c != null)
				{
					c.close();
				}
			}
		}
		return Integer.MAX_VALUE;
	}
	
	public static long getTopLong(SQLiteDatabase db, String tableName, String[] columns, String whereText, String orderBy)
	{
		if (orderBy != null && orderBy.length() > 0)
		{
			Cursor c = selectOneByOrder(db, tableName, columns, whereText, orderBy);
			try
			{
				if (c != null && c.moveToFirst())
				{
					return c.getLong(0);
				}
			}
			catch (Exception exp)
			{
				;
			}
			finally
			{
				if (c != null)
				{
					c.close();
				}
			}
		}
		else
		{
			Cursor c = selectOne(db, tableName, columns, whereText);
			try
			{
				if (c != null && c.moveToFirst())
				{
					return c.getLong(0);
				}
			}
			catch (Exception exp)
			{
				;
			}
			finally
			{
				if (c != null)
				{
					c.close();
				}
			}
		}
		return Long.MAX_VALUE;
	}
	
	public static double getTopDouble(SQLiteDatabase db, String tableName, String[] columns, String whereText, String orderBy)
	{
		if (orderBy != null && orderBy.length() > 0)
		{
			Cursor c = selectOneByOrder(db, tableName, columns, whereText, orderBy);
			try
			{
				if (c != null && c.moveToFirst())
				{
					return c.getDouble(0);
				}
			}
			catch (Exception exp)
			{
				;
			}
			finally
			{
				if (c != null)
				{
					c.close();
				}
			}
		}
		else
		{
			Cursor c = selectOne(db, tableName, columns, whereText);
			try
			{
				if (c != null && c.moveToFirst())
				{
					return c.getDouble(0);
				}
			}
			catch (Exception exp)
			{
				;
			}
			finally
			{
				if (c != null)
				{
					c.close();
				}
			}
		}
		return Double.MAX_VALUE;
	}
	
	public static float getTopFloat(SQLiteDatabase db, String tableName, String[] columns, String whereText, String orderBy)
	{
		if (orderBy != null && orderBy.length() > 0)
		{
			Cursor c = selectOneByOrder(db, tableName, columns, whereText, orderBy);
			try
			{
				if (c != null && c.moveToFirst())
				{
					return c.getFloat(0);
				}
			}
			catch (Exception exp)
			{
				;
			}
			finally
			{
				if (c != null)
				{
					c.close();
				}
			}
		}
		else
		{
			Cursor c = selectOne(db, tableName, columns, whereText);
			try
			{
				if (c != null && c.moveToFirst())
				{
					return c.getFloat(0);
				}
			}
			catch (Exception exp)
			{
				;
			}
			finally
			{
				if (c != null)
				{
					c.close();
				}
			}
		}
		return Float.MAX_VALUE;
	}
	
}

package com.wuren.datacenter.widgets;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.View;

public abstract class MultiColumnAdapter extends Object
{
	public interface OnDataChangedListener
	{
		public void onAdd(int index, int count);
		public void onInsert(int count);
		public void onReset(int count);
		public void onRemove(int index);
		public void onClear();
	}
	
	private List<Object> m_Datas = new ArrayList<Object>();
	
	private OnDataChangedListener m_Listener = null;
	
	public void setOnDataChangedListener(OnDataChangedListener listener)
	{
		m_Listener = listener;
	}
	
	public  int getCount()
	{
		return m_Datas.size();
	}
	
	public Object getItem(int index)
	{
		if (index >= 0 && index < m_Datas.size())
		{
			return m_Datas.get(index);
		}
		throw new IndexOutOfBoundsException("index");
	}
	
	public void add(Object...items)
	{
		int startIndex = m_Datas.size();
		for (int i = 0; i < items.length; i++)
		{
			m_Datas.add(items[i]);
		}
		
		if (m_Listener != null)
		{
			m_Listener.onAdd(startIndex, items.length);
		}
	}
	
	public void insert(Object...items)
	{
		for (int i = 0; i < items.length; i++)
		{
			m_Datas.add(0, items[i]);
		}
		
		if (m_Listener != null)
		{
			m_Listener.onInsert(items.length);
		}
	}
	
	public boolean remove(int index)
	{
		if (index >= 0 && index < m_Datas.size())
		{
			m_Datas.remove(index);

			if (m_Listener != null)
			{
				m_Listener.onRemove(index);
			}
			
			return true;
		}
		return false;
	}
	
	public void reset(Object...items)
	{
		m_Datas.clear();
		for (int i = 0; i < items.length; i++)
		{
			m_Datas.add(items[i]);
		}

		if (m_Listener != null)
		{
			m_Listener.onReset(items.length);
		}
	}
	
	public void adjustCount(int count)
	{
		if (m_Datas.size() > count)
		{
			m_Datas = m_Datas.subList(m_Datas.size() - count, m_Datas.size());
		}
	}
	
	public void clear()
	{
		m_Datas.clear();

		if (m_Listener != null)
		{
			m_Listener.onClear();
		}
	}

	public abstract View getView(Context context, int index, boolean isInsert);
	
	public abstract View resetView(View view, int index, boolean isInsert);
}
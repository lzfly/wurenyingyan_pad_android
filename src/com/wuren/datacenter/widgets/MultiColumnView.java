package com.wuren.datacenter.widgets;

import java.util.ArrayList;
import java.util.List;

import com.wuren.datacenter.R;


import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ScrollView;

public class MultiColumnView extends LinearLayout {

	public interface OnLoadMoreDoneListener
	{
		public void onLoadMoreDone();
	}
	
	private int m_ColumnNum = 2;
	private int m_MaxItemCount = 60;
	
	private List<LinearLayout> m_Columns = new ArrayList<LinearLayout>();
	private List<View> m_Views = new ArrayList<View>();
	
	private OnLoadMoreDoneListener m_LoadMoreDoneListener = null;
	
	private MultiColumnAdapter m_Adapter;
	
	private int m_ScreenWidth;
	private int m_ImageWidth = 0;
	
	public MultiColumnView(Context context) {
		this(context, null);
	}

	public MultiColumnView(Context context, AttributeSet attrs) {
		super(context, attrs);

		WindowManager wm = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
		m_ScreenWidth = wm.getDefaultDisplay().getWidth();
		
		TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.MultiColumnView);
		try
		{
			m_ColumnNum = typedArray.getInt(R.styleable.MultiColumnView_columns, 2);
			m_MaxItemCount = typedArray.getInt(R.styleable.MultiColumnView_maxItemCount, 60);
		}
		finally
		{
			typedArray.recycle();
		}

		initColumns();
	}
	
	public void setOnLoadMoreDoneListener(OnLoadMoreDoneListener l)
	{
		m_LoadMoreDoneListener = l;
	}
	
	private ScrollView m_ScrollViewContainer = null;
	public void setScrollView(ScrollView sv)
	{
		m_ScrollViewContainer = sv;
	}
	
	private void initColumns()
	{	
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT);
		lp.weight = 1;
		for (int i = 0; i < m_ColumnNum; i++)
		{
			LinearLayout columnView = new LinearLayout(this.getContext());
			columnView.setOrientation(LinearLayout.VERTICAL);
			super.addView(columnView, lp);
			
			m_Columns.add(columnView);
		}
	}
	
	public int getColumnWidth()
	{
		if (m_ImageWidth == 0)
		{
			if (m_Columns.size() > 0)
			{
				m_ImageWidth = m_ScreenWidth / m_ColumnNum;
			}
		}
		return m_ImageWidth;
	}
	
	public void setAdapter(MultiColumnAdapter adapter)
	{
		m_Adapter = adapter;
		
		cleanColumnViews();

		for (int i = 0; i < m_Adapter.getCount(); i++)
		{
			View childView = m_Adapter.getView(this.getContext(), i, false);
			if (childView != null)
			{
				int colIdx = i % m_ColumnNum;
				LinearLayout columnView = m_Columns.get(colIdx);
				columnView.addView(childView);
				
				m_Views.add(childView);
			}
		}
		
		m_Adapter.setOnDataChangedListener(l);
	}
	
	private MultiColumnView thisObj = this;
	
	MultiColumnAdapter.OnDataChangedListener l = new MultiColumnAdapter.OnDataChangedListener() {
		
		@Override
		public void onInsert(int count) {
			final int loadCount = count;
			
			Handler handler = new Handler();
			Runnable runnable = new Runnable()
			{

				@Override
				public void run() {
					boolean needRemove = false;
					int maxHeight = Integer.MAX_VALUE;

					for (int i = 0; i < loadCount; i++)
					{
						View childView = m_Adapter.getView(thisObj.getContext(), i, true);
						if (childView != null)
						{
							int colIdx = i % m_ColumnNum;
							LinearLayout columnView = m_Columns.get(colIdx);
							columnView.addView(childView, 0);
							
							m_Views.add(0, childView);
						}
					}
					
					m_ScrollViewContainer.scrollTo(0, 0);
					
					if (m_LoadMoreDoneListener != null)
					{
						m_LoadMoreDoneListener.onLoadMoreDone();
					}
				}

			};
			handler.post(runnable);
		}
		
		@Override
		public void onAdd(int index, int count) {
			final int loadStart = index;
			final int loadCount = count;
			
			Handler handler = new Handler();
			Runnable runnable = new Runnable()
			{

				@Override
				public void run() {
					boolean needRemove = false;
					int maxHeight = Integer.MAX_VALUE;
					
					if (m_ScrollViewContainer != null && m_Views.size() + loadCount > m_MaxItemCount)
					{
						int removeCount = m_Views.size() + loadCount - m_MaxItemCount;
						int countPerColumn = removeCount / m_ColumnNum;
						int removeMod = removeCount % m_ColumnNum;
						for (int i = 0; i < m_ColumnNum; i++)
						{
							LinearLayout columnView = m_Columns.get(i);
							if (removeMod > i + 1)
							{
								View view = columnView.getChildAt(countPerColumn);
								maxHeight = Math.min(maxHeight, view.getTop() + view.getHeight());
								
								columnView.removeViews(0, countPerColumn + 1);
							}
							else
							{
								View view = columnView.getChildAt(countPerColumn - 1);
								maxHeight = Math.min(maxHeight, view.getTop() + view.getHeight());
								
								columnView.removeViews(0, countPerColumn);
							}
						}
						needRemove = true;
					}
					
					for (int i = loadStart; i < loadStart + loadCount; i++)
					{
						View childView = m_Adapter.getView(thisObj.getContext(), i, false);
						if (childView != null)
						{
							int colIdx = i % m_ColumnNum;
							LinearLayout columnView = m_Columns.get(colIdx);
							columnView.addView(childView);
							
							m_Views.add(childView);
						}
					}

					if (needRemove)
					{
						m_Views = m_Views.subList(m_Views.size() - m_MaxItemCount, m_Views.size());
						m_Adapter.adjustCount(m_MaxItemCount);
						
						if (m_ScrollToEndWhenAdded)
						{
							m_ScrollViewContainer.scrollTo(0, m_ScrollViewContainer.getScrollY() - maxHeight + 60);
						}
					}
					else
					{
						if (m_ScrollToEndWhenAdded)
						{
							Handler h = new Handler();
							h.postDelayed(new Runnable() {
	
								@Override
								public void run() {
									m_ScrollViewContainer.scrollTo(0, m_ScrollViewContainer.getScrollY() + 60);
								}
								
							}, 100);
						}
					}
					
					if (m_LoadMoreDoneListener != null)
					{
						m_LoadMoreDoneListener.onLoadMoreDone();
					}
				}
				
			};
			handler.post(runnable);
		}

		@Override
		public void onRemove(int index) {
			if (index >= 0 && index < m_Views.size())
			{
				View view = m_Views.get(index);
				ViewParent parent = view.getParent();
				if (parent != null)
				{
					((ViewGroup)parent).removeView(view);
					m_Views.remove(index);
				}
			}
		}

		@Override
		public void onReset(int count) {
			int removeCount = m_Views.size() - count;
			if (removeCount > 0)
			{
			 	int currentMod = m_Views.size() % m_ColumnNum;

				int countPerColumn = removeCount / m_ColumnNum;
				int newMod = count % m_ColumnNum;

				for (int i = m_ColumnNum - 1; i >= 0; i--)
				{
					LinearLayout columnView = m_Columns.get(i);
					int childCount = columnView.getChildCount();
					
					if (newMod == currentMod)
					{
						columnView.removeViews(childCount - countPerColumn, countPerColumn);
					}
					else if (newMod > currentMod)
					{
						if (i > newMod - 1)
						{
							columnView.removeViews(childCount - countPerColumn - 1, countPerColumn + 1);
						}
						else
						{
							columnView.removeViews(childCount - countPerColumn, countPerColumn);
						}
					}
					else if (newMod < currentMod)
					{
						if (currentMod > i)
						{
							columnView.removeViews(childCount - countPerColumn - 1, countPerColumn + 1);
						}
						else
						{
							columnView.removeViews(childCount - countPerColumn, countPerColumn);
						}
					}
				}
				
				m_Views = m_Views.subList(0, count);
			}
			
			for (int i = 0; i < count; i++)
			{
				if (i < m_Views.size())
				{
					View childView = m_Views.get(i);
					m_Adapter.resetView(childView, i, false);
				}
				else
				{
					View childView = m_Adapter.getView(thisObj.getContext(), i, false);
					if (childView != null)
					{
						int colIdx = i % m_ColumnNum;
						LinearLayout columnView = m_Columns.get(colIdx);
						columnView.addView(childView);
						
						m_Views.add(childView);
					}
				}
			}
			
			if (m_LoadMoreDoneListener != null)
			{
				m_LoadMoreDoneListener.onLoadMoreDone();
			}
		}

		@Override
		public void onClear() {
			cleanColumnViews();
		}

	};
	
	private boolean m_ScrollToEndWhenAdded = true;
	
	public void enableScrollToEndWhenAdded(boolean enable)
	{
		m_ScrollToEndWhenAdded = enable;
	}
	
	private void cleanColumnViews()
	{
		m_Views.clear();

		for (int i = 0; i < m_ColumnNum; i++)
		{
			LinearLayout columnView = m_Columns.get(i);
			columnView.removeAllViews();
		}
	}
	
	public int getViewCount()
	{
		return m_Views.size();
	}
	
	public View getViewAt(int index)
	{
		if (index >= 0 && index < m_Views.size())
		{
			return m_Views.get(index);
		}
		return null;
	}
	
	public int getViewIndex(View view)
	{
		return m_Views.indexOf(view);
	}

}

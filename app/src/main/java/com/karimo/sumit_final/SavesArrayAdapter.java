package com.karimo.sumit_final;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class SavesArrayAdapter extends BaseAdapter 
{
	String[] saveNames;
	Context ctxt;
	LayoutInflater myLayoutInf;
	
	public SavesArrayAdapter(String[] arr, Context c)
	{
		saveNames = arr;
		ctxt = c;
		myLayoutInf = (LayoutInflater)c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() 
	{
		return saveNames.length;
	}

	@Override
	public Object getItem(int arg0) 
	{
		return saveNames[arg0];
	}

	@Override
	public long getItemId(int arg0) 
	{
		return arg0;
	}

	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) 
	{
		if(arg1 == null)
		{
			arg1 = myLayoutInf.inflate(android.R.layout.simple_list_item_1, arg2, false);
		}
		TextView name = (TextView)arg1.findViewById(android.R.id.text1);
		name.setText(saveNames[arg0]);
		return arg1;
	}
}

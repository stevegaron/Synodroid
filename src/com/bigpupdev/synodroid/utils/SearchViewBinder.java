package com.bigpupdev.synodroid.utils;

import java.util.Date;

import android.database.Cursor;
import android.view.View;
import android.widget.TextView;
import android.widget.SimpleCursorAdapter.ViewBinder;

public class SearchViewBinder implements ViewBinder{

	public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
		try{
			if (columnIndex == cursor.getColumnIndex("ADDED")){
				Long milliseconds = cursor.getLong(5);
				Date d = new Date(milliseconds);
				((TextView)view).setText(d.toLocaleString());
				return true;
			}
		}
		catch (Exception e){}
		return false;
	}
}
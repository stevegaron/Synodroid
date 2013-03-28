package com.bigpupdev.synodroid.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

public class BookmarkDBHelper extends SQLiteOpenHelper {
	private static final String TEXT_TYPE = " TEXT";
	private static final String COMMA_SEP = ",";
	private static final String SQL_CREATE_ENTRIES =
	    "CREATE TABLE " + BookmarkEntry.TABLE_NAME + " (" +
	    		BookmarkEntry._ID + " INTEGER PRIMARY KEY," +
	    		BookmarkEntry.COLUMN_NAME_TITLE + TEXT_TYPE + COMMA_SEP +
	    		BookmarkEntry.COLUMN_NAME_URL + TEXT_TYPE + " )";
	private static final String SQL_DELETE_ENTRIES =
		    "DROP TABLE IF EXISTS " + BookmarkEntry.TABLE_NAME;
	
	public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Bookmark.db";

    public BookmarkDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
	
	
	public static abstract class BookmarkEntry implements BaseColumns {
	    public static final String TABLE_NAME = "bookmark";
	    public static final String COLUMN_NAME_TITLE = "title";
	    public static final String COLUMN_NAME_URL = "url";
	}
}
